package com.yalantis.ucrop;

/**
 * @author：luck
 * @date：2021/12/2 10:23 上午
 * @describe：UCropDevelopConfig
 */
public final class UCropDevelopConfig {
    /**
     * 图片加载引擎
     */
    public static UCropImageEngine imageEngine;

    /**
     * 释放监听器
     */
    public static void destroy() {
        UCropDevelopConfig.imageEngine = null;
    }
}
