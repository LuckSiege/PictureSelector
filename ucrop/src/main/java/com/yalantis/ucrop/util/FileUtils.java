/*
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yalantis.ucrop.util;

import static com.yalantis.ucrop.util.BitmapLoadUtils.close;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Peli
 * @author paulburke (ipaulpro)
 * @version 2013-12-11
 */
public class FileUtils {

    /**
     * TAG for log messages.
     */
    private static final String TAG = "FileUtils";

    public static final String GIF = ".gif";

    public static final String WEBP = ".webp";

    public static final String JPEG = ".jpeg";

    private FileUtils() {
    }

    /**
     * is content://
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
     * 是否替换Output Uri
     *
     * @param context
     * @param isForbidGifWebp 是否禁止裁剪Gif或webp
     * @param inputUri        裁剪源文件
     * @param outputUri       裁剪输出目录
     * @return
     */
    public static Uri replaceOutputUri(Context context, boolean isForbidGifWebp, Uri inputUri, Uri outputUri) {
        try {
            String postfix = FileUtils.getPostfixDefaultEmpty(context, isForbidGifWebp, inputUri);
            if (TextUtils.isEmpty(postfix)) {
                return outputUri;
            } else {
                String outputPath = FileUtils.isContent(outputUri.toString()) ? outputUri.toString() : outputUri.getPath();
                int lastIndexOf = outputPath.lastIndexOf(".");
                outputPath = outputPath.replace(outputPath.substring(lastIndexOf), postfix);
                outputUri = FileUtils.isContent(outputPath) ? Uri.parse(outputPath) : Uri.fromFile(new File(outputPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputUri;
    }

    /**
     * 生成图片的后缀
     *
     * @param context
     * @param isForbidGifWebp 是否禁止裁剪Gif或Webp
     * @param inputUri        裁剪源文件
     * @return
     */
    public static String getPostfixDefaultJPEG(Context context, boolean isForbidGifWebp, Uri inputUri) {
        String postfix = FileUtils.JPEG;
        if (isForbidGifWebp) {
            String mimeType = FileUtils.getMimeTypeFromMediaContentUri(context, inputUri);
            if (FileUtils.isGif(mimeType)) {
                postfix = FileUtils.GIF;
            } else if (FileUtils.isWebp(mimeType)) {
                postfix = FileUtils.WEBP;
            }
        }
        return postfix;
    }

    /**
     * 生成图片的后缀
     *
     * @param context
     * @param isForbidGifWebp 是否禁止裁剪Gif或Webp
     * @param inputUri        裁剪源
     * @return
     */
    public static String getPostfixDefaultEmpty(Context context, boolean isForbidGifWebp, Uri inputUri) {
        String postfix = "";
        if (isForbidGifWebp) {
            String mimeType = FileUtils.getMimeTypeFromMediaContentUri(context, inputUri);
            if (FileUtils.isGif(mimeType)) {
                postfix = FileUtils.GIF;
            } else if (FileUtils.isWebp(mimeType)) {
                postfix = FileUtils.WEBP;
            }
        }
        return postfix;
    }

    /**
     * 获取裁剪源路径
     *
     * @param inputUri
     * @return
     */
    public static String getInputPath(Uri inputUri) {
        return FileUtils.isContent(inputUri.toString())
                || isHasHttp(inputUri.toString()) ? inputUri.toString() : inputUri.getPath();
    }

    /**
     * isVideo
     *
     * @param url
     * @return
     */
    public static boolean isUrlHasVideo(String url) {
        return !TextUtils.isEmpty(url) && url.toLowerCase().endsWith(".mp4");
    }

    /**
     * isVideo
     *
     * @param mimeType
     * @return
     */
    public static boolean isHasVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video");
    }

    /**
     * isAudio
     *
     * @param mimeType
     * @return
     */
    public static boolean isHasAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith("audio");
    }

    /**
     * is Network image
     *
     * @param path
     * @return
     */
    public static boolean isHasHttp(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.startsWith("http") || path.startsWith("https") || path.startsWith("/http") || path.startsWith("/https");
    }

    /**
     * isGif
     *
     * @param mimeType
     * @return
     */
    public static boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }

    /**
     * isWebp
     *
     * @param mimeType
     * @return
     */
    public static boolean isWebp(String mimeType) {
        return mimeType != null && (mimeType.equals("image/webp") || mimeType.equals("image/WEBP"));
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
        return mimeType;
    }


    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + sf.format(millis);
    }

    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    public static String getCreateFileName() {
        long millis = System.currentTimeMillis();
        return sf.format(millis);
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
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        Log.i(TAG, e.getMessage());
                        return null;
                    }
                }

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
     *
     * @param pathFrom Represents the source file
     * @param pathTo   Represents the destination file
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
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
    }

    /**
     * Copies one file into the other with the given Uris.
     * In the event that the Uris are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     *
     * @param context The context from which to require the {@link android.content.ContentResolver}
     * @param uriFrom Represents the source file
     * @param uriTo   Represents the destination file
     */
    public static void copyFile(@NonNull Context context, @NonNull Uri uriFrom, @NonNull Uri uriTo) throws IOException {
        if (uriFrom.equals(uriTo)) {
            return;
        }

        InputStream isFrom = null;
        OutputStream osTo = null;
        try {
            isFrom = context.getContentResolver().openInputStream(uriFrom);
            osTo = context.getContentResolver().openOutputStream(uriTo);

            if (isFrom instanceof FileInputStream && osTo instanceof FileOutputStream) {
                FileChannel inputChannel = ((FileInputStream) isFrom).getChannel();
                FileChannel outputChannel = ((FileOutputStream) osTo).getChannel();
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            } else {
                throw new IllegalArgumentException("The input or output URI don't represent a file. " +
                        "uCrop requires then to represent files in order to work properly.");
            }
        } finally {
            if (isFrom != null) isFrom.close();
            if (osTo != null) osTo.close();
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
            byte[] data = new byte[1024];
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

}
