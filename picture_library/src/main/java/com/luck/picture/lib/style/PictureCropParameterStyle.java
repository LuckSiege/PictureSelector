package com.luck.picture.lib.style;


import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;

/**
 * @author：luck
 * @date：2019-11-22 17:24
 * @describe：裁剪动态样式参数设置 <p>
 * {@link PictureSelectorUIStyle}
 * </>
 */
public class PictureCropParameterStyle implements Parcelable {
    /**
     * 是否改变状态栏字体颜色 黑白切换
     */
    public boolean isChangeStatusBarFontColor;

    /**
     * 裁剪页标题背景颜色
     */
    @ColorInt
    public int cropTitleBarBackgroundColor;
    /**
     * 裁剪页状态栏颜色
     */
    @ColorInt
    public int cropStatusBarColorPrimaryDark;
    /**
     * 裁剪页标题栏字体颜色
     */
    @ColorInt
    public int cropTitleColor;

    /**
     * # SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
     * 裁剪导航条颜色
     */
    @ColorInt
    public int cropNavBarColor;


    public PictureCropParameterStyle() {
        super();
    }

    public PictureCropParameterStyle(int cropTitleBarBackgroundColor,
                                     int cropStatusBarColorPrimaryDark,
                                     int cropTitleColor,
                                     boolean isChangeStatusBarFontColor) {
        this.cropTitleBarBackgroundColor = cropTitleBarBackgroundColor;
        this.cropStatusBarColorPrimaryDark = cropStatusBarColorPrimaryDark;
        this.cropTitleColor = cropTitleColor;
        this.isChangeStatusBarFontColor = isChangeStatusBarFontColor;
    }

    public PictureCropParameterStyle(int cropTitleBarBackgroundColor,
                                     int cropStatusBarColorPrimaryDark,
                                     int cropNavBarColor,
                                     int cropTitleColor,
                                     boolean isChangeStatusBarFontColor) {
        this.cropTitleBarBackgroundColor = cropTitleBarBackgroundColor;
        this.cropNavBarColor = cropNavBarColor;
        this.cropStatusBarColorPrimaryDark = cropStatusBarColorPrimaryDark;
        this.cropTitleColor = cropTitleColor;
        this.isChangeStatusBarFontColor = isChangeStatusBarFontColor;
    }

    /**
     * default crop style
     *
     * @return
     */
    public static PictureCropParameterStyle ofDefaultCropStyle() {
        return new PictureCropParameterStyle(
                Color.parseColor("#393a3e"),
                Color.parseColor("#393a3e"),
                Color.parseColor("#393a3e"),
                Color.parseColor("#FFFFFF"),
                false);
    }

    /**
     * 0/N style
     *
     * @return
     */
    public static PictureCropParameterStyle ofSelectTotalStyle() {
        return new PictureCropParameterStyle(
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#000000"),
                true);
    }

    /**
     * number style
     *
     * @return
     */
    public static PictureCropParameterStyle ofSelectNumberStyle() {
        return new PictureCropParameterStyle(
                Color.parseColor("#7D7DFF"),
                Color.parseColor("#7D7DFF"),
                Color.parseColor("#FFFFFF"),
                false);
    }

    /**
     * new style WeChat
     *
     * @return
     */
    public static PictureCropParameterStyle ofNewStyle() {
        return new PictureCropParameterStyle(
                Color.parseColor("#393a3e"),
                Color.parseColor("#393a3e"),
                Color.parseColor("#393a3e"),
                Color.parseColor("#FFFFFF"),
                false);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isChangeStatusBarFontColor ? (byte) 1 : (byte) 0);
        dest.writeInt(this.cropTitleBarBackgroundColor);
        dest.writeInt(this.cropStatusBarColorPrimaryDark);
        dest.writeInt(this.cropTitleColor);
        dest.writeInt(this.cropNavBarColor);
    }

    protected PictureCropParameterStyle(Parcel in) {
        this.isChangeStatusBarFontColor = in.readByte() != 0;
        this.cropTitleBarBackgroundColor = in.readInt();
        this.cropStatusBarColorPrimaryDark = in.readInt();
        this.cropTitleColor = in.readInt();
        this.cropNavBarColor = in.readInt();
    }

    public static final Parcelable.Creator<PictureCropParameterStyle> CREATOR = new Parcelable.Creator<PictureCropParameterStyle>() {
        @Override
        public PictureCropParameterStyle createFromParcel(Parcel source) {
            return new PictureCropParameterStyle(source);
        }

        @Override
        public PictureCropParameterStyle[] newArray(int size) {
            return new PictureCropParameterStyle[size];
        }
    };
}
