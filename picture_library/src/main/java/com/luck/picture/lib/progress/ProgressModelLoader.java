package com.luck.picture.lib.progress;

import android.content.Context;
import android.os.Handler;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

public class ProgressModelLoader implements StreamModelLoader<String> {

    private final ModelCache<String, String> modelCache;
    private final Handler handler;

    public ProgressModelLoader(Handler handler) {
        this(null, handler);
    }

    public ProgressModelLoader(ModelCache<String, String> modelCache) {
        this(modelCache, null);
    }

    public ProgressModelLoader(ModelCache<String, String> modelCache, Handler handler) {
        this.modelCache = modelCache;
        this.handler = handler;
    }


    @Override
    public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
        String result = null;
        if (modelCache != null) {
            result = modelCache.get(model, width, height);
        }
        if (result == null) {
            result = model;
            if (modelCache != null) {
                modelCache.put(model, width, height, result);
            }
        }
        return new ProgressDataFetcher(result, handler);
    }

    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        private final ModelCache<String, String> mModelCache = new ModelCache<>(500);

        @Override
        public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new ProgressModelLoader(mModelCache);
        }

        @Override
        public void teardown() {

        }
    }

}
