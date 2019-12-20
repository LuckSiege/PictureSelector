package com.luck.picture.lib.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.luck.picture.lib.PicturePreviewActivity;
import com.luck.picture.lib.PictureSelectorPreviewWeChatStyleActivity;
import com.luck.picture.lib.PictureVideoPlayActivity;

/**
 * @author：luck
 * @date：2019-11-23 18:57
 * @describe：Activity跳转
 */
public class JumpUtils {
    /**
     * 启动视频播放页面
     *
     * @param context
     * @param bundle
     */
    public static void startPictureVideoPlayActivity(Context context, Bundle bundle, int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(context, PictureVideoPlayActivity.class);
            intent.putExtras(bundle);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * 启动预览界面
     *
     * @param context
     * @param isWeChatStyle
     * @param bundle
     * @param requestCode
     */
    public static void startPicturePreviewActivity(Context context, boolean isWeChatStyle, Bundle bundle, int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(context, isWeChatStyle ? PictureSelectorPreviewWeChatStyleActivity.class : PicturePreviewActivity.class);
            intent.putExtras(bundle);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        }
    }
}
