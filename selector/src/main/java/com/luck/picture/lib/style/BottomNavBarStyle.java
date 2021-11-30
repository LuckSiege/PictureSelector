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
     * 底部预览页NarBar背景色
     */
    private int bottomPreviewNarBarBackgroundColor;

    /**
     * 底部导航栏高度
     * <p>
     * use unit dp
     * </p>
     */
    private int bottomNarBarHeight;

    /**
     * 底部预览文本
     */
    private String bottomPreviewNormalText;

    /**
     * 底部预览文本字体大小
     */
    private int bottomPreviewNormalTextSize;
    /**
     * 底部预览文本正常字体色值
     */
    private int bottomPreviewNormalTextColor;

    /**
     * 底部选中预览文本
     */
    private String bottomPreviewSelectText;

    /**
     * 底部预览文本选中字体色值
     */
    private int bottomPreviewSelectTextColor;

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
    private int bottomSelectNumResources;
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
    private boolean isCompleteCountTips = true;


    public BottomNavBarStyle() {
    }

    protected BottomNavBarStyle(Parcel in) {
        bottomNarBarBackgroundColor = in.readInt();
        bottomPreviewNarBarBackgroundColor = in.readInt();
        bottomNarBarHeight = in.readInt();
        bottomPreviewNormalText = in.readString();
        bottomPreviewNormalTextSize = in.readInt();
        bottomPreviewNormalTextColor = in.readInt();
        bottomPreviewSelectText = in.readString();
        bottomPreviewSelectTextColor = in.readInt();
        bottomEditorText = in.readString();
        bottomEditorTextSize = in.readInt();
        bottomEditorTextColor = in.readInt();
        bottomOriginalDrawableLeft = in.readInt();
        bottomOriginalText = in.readString();
        bottomOriginalTextSize = in.readInt();
        bottomOriginalTextColor = in.readInt();
        bottomSelectNumResources = in.readInt();
        bottomSelectNumTextSize = in.readInt();
        bottomSelectNumTextColor = in.readInt();
        isCompleteCountTips = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bottomNarBarBackgroundColor);
        dest.writeInt(bottomPreviewNarBarBackgroundColor);
        dest.writeInt(bottomNarBarHeight);
        dest.writeString(bottomPreviewNormalText);
        dest.writeInt(bottomPreviewNormalTextSize);
        dest.writeInt(bottomPreviewNormalTextColor);
        dest.writeString(bottomPreviewSelectText);
        dest.writeInt(bottomPreviewSelectTextColor);
        dest.writeString(bottomEditorText);
        dest.writeInt(bottomEditorTextSize);
        dest.writeInt(bottomEditorTextColor);
        dest.writeInt(bottomOriginalDrawableLeft);
        dest.writeString(bottomOriginalText);
        dest.writeInt(bottomOriginalTextSize);
        dest.writeInt(bottomOriginalTextColor);
        dest.writeInt(bottomSelectNumResources);
        dest.writeInt(bottomSelectNumTextSize);
        dest.writeInt(bottomSelectNumTextColor);
        dest.writeByte((byte) (isCompleteCountTips ? 1 : 0));
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

    public int getBottomPreviewNarBarBackgroundColor() {
        return bottomPreviewNarBarBackgroundColor;
    }

    public void setBottomPreviewNarBarBackgroundColor(int bottomPreviewNarBarBackgroundColor) {
        this.bottomPreviewNarBarBackgroundColor = bottomPreviewNarBarBackgroundColor;
    }

    public int getBottomNarBarHeight() {
        return bottomNarBarHeight;
    }

    public void setBottomNarBarHeight(int bottomNarBarHeight) {
        this.bottomNarBarHeight = bottomNarBarHeight;
    }

    public String getBottomPreviewNormalText() {
        return bottomPreviewNormalText;
    }

    public void setBottomPreviewNormalText(String bottomPreviewNormalText) {
        this.bottomPreviewNormalText = bottomPreviewNormalText;
    }

    public int getBottomPreviewNormalTextSize() {
        return bottomPreviewNormalTextSize;
    }

    public void setBottomPreviewNormalTextSize(int bottomPreviewNormalTextSize) {
        this.bottomPreviewNormalTextSize = bottomPreviewNormalTextSize;
    }

    public int getBottomPreviewNormalTextColor() {
        return bottomPreviewNormalTextColor;
    }

    public void setBottomPreviewNormalTextColor(int bottomPreviewNormalTextColor) {
        this.bottomPreviewNormalTextColor = bottomPreviewNormalTextColor;
    }

    public String getBottomPreviewSelectText() {
        return bottomPreviewSelectText;
    }

    public void setBottomPreviewSelectText(String bottomPreviewSelectText) {
        this.bottomPreviewSelectText = bottomPreviewSelectText;
    }

    public int getBottomPreviewSelectTextColor() {
        return bottomPreviewSelectTextColor;
    }

    public void setBottomPreviewSelectTextColor(int bottomPreviewSelectTextColor) {
        this.bottomPreviewSelectTextColor = bottomPreviewSelectTextColor;
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

    public int getBottomSelectNumResources() {
        return bottomSelectNumResources;
    }

    public void setBottomSelectNumResources(int bottomSelectNumResources) {
        this.bottomSelectNumResources = bottomSelectNumResources;
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

    public boolean isCompleteCountTips() {
        return isCompleteCountTips;
    }

    public void setCompleteCountTips(boolean completeCountTips) {
        isCompleteCountTips = completeCountTips;
    }
}
