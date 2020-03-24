package com.luck.picture.lib.engine;

import android.content.Context;

/**
 * @author：luck
 * @date：2020-03-24 09:36
 * @describe：图片缓存引擎
 */
public interface CacheResourcesEngine {
    /**
     * 获取缓存路径
     *
     * @param context
     * @param url
     */
    String onCachePath(Context context, String url);
}
