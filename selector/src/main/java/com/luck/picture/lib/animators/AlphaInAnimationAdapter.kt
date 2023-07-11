package com.luck.picture.lib.animators

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import kotlin.jvm.JvmOverloads
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：luck
 * @date：2020-04-18 14:11
 * @describe：AlphaInAnimationAdapter
 */
class AlphaInAnimationAdapter @JvmOverloads constructor(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    private val mFrom: Float = DEFAULT_ALPHA_FROM
) : BaseAnimationAdapter(adapter) {

    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f))
    }

    companion object {
        private const val DEFAULT_ALPHA_FROM = 0f
    }
}