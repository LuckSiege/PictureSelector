package com.luck.picture.lib.compress;

import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：16/12/31
 */

public interface CompressInterface {
    void compress();

    /**
     * 压缩结果监听器
     */
    interface CompressListener {
        /**
         * 压缩成功
         * @param images 已经压缩图片
         */
        void onCompressSuccess(List<LocalMedia> images);

        /**
         * 压缩失败
         * @param images 压缩失败的图片
         * @param msg 失败的原因
         */
        void onCompressError(List<LocalMedia> images, String msg);
    }
}
