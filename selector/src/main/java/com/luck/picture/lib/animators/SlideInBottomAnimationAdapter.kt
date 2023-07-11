package com.luck.picture.lib.animators

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：luck
 * @date：2020-04-18 14:19
 * @describe：SlideInBottomAnimationAdapter
 */
class SlideInBottomAnimationAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    BaseAnimationAdapter(adapter) {
    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(
            ObjectAnimator.ofFloat(view, "translationY", view.measuredHeight.toFloat(), 0f)
        )
    }
}