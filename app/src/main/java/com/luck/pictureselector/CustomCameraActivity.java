package com.luck.pictureselector;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.luck.lib.camerax.PictureCameraActivity;
import com.luck.picture.lib.registry.ImageCaptureComponent;
import com.luck.picture.lib.registry.VideoCaptureComponent;

/**
 * @author：luck
 * @date：2023/6/7 10:33 上午
 * @describe：CustomCameraActivity
 */
public class CustomCameraActivity extends PictureCameraActivity implements VideoCaptureComponent {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri outputUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        Log.i("YY", outputUri.toString());
    }
}
