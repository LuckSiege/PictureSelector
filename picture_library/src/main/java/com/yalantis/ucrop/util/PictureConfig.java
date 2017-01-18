package com.yalantis.ucrop.util;

import android.app.Activity;
import android.content.Intent;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.ui.AlbumDirectoryActivity;
import com.yalantis.ucrop.ui.ExternalPreviewActivity;
import com.yalantis.ucrop.ui.ImageGridActivity;

import java.io.Serializable;
import java.util.List;


/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.util
 * email：893855882@qq.com
 * data：17/1/5
 */
public class PictureConfig {
    public static OnSelectResultCallback resultCallback;
    public static FunctionConfig config;

    public static OnSelectResultCallback getResultCallback() {
        return resultCallback;
    }

    public static void init(FunctionConfig functionConfig) {
        config = functionConfig;
    }

    /**
     * 启动相册
     *
     * @param activity        上下文
     * @param mResultCallback 回调函数
     */
    public static void openPhoto(Activity activity, OnSelectResultCallback mResultCallback) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        if (config == null) {
            config = new FunctionConfig();
        }
        // 这里仿ios微信相册启动模式
        Intent intent1 = new Intent(activity, AlbumDirectoryActivity.class);
        Intent intent2 = new Intent(activity, ImageGridActivity.class);
        Intent[] intents = new Intent[2];
        intents[0] = intent1;
        intents[1] = intent2;
        intent1.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        intent2.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        activity.startActivities(intents);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
        // 绑定图片接口回调函数事件
        resultCallback = mResultCallback;
    }


    /**
     * 外部图片预览
     *
     * @param activity
     * @param position
     * @param medias
     */
    public static void externalPicturePreview(Activity activity, int position, List<LocalMedia> medias) {
        if (medias != null && medias.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
            intent.putExtra(FunctionConfig.EXTRA_POSITION, position);
            intent.setClass(activity, ExternalPreviewActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.toast_enter, 0);
        }
    }


    /**
     * 处理结果
     */
    public static interface OnSelectResultCallback {
        /**
         * 处理成功
         *
         * @param resultList
         */
        public void onSelectSuccess(List<LocalMedia> resultList);

    }
}
