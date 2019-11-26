package com.luck.picture.lib.tools;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
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
import androidx.core.content.FileProvider;

import com.luck.picture.lib.compress.InputStreamProvider;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;

/**
 * @author：luck
 * @date：2017-5-30 19:30
 * @describe：PictureFileUtils
 */

public class PictureFileUtils {

    public static final String POSTFIX = ".JPEG";
    public static final String POST_VIDEO = ".mp4";
    public static final String POST_AUDIO = ".mp3";
    public static final String APP_NAME = "PictureSelector";
    public static final String CAMERA_PATH_IMAGE = "/" + APP_NAME + "/CameraImage/";
    public static final String CAMERA_PATH_VIDEO = "/" + APP_NAME + "/CameraVideo/";
    public static final String CAMERA_PATH_AUDIO = "/" + APP_NAME + "/CameraAudio/";

    /**
     * @param context
     * @param type
     * @param format
     * @return
     */
    public static File createCameraFile(Context context, int type, String fileName, String format) {
        return createMediaFile(context, type, fileName, format);
    }

    /**
     * 创建文件
     *
     * @param context
     * @param type
     * @param fileName
     * @param format
     * @return
     */
    private static File createMediaFile(Context context, int type, String fileName, String format) {
        File rootDir;
        if (SdkVersionUtils.checkedAndroid_Q()) {
            rootDir = getRootDirFile(context, type);
        } else {
            String state = Environment.getExternalStorageState();
            rootDir = state.equals(Environment.MEDIA_MOUNTED) ?
                    Environment.getExternalStorageDirectory() : context.getCacheDir();
        }
        if (rootDir != null && !rootDir.exists() && rootDir.mkdirs()) {
        }
        String parentPath = getParentPath(type);
        File folderDir = new File(SdkVersionUtils.checkedAndroid_Q()
                ? rootDir.getAbsolutePath() : rootDir.getAbsolutePath() + parentPath);
        if (folderDir != null && !folderDir.exists() && folderDir.mkdirs()) {
        }
        fileName = TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName;
        File tmpFile;
        String suffixType;
        switch (type) {
            case PictureConfig.TYPE_VIDEO:
                tmpFile = new File(folderDir, fileName + POST_VIDEO);
                break;
            case PictureConfig.TYPE_AUDIO:
                tmpFile = new File(folderDir, fileName + POST_AUDIO);
                break;
            default:
                suffixType = TextUtils.isEmpty(format) ? POSTFIX : format;
                tmpFile = new File(folderDir, fileName + suffixType);
                break;
        }
        return tmpFile;
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
     * 内存卡目录下的媒体文件目录
     *
     * @param type
     * @return
     */
    private static String getParentPath(int type) {
        switch (type) {
            case PictureConfig.TYPE_VIDEO:
                return CAMERA_PATH_VIDEO;
            case PictureConfig.TYPE_AUDIO:
                return CAMERA_PATH_AUDIO;
            default:
                return CAMERA_PATH_IMAGE;
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
    public static String getPath(final Context context, final Uri uri) {
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
     * 旋转Bitmap
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotatingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static void saveBitmapFile(Bitmap bitmap, File file) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹
     *
     * @param filename
     * @return
     */
    public static String createDir(Context context, String filename) {
        File rootDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!rootDir.exists())
        // 若不存在，创建目录，可以在应用启动的时候创建
        {
            rootDir.mkdirs();
        }
        return rootDir + "/" + filename;
    }


    public static String getDCIMCameraPath(Context ctx) {
        String absolutePath;
        try {
            if (SdkVersionUtils.checkedAndroid_Q()) {
                absolutePath = "%" + ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/Camera";
            } else {
                absolutePath = "%" + Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
            }
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
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * @param ctx
     * @return
     */
    public static String getDiskCacheDir(Context ctx) {
        return ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
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
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param file
     */
    public static void rotateImage(int degree, String path) {
        if (degree > 0) {
            try {
                // 针对相片有旋转问题的处理方式
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                File file = new File(path);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Bitmap bmp = PictureFileUtils.rotatingImageView(degree, bitmap);
                if (bmp != null) {
                    PictureFileUtils.saveBitmapFile(bmp, file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param path
     */
    public static String rotateImageToAndroidQ(Context context, int degree, String path) {
        if (degree > 0) {
            try {
                // 针对相片有旋转问题的处理方式
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 2;
                    ParcelFileDescriptor parcelFileDescriptor =
                            context.getContentResolver()
                                    .openFileDescriptor(Uri.parse(path), "r");
                    FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                    Bitmap bitmap = BitmapFactory
                            .decodeStream(inputStream, null, opts);
                    String suffix = PictureFileUtils.extSuffix(inputStream);
                    Bitmap bmp = PictureFileUtils.rotatingImageView(degree, bitmap);
                    if (bmp != null) {
                        String dir = createDir(context, System.currentTimeMillis() + suffix);
                        PictureFileUtils.saveBitmapFile(bmp, new File(dir));
                        return dir;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
