package com.luck.picture.lib.compress;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";

    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;
    private static final int MSG_COMPRESS_MULTIPLE_SUCCESS = 3;
    private String mTargetDir;
    private List<String> mPaths;
    private List<LocalMedia> medias;
    private int mLeastCompressSize;
    private OnCompressListener mCompressListener;
    private int index = -1;
    private Handler mHandler;
    private Context context;

    private Luban(Builder builder) {
        this.mPaths = builder.mPaths;
        this.medias = builder.medias;
        this.context = builder.context;
        this.mTargetDir = builder.mTargetDir;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * Returns a mFile with a cache audio name in the private cache directory.
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

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     *
     * @param context A context.
     * @see #getImageCacheDir(Context, String)
     */
    @Nullable
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
    @Nullable
    private File getImageCacheDir(Context context, String cacheName) {
        String dir = PictureFileUtils.getDiskCacheDir(context);
        File cacheDir = new File(dir);
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
    @UiThread
    private void launch(final Context context) {
        if (mPaths == null || mPaths.size() == 0 && mCompressListener != null) {
            mCompressListener.onError(new NullPointerException("image file cannot be null"));
        }

        Iterator<String> iterator = mPaths.iterator();
        index = -1;// 当前压缩下标
        while (iterator.hasNext()) {
            final String path = iterator.next();
            if (Checker.isImage(path)) {
                AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            index++;
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                            File result = Checker.isNeedCompress(mLeastCompressSize, path) ?
                                    new Engine(path, getImageCacheFile(context, Checker.checkSuffix(path))).compress() :
                                    new File(path);
                            if (medias != null && medias.size() > 0) {
                                LocalMedia media = medias.get(index);
                                String path = result.getAbsolutePath();
                                boolean eqHttp = PictureMimeType.isHttp(path);
                                media.setCompressed(eqHttp ? false : true);
                                media.setCompressPath(eqHttp ? "" : result.getAbsolutePath());
                                boolean isLast = index == medias.size() - 1;
                                if (isLast) {
                                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_MULTIPLE_SUCCESS, medias));
                                }
                            } else {
                                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, new IOException()));
                            }
                        } catch (IOException e) {
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                        }
                    }
                });
            } else {
                mCompressListener.onError(new IllegalArgumentException("can not read the path : " + path));
            }
            iterator.remove();
        }
    }

    /**
     * start compress and return the mFile
     */
    @WorkerThread
    private File get(String path, Context context) throws IOException {
        return Checker.isNeedCompress(mLeastCompressSize, path) ?
                new Engine(path, getImageCacheFile(context, Checker.checkSuffix(path))).compress() :
                new File(path);
    }

    @WorkerThread
    private List<File> get(Context context) throws IOException {
        List<File> results = new ArrayList<>();
        Iterator<String> iterator = mPaths.iterator();

        while (iterator.hasNext()) {
            String path = iterator.next();
            if (Checker.isImage(path)) {
                File result = Checker.isNeedCompress(mLeastCompressSize, path) ?
                        new Engine(path, getImageCacheFile(context, Checker.checkSuffix(path))).compress() :
                        new File(path);
                results.add(result);
            }
            iterator.remove();
        }

        return results;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_MULTIPLE_SUCCESS:
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
        private List<String> mPaths;
        private List<LocalMedia> medias;
        private int mLeastCompressSize = 100;
        private OnCompressListener mCompressListener;

        Builder(Context context) {
            this.context = context;
            this.mPaths = new ArrayList<>();
        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(File file) {
            this.mPaths.add(file.getAbsolutePath());
            return this;
        }

        public Builder load(String string) {
            this.mPaths.add(string);
            return this;
        }

        public Builder load(List<String> list) {
            this.mPaths.addAll(list);
            return this;
        }

        public Builder loadLocalMedia(List<LocalMedia> list) {
            if (list == null) {
                list = new ArrayList<>();
            }
            this.medias = list;
            for (LocalMedia media : list) {
                this.mPaths.add(media.isCut() ? media.getCutPath() : media.getPath());
            }
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
         * do not compress when the origin image file size less than one value
         *
         * @param size the value of file size, unit KB, default 100K
         */
        public Builder ignoreBy(int size) {
            this.mLeastCompressSize = size;
            return this;
        }

        /**
         * begin compress image with asynchronous
         */
        public void launch() {
            build().launch(context);
        }

        public File get(String path) throws IOException {
            return build().get(path, context);
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