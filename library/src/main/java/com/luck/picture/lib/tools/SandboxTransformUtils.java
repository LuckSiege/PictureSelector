package com.luck.picture.lib.tools;

import android.content.Context;
import android.net.Uri;

import com.luck.picture.lib.PictureContentResolver;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：SandboxTransformUtils
 */
public class SandboxTransformUtils {

    /**
     * 把外部目录下的图片拷贝至沙盒内
     *
     * @param ctx
     * @param url
     * @param mineType
     * @param customFileName
     * @return
     */
    public static String copyPathToSandbox(Context ctx, String url, String mineType, String customFileName) {
        try {
            String sandboxPath = PictureFileUtils.createFilePath(ctx, "", mineType, customFileName);
            InputStream inputStream = PictureContentResolver.getContentResolverOpenInputStream(ctx, Uri.parse(url));
            boolean copyFileSuccess = PictureFileUtils.writeFileFromIS(inputStream, new FileOutputStream(sandboxPath));
            if (copyFileSuccess) {
                return sandboxPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
