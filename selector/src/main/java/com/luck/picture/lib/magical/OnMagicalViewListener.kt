package com.luck.picture.lib.magical

/**
 * @author：luck
 * @date：2021/12/15 11:06 上午
 * @describe：OnMagicalViewListener
 */
interface OnMagicalViewListener {
    fun onBeginBackMinAnim()
    fun onBeginBackMinMagicalFinish(isResetSize: Boolean)
    fun onBeginMagicalAnimComplete(mojitoView: MagicalView, showImmediately: Boolean)
    fun onBackgroundAlpha(alpha: Float)
    fun onMagicalViewFinish()
}