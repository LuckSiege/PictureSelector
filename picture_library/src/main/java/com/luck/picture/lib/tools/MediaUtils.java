package com.luck.picture.lib.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;


import androidx.annotation.RequiresApi;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;

/**
 * @author：luck
 * @date：2019-10-21 17:10
 * @describe：资源处理工具类
 */
public class MediaUtils {

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @return 图片的uri
     */
    public static Uri createImagePathUri(final Context context, String fileName) {
        final Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = String.valueOf(System.currentTimeMillis());
        String imageName = TextUtils.isEmpty(fileName) ? time : fileName;
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, PictureMimeType.MIME_TYPE_IMAGE);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Files.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Files.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }


    /**
     * 创建一条视频地址uri,用于保存录制的视频
     *
     * @param context
     * @return 视频的uri
     */
    public static Uri createImageVideoUri(final Context context, String fileName) {
        final Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = String.valueOf(System.currentTimeMillis());
        String imageName = TextUtils.isEmpty(fileName) ? time : fileName;
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, PictureMimeType.MIME_TYPE_VIDEO);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Files.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Files.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }

    /**
     * 获取视频时长
     *
     * @param context
     * @param isAndroidQ
     * @param path
     * @return
     */
    public static long extractDuration(Context context, boolean isAndroidQ, String path) {
        return isAndroidQ ? getLocalDuration(context, Uri.parse(path))
                : getLocalDuration(path);
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(LocalMedia media) {
        if (null != media) {
            int width = media.getWidth();
            int height = media.getHeight();
            int h = width * 3;
            return height > h;
        }
        return false;
    }

    /**
     * get Local video duration
     *
     * @return
     */
    private static long getLocalDuration(Context context, Uri uri) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            return Long.parseLong(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get Local video duration
     *
     * @return
     */
    private static long getLocalDuration(String path) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            return Long.parseLong(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get Local video width or height for api 29
     *
     * @return
     */
    public static int[] getLocalVideoWidthOrHeightToAndroidQ(Context context, String videoPath) {
        int[] wh = new int[2];
        try {
            Cursor query = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                query = context.getApplicationContext().getContentResolver().query(Uri.parse(videoPath),
                        null, null, null);
            }
            if (query != null) {
                query.moveToFirst();
                wh[0] = query.getInt(query.getColumnIndexOrThrow(MediaStore.Video
                        .Media.WIDTH));
                wh[1] = query.getInt(query.getColumnIndexOrThrow(MediaStore.Video
                        .Media.HEIGHT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wh;
    }

    /**
     * get Local video width or height
     *
     * @return
     */
    public static int[] getLocalVideoWidthOrHeight(String videoPath) {
        int[] wh = new int[2];
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            wh[0] = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            wh[1] = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wh;
    }
}
