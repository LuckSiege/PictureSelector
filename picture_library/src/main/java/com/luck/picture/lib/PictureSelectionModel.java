package com.luck.picture.lib;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DoubleUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib
 * describe：PictureSelector selection configuration.
 * email：893855882@qq.com
 * data：2017/5/24
 */

public class PictureSelectionModel {
    private PictureSelectionConfig selectionConfig;
    private PictureSelector selector;

    public PictureSelectionModel(PictureSelector selector, int mimeType) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.mimeType = mimeType;
    }

    public PictureSelectionModel(PictureSelector selector, int mimeType, boolean camera) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.camera = camera;
        selectionConfig.mimeType = mimeType;
    }

    /**
     * @param themeStyleId PictureSelector Theme style
     * @return
     */
    public PictureSelectionModel theme(@StyleRes int themeStyleId) {
        selectionConfig.themeStyleId = themeStyleId;
        return this;
    }

    /**
     * @param selectionMode PictureSelector Selection model and PictureConfig.MULTIPLE or PictureConfig.SINGLE
     * @return
     */
    public PictureSelectionModel selectionMode(int selectionMode) {
        selectionConfig.selectionMode = selectionMode;
        return this;
    }

    /**
     * @param enableCrop Do you want to start cutting ?
     * @return
     */
    public PictureSelectionModel enableCrop(boolean enableCrop) {
        selectionConfig.enableCrop = enableCrop;
        return this;
    }

    /**
     * @param enablePreviewAudio Do you want to play audio ?
     * @return
     */
    public PictureSelectionModel enablePreviewAudio(boolean enablePreviewAudio) {
        selectionConfig.enablePreviewAudio = enablePreviewAudio;
        return this;
    }

    /**
     * @param freeStyleCropEnabled Crop frame is move ?
     * @return
     */
    public PictureSelectionModel freeStyleCropEnabled(boolean freeStyleCropEnabled) {
        selectionConfig.freeStyleCropEnabled = freeStyleCropEnabled;
        return this;
    }

    /**
     * @param scaleEnabled Crop frame is zoom ?
     * @return
     */
    public PictureSelectionModel scaleEnabled(boolean scaleEnabled) {
        selectionConfig.scaleEnabled = scaleEnabled;
        return this;
    }

    /**
     * @param rotateEnabled Crop frame is rotate ?
     * @return
     */
    public PictureSelectionModel rotateEnabled(boolean rotateEnabled) {
        selectionConfig.rotateEnabled = rotateEnabled;
        return this;
    }

    /**
     * @param circleDimmedLayer Circular head cutting
     * @return
     */
    public PictureSelectionModel circleDimmedLayer(boolean circleDimmedLayer) {
        selectionConfig.circleDimmedLayer = circleDimmedLayer;
        return this;
    }

    /**
     * @param showCropFrame Whether to show crop frame
     * @return
     */
    public PictureSelectionModel showCropFrame(boolean showCropFrame) {
        selectionConfig.showCropFrame = showCropFrame;
        return this;
    }

    /**
     * @param showCropGrid Whether to show CropGrid
     * @return
     */
    public PictureSelectionModel showCropGrid(boolean showCropGrid) {
        selectionConfig.showCropGrid = showCropGrid;
        return this;
    }

    /**
     * @param hideBottomControls Whether is Clipping function bar
     *                           单选有效
     * @return
     */
    public PictureSelectionModel hideBottomControls(boolean hideBottomControls) {
        selectionConfig.hideBottomControls = hideBottomControls;
        return this;
    }

    /**
     * @param aspect_ratio_x Crop Proportion x
     * @param aspect_ratio_y Crop Proportion y
     * @return
     */
    public PictureSelectionModel withAspectRatio(int aspect_ratio_x, int aspect_ratio_y) {
        selectionConfig.aspect_ratio_x = aspect_ratio_x;
        selectionConfig.aspect_ratio_y = aspect_ratio_y;
        return this;
    }

    /**
     * @param maxSelectNum PictureSelector max selection
     * @return
     */
    public PictureSelectionModel maxSelectNum(int maxSelectNum) {
        selectionConfig.maxSelectNum = maxSelectNum;
        return this;
    }

    /**
     * @param minSelectNum PictureSelector min selection
     * @return
     */
    public PictureSelectionModel minSelectNum(int minSelectNum) {
        selectionConfig.minSelectNum = minSelectNum;
        return this;
    }

    /**
     * @param videoQuality video quality and 0 or 1
     * @return
     */
    public PictureSelectionModel videoQuality(int videoQuality) {
        selectionConfig.videoQuality = videoQuality;
        return this;
    }

    /**
     * @param suffixType PictureSelector media format
     * @return
     */
    public PictureSelectionModel imageFormat(String suffixType) {
        selectionConfig.suffixType = suffixType;
        return this;
    }


    /**
     * @param cropWidth  crop width
     * @param cropHeight crop height
     * @return
     */
    public PictureSelectionModel cropWH(int cropWidth, int cropHeight) {
        selectionConfig.cropWidth = cropWidth;
        selectionConfig.cropHeight = cropHeight;
        return this;
    }

    /**
     * @param videoMaxSecond selection video max second
     * @return
     */
    public PictureSelectionModel videoMaxSecond(int videoMaxSecond) {
        selectionConfig.videoMaxSecond = videoMaxSecond * 1000;
        return this;
    }

    /**
     * @param videoMinSecond selection video min second
     * @return
     */
    public PictureSelectionModel videoMinSecond(int videoMinSecond) {
        selectionConfig.videoMinSecond = videoMinSecond * 1000;
        return this;
    }


    /**
     * @param recordVideoSecond video record second
     * @return
     */
    public PictureSelectionModel recordVideoSecond(int recordVideoSecond) {
        selectionConfig.recordVideoSecond = recordVideoSecond;
        return this;
    }

    /**
     * @param width  glide width
     * @param height glide height
     * @return
     */
    public PictureSelectionModel glideOverride(@IntRange(from = 100) int width,
                                               @IntRange(from = 100) int height) {
        selectionConfig.overrideWidth = width;
        selectionConfig.overrideHeight = height;
        return this;
    }

    /**
     * @param sizeMultiplier The multiplier to apply to the
     *                       {@link com.bumptech.glide.request.target.Target}'s dimensions when
     *                       loading the resource.
     * @return
     */
    public PictureSelectionModel sizeMultiplier(@FloatRange(from = 0.1f) float sizeMultiplier) {
        selectionConfig.sizeMultiplier = sizeMultiplier;
        return this;
    }

    /**
     * @param imageSpanCount PictureSelector image span count
     * @return
     */
    public PictureSelectionModel imageSpanCount(int imageSpanCount) {
        selectionConfig.imageSpanCount = imageSpanCount;
        return this;
    }

    /**
     * @param Less than how many KB images are not compressed
     * @return
     */
    public PictureSelectionModel minimumCompressSize(int size) {
        selectionConfig.minimumCompressSize = size;
        return this;
    }

    /**
     * @param compressQuality crop compress quality default 90
     * @return
     */
    public PictureSelectionModel cropCompressQuality(int compressQuality) {
        selectionConfig.cropCompressQuality = compressQuality;
        return this;
    }

    /**
     * @param isCompress Whether to open compress
     * @return
     */
    public PictureSelectionModel compress(boolean isCompress) {
        selectionConfig.isCompress = isCompress;
        return this;
    }

    /**
     * @param synOrAsy Synchronous or asynchronous compression
     * @return
     */
    public PictureSelectionModel synOrAsy(boolean synOrAsy) {
        selectionConfig.synOrAsy = synOrAsy;
        return this;
    }

    /**
     * @param path save path
     * @return
     */
    public PictureSelectionModel compressSavePath(String path) {
        selectionConfig.compressSavePath = path;
        return this;
    }

    /**
     * @param zoomAnim Picture list zoom anim
     * @return
     */
    public PictureSelectionModel isZoomAnim(boolean zoomAnim) {
        selectionConfig.zoomAnim = zoomAnim;
        return this;
    }

    /**
     * @param previewEggs preview eggs  It doesn't make much sense
     * @return
     */
    public PictureSelectionModel previewEggs(boolean previewEggs) {
        selectionConfig.previewEggs = previewEggs;
        return this;
    }

    /**
     * @param isCamera Whether to open camera button
     * @return
     */
    public PictureSelectionModel isCamera(boolean isCamera) {
        selectionConfig.isCamera = isCamera;
        return this;
    }

    /**
     * @param outputCameraPath Camera save path
     * @return
     */
    public PictureSelectionModel setOutputCameraPath(String outputCameraPath) {
        selectionConfig.outputCameraPath = outputCameraPath;
        return this;
    }

    /**
     * @param isGif Whether to open gif
     * @return
     */
    public PictureSelectionModel isGif(boolean isGif) {
        selectionConfig.isGif = isGif;
        return this;
    }

    /**
     * @param enablePreview Do you want to preview the picture?
     * @return
     */
    public PictureSelectionModel previewImage(boolean enablePreview) {
        selectionConfig.enablePreview = enablePreview;
        return this;
    }

    /**
     * @param enPreviewVideo Do you want to preview the video?
     * @return
     */
    public PictureSelectionModel previewVideo(boolean enPreviewVideo) {
        selectionConfig.enPreviewVideo = enPreviewVideo;
        return this;
    }

    /**
     * @param openClickSound Whether to open click voice
     * @return
     */
    public PictureSelectionModel openClickSound(boolean openClickSound) {
        selectionConfig.openClickSound = openClickSound;
        return this;
    }

    /**
     * 是否可拖动裁剪框(setFreeStyleCropEnabled 为true 有效)
     */
    public PictureSelectionModel isDragFrame(boolean isDragFrame) {
        selectionConfig.isDragFrame = isDragFrame;
        return this;
    }

    /**
     * @param selectionMedia Select the selected picture set
     * @return
     */
    public PictureSelectionModel selectionMedia(List<LocalMedia> selectionMedia) {
        if (selectionMedia == null) {
            selectionMedia = new ArrayList<>();
        }
        selectionConfig.selectionMedias = selectionMedia;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    public void forResult(int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                return;
            }
            Intent intent = new Intent(activity, PictureSelectorActivity.class);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            activity.overridePendingTransition(R.anim.a5, 0);
        }
    }

    /**
     * 提供外部预览图片方法
     *
     * @param position
     * @param medias
     */
    public void openExternalPreview(int position, List<LocalMedia> medias) {
        if (selector != null) {
            selector.externalPicturePreview(position, medias);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }

    /**
     * 提供外部预览图片方法-带自定义下载保存路径
     *
     * @param position
     * @param medias
     */
    public void openExternalPreview(int position, String directory_path, List<LocalMedia> medias) {
        if (selector != null) {
            selector.externalPicturePreview(position, directory_path, medias);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }

}
