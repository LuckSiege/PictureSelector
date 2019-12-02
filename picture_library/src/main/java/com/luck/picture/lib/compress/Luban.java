package com.luck.picture.lib.compress;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;
    private int compressQuality;
    private String mTargetDir;
    private boolean focusAlpha;
    private int mLeastCompressSize;
    private OnRenameListener mRenameListener;
    private OnCompressListener mCompressListener;
    private CompressionPredicate mCompressionPredicate;
    private List<InputStreamProvider> mStreamProviders;
    private List<String> mPaths;
    private List<LocalMedia> mediaList;
    private int index = -1;
    private Handler mHandler;
    private boolean isAndroidQ;

    private Luban(Builder builder) {
        this.mPaths = builder.mPaths;
        this.mediaList = builder.mediaList;
        this.mTargetDir = builder.mTargetDir;
        this.mRenameListener = builder.mRenameListener;
        this.mStreamProviders = builder.mStreamProviders;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        this.mCompressionPredicate = builder.mCompressionPredicate;
        this.compressQuality = builder.compressQuality;
        this.mHandler = new Handler(Looper.getMainLooper(), this);
        this.isAndroidQ = builder.isAndroidQ;
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
            if (getImageCacheDir(context) != null) {
                mTargetDir = getImageCacheDir(context).getAbsolutePath();
            }
        }
        String cacheBuilder = mTargetDir + "/" + DateUtils.getCreateFileName("IMG_") +
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
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context   A context.
     * @param cacheName The name of the subdirectory in which to store the cache.
     * @see #getImageCacheDir(Context)
     */
    private static File getImageCacheDir(Context context) {
        File cacheDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (cacheDir != null) {
            if (!cacheDir.mkdirs() && (!cacheDir.exists() || !cacheDir.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return cacheDir;
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
        if (mStreamProviders == null || mPaths == null || mStreamProviders.size() == 0 && mCompressListener != null) {
            mCompressListener.onError(new NullPointerException("image file cannot be null"));
        }
        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();
        // 当前压缩下标
        index = -1;
        while (iterator.hasNext()) {
            final InputStreamProvider path = iterator.next();
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                try {
                    index++;
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                    File result = compress(context, path);

                    if (mediaList != null && mediaList.size() > 0) {
                        LocalMedia media = mediaList.get(index);
                        String newPath = result.getAbsolutePath();
                        boolean eqHttp = PictureMimeType.isHttp(newPath);
                        media.setCompressed(eqHttp ? false : true);
                        media.setCompressPath(eqHttp ? "" : result.getAbsolutePath());
                        boolean isLast = index == mediaList.size() - 1;
                        if (isLast) {
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, mediaList));
                        }
                    } else {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, new IOException()));
                    }
                } catch (IOException e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
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
            return new Engine(input, getImageCacheFile(context, Checker.SINGLE.extSuffix(input)), focusAlpha, compressQuality).compress();
        } finally {
            input.close();
        }
    }

    private List<File> get(Context context) throws IOException {
        List<File> results = new ArrayList<>();
        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();

        while (iterator.hasNext()) {
            InputStreamProvider provider = iterator.next();
            results.add(compress(context, provider));
            iterator.remove();
        }

        return results;
    }

    private File compress(Context context, InputStreamProvider path) throws IOException {
        try {
            return compressRealLocalMedia(context, path);
        } finally {
            path.close();
        }
    }

    private File compressReal(Context context, InputStreamProvider path) throws IOException {
        File result;
        String suffix = Checker.SINGLE.extSuffix(path.getMedia() != null ? path.getMedia().getMimeType() : "");
        File outFile = getImageCacheFile(context, TextUtils.isEmpty(suffix) ? Checker.SINGLE.extSuffix(path) : suffix);
        if (mRenameListener != null) {
            String filename = mRenameListener.rename(path.getPath());
            outFile = getImageCustomFile(context, filename);
        }

        if (mCompressionPredicate != null) {
            if (mCompressionPredicate.apply(path.getPath())
                    && Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath())) {
                result = new Engine(path, outFile, focusAlpha, compressQuality).compress();
            } else {
                result = new File(path.getPath());
            }
        } else {
            if (Checker.SINGLE.extSuffix(path).startsWith(".gif")) {
                // GIF without compression
                result = new File(path.getPath());
            } else {
                result = Checker.SINGLE.needCompress(mLeastCompressSize, path.getPath()) ?
                        new Engine(path, outFile, focusAlpha, compressQuality).compress() :
                        new File(path.getPath());
            }
        }
        return result;
    }

    private File compressRealLocalMedia(Context context, InputStreamProvider path) throws IOException {
        File result;
        LocalMedia media = path.getMedia();
        String newPath = isAndroidQ ? PictureFileUtils
                .getPath(context, Uri.parse(path.getPath())) : path.getPath();
        String suffix = Checker.SINGLE.extSuffix(media != null ? path.getMedia().getMimeType() : "");
        File outFile = getImageCacheFile(context, TextUtils.isEmpty(suffix) ? Checker.SINGLE.extSuffix(path) : suffix);
        String filename = "";
        if (mRenameListener != null) {
            filename = mRenameListener.rename(newPath);
            if (!TextUtils.isEmpty(filename)) {
                outFile = getImageCustomFile(context, filename);
            }
        }
        if (mCompressionPredicate != null) {
            if (Checker.SINGLE.extSuffix(path).startsWith(".gif")) {
                // GIF without compression
                if (isAndroidQ) {
                    String newFilePath = media.isCut() ? media.getCutPath() :
                            AndroidQTransformUtils.copyImagePathToDirectoryPictures
                                    (context, path.getPath(), filename, media.getMimeType());
                    result = new File(newFilePath);
                } else {
                    result = new File(newPath);
                }
            } else {
                if (mCompressionPredicate.apply(newPath)
                        && Checker.SINGLE.needCompressToLocalMedia(mLeastCompressSize, newPath)) {
                    result = new Engine(path, outFile, focusAlpha, compressQuality).compress();
                } else {
                    if (isAndroidQ) {
                        String newFilePath = media.isCut() ? media.getCutPath() :
                                AndroidQTransformUtils.copyImagePathToDirectoryPictures
                                        (context, path.getPath(), filename, media.getMimeType());
                        result = new File(newFilePath);
                    } else {
                        result = new File(newPath);
                    }
                }
            }
        } else {
            if (Checker.SINGLE.extSuffix(path).startsWith(".gif")) {
                // GIF without compression
                if (isAndroidQ) {
                    String newFilePath = media.isCut() ? media.getCutPath() :
                            AndroidQTransformUtils.copyImagePathToDirectoryPictures
                                    (context, path.getPath(), filename, media.getMimeType());
                    result = new File(newFilePath);
                } else {
                    result = new File(newPath);
                }
            } else {
                boolean isCompress = Checker.SINGLE.needCompressToLocalMedia(mLeastCompressSize, newPath);
                result = isCompress ? new Engine(path, outFile, focusAlpha, compressQuality).compress() :
                        isAndroidQ ? new File(media.isCut() ? media.getCutPath() :
                                AndroidQTransformUtils.copyImagePathToDirectoryPictures
                                        (context, path.getPath(), filename, media.getMimeType())) : new File(newPath);
            }
        }
        return result;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mCompressListener.onSuccess((List<LocalMedia>) msg.obj);
                break;
            case MSG_COMPRESS_ERROR:
                mCompressListener.onError((Throwable) msg.obj);
                break;
        }
        return false;
    }

    public static class Builder {
        private Context context;
        private String mTargetDir;
        private boolean focusAlpha;
        private int compressQuality;
        private int mLeastCompressSize = 100;
        private OnRenameListener mRenameListener;
        private OnCompressListener mCompressListener;
        private CompressionPredicate mCompressionPredicate;
        private List<InputStreamProvider> mStreamProviders;
        private List<String> mPaths;
        private List<LocalMedia> mediaList;
        private boolean isAndroidQ;

        Builder(Context context) {
            this.context = context;
            this.mPaths = new ArrayList<>();
            this.mediaList = new ArrayList<>();
            this.mStreamProviders = new ArrayList<>();
            this.isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(InputStreamProvider inputStreamProvider) {
            mStreamProviders.add(inputStreamProvider);
            return this;
        }

        /**
         * 扩展符合PictureSelector的压缩策略
         *
         * @param list LocalMedia集合
         * @param <T>
         * @return
         */
        public <T> Builder loadMediaData(List<LocalMedia> list) {
            this.mediaList = list;
            for (LocalMedia src : list) {
                load(src);
            }
            return this;
        }


        /**
         * 扩展符合PictureSelector的压缩策略
         *
         * @param media LocalMedia对象
         * @param <T>
         * @return
         */
        private Builder load(final LocalMedia media) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() throws IOException {
                    return isAndroidQ ?
                            context.getContentResolver().openInputStream(Uri.parse(media.getPath()))
                            : new FileInputStream(media.isCut() ? media.getCutPath() : media.getPath());
                }

                @Override
                public String getPath() {
                    return media.isCut() ? media.getCutPath() : media.getPath();
                }

                @Override
                public LocalMedia getMedia() {
                    return media;
                }
            });
            return this;
        }

        public Builder load(final Uri uri) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() throws IOException {
                    return context.getContentResolver().openInputStream(uri);
                }

                @Override
                public String getPath() {
                    return uri.getPath();
                }

                @Override
                public LocalMedia getMedia() {
                    return null;
                }
            });
            return this;
        }

        public Builder load(final File file) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(file);
                }

                @Override
                public String getPath() {
                    return file.getAbsolutePath();
                }

                @Override
                public LocalMedia getMedia() {
                    return null;
                }

            });
            return this;
        }

        public Builder load(final String string) {
            mStreamProviders.add(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(string);
                }

                @Override
                public String getPath() {
                    return string;
                }

                @Override
                public LocalMedia getMedia() {
                    return null;
                }

            });
            return this;
        }

        public <T> Builder load(List<T> list) {
            for (T src : list) {
                if (src instanceof String) {
                    load((String) src);
                } else if (src instanceof File) {
                    load((File) src);
                } else if (src instanceof Uri) {
                    load((Uri) src);
                } else {
                    throw new IllegalArgumentException("Incoming data type exception, it must be String, File, Uri or Bitmap");
                }
            }
            return this;
        }

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
        public Builder setFocusAlpha(boolean focusAlpha) {
            this.focusAlpha = focusAlpha;
            return this;
        }

        /**
         * Image compressed output quality
         *
         * @param compressQuality The quality is better than
         */
        public Builder setCompressQuality(int compressQuality) {
            this.compressQuality = compressQuality;
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
            return build().get(new InputStreamAdapter() {
                @Override
                public InputStream openInternal() throws IOException {
                    return new FileInputStream(path);
                }

                @Override
                public String getPath() {
                    return path;
                }

                @Override
                public LocalMedia getMedia() {
                    return null;
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