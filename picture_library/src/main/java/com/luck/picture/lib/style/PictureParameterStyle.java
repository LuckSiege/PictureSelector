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
     * 相册父容器背景色
     */
    @ColorInt
    public int pictureContainerBackgroundColor;

    /**
     * 相册标题色值
     */
    @ColorInt
    public int pictureTitleTextColor;

    /**
     * 相册标题字体大小
     */
    public int pictureTitleTextSize;

    /**
     * 相册取消按钮色值
     */
    @ColorInt
    @Deprecated
    public int pictureCancelTextColor;

    /**
     * 相册右侧按钮色值
     */
    @ColorInt
    public int pictureRightDefaultTextColor;

    /**
     * 相册右侧文字字体大小
     */
    public int pictureRightTextSize;

    /**
     * 相册右侧按钮文本
     */
    public String pictureRightDefaultText;

    /**
     * 相册右侧按钮色值
     */
    @ColorInt
    public int pictureRightSelectedTextColor;

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
     * 相册列表完成按钮字体大小
     */
    public int pictureCompleteTextSize;

    /**
     * 相册列表不可预览文字颜色
     */
    @ColorInt
    public int pictureUnPreviewTextColor;

    /**
     * 相册列表预览文字大小
     */
    public int picturePreviewTextSize;

    /**
     * 相册列表未完成按钮文本
     */
    public String pictureUnCompleteText;

    /**
     * 相册列表已完成按钮文本
     */
    public String pictureCompleteText;

    /**
     * 相册列表预览文字颜色
     */
    @ColorInt
    public int picturePreviewTextColor;

    /**
     * 相册列表不可预览文字
     */
    public String pictureUnPreviewText;

    /**
     * 相册列表预览文字
     */
    public String picturePreviewText;

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
     * 原图字体颜色
     */
    @ColorInt
    public int pictureOriginalFontColor;

    /**
     * 原图字体大小
     */
    public int pictureOriginalTextSize;

    /**
     * 相册右侧按钮不可点击背景样式
     */
    @DrawableRes
    public int pictureUnCompleteBackgroundStyle;

    /**
     * 相册右侧按钮可点击背景样式
     */
    @DrawableRes
    public int pictureCompleteBackgroundStyle;

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
     * 是否使用(%1$d/%2$d)字符串
     */
    public boolean isCompleteReplaceNum;

    /**
     * WeChatStyle 预览右下角 勾选CheckBox drawable样式
     */
    @DrawableRes
    public int pictureWeChatChooseStyle;

    /**
     * WeChatStyle 预览界面返回键样式
     */
    @DrawableRes
    public int pictureWeChatLeftBackStyle;

    /**
     * WeChatStyle 相册界面标题背景样式
     */
    @DrawableRes
    public int pictureWeChatTitleBackgroundStyle;

    /**
     * WeChatStyle 自定义预览页右下角选择文字大小
     */
    public int pictureWeChatPreviewSelectedTextSize;

    /**
     * WeChatStyle 自定义预览页右下角选择文字文案
     */
    public String pictureWeChatPreviewSelectedText;

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
     * 原图勾选样式
     */
    @DrawableRes
    public int pictureOriginalControlStyle;


    /**
     * 外部预览图片是否显示删除按钮
     */
    public boolean pictureExternalPreviewGonePreviewDelete;


    /**
     * 选择相册目录背景样式
     */
    @DrawableRes
    public int pictureAlbumStyle;


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
        dest.writeInt(this.pictureContainerBackgroundColor);
        dest.writeInt(this.pictureTitleTextColor);
        dest.writeInt(this.pictureTitleTextSize);
        dest.writeInt(this.pictureCancelTextColor);
        dest.writeInt(this.pictureRightDefaultTextColor);
        dest.writeInt(this.pictureRightTextSize);
        dest.writeString(this.pictureRightDefaultText);
        dest.writeInt(this.pictureRightSelectedTextColor);
        dest.writeInt(this.pictureBottomBgColor);
        dest.writeInt(this.pictureCompleteTextColor);
        dest.writeInt(this.pictureUnCompleteTextColor);
        dest.writeInt(this.pictureCompleteTextSize);
        dest.writeInt(this.pictureUnPreviewTextColor);
        dest.writeInt(this.picturePreviewTextSize);
        dest.writeString(this.pictureUnCompleteText);
        dest.writeString(this.pictureCompleteText);
        dest.writeInt(this.picturePreviewTextColor);
        dest.writeString(this.pictureUnPreviewText);
        dest.writeString(this.picturePreviewText);
        dest.writeInt(this.picturePreviewBottomBgColor);
        dest.writeInt(this.pictureNavBarColor);
        dest.writeInt(this.pictureOriginalFontColor);
        dest.writeInt(this.pictureOriginalTextSize);
        dest.writeInt(this.pictureUnCompleteBackgroundStyle);
        dest.writeInt(this.pictureCompleteBackgroundStyle);
        dest.writeInt(this.pictureTitleUpResId);
        dest.writeInt(this.pictureTitleDownResId);
        dest.writeInt(this.pictureLeftBackIcon);
        dest.writeInt(this.pictureCheckedStyle);
        dest.writeByte(this.isCompleteReplaceNum ? (byte) 1 : (byte) 0);
        dest.writeInt(this.pictureWeChatChooseStyle);
        dest.writeInt(this.pictureWeChatLeftBackStyle);
        dest.writeInt(this.pictureWeChatTitleBackgroundStyle);
        dest.writeInt(this.pictureWeChatPreviewSelectedTextSize);
        dest.writeString(this.pictureWeChatPreviewSelectedText);
        dest.writeInt(this.pictureCheckNumBgStyle);
        dest.writeInt(this.pictureFolderCheckedDotStyle);
        dest.writeInt(this.pictureExternalPreviewDeleteStyle);
        dest.writeInt(this.pictureOriginalControlStyle);
        dest.writeByte(this.pictureExternalPreviewGonePreviewDelete ? (byte) 1 : (byte) 0);
        dest.writeInt(this.pictureAlbumStyle);
    }

    protected PictureParameterStyle(Parcel in) {
        this.isChangeStatusBarFontColor = in.readByte() != 0;
        this.isOpenCompletedNumStyle = in.readByte() != 0;
        this.isOpenCheckNumStyle = in.readByte() != 0;
        this.pictureStatusBarColor = in.readInt();
        this.pictureTitleBarBackgroundColor = in.readInt();
        this.pictureContainerBackgroundColor = in.readInt();
        this.pictureTitleTextColor = in.readInt();
        this.pictureTitleTextSize = in.readInt();
        this.pictureCancelTextColor = in.readInt();
        this.pictureRightDefaultTextColor = in.readInt();
        this.pictureRightTextSize = in.readInt();
        this.pictureRightDefaultText = in.readString();
        this.pictureRightSelectedTextColor = in.readInt();
        this.pictureBottomBgColor = in.readInt();
        this.pictureCompleteTextColor = in.readInt();
        this.pictureUnCompleteTextColor = in.readInt();
        this.pictureCompleteTextSize = in.readInt();
        this.pictureUnPreviewTextColor = in.readInt();
        this.picturePreviewTextSize = in.readInt();
        this.pictureUnCompleteText = in.readString();
        this.pictureCompleteText = in.readString();
        this.picturePreviewTextColor = in.readInt();
        this.pictureUnPreviewText = in.readString();
        this.picturePreviewText = in.readString();
        this.picturePreviewBottomBgColor = in.readInt();
        this.pictureNavBarColor = in.readInt();
        this.pictureOriginalFontColor = in.readInt();
        this.pictureOriginalTextSize = in.readInt();
        this.pictureUnCompleteBackgroundStyle = in.readInt();
        this.pictureCompleteBackgroundStyle = in.readInt();
        this.pictureTitleUpResId = in.readInt();
        this.pictureTitleDownResId = in.readInt();
        this.pictureLeftBackIcon = in.readInt();
        this.pictureCheckedStyle = in.readInt();
        this.isCompleteReplaceNum = in.readByte() != 0;
        this.pictureWeChatChooseStyle = in.readInt();
        this.pictureWeChatLeftBackStyle = in.readInt();
        this.pictureWeChatTitleBackgroundStyle = in.readInt();
        this.pictureWeChatPreviewSelectedTextSize = in.readInt();
        this.pictureWeChatPreviewSelectedText = in.readString();
        this.pictureCheckNumBgStyle = in.readInt();
        this.pictureFolderCheckedDotStyle = in.readInt();
        this.pictureExternalPreviewDeleteStyle = in.readInt();
        this.pictureOriginalControlStyle = in.readInt();
        this.pictureExternalPreviewGonePreviewDelete = in.readByte() != 0;
        this.pictureAlbumStyle = in.readInt();
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
