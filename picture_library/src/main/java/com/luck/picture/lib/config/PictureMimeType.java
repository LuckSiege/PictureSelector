package com.luck.picture.lib.config;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.luck.picture.lib.R;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;

import java.io.File;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 *
 * @author luck
 */

public final class PictureMimeType {
    public static int ofAll() {
        return PictureConfig.TYPE_ALL;
    }

    public static int ofImage() {
        return PictureConfig.TYPE_IMAGE;
    }


    /**
     * Audio correlation is no longer maintained
     * 不再维护音频相关功能，但可以继续使用但会有机型兼容性问题
     */
    @Deprecated
    public static int ofVideo() {
        return PictureConfig.TYPE_VIDEO;
    }

    public static int ofAudio() {
        return PictureConfig.TYPE_AUDIO;
    }

    public static int isPictureType(String pictureType) {
        switch (pictureType) {
            case "video/3gp":
            case "video/3gpp":
            case "video/3gpp2":
            case "video/avi":
            case "video/mp4":
            case "video/quicktime":
            case "video/x-msvideo":
            case "video/x-matroska":
            case "video/mpeg":
            case "video/webm":
            case "video/mp2ts":
                return PictureConfig.TYPE_VIDEO;
            case "audio/mpeg":
            case "audio/x-ms-wma":
            case "audio/x-wav":
            case "audio/amr":
            case "audio/wav":
            case "audio/aac":
            case "audio/mp4":
            case "audio/quicktime":
            case "audio/lamr":
            case "audio/3gpp":
                return PictureConfig.TYPE_AUDIO;
            default:
                return PictureConfig.TYPE_IMAGE;
        }
    }

    /**
     * 是否是gif
     *
     * @param pictureType
     * @return
     */
    public static boolean isGif(String pictureType) {
        switch (pictureType) {
            case "image/gif":
            case "image/GIF":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是视频
     *
     * @param pictureType
     * @return
     */
    public static boolean isVideo(String pictureType) {
        switch (pictureType) {
            case "video/3gp":
            case "video/3gpp":
            case "video/3gpp2":
            case "video/avi":
            case "video/mp4":
            case "video/quicktime":
            case "video/x-msvideo":
            case "video/x-matroska":
            case "video/mpeg":
            case "video/webm":
            case "video/mp2ts":
                return true;
            default:
                return false;
        }
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断文件类型是图片还是视频
     *
     * @param file
     * @return
     */
    public static String fileToType(File file) {
        if (file != null) {
            String name = file.getName();
            if (name.endsWith(".mp4") || name.endsWith(".avi")
                    || name.endsWith(".3gpp") || name.endsWith(".3gp") || name.startsWith(".mov")) {
                return "video/mp4";
            } else if (name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg")
                    || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg")
                    || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG")
                    || name.endsWith(".bmp")) {
                return "image/jpeg";
            } else if (name.endsWith(".mp3") || name.endsWith(".amr")
                    || name.endsWith(".aac") || name.endsWith(".war")
                    || name.endsWith(".flac") || name.endsWith(".lamr")) {
                return "audio/mpeg";
            }
        }
        return "image/jpeg";
    }

    /**
     * is type Equal
     *
     * @param p1
     * @param p2
     * @return
     */
    public static boolean mimeToEqual(String p1, String p2) {
        return isPictureType(p1) == isPictureType(p2);
    }

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
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public static String getVideoMimeType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int last = fileName.lastIndexOf(".") + 1;
                String temp = fileName.substring(last);
                return "video/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "video/mp4";
        }
        return "video/mp4";
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
        return "image/jpeg";
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
        if (mimeType.startsWith("video")) {
            return PictureConfig.TYPE_VIDEO;
        } else if (mimeType.startsWith("audio")) {
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
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }

    /**
     * 获取图片后缀
     *
     * @param mineType
     * @return
     */
    public static String getLastImgSuffix(String mineType) {
        String defaultSuffix = ".png";
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
     * 根据不同的类型，返回不同的错误提示
     *
     * @param mediaMimeType
     * @return
     */
    public static String s(Context context, int mediaMimeType) {
        Context ctx = context.getApplicationContext();
        switch (mediaMimeType) {
            case PictureConfig.TYPE_IMAGE:
                return ctx.getString(R.string.picture_error);
            case PictureConfig.TYPE_VIDEO:
                return ctx.getString(R.string.picture_video_error);
            case PictureConfig.TYPE_AUDIO:
                return ctx.getString(R.string.picture_audio_error);
            default:
                return ctx.getString(R.string.picture_error);
        }
    }

    public final static String JPEG = ".JPEG";

    public final static String PNG = ".png";

    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_AUDIO = "audio/mpeg";
}
