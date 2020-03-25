package com.luck.pictureselector;

import android.content.Context;

import com.luck.picture.lib.engine.CacheResourcesEngine;

import java.io.File;

/**
 * @author：luck
 * @date：2020-03-24 09:48
 * @describe：GlideCacheResources管理类
 */
public class GlideCacheResourcesEngine implements CacheResourcesEngine {
    /**
     * glide版本号,请根据用户集成为准 这里只是模拟
     */
    private final static int GLIDE_VERSION = 4;

    @Override
    public String onCachePath(Context context, String url) {
        File cacheFile;
        if (GLIDE_VERSION >= 4) {
            // Glide 4.x
            cacheFile = ImageCacheUtils.getCacheFileTo4x(context, url);
        } else {
            // Glide 3.x
            cacheFile = ImageCacheUtils.getCacheFileTo3x(context, url);
        }
        return cacheFile != null ? cacheFile.getAbsolutePath() : "";
    }


    private GlideCacheResourcesEngine() {
    }

    private static GlideCacheResourcesEngine instance;

    public static GlideCacheResourcesEngine createCacheEngine() {
        if (null == instance) {
            synchronized (GlideCacheResourcesEngine.class) {
                if (null == instance) {
                    instance = new GlideCacheResourcesEngine();
                }
            }
        }
        return instance;
    }
}
