package com.luck.picture.lib.player

import com.luck.picture.lib.interfaces.OnPlayerListener

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player General Function Behavior
 */
interface IMediaPlayer {
    fun initMediaPlayer(l: OnPlayerListener?)
    fun onStart(path: String, isLoopAutoPlay: Boolean)
    fun onResume()
    fun onPause()
    fun isPlaying(): Boolean
    fun destroy()
}