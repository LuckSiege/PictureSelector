package com.luck.picture.lib.loader;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.SortUtils;
import com.luck.picture.lib.utils.ValueOf;

import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;
import java.security.MessageDigest;
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
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     * @param isGif      是否查询gif
     */
    public static LocalMediaFolder loadInAppSandboxFolderFile(Context context, String sandboxDir, boolean isGift) {
        List<LocalMedia> list = loadInAppSandboxFile(context, sandboxDir, isGift);
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
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     * @param isGif      是否查询gif
     */
    public static List<LocalMedia> loadInAppSandboxFile(Context context, String sandboxDir, boolean isGif) {
        if (TextUtils.isEmpty(sandboxDir)) {
            return null;
        }
        List<LocalMedia> list = new ArrayList<>();
        File sandboxFile = new File(sandboxDir);
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
                String mimeType = PictureMimeType.getMimeTypeFromMediaContentUri(context, Uri.fromFile(f));
                if (!isGif) {
                    if (PictureMimeType.isGif(mimeType)) {
                        continue;
                    }
                }
                String absolutePath = f.getAbsolutePath();
                long size = f.length();
                long id = f.lastModified() / 1000;
                String parentFolderName = f.getParentFile() != null ? f.getParentFile().getName() : "";
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(absolutePath.getBytes());
                    id = new BigInteger(1, md.digest()).longValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    chooseModel = SelectMimeType.ofVideo();
                } else {
                    MediaExtraInfo imageSize = MediaUtils.getImageSize(context, absolutePath);
                    width = imageSize.getWidth();
                    height = imageSize.getHeight();
                    chooseModel = SelectMimeType.ofImage();
                    duration = 0L;
                }
                LocalMedia media = LocalMedia.parseLocalMedia(id, absolutePath, absolutePath, f.getName(),
                        parentFolderName, duration, chooseModel, mimeType, width, height, size, bucketId, dateTime);
                media.setSandboxPath(SdkVersionUtils.isQ() ? absolutePath : null);
                list.add(media);
            }
        }
        return list;
    }
}
