package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnimRes;

/**
 * @author：luck
 * @date：2019-11-25 18:17
 * @describe：PictureSelector Activity动画管理Style
 */
public class PictureWindowAnimationStyle implements Parcelable {
    /**
     * 相册启动动画
     */
    @AnimRes
    public int activityEnterAnimation;

    /**
     * 相册退出动画
     */
    @AnimRes
    public int activityExitAnimation;

    /**
     * 预览界面启动动画
     */
    @AnimRes
    public int activityPreviewEnterAnimation;

    /**
     * 预览界面退出动画
     */
    @AnimRes
    public int activityPreviewExitAnimation;

    /**
     * 裁剪界面启动动画
     */
    @AnimRes
    public int activityCropEnterAnimation;

    /**
     * 裁剪界面退出动画
     */
    @AnimRes
    public int activityCropExitAnimation;


    public PictureWindowAnimationStyle() {
        super();
    }

    public PictureWindowAnimationStyle(@AnimRes int activityEnterAnimation,
                                       @AnimRes int activityExitAnimation) {
        super();
        this.activityEnterAnimation = activityEnterAnimation;
        this.activityExitAnimation = activityExitAnimation;
    }

    public PictureWindowAnimationStyle(@AnimRes int activityEnterAnimation,
                                       @AnimRes int activityExitAnimation,
                                       @AnimRes int activityPreviewEnterAnimation,
                                       @AnimRes int activityPreviewExitAnimation) {
        super();
        this.activityEnterAnimation = activityEnterAnimation;
        this.activityExitAnimation = activityExitAnimation;
        this.activityPreviewEnterAnimation = activityPreviewEnterAnimation;
        this.activityPreviewExitAnimation = activityPreviewExitAnimation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.activityEnterAnimation);
        dest.writeInt(this.activityExitAnimation);
        dest.writeInt(this.activityPreviewEnterAnimation);
        dest.writeInt(this.activityPreviewExitAnimation);
        dest.writeInt(this.activityCropEnterAnimation);
        dest.writeInt(this.activityCropExitAnimation);
    }

    protected PictureWindowAnimationStyle(Parcel in) {
        this.activityEnterAnimation = in.readInt();
        this.activityExitAnimation = in.readInt();
        this.activityPreviewEnterAnimation = in.readInt();
        this.activityPreviewExitAnimation = in.readInt();
        this.activityCropEnterAnimation = in.readInt();
        this.activityCropExitAnimation = in.readInt();
    }

    public static final Creator<PictureWindowAnimationStyle> CREATOR = new Creator<PictureWindowAnimationStyle>() {
        @Override
        public PictureWindowAnimationStyle createFromParcel(Parcel source) {
            return new PictureWindowAnimationStyle(source);
        }

        @Override
        public PictureWindowAnimationStyle[] newArray(int size) {
            return new PictureWindowAnimationStyle[size];
        }
    };
}
