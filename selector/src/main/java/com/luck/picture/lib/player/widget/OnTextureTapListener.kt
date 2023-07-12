package com.luck.picture.lib.player.widget

import android.view.TextureView

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：A callback to be invoked when the Photo is tapped with a single tap.
 */
interface OnTextureTapListener {
    /**
     * A callback to receive where the user taps on a photo. You will only receive a callback if
     * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
     *
     * @param view ImageView the user tapped.
     * @param x    where the user tapped from the of the Drawable, as percentage of the
     * Drawable width.
     * @param y    where the user tapped from the top of the Drawable, as percentage of the
     * Drawable height.
     */
    fun onTextureTap(view: TextureView?, x: Float, y: Float)
}