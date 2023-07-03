package com.luck.picture.lib.registry

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

    override fun clear() {
        captureRegistry.clear()
        adapterRegistry.clear()
        fragmentRegistry.clear()
        viewHolderRegistry.clear()
    }
}