package com.luck.pictureselector;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.basic.IBridgePictureBehavior;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureContextWrapper;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/12/20 1:40 下午
 * @describe：InjectFragmentActivity
 */
public class InjectFragmentActivity extends AppCompatActivity implements IBridgePictureBehavior {
    private final static String TAG = "PictureSelectorTag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_fragment);
        findViewById(R.id.tvb_inject_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(v.getContext())
                        .openGallery(SelectMimeType.ofAll())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .build(R.id.fragment_container, new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                analyticalSelectResults(result);
                            }

                            @Override
                            public void onCancel() {
                                Log.i(TAG, "onCancel");
                            }
                        });
            }
        });
    }

    @Override
    public void onSelectFinish(boolean isForcedExit, PictureCommonFragment.SelectorResult result) {
        if (isForcedExit) {
            exit();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (ActivityCompatHelper.checkRootFragment(this)) {
            exit();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void exit() {
        if (SdkVersionUtils.isQ()) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
        finish();
        PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
        overridePendingTransition(0, windowAnimationStyle.activityExitAnimation);
        PictureSelectionConfig.destroy();
    }

    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(PictureAppMaster.getInstance().getAppContext(), media.getPath());
                    media.setWidth(videoExtraInfo.getWidth());
                    media.setHeight(videoExtraInfo.getHeight());
                }
            }
            Log.i(TAG, "文件名: " + media.getFileName());
            Log.i(TAG, "是否压缩:" + media.isCompressed());
            Log.i(TAG, "压缩:" + media.getCompressPath());
            Log.i(TAG, "原图:" + media.getPath());
            Log.i(TAG, "绝对路径:" + media.getRealPath());
            Log.i(TAG, "是否裁剪:" + media.isCut());
            Log.i(TAG, "裁剪:" + media.getCutPath());
            Log.i(TAG, "是否开启原图:" + media.isOriginal());
            Log.i(TAG, "原图路径:" + media.getOriginalPath());
            Log.i(TAG, "沙盒路径:" + media.getSandboxPath());
            Log.i(TAG, "原始宽高: " + media.getWidth() + "x" + media.getHeight());
            Log.i(TAG, "裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
            Log.i(TAG, "文件大小: " + media.getSize());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (ActivityCompatHelper.checkRootFragment(this)) {
                if (PictureSelectionConfig.onResultCallListener != null) {
                    PictureSelectionConfig.onResultCallListener.onCancel();
                }
            } else {
                PictureSelectorPreviewFragment previewFragment = getPreviewFragment();
                if (PictureSelectionConfig.getInstance().isPreviewZoomEffect && previewFragment != null) {
                    previewFragment.onKeyDownBackToMin();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private PictureSelectorPreviewFragment getPreviewFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof PictureSelectorPreviewFragment) {
                return (PictureSelectorPreviewFragment) fragment;
            }
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(PictureContextWrapper.wrap(newBase,
                PictureSelectionConfig.getInstance().language));
    }
}
