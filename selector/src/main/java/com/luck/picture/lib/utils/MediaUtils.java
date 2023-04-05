package com.luck.picture.lib.utils;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.basic.PictureContentResolver;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.thread.PictureThreadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;


/**
 * @author：luck
 * @date：2019-10-21 17:10
 * @describe：资源处理工具类
 */
public class MediaUtils {
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
     * 获取mimeType
     *
     * @param path
     * @return
     */
    public static String getMimeTypeFromMediaUrl(String path) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(path);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase());
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = getMimeType(new File(path));
        }
        return TextUtils.isEmpty(mimeType) ? PictureMimeType.MIME_TYPE_JPEG : mimeType;
    }

    /**
     * 获取mimeType
     *
     * @param url
     * @return
     */
    public static String getMimeTypeFromMediaHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (url.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (url.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (url.toLowerCase().endsWith(".webp")) {
            return "image/webp";
        } else if (url.toLowerCase().endsWith(".bmp")) {
            return "image/bmp";
        } else if (url.toLowerCase().endsWith(".mp4")) {
            return "video/mp4";
        } else if (url.toLowerCase().endsWith(".avi")) {
            return "video/avi";
        } else if (url.toLowerCase().endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (url.toLowerCase().endsWith(".amr")) {
            return "audio/amr";
        } else if (url.toLowerCase().endsWith(".m4a")) {
            return "audio/mpeg";
        }
        return null;
    }

    /**
     * 获取mimeType
     *
     * @param file
     * @return
     */
    private static String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(file.getName());
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
     * 创建目录名
     *
     * @param absolutePath 资源路径
     * @return
     */
    public static String generateCameraFolderName(String absolutePath) {
        String folderName;
        File cameraFile = new File(absolutePath);
        if (cameraFile.getParentFile() != null) {
            folderName = cameraFile.getParentFile().getName();
        } else {
            folderName = PictureMimeType.CAMERA;
        }
        return folderName;
    }

    /**
     * get Local image width or height
     * <p>
     * Use {@link MediaUtils.getImageSize(Context context, String url)}
     *
     * @param url
     * @return
     */
    @Deprecated
    public static MediaExtraInfo getImageSize(String url) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (PictureMimeType.isContent(url)) {
                inputStream = PictureContentResolver.openInputStream(PictureAppMaster.getInstance().getAppContext(), Uri.parse(url));
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
     * get Local image width or height
     *
     * @param url
     * @return
     */
    public static MediaExtraInfo getImageSize(Context context, String url) {
        MediaExtraInfo mediaExtraInfo = new MediaExtraInfo();
        if (PictureMimeType.isHasHttp(url)) {
            return mediaExtraInfo;
        }
        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (PictureMimeType.isContent(url)) {
                inputStream = PictureContentResolver.openInputStream(context, Uri.parse(url));
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
     * get Local image width or height
     *
     * @param context
     * @param url
     * @param call
     */
    public static void getImageSize(Context context, String url, OnCallbackListener<MediaExtraInfo> call) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaExtraInfo>() {

            @Override
            public MediaExtraInfo doInBackground() {
                return getImageSize(context, url);
            }

            @Override
            public void onSuccess(MediaExtraInfo result) {
                PictureThreadUtils.cancel(this);
                if (call != null) {
                    call.onCall(result);
                }
            }
        });
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    public static void getVideoSize(Context context, String url, OnCallbackListener<MediaExtraInfo> call) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaExtraInfo>() {

            @Override
            public MediaExtraInfo doInBackground() {
                return getVideoSize(context, url);
            }

            @Override
            public void onSuccess(MediaExtraInfo result) {
                PictureThreadUtils.cancel(this);
                if (call != null) {
                    call.onCall(result);
                }
            }
        });
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
        if (PictureMimeType.isHasHttp(url)) {
            return mediaExtraInfo;
        }
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
            mediaExtraInfo.setOrientation(orientation);
            mediaExtraInfo.setDuration(ValueOf.toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (PictureMimeType.isHasHttp(url)) {
            return mediaExtraInfo;
        }
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
            try {
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public static int getDCIMLastImageId(Context context,String absoluteDir) {
        Cursor data = null;
        try {
            //selection: 指定查询条件
            String selection = MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {"%" + absoluteDir + "%"};
            if (SdkVersionUtils.isR()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(selection, selectionArgs, 1, 0,MediaStore.Files.FileColumns._ID + " DESC");
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = context.getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, orderBy);
            }
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                int id = data.getInt(data.getColumnIndex(MediaStore.Images.Media._ID));
                long date = data.getLong(data.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int duration = DateUtils.dateDiffer(date);
                // 最近时间1s以内的图片，可以判定是最新生成的重复照片
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
     * getPathMediaBucketId
     *
     * @return
     */
    public static Long[] getPathMediaBucketId(Context context, String absolutePath) {
        Long[] mediaBucketId = new Long[]{0L, 0L};
        Cursor data = null;
        try {
            //selection: 指定查询条件
            String selection = MediaStore.Files.FileColumns.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {"%" + absolutePath + "%"};
            if (SdkVersionUtils.isR()) {
                Bundle queryArgs = MediaUtils.createQueryArgsBundle(selection, selectionArgs, 1, 0, MediaStore.Files.FileColumns._ID + " DESC");
                data = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), null, queryArgs, null);
            } else {
                String orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0";
                data = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, selectionArgs, orderBy);
            }
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                mediaBucketId[0] = data.getLong(data.getColumnIndex(MediaStore.Files.FileColumns._ID));
                mediaBucketId[1] = data.getLong(data.getColumnIndex("bucket_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                data.close();
            }
        }
        return mediaBucketId;
    }


    /**
     * Key for an SQL style {@code LIMIT} string that may be present in the
     * query Bundle argument passed to
     * {@link ContentProvider#query(Uri, String[], Bundle, CancellationSignal)}.
     *
     * <p><b>Apps targeting {@link android.os.Build.VERSION_CODES#O} or higher are strongly
     * encourage to use structured query arguments in lieu of opaque SQL query clauses.</b>
     *
     * @see #QUERY_ARG_LIMIT
     * @see #QUERY_ARG_OFFSET
     */
    public static final String QUERY_ARG_SQL_LIMIT = "android:query-arg-sql-limit";

    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    public static Bundle createQueryArgsBundle(String selection, String[] selectionArgs, int limitCount, int offset, String orderBy) {
        Bundle queryArgs = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, orderBy);
            if (SdkVersionUtils.isR()) {
                queryArgs.putString(ContentResolver.QUERY_ARG_SQL_LIMIT, limitCount + " offset " + offset);
            }
        }
        return queryArgs;
    }

    /**
     * 异步获取视频缩略图地址
     *
     * @param context
     * @param url
     * @param call
     * @return
     */
    public static void getAsyncVideoThumbnail(Context context, String url, OnCallbackListener<MediaExtraInfo> call) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaExtraInfo>() {

            @Override
            public MediaExtraInfo doInBackground() {
                return getVideoThumbnail(context, url);
            }

            @Override
            public void onSuccess(MediaExtraInfo result) {
                PictureThreadUtils.cancel(this);
                if (call != null) {
                    call.onCall(result);
                }
            }
        });
    }

    /**
     * 获取视频缩略图地址
     *
     * @param context
     * @param url
     * @return
     */
    public static MediaExtraInfo getVideoThumbnail(Context context, String url) {
        Bitmap bitmap = null;
        ByteArrayOutputStream stream = null;
        FileOutputStream fos = null;
        MediaExtraInfo extraInfo = new MediaExtraInfo();
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            if (PictureMimeType.isContent(url)) {
                mmr.setDataSource(context, Uri.parse(url));
            } else {
                mmr.setDataSource(url);
            }
            bitmap = mmr.getFrameAtTime();
            if (bitmap != null && !bitmap.isRecycled()) {
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                String videoThumbnailDir = PictureFileUtils.getVideoThumbnailDir(context);
                File targetFile = new File(videoThumbnailDir, DateUtils.getCreateFileName("vid_") + "_thumb.jpg");
                fos = new FileOutputStream(targetFile);
                fos.write(stream.toByteArray());
                fos.flush();
                extraInfo.setVideoThumbnail(targetFile.getAbsolutePath());
                extraInfo.setWidth(bitmap.getWidth());
                extraInfo.setHeight(bitmap.getHeight());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PictureFileUtils.close(stream);
            PictureFileUtils.close(fos);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        return extraInfo;
    }

    /**
     * delete camera PATH
     *
     * @param context Context
     * @param path    path
     */
    public static void deleteUri(Context context, String path) {
        try {
            if (!TextUtils.isEmpty(path) && PictureMimeType.isContent(path)) {
                context.getContentResolver().delete(Uri.parse(path), null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
