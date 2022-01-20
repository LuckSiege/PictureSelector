package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RelativeLayout;

/**
 * @author：luck
 * @date：2021/11/15 4:14 下午
 * @describe：SelectMainStyle
 */
public class SelectMainStyle implements Parcelable {

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

    /**
     * 完成按钮从底部放在右上角
     */
    private boolean isCompleteSelectRelativeTop;

    /**
     * 预览页选择按钮从顶部放在右下角
     */
    private boolean isPreviewSelectRelativeBottom;

    /**
     * 预览页是否显示选择画廊
     */
    private boolean isPreviewDisplaySelectGallery;

    /**
     * 预览页选择按钮MarginRight
     * <p>
     * unit dp
     * </p>
     */
    private int previewSelectMarginRight;

    /**
     * 预览背景色
     */
    private int previewBackgroundColor;

    /**
     * 预览页选择按钮文本
     */
    private String previewSelectText;

    /**
     * 预览页选择按钮字体大小
     */
    private int previewSelectTextSize;

    /**
     * 预览页选择按钮字体颜色
     */
    private int previewSelectTextColor;


    /**
     * 勾选样式
     */
    private int selectBackground;

    /**
     * 预览样式勾选样式
     */
    private int previewSelectBackground;

    /**
     * 勾选样式是否使用数量类型
     */
    private boolean isSelectNumberStyle;

    /**
     * 预览页勾选样式是否使用数量类型
     */
    private boolean isPreviewSelectNumberStyle;

    /**
     * 列表背景色
     */
    private int mainListBackgroundColor;

    /**
     * 选择按钮默认文本
     */
    private String selectNormalText;
    /**
     * 选择按钮默认文本字体大小
     */
    private int selectNormalTextSize;
    /**
     * 选择按钮默认文本字体色值
     */
    private int selectNormalTextColor;

    /**
     * 选择按钮默认背景
     */
    private int selectNormalBackgroundResources;

    /**
     * 选择按钮文本
     */
    private String selectText;
    /**
     * 选择按钮文本字体大小
     */
    private int selectTextSize;
    /**
     * 选择按钮文本字体色值
     */
    private int selectTextColor;

    /**
     * 选择按钮选中背景
     */
    private int selectBackgroundResources;

    /**
     * RecyclerView列表item间隙
     * <p>
     * use unit dp
     * </p>
     */
    private int adapterItemSpacingSize;

    /**
     * 是否显示左右间距
     */
    private boolean isAdapterItemIncludeEdge;

    /**
     * 勾选样式字体大小
     */
    private int adapterSelectTextSize;

    /**
     * 勾选按钮点击区域
     * <p>
     * use unit dp
     * </p>
     */
    private int adapterSelectClickArea;

    /**
     * 勾选样式字体色值
     */
    private int adapterSelectTextColor;

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
     * 时长文字阴影背景
     */
    private int adapterDurationBackgroundResources;

    /**
     * 拍照按钮背景色
     */
    private int adapterCameraBackgroundColor;

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
    private int adapterTagBackgroundResources;
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
    private int adapterImageEditorResources;

    /**
     * 图片被编辑标识位置
     * {@link RelativeLayout.addRule()}
     */
    private int[] adapterImageEditorGravity;

    /**
     * 预览页画廊边框样式
     */
    private int adapterPreviewGalleryFrameResource;

    /**
     * 预览页画廊背景色
     */
    private int adapterPreviewGalleryBackgroundResource;

    /**
     * 预览页画廊item大小
     * <p>
     * use unit dp
     * </p>
     */
    private int adapterPreviewGalleryItemSize;

    public SelectMainStyle() {

    }


    protected SelectMainStyle(Parcel in) {
        statusBarColor = in.readInt();
        navigationBarColor = in.readInt();
        isDarkStatusBarBlack = in.readByte() != 0;
        isCompleteSelectRelativeTop = in.readByte() != 0;
        isPreviewSelectRelativeBottom = in.readByte() != 0;
        isPreviewDisplaySelectGallery = in.readByte() != 0;
        previewSelectMarginRight = in.readInt();
        previewBackgroundColor = in.readInt();
        previewSelectText = in.readString();
        previewSelectTextSize = in.readInt();
        previewSelectTextColor = in.readInt();
        selectBackground = in.readInt();
        previewSelectBackground = in.readInt();
        isSelectNumberStyle = in.readByte() != 0;
        isPreviewSelectNumberStyle = in.readByte() != 0;
        mainListBackgroundColor = in.readInt();
        selectNormalText = in.readString();
        selectNormalTextSize = in.readInt();
        selectNormalTextColor = in.readInt();
        selectNormalBackgroundResources = in.readInt();
        selectText = in.readString();
        selectTextSize = in.readInt();
        selectTextColor = in.readInt();
        selectBackgroundResources = in.readInt();
        adapterItemSpacingSize = in.readInt();
        isAdapterItemIncludeEdge = in.readByte() != 0;
        adapterSelectTextSize = in.readInt();
        adapterSelectClickArea = in.readInt();
        adapterSelectTextColor = in.readInt();
        adapterSelectStyleGravity = in.createIntArray();
        adapterDurationDrawableLeft = in.readInt();
        adapterDurationTextSize = in.readInt();
        adapterDurationTextColor = in.readInt();
        adapterDurationGravity = in.createIntArray();
        adapterDurationBackgroundResources = in.readInt();
        adapterCameraBackgroundColor = in.readInt();
        adapterCameraDrawableTop = in.readInt();
        adapterCameraText = in.readString();
        adapterCameraTextColor = in.readInt();
        adapterCameraTextSize = in.readInt();
        adapterTagBackgroundResources = in.readInt();
        adapterTagTextSize = in.readInt();
        adapterTagTextColor = in.readInt();
        adapterTagGravity = in.createIntArray();
        adapterImageEditorResources = in.readInt();
        adapterImageEditorGravity = in.createIntArray();
        adapterPreviewGalleryFrameResource = in.readInt();
        adapterPreviewGalleryBackgroundResource = in.readInt();
        adapterPreviewGalleryItemSize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusBarColor);
        dest.writeInt(navigationBarColor);
        dest.writeByte((byte) (isDarkStatusBarBlack ? 1 : 0));
        dest.writeByte((byte) (isCompleteSelectRelativeTop ? 1 : 0));
        dest.writeByte((byte) (isPreviewSelectRelativeBottom ? 1 : 0));
        dest.writeByte((byte) (isPreviewDisplaySelectGallery ? 1 : 0));
        dest.writeInt(previewSelectMarginRight);
        dest.writeInt(previewBackgroundColor);
        dest.writeString(previewSelectText);
        dest.writeInt(previewSelectTextSize);
        dest.writeInt(previewSelectTextColor);
        dest.writeInt(selectBackground);
        dest.writeInt(previewSelectBackground);
        dest.writeByte((byte) (isSelectNumberStyle ? 1 : 0));
        dest.writeByte((byte) (isPreviewSelectNumberStyle ? 1 : 0));
        dest.writeInt(mainListBackgroundColor);
        dest.writeString(selectNormalText);
        dest.writeInt(selectNormalTextSize);
        dest.writeInt(selectNormalTextColor);
        dest.writeInt(selectNormalBackgroundResources);
        dest.writeString(selectText);
        dest.writeInt(selectTextSize);
        dest.writeInt(selectTextColor);
        dest.writeInt(selectBackgroundResources);
        dest.writeInt(adapterItemSpacingSize);
        dest.writeByte((byte) (isAdapterItemIncludeEdge ? 1 : 0));
        dest.writeInt(adapterSelectTextSize);
        dest.writeInt(adapterSelectClickArea);
        dest.writeInt(adapterSelectTextColor);
        dest.writeIntArray(adapterSelectStyleGravity);
        dest.writeInt(adapterDurationDrawableLeft);
        dest.writeInt(adapterDurationTextSize);
        dest.writeInt(adapterDurationTextColor);
        dest.writeIntArray(adapterDurationGravity);
        dest.writeInt(adapterDurationBackgroundResources);
        dest.writeInt(adapterCameraBackgroundColor);
        dest.writeInt(adapterCameraDrawableTop);
        dest.writeString(adapterCameraText);
        dest.writeInt(adapterCameraTextColor);
        dest.writeInt(adapterCameraTextSize);
        dest.writeInt(adapterTagBackgroundResources);
        dest.writeInt(adapterTagTextSize);
        dest.writeInt(adapterTagTextColor);
        dest.writeIntArray(adapterTagGravity);
        dest.writeInt(adapterImageEditorResources);
        dest.writeIntArray(adapterImageEditorGravity);
        dest.writeInt(adapterPreviewGalleryFrameResource);
        dest.writeInt(adapterPreviewGalleryBackgroundResource);
        dest.writeInt(adapterPreviewGalleryItemSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectMainStyle> CREATOR = new Creator<SelectMainStyle>() {
        @Override
        public SelectMainStyle createFromParcel(Parcel in) {
            return new SelectMainStyle(in);
        }

        @Override
        public SelectMainStyle[] newArray(int size) {
            return new SelectMainStyle[size];
        }
    };

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

    public boolean isCompleteSelectRelativeTop() {
        return isCompleteSelectRelativeTop;
    }

    public void setCompleteSelectRelativeTop(boolean completeSelectRelativeTop) {
        isCompleteSelectRelativeTop = completeSelectRelativeTop;
    }

    public boolean isPreviewSelectRelativeBottom() {
        return isPreviewSelectRelativeBottom;
    }

    public void setPreviewSelectRelativeBottom(boolean previewSelectRelativeBottom) {
        isPreviewSelectRelativeBottom = previewSelectRelativeBottom;
    }

    public boolean isPreviewDisplaySelectGallery() {
        return isPreviewDisplaySelectGallery;
    }

    public void setPreviewDisplaySelectGallery(boolean previewDisplaySelectGallery) {
        isPreviewDisplaySelectGallery = previewDisplaySelectGallery;
    }

    public int getPreviewSelectMarginRight() {
        return previewSelectMarginRight;
    }

    public void setPreviewSelectMarginRight(int previewSelectMarginRight) {
        this.previewSelectMarginRight = previewSelectMarginRight;
    }

    public String getPreviewSelectText() {
        return previewSelectText;
    }

    public void setPreviewSelectText(String previewSelectText) {
        this.previewSelectText = previewSelectText;
    }

    public int getPreviewSelectTextSize() {
        return previewSelectTextSize;
    }

    public void setPreviewSelectTextSize(int previewSelectTextSize) {
        this.previewSelectTextSize = previewSelectTextSize;
    }

    public int getPreviewSelectTextColor() {
        return previewSelectTextColor;
    }

    public void setPreviewSelectTextColor(int previewSelectTextColor) {
        this.previewSelectTextColor = previewSelectTextColor;
    }

    public int getSelectBackground() {
        return selectBackground;
    }

    public void setSelectBackground(int selectBackground) {
        this.selectBackground = selectBackground;
    }

    public int getPreviewSelectBackground() {
        return previewSelectBackground;
    }

    public void setPreviewSelectBackground(int previewSelectBackground) {
        this.previewSelectBackground = previewSelectBackground;
    }

    public boolean isSelectNumberStyle() {
        return isSelectNumberStyle;
    }

    public void setSelectNumberStyle(boolean selectNumberStyle) {
        isSelectNumberStyle = selectNumberStyle;
    }

    public boolean isPreviewSelectNumberStyle() {
        return isPreviewSelectNumberStyle;
    }

    public void setPreviewSelectNumberStyle(boolean previewSelectNumberStyle) {
        isPreviewSelectNumberStyle = previewSelectNumberStyle;
    }

    public int getMainListBackgroundColor() {
        return mainListBackgroundColor;
    }

    public void setMainListBackgroundColor(int mainListBackgroundColor) {
        this.mainListBackgroundColor = mainListBackgroundColor;
    }

    public String getSelectNormalText() {
        return selectNormalText;
    }

    public void setSelectNormalText(String selectNormalText) {
        this.selectNormalText = selectNormalText;
    }

    public int getSelectNormalTextSize() {
        return selectNormalTextSize;
    }

    public void setSelectNormalTextSize(int selectNormalTextSize) {
        this.selectNormalTextSize = selectNormalTextSize;
    }

    public int getSelectNormalTextColor() {
        return selectNormalTextColor;
    }

    public void setSelectNormalTextColor(int selectNormalTextColor) {
        this.selectNormalTextColor = selectNormalTextColor;
    }

    public int getSelectNormalBackgroundResources() {
        return selectNormalBackgroundResources;
    }

    public void setSelectNormalBackgroundResources(int selectNormalBackgroundResources) {
        this.selectNormalBackgroundResources = selectNormalBackgroundResources;
    }

    public String getSelectText() {
        return selectText;
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public int getSelectTextSize() {
        return selectTextSize;
    }

    public void setSelectTextSize(int selectTextSize) {
        this.selectTextSize = selectTextSize;
    }

    public int getSelectTextColor() {
        return selectTextColor;
    }

    public void setSelectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
    }

    public int getSelectBackgroundResources() {
        return selectBackgroundResources;
    }

    public void setSelectBackgroundResources(int selectBackgroundResources) {
        this.selectBackgroundResources = selectBackgroundResources;
    }

    public int getAdapterItemSpacingSize() {
        return adapterItemSpacingSize;
    }

    public void setAdapterItemSpacingSize(int adapterItemSpacingSize) {
        this.adapterItemSpacingSize = adapterItemSpacingSize;
    }

    public boolean isAdapterItemIncludeEdge() {
        return isAdapterItemIncludeEdge;
    }

    public void setAdapterItemIncludeEdge(boolean adapterItemIncludeEdge) {
        isAdapterItemIncludeEdge = adapterItemIncludeEdge;
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

    public int getAdapterDurationBackgroundResources() {
        return adapterDurationBackgroundResources;
    }

    public void setAdapterDurationBackgroundResources(int adapterDurationBackgroundResources) {
        this.adapterDurationBackgroundResources = adapterDurationBackgroundResources;
    }

    public int getAdapterCameraBackgroundColor() {
        return adapterCameraBackgroundColor;
    }

    public void setAdapterCameraBackgroundColor(int adapterCameraBackgroundColor) {
        this.adapterCameraBackgroundColor = adapterCameraBackgroundColor;
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

    public int getAdapterTagBackgroundResources() {
        return adapterTagBackgroundResources;
    }

    public void setAdapterTagBackgroundResources(int adapterTagBackgroundResources) {
        this.adapterTagBackgroundResources = adapterTagBackgroundResources;
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

    public int getAdapterImageEditorResources() {
        return adapterImageEditorResources;
    }

    public void setAdapterImageEditorResources(int adapterImageEditorResources) {
        this.adapterImageEditorResources = adapterImageEditorResources;
    }

    public int[] getAdapterImageEditorGravity() {
        return adapterImageEditorGravity;
    }

    public void setAdapterImageEditorGravity(int[] adapterImageEditorGravity) {
        this.adapterImageEditorGravity = adapterImageEditorGravity;
    }

    public int getAdapterPreviewGalleryFrameResource() {
        return adapterPreviewGalleryFrameResource;
    }

    public void setAdapterPreviewGalleryFrameResource(int adapterPreviewGalleryFrameResource) {
        this.adapterPreviewGalleryFrameResource = adapterPreviewGalleryFrameResource;
    }

    public int getAdapterPreviewGalleryBackgroundResource() {
        return adapterPreviewGalleryBackgroundResource;
    }

    public void setAdapterPreviewGalleryBackgroundResource(int adapterPreviewGalleryBackgroundResource) {
        this.adapterPreviewGalleryBackgroundResource = adapterPreviewGalleryBackgroundResource;
    }

    public int getAdapterPreviewGalleryItemSize() {
        return adapterPreviewGalleryItemSize;
    }

    public void setAdapterPreviewGalleryItemSize(int adapterPreviewGalleryItemSize) {
        this.adapterPreviewGalleryItemSize = adapterPreviewGalleryItemSize;
    }

    public int getPreviewBackgroundColor() {
        return previewBackgroundColor;
    }

    public void setPreviewBackgroundColor(int previewBackgroundColor) {
        this.previewBackgroundColor = previewBackgroundColor;
    }
}
