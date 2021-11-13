package com.luck.picture.lib.model;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.SortUtils;
import com.luck.picture.lib.tools.ValueOf;

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
    public static LocalMediaFolder loadInAppSandboxFolderFile(Context context, String directoryPath) {
        List<LocalMedia> list = loadInAppSandboxFile(context, directoryPath);
        LocalMediaFolder folder = null;
        if (list != null && list.size() > 0) {
            SortUtils.sortLocalMediaAddedTime(list);
            LocalMedia firstMedia = list.get(0);
            folder = new LocalMediaFolder();
            folder.setName(firstMedia.getParentFolderName());
            folder.setFirstImagePath(firstMedia.getPath());
            folder.setFirstMimeType(firstMedia.getMimeType());
            folder.setBucketId(firstMedia.getBucketId());
            folder.setImageNum(list.size());
            folder.setData(list);
        }
        return folder;
    }

    /**
     * 查询应用内部目录的图片
     *
     * @param context       上下文
     * @param directoryPath 资源目标路径
     */
    public static List<LocalMedia> loadInAppSandboxFile(Context context, String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return null;
        }
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
                long bucketId = ValueOf.toLong(parentFolderName.hashCode());
                long dateTime = f.lastModified() / 1000;
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
                        parentFolderName, duration, chooseModel, mimeType, width, height, size, bucketId, dateTime);
                media.setAndroidQToPath(SdkVersionUtils.isQ() ? absolutePath : null);
                list.add(media);
            }
        }
        return list;
    }
}
