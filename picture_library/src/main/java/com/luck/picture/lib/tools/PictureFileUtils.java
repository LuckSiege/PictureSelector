package com.luck.picture.lib.tools;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author：luck
 * @date：2017-5-30 19:30
 * @describe：PictureFileUtils
 */

public class PictureFileUtils {

    public static final String POSTFIX = ".jpg";
    public static final String POST_VIDEO = ".mp4";
    public static final String POST_AUDIO = ".mp3";


    /**
     * @param context
     * @param type
     * @param format
     * @param outCameraDirectory
     * @return
     */
    @Nullable
    public static File createCameraFile(Context context, int type, String fileName, String format, String outCameraDirectory) {
        return createMediaFile(context, type, fileName, format, outCameraDirectory);
    }

    /**
     * 创建文件
     *
     * @param context
     * @param type
     * @param fileName
     * @param format
     * @param outCameraDirectory
     * @return
     */
    @Nullable
    private static File createMediaFile(Context context, int chooseMode, String fileName, String format, String outCameraDirectory) {
        return createOutFile(context, chooseMode, fileName, format, outCameraDirectory);
    }

    @Nullable
    private static File createOutFile(Context context, int chooseMode, String fileName, String format, String outCameraDirectory) {
        File folderDir = null;
        if (TextUtils.isEmpty(outCameraDirectory)) {
            // 外部没有自定义拍照存储路径使用默认
            String state = Environment.getExternalStorageState();
            File rootDir = state.equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    : getRootDirFile(context, chooseMode);
            if (rootDir != null) {
                if (!rootDir.exists()) {
                    rootDir.mkdirs();
                }
                folderDir = new File(rootDir.getAbsolutePath() + File.separator + PictureMimeType.CAMERA + File.separator);
                if (!folderDir.exists() && folderDir.mkdirs()) {
                }
            }
        } else {
            // 自定义存储路径
            folderDir = new File(outCameraDirectory);
            if (!folderDir.exists()) {
                folderDir.mkdirs();
            }
        }

        if (folderDir == null) {
            throw new NullPointerException("The media output path cannot be null");
        }

        boolean isOutFileNameEmpty = TextUtils.isEmpty(fileName);
        switch (chooseMode) {
            case PictureConfig.TYPE_VIDEO:
                String newFileVideoName = isOutFileNameEmpty ? DateUtils.getCreateFileName("VID_") + POST_VIDEO : fileName;
                return new File(folderDir, newFileVideoName);
            case PictureConfig.TYPE_AUDIO:
                String newFileAudioName = isOutFileNameEmpty ? DateUtils.getCreateFileName("AUD_") + POST_AUDIO : fileName;
                return new File(folderDir, newFileAudioName);
            default:
                String suffix = TextUtils.isEmpty(format) ? POSTFIX : format;
                String newFileImageName = isOutFileNameEmpty ? DateUtils.getCreateFileName("IMG_") + suffix : fileName;
                return new File(folderDir, newFileImageName);
        }
    }

    /**
     * 文件根目录
     *
     * @param context
     * @param type
     * @return
     */
    @Nullable
    private static File getRootDirFile(Context context, int type) {
        switch (type) {
            case PictureConfig.TYPE_VIDEO:
                return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            case PictureConfig.TYPE_AUDIO:
                return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            default:
                return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
    }

    /**
     * TAG for log messages.
     */
    static final String TAG = "PictureFileUtils";

    private PictureFileUtils() {
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context ctx, final Uri uri) {
        Context context = ctx.getApplicationContext();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    public static void copyFile(@NonNull String pathFrom, @NonNull String pathTo) throws IOException {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }

        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(new File(pathFrom)).getChannel();
            outputChannel = new FileOutputStream(new File(pathTo)).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
        } finally {
            if (inputChannel != null) {
                inputChannel.close();
            }
            if (outputChannel != null) {
                outputChannel.close();
            }
        }
    }

    /**
     * 拷贝文件
     *
     * @param outFile
     * @return
     */
    public static boolean bufferCopy(BufferedSource inBuffer, File outFile) {
        BufferedSink outBuffer = null;
        try {
            outBuffer = Okio.buffer(Okio.sink(outFile));
            outBuffer.writeAll(inBuffer);
            outBuffer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inBuffer);
            close(outBuffer);
        }
        return false;
    }

    /**
     * 拷贝文件
     *
     * @param outputStream
     * @return
     */
    public static boolean bufferCopy(BufferedSource inBuffer, OutputStream outputStream) {
        BufferedSink outBuffer = null;
        try {
            outBuffer = Okio.buffer(Okio.sink(outputStream));
            outBuffer.writeAll(inBuffer);
            outBuffer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inBuffer);
            close(outBuffer);
        }
        return false;
    }


    /**
     * 拷贝文件
     *
     * @param inFile
     * @param outPutStream
     * @return
     */
    public static boolean bufferCopy(File inFile, OutputStream outPutStream) {
        BufferedSource inBuffer = null;
        BufferedSink outBuffer = null;
        try {
            inBuffer = Okio.buffer(Okio.source(inFile));
            outBuffer = Okio.buffer(Okio.sink(outPutStream));
            outBuffer.writeAll(inBuffer);
            outBuffer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inBuffer);
            close(outPutStream);
            close(outBuffer);
        }
        return false;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(Context context, String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface;
            if (SdkVersionUtils.checkedAndroid_Q()) {
                ParcelFileDescriptor parcelFileDescriptor =
                        context.getContentResolver()
                                .openFileDescriptor(Uri.parse(path), "r");
                exifInterface = new ExifInterface(parcelFileDescriptor.getFileDescriptor());
            } else {
                exifInterface = new ExifInterface(path);
            }
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * getDCIMCameraPath
     *
     * @return
     */
    public static String getDCIMCameraPath() {
        String absolutePath;
        try {
            absolutePath = "%" + Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return absolutePath;
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param mContext
     * @param type     image or video ...
     */
    public static void deleteCacheDirFile(Context mContext, int type) {
        File cutDir = mContext.getExternalFilesDir(type == PictureMimeType.ofImage()
                ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        if (cutDir != null) {
            File[] files = cutDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * set empty PictureSelector Cache
     *
     * @param context
     * @param type    image、video、audio ...
     */
    public static void deleteAllCacheDirFile(Context context) {

        File dirPictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dirPictures != null) {
            File[] files = dirPictures.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }

        File dirMovies = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (dirMovies != null) {
            File[] files = dirMovies.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }

        File dirMusic = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (dirMusic != null) {
            File[] files = dirMusic.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * @param ctx
     * @return
     */
    public static String getDiskCacheDir(Context ctx) {
        File filesDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (filesDir == null) {
            return "";
        }
        return filesDir.getPath();
    }

    /**
     * @param ctx
     * @return
     */
    public static String getVideoDiskCacheDir(Context ctx) {
        File filesDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (filesDir == null) {
            return "";
        }
        return filesDir.getPath();
    }

    /**
     * @param ctx
     * @return
     */
    public static String getAudioDiskCacheDir(Context ctx) {
        File filesDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (filesDir == null) {
            return "";
        }
        return filesDir.getPath();
    }

    /**
     * 生成uri
     *
     * @param context
     * @param cameraFile
     * @return
     */
    public static Uri parUri(Context context, File cameraFile) {
        Uri imageUri;
        String authority = context.getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(context, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }

    /**
     * 获取图片后缀
     *
     * @param input
     * @return
     */
    public static String extSuffix(InputStream input) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            return options.outMimeType.replace("image/", ".");
        } catch (Exception e) {
            return PictureMimeType.JPEG;
        }
    }

    /**
     * 根据类型创建文件名
     *
     * @param context
     * @param md5
     * @param mineType
     * @param customFileName
     * @return
     */
    public static String createFilePath(Context context, String md5, String mineType, String customFileName) {
        String suffix = PictureMimeType.getLastImgSuffix(mineType);
        if (PictureMimeType.isHasVideo(mineType)) {
            // 视频
            String filesDir = PictureFileUtils.getVideoDiskCacheDir(context) + File.separator;
            if (!TextUtils.isEmpty(md5)) {
                String fileName = TextUtils.isEmpty(customFileName) ? "VID_" + md5.toUpperCase() + suffix : customFileName;
                return filesDir + fileName;
            } else {
                String fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("VID_") + suffix : customFileName;
                return filesDir + fileName;
            }
        } else if (PictureMimeType.isHasAudio(mineType)) {
            // 音频
            String filesDir = PictureFileUtils.getAudioDiskCacheDir(context) + File.separator;
            if (!TextUtils.isEmpty(md5)) {
                String fileName = TextUtils.isEmpty(customFileName) ? "AUD_" + md5.toUpperCase() + suffix : customFileName;
                return filesDir + fileName;
            } else {
                String fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("AUD_") + suffix : customFileName;
                return filesDir + fileName;
            }
        } else {
            // 图片
            String filesDir = PictureFileUtils.getDiskCacheDir(context) + File.separator;
            if (!TextUtils.isEmpty(md5)) {
                String fileName = TextUtils.isEmpty(customFileName) ? "IMG_" + md5.toUpperCase() + suffix : customFileName;
                return filesDir + fileName;
            } else {
                String fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("IMG_") + suffix : customFileName;
                return filesDir + fileName;
            }
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExists(String path) {
        if (!TextUtils.isEmpty(path) && !new File(path).exists()) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static void close(@Nullable Closeable c) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c != null && c instanceof Closeable) {
            try {
                c.close();
            } catch (Exception e) {
                // silence
            }
        }
    }
}
