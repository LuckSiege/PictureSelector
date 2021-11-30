package com.luck.picture.lib.basic;

import androidx.fragment.app.Fragment;

/**
 * @author：luck
 * @date：2021/11/22 4:38 下午
 * @describe：IBridgePictureBehavior
 */
public interface IBridgePictureBehavior {

    /**
     * inject fragment
     *
     * @param tag      fragment tag
     * @param fragment inject fragment
     */
    void injectFragmentFromScreen(String tag, Fragment fragment);

    /**
     * finish activity
     */
    void onFinish();

    /**
     * immediate finish activity
     */
    void onImmediateFinish();
}
