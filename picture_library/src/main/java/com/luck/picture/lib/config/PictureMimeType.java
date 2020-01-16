package com.luck.picture.lib.config;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.luck.picture.lib.R;

import java.io.File;

/**
 * @author：luck
 * @date：2017-5-24 17:02
 * @describe：图片列表
 */

public final class PictureMimeType {
    public static final int ofAll() {
        return PictureConfig.TYPE_ALL;
    }

    public static final int ofImage() {
        return PictureConfig.TYPE_IMAGE;
    }

    public static final int ofVideo() {
        return PictureConfig.TYPE_VIDEO;
    }

    /**
     * # No longer maintain audio related functions,
     * but can continue to use but there will be phone compatibility issues
     * <p>
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    @Deprecated
    public static final int ofAudio() {
        return PictureConfig.TYPE_AUDIO;
    }


    public static final String ofPNG() {
        return MIME_TYPE_PNG;
    }

    public static final String ofJPEG() {
        return MIME_TYPE_JPEG;
    }

    public static final String ofBMP() {
        return MIME_TYPE_BMP;
    }

    public static final String ofGIF() {
        return MIME_TYPE_GIF;
    }

    public static final String ofWEBP() {
        return MIME_TYPE_WEBP;
    }

    public static final String of3GP() {
        return MIME_TYPE_3GP;
    }

    public static final String ofMP4() {
        return MIME_TYPE_MP4;
    }

    public static final String ofMPEG() {
        return MIME_TYPE_MPEG;
    }

    public static final String ofAVI() {
        return MIME_TYPE_AVI;
    }

    public final static String MIME_TYPE_PNG = "image/png";
    public final static String MIME_TYPE_JPEG = "image/jpeg";
    public final static String MIME_TYPE_JPG = "image/jpg";
    public final static String MIME_TYPE_BMP = "image/bmp";
    public final static String MIME_TYPE_GIF = "image/gif";
    public final static String MIME_TYPE_WEBP = "image/webp";

    public final static String MIME_TYPE_3GP = "video/3gp";
    public final static String MIME_TYPE_MP4 = "video/mp4";
    public final static String MIME_TYPE_MPEG = "video/mpeg";
    public final static String MIME_TYPE_AVI = "video/avi";


    /**
     * 是否是gif
     *
     * @param mimeType
     * @return
     */
    public static boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }


    /**
     * 是否是视频
     *
     * @param mimeType
     * @return
     */
    public static boolean eqVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO);
    }

    /**
     * 是否是音频
     *
     * @param mimeType
     * @return
     */
    public static boolean eqAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO);
    }

    /**
     * 是否是图片
     *
     * @param mimeType
     * @return
     */
    public static boolean eqImage(String mimeType) {
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
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.startsWith("http")
                || path.startsWith("https")
                || path.startsWith("/http")
                || path.startsWith("/https");
    }

    /**
     * 判断文件类型是图片还是视频
     *
     * @param file
     * @return
     */
    public static String getMimeType(File file) {
        if (file != null) {
            String name = file.getName();
            if (name.endsWith(".mp4") || name.endsWith(".avi")
                    || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.endsWith(".mov")) {
                return MIME_TYPE_VIDEO;
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                    || name.endsWith(".bmp")) {
                return MIME_TYPE_IMAGE;
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac") || name.endsWith(".lamr")) {
                return MIME_TYPE_AUDIO;
            }
        }
        return MIME_TYPE_IMAGE;
    }

    /**
     * 判断文件名是否是图片
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
     * 是否是同一类型
     *
     * @param oldMimeType
     * @param newMimeType
     * @return
     */
    public static boolean isMimeTypeSame(String oldMimeType, String newMimeType) {

        return getMimeType(oldMimeType) == getMimeType(newMimeType);
    }

    /**
     * 获取图片mimeType
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
     * 根据uri获取MIME_TYPE
     *
     * @param uri
     * @return
     */
    public static String getMimeType(Context context, Uri uri) {
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = context.getApplicationContext().getContentResolver().query(uri,
                    new String[]{MediaStore.Files.FileColumns.MIME_TYPE}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
                    if (columnIndex > -1) {
                        return cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
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
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastImgType(String path) {
        try {
            int index = path.lastIndexOf(".");
            if (index > 0) {
                String imageType = path.substring(index);
                switch (imageType) {
                    case ".png":
                    case ".PNG":
                    case ".jpg":
                    case ".jpeg":
                    case ".JPEG":
                    case ".WEBP":
                    case ".bmp":
                    case ".BMP":
                    case ".webp":
                    case ".gif":
                    case ".GIF":
                        return imageType;
                    default:
                        return PNG;
                }
            } else {
                return PNG;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return PNG;
        }
    }

    /**
     * 获取图片后缀
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
        return TextUtils.isEmpty(mimeType) ? "image/jpeg" : mimeType;
    }

    /**
     * 根据不同的类型，返回不同的错误提示
     *
     * @param mimeType
     * @return
     */
    public static String s(Context context, String mimeType) {
        Context ctx = context.getApplicationContext();
        if (eqVideo(mimeType)) {
            return ctx.getString(R.string.picture_video_error);
        } else if (eqAudio(mimeType)) {
            return ctx.getString(R.string.picture_audio_error);
        } else {
            return ctx.getString(R.string.picture_error);
        }
    }

    public final static String JPEG = ".jpg";

    public final static String PNG = ".png";

    public final static String MP4 = ".mp4";

    public final static String DCIM = "DCIM/Camera";
    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_AUDIO = "audio/mpeg";


    public final static String MIME_TYPE_PREFIX_IMAGE = "image";
    public final static String MIME_TYPE_PREFIX_VIDEO = "video";
    public final static String MIME_TYPE_PREFIX_AUDIO = "audio";

}
