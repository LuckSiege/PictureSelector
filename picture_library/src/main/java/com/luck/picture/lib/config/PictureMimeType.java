package com.luck.picture.lib.config;


import android.content.Context;
import android.text.TextUtils;

import com.luck.picture.lib.R;

import java.io.File;

/**
 * @author：luck
 * @date：2017-5-24 17:02
 * @describe：PictureMimeType
 */

public final class PictureMimeType {
    public static int ofAll() {
        return PictureConfig.TYPE_ALL;
    }

    public static int ofImage() {
        return PictureConfig.TYPE_IMAGE;
    }

    public static int ofVideo() {
        return PictureConfig.TYPE_VIDEO;
    }

    /**
     * # No longer maintain audio related functions,
     * but can continue to use but there will be phone compatibility issues
     * <p>
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    @Deprecated
    public static int ofAudio() {
        return PictureConfig.TYPE_AUDIO;
    }


    public static String ofPNG() {
        return MIME_TYPE_PNG;
    }

    public static String ofJPEG() {
        return MIME_TYPE_JPEG;
    }

    public static String ofBMP() {
        return MIME_TYPE_BMP;
    }

    public static String ofGIF() {
        return MIME_TYPE_GIF;
    }

    public static String ofWEBP() {
        return MIME_TYPE_WEBP;
    }

    public static String of3GP() {
        return MIME_TYPE_3GP;
    }

    public static String ofMP4() {
        return MIME_TYPE_MP4;
    }

    public static String ofMPEG() {
        return MIME_TYPE_MPEG;
    }

    public static String ofAVI() {
        return MIME_TYPE_AVI;
    }

    private final static String MIME_TYPE_PNG = "image/png";
    public final static String MIME_TYPE_JPEG = "image/jpeg";
    private final static String MIME_TYPE_JPG = "image/jpg";
    private final static String MIME_TYPE_BMP = "image/bmp";
    private final static String MIME_TYPE_GIF = "image/gif";
    private final static String MIME_TYPE_WEBP = "image/webp";

    private final static String MIME_TYPE_3GP = "video/3gp";
    private final static String MIME_TYPE_MP4 = "video/mp4";
    private final static String MIME_TYPE_MPEG = "video/mpeg";
    private final static String MIME_TYPE_AVI = "video/avi";


    /**
     * isGif
     *
     * @param mimeType
     * @return
     */
    public static boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }


    /**
     * isVideo
     *
     * @param mimeType
     * @return
     */
    public static boolean isHasVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO);
    }

    /**
     * isVideo
     *
     * @param url
     * @return
     */
    public static boolean isUrlHasVideo(String url) {
        return url.endsWith(".mp4");
    }

    /**
     * isAudio
     *
     * @param mimeType
     * @return
     */
    public static boolean isHasAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO);
    }

    /**
     * isImage
     *
     * @param mimeType
     * @return
     */
    public static boolean isHasImage(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE);
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file mimeType
     */
    public static boolean isJPEG(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.startsWith(MIME_TYPE_JPEG) || mimeType.startsWith(MIME_TYPE_JPG);
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file mimeType
     */
    public static boolean isJPG(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.startsWith(MIME_TYPE_JPG);
    }


    /**
     * is Network image
     *
     * @param path
     * @return
     */
    public static boolean isHasHttp(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.startsWith("http")
                || path.startsWith("https")
                || path.startsWith("/http")
                || path.startsWith("/https");
    }

    /**
     * Determine whether the file type is an image or a video
     *
     * @param cameraMimeType
     * @return
     */
    public static String getMimeType(int cameraMimeType) {
        switch (cameraMimeType) {
            case PictureConfig.TYPE_VIDEO:
                return MIME_TYPE_VIDEO;
            case PictureConfig.TYPE_AUDIO:
                return MIME_TYPE_AUDIO;
            default:
                return MIME_TYPE_IMAGE;
        }
    }

    /**
     * Determines if the file name is a picture
     *
     * @param name
     * @return
     */
    public static boolean isSuffixOfImage(String name) {
        return !TextUtils.isEmpty(name) && name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                || name.endsWith(".bmp");
    }

    /**
     * Is it the same type
     *
     * @param oldMimeType
     * @param newMimeType
     * @return
     */
    public static boolean isMimeTypeSame(String oldMimeType, String newMimeType) {

        return getMimeType(oldMimeType) == getMimeType(newMimeType);
    }

    /**
     * Get Image mimeType
     *
     * @param path
     * @return
     */
    public static String getImageMimeType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last);
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MIME_TYPE_IMAGE;
        }
        return MIME_TYPE_IMAGE;
    }


    /**
     * Picture or video
     *
     * @return
     */
    public static int getMimeType(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return PictureConfig.TYPE_IMAGE;
        }
        if (mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)) {
            return PictureConfig.TYPE_VIDEO;
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)) {
            return PictureConfig.TYPE_AUDIO;
        } else {
            return PictureConfig.TYPE_IMAGE;
        }
    }

    /**
     * Get image suffix
     *
     * @param mineType
     * @return
     */
    public static String getLastImgSuffix(String mineType) {
        String defaultSuffix = PNG;
        try {
            int index = mineType.lastIndexOf("/") + 1;
            if (index > 0) {
                return "." + mineType.substring(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultSuffix;
        }
        return defaultSuffix;
    }


    /**
     * is content://
     *
     * @param url
     * @return
     */
    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }

    /**
     * Returns an error message by type
     *
     * @param context
     * @param mimeType
     * @return
     */
    public static String s(Context context, String mimeType) {
        Context ctx = context.getApplicationContext();
        if (isHasVideo(mimeType)) {
            return ctx.getString(R.string.picture_video_error);
        } else if (isHasAudio(mimeType)) {
            return ctx.getString(R.string.picture_audio_error);
        } else {
            return ctx.getString(R.string.picture_error);
        }
    }

    public final static String JPEG = ".jpg";

    private final static String PNG = ".png";

    public final static String MP4 = ".mp4";

    public final static String JPEG_Q = "image/jpeg";

    public final static String PNG_Q = "image/png";

    public final static String MP4_Q = "video/mp4";

    public final static String AVI_Q = "video/avi";

    public final static String DCIM = "DCIM/Camera";

    public final static String CAMERA = "Camera";

    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_AUDIO = "audio/mpeg";


    private final static String MIME_TYPE_PREFIX_IMAGE = "image";
    private final static String MIME_TYPE_PREFIX_VIDEO = "video";
    private final static String MIME_TYPE_PREFIX_AUDIO = "audio";

}
