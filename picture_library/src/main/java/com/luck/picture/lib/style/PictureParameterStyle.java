package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

/**
 * @author：luck
 * @date：2019-11-22 17:24
 * @describe：相册动态样式参数设置
 */
public class PictureParameterStyle implements Parcelable {
    /**
     * 是否改变状态栏字体颜色 黑白切换
     */
    public boolean isChangeStatusBarFontColor;
    /**
     * 是否开启 已完成(0/9) 模式
     */
    public boolean isOpenCompletedNumStyle;
    /**
     * 是否开启QQ 数字选择风格
     */
    public boolean isOpenCheckNumStyle;

    /**
     * 状态栏色值
     */
    @ColorInt
    public int pictureStatusBarColor;

    /**
     * 标题栏背景色
     */
    @ColorInt
    public int pictureTitleBarBackgroundColor;

    /**
     * 相册标题色值
     */
    @ColorInt
    public int pictureTitleTextColor;

    /**
     * 相册取消按钮色值
     */
    @ColorInt
    public int pictureCancelTextColor;

    /**
     * 相册列表底部背景色
     */
    @ColorInt
    public int pictureBottomBgColor;

    /**
     * 相册列表已完成按钮色值
     */
    @ColorInt
    public int pictureCompleteTextColor;

    /**
     * 相册列表未完成按钮色值
     */
    @ColorInt
    public int pictureUnCompleteTextColor;

    /**
     * 相册列表不可预览文字颜色
     */
    @ColorInt
    public int pictureUnPreviewTextColor;

    /**
     * 相册列表预览文字颜色
     */
    @ColorInt
    public int picturePreviewTextColor;

    /**
     * 相册列表预览界面底部背景色
     */
    @ColorInt
    public int picturePreviewBottomBgColor;

    /**
     * # SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
     * 相册导航条颜色
     */
    @ColorInt
    public int pictureNavBarColor;

    /**
     * 相册标题右侧箭头
     */
    @DrawableRes
    public int pictureTitleUpResId;
    /**
     * 相册标题右侧箭头
     */
    @DrawableRes
    public int pictureTitleDownResId;

    /**
     * 相册返回图标
     */
    @DrawableRes
    public int pictureLeftBackIcon;

    /**
     * 相册勾选CheckBox drawable样式
     */
    @DrawableRes
    public int pictureCheckedStyle;

    /**
     * 图片已选数量圆点背景色
     */
    @DrawableRes
    public int pictureCheckNumBgStyle;

    /**
     * 相册文件夹列表选中圆点
     */
    @DrawableRes
    public int pictureFolderCheckedDotStyle;

    /**
     * 外部预览图片删除按钮样式
     */
    @DrawableRes
    public int pictureExternalPreviewDeleteStyle;

    /**
     * 外部预览图片是否显示删除按钮
     */
    public boolean pictureExternalPreviewGonePreviewDelete;

    public PictureParameterStyle() {
        super();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isChangeStatusBarFontColor ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOpenCompletedNumStyle ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOpenCheckNumStyle ? (byte) 1 : (byte) 0);
        dest.writeInt(this.pictureStatusBarColor);
        dest.writeInt(this.pictureTitleBarBackgroundColor);
        dest.writeInt(this.pictureTitleTextColor);
        dest.writeInt(this.pictureCancelTextColor);
        dest.writeInt(this.pictureBottomBgColor);
        dest.writeInt(this.pictureCompleteTextColor);
        dest.writeInt(this.pictureUnCompleteTextColor);
        dest.writeInt(this.pictureUnPreviewTextColor);
        dest.writeInt(this.picturePreviewTextColor);
        dest.writeInt(this.picturePreviewBottomBgColor);
        dest.writeInt(this.pictureNavBarColor);
        dest.writeInt(this.pictureTitleUpResId);
        dest.writeInt(this.pictureTitleDownResId);
        dest.writeInt(this.pictureLeftBackIcon);
        dest.writeInt(this.pictureCheckedStyle);
        dest.writeInt(this.pictureCheckNumBgStyle);
        dest.writeInt(this.pictureFolderCheckedDotStyle);
        dest.writeInt(this.pictureExternalPreviewDeleteStyle);
        dest.writeByte(this.pictureExternalPreviewGonePreviewDelete ? (byte) 1 : (byte) 0);
    }

    protected PictureParameterStyle(Parcel in) {
        this.isChangeStatusBarFontColor = in.readByte() != 0;
        this.isOpenCompletedNumStyle = in.readByte() != 0;
        this.isOpenCheckNumStyle = in.readByte() != 0;
        this.pictureStatusBarColor = in.readInt();
        this.pictureTitleBarBackgroundColor = in.readInt();
        this.pictureTitleTextColor = in.readInt();
        this.pictureCancelTextColor = in.readInt();
        this.pictureBottomBgColor = in.readInt();
        this.pictureCompleteTextColor = in.readInt();
        this.pictureUnCompleteTextColor = in.readInt();
        this.pictureUnPreviewTextColor = in.readInt();
        this.picturePreviewTextColor = in.readInt();
        this.picturePreviewBottomBgColor = in.readInt();
        this.pictureNavBarColor = in.readInt();
        this.pictureTitleUpResId = in.readInt();
        this.pictureTitleDownResId = in.readInt();
        this.pictureLeftBackIcon = in.readInt();
        this.pictureCheckedStyle = in.readInt();
        this.pictureCheckNumBgStyle = in.readInt();
        this.pictureFolderCheckedDotStyle = in.readInt();
        this.pictureExternalPreviewDeleteStyle = in.readInt();
        this.pictureExternalPreviewGonePreviewDelete = in.readByte() != 0;
    }

    public static final Creator<PictureParameterStyle> CREATOR = new Creator<PictureParameterStyle>() {
        @Override
        public PictureParameterStyle createFromParcel(Parcel source) {
            return new PictureParameterStyle(source);
        }

        @Override
        public PictureParameterStyle[] newArray(int size) {
            return new PictureParameterStyle[size];
        }
    };
}
