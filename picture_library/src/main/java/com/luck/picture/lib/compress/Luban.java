package com.luck.picture.lib.compress;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.PictureContentResolver;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class Luban {
    private static final String TAG = "Luban";

    private String mTargetDir;
    private final String mNewFileName;
    private final boolean focusAlpha;
    private final boolean isCamera;
    private final int mLeastCompressSize;
    private final OnRenameListener mRenameListener;
    private final OnCompressListener mCompressListener;
    private final CompressionPredicate mCompressionPredicate;
    private final List<InputStreamProvider> mStreamProviders;
    private final List<String> mPaths;
    private final List<LocalMedia> mediaList;
    private int index = -1;
    private final int compressQuality;
    private final int dataCount;
    private final boolean isAutoRotating;

    private Luban(Builder builder) {
        this.mPaths = builder.mPaths;
        this.mediaList = builder.mediaList;
        this.dataCount = builder.dataCount;
        this.mTargetDir = builder.mTargetDir;
        this.mNewFileName = builder.mNewFileName;
        this.mRenameListener = builder.mRenameListener;
        this.mStreamProviders = builder.mStreamProviders;
        this.mCompressListener = builder.mCompressListener;
        this.mLeastCompressSize = builder.mLeastCompressSize;
        this.mCompressionPredicate = builder.mCompressionPredicate;
        this.compressQuality = builder.compressQuality;
        this.isAutoRotating = builder.isAutoRotating;
        this.focusAlpha = builder.focusAlpha;
        this.isCamera = builder.isCamera;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context A context.
     */
    private File getImageCacheFile(Context context, InputStreamProvider provider, String suffix) {
        if (TextUtils.isEmpty(mTargetDir)) {
            File imageCacheDir = getImageCacheDir(context);
            if (imageCacheDir != null) {
                mTargetDir = imageCacheDir.getAbsolutePath();
            }
        }
        String cacheBuilder = "";
        try {
            LocalMedia media = provider.getMedia();
            String encryptionValue = StringUtils.getEncryptionValue(media.getId(), media.getWidth(), media.getHeight());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mTargetDir);
            if (TextUtils.isEmpty(encryptionValue) && !media.isCut()) {
                String imgCmpTime_ = DateUtils.getCreateFileName("IMG_CMP_");
                cacheBuilder = stringBuilder.append("/").append(imgCmpTime_).append(TextUtils.isEmpty(suffix) ? PictureMimeType.JPG : suffix).toString();
            } else {
                cacheBuilder = stringBuilder.append("/IMG_CMP_").append(encryptionValue).append(TextUtils.isEmpty(suffix) ? PictureMimeType.JPG : suffix).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new File(cacheBuilder);
    }

    private File getImageCustomFile(Context context, String filename) {
        if (TextUtils.isEmpty(mTargetDir)) {
            File imageCacheDir = getImageCacheDir(context);
            mTargetDir = imageCacheDir != null ? imageCacheDir.getAbsolutePath() : "";
        }
        String cacheBuilder = mTargetDir + "/" + filename;
        return new File(cacheBuilder);
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context A context.
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
            return;
        }
        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();
        if (mCompressListener != null) {
            mCompressListener.onStart();
        }
        PictureThreadUtils.executeBySingle(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {

            @Override
            public List<LocalMedia> doInBackground() {
                // 当前压缩下标
                index = -1;
                while (iterator.hasNext()) {
                    try {
                        index++;
                        InputStreamProvider path = iterator.next();
                        String newPath = null;
                        if (path.getMedia().isCompressed() && !TextUtils.isEmpty(path.getMedia().getCompressPath())) {
                            // 压缩过的图片不重复压缩  注意:如果是开启了裁剪 就算压缩过也要重新压缩
                            boolean exists = !path.getMedia().isCut() && new File(path.getMedia().getCompressPath()).exists();
                            File result = exists ? new File(path.getMedia().getCompressPath()) : compress(context, path);
                            if (result != null) {
                                newPath = result.getAbsolutePath();
                            }
                        } else {
                            if (PictureMimeType.isHasHttp(path.getMedia().getPath()) && TextUtils.isEmpty(path.getMedia().getCutPath())) {
                                newPath = path.getMedia().getPath();
                            } else {
                                File result = PictureMimeType.isHasVideo(path.getMedia().getMimeType())
                                        ? new File(path.getPath()) : compress(context, path);
                                if (result != null) {
                                    newPath = result.getAbsolutePath();
                                }
                            }
                        }
                        if (mediaList != null && mediaList.size() > 0) {
                            LocalMedia media = mediaList.get(index);
                            boolean isHasHttp = PictureMimeType.isHasHttp(newPath);
                            boolean isHasVideo = PictureMimeType.isHasVideo(media.getMimeType());
                            media.setCompressed(!isHasHttp && !isHasVideo && !TextUtils.isEmpty(newPath));
                            media.setCompressPath(isHasHttp || isHasVideo ? null : newPath);
                            media.setAndroidQToPath(SdkVersionUtils.checkedAndroid_Q() ? media.getCompressPath() : null);
                            boolean isLast = index == mediaList.size() - 1;
                            if (isLast) {
                                return mediaList;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iterator.remove();
                }
                return null;
            }

            @Override
            public void onSuccess(List<LocalMedia> result) {
                if (mCompressListener == null) {
                    return;
                }
                if (result != null) {
                    mCompressListener.onSuccess(result);
                } else {
                    mCompressListener.onError(new Throwable("Failed to compress file"));
                }
            }
        });
    }

    /**
     * start compress and return the file
     */
    private File get(InputStreamProvider input, Context context) throws IOException {
        try {
            return new Engine(context, input, getImageCacheFile(context, input, Checker.SINGLE.extSuffix(input.getMedia().getMimeType())), focusAlpha, compressQuality, isAutoRotating).compress();
        } finally {
            input.close();
        }
    }

    private List<LocalMedia> get(Context context) throws Exception {
        List<LocalMedia> results = new ArrayList<>();
        Iterator<InputStreamProvider> iterator = mStreamProviders.iterator();
        while (iterator.hasNext()) {
            InputStreamProvider provider = iterator.next();
            if (provider.getMedia() == null) {
                continue;
            }
            LocalMedia localMedia = provider.getMedia();
            if (localMedia.isCompressed() && !TextUtils.isEmpty(localMedia.getCompressPath())) {
                // 压缩过的图片不重复压缩  注意:如果是开启了裁剪 就算压缩过也要重新压缩
                boolean exists = !localMedia.isCut() && new File(localMedia.getCompressPath()).exists();
                File result = exists ? new File(localMedia.getCompressPath())
                        : compress(context, provider);
                if (result != null) {
                    String absolutePath = result.getAbsolutePath();
                    localMedia.setCompressed(true);
                    localMedia.setCompressPath(absolutePath);
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        localMedia.setAndroidQToPath(absolutePath);
                    }
                }
                results.add(localMedia);
            } else {
                boolean isHasHttp = PictureMimeType.isHasHttp(localMedia.getPath()) && TextUtils.isEmpty(localMedia.getCutPath());
                boolean isHasVideo = PictureMimeType.isHasVideo(localMedia.getMimeType());
                File result = isHasHttp || isHasVideo ? new File(localMedia.getPath()) : compress(context, provider);
                if (result != null) {
                    String absolutePath = result.getAbsolutePath();
                    boolean http = PictureMimeType.isHasHttp(absolutePath);
                    boolean flag = !TextUtils.isEmpty(absolutePath) && http;
                    localMedia.setCompressed(!isHasVideo && !flag);
                    localMedia.setCompressPath(isHasVideo || flag ? null : absolutePath);
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        localMedia.setAndroidQToPath(localMedia.getCompressPath());
                    }
                }
                results.add(localMedia);
            }
            iterator.remove();
        }

        return results;
    }

    private File compress(Context context, InputStreamProvider path) throws Exception {
        try {
            return compressRealLocalMedia(context, path);
        } finally {
            path.close();
        }
    }

    private File compressReal(Context context, InputStreamProvider streamProvider) throws IOException {
        File result;
        String suffix = Checker.SINGLE.extSuffix(streamProvider.getMedia() != null ? streamProvider.getMedia().getMimeType() : "");
        File outFile = getImageCacheFile(context, streamProvider, suffix);
        if (mRenameListener != null) {
            String filename = mRenameListener.rename(streamProvider.getPath());
            outFile = getImageCustomFile(context, filename);
        }

        if (mCompressionPredicate != null) {
            if (mCompressionPredicate.apply(streamProvider.getPath())
                    && Checker.SINGLE.needCompress(mLeastCompressSize, streamProvider.getPath())) {
                result = new Engine(context, streamProvider, outFile, focusAlpha, compressQuality, isAutoRotating).compress();
            } else {
                result = new File(streamProvider.getPath());
            }
        } else {
            if (suffix.startsWith(".gif")) {
                // GIF without compression
                result = new File(streamProvider.getPath());
            } else {
                result = Checker.SINGLE.needCompress(mLeastCompressSize, streamProvider.getPath()) ?
                        new Engine(context, streamProvider, outFile, focusAlpha, compressQuality, isAutoRotating).compress() :
                        new File(streamProvider.getPath());
            }
        }
        return result;
    }

    private File compressRealLocalMedia(Context context, InputStreamProvider streamProvider) throws Exception {
        File result;
        LocalMedia media = streamProvider.getMedia();
        String newPath = media.isCut() && !TextUtils.isEmpty(media.getCutPath()) ? media.getCutPath() : media.getRealPath();
        String suffix = Checker.SINGLE.extSuffix(media.getMimeType());
        File outFile = getImageCacheFile(context, streamProvider, suffix);
        String filename = "";
        if (!TextUtils.isEmpty(mNewFileName)) {
            filename = isCamera || dataCount == 1 ? mNewFileName : StringUtils.rename(mNewFileName);
            outFile = getImageCustomFile(context, filename);
        }
        // 如果文件存在直接返回不处理
        if (outFile.exists()) {
            return outFile;
        }
        if (mCompressionPredicate != null) {
            if (suffix.startsWith(".gif")) {
                // GIF without compression
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    if (media.isCut() && !TextUtils.isEmpty(media.getCutPath())) {
                        result = new File(media.getCutPath());
                    } else {
                        String androidQToPath = AndroidQTransformUtils.copyPathToAndroidQ(context, streamProvider.getMedia().getId(), streamProvider.getPath(),
                                media.getWidth(), media.getHeight(), media.getMimeType(), filename);
                        result = new File(androidQToPath);
                    }
                } else {
                    result = new File(newPath);
                }
            } else {
                boolean isCompress = Checker.SINGLE.needCompressToLocalMedia(mLeastCompressSize, newPath);
                if (mCompressionPredicate.apply(newPath) && isCompress) {
                    result = new Engine(context, streamProvider, outFile, focusAlpha, compressQuality, isAutoRotating).compress();
                } else {
                    if (isCompress) {
                        result = new Engine(context, streamProvider, outFile, focusAlpha, compressQuality, isAutoRotating).compress();
                    } else {
                        // 这种情况判断一下，如果是小于设置的图片压缩阀值，再Android 10以上做下拷贝的处理
                        if (SdkVersionUtils.checkedAndroid_Q()) {
                            String newFilePath = media.isCut() ? media.getCutPath() :
                                    AndroidQTransformUtils.copyPathToAndroidQ(context, media.getId(),
                                            streamProvider.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), filename);
                            result = new File(TextUtils.isEmpty(newFilePath) ? newPath : newFilePath);
                        } else {
                            result = new File(newPath);
                        }
                    }
                }
            }
        } else {
            if (suffix.startsWith(".gif")) {
                // GIF without compression
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    String newFilePath = media.isCut() ? media.getCutPath() :
                            AndroidQTransformUtils.copyPathToAndroidQ(context, media.getId(),
                                    streamProvider.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), filename);
                    result = new File(TextUtils.isEmpty(newFilePath) ? newPath : newFilePath);
                } else {
                    result = new File(newPath);
                }
            } else {
                boolean isCompress = Checker.SINGLE.needCompressToLocalMedia(mLeastCompressSize, newPath);
                if (isCompress) {
                    result = new Engine(context, streamProvider, outFile, focusAlpha, compressQuality, isAutoRotating).compress();
                } else {
                    // 这种情况判断一下，如果是小于设置的图片压缩阀值，再Android 10以上做下拷贝的处理
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        String newFilePath = media.isCut() ? media.getCutPath() :
                                AndroidQTransformUtils.copyPathToAndroidQ(context, media.getId(),
                                        streamProvider.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), filename);
                        result = new File(TextUtils.isEmpty(newFilePath) ? newPath : newFilePath);
                    } else {
                        result = new File(newPath);
                    }
                }
            }
        }
        return result;
    }

    public static class Builder {
        private final Context context;
        private String mTargetDir;
        private String mNewFileName;
        private boolean focusAlpha;
        private boolean isCamera;
        private int compressQuality;
        private boolean isAutoRotating;
        private int mLeastCompressSize = 100;
        private OnRenameListener mRenameListener;
        private OnCompressListener mCompressListener;
        private CompressionPredicate mCompressionPredicate;
        private final List<InputStreamProvider> mStreamProviders;
        private final List<String> mPaths;
        private List<LocalMedia> mediaList;
        private int dataCount;

        Builder(Context context) {
            this.context = context;
            this.mPaths = new ArrayList<>();
            this.mediaList = new ArrayList<>();
            this.mStreamProviders = new ArrayList<>();
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
            this.dataCount = list.size();
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
                    if (PictureMimeType.isContent(media.getPath()) && !media.isCut()) {
                        if (TextUtils.isEmpty(media.getAndroidQToPath())) {
                            return PictureContentResolver.getContentResolverOpenInputStream(context, Uri.parse(media.getPath()));
                        } else {
                            return new FileInputStream(media.getAndroidQToPath());
                        }
                    } else {
                        if (PictureMimeType.isHasHttp(media.getPath()) && TextUtils.isEmpty(media.getCutPath())) {
                            return null;
                        } else {
                            return new FileInputStream(media.isCut() ? media.getCutPath() : media.getPath());
                        }
                    }
                }

                @Override
                public String getPath() {
                    if (media.isCut()) {
                        return media.getCutPath();
                    } else {
                        return TextUtils.isEmpty(media.getAndroidQToPath()) ? media.getPath() : media.getAndroidQToPath();
                    }
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
                public InputStream openInternal() {
                    return PictureContentResolver.getContentResolverOpenInputStream(context, uri);
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

        @Deprecated
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

        public Builder setNewCompressFileName(String newFileName) {
            this.mNewFileName = newFileName;
            return this;
        }

        public Builder isCamera(boolean isCamera) {
            this.isCamera = isCamera;
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
         * Image compressed output quality
         *
         * @param compressQuality The quality is better than
         */
        public Builder setCompressQuality(int compressQuality) {
            this.compressQuality = compressQuality;
            return this;
        }

        /**
         * If the picture is in the wrong direction Auto Rotate Picture
         *
         * @param isAutoRotating
         */
        public Builder isAutoRotating(boolean isAutoRotating) {
            this.isAutoRotating = isAutoRotating;
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
        public List<LocalMedia> get() throws Exception {
            return build().get(context);
        }
    }
}