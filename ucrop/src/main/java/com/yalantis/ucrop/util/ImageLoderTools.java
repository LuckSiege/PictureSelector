package com.yalantis.ucrop.util;

import com.yalantis.ucrop.callback.ImageLoder;

/**
 * 项目名称：PictureSelector
 * 类描述：
 * 创建人：张志华
 * 创建时间：2018/1/18 17:15@郑州卡卡罗特科技有限公司
 * 修改人：Administrator
 * 修改时间：2018/1/18 17:15
 * 修改备注：
 *
 * @version 1
 *          相关联类：
 * @see
 * @see
 */

public class ImageLoderTools {
    public static final String C8="c8";//圆角图片角半径8dp
    public static final String GIF_CALLBACK="gif-callback";//加载GIF图片并回调
    public static final String LONG_CALLBACK="longImg-callback";//加载有可能是长图图片并回调
    public static ImageLoder loder;

    public static ImageLoder getLoder() {
        return loder;
    }

    public static void setLoder(ImageLoder defloder) {
        loder = defloder;
    }
}
