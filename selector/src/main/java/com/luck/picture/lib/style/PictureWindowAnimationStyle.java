package com.luck.picture.lib.style;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnimRes;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2021/11/16 6:41 下午
 * @describe：PictureWindowAnimationStyle
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
     * 默认WindowAnimationStyle
     *
     * @return this
     */
    public static PictureWindowAnimationStyle ofDefaultWindowAnimationStyle() {
        return new PictureWindowAnimationStyle(R.anim.ps_anim_enter, R.anim.ps_anim_exit);
    }


    public PictureWindowAnimationStyle() {

    }
    public PictureWindowAnimationStyle(@AnimRes int activityEnterAnimation,
                                       @AnimRes int activityExitAnimation) {
        this.activityEnterAnimation = activityEnterAnimation;
        this.activityExitAnimation = activityExitAnimation;
        this.activityPreviewEnterAnimation = activityEnterAnimation;
        this.activityPreviewExitAnimation = activityExitAnimation;
    }

    protected PictureWindowAnimationStyle(Parcel in) {
        activityEnterAnimation = in.readInt();
        activityExitAnimation = in.readInt();
        activityPreviewEnterAnimation = in.readInt();
        activityPreviewExitAnimation = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(activityEnterAnimation);
        dest.writeInt(activityExitAnimation);
        dest.writeInt(activityPreviewEnterAnimation);
        dest.writeInt(activityPreviewExitAnimation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PictureWindowAnimationStyle> CREATOR = new Creator<PictureWindowAnimationStyle>() {
        @Override
        public PictureWindowAnimationStyle createFromParcel(Parcel in) {
            return new PictureWindowAnimationStyle(in);
        }

        @Override
        public PictureWindowAnimationStyle[] newArray(int size) {
            return new PictureWindowAnimationStyle[size];
        }
    };

    public int getActivityEnterAnimation() {
        return activityEnterAnimation;
    }

    public void setActivityEnterAnimation(int activityEnterAnimation) {
        this.activityEnterAnimation = activityEnterAnimation;
    }

    public int getActivityExitAnimation() {
        return activityExitAnimation;
    }

    public void setActivityExitAnimation(int activityExitAnimation) {
        this.activityExitAnimation = activityExitAnimation;
    }

    public int getActivityPreviewEnterAnimation() {
        return activityPreviewEnterAnimation;
    }

    public void setActivityPreviewEnterAnimation(int activityPreviewEnterAnimation) {
        this.activityPreviewEnterAnimation = activityPreviewEnterAnimation;
    }

    public int getActivityPreviewExitAnimation() {
        return activityPreviewExitAnimation;
    }

    public void setActivityPreviewExitAnimation(int activityPreviewExitAnimation) {
        this.activityPreviewExitAnimation = activityPreviewExitAnimation;
    }
}
