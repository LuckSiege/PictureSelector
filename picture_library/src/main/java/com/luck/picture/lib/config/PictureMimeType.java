package com.luck.picture.lib.config;


import android.content.Context;
import android.net.Uri;
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
     * 是否是gif
     *
     * @param path
     * @return
     */
    public static boolean isImageGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            int lastIndex = path.lastIndexOf(".");
            String pictureType = path.substring(lastIndex);
            return pictureType.startsWith(".gif")
                    || pictureType.startsWith(".GIF");
        }
        return false;
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

    public static String createImageType(String path) {
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

    public static String createVideoType(Context context, String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                boolean androidQ = SdkVersionUtils.checkedAndroid_Q();
                File file;
                if (androidQ) {
                    String newPath = PictureFileUtils.getPath(context.getApplicationContext(),
                            Uri.parse(path));
                    file = new File(newPath);
                } else {
                    file = new File(path);
                }
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
     * Picture or video
     *
     * @return
     */
    public static int pictureToVideo(String pictureType) {
        if (!TextUtils.isEmpty(pictureType)) {
            if (pictureType.startsWith("video")) {
                return PictureConfig.TYPE_VIDEO;
            } else if (pictureType.startsWith("audio")) {
                return PictureConfig.TYPE_AUDIO;
            }
        }
        return PictureConfig.TYPE_IMAGE;
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
}
