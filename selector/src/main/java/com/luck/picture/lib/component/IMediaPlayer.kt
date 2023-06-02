package com.luck.picture.lib.component

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player General Function Behavior
 */
interface IMediaPlayer : IPreviewCoverComponent {
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun onStart(path: String, isLoopAutoPlay: Boolean)
    fun onResume()
    fun onPause()
    fun isPlaying(): Boolean
    fun getController(): IPlayerController
}