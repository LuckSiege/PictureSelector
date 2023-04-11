package com.luck.picture.lib.interfaces;

import android.content.Context;

import androidx.annotation.Nullable;

import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2022/1/8 2:12 下午
 * @describe：OnSelectLimitTipsListener
 */
public interface OnSelectLimitTipsListener {
    /**
     * Custom limit tips
     *
     * @param media Current Selection {@link LocalMedia}
     * @param config    PictureSelectionConfig
     * @param limitType Use {@link SelectLimitType}
     * @return If true is returned, the user needs to customize the implementation prompt content，
     * Otherwise, use the system default prompt
     */
    boolean onSelectLimitTips(Context context, @Nullable LocalMedia media, SelectorConfig config, int limitType);
}
