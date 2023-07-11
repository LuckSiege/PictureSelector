package com.luck.picture.lib.animators

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.luck.picture.lib.animators.ViewHelper.clear
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

/**
 * @author：luck
 * @date：2020-04-18 14:12
 * @describe：BaseAnimationAdapter
 */
abstract class BaseAnimationAdapter(private val wrapAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mDuration = 250
    private var mInterpolator: Interpolator = LinearInterpolator()
    private var mLastPosition = -1
    private var isFirstOnly = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return wrapAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun registerAdapterDataObserver(observer: AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        wrapAdapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        wrapAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        wrapAdapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        wrapAdapter.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        wrapAdapter.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        wrapAdapter.onViewDetachedFromWindow(holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        wrapAdapter.onBindViewHolder(holder, position)
        val adapterPosition = holder.adapterPosition
        if (!isFirstOnly || adapterPosition > mLastPosition) {
            for (anim in getAnimators(holder.itemView)) {
                anim.setDuration(mDuration.toLong()).start()
                anim.interpolator = mInterpolator
            }
            mLastPosition = adapterPosition
        } else {
            clear(holder.itemView)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        wrapAdapter.onViewRecycled(holder)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return wrapAdapter.itemCount
    }

    fun setDuration(duration: Int) {
        mDuration = duration
    }

    fun setInterpolator(interpolator: Interpolator) {
        mInterpolator = interpolator
    }

    fun setStartPosition(start: Int) {
        mLastPosition = start
    }

    protected abstract fun getAnimators(view: View): Array<Animator>

    fun setFirstOnly(firstOnly: Boolean) {
        isFirstOnly = firstOnly
    }

    override fun getItemViewType(position: Int): Int {
        return wrapAdapter.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return wrapAdapter.getItemId(position)
    }
}