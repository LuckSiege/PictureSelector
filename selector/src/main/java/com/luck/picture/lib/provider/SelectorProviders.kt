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
    private val configQueue = LinkedList<SelectorConfig>()

    fun addConfigQueue(config: SelectorConfig) {
        configQueue.add(config)
    }

    fun getConfig(): SelectorConfig {
        return if (configQueue.size > 0) configQueue.last else SelectorConfig()
    }

    fun destroy() {
        val config = getConfig()
        config.destroy()
        configQueue.remove(config)
        TempDataProvider.getInstance().reset()
        SelectorLogUtils.info("${System.currentTimeMillis()}:销毁")
    }

    fun reset() {
        for (i in configQueue.indices) {
            configQueue[i].destroy()
        }
        configQueue.clear()
    }

    companion object {
        fun getInstance() = InstanceHelper.instance
    }

    object InstanceHelper {
        val instance = SelectorProviders()
    }
}