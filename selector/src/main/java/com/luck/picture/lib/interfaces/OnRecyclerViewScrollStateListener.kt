package com.luck.picture.lib.interfaces

/**
 * @author：luck
 * @date：2020-04-14 18:44
 * @describe：OnRecyclerViewScrollStateListener
 */
interface OnRecyclerViewScrollStateListener {
    /**
     * RecyclerView Scroll Fast
     */
    fun onScrollFast()

    /**
     * RecyclerView Scroll Slow
     */
    fun onScrollSlow()
}