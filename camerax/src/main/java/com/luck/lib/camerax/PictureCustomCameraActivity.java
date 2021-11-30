package com.luck.lib.camerax;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luck.lib.camerax.listener.CameraListener;
import com.luck.lib.camerax.listener.ClickListener;
import com.luck.lib.camerax.listener.ImageCallbackListener;
import com.luck.lib.camerax.permissions.PermissionResultCallback;

/**
 * @author：luck
 * @date：2021/11/29 7:50 下午
 * @describe：PictureCustomCameraActivity
 */
public class PictureCustomCameraActivity extends AppCompatActivity {
    /**
     * PermissionResultCallback
     */
    private PermissionResultCallback mPermissionResultCallback;

    private CustomCameraView mCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        mCameraView = new CustomCameraView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mCameraView.setLayoutParams(layoutParams);
        setContentView(mCameraView);
        mCameraView.setCameraConfig(getIntent());
        mCameraView.setImageCallbackListener(new ImageCallbackListener() {
            @Override
            public void onLoadImage(String url, ImageView imageView) {
                Glide.with(imageView.getContext()).load(url).into(imageView);
            }
        });
        mCameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureSuccess(@NonNull String url) {
                handleCameraSuccess();
            }

            @Override
            public void onRecordSuccess(@NonNull String url) {
                handleCameraSuccess();
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message,
                                @Nullable Throwable cause) {
                Toast.makeText(PictureCustomCameraActivity.this,
                        message, Toast.LENGTH_LONG).show();
                handleCameraCancel();
            }
        });

        mCameraView.setOnCancelClickListener(new ClickListener() {
            @Override
            public void onClick() {
                handleCameraCancel();
            }
        });
    }

    private void handleCameraSuccess() {
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private void handleCameraCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            mCameraView.onCancelMedia();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Set PermissionResultCallback
     *
     * @param callback
     */
    public void setPermissionsResultAction(PermissionResultCallback callback) {
        mPermissionResultCallback = callback;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionResultCallback != null) {
            com.luck.lib.camerax.permissions.PermissionChecker.getInstance()
                    .onRequestPermissionsResult(grantResults, mPermissionResultCallback);
            mPermissionResultCallback = null;
        }
    }
}
