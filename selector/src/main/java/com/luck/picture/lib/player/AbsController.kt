package com.luck.picture.lib.player

import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Video Player Controller
 */
interface AbsController {
    fun getViewPlay(): ImageView?
    fun getSeekBar(): SeekBar?
    fun getFast(): ImageView?
    fun getBack(): ImageView?
    fun getTvDuration(): TextView?
    fun getTvCurrentDuration(): TextView?
    fun setDataSource(media: LocalMedia)
    fun setIMediaPlayer(mediaPlayer: IMediaPlayer)
    fun start()
    fun stop(isReset: Boolean)
    fun setOnPlayStateListener(l: OnPlayStateListener?)
    fun setOnSeekBarChangeListener(l: SeekBar.OnSeekBarChangeListener?)
    interface OnPlayStateListener {
        fun onPlayState(isPlaying: Boolean)
    }
}