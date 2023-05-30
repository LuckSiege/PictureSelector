package com.luck.picture.lib.interfaces

/**
 * @author：luck
 * @date：2022/7/1 23:25 下午
 * @describe：OnPlayerListener
 */
interface OnPlayerListener {
    /**
     * player error
     */
    fun onPlayerError()

    /**
     * playing
     */
    fun onPlayerReady()

    /**
     * preparing to play
     */
    fun onPlayerLoading()

    /**
     * end of playback
     */
    fun onPlayerComplete()
}