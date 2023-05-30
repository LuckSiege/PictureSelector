package com.luck.picture.lib.player

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：Player controller
 */
class VideoController : FrameLayout, Controller {

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
    }

    @SuppressLint("WrongViewCast")
    override fun getVideoPlay(): View {
        return findViewById(R.id.iv_play_video)
    }

    @SuppressLint("WrongViewCast")
    override fun getVideoLoading(): View {
        return findViewById(R.id.pb_loading)
    }
}