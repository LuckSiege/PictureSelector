package com.luck.picture.lib.magical;


/**
 * @author：luck
 * @date：2021/12/15 11:06 上午
 * @describe：OnMagicalViewCallback
 */
public interface OnMagicalViewCallback {

    void onBeginBackToMin(boolean isResetSize);

    void showFinish(MagicalView mojitoView, boolean showImmediately);

    void onMojitoViewFinish();
}
