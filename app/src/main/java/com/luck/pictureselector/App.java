package com.luck.pictureselector;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;


/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.pictureselector
 * email：893855882@qq.com
 * data：2017/4/29
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        LeakCanary.install(this);
        //bugly追踪的上报测试时候设置为true
        CrashReport.initCrashReport(getApplicationContext(), "b43cc54bf9", true);
    }
}
