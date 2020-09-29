package com.luck.picture.lib.engine;

import android.content.Context;

/**
 * @author：luck
 * @date：2020-03-24 09:36
 * @describe：CacheResourcesEngine
 */
public interface CacheResourcesEngine {
    /**
     * Get the cache path
     *
     * @param context
     * @param url
     */
    String onCachePath(Context context, String url);
}
