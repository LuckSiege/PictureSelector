package com.luck.pictureselector;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.luck.lib.camerax.CustomCameraConfig;
import com.luck.lib.camerax.PictureCameraActivity;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.picture.lib.registry.ImageCaptureComponent;
import com.luck.picture.lib.registry.VideoCaptureComponent;

import java.io.File;
import java.util.Objects;

/**
 * @author：luck
 * @date：2023/6/7 10:33 上午
 * @describe：自定义相机案例，用户可以随意使用其他，但需符合PictureSelector的规则 自定义相机主Activity需要继承；
 * ImageCaptureComponent 拍照组件
 * VideoCaptureComponent 录像组件
 * AudioCaptureComponent 录音组件
 */
public class CustomCameraActivity extends PictureCameraActivity implements ImageCaptureComponent, VideoCaptureComponent {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 可以使用PictureSelector默认的相机输出地址，用户也可以自定义
        // 最终结果通过Activity#setResult(); Intent#MediaStore.EXTRA_OUTPUT字段返回
        // TODO 这里只是以SimpleCameraX案例来，具体根据自己需求来
        Uri outputUri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        if (outputUri.getScheme().equalsIgnoreCase("file")) {
            File file = new File(outputUri.getPath());
            File parentFile = file.getParentFile();
            String dir = Objects.requireNonNull(parentFile).getAbsolutePath() + File.separator;
            String fileName = file.getName();
            getIntent().putExtra(SimpleCameraX.EXTRA_OUTPUT_PATH_DIR, dir);
            getIntent().putExtra(SimpleCameraX.EXTRA_CAMERA_FILE_NAME, fileName);
        }
        CustomCameraConfig.imageEngine = (context, url, imageView) -> {
            Glide.with(context).load(url).into(imageView);
        };
    }
}
