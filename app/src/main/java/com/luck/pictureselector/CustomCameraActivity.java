package com.luck.pictureselector;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.luck.lib.camerax.CustomCameraConfig;
import com.luck.lib.camerax.PictureCameraActivity;
import com.luck.picture.lib.registry.ImageCaptureComponent;

/**
 * @author：luck
 * @date：2023/6/7 10:33 上午
 * @describe：自定义相机案例，用户可以随意使用其他，但需符合PictureSelector的规则 自定义相机主Activity需要继承；
 * ImageCaptureComponent 拍照组件
 * VideoCaptureComponent 录像组件
 * AudioCaptureComponent 录音组件
 */
public class CustomCameraActivity extends PictureCameraActivity implements ImageCaptureComponent {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 可以使用PictureSelector默认的相机输出地址，用户也可以自定义
        // 最终结果通过Activity#setResult(); Intent#MediaStore.EXTRA_OUTPUT字段返回
        Uri outputUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        CustomCameraConfig.imageEngine = (context, url, imageView) -> {
            Glide.with(context).load(url).into(imageView);
        };
    }
}
