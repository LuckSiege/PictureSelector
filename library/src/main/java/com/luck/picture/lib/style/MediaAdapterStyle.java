package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RelativeLayout;

/**
 * @author：luck
 * @date：2021/11/15 4:14 下午
 * @describe：AdapterUI
 */
public class MediaAdapterStyle implements Parcelable {
    /**
     * 列表背景色
     */
    private int adapterListBackgroundColor;
    /**
     * 勾选样式字体大小
     */
    private int adapterSelectTextSize;

    /**
     * 勾选按钮点击区域
     */
    private int adapterSelectClickArea;

    /**
     * 勾选样式字体色值
     */
    private int adapterSelectTextColor;
    /**
     * 勾选样式
     */
    private int adapterSelectStyle;

    /**
     * 勾选样式位置
     * {@link RelativeLayout.addRule()}
     */
    private int[] adapterSelectStyleGravity;

    /**
     * 资源类型标识
     */
    private int adapterDurationDrawableLeft;

    /**
     * 时长文字字体大小
     */
    private int adapterDurationTextSize;

    /**
     * 时长文字颜色
     */
    private int adapterDurationTextColor;

    /**
     * 时长文字位置
     * {@link RelativeLayout.addRule()}
     */
    private int[] adapterDurationGravity;

    /**
     * 时长文字阴影位置
     * {@link RelativeLayout.addRule()}
     */
    private int[] adapterDurationShadowGravity;

    /**
     * 时长文字阴影背景
     */
    private int adapterDurationShadowBackground;

    /**
     * 拍照按钮背景色
     */
    private int adapterCameraBackground;

    /**
     * 拍照按钮图标
     */
    private int adapterCameraDrawableTop;

    /**
     * 拍照按钮文本
     */
    private String adapterCameraText;

    /**
     * 拍照按钮文本字体色值
     */
    private int adapterCameraTextColor;
    /**
     * 拍照按钮文本字体大小
     */
    private int adapterCameraTextSize;
    /**
     * 资源图标识的背景
     */
    private int adapterTagBackground;
    /**
     * 资源标识的字体大小
     */
    private int adapterTagTextSize;
    /**
     * 资源标识的字体色值
     */
    private int adapterTagTextColor;
    /**
     * 资源标识的位置
     * {@link RelativeLayout.addRule()}
     */
    private int[] adapterTagGravity;
    /**
     * 图片被编辑标识
     */
    private int adapterImageEditorRes;

    /**
     * 图片被编辑标识位置
     * {@link RelativeLayout.addRule()}
     */
    private int[] adapterImageEditorGravity;

    public MediaAdapterStyle() {

    }

    protected MediaAdapterStyle(Parcel in) {
        adapterListBackgroundColor = in.readInt();
        adapterSelectTextSize = in.readInt();
        adapterSelectClickArea = in.readInt();
        adapterSelectTextColor = in.readInt();
        adapterSelectStyle = in.readInt();
        adapterSelectStyleGravity = in.createIntArray();
        adapterDurationDrawableLeft = in.readInt();
        adapterDurationTextSize = in.readInt();
        adapterDurationTextColor = in.readInt();
        adapterDurationGravity = in.createIntArray();
        adapterDurationShadowGravity = in.createIntArray();
        adapterDurationShadowBackground = in.readInt();
        adapterCameraBackground = in.readInt();
        adapterCameraDrawableTop = in.readInt();
        adapterCameraText = in.readString();
        adapterCameraTextColor = in.readInt();
        adapterCameraTextSize = in.readInt();
        adapterTagBackground = in.readInt();
        adapterTagTextSize = in.readInt();
        adapterTagTextColor = in.readInt();
        adapterTagGravity = in.createIntArray();
        adapterImageEditorRes = in.readInt();
        adapterImageEditorGravity = in.createIntArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(adapterListBackgroundColor);
        dest.writeInt(adapterSelectTextSize);
        dest.writeInt(adapterSelectClickArea);
        dest.writeInt(adapterSelectTextColor);
        dest.writeInt(adapterSelectStyle);
        dest.writeIntArray(adapterSelectStyleGravity);
        dest.writeInt(adapterDurationDrawableLeft);
        dest.writeInt(adapterDurationTextSize);
        dest.writeInt(adapterDurationTextColor);
        dest.writeIntArray(adapterDurationGravity);
        dest.writeIntArray(adapterDurationShadowGravity);
        dest.writeInt(adapterDurationShadowBackground);
        dest.writeInt(adapterCameraBackground);
        dest.writeInt(adapterCameraDrawableTop);
        dest.writeString(adapterCameraText);
        dest.writeInt(adapterCameraTextColor);
        dest.writeInt(adapterCameraTextSize);
        dest.writeInt(adapterTagBackground);
        dest.writeInt(adapterTagTextSize);
        dest.writeInt(adapterTagTextColor);
        dest.writeIntArray(adapterTagGravity);
        dest.writeInt(adapterImageEditorRes);
        dest.writeIntArray(adapterImageEditorGravity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaAdapterStyle> CREATOR = new Creator<MediaAdapterStyle>() {
        @Override
        public MediaAdapterStyle createFromParcel(Parcel in) {
            return new MediaAdapterStyle(in);
        }

        @Override
        public MediaAdapterStyle[] newArray(int size) {
            return new MediaAdapterStyle[size];
        }
    };

    public int getAdapterListBackgroundColor() {
        return adapterListBackgroundColor;
    }

    public void setAdapterListBackgroundColor(int adapterListBackgroundColor) {
        this.adapterListBackgroundColor = adapterListBackgroundColor;
    }

    public int getAdapterSelectTextSize() {
        return adapterSelectTextSize;
    }

    public void setAdapterSelectTextSize(int adapterSelectTextSize) {
        this.adapterSelectTextSize = adapterSelectTextSize;
    }

    public int getAdapterSelectClickArea() {
        return adapterSelectClickArea;
    }

    public void setAdapterSelectClickArea(int adapterSelectClickArea) {
        this.adapterSelectClickArea = adapterSelectClickArea;
    }

    public int getAdapterSelectTextColor() {
        return adapterSelectTextColor;
    }

    public void setAdapterSelectTextColor(int adapterSelectTextColor) {
        this.adapterSelectTextColor = adapterSelectTextColor;
    }

    public int getAdapterSelectStyle() {
        return adapterSelectStyle;
    }

    public void setAdapterSelectStyle(int adapterSelectStyle) {
        this.adapterSelectStyle = adapterSelectStyle;
    }

    public int[] getAdapterSelectStyleGravity() {
        return adapterSelectStyleGravity;
    }

    public void setAdapterSelectStyleGravity(int[] adapterSelectStyleGravity) {
        this.adapterSelectStyleGravity = adapterSelectStyleGravity;
    }

    public int getAdapterDurationDrawableLeft() {
        return adapterDurationDrawableLeft;
    }

    public void setAdapterDurationDrawableLeft(int adapterDurationDrawableLeft) {
        this.adapterDurationDrawableLeft = adapterDurationDrawableLeft;
    }

    public int getAdapterDurationTextSize() {
        return adapterDurationTextSize;
    }

    public void setAdapterDurationTextSize(int adapterDurationTextSize) {
        this.adapterDurationTextSize = adapterDurationTextSize;
    }

    public int getAdapterDurationTextColor() {
        return adapterDurationTextColor;
    }

    public void setAdapterDurationTextColor(int adapterDurationTextColor) {
        this.adapterDurationTextColor = adapterDurationTextColor;
    }

    public int[] getAdapterDurationGravity() {
        return adapterDurationGravity;
    }

    public void setAdapterDurationGravity(int[] adapterDurationGravity) {
        this.adapterDurationGravity = adapterDurationGravity;
    }

    public int[] getAdapterDurationShadowGravity() {
        return adapterDurationShadowGravity;
    }

    public void setAdapterDurationShadowGravity(int[] adapterDurationShadowGravity) {
        this.adapterDurationShadowGravity = adapterDurationShadowGravity;
    }

    public int getAdapterDurationShadowBackground() {
        return adapterDurationShadowBackground;
    }

    public void setAdapterDurationShadowBackground(int adapterDurationShadowBackground) {
        this.adapterDurationShadowBackground = adapterDurationShadowBackground;
    }

    public int getAdapterCameraBackground() {
        return adapterCameraBackground;
    }

    public void setAdapterCameraBackground(int adapterCameraBackground) {
        this.adapterCameraBackground = adapterCameraBackground;
    }

    public int getAdapterCameraDrawableTop() {
        return adapterCameraDrawableTop;
    }

    public void setAdapterCameraDrawableTop(int adapterCameraDrawableTop) {
        this.adapterCameraDrawableTop = adapterCameraDrawableTop;
    }

    public String getAdapterCameraText() {
        return adapterCameraText;
    }

    public void setAdapterCameraText(String adapterCameraText) {
        this.adapterCameraText = adapterCameraText;
    }

    public int getAdapterCameraTextColor() {
        return adapterCameraTextColor;
    }

    public void setAdapterCameraTextColor(int adapterCameraTextColor) {
        this.adapterCameraTextColor = adapterCameraTextColor;
    }

    public int getAdapterCameraTextSize() {
        return adapterCameraTextSize;
    }

    public void setAdapterCameraTextSize(int adapterCameraTextSize) {
        this.adapterCameraTextSize = adapterCameraTextSize;
    }

    public int getAdapterTagBackground() {
        return adapterTagBackground;
    }

    public void setAdapterTagBackground(int adapterTagBackground) {
        this.adapterTagBackground = adapterTagBackground;
    }

    public int getAdapterTagTextSize() {
        return adapterTagTextSize;
    }

    public void setAdapterTagTextSize(int adapterTagTextSize) {
        this.adapterTagTextSize = adapterTagTextSize;
    }

    public int getAdapterTagTextColor() {
        return adapterTagTextColor;
    }

    public void setAdapterTagTextColor(int adapterTagTextColor) {
        this.adapterTagTextColor = adapterTagTextColor;
    }

    public int[] getAdapterTagGravity() {
        return adapterTagGravity;
    }

    public void setAdapterTagGravity(int[] adapterTagGravity) {
        this.adapterTagGravity = adapterTagGravity;
    }

    public int getAdapterImageEditorRes() {
        return adapterImageEditorRes;
    }

    public void setAdapterImageEditorRes(int adapterImageEditorRes) {
        this.adapterImageEditorRes = adapterImageEditorRes;
    }

    public int[] getAdapterImageEditorGravity() {
        return adapterImageEditorGravity;
    }

    public void setAdapterImageEditorGravity(int[] adapterImageEditorGravity) {
        this.adapterImageEditorGravity = adapterImageEditorGravity;
    }
}
