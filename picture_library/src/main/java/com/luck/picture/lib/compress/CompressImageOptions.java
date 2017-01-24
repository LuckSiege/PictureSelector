package com.luck.picture.lib.compress;

import android.content.Context;
import android.text.TextUtils;

import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：16/12/31
 */
public class CompressImageOptions implements CompressInterface {
    private CompressImageUtil compressImageUtil;
    private List<LocalMedia> images;
    private CompressInterface.CompressListener listener;

    public static CompressInterface compress(Context context, CompressConfig config, List<LocalMedia> images, CompressInterface.CompressListener listener) {
        if (config.getLubanOptions() != null) {
            return new LuBanCompress(context,config,images,listener);
        } else {
            return new CompressImageOptions(context, config, images, listener);
        }
    }

    private CompressImageOptions(Context context, CompressConfig config, List<LocalMedia> images, CompressInterface.CompressListener listener) {
        compressImageUtil = new CompressImageUtil(context, config);
        this.images = images;
        this.listener = listener;
    }

    @Override
    public void compress() {
        if (images == null || images.isEmpty())
            listener.onCompressError(images, " images is null");
        for (LocalMedia image : images) {
            if (image == null) {
                listener.onCompressError(images, " There are pictures of compress  is null.");
                return;
            }
        }
        compress(images.get(0));
    }

    private void compress(final LocalMedia image) {
        String path = "";
        if (image.isCut()) {
            path = image.getCutPath();
        } else {
            path = image.getPath();
        }
        if (TextUtils.isEmpty(path)) {
            continueCompress(image, false);
            return;
        }

        File file = new File(path);
        if (file == null || !file.exists() || !file.isFile()) {
            continueCompress(image, false);
            return;
        }

        compressImageUtil.compress(path, new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                image.setCompressPath(imgPath);
                continueCompress(image, true);
            }

            @Override
            public void onCompressFailed(String imgPath, String msg) {
                continueCompress(image, false, msg);
            }
        });
    }

    private void continueCompress(LocalMedia image, boolean preSuccess, String... message) {
        image.setCompressed(preSuccess);
        int index = images.indexOf(image);
        boolean isLast = index == images.size() - 1;
        if (isLast) {
            handleCompressCallBack(message);
        } else {
            compress(images.get(index + 1));
        }
    }

    private void handleCompressCallBack(String... message) {
        if (message.length > 0) {
            listener.onCompressError(images, message[0]);
            return;
        }

        for (LocalMedia image : images) {
            if (!image.isCompressed()) {
                listener.onCompressError(images, image.getCompressPath() + " is compress failures");
                return;
            }
        }
        listener.onCompressSuccess(images);
    }
}
