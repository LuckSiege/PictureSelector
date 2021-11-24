package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author：luck
 * @date：2021/11/15 4:15 下午
 * @describe：NavBarbottomStyle
 */
public class BottomNavBarStyle implements Parcelable {
    /**
     * 底部导航栏背景色
     */
    private int bottomNarBarBackgroundColor;

    /**
     * 底部导航栏高度
     */
    private int bottomNarBarHeight;
    /**
     * 底部预览文本
     */
    private String bottomPreviewText;

    /**
     * 底部选中预览文本
     */
    private String bottomPreviewSelectText;

    /**
     * 底部预览文本字体大小
     */
    private int bottomPreviewTextSize;
    /**
     * 底部预览文本正常字体色值
     */
    private int bottomPreviewNormalTextColor;
    /**
     * 底部预览文本选中字体色值
     */
    private int bottomPreviewSelectTextColor;
    /**
     * 底部默认选择文本
     */
    private String bottomSelectNormalText;
    /**
     * 底部默认选择文本字体大小
     */
    private int bottomSelectNormalTextSize;
    /**
     * 底部默认选择文本字体色值
     */
    private int bottomSelectNormalTextColor;
    /**
     * 底部选择文本
     */
    private String bottomSelectText;
    /**
     * 底部选择文本字体大小
     */
    private int bottomSelectTextSize;
    /**
     * 底部选择文本字体色值
     */
    private int bottomSelectTextColor;
    /**
     * 底部编辑文字
     */
    private String bottomEditorText;
    /**
     * 底部编辑文字大小
     */
    private int bottomEditorTextSize;
    /**
     * 底部编辑文字色值
     */
    private int bottomEditorTextColor;
    /**
     * 底部原图文字DrawableLeft
     */
    private int bottomOriginalDrawableLeft;
    /**
     * 底部原图文字
     */
    private String bottomOriginalText;
    /**
     * 底部原图文字大小
     */
    private int bottomOriginalTextSize;
    /**
     * 底部原图文字色值
     */
    private int bottomOriginalTextColor;

    /**
     * 已选数量背景样式
     */
    private int bottomSelectNumRes;
    /**
     * 已选数量文字大小
     */
    private int bottomSelectNumTextSize;
    /**
     * 已选数量文字颜色
     */
    private int bottomSelectNumTextColor;

    /**
     * 是否显示已选数量圆点提醒
     */
    private boolean isSelectNumVisible = true;


    public BottomNavBarStyle() {
    }

    protected BottomNavBarStyle(Parcel in) {
        bottomNarBarBackgroundColor = in.readInt();
        bottomNarBarHeight = in.readInt();
        bottomPreviewText = in.readString();
        bottomPreviewSelectText = in.readString();
        bottomPreviewTextSize = in.readInt();
        bottomPreviewNormalTextColor = in.readInt();
        bottomPreviewSelectTextColor = in.readInt();
        bottomSelectNormalText = in.readString();
        bottomSelectNormalTextSize = in.readInt();
        bottomSelectNormalTextColor = in.readInt();
        bottomSelectText = in.readString();
        bottomSelectTextSize = in.readInt();
        bottomSelectTextColor = in.readInt();
        bottomEditorText = in.readString();
        bottomEditorTextSize = in.readInt();
        bottomEditorTextColor = in.readInt();
        bottomOriginalDrawableLeft = in.readInt();
        bottomOriginalText = in.readString();
        bottomOriginalTextSize = in.readInt();
        bottomOriginalTextColor = in.readInt();
        bottomSelectNumRes = in.readInt();
        bottomSelectNumTextSize = in.readInt();
        bottomSelectNumTextColor = in.readInt();
        isSelectNumVisible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bottomNarBarBackgroundColor);
        dest.writeInt(bottomNarBarHeight);
        dest.writeString(bottomPreviewText);
        dest.writeString(bottomPreviewSelectText);
        dest.writeInt(bottomPreviewTextSize);
        dest.writeInt(bottomPreviewNormalTextColor);
        dest.writeInt(bottomPreviewSelectTextColor);
        dest.writeString(bottomSelectNormalText);
        dest.writeInt(bottomSelectNormalTextSize);
        dest.writeInt(bottomSelectNormalTextColor);
        dest.writeString(bottomSelectText);
        dest.writeInt(bottomSelectTextSize);
        dest.writeInt(bottomSelectTextColor);
        dest.writeString(bottomEditorText);
        dest.writeInt(bottomEditorTextSize);
        dest.writeInt(bottomEditorTextColor);
        dest.writeInt(bottomOriginalDrawableLeft);
        dest.writeString(bottomOriginalText);
        dest.writeInt(bottomOriginalTextSize);
        dest.writeInt(bottomOriginalTextColor);
        dest.writeInt(bottomSelectNumRes);
        dest.writeInt(bottomSelectNumTextSize);
        dest.writeInt(bottomSelectNumTextColor);
        dest.writeByte((byte) (isSelectNumVisible ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BottomNavBarStyle> CREATOR = new Creator<BottomNavBarStyle>() {
        @Override
        public BottomNavBarStyle createFromParcel(Parcel in) {
            return new BottomNavBarStyle(in);
        }

        @Override
        public BottomNavBarStyle[] newArray(int size) {
            return new BottomNavBarStyle[size];
        }
    };

    public int getBottomNarBarBackgroundColor() {
        return bottomNarBarBackgroundColor;
    }

    public void setBottomNarBarBackgroundColor(int bottomNarBarBackgroundColor) {
        this.bottomNarBarBackgroundColor = bottomNarBarBackgroundColor;
    }

    public int getBottomNarBarHeight() {
        return bottomNarBarHeight;
    }

    public void setBottomNarBarHeight(int bottomNarBarHeight) {
        this.bottomNarBarHeight = bottomNarBarHeight;
    }

    public String getBottomPreviewText() {
        return bottomPreviewText;
    }

    public void setBottomPreviewText(String bottomPreviewText) {
        this.bottomPreviewText = bottomPreviewText;
    }

    public String getBottomPreviewSelectText() {
        return bottomPreviewSelectText;
    }

    public void setBottomPreviewSelectText(String bottomPreviewSelectText) {
        this.bottomPreviewSelectText = bottomPreviewSelectText;
    }

    public int getBottomPreviewTextSize() {
        return bottomPreviewTextSize;
    }

    public void setBottomPreviewTextSize(int bottomPreviewTextSize) {
        this.bottomPreviewTextSize = bottomPreviewTextSize;
    }

    public int getBottomPreviewNormalTextColor() {
        return bottomPreviewNormalTextColor;
    }

    public void setBottomPreviewNormalTextColor(int bottomPreviewNormalTextColor) {
        this.bottomPreviewNormalTextColor = bottomPreviewNormalTextColor;
    }

    public int getBottomPreviewSelectTextColor() {
        return bottomPreviewSelectTextColor;
    }

    public void setBottomPreviewSelectTextColor(int bottomPreviewSelectTextColor) {
        this.bottomPreviewSelectTextColor = bottomPreviewSelectTextColor;
    }

    public String getBottomSelectNormalText() {
        return bottomSelectNormalText;
    }

    public void setBottomSelectNormalText(String bottomSelectNormalText) {
        this.bottomSelectNormalText = bottomSelectNormalText;
    }

    public int getBottomSelectNormalTextSize() {
        return bottomSelectNormalTextSize;
    }

    public void setBottomSelectNormalTextSize(int bottomSelectNormalTextSize) {
        this.bottomSelectNormalTextSize = bottomSelectNormalTextSize;
    }

    public int getBottomSelectNormalTextColor() {
        return bottomSelectNormalTextColor;
    }

    public void setBottomSelectNormalTextColor(int bottomSelectNormalTextColor) {
        this.bottomSelectNormalTextColor = bottomSelectNormalTextColor;
    }

    public String getBottomSelectText() {
        return bottomSelectText;
    }

    public void setBottomSelectText(String bottomSelectText) {
        this.bottomSelectText = bottomSelectText;
    }

    public int getBottomSelectTextSize() {
        return bottomSelectTextSize;
    }

    public void setBottomSelectTextSize(int bottomSelectTextSize) {
        this.bottomSelectTextSize = bottomSelectTextSize;
    }

    public int getBottomSelectTextColor() {
        return bottomSelectTextColor;
    }

    public void setBottomSelectTextColor(int bottomSelectTextColor) {
        this.bottomSelectTextColor = bottomSelectTextColor;
    }

    public String getBottomEditorText() {
        return bottomEditorText;
    }

    public void setBottomEditorText(String bottomEditorText) {
        this.bottomEditorText = bottomEditorText;
    }

    public int getBottomEditorTextSize() {
        return bottomEditorTextSize;
    }

    public void setBottomEditorTextSize(int bottomEditorTextSize) {
        this.bottomEditorTextSize = bottomEditorTextSize;
    }

    public int getBottomEditorTextColor() {
        return bottomEditorTextColor;
    }

    public void setBottomEditorTextColor(int bottomEditorTextColor) {
        this.bottomEditorTextColor = bottomEditorTextColor;
    }

    public int getBottomOriginalDrawableLeft() {
        return bottomOriginalDrawableLeft;
    }

    public void setBottomOriginalDrawableLeft(int bottomOriginalDrawableLeft) {
        this.bottomOriginalDrawableLeft = bottomOriginalDrawableLeft;
    }

    public String getBottomOriginalText() {
        return bottomOriginalText;
    }

    public void setBottomOriginalText(String bottomOriginalText) {
        this.bottomOriginalText = bottomOriginalText;
    }

    public int getBottomOriginalTextSize() {
        return bottomOriginalTextSize;
    }

    public void setBottomOriginalTextSize(int bottomOriginalTextSize) {
        this.bottomOriginalTextSize = bottomOriginalTextSize;
    }

    public int getBottomOriginalTextColor() {
        return bottomOriginalTextColor;
    }

    public void setBottomOriginalTextColor(int bottomOriginalTextColor) {
        this.bottomOriginalTextColor = bottomOriginalTextColor;
    }

    public int getBottomSelectNumRes() {
        return bottomSelectNumRes;
    }

    public void setBottomSelectNumRes(int bottomSelectNumRes) {
        this.bottomSelectNumRes = bottomSelectNumRes;
    }

    public int getBottomSelectNumTextSize() {
        return bottomSelectNumTextSize;
    }

    public void setBottomSelectNumTextSize(int bottomSelectNumTextSize) {
        this.bottomSelectNumTextSize = bottomSelectNumTextSize;
    }

    public int getBottomSelectNumTextColor() {
        return bottomSelectNumTextColor;
    }

    public void setBottomSelectNumTextColor(int bottomSelectNumTextColor) {
        this.bottomSelectNumTextColor = bottomSelectNumTextColor;
    }

    public boolean isSelectNumVisible() {
        return isSelectNumVisible;
    }

    public void setSelectNumVisible(boolean selectNumVisible) {
        isSelectNumVisible = selectNumVisible;
    }
}
