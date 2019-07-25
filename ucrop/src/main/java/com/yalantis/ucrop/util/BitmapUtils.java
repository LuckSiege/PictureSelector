package com.yalantis.ucrop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author：luck
 * @date：2019-07-18 11:14
 * @describe：Bitmap处理类
 */
public class BitmapUtils {
    /**
     * 获取Bitmap
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把Bitmap写入到sdcard中
     *
     * @param bitmap
     * @param savePath
     */
    public static void saveBitmap(Bitmap bitmap, String savePath) {
        if (bitmap == null) {
            return;
        }
        // 判断是否可以对SdCard进行操作
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //目录转化成文件夹
            File dirFile = new File(FileUtils.getDirName(savePath));
            if (!dirFile.exists()) {
                //如果不存在，那就建立这个文件夹
                dirFile.mkdirs();
            }
            //文件夹有啦，就可以保存图片啦
            // 在SdCard的目录下创建图片文,以当前时间为其命名
            File file = new File(savePath);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
