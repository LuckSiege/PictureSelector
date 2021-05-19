package com.luck.pictureselector;

import android.util.Log;

import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.PictureSelectorEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.util.List;

/**
 * @author：luck
 * @date：2020/4/22 12:15 PM
 * @describe：PictureSelectorEngineImp
 */
public class PictureSelectorEngineImp implements PictureSelectorEngine {
    private static final String TAG = PictureSelectorEngineImp.class.getSimpleName();

    @Override
    public ImageEngine createEngine() {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致ImageEngine被回收
        // 重新创建图片加载引擎
        return GlideEngine.createGlideEngine();
    }

    @Override
    public OnResultCallbackListener<LocalMedia> getResultCallbackListener() {
        return new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List<LocalMedia> result) {
                // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致OnResultCallbackListener被回收
                // 可以在这里进行一些补救措施，通过广播或其他方式将结果推送到相应页面，防止结果丢失的情况
                Log.i(TAG, "onResult:" + result.size());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "PictureSelector onCancel");
            }
        };
    }
}
