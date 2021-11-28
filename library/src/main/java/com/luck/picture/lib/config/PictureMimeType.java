package com.luck.picture.lib.config;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.luck.picture.lib.R;

import java.io.File;

/**
 * @author：luck
 * @date：2017-5-24 17:02
 * @describe：PictureMimeType
 */

public final class PictureMimeType {

    /**
     * get uri
     *
     * @param id
     * @return
     */
    public static String getRealPathUri(long id, String mimeType) {
        Uri contentUri;
        if (PictureMimeType.isHasImage(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (PictureMimeType.isHasVideo(mimeType)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        return ContentUris.withAppendedId(contentUri, id).toString();
    }

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
     * isGif
     *
     * @param url
     * @return
     */
    public static boolean isUrlHasGif(String url) {
        return url.toLowerCase().endsWith(".gif");
    }

    /**
     * is has image
     *
     * @param url
     * @return
     */
    public static boolean isUrlHasImage(String url) {
        return url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".jpeg")
                || url.toLowerCase().endsWith(".png");
    }

    /**
     * isWebp
     *
     * @param mimeType
     * @return
     */
    public static boolean isWebp(String mimeType) {
        return mimeType != null && mimeType.equalsIgnoreCase("image/webp");
    }

    /**
     * 是否是长图
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @return
     */
    public static boolean isLongImage(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        return height > width * 3;
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
        return url.toLowerCase().endsWith(".mp4");
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
     * isAudio
     *
     * @param url
     * @return
     */
    public static boolean isUrlHasAudio(String url) {
        return url.toLowerCase().endsWith(".amr");
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
        return path.startsWith("http") || path.startsWith("https") || path.startsWith("/http") || path.startsWith("/https");
    }

    /**
     * 获取mimeType
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getMimeTypeFromMediaContentUri(Context context, Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return TextUtils.isEmpty(mimeType) ? MIME_TYPE_JPEG : mimeType;
    }

    /**
     * Determines if the file name is a picture
     *
     * @param fileName
     * @return
     */
    public static boolean isSuffixOfImage(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        return PNG.equalsIgnoreCase(fileName) || JPEG.equalsIgnoreCase(fileName)
                || JPG.equalsIgnoreCase(fileName) || WEBP.equalsIgnoreCase(fileName)
                || GIF.equalsIgnoreCase(fileName) || BMP.equalsIgnoreCase(fileName);
    }

    /**
     * Is it the same type
     *
     * @param oldMimeType 已选的资源类型
     * @param newMimeType 当次选中的资源类型
     * @return
     */
    public static boolean isMimeTypeSame(String oldMimeType, String newMimeType) {
        if (TextUtils.isEmpty(oldMimeType)) {
            return true;
        }
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
                int beginIndex = fileName.lastIndexOf(".");
                String temp = beginIndex == -1 ? "jpeg" : fileName.substring(beginIndex + 1);
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
            return SelectMimeType.TYPE_IMAGE;
        }
        if (mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)) {
            return SelectMimeType.TYPE_VIDEO;
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)) {
            return SelectMimeType.TYPE_AUDIO;
        } else {
            return SelectMimeType.TYPE_IMAGE;
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
            return ctx.getString(R.string.ps_video_error);
        } else if (isHasAudio(mimeType)) {
            return ctx.getString(R.string.ps_audio_error);
        } else {
            return ctx.getString(R.string.ps_error);
        }
    }

    public final static String JPEG = ".jpeg";

    public final static String JPG = ".jpg";

    public final static String PNG = ".png";

    public final static String WEBP = ".webp";

    public final static String GIF = ".gif";

    public final static String BMP = ".bmp";

    public final static String AMR = ".amr";

    public final static String WAV = ".wav";

    public final static String MP3 = ".mp3";

    public final static String MP4 = ".mp4";

    public final static String AVI = ".avi";

    public final static String JPEG_Q = "image/jpeg";

    public final static String PNG_Q = "image/png";

    public final static String MP4_Q = "video/mp4";

    public final static String AVI_Q = "video/avi";

    public final static String AMR_Q = "audio/amr";

    public final static String WAV_Q = "audio/x-wav";

    public final static String MP3_Q = "audio/mpeg";

    public final static String DCIM = "DCIM/Camera";

    public final static String CAMERA = "Camera";

    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_AUDIO = "audio/mpeg";
    public final static String MIME_TYPE_AUDIO_AMR = "audio/amr";

    public final static String MIME_TYPE_PREFIX_IMAGE = "image";
    public final static String MIME_TYPE_PREFIX_VIDEO = "video";
    public final static String MIME_TYPE_PREFIX_AUDIO = "audio";

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

}
