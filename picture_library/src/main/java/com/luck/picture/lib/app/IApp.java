package com.luck.picture.lib.app;

import android.content.Context;

import com.luck.picture.lib.engine.PictureSelectorEngine;

/**
 * @author：luck
 * @date：2019-12-03 15:14
 * @describe：IApp
 */
public interface IApp {
    /**
     * 获取全局应用Application
     *
     * @return
     */
    Context getAppContext();

    PictureSelectorEngine getPictureSelectorEngine();
}
