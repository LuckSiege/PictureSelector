package com.luck.picture.lib.magical;


/**
 * @author：luck
 * @date：2021/12/15 11:06 上午
 * @describe：OnMagicalViewCallback
 */
public interface OnMagicalViewCallback {
    void onDrag(MagicalView view, float moveX, float moveY);

    void showFinish(MagicalView mojitoView, boolean showImmediately);

    void onMojitoViewFinish();

    void onRelease(boolean isToMax, boolean isToMin);

    void onLock(boolean isLock);

    void onLongImageMove(float ratio);
}
