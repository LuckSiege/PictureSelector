package com.luck.picture.lib.interfaces;

import android.content.Context;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.InjectResourceSource;

/**
 * @author：luck
 * @date：2021/12/23 10:33 上午
 * @describe：OnInjectLayoutResourceListener
 */
public interface OnInjectLayoutResourceListener {
    /**
     * inject custom layout resource id
     * <p>
     * The layout ID must be the same as
     * {@link R.layout.ps_fragment_selector}
     * {@link R.layout.ps_fragment_preview}
     * {@link R.layout.ps_item_grid_image}
     * {@link R.layout.ps_item_grid_video}
     * {@link R.layout.ps_item_grid_audio}
     * {@link R.layout.ps_album_folder_item}
     * {@link R.layout.ps_preview_image}
     * {@link R.layout.ps_preview_video}
     * <p>
     * The layout can be overloaded to implement differences on the UI, but the view ID cannot be changed
     * </p>
     *
     * @param context
     * @param resourceSource {@link InjectResourceSource}
     * @return
     */
    int getLayoutResourceId(Context context, int resourceSource);
}
