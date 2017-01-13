package com.yalantis.ucrop.compress;

import android.content.Context;
import android.text.TextUtils;

import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.util.List;

/**
 * 压缩照片
 * <p/>
 * Date: 2017/01/05
 * GitHub:https://github.com/LuckSiege
 * Email:893855882@qq.com
 */
public class CompressImageOptions implements CompressInterface {
    private CompressImageUtil compressImageUtil;
    private List<LocalMedia> images;
    private CompressInterface.CompressListener listener;

    public static CompressImageOptions compress(Context context, CompressConfig config, List<LocalMedia> images, CompressInterface.CompressListener listener) {
        return new CompressImageOptions(context, config, images, listener);
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

    private void compress(final LocalMedia compress) {
        String path = compress.getPath();
        if (TextUtils.isEmpty(path)) {
            continueCompress(compress, false);
            return;
        }

        File file = new File(compress.getPath());
        if (file == null || !file.exists() || !file.isFile()) {
            continueCompress(compress, false);
            return;
        }

        compressImageUtil.compress(compress.getPath(), new CompressImageUtil.CompressListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                compress.setCompressPath(imgPath);
                continueCompress(compress, true);
            }

            @Override
            public void onCompressError(String imgPath, String msg) {
                continueCompress(compress, false, msg);
            }
        });
    }

    private void continueCompress(LocalMedia compress, boolean preSuccess, String... message) {
        compress.setCompressed(preSuccess);
        int index = images.indexOf(compress);
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

        for (LocalMedia compress : images) {
            if (!compress.isCompressed()) {
                listener.onCompressError(images, compress.getCompressPath() + " is compress failures");
                return;
            }
        }
        listener.onCompressSuccess(images);
    }
}
