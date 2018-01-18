package com.yalantis.ucrop.util;

import android.content.Context;
import android.widget.ImageView;

import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.callback.ImageLoder;

/**
 * 项目名称：PictureSelector
 * 类描述：
 * 创建人：张志华
 * 创建时间：2018/1/18 17:28@郑州卡卡罗特科技有限公司
 * 修改人：Administrator
 * 修改时间：2018/1/18 17:28
 * 修改备注：
 *
 * @version 1
 *          相关联类：
 * @see
 * @see
 */

public class DefImageLoder implements ImageLoder<ImageView>{
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {

    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, int def) {

    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, int def, int error) {

    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, String type) {

    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView, String type, BitmapLoadCallback callback) {

    }

    @Override
    public ImageView createImageView(Context context) {
        return null;
    }
}
