package com.luck.picture.lib.app;

import android.content.Context;

/**
 * @author：luck
 * @date：2019-12-03 15:12
 * @describe：获取一些全局所需参数
 */
public class PictureAppMaster implements IApp {


    @Override
    public Context getAppContext() {
        if (app == null) {
            return null;
        }
        return app.getAppContext();
    }


    private PictureAppMaster() {
    }

    private static PictureAppMaster mInstance;

    public static PictureAppMaster getInstance() {
        if (mInstance == null) {
            synchronized (PictureAppMaster.class) {
                if (mInstance == null) {
                    mInstance = new PictureAppMaster();
                }
            }
        }
        return mInstance;
    }

    private IApp app;

    public void setApp(IApp app) {
        this.app = app;
    }

    public IApp getApp() {
        return app;
    }
}
