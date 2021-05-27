package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.PictureContentResolver;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import okio.BufferedSource;
import okio.Okio;

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
        // 走普通的文件复制流程，拷贝至应用沙盒内来
        BufferedSource inBuffer = null;
        try {
            String encryptionValue = StringUtils.getEncryptionValue(id, width, height);
            String newPath = PictureFileUtils.createFilePath(ctx, encryptionValue, mineType, customFileName);
            File outFile = new File(newPath);
            if (outFile.exists()) {
                return newPath;
            }
            InputStream inputStream = PictureContentResolver.getContentResolverOpenInputStream(ctx, Uri.parse(url));
            inBuffer = Okio.buffer(Okio.source(Objects.requireNonNull(inputStream)));
            boolean copyFileSuccess = PictureFileUtils.bufferCopy(inBuffer, outFile);
            if (copyFileSuccess) {
                return newPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inBuffer != null && inBuffer.isOpen()) {
                PictureFileUtils.close(inBuffer);
            }
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
            OutputStream fileOutputStream = PictureContentResolver.getContentResolverOpenOutputStream(context, outUri);
            return PictureFileUtils.bufferCopy(inFile, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
