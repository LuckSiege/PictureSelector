package com.luck.picture.lib.tools;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.manager.PictureCacheManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;
import java.util.Objects;

/**
 * @author：luck
 * @date：2017-5-30 19:30
 * @describe：PictureFileUtils
 */

public class PictureFileUtils {
    private static final int BYTE_SIZE = 8192;
    public static final String POSTFIX = ".jpeg";
    public static final String POST_VIDEO = ".mp4";
    public static final String POST_AUDIO = ".amr";


    /**
     * @param context
     * @param chooseMode
     * @param format
     * @param outCameraDirectory
     * @return
     */
    public static File createCameraFile(Context context, int chooseMode, String fileName, String format, String outCameraDirectory) {
        return createMediaFile(context, chooseMode, fileName, format, outCameraDirectory);
    }

    /**
     * 创建文件
     *
     * @param context
     * @param chooseMode
     * @param fileName
     * @param format
     * @param outCameraDirectory
     * @return
     */
    private static File createMediaFile(Context context, int chooseMode, String fileName, String format, String outCameraDirectory) {
        return createOutFile(context, chooseMode, fileName, format, outCameraDirectory);
    }

    /**
     * 创建文件
     *
     * @param ctx                上下文
     * @param chooseMode         选择模式
     * @param fileName           文件名
     * @param format             文件格式
     * @param outCameraDirectory 输出目录
     * @return
     */
    private static File createOutFile(Context ctx, int chooseMode, String fileName, String format, String outCameraDirectory) {
        Context context = ctx.getApplicationContext();
        File folderDir;
        if (TextUtils.isEmpty(outCameraDirectory)) {
            // 外部没有自定义拍照存储路径使用默认
            File rootDir;
            if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
                rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                folderDir = new File(rootDir.getAbsolutePath() + File.separator + PictureMimeType.CAMERA + File.separator);
            } else {
                rootDir = getRootDirFile(context, chooseMode);
                folderDir = new File(rootDir.getAbsolutePath() + File.separator);
            }
            if (!rootDir.exists()) {
                rootDir.mkdirs();
            }
        } else {
            // 自定义存储路径
            folderDir = new File(outCameraDirectory);
            if (!Objects.requireNonNull(folderDir.getParentFile()).exists()) {
                folderDir.getParentFile().mkdirs();
            }
        }
        if (!folderDir.exists()) {
            folderDir.mkdirs();
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
        return "";
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
                        Uri.parse("content://downloads/public_downloads"), ValueOf.toLong(id));

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

        return "";
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    public static void copyFile(@NonNull String pathFrom, @NonNull String pathTo) {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(pathFrom).getChannel();
            outputChannel = new FileOutputStream(pathTo).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputChannel);
            close(outputChannel);
        }
    }


    /**
     * 复制文件
     *
     * @param is 文件输入流
     * @param os 文件输出流
     * @return
     */
    public static boolean writeFileFromIS(final InputStream is, final OutputStream os) {
        OutputStream osBuffer = null;
        BufferedInputStream isBuffer = null;
        try {
            isBuffer = new BufferedInputStream(is);
            osBuffer = new BufferedOutputStream(os);
            byte[] data = new byte[BYTE_SIZE];
            for (int len; (len = isBuffer.read(data)) != -1; ) {
                os.write(data, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(isBuffer);
            close(osBuffer);
        }
    }


    /**
     * 重命名相册拍照
     *
     * @param fileName
     * @return
     */
    public static String rename(String fileName) {
        String temp = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        return temp + "_" + DateUtils.getCreateFileName() + suffix;
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
     * Use {@link PictureCacheManager}
     *
     * @param mContext
     * @param type     image or video ...
     */
    @Deprecated
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
     * Use {@link PictureCacheManager}
     *
     * @param context
     * @param type    image、video、audio ...
     */
    @Deprecated
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
        String authority = context.getPackageName() + ".luckProvider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(context, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
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
            if (TextUtils.isEmpty(md5)) {
                String fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("VID_") + suffix : customFileName;
                return filesDir + fileName;
            } else {
                String fileName = TextUtils.isEmpty(customFileName) ? "VID_" + md5.toUpperCase() + suffix : customFileName;
                return filesDir + fileName;
            }
        } else if (PictureMimeType.isHasAudio(mineType)) {
            // 音频
            String filesDir = PictureFileUtils.getAudioDiskCacheDir(context) + File.separator;
            if (TextUtils.isEmpty(md5)) {
                String fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("AUD_") + suffix : customFileName;
                return filesDir + fileName;
            } else {
                String fileName = TextUtils.isEmpty(customFileName) ? "AUD_" + md5.toUpperCase() + suffix : customFileName;
                return filesDir + fileName;
            }
        } else {
            // 图片
            String filesDir = PictureFileUtils.getDiskCacheDir(context) + File.separator;
            if (TextUtils.isEmpty(md5)) {
                String fileName = TextUtils.isEmpty(customFileName) ? DateUtils.getCreateFileName("IMG_") + suffix : customFileName;
                return filesDir + fileName;
            } else {
                String fileName = TextUtils.isEmpty(customFileName) ? "IMG_" + md5.toUpperCase() + suffix : customFileName;
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
        return TextUtils.isEmpty(path) || new File(path).exists();
    }

    public static final int KB = 1024;
    public static final int MB = 1048576;
    public static final int GB = 1073741824;

    /**
     * Size of byte to fit size of memory.
     * <p>to three decimal places</p>
     *
     * @param byteSize  Size of byte.
     * @param precision The precision
     * @return fit size of memory
     */
    @SuppressLint("DefaultLocale")
    public static String formatFileSize(final long byteSize, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision shouldn't be less than zero!");
        }
        if (byteSize < 0) {
            throw new IllegalArgumentException("byteSize shouldn't be less than zero!");
        } else if (byteSize < KB) {
            return String.format("%." + precision + "fB", (double) byteSize);
        } else if (byteSize < MB) {
            return String.format("%." + precision + "fKB", (double) byteSize / KB);
        } else if (byteSize < GB) {
            return String.format("%." + precision + "fMB", (double) byteSize / MB);
        } else {
            return String.format("%." + precision + "fGB", (double) byteSize / GB);
        }
    }


    @SuppressWarnings("ConstantConditions")
    public static void close(@Nullable Closeable c) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c instanceof Closeable) {
            try {
                c.close();
            } catch (Exception e) {
                // silence
            }
        }
    }
}
