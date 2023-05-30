package com.luck.picture.lib.interfaces

/**
 * @author：luck
 * @date：2023/1/13 11:40 上午
 * @describe：OnItemClickListener
 */
interface OnItemClickListener<T> {
    fun onItemClick(position: Int, data: T)
}