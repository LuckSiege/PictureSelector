package com.luck.picture.lib.magical;


/**
 * @author：luck
 * @date：2021/12/15 11:06 上午
 * @describe：OnMagicalViewCallback
 */
public interface OnMagicalViewCallback {

    void onBeginBackMinAnim();

    void onBeginBackMinMagicalFinish(boolean isResetSize);

    void onBackgroundAlpha(float alpha);

    void onBeginMagicalAnimComplete(MagicalView mojitoView, boolean showImmediately);

    void onMagicalViewFinish();
}
