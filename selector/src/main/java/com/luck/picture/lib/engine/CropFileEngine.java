package com.luck.picture.lib.engine;


import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.luck.picture.lib.config.Crop;
import com.luck.picture.lib.config.CustomIntentKey;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2021/11/23 8:13 下午
 * @describe：CropFileEngine
 */
public interface CropFileEngine {

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
     * <p>
     * 3、If you implement your own clipping function, you need to assign the following values in
     * Intent.putExtra {@link CustomIntentKey}
     *
     * </p>
     *
     * @param fragment       Fragment
     * @param srcUri         current src Uri
     * @param destinationUri current output src Uri
     * @param dataSource     crop data
     * @param requestCode    Activity result code or fragment result code
     */
    void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode);

}
