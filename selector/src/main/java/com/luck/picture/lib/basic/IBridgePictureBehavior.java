package com.luck.picture.lib.basic;

import androidx.annotation.Nullable;

/**
 * @author：luck
 * @date：2021/11/22 4:38 下午
 * @describe：IBridgePictureBehavior
 */
public interface IBridgePictureBehavior {


    /**
     * finish activity
     *
     * @param isForcedExit true; Direct forced exit;
     *                     false; Return according to the task stack
     * @param result data
     */
    void onSelectFinish(boolean isForcedExit, @Nullable PictureCommonFragment.SelectorResult result);

}
