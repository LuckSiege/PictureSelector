package com.luck.picture.lib.style;

import androidx.annotation.AnimRes;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2021/11/16 6:41 下午
 * @describe：PictureWindowAnimationStyle
 */
public class PictureWindowAnimationStyle {
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
