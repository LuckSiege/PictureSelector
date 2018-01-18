package com.yalantis.ucrop.callback;

import android.content.Context;

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

public interface ImageLoder<T> {
    void displayImage(Context context, Object path, T imageView);
    void displayImage(Context context, Object path, T imageView,int def);
    void displayImage(Context context, Object path, T imageView,int def,int error);
    void displayImage(Context context, Object path, T imageView,String type);
    void displayImage(Context context, Object path, T imageView,String type,BitmapLoadCallback callback);
    T createImageView(Context context);
}
