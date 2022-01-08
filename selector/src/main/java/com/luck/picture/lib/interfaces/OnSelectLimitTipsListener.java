package com.luck.picture.lib.interfaces;

import android.content.Context;

import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectLimitType;

/**
 * @author：luck
 * @date：2022/1/8 2:12 下午
 * @describe：OnSelectLimitTipsListener
 */
public interface OnSelectLimitTipsListener {
    /**
     * Custom limit tips
     *
     * @param context
     * @param config    PictureSelectionConfig
     * @param limitType Use {@link SelectLimitType}
     * @return If true is returned, the user needs to customize the implementation prompt content，
     * Otherwise, use the system default prompt
     */
    boolean onSelectLimitTips(Context context, PictureSelectionConfig config, int limitType);
}
