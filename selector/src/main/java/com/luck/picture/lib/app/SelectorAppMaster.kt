package com.luck.picture.lib.app

import android.content.Context

/**
 * @author：luck
 * @date：2019-12-03 15:12
 * @describe：SelectorAppMaster
 */
class SelectorAppMaster : IApp {
    override fun getAppContext(): Context? {
        return app?.getAppContext()
    }

    override fun getSelectorEngine(): SelectorEngine? {
        return app?.getSelectorEngine()
    }

    companion object {
        fun getInstance() = InstanceHelper.instance
    }

    object InstanceHelper {
        val instance = SelectorAppMaster()
    }

    private var app: IApp? = null

    fun setApp(app: IApp?) {
        this.app = app
    }

    fun getApp(): IApp? {
        return app
    }
}