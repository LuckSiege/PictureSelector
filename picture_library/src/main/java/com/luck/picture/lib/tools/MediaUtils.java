package com.luck.picture.lib.tools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import com.luck.picture.lib.PictureContentResolver;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;

import java.io.FileInputStream;
import java.io.InputStream;


/**
 * @author：luck
 * @date：2019-10-21 17:10
 * @describe：资源处理工具类
 */
public class MediaUtils {
    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param ctx
     * @param cameraFileName
     * @param suffixType
     * @return 图片的uri
     */
    public static Uri createImageUri(final Context ctx, String cameraFileName, String mimeType) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
            } else {
                String suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."));
                String fileName = cameraFileName.replaceAll(suffix, "");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        }
        values.put(MediaStore.Images.Media.MIME_TYPE, TextUtils.isEmpty(mimeType) || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) ? PictureMimeType.MIME_TYPE_IMAGE : mimeType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM);
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }


    /**
     * 创建一条视频地址uri,用于保存录制的视频
     *
     * @param ctx
     * @param cameraFileName
     * @param suffixType
     * @return 视频的uri
     */
    public static Uri createVideoUri(final Context ctx, String cameraFileName, String mimeType) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
            } else {
                String suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."));
                String fileName = cameraFileName.replaceAll(suffix, "");
                values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time);
        }
        values.put(MediaStore.Video.Media.MIME_TYPE, TextUtils.isEmpty(mimeType) || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) ? PictureMimeType.MIME_TYPE_VIDEO : mimeType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }


    /**
     * 创建一条音频地址uri,用于保存录制的音频
     *
     * @param ctx
     * @param suffixType
     * @return 音频的uri
     */
    public static Uri createAudioUri(final Context ctx, String mimeType) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, DateUtils.getCreateFileName("AUD_"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Audio.Media.DATE_TAKEN, time);
        }
        values.put(MediaStore.Video.Media.MIME_TYPE, TextUtils.isEmpty(mimeType) || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) ? PictureMimeType.MIME_TYPE_AUDIO_AMR : mimeType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Audio.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Audio.Media.getContentUri("internal"), values);
        }
        return imageFilePath[0];
    }

    /**
     * 是否是长图
     *
     * @param media
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(LocalMedia media) {
        int width = media.getWidth();
        int height = media.getHeight();
        if (width <= 0 || height <= 0) {
            return false;
        }
        int newHeight = width * 3;
        return height > newHeight;
    }

    /**
     * 是否是长图
     *
     * @param width  宽
     * @param height 高
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        int newHeight = width * 3;
        return height > newHeight;
    }


    /**
     * get Local image width or height
     *
     * @param context
     * @param url
     * @return
     */
    public static MediaExtraInfo getImageSize(Context context, String url) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        ExifInterface exifInterface;
        InputStream inputStream = null;
        try {
            if (PictureMimeType.isContent(url)) {
                inputStream = PictureContentResolver.getContentResolverOpenInputStream(context, Uri.parse(url));
                exifInterface = new ExifInterface(inputStream);
            } else {
                exifInterface = new ExifInterface(url);
            }
            mediaExtraInfo.setWidth(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL));
            mediaExtraInfo.setHeight(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(inputStream);
        }
        return mediaExtraInfo;
    }

    /**
     * get Local image width or height
     *
     * @param url
     * @return
     */
    public static MediaExtraInfo getImageSize(String url) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (PictureMimeType.isContent(url)) {
                inputStream = PictureContentResolver.getContentResolverOpenInputStream(PictureAppMaster.getInstance().getAppContext(), Uri.parse(url));
            } else {
                inputStream = new FileInputStream(url);
            }
            BitmapFactory.decodeStream(inputStream, null, options);
            mediaExtraInfo.setWidth(options.outWidth);
            mediaExtraInfo.setHeight(options.outHeight);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(inputStream);
        }
        return mediaExtraInfo;
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    public static MediaExtraInfo getVideoSize(Context context, String url) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (PictureMimeType.isContent(url)) {
                retriever.setDataSource(context, Uri.parse(url));
            } else {
                retriever.setDataSource(url);
            }
            String orientation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            int width, height;
            if (TextUtils.equals("90", orientation) || TextUtils.equals("270", orientation)) {
                height = ValueOf.toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                width = ValueOf.toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            } else {
                width = ValueOf.toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = ValueOf.toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }
            mediaExtraInfo.setWidth(width);
            mediaExtraInfo.setHeight(height);
            mediaExtraInfo.setDuration(ValueOf.toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return mediaExtraInfo;
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    public static MediaExtraInfo getAudioSize(Context context, String url) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (PictureMimeType.isContent(url)) {
                retriever.setDataSource(context, Uri.parse(url));
            } else {
                retriever.setDataSource(url);
            }
            mediaExtraInfo.setDuration(ValueOf.toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return mediaExtraInfo;
    }

    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     */
    public static void removeMedia(Context context, int id) {
        try {
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Images.Media._ID + "=?";
            cr.delete(uri, selection, new String[]{Long.toString(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    public static int getDCIMLastImageId(Context context) {
        Cursor data = null;
        try {
            //selection: 指定查询条件
            String absolutePath = PictureFileUtils.getDCIMCameraPath();
            String selection = MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            if (SdkVersionUtils.checkedAndroid_R()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(selection, selectionArgs, 1, 0);
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, orderBy);
            }
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                int id = data.getInt(data.getColumnIndex(MediaStore.Images.Media._ID));
                long date = data.getLong(data.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int duration = DateUtils.dateDiffer(date);
                // DCIM文件下最近时间1s以内的图片，可以判定是最新生成的重复照片
                return duration <= 1 ? id : -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (data != null) {
                data.close();
            }
        }
    }

    /**
     * 获取Camera文件下最新一条拍照记录
     *
     * @return
     */
    public static long getCameraFirstBucketId(Context context) {
        Cursor data = null;
        try {
            String absolutePath = PictureFileUtils.getDCIMCameraPath();
            //selection: 指定查询条件
            String selection = MediaStore.Files.FileColumns.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            if (SdkVersionUtils.checkedAndroid_R()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(selection, selectionArgs, 1, 0);
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, selectionArgs, orderBy);
            }
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                return data.getLong(data.getColumnIndex("bucket_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                data.close();
            }
        }
        return -1;
    }


    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    @Nullable
    public static String getAudioFilePathFromUri(Context context, Uri uri) {
        String path = "";
        try (Cursor cursor = context.getApplicationContext().getContentResolver()
                .query(uri, null, null, null, null)) {
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                path = cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    public static Bundle createQueryArgsBundle(String selection, String[] selectionArgs, int limitCount, int offset) {
        Bundle queryArgs = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, MediaStore.Files.FileColumns._ID + " DESC");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                queryArgs.putString(ContentResolver.QUERY_ARG_SQL_LIMIT, limitCount + " offset " + offset);
            }
        }
        return queryArgs;
    }

    /**
     * delete camera PATH
     *
     * @param context    Context
     * @param cameraPath Camera url
     */
    public static void deleteCamera(Context context, String cameraPath) {
        try {
            if (PictureMimeType.isContent(cameraPath)) {
                context.getContentResolver().delete(Uri.parse(cameraPath), null, null);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
