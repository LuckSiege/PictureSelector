package com.luck.picture.lib.config;

import android.support.annotation.StyleRes;

import com.luck.picture.lib.R;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DebugUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 */

public final class PictureSelectionConfig implements Serializable {
    public int mimeType;
    public boolean camera;
    public String outputCameraPath;
    @StyleRes
    public int themeStyleId;
    public int selectionMode;
    public int maxSelectNum;
    public int minSelectNum;
    public int videoQuality;
    public int cropCompressQuality;
    public int videoSecond;
    public int recordVideoSecond;
    public int compressMaxkB;
    public int compressGrade;
    public int imageSpanCount;
    public int compressMode;
    public int compressWidth;
    public int compressHeight;
    public int overrideWidth;
    public int overrideHeight;
    public int aspect_ratio_x;
    public int aspect_ratio_y;
    public float sizeMultiplier;
    public int cropWidth;
    public int cropHeight;
    public boolean zoomAnim;
    public boolean isCompress;
    public boolean isCamera;
    public boolean isGif;
    public boolean enablePreview;
    public boolean enPreviewVideo;
    public boolean enablePreviewAudio;
    public boolean checkNumMode;
    public boolean openClickSound;
    public boolean enableCrop;
    public boolean freeStyleCropEnabled;
    public boolean circleDimmedLayer;
    public boolean showCropFrame;
    public boolean showCropGrid;
    public boolean hideBottomControls;
    public boolean rotateEnabled;
    public boolean scaleEnabled;
    public boolean previewEggs;

    public List<LocalMedia> selectionMedias;

    private void reset() {
        mimeType = PictureConfig.TYPE_IMAGE;
        camera = false;
        themeStyleId = R.style.picture_default_style;
        selectionMode = PictureConfig.MULTIPLE;
        maxSelectNum = 9;
        minSelectNum = 0;
        videoQuality = 1;
        cropCompressQuality = 90;
        videoSecond = 0;
        recordVideoSecond = 60;
        compressMaxkB = PictureConfig.MAX_COMPRESS_SIZE;
        compressGrade = Luban.THIRD_GEAR;
        imageSpanCount = 4;
        compressMode = PictureConfig.LUBAN_COMPRESS_MODE;
        compressWidth = 0;
        compressHeight = 0;
        overrideWidth = 0;
        overrideHeight = 0;
        isCompress = false;
        aspect_ratio_x = 0;
        aspect_ratio_y = 0;
        cropWidth = 0;
        cropHeight = 0;
        isCamera = true;
        isGif = false;
        enablePreview = true;
        enPreviewVideo = true;
        enablePreviewAudio = true;
        checkNumMode = false;
        openClickSound = false;
        enableCrop = false;
        freeStyleCropEnabled = false;
        circleDimmedLayer = false;
        showCropFrame = true;
        showCropGrid = true;
        hideBottomControls = true;
        rotateEnabled = true;
        scaleEnabled = true;
        previewEggs = false;
        zoomAnim = true;
        outputCameraPath = "";
        sizeMultiplier = 0.5f;
        selectionMedias = new ArrayList<>();
        DebugUtil.i("*******", "reset PictureSelectionConfig");
    }

    public static PictureSelectionConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static PictureSelectionConfig getCleanInstance() {
        PictureSelectionConfig selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private static final class InstanceHolder {
        private static final PictureSelectionConfig INSTANCE = new PictureSelectionConfig();
    }
}
