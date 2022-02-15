package com.luck.pictureselector;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;

import com.luck.picture.lib.app.IApp;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.engine.PictureSelectorEngine;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.ImageLoaderFactory;
import coil.decode.GifDecoder;
import coil.decode.ImageDecoderDecoder;
import coil.decode.VideoFrameDecoder;
import coil.util.CoilUtils;
import okhttp3.OkHttpClient;


/**
 * @author：luck
 * @date：2019-12-03 22:53
 * @describe：Application
 */

public class App extends Application implements IApp, CameraXConfig.Provider, ImageLoaderFactory {
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        PictureAppMaster.getInstance().setApp(this);
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public PictureSelectorEngine getPictureSelectorEngine() {
        return new PictureSelectorEngineImp();
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
                .setMinimumLoggingLevel(Log.ERROR).build();
    }

    @NonNull
    @Override
    public ImageLoader newImageLoader() {
        ComponentRegistry registry = new ComponentRegistry.Builder()
                .add(SDK_INT >= 28 ? new ImageDecoderDecoder(getAppContext()) : new GifDecoder())
                .add(new VideoFrameDecoder(getAppContext()))
                .build();
        return new ImageLoader.Builder(getAppContext())
                .componentRegistry(registry)
                .crossfade(true)
                .okHttpClient(new OkHttpClient.Builder()
                        .cache(CoilUtils.createDefaultCache(getAppContext())).build())
                .availableMemoryPercentage(0.5)
                .allowHardware(false)
                .allowRgb565(true)
                .build();
    }
}
