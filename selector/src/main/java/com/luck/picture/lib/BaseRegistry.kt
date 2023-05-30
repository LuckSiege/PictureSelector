package com.luck.picture.lib

import androidx.annotation.NonNull
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：BaseRegistry
 */
abstract class BaseRegistry {
    var transcoders = CopyOnWriteArrayList<Entry<*>>()
    abstract fun <Model> register(@NonNull targetClass: Class<Model>)
    abstract fun <Model> unregister(@NonNull targetClass: Class<Model>)
    abstract fun <Model> get(@NonNull targetClass: Class<Model>): Class<Model>
    class Entry<Model> constructor(var fromClass: Class<Model>) {
        fun handles(fromClass: Class<*>): Boolean {
            return fromClass.isAssignableFrom(this.fromClass)
        }
    }

    open fun clear() {
        if (transcoders.isNotEmpty()) {
            transcoders.clear()
        }
    }
}