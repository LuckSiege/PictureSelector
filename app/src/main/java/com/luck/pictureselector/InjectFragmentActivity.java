package com.luck.pictureselector;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.luck.picture.lib.PictureSelectorFragment;
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
import com.luck.picture.lib.immersive.ImmersiveManager;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.utils.MediaUtils;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2021/12/20 1:40 下午
 * @describe：InjectFragmentActivity
 */
public class InjectFragmentActivity extends AppCompatActivity implements IBridgePictureBehavior {
    private final static String TAG = "PictureSelectorTag";
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int color = ContextCompat.getColor(this, R.color.app_color_white);
        ImmersiveManager.immersiveAboveAPI23(this, color, color, true);
        setContentView(R.layout.activity_inject_fragment);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.tvb_inject_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方式一
                PictureSelector.create(v.getContext())
                        .openGallery(SelectMimeType.ofAll())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .buildLaunch(R.id.fragment_container, new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                setTranslucentStatusBar();
                                analyticalSelectResults(result);
                            }

                            @Override
                            public void onCancel() {
                                setTranslucentStatusBar();
                                Log.i(TAG, "PictureSelector Cancel");
                            }
                        });
            }
        });

        findViewById(R.id.tvb_inject_result_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方式二
                PictureSelectorFragment selectorFragment = PictureSelector.create(v.getContext())
                        .openGallery(SelectMimeType.ofAll())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .build();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, selectorFragment, selectorFragment.getFragmentTag())
                        .addToBackStack(selectorFragment.getFragmentTag())
                        .commitAllowingStateLoss();
            }
        });
    }


    @Override
    public void onSelectFinish(PictureCommonFragment.SelectorResult result) {
        setTranslucentStatusBar();
        if (result == null) {
            return;
        }
        if (result.mResultCode == RESULT_OK) {
            ArrayList<LocalMedia> selectorResult = PictureSelector.obtainSelectorList(result.mResultData);
            analyticalSelectResults(selectorResult);
        } else if (result.mResultCode == RESULT_CANCELED) {
            Log.i(TAG, "onSelectFinish PictureSelector Cancel");
            setTranslucentStatusBar();
        }
    }

    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        StringBuilder builder = new StringBuilder();
        builder.append("Result").append("\n");
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(this,media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(PictureAppMaster.getInstance().getAppContext(), media.getPath());
                    media.setWidth(videoExtraInfo.getWidth());
                    media.setHeight(videoExtraInfo.getHeight());
                }
            }
            builder.append(media.getAvailablePath()).append("\n");
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
        tvResult.setText(builder.toString());
    }

    /**
     * 设置状态栏字体颜色
     */
    private void setTranslucentStatusBar() {
        ImmersiveManager.translucentStatusBar(InjectFragmentActivity.this, true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(PictureContextWrapper.wrap(newBase,
                PictureSelectionConfig.getInstance().language,PictureSelectionConfig.getInstance().defaultLanguage));
    }

}
