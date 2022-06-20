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

import java.io.File;

import coil.ComponentRegistry;
import coil.ImageLoader;
import coil.ImageLoaderFactory;
import coil.decode.GifDecoder;
import coil.decode.ImageDecoderDecoder;
import coil.decode.VideoFrameDecoder;
import coil.disk.DiskCache;
import coil.memory.MemoryCache;


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
        ImageLoader.Builder imageLoader = new ImageLoader.Builder(getAppContext());
        ComponentRegistry.Builder newBuilder = new ComponentRegistry().newBuilder();
        newBuilder.add(new VideoFrameDecoder.Factory());
        if (SDK_INT >= 28) {
            newBuilder.add(new ImageDecoderDecoder.Factory());
        } else {
            newBuilder.add(new GifDecoder.Factory());
        }
        ComponentRegistry componentRegistry = newBuilder.build();
        imageLoader.components(componentRegistry);
        imageLoader.memoryCache(new MemoryCache.Builder(getAppContext())
                .maxSizePercent(0.25).build());
        imageLoader.diskCache(new DiskCache.Builder()
                .directory(new File(getCacheDir(), "image_cache"))
                .maxSizePercent(0.02)
                .build());
        return imageLoader.build();
    }
}
