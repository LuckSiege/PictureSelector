package com.luck.picture.lib.interfaces;

import android.content.Context;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.ResourceSource;

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
     * {@link R.id.ps_fragment_selector}
     * {@link R.id.ps_fragment_preview}
     * The selector is the same, Otherwise, there will be a rush
     * </p>
     *
     * @param context
     * @param resourceSource {@link ResourceSource}
     * @return
     */
    int getLayoutResourceId(Context context, int resourceSource);
}
