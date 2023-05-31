package com.luck.picture.lib.player

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player controller
 */
class VideoController : FrameLayout, Controller {
    private lateinit var pbLoading: ProgressBar
    private lateinit var ivPlayVideo: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.ps_video_controller, this)
        pbLoading = findViewById(R.id.pb_loading)
        ivPlayVideo = findViewById(R.id.iv_play_video)
    }

    override fun getViewPlay(): View {
        return ivPlayVideo
    }

    override fun getViewLoading(): View {
        return pbLoading
    }
}