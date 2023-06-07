package com.luck.picture.lib.registry

import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.adapter.base.BaseListViewHolder
import com.luck.picture.lib.adapter.base.BaseMediaListAdapter
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.base.BaseSelectorFragment

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：Used by users to customize PictureSelector
 */
class Registry : BaseRegistry() {
    private var captureRegistry = CaptureRegistry()
    private var adapterRegistry = AdapterRegistry()
    private var fragmentRegistry = FragmentRegistry()
    private var viewHolderRegistry = ViewHolderRegistry()

    override fun <Model> register(targetClass: Class<Model>) {
        when {
            isAssignableFromCapture(targetClass) -> {
                captureRegistry.register(targetClass)
            }
            isAssignableFromFragment(targetClass) -> {
                fragmentRegistry.register(targetClass)
            }
            isAssignableFromHolder(targetClass) -> {
                viewHolderRegistry.register(targetClass)
            }
            isAssignableFromAdapter(targetClass) -> {
                adapterRegistry.register(targetClass)
            }
        }
    }

    override fun <Model> unregister(targetClass: Class<Model>) {
        when {
            isAssignableFromCapture(targetClass) -> {
                captureRegistry.unregister(targetClass)
            }
            isAssignableFromFragment(targetClass) -> {
                fragmentRegistry.unregister(targetClass)
            }
            isAssignableFromHolder(targetClass) -> {
                viewHolderRegistry.unregister(targetClass)
            }
            isAssignableFromAdapter(targetClass) -> {
                adapterRegistry.unregister(targetClass)
            }
        }
    }

    override fun <Model> get(targetClass: Class<Model>): Class<Model> {
        when {
            isAssignableFromCapture(targetClass) -> {
                return captureRegistry.get(targetClass)
            }
            isAssignableFromFragment(targetClass) -> {
                return fragmentRegistry.get(targetClass)
            }
            isAssignableFromHolder(targetClass) -> {
                return viewHolderRegistry.get(targetClass)
            }
            isAssignableFromAdapter(targetClass) -> {
                return adapterRegistry.get(targetClass)
            }
        }
        throw IllegalStateException("$targetClass not found")
    }

    fun getCaptureRegistry(): CaptureRegistry {
        return captureRegistry
    }

    fun getAdapterRegistry(): AdapterRegistry {
        return adapterRegistry
    }

    fun getFragmentRegistry(): FragmentRegistry {
        return fragmentRegistry
    }

    fun getViewHolderRegistry(): ViewHolderRegistry {
        return viewHolderRegistry
    }

    private fun <V> isAssignableFromCapture(targetClass: Class<V>): Boolean {
        return VideoCaptureComponent::class.java.isAssignableFrom(targetClass)
                || ImageCaptureComponent::class.java.isAssignableFrom(targetClass)
                || SoundCaptureComponent::class.java.isAssignableFrom(targetClass)
    }

    private fun <V> isAssignableFromHolder(targetClass: Class<V>): Boolean {
        return BaseListViewHolder::class.java.isAssignableFrom(targetClass)
                || BasePreviewMediaHolder::class.java.isAssignableFrom(targetClass)
    }

    private fun <V> isAssignableFromFragment(targetClass: Class<V>): Boolean {
        return BaseSelectorFragment::class.java.isAssignableFrom(targetClass)
    }

    private fun <A> isAssignableFromAdapter(targetClass: Class<A>): Boolean {
        return BaseMediaListAdapter::class.java.isAssignableFrom(targetClass)
                || MediaPreviewAdapter::class.java.isAssignableFrom(targetClass)
    }

    override fun clear() {
        getCaptureRegistry().clear()
        getAdapterRegistry().clear()
        getFragmentRegistry().clear()
        getViewHolderRegistry().clear()
    }
}