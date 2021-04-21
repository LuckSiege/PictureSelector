package com.luck.pictureselector;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.yalantis.ucrop.util.ScreenUtils;
import com.yalantis.ucrop.view.widget.CustomLoadingDialog;

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_activity, btn_fragment, btn_dialog, clapper,videoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        btn_activity = findViewById(R.id.btn_activity);
        btn_fragment = findViewById(R.id.btn_fragment);
        btn_dialog = findViewById(R.id.btn_dialog);
        clapper = findViewById(R.id.clapper);
        btn_activity.setOnClickListener(this);
        btn_fragment.setOnClickListener(this);
        btn_dialog.setOnClickListener(this);
        clapper.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity:
                startActivity(new Intent(SimpleActivity.this, MainActivity.class));
                break;
            case R.id.btn_fragment:
                startActivity(new Intent(SimpleActivity.this, PhotoFragmentActivity.class));
                break;
            case R.id.btn_dialog:
                View view = getLayoutInflater().inflate(R.layout.ucrop_dialog_loading, null);
                CustomLoadingDialog mMyDialog = new CustomLoadingDialog(this,
                        ScreenUtils.dip2px(this,228),
                        ScreenUtils.dip2px(this,108),
                        view,
                        R.style.ucrop_DialogStyle);
                mMyDialog.setCancelable(true);
                mMyDialog.show();
                break;
            case R.id.clapper:
                List<String> filterMimeType = new ArrayList();
                filterMimeType.add("video/mp4");
                filterMimeType.add("video/quicktime");
                filterMimeType.add("image/jpeg");
                filterMimeType.add("image/jpg");
                filterMimeType.add("image/png");
                //upload
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofVideo())
                        .isWeChatStyle(true)
                        .imageEngine(GlideEngine.createGlideEngine())
                        .isWithVideoImage(true)
                        .theme(R.style.picture_WeChat_style)
                        .setLanguage(LanguageConfig.ENGLISH)
                        .maxVideoSelectNum(35)
                        .isMaxSelectEnabledMask(true)
                        .isOnlyVideo(false)
                        .isPreviewVideo(false)
                        .selectionMode(PictureConfig.MULTIPLE)
                        .isSingleDirectReturn(false)
                        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .isCamera(false)
                        .maxSelectNum(35)
                        .videoMinSecond(6)
                        .setFilterMimeType(filterMimeType)
                        .isShowPreView(false)
                        .forResult(new OnResultCallbackListener(){

                            @Override
                            public void onResult(List result) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });

                break;
            default:
                break;
        }
    }
}
