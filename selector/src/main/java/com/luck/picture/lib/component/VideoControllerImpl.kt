package com.luck.picture.lib.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Video MediaPlayer Controller
 */
class VideoControllerImpl : FrameLayout, IPlayerController {
    private lateinit var ivPlay: ImageView
    private lateinit var pbLoading: ProgressBar

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
        inflate(context, R.layout.ps_video_controller, this)
        pbLoading = findViewById(R.id.pb_loading)
        ivPlay = findViewById(R.id.iv_play)
    }

    override fun getViewLoading(): View {
        return pbLoading
    }

    override fun getViewPlay(): ImageView {
        return ivPlay
    }

    override fun getViewFast(): View? {
        return null
    }

    override fun getViewBack(): View? {
        return null
    }

    override fun getSeekBar(): SeekBar? {
        return null
    }

    override fun getViewDuration(): TextView? {
        return null
    }

    override fun getViewCurrentDuration(): TextView? {
        return null
    }
}