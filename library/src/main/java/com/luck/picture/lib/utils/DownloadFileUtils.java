package com.luck.picture.lib.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import com.luck.picture.lib.basic.PictureContentResolver;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.thread.PictureThreadUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author：luck
 * @date：2021/11/25 2:02 下午
 * @describe：DownloadFileUtils
 */
public class DownloadFileUtils {

    /**
     * 保存文件
     *
     * @param context  上下文
     * @param path     文件路径
     * @param fileName 文件名
     * @param mimeType 文件类型
     * @param listener 结果回调监听
     */
    public static void saveLocalFile(Context context, String path, String fileName, String mimeType,
                                     OnCallbackListener<String> listener) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<String>() {

            @Override
            public String doInBackground() throws Throwable {
                ContentValues contentValues;
                Uri uri;
                if (PictureMimeType.isHasVideo(mimeType)) {
                    contentValues = MediaStoreUtils
                            .buildVideoContentValues(fileName, mimeType);
                    uri = context.getContentResolver()
                            .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                } else {
                    contentValues = MediaStoreUtils
                            .buildImageContentValues(fileName, mimeType);
                    uri = context.getContentResolver()
                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                }
                if (uri != null) {
                    InputStream inputStream;
                    if (PictureMimeType.isHasHttp(path)) {
                        inputStream = new URL(path).openStream();
                    } else {
                        if (PictureMimeType.isContent(path)) {
                            inputStream = PictureContentResolver.getContentResolverOpenInputStream(context, Uri.parse(path));
                        } else {
                            inputStream = new FileInputStream(path);
                        }
                    }
                    OutputStream outputStream = PictureContentResolver.getContentResolverOpenOutputStream(context, uri);
                    boolean bufferCopy = PictureFileUtils.writeFileFromIS(inputStream, outputStream);
                    if (bufferCopy) {
                        return PictureFileUtils.getPath(context, uri);
                    }
                }
                return null;
            }

            @Override
            public void onSuccess(String result) {
                PictureThreadUtils.cancel(this);
                if (listener != null) {
                    listener.onCall(result);
                }
            }
        });

    }
}
