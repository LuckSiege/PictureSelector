package com.luck.picture.lib;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.UCropOptions;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.listener.OnVideoSelectedPlayCallback;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.DoubleUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author：luck
 * @date：2017-5-24 21:30
 * @describe：PictureSelectionModel
 */

public class PictureSelectionModel {
    private PictureSelectionConfig selectionConfig;
    private PictureSelector selector;

    public PictureSelectionModel(PictureSelector selector, int chooseMode) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.chooseMode = chooseMode;
    }

    public PictureSelectionModel(PictureSelector selector, int chooseMode, boolean camera) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.camera = camera;
        selectionConfig.chooseMode = chooseMode;
    }

    /**
     * @param themeStyleId PictureSelector Theme style
     * @return PictureSelectionModel
     */
    public PictureSelectionModel theme(@StyleRes int themeStyleId) {
        selectionConfig.themeStyleId = themeStyleId;
        return this;
    }

    /**
     * @param locale Language
     * @return PictureSelectionModel
     */
    public PictureSelectionModel setLanguage(int language) {
        selectionConfig.language = language;
        return this;
    }

    /**
     * Change the desired orientation of this activity.  If the activity
     * is currently in the foreground or otherwise impacting the screen
     * orientation, the screen will immediately be changed (possibly causing
     * the activity to be restarted). Otherwise, this will be used the next
     * time the activity is visible.
     *
     * @param requestedOrientation An orientation constant as used in
     *                             {@link ActivityInfo#screenOrientation ActivityInfo.screenOrientation}.
     */
    public PictureSelectionModel setRequestedOrientation(int requestedOrientation) {
        selectionConfig.requestedOrientation = requestedOrientation;
        return this;
    }

    /**
     * @param engine Image Load the engine
     * @return
     */
    public PictureSelectionModel loadImageEngine(ImageEngine engine) {
        if (selectionConfig.imageEngine != engine) {
            selectionConfig.imageEngine = engine;
        }
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
     * @param isWeChatStyle Select style with or without WeChat enabled
     * @return
     */
    public PictureSelectionModel isWeChatStyle(boolean isWeChatStyle) {
        selectionConfig.isWeChatStyle = isWeChatStyle;
        return this;
    }

    /**
     * @param isUseCustomCamera Whether to use a custom camera
     * @return
     */
    public PictureSelectionModel isUseCustomCamera(boolean isUseCustomCamera) {
        selectionConfig.isUseCustomCamera = isUseCustomCamera;
        return this;
    }

    /**
     * @param callback Provide video playback control，Users are free to customize the video display interface
     * @return
     */
    public PictureSelectionModel bindCustomPlayVideoCallback(OnVideoSelectedPlayCallback callback) {
        selectionConfig.customVideoPlayCallback = new WeakReference<>(callback).get();
        return this;
    }

    /**
     * @param buttonFeatures Set the record button function
     *                       # 具体参考 CustomCameraView.BUTTON_STATE_BOTH、BUTTON_STATE_ONLY_CAPTURE、BUTTON_STATE_ONLY_RECORDER
     * @return
     */
    public PictureSelectionModel setButtonFeatures(int buttonFeatures) {
        selectionConfig.buttonFeatures = buttonFeatures;
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
     * @param uCropOptions UCrop parameter configuration is provided
     * @return
     */
    public PictureSelectionModel basicUCropConfig(UCropOptions uCropOptions) {
        selectionConfig.uCropOptions = uCropOptions;
        return this;
    }

    /**
     * @param isMultipleSkipCrop Whether multiple images can be skipped when cropping
     * @return
     */
    public PictureSelectionModel isMultipleSkipCrop(boolean isMultipleSkipCrop) {
        selectionConfig.isMultipleSkipCrop = isMultipleSkipCrop;
        return this;
    }


    /**
     * @param enablePreviewAudio Do you want to ic_play audio ?
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
     * @param circleDimmedColor setCircleDimmedColor
     * @return
     */
    public PictureSelectionModel setCircleDimmedColor(int circleDimmedColor) {
        selectionConfig.circleDimmedColor = circleDimmedColor;
        return this;
    }

    /**
     * @param circleDimmedBorderColor setCircleDimmedBorderColor
     * @return
     */
    public PictureSelectionModel setCircleDimmedBorderColor(int circleDimmedBorderColor) {
        selectionConfig.circleDimmedBorderColor = circleDimmedBorderColor;
        return this;
    }

    /**
     * @param circleStrokeWidth setCircleStrokeWidth
     * @return
     */
    public PictureSelectionModel setCircleStrokeWidth(int circleStrokeWidth) {
        selectionConfig.circleStrokeWidth = circleStrokeWidth;
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
     * @param isWithVideoImage Whether the pictures and videos can be selected together
     * @return
     */
    public PictureSelectionModel isWithVideoImage(boolean isWithVideoImage) {
        selectionConfig.isWithVideoImage =
                selectionConfig.selectionMode == PictureConfig.SINGLE
                        || selectionConfig.chooseMode != PictureMimeType.ofAll() ? false : isWithVideoImage;
        return this;
    }

    /**
     * @param maxSelectNum Selector max selection
     * @return
     */
    public PictureSelectionModel maxSelectNum(int maxSelectNum) {
        selectionConfig.maxSelectNum = maxSelectNum;
        return this;
    }

    /**
     * @param minSelectNum Selector min selection
     * @return
     */
    public PictureSelectionModel minSelectNum(int minSelectNum) {
        selectionConfig.minSelectNum = minSelectNum;
        return this;
    }

    /**
     * @param maxSelectNum PictureSelector max selection
     * @return
     */
    public PictureSelectionModel maxPictureSelectNum(int maxSelectNum) {
        selectionConfig.maxPictureSelectNum = maxSelectNum;
        return this;
    }

    /**
     * @param minSelectNum PictureSelector min selection
     * @return
     */
    public PictureSelectionModel minPictureSelectNum(int minSelectNum) {
        selectionConfig.minPictureSelectNum = minSelectNum;
        return this;
    }

    /**
     * @param maxVideoSelectNum PictureSelector video max selection
     * @return
     */
    public PictureSelectionModel maxVideoSelectNum(int maxVideoSelectNum) {
        selectionConfig.maxVideoSelectNum = maxVideoSelectNum;
        return this;
    }

    /**
     * @param minVideoSelectNum PictureSelector video min selection
     * @return
     */
    public PictureSelectionModel minVideoSelectNum(int minVideoSelectNum) {
        selectionConfig.minVideoSelectNum = minVideoSelectNum;
        return this;
    }


    /**
     * @param Select whether to return directly
     * @return
     */
    public PictureSelectionModel isSingleDirectReturn(boolean isSingleDirectReturn) {
        selectionConfig.isSingleDirectReturn = selectionConfig.selectionMode
                == PictureConfig.SINGLE ? isSingleDirectReturn : false;
        selectionConfig.isOriginalControl = selectionConfig.selectionMode
                == PictureConfig.SINGLE && isSingleDirectReturn ? false : selectionConfig.isOriginalControl;
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
     * # alternative api cameraFileName(xxx.PNG);
     *
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
     * @return this
     * @deprecated Crop image output width and height
     * {@link cropImageWideHigh()}
     */
    @Deprecated
    public PictureSelectionModel cropWH(int cropWidth, int cropHeight) {
        selectionConfig.cropWidth = cropWidth;
        selectionConfig.cropHeight = cropHeight;
        return this;
    }

    /**
     * @param cropWidth  crop width
     * @param cropHeight crop height
     * @return this
     */
    public PictureSelectionModel cropImageWideHigh(int cropWidth, int cropHeight) {
        selectionConfig.cropWidth = cropWidth;
        selectionConfig.cropHeight = cropHeight;
        return this;
    }

    /**
     * @param videoMaxSecond selection video max second
     * @return
     */
    public PictureSelectionModel videoMaxSecond(int videoMaxSecond) {
        selectionConfig.videoMaxSecond = (videoMaxSecond * 1000);
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
     * @return 2.2.9开始 Glide改为外部用户自己定义此方法没有意义了
     */
    @Deprecated
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
     * @return 2.2.9开始Glide改为外部用户自己定义此方法没有意义了
     */
    @Deprecated
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
     * @return 请使用 cutOutQuality();方法
     */
    @Deprecated
    public PictureSelectionModel cropCompressQuality(int compressQuality) {
        selectionConfig.cropCompressQuality = compressQuality;
        return this;
    }

    /**
     * @param cutQuality crop compress quality default 90
     * @return
     */
    public PictureSelectionModel cutOutQuality(int cutQuality) {
        selectionConfig.cropCompressQuality = cutQuality;
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
     * @param compressQuality Image compressed output quality
     * @return
     */
    public PictureSelectionModel compressQuality(int compressQuality) {
        selectionConfig.compressQuality = compressQuality;
        return this;
    }

    /**
     * @param returnEmpty No data can be returned
     * @return
     */
    public PictureSelectionModel isReturnEmpty(boolean returnEmpty) {
        selectionConfig.returnEmpty = returnEmpty;
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
     * @param focusAlpha After compression, the transparent channel is retained
     * @return
     */
    public PictureSelectionModel compressFocusAlpha(boolean focusAlpha) {
        selectionConfig.focusAlpha = focusAlpha;
        return this;
    }

    /**
     * @param isOriginalControl Whether the original image is displayed
     * @return
     */
    public PictureSelectionModel isOriginalImageControl(boolean isOriginalControl) {
        selectionConfig.isOriginalControl = selectionConfig.camera
                || selectionConfig.chooseMode == PictureMimeType.ofVideo()
                || selectionConfig.chooseMode == PictureMimeType.ofAudio() ? false : isOriginalControl;
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
     * Camera custom local file name
     * # Such as xxx.png
     *
     * @param fileName
     * @return
     */
    public PictureSelectionModel cameraFileName(String fileName) {
        selectionConfig.cameraFileName = fileName;
        return this;
    }

    /**
     * crop custom local file name
     * # Such as xxx.png
     *
     * @param renameCropFileName
     * @return
     */
    public PictureSelectionModel renameCropFileName(String renameCropFileName) {
        selectionConfig.renameCropFileName = renameCropFileName;
        return this;
    }

    /**
     * custom compress local file name
     * # Such as xxx.png
     *
     * @param renameFile
     * @return
     */
    public PictureSelectionModel renameCompressFile(String renameFile) {
        selectionConfig.renameCompressFileName = renameFile;
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
     * # Responding to the Q version of Android, it's all in the app
     * sandbox so customizations are no longer provided
     *
     * @param outputCameraPath Camera save path   由于Android Q的原因 其实此方法作用的意义就没了
     * @return
     */
    @Deprecated
    public PictureSelectionModel setOutputCameraPath(String outputCameraPath) {
        selectionConfig.outputCameraPath = outputCameraPath;
        return this;
    }


    /**
     * # file size The unit is M
     *
     * @param fileSize Filter file size
     * @return
     */
    public PictureSelectionModel queryMaxFileSize(int fileSize) {
        selectionConfig.filterFileSize = fileSize;
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
     * @param isNotPreviewDownload Previews do not show downloads
     * @return
     */
    public PictureSelectionModel isNotPreviewDownload(boolean isNotPreviewDownload) {
        selectionConfig.isNotPreviewDownload = isNotPreviewDownload;
        return this;
    }

    /**
     * @param Specify get image format
     * @return
     */
    public PictureSelectionModel querySpecifiedFormatSuffix(String specifiedFormat) {
        selectionConfig.specifiedFormat = specifiedFormat;
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
     * Whether the multi-graph clipping list is animated or not
     *
     * @param isAnimation
     * @return
     */
    public PictureSelectionModel isMultipleRecyclerAnimation(boolean isAnimation) {
        selectionConfig.isMultipleRecyclerAnimation = isAnimation;
        return this;
    }


    /**
     * 设置摄像头方向(前后 默认后置)
     */
    public PictureSelectionModel isCameraAroundState(boolean isCameraAroundState) {
        selectionConfig.isCameraAroundState = isCameraAroundState;
        return this;
    }

    /**
     * @param selectionMedia Select the selected picture set
     * @return
     */
    public PictureSelectionModel selectionMedia(List<LocalMedia> selectionMedia) {
        if (selectionConfig.selectionMode == PictureConfig.SINGLE && selectionConfig.isSingleDirectReturn) {
            selectionConfig.selectionMedias = null;
        } else {
            selectionConfig.selectionMedias = selectionMedia;
        }
        return this;
    }

    /**
     * 是否改变状态栏字段颜色(黑白字体转换)
     * #适合所有style使用
     *
     * @param isChangeStatusBarFontColor
     * @return
     */
    @Deprecated
    public PictureSelectionModel isChangeStatusBarFontColor(boolean isChangeStatusBarFontColor) {
        selectionConfig.isChangeStatusBarFontColor = isChangeStatusBarFontColor;
        return this;
    }

    /**
     * 选择图片样式0/9
     * #适合所有style使用
     *
     * @param isOpenStyleNumComplete
     * @return 使用setPictureStyle方法
     */
    @Deprecated
    public PictureSelectionModel isOpenStyleNumComplete(boolean isOpenStyleNumComplete) {
        selectionConfig.isOpenStyleNumComplete = isOpenStyleNumComplete;
        return this;
    }

    /**
     * 是否开启数字选择模式
     * #适合qq style 样式使用
     *
     * @param isOpenStyleCheckNumMode
     * @return 使用setPictureStyle方法
     */
    @Deprecated
    public PictureSelectionModel isOpenStyleCheckNumMode(boolean isOpenStyleCheckNumMode) {
        selectionConfig.isOpenStyleCheckNumMode = isOpenStyleCheckNumMode;
        return this;
    }

    /**
     * 设置标题栏背景色
     *
     * @param color
     * @return 使用setPictureStyle方法
     */
    @Deprecated
    public PictureSelectionModel setTitleBarBackgroundColor(@ColorInt int color) {
        selectionConfig.titleBarBackgroundColor = color;
        return this;
    }


    /**
     * 状态栏背景色
     *
     * @param color
     * @return 使用setPictureStyle方法
     */
    @Deprecated
    public PictureSelectionModel setStatusBarColorPrimaryDark(@ColorInt int color) {
        selectionConfig.pictureStatusBarColor = color;
        return this;
    }


    /**
     * 裁剪页面标题背景色
     *
     * @param color
     * @return 使用setPictureCropStyle方法
     */
    @Deprecated
    public PictureSelectionModel setCropTitleBarBackgroundColor(@ColorInt int color) {
        selectionConfig.cropTitleBarBackgroundColor = color;
        return this;
    }

    /**
     * 裁剪页面状态栏背景色
     *
     * @param color
     * @return 使用setPictureCropStyle方法
     */
    @Deprecated
    public PictureSelectionModel setCropStatusBarColorPrimaryDark(@ColorInt int color) {
        selectionConfig.cropStatusBarColorPrimaryDark = color;
        return this;
    }

    /**
     * 裁剪页面标题文字颜色
     *
     * @param color
     * @return 使用setPictureCropStyle方法
     */
    @Deprecated
    public PictureSelectionModel setCropTitleColor(@ColorInt int color) {
        selectionConfig.cropTitleColor = color;
        return this;
    }

    /**
     * 设置相册标题右侧向上箭头图标
     *
     * @param resId
     * @return 使用setPictureStyle方法
     */
    @Deprecated
    public PictureSelectionModel setUpArrowDrawable(int resId) {
        selectionConfig.upResId = resId;
        return this;
    }

    /**
     * 设置相册标题右侧向下箭头图标
     *
     * @param resId
     * @return 使用setPictureStyle方法
     */
    @Deprecated
    public PictureSelectionModel setDownArrowDrawable(int resId) {
        selectionConfig.downResId = resId;
        return this;
    }

    /**
     * 动态设置裁剪主题样式
     *
     * @param style 裁剪页主题
     * @return
     */
    public PictureSelectionModel setPictureCropStyle(PictureCropParameterStyle style) {
        selectionConfig.cropStyle = style;
        return this;
    }

    /**
     * 动态设置相册主题样式
     *
     * @param style 主题
     * @return
     */
    public PictureSelectionModel setPictureStyle(PictureParameterStyle style) {
        selectionConfig.style = style;
        return this;
    }

    /**
     * Dynamically set the album to start and exit the animation
     *
     * @param style Activity Launch exit animation theme
     * @return
     */
    public PictureSelectionModel setPictureWindowAnimationStyle(PictureWindowAnimationStyle windowAnimationStyle) {
        selectionConfig.windowAnimationStyle = windowAnimationStyle;
        return this;
    }

    /**
     * # If you want to handle the Android Q path, if not, just return the uri，
     * The getAndroidQToPath(); field will be empty
     *
     * @param isAndroidQTransform
     * @return
     */
    public PictureSelectionModel isAndroidQTransform(boolean isAndroidQTransform) {
        selectionConfig.isAndroidQTransform = isAndroidQTransform;
        return this;
    }

    /**
     * # 内部方法-要使用此方法时最好先咨询作者！！！
     *
     * @param isFallbackVersion 仅供特殊情况内部使用 如果某功能出错此开关可以回退至之前版本
     * @return
     */
    public PictureSelectionModel isFallbackVersion(boolean isFallbackVersion) {
        selectionConfig.isFallbackVersion = isFallbackVersion;
        return this;
    }

    /**
     * # 内部方法-要使用此方法时最好先咨询作者！！！
     *
     * @param isFallbackVersion 仅供特殊情况内部使用 如果某功能出错此开关可以回退至之前版本
     * @return
     */
    public PictureSelectionModel isFallbackVersion2(boolean isFallbackVersion) {
        selectionConfig.isFallbackVersion2 = isFallbackVersion;
        return this;
    }

    /**
     * # 内部方法-要使用此方法时最好先咨询作者！！！
     *
     * @param isFallbackVersion 仅供特殊情况内部使用 如果某功能出错此开关可以回退至之前版本
     * @return
     */
    public PictureSelectionModel isFallbackVersion3(boolean isFallbackVersion) {
        selectionConfig.isFallbackVersion3 = isFallbackVersion;
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
            if (activity == null || selectionConfig == null) {
                return;
            }
            Intent intent;
            if (selectionConfig.camera && selectionConfig.isUseCustomCamera) {
                intent = new Intent(activity, PictureCustomCameraActivity.class);
            } else {
                intent = new Intent(activity, selectionConfig.camera
                        ? PictureSelectorCameraEmptyActivity.class :
                        selectionConfig.isWeChatStyle ? PictureSelectorWeChatStyleActivity.class
                                : PictureSelectorActivity.class);
            }
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.windowAnimationStyle;
            activity.overridePendingTransition(windowAnimationStyle != null &&
                    windowAnimationStyle.activityEnterAnimation != 0 ?
                    windowAnimationStyle.activityEnterAnimation :
                    R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
        }
    }

    /**
     * # replace for setPictureWindowAnimationStyle();
     * Start to select media and wait for result.
     * <p>
     * # Use PictureWindowAnimationStyle to achieve animation effects
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    @Deprecated
    public void forResult(int requestCode, int enterAnim, int exitAnim) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                return;
            }
            Intent intent = new Intent(activity, selectionConfig != null && selectionConfig.camera
                    ? PictureSelectorCameraEmptyActivity.class :
                    selectionConfig.isWeChatStyle ? PictureSelectorWeChatStyleActivity.class :
                            PictureSelectorActivity.class);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }


    /**
     * Start to select media and wait for result.
     *
     * @param listener The resulting callback listens
     */
    public void forResult(OnResultCallbackListener listener) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null || selectionConfig == null) {
                return;
            }
            // 绑定回调监听
            selectionConfig.listener = new WeakReference<>(listener).get();

            Intent intent;
            if (selectionConfig.camera && selectionConfig.isUseCustomCamera) {
                intent = new Intent(activity, PictureCustomCameraActivity.class);
            } else {
                intent = new Intent(activity, selectionConfig.camera
                        ? PictureSelectorCameraEmptyActivity.class :
                        selectionConfig.isWeChatStyle ? PictureSelectorWeChatStyleActivity.class
                                : PictureSelectorActivity.class);
            }
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivity(intent);
            } else {
                activity.startActivity(intent);
            }
            PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.windowAnimationStyle;
            activity.overridePendingTransition(windowAnimationStyle != null &&
                    windowAnimationStyle.activityEnterAnimation != 0 ?
                    windowAnimationStyle.activityEnterAnimation :
                    R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
        }
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     * @param listener    The resulting callback listens
     */
    public void forResult(int requestCode, OnResultCallbackListener listener) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null || selectionConfig == null) {
                return;
            }
            // 绑定回调监听
            selectionConfig.listener = new WeakReference<>(listener).get();
            Intent intent;
            if (selectionConfig.camera && selectionConfig.isUseCustomCamera) {
                intent = new Intent(activity, PictureCustomCameraActivity.class);
            } else {
                intent = new Intent(activity, selectionConfig.camera
                        ? PictureSelectorCameraEmptyActivity.class :
                        selectionConfig.isWeChatStyle ? PictureSelectorWeChatStyleActivity.class
                                : PictureSelectorActivity.class);
            }
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
            PictureWindowAnimationStyle windowAnimationStyle = selectionConfig.windowAnimationStyle;
            activity.overridePendingTransition(windowAnimationStyle != null &&
                    windowAnimationStyle.activityEnterAnimation != 0 ?
                    windowAnimationStyle.activityEnterAnimation :
                    R.anim.picture_anim_enter, R.anim.picture_anim_fade_in);
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
            selector.externalPicturePreview(position, medias,
                    selectionConfig.windowAnimationStyle != null &&
                            selectionConfig.windowAnimationStyle.activityPreviewEnterAnimation != 0
                            ? selectionConfig.windowAnimationStyle.activityPreviewEnterAnimation : 0);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }


    /**
     * 提供外部预览图片方法-带自定义下载保存路径
     * # 废弃 由于Android Q沙盒机制 此方法不在需要了
     *
     * @param position
     * @param medias
     */
    @Deprecated
    public void openExternalPreview(int position, String directory_path, List<LocalMedia> medias) {
        if (selector != null) {
            selector.externalPicturePreview(position, directory_path, medias,
                    selectionConfig.windowAnimationStyle != null &&
                            selectionConfig.windowAnimationStyle.activityPreviewEnterAnimation != 0
                            ? selectionConfig.windowAnimationStyle.activityPreviewEnterAnimation : 0);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }

    /**
     * set preview video
     *
     * @param path
     */
    public void externalPictureVideo(String path) {
        if (selector != null) {
            selector.externalPictureVideo(path);
        } else {
            throw new NullPointerException("This PictureSelector is Null");
        }
    }
}
