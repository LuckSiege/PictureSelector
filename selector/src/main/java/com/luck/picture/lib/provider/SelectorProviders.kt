package com.luck.picture.lib.provider

import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.utils.SelectorLogUtils
import java.util.*

/**
 * @author：luck
 * @date：2023/3/31 4:15 下午
 * @describe：SelectorProviders
 */
class SelectorProviders {
    private val selectionConfigsQueue = LinkedList<SelectorConfig>()

    fun addSelectorConfigQueue(config: SelectorConfig) {
        selectionConfigsQueue.add(config)
    }

    fun getSelectorConfig(): SelectorConfig {
        return if (selectionConfigsQueue.size > 0) selectionConfigsQueue.last else SelectorConfig()
    }

    fun destroy() {
        val selectorConfig = getSelectorConfig()
        selectorConfig.destroy()
        selectionConfigsQueue.remove(selectorConfig)
        SelectorLogUtils.info("${System.currentTimeMillis()}:销毁")
    }

    fun reset() {
        for (i in selectionConfigsQueue.indices) {
            selectionConfigsQueue[i].destroy()
        }
        selectionConfigsQueue.clear()
    }

    companion object {
        fun getInstance() = InstanceHelper.sSingle
    }

    object InstanceHelper {
        val sSingle = SelectorProviders()
    }
}