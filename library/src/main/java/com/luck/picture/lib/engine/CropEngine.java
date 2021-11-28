package com.luck.picture.lib.engine;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.luck.picture.lib.config.Crop;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author：luck
 * @date：2021/11/23 8:13 下午
 * @describe：CropEngine
 */
public interface CropEngine {

    /**
     * Custom crop image engine
     * <p>
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the {@link LocalMedia} object;
     * <p>
     * 1、If Activity start crop use context;
     * activity.startActivityForResult({@link Crop.REQUEST_CROP})
     * <p>
     * 2、If Fragment start crop use fragment;
     * fragment.startActivityForResult({@link Crop.REQUEST_CROP})
     *
     * </p>
     *
     * @param context  Activity
     * @param fragment Fragment
     * @param media    crop data
     */
    void onStartSingleCrop(Context context, Fragment fragment, LocalMedia media);

    /**
     * Custom crop image engine
     * <p>
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the {@link LocalMedia} object;
     * <p>
     * 1、If Activity start crop use context;
     * activity.startActivityForResult({@link Crop.REQUEST_CROP})
     * <p>
     * 2、If Fragment start crop use fragment;
     * fragment.startActivityForResult({@link Crop.REQUEST_CROP})
     *
     * </p>
     *
     * @param context         Activity
     * @param fragment        Fragment
     * @param firstImageMedia first crop data
     * @param list            crop data
     */
    void onStartMultipleCrop(Context context, Fragment fragment, LocalMedia firstImageMedia, List<LocalMedia> list);
}
