package com.luck.picture.lib.tools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnCallbackListener;
import com.luck.picture.lib.thread.PictureThreadUtils;

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
     * @param context
     * @param suffixType
     * @return 图片的uri
     */
    @Nullable
    public static Uri createImageUri(final Context context, String suffixType) {
        final Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"));
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, TextUtils.isEmpty(suffixType) ? PictureMimeType.MIME_TYPE_IMAGE : suffixType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM);
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
     * @param context
     * @param suffixType
     * @return 视频的uri
     */
    @Nullable
    public static Uri createVideoUri(final Context context, String suffixType) {
        final Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = ValueOf.toString(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"));
        values.put(MediaStore.Video.Media.DATE_TAKEN, time);
        values.put(MediaStore.Video.Media.MIME_TYPE, TextUtils.isEmpty(suffixType) ? PictureMimeType.MIME_TYPE_VIDEO : suffixType);
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.getContentUri("external"), values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Video.Media.getContentUri("internal"), values);
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
            int newHeight = width * 3;
            return height > newHeight;
        }
        return false;
    }

    /**
     * 是否是长图
     *
     * @param width  宽
     * @param height 高
     * @return true 是 or false 不是
     */
    public static boolean isLongImg(int width, int height) {
        int newHeight = width * 3;
        return height > newHeight;
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
     * get Local image width or height for api 29
     *
     * @return
     */
    public static int[] getImageSizeForUrlToAndroidQ(Context context, String url) {
        int[] size = new int[2];
        Cursor query = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                query = context.getApplicationContext().getContentResolver()
                        .query(Uri.parse(url),
                                null, null, null);
                if (query != null) {
                    query.moveToFirst();
                    size[0] = query.getInt(query.getColumnIndexOrThrow(MediaStore.Images
                            .Media.WIDTH));
                    size[1] = query.getInt(query.getColumnIndexOrThrow(MediaStore.Images
                            .Media.HEIGHT));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (query != null) {
                query.close();
            }
        }
        return size;
    }

    /**
     * get Local video width or height
     *
     * @return
     */
    public static int[] getVideoSizeForUrl(String url) {
        int[] size = new int[2];
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(url);
            size[0] = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            size[1] = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * get Local video width or height
     *
     * @return
     */
    public static int[] getVideoSizeForUri(Context context, Uri uri) {
        int[] size = new int[2];
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            size[0] = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            size[1] = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * get Local image width or height
     *
     * @return
     */
    public static int[] getImageSizeForUrl(String url) {
        int[] size = new int[2];
        try {
            ExifInterface exifInterface = new ExifInterface(url);
            // 获取图片的宽度
            int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);
            // 获取图片的高度
            int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);
            size[0] = width;
            size[1] = height;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * get Local image width or height
     *
     * @return
     */
    public static int[] getImageSizeForUri(Context context, Uri uri) {
        int[] size = new int[2];
        ParcelFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            if (fileDescriptor != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                size[0] = options.outWidth;
                size[1] = options.outHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(fileDescriptor);
        }
        return size;
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
            String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
            String selection = MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    selection, selectionArgs, orderBy);
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
            String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
            data = context.getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null,
                    selection, selectionArgs, orderBy);
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
        Cursor cursor = null;
        try {
            cursor = context.getApplicationContext().getContentResolver()
                    .query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
                path = cursor.getString(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }


    /**
     * 获取旋转角度
     *
     * @param path
     * @return
     */
    public static int getVideoOrientationForUrl(String path) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            int rotation = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            switch (rotation) {
                case 90:
                    return ExifInterface.ORIENTATION_ROTATE_90;
                case 270:
                    return ExifInterface.ORIENTATION_ROTATE_270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取旋转角度
     *
     * @param uri
     * @return
     */
    public static int getVideoOrientationForUri(Context context, Uri uri) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            int orientation = ValueOf.toInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            switch (orientation) {
                case 90:
                    return ExifInterface.ORIENTATION_ROTATE_90;
                case 270:
                    return ExifInterface.ORIENTATION_ROTATE_270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取旋转角度
     *
     * @param context
     * @param url
     * @return
     */
    public static int getImageOrientationForUrl(Context context, String url) {
        ExifInterface exifInterface = null;
        InputStream inputStream = null;
        try {
            if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(url)) {
                inputStream = context.getContentResolver().openInputStream(Uri.parse(url));
                if (inputStream != null) {
                    exifInterface = new ExifInterface(inputStream);
                }
            } else {
                exifInterface = new ExifInterface(url);
            }
            return exifInterface != null ? exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            PictureFileUtils.close(inputStream);
        }
    }

    /**
     * 设置LocalMedia旋转信息
     *
     * @param context
     * @param media
     * @param isAndroidQChangeWH
     * @param listener
     * @return
     */
    public static void setOrientationAsynchronous(Context context, LocalMedia media,
                                                  boolean isAndroidQChangeWH,
                                                  boolean isAndroidQChangeVideoWH,
                                                  OnCallbackListener<LocalMedia> listener) {
        if (PictureMimeType.isHasImage(media.getMimeType())) {
            if (!isAndroidQChangeWH) {
                return;
            }
        }

        if (PictureMimeType.isHasVideo(media.getMimeType())) {
            if (!isAndroidQChangeVideoWH) {
                return;
            }
        }

        if (media.getOrientation() != -1) {
            if (listener != null) {
                listener.onCall(media);
            }
            return;
        }
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<Integer>() {

            @Override
            public Integer doInBackground() {
                int orientation = 0;
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    orientation = MediaUtils.getImageOrientationForUrl(context, media.getPath());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    if (PictureMimeType.isContent(media.getPath())) {
                        orientation = MediaUtils.getVideoOrientationForUri(context, Uri.parse(media.getPath()));
                    } else {
                        orientation = MediaUtils.getVideoOrientationForUrl(media.getPath());
                    }
                }
                return orientation;
            }

            @Override
            public void onSuccess(Integer orientation) {
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                        || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    int width = media.getWidth();
                    int height = media.getHeight();
                    media.setWidth(height);
                    media.setHeight(width);
                }
                media.setOrientation(orientation);
                if (listener != null) {
                    listener.onCall(media);
                }
            }
        });
    }

    /**
     * 设置LocalMedia旋转信息
     *
     * @param context
     * @param media
     * @param isAndroidQChangeWH
     * @return
     */
    public static void setOrientationSynchronous(Context context, LocalMedia media,
                                                 boolean isAndroidQChangeWH,
                                                 boolean isAndroidQChangeVideoWH) {
        if (PictureMimeType.isHasImage(media.getMimeType())) {
            if (!isAndroidQChangeWH) {
                return;
            }
        }
        if (PictureMimeType.isHasVideo(media.getMimeType())) {
            if (!isAndroidQChangeVideoWH) {
                return;
            }
        }
        // 如果有旋转信息图片宽高则是相反
        int orientation = 0;
        if (PictureMimeType.isHasImage(media.getMimeType())) {
            orientation = MediaUtils.getImageOrientationForUrl(context, media.getPath());
        } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
            if (PictureMimeType.isContent(media.getPath())) {
                orientation = MediaUtils.getVideoOrientationForUri(context, Uri.parse(media.getPath()));
            } else {
                orientation = MediaUtils.getVideoOrientationForUrl(media.getPath());
            }
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            int width = media.getWidth();
            int height = media.getHeight();
            media.setWidth(height);
            media.setHeight(width);
        }
        media.setOrientation(orientation);
    }
}
