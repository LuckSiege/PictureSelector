package com.luck.picture.lib.app

import android.content.Context

/**
 * @author：luck
 * @date：2019-12-03 15:14
 * @describe：IApp
 */
interface IApp {
    fun getAppContext(): Context?
    fun getSelectorEngine(): SelectorEngine?
}