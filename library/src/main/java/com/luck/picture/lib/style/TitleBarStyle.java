package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author：luck
 * @date：2021/11/15 4:15 下午
 * @describe：titleBarStyle
 */
public class TitleBarStyle implements Parcelable {
    /**
     * 标题栏左边关闭样式
     */
    private int titleLeftBackRes;
    /**
     * 标题栏默认文案
     */
    private String titleDefaultText;
    /**
     * 标题栏字体大小
     */
    private int titleTextSize;
    /**
     * 标题栏字体色值
     */
    private int titleTextColor;
    /**
     * 标题栏背景
     */
    private int titleBackgroundColor;

    /**
     * 标题栏高度
     */
    private int titleBarHeight;

    /**
     * 标题栏专辑背景
     */
    private int titleAlbumBackgroundRes;

    /**
     * 标题栏位置居左
     */
    private boolean isTitleGravityLeft;
    /**
     * 标题栏右边向上图标
     */
    private int titleDrawableRightRes;

    /**
     * 标题栏右边背景
     */
    private int titleRightBackgroundRes;
    /**
     * 标题栏右边默认文本
     */
    private String titleRightNormalText;
    /**
     * 标题栏右边可选文本
     */
    private String titleRightSelectText;
    /**
     * 标题栏右边文本字体大小
     */
    private int titleRightTextSize;
    /**
     * 标题栏右边文本字体色值
     */
    private int titleRightTextColor;

    /**
     * 状态栏背景色
     */
    private int statusBarColor;

    /**
     * 导航栏背景色
     */
    private int navigationBarColor;

    /**
     * 状态栏字体颜色，非黑即白
     */
    private boolean isDarkStatusBarBlack = false;

    public TitleBarStyle() {
    }

    protected TitleBarStyle(Parcel in) {
        titleLeftBackRes = in.readInt();
        titleDefaultText = in.readString();
        titleTextSize = in.readInt();
        titleTextColor = in.readInt();
        titleBackgroundColor = in.readInt();
        titleBarHeight = in.readInt();
        titleAlbumBackgroundRes = in.readInt();
        isTitleGravityLeft = in.readByte() != 0;
        titleDrawableRightRes = in.readInt();
        titleRightBackgroundRes = in.readInt();
        titleRightNormalText = in.readString();
        titleRightSelectText = in.readString();
        titleRightTextSize = in.readInt();
        titleRightTextColor = in.readInt();
        statusBarColor = in.readInt();
        navigationBarColor = in.readInt();
        isDarkStatusBarBlack = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(titleLeftBackRes);
        dest.writeString(titleDefaultText);
        dest.writeInt(titleTextSize);
        dest.writeInt(titleTextColor);
        dest.writeInt(titleBackgroundColor);
        dest.writeInt(titleBarHeight);
        dest.writeInt(titleAlbumBackgroundRes);
        dest.writeByte((byte) (isTitleGravityLeft ? 1 : 0));
        dest.writeInt(titleDrawableRightRes);
        dest.writeInt(titleRightBackgroundRes);
        dest.writeString(titleRightNormalText);
        dest.writeString(titleRightSelectText);
        dest.writeInt(titleRightTextSize);
        dest.writeInt(titleRightTextColor);
        dest.writeInt(statusBarColor);
        dest.writeInt(navigationBarColor);
        dest.writeByte((byte) (isDarkStatusBarBlack ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TitleBarStyle> CREATOR = new Creator<TitleBarStyle>() {
        @Override
        public TitleBarStyle createFromParcel(Parcel in) {
            return new TitleBarStyle(in);
        }

        @Override
        public TitleBarStyle[] newArray(int size) {
            return new TitleBarStyle[size];
        }
    };

    public int getTitleLeftBackRes() {
        return titleLeftBackRes;
    }

    public void setTitleLeftBackRes(int titleLeftBackRes) {
        this.titleLeftBackRes = titleLeftBackRes;
    }

    public String getTitleDefaultText() {
        return titleDefaultText;
    }

    public void setTitleDefaultText(String titleDefaultText) {
        this.titleDefaultText = titleDefaultText;
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public int getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public void setTitleBackgroundColor(int titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
    }

    public int getTitleBarHeight() {
        return titleBarHeight;
    }

    public void setTitleBarHeight(int titleBarHeight) {
        this.titleBarHeight = titleBarHeight;
    }

    public int getTitleAlbumBackgroundRes() {
        return titleAlbumBackgroundRes;
    }

    public void setTitleAlbumBackgroundRes(int titleAlbumBackgroundRes) {
        this.titleAlbumBackgroundRes = titleAlbumBackgroundRes;
    }

    public boolean isTitleGravityLeft() {
        return isTitleGravityLeft;
    }

    public void setTitleGravityLeft(boolean titleGravityLeft) {
        isTitleGravityLeft = titleGravityLeft;
    }

    public int getTitleDrawableRightRes() {
        return titleDrawableRightRes;
    }

    public void setTitleDrawableRightRes(int titleDrawableRightRes) {
        this.titleDrawableRightRes = titleDrawableRightRes;
    }

    public int getTitleRightBackgroundRes() {
        return titleRightBackgroundRes;
    }

    public void setTitleRightBackgroundRes(int titleRightBackgroundRes) {
        this.titleRightBackgroundRes = titleRightBackgroundRes;
    }

    public String getTitleRightNormalText() {
        return titleRightNormalText;
    }

    public void setTitleRightNormalText(String titleRightNormalText) {
        this.titleRightNormalText = titleRightNormalText;
    }

    public String getTitleRightSelectText() {
        return titleRightSelectText;
    }

    public void setTitleRightSelectText(String titleRightSelectText) {
        this.titleRightSelectText = titleRightSelectText;
    }

    public int getTitleRightTextSize() {
        return titleRightTextSize;
    }

    public void setTitleRightTextSize(int titleRightTextSize) {
        this.titleRightTextSize = titleRightTextSize;
    }

    public int getTitleRightTextColor() {
        return titleRightTextColor;
    }

    public void setTitleRightTextColor(int titleRightTextColor) {
        this.titleRightTextColor = titleRightTextColor;
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public void setStatusBarColor(int statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public int getNavigationBarColor() {
        return navigationBarColor;
    }

    public void setNavigationBarColor(int navigationBarColor) {
        this.navigationBarColor = navigationBarColor;
    }

    public boolean isDarkStatusBarBlack() {
        return isDarkStatusBarBlack;
    }

    public void setDarkStatusBarBlack(boolean darkStatusBarBlack) {
        isDarkStatusBarBlack = darkStatusBarBlack;
    }
}
