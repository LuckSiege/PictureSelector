package com.luck.picture.lib.model;

import android.content.Context;
import android.net.Uri;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.tools.MediaUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/10 5:40 下午
 * @describe：SandboxFileLoader
 */
public final class SandboxFileLoader {
    /**
     * 查询应用内部目录的图片
     *
     * @param context       上下文
     * @param directoryPath 资源目标路径
     */
    public static List<LocalMedia> loadSandboxFile(Context context, String directoryPath) {
        List<LocalMedia> list = new ArrayList<>();
        File sandboxFile = new File(directoryPath);
        if (sandboxFile.exists()) {
            File[] files = sandboxFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });
            if (files == null) {
                return list;
            }
            for (File f : files) {
                String absolutePath = f.getAbsolutePath();
                long size = f.length();
                String mimeType = PictureMimeType.getMimeTypeFromMediaContentUri(context, Uri.fromFile(f));
                String parentFolderName = f.getParentFile() != null ? f.getParentFile().getName() : "";
                long dateTime = f.lastModified();
                long duration;
                int width, height;
                int chooseModel;
                if (PictureMimeType.isHasVideo(mimeType)) {
                    MediaExtraInfo videoSize = MediaUtils.getVideoSize(context, absolutePath);
                    width = videoSize.getWidth();
                    height = videoSize.getHeight();
                    duration = videoSize.getDuration();
                    chooseModel = PictureMimeType.ofVideo();
                } else {
                    MediaExtraInfo imageSize = MediaUtils.getImageSize(context, absolutePath);
                    width = imageSize.getWidth();
                    height = imageSize.getHeight();
                    chooseModel = PictureMimeType.ofImage();
                    duration = 0L;
                }
                LocalMedia media = LocalMedia.parseLocalMedia(dateTime, absolutePath, absolutePath, f.getName(),
                        parentFolderName, duration, chooseModel, mimeType, width, height, size, dateTime, dateTime);
                media.setAndroidQToPath(absolutePath);
                list.add(media);
            }
        }
        return list;
    }
}
