package com.luck.picture.lib.config;

import java.util.LinkedList;

/**
 * @author：luck
 * @date：2023/3/31 4:15 下午
 * @describe：SelectorProviders
 */
public class SelectorProviders {

    private final LinkedList<SelectorConfig> selectionConfigsQueue = new LinkedList<>();

    public void addSelectorConfigQueue(SelectorConfig config) {
        selectionConfigsQueue.add(config);
    }

    public SelectorConfig getSelectorConfig() {
        return selectionConfigsQueue.size() > 0 ? selectionConfigsQueue.getLast() : new SelectorConfig();
    }

    public void destroy() {
        SelectorConfig selectorConfig = getSelectorConfig();
        if (selectorConfig != null) {
            selectorConfig.destroy();
            selectionConfigsQueue.remove(selectorConfig);
        }
    }

    public void reset() {
        for (int i = 0; i < selectionConfigsQueue.size(); i++) {
            SelectorConfig selectorConfig = selectionConfigsQueue.get(i);
            if (selectorConfig != null) {
                selectorConfig.destroy();
            }
        }
        selectionConfigsQueue.clear();
    }

    private static volatile SelectorProviders selectorProviders;

    public static SelectorProviders getInstance() {
        if (selectorProviders == null) {
            synchronized (SelectorProviders.class) {
                if (selectorProviders == null) {
                    selectorProviders = new SelectorProviders();
                }
            }
        }
        return selectorProviders;
    }
}
