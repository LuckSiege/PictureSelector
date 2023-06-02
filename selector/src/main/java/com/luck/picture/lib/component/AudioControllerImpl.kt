package com.luck.picture.lib.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Audio MediaPlayer Controller
 */
class AudioControllerImpl : RelativeLayout, IPlayerController {
    private lateinit var ivPlay: ImageView
    private lateinit var ivPlayBack: ImageView
    private lateinit var ivPlayFast: ImageView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalDuration: TextView
    private lateinit var seekBar: SeekBar

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.ps_audio_controller, this)
        ivPlay = findViewById(R.id.iv_play_audio)
        ivPlayBack = findViewById(R.id.iv_play_back)
        ivPlayFast = findViewById(R.id.iv_play_fast)
        seekBar = findViewById(R.id.seek_bar)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvTotalDuration = findViewById(R.id.tv_total_duration)
    }

    override fun getViewLoading(): View? {
        return null
    }

    override fun getViewPlay(): ImageView {
        return ivPlay
    }

    override fun getViewFast(): View {
        return ivPlayFast
    }

    override fun getViewBack(): View {
        return ivPlayBack
    }

    override fun getSeekBar(): SeekBar {
        return seekBar
    }

    override fun getViewDuration(): TextView {
        return tvTotalDuration
    }

    override fun getViewCurrentDuration(): TextView {
        return tvCurrentTime
    }
}