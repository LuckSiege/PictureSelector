package com.yalantis.ucrop.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * @author：luck
 * @date：2020-01-08 20:11
 * @describe：MimeType
 */
public class MimeType {

    /**
     * 是否是图片
     *
     * @param mimeType
     * @return
     */
    public final static String MIME_TYPE_PREFIX_IMAGE = "image";

    public static boolean isHasImage(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE);
    }


    public static boolean isGifForSuffix(String suffix) {
        return suffix != null && suffix.startsWith(".gif") || suffix.startsWith(".GIF");
    }

    /**
     * 是否是视频
     *
     * @param mimeType
     * @return
     */
    public final static String MIME_TYPE_PREFIX_VIDEO = "video";

    public static boolean isHasVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO);
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
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public static boolean isHttp(String path) {
        return !TextUtils.isEmpty(path) && path.startsWith("http") || path.startsWith("https");
    }

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
     * 是否是content://类型
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
}
