package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.PictureContentResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：Android Q相关处理类
 */
public class AndroidQTransformUtils {


    /**
     * 解析Android Q版本下图片
     * #耗时操作需要放在子线程中操作
     *
     * @param ctx
     * @param id
     * @param url
     * @param width
     * @param height
     * @param mineType
     * @param customFileName
     * @return
     */
    public static String copyPathToAndroidQ(Context ctx, long id, String url, int width, int height, String mineType, String customFileName) {
        try {
            String encryptionValue = StringUtils.getEncryptionValue(id, width, height);
            String newPath = PictureFileUtils.createFilePath(ctx, encryptionValue, mineType, customFileName);
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            InputStream inputStream = PictureContentResolver.getContentResolverOpenInputStream(ctx, Uri.parse(url));
            boolean copyFileSuccess = PictureFileUtils.writeFileFromIS(inputStream, new FileOutputStream(outFile));
            if (copyFileSuccess) {
                return newPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 复制文件至AndroidQ手机相册目录
     *
     * @param context
     * @param inFile
     * @param outUri
     */
    public static boolean copyPathToDCIM(Context context, File inFile, Uri outUri) {
        try {
            OutputStream os = PictureContentResolver.getContentResolverOpenOutputStream(context, outUri);
            return PictureFileUtils.writeFileFromIS(new FileInputStream(inFile), os);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
