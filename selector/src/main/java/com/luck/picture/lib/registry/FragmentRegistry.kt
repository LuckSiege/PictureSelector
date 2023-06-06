package com.luck.picture.lib.registry

import androidx.annotation.NonNull

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：Customizing PictureSelector Fragment
 */
class FragmentRegistry : BaseRegistry() {

    @Synchronized
    override fun <Model> register(@NonNull targetClass: Class<Model>) {
        transcoders.add(Entry(targetClass))
    }

    @Synchronized
    override fun <Model> unregister(@NonNull targetClass: Class<Model>) {
        transcoders.forEach { entry ->
            if (entry.handles(targetClass)) {
                transcoders.remove(entry)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <Model> get(@NonNull targetClass: Class<Model>): Class<Model> {
        transcoders.forEach { entry ->
            if (entry.handles(targetClass)) {
                return entry.fromClass as Class<Model>
            }
        }
        return targetClass
    }
}