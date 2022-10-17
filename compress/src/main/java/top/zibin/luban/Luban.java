package top.zibin.luban;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import top.zibin.luban.io.ArrayPoolProvide;

@SuppressWarnings("unused")
public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";
    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;
    private static final String KEY_SOURCE = "source";
    private String mTargetDir;
    private boolean focusAlpha;
    private boolean isUseIOBufferPool;
    private int mLeastCompressSize;
    private OnRenameListener mRenameListener;
    private OnCompressListener mCompressListener;
    private OnNewCompressListener mNewCompressListener;
    private CompressionPredicate mCompressionPredicate;
    private List<InputStreamProvider> mStreamProviders;

    private Handler mHandler;

    private Luban(Builder builder) {
        this.mTargetDir = builder.mTargetDir;
        this.focusAlpha = builder.focusAlpha;
        this.isUseIOBufferPool = builder.isUseBufferPool;
        this.mRenameListener = builder.mRenameListener;
        this.mStreamProviders = builder.mStreamProviders;
        this.mCompressListener = builder.mCompressListener;
        this.mNewCompressListener = builder.mNewCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        this.mCompressionPredicate = builder.mCompressionPredicate;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context A context.
     */
    private File getImageCacheFile(Context context, String suffix) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);

        return new File(cacheBuilder);
    }

    private File getImageCustomFile(Context context, String filename) {
        if (TextUtils.isEmpty(mTargetDir)) {
            mTargetDir = getImageCacheDir(context).getAbsolutePath();
        }

        String cacheBuilder = mTargetDir + "/" + filename;

        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     *
     * @param context A context.
     * @see #getImageCacheDir(Context, String)
     */
    private File getImageCacheDir(Context context) {
        return getImageCacheDir(context, DEFAULT_DISK_CACHE_DIR);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getImageCacheDir(Context)
     */
    private static File getImageCacheDir(Context context, String cacheName) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }

    /**
     * start asynchronous compress thread
     */
    private void launch(final Context context) {
        if (mStreamProviders == null || mStreamProviders.size() == 0) {
            if (mCompressListener != null) {
                mCompressListener.onError(-1, new NullPointerException("image file cannot be null"));
            }
            if (mNewCompressListener != null) {
                mNewCompressListener.onError("", new NullPointerException("image file cannot be null"));
            }
            return;
        }

        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();

        while (iterator.hasNext()) {
            final InputStreamProvider path = iterator.next();

            AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                        File result = compress(context, path);
                        Message message = mHandler.obtainMessage(MSG_COMPRESS_SUCCESS);
                        message.arg1 = path.getIndex();
                        message.obj = result;
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_SOURCE, path.getPath());
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    } catch (Exception e) {
                        Message message = mHandler.obtainMessage(MSG_COMPRESS_ERROR);
                        message.arg1 = path.getIndex();
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_SOURCE, path.getPath());
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            });

            iterator.remove();
        }
    }

    /**
     * start compress and return the file
     */
    private File get(InputStreamProvider input, Context context) throws IOException {
        try {
            return new Engine(input, getImageCacheFile(context, Checker.SINGLE.extSuffix(input)), focusAlpha).compress();
        } finally {
            input.close();
        }
    }

    private List<File> get(Context context) throws IOException {
        List<File> results = new ArrayList<>();
        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();

        while (iterator.hasNext()) {
            results.add(compress(context, iterator.next()));
            iterator.remove();
        }

        return results;
    }

    private File compress(Context context, InputStreamProvider path) throws IOException {
        try {
            return compressReal(context, path);
        } finally {
            path.close();
        }
    }

    private File compressReal(Context context, InputStreamProvider path) throws IOException {
        File result;

        File outFile = getImageCacheFile(context, Checker.SINGLE.extSuffix(path));
        String source = Checker.isContent(path.getPath()) ? LubanUtils.getPath(context, Uri.parse(path.getPath())) : path.getPath();
        if (mRenameListener != null) {
            String filename = mRenameListener.rename(source);
            outFile = getImageCustomFile(context, filename);
        }

        if (mCompressionPredicate != null) {
            if (mCompressionPredicate.apply(source)
                    && Checker.SINGLE.needCompress(mLeastCompressSize, source)) {
                result = new Engine(path, outFile, focusAlpha).compress();
            } else {
                // Ignore compression
                result = new File(source);
            }
        } else {
            if (Checker.SINGLE.needCompress(mLeastCompressSize, source)) {
                result = new Engine(path, outFile, focusAlpha).compress();
            } else {
                // Ignore compression
                result = new File(source);
            }
        }

        return result;
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_COMPRESS_START:
                if (mCompressListener != null) {
                    mCompressListener.onStart();
                }
                if (mNewCompressListener != null) {
                    mNewCompressListener.onStart();
                }
                break;
            case MSG_COMPRESS_SUCCESS:
                if (mCompressListener != null) {
                    mCompressListener.onSuccess(msg.arg1, (File) msg.obj);
                }
                if (mNewCompressListener !=null) {
                    mNewCompressListener.onSuccess(msg.getData().getString(KEY_SOURCE), (File) msg.obj);
                }
                break;
            case MSG_COMPRESS_ERROR:
                if (mCompressListener != null) {
                    mCompressListener.onError(msg.arg1, (Throwable) msg.obj);
                }
                if (mNewCompressListener != null) {
                    mNewCompressListener.onError(msg.getData().getString(KEY_SOURCE), (Throwable) msg.obj);
                }
                break;
        }
        return false;
    }

    public static class Builder {
        private Context context;
        private String mTargetDir;
        private boolean focusAlpha;
        private boolean isUseBufferPool = true;
        private int mLeastCompressSize = 100;
        private OnRenameListener mRenameListener;
        private OnCompressListener mCompressListener;
        private OnNewCompressListener mNewCompressListener;
        private CompressionPredicate mCompressionPredicate;
        private List<InputStreamProvider> mStreamProviders;

        Builder(Context context) {
            this.context = context;
            this.mStreamProviders = new ArrayList<>();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(InputStreamProvider inputStreamProvider) {
            mStreamProviders.add(inputStreamProvider);
            return this;
        }

        public <T> Builder load(List<T> list) {
            int index = -1;
            for (T src : list) {
                index++;
                if (src instanceof String) {
                    load((String) src, index);
                } else if (src instanceof File) {
                    load((File) src, index);
                } else if (src instanceof Uri) {
                    load((Uri) src, index);
                } else {
                    throw new IllegalArgumentException("Incoming data type exception, it must be String, File, Uri or Bitmap");
                }
            }
            return this;
        }

        public Builder load(final File file) {
            load(file,0);
            return this;
        }

        private Builder load(final File file, int index) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() {
                    return ArrayPoolProvide.getInstance().openInputStream(file.getAbsolutePath());
                }

                @Override
                public int getIndex() {
                    return index;
                }


                @Override
                public String getPath() {
                    return file.getAbsolutePath();
                }
            });
            return this;
        }

        public Builder load(final String string) {
            load(string, 0);
            return this;
        }

        private Builder load(final String string, int index) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() {
                    return ArrayPoolProvide.getInstance().openInputStream(string);
                }

                @Override
                public int getIndex() {
                    return index;
                }

                @Override
                public String getPath() {
                    return string;
                }
            });
            return this;
        }

        public Builder load(final Uri uri) {
            load(uri, 0);
            return this;
        }

        private Builder load(final Uri uri, int index) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() throws IOException {
                    if (isUseBufferPool) {
                        return ArrayPoolProvide.getInstance().openInputStream(context.getContentResolver(), uri);
                    }
                    return context.getContentResolver().openInputStream(uri);
                }

                @Override
                public int getIndex() {
                    return index;
                }

                @Override
                public String getPath() {
                    return Checker.isContent(uri.toString()) ? uri.toString() : uri.getPath();
                }
            });
            return this;
        }

        @Deprecated
        public Builder putGear(int gear) {
            return this;
        }

        public Builder setRenameListener(OnRenameListener listener) {
            this.mRenameListener = listener;
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.mCompressListener = listener;
            return this;
        }

        public Builder setCompressListener(OnNewCompressListener listener) {
            this.mNewCompressListener = listener;
            return this;
        }

        public Builder setTargetDir(String targetDir) {
            this.mTargetDir = targetDir;
            return this;
        }

        /**
         * Do I need to keep the image's alpha channel
         *
         * @param focusAlpha <p> true - to keep alpha channel, the compress speed will be slow. </p>
         *                   <p> false - don't keep alpha channel, it might have a black background.</p>
         */
        @Deprecated
        public Builder setFocusAlpha(boolean focusAlpha) {
            this.focusAlpha = focusAlpha;
            return this;
        }

        /**
         * getContentResolver().openInputStream(); open using buffer pool mode
         *
         * @param isUseBufferPool
         * @return
         */
        public Builder isUseIOBufferPool(boolean isUseBufferPool) {
            this.isUseBufferPool = isUseBufferPool;
            return this;
        }

        /**
         * do not compress when the origin image file size less than one value
         *
         * @param size the value of file size, unit KB, default 100K
         */
        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        /**
         * do compress image when return value was true, otherwise, do not compress the image file
         *
         * @param compressionPredicate A predicate callback that returns true or false for the given input path should be compressed.
         */
        public Builder filter(CompressionPredicate compressionPredicate) {
            this.mCompressionPredicate = compressionPredicate;
            return this;
        }


        /**
         * begin compress image with asynchronous
         */
        public void launch() {
            build().launch(context);
        }

        public File get(final String path) throws IOException {
            return get(path, 0);
        }

        public File get(final String path,int index) throws IOException {
            return build().get(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() {
                    return ArrayPoolProvide.getInstance().openInputStream(path);
                }

                @Override
                public int getIndex() {
                    return index;
                }

                @Override
                public String getPath() {
                    return path;
                }
            }, context);
        }

        /**
         * begin compress image with synchronize
         *
         * @return the thumb image file list
         */
        public List<File> get() throws IOException {
            return build().get(context);
        }
    }
}