package com.luck.picture.lib.player.widget

import android.view.TextureView

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Callback when the user tapped outside of the photo
 */
interface OnOutsideTextureTapListener {
    /**
     * The outside of the photo has been tapped
     */
    fun onOutsideTextureTap(imageView: TextureView?)
}