package com.luck.picture.lib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.compress.OnCompressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureLoadingDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.immersive.NavBarUtils;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.VoiceUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.CutInfo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: Activity基类
 */
public abstract class PictureBaseActivity extends AppCompatActivity {
    protected PictureSelectionConfig config;
    protected boolean openWhiteStatusBar, numComplete;
    protected int colorPrimary, colorPrimaryDark;
    protected PictureLoadingDialog mLoadingDialog;
    protected List<LocalMedia> selectionMedias;
    protected Handler mHandler;
    protected View container;
    /**
     * 是否走过onSaveInstanceState方法，用于内存不足情况
     */
    protected boolean isOnSaveInstanceState;

    /**
     * 是否使用沉浸式，子类复写该方法来确定是否采用沉浸式
     *
     * @return 是否沉浸式，默认true
     */
    @Override
    public boolean isImmersive() {
        return true;
    }

    /**
     * 是否改变屏幕方向
     *
     * @return
     */
    public boolean isRequestedOrientation() {
        return true;
    }

    /**
     * 具体沉浸的样式，可以根据需要自行修改状态栏和导航栏的颜色
     */
    public void immersive() {
        ImmersiveManage.immersiveAboveAPI23(this
                , colorPrimaryDark
                , colorPrimary
                , openWhiteStatusBar);
    }


    /**
     * get Layout Resources Id
     *
     * @return
     */
    public abstract int getResourceId();

    /**
     * init Views
     */
    protected void initWidgets() {

    }

    /**
     * init PictureSelector Style
     */
    protected void initPictureSelectorStyle() {

    }

    /**
     * Set CompleteText
     */
    protected void initCompleteText(int startCount) {

    }

    /**
     * Set CompleteText
     */
    protected void initCompleteText(List<LocalMedia> list) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(PictureConfig.EXTRA_CONFIG);
        }
        isCheckConfigNull();
        // 单独拍照不设置主题因为拍照界面已经设置了透明主题了
        if (!config.camera) {
            setTheme(config.themeStyleId);
        }
        super.onCreate(savedInstanceState == null ? new Bundle() : savedInstanceState);
        if (isRequestedOrientation()) {
            setNewRequestedOrientation();
        }
        mHandler = new Handler(Looper.getMainLooper());
        initConfig();
        if (isImmersive()) {
            immersive();
        }
        // 导航条色值
        if (config.style != null && config.style.pictureNavBarColor != 0) {
            NavBarUtils.setNavBarColor(this, config.style.pictureNavBarColor);
        }
        int layoutResID = getResourceId();
        if (layoutResID != 0) {
            setContentView(layoutResID);
        }
        initWidgets();
        initPictureSelectorStyle();
        // 重置回收状态
        isOnSaveInstanceState = false;
    }

    private void isCheckConfigNull() {
        if (config == null) {
            config = PictureSelectionConfig.getInstance();
        }
    }

    /**
     * 设置屏幕方向
     */
    protected void setNewRequestedOrientation() {
        if (config != null && !config.camera) {
            setRequestedOrientation(config.requestedOrientation);
        }
    }

    /**
     * get Context
     *
     * @return this
     */
    protected Context getContext() {
        return this;
    }

    /**
     * init Config
     */
    private void initConfig() {
        // 已选图片列表
        selectionMedias = config.selectionMedias == null ? new ArrayList<>() : config.selectionMedias;
        if (config.style != null) {
            // 是否开启白色状态栏
            openWhiteStatusBar = config.style.isChangeStatusBarFontColor;
            // 标题栏背景色
            if (config.style.pictureTitleBarBackgroundColor != 0) {
                colorPrimary = config.style.pictureTitleBarBackgroundColor;
            }
            // 状态栏色值
            if (config.style.pictureStatusBarColor != 0) {
                colorPrimaryDark = config.style.pictureStatusBarColor;
            }
            // 是否是0/9样式
            numComplete = config.style.isOpenCompletedNumStyle;
            // 是否开启数字勾选模式
            config.checkNumMode = config.style.isOpenCheckNumStyle;
        } else {
            // 是否开启白色状态栏，兼容单独动态设置主题方式
            openWhiteStatusBar = config.isChangeStatusBarFontColor;
            if (!openWhiteStatusBar) {
                // 兼容老的Theme方式
                openWhiteStatusBar = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_statusFontColor);
            }

            // 是否是0/9样式，兼容单独动态设置主题方式
            numComplete = config.isOpenStyleNumComplete;
            if (!numComplete) {
                // 兼容老的Theme方式
                numComplete = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_style_numComplete);
            }

            // 是否开启数字勾选模式，兼容单独动态设置主题方式
            config.checkNumMode = config.isOpenStyleCheckNumMode;
            if (!config.checkNumMode) {
                // 兼容老的Theme方式
                config.checkNumMode = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_style_checkNumMode);
            }

            // 标题栏背景色
            if (config.titleBarBackgroundColor != 0) {
                colorPrimary = config.titleBarBackgroundColor;
            } else {
                // 兼容老的Theme方式
                colorPrimary = AttrsUtils.getTypeValueColor(this, R.attr.colorPrimary);
            }

            // 状态栏色值
            if (config.pictureStatusBarColor != 0) {
                colorPrimaryDark = config.pictureStatusBarColor;
            } else {
                // 兼容老的Theme方式
                colorPrimaryDark = AttrsUtils.getTypeValueColor(this, R.attr.colorPrimaryDark);
            }
        }

        if (config.openClickSound) {
            VoiceUtils.getInstance().init(getContext());
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        isOnSaveInstanceState = true;
        outState.putParcelable(PictureConfig.EXTRA_CONFIG, config);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        config = PictureSelectionConfig.getInstance();
        if (config != null) {
            super.attachBaseContext(PictureContextWrapper.wrap(newBase, config.language));
        }
    }

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(getContext());
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        if (!isFinishing()) {
            try {
                if (mLoadingDialog != null
                        && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
            } catch (Exception e) {
                mLoadingDialog = null;
                e.printStackTrace();
            }
        }
    }


    /**
     * compressImage
     */
    protected void compressImage(final List<LocalMedia> result) {
        showPleaseDialog();
        if (PictureSelectionConfig.cacheResourcesEngine != null) {
            // 在Android 10上通过图片加载引擎的缓存来获得沙盒内的图片
            PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {

                @Override
                public List<LocalMedia> doInBackground() {
                    int size = result.size();
                    for (int i = 0; i < size; i++) {
                        LocalMedia media = result.get(i);
                        if (media == null) {
                            continue;
                        }
                        String cachePath = PictureSelectionConfig.cacheResourcesEngine.onCachePath(getContext(), media.getPath());
                        media.setAndroidQToPath(cachePath);
                    }
                    return result;
                }

                @Override
                public void onSuccess(List<LocalMedia> result) {
                    PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
                    compressToLuban(result);
                }
            });
        } else {
            compressToLuban(result);
        }
    }

    /**
     * 调用Luban压缩
     *
     * @param result
     */
    private void compressToLuban(List<LocalMedia> result) {
        if (config.synOrAsy) {
            PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<List<File>>() {

                @Override
                public List<File> doInBackground() throws Exception {
                    return Luban.with(getContext())
                            .loadMediaData(result)
                            .isCamera(config.camera)
                            .setTargetDir(config.compressSavePath)
                            .setCompressQuality(config.compressQuality)
                            .setFocusAlpha(config.focusAlpha)
                            .setNewCompressFileName(config.renameCompressFileName)
                            .ignoreBy(config.minimumCompressSize).get();
                }

                @Override
                public void onSuccess(List<File> files) {
                    PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
                    // 异步压缩回调
                    if (files != null && files.size() > 0 && files.size() == result.size()) {
                        handleCompressCallBack(result, files);
                    } else {
                        onResult(result);
                    }
                }
            });
        } else {
            Luban.with(this)
                    .loadMediaData(result)
                    .ignoreBy(config.minimumCompressSize)
                    .isCamera(config.camera)
                    .setCompressQuality(config.compressQuality)
                    .setTargetDir(config.compressSavePath)
                    .setFocusAlpha(config.focusAlpha)
                    .setNewCompressFileName(config.renameCompressFileName)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(List<LocalMedia> list) {
                            onResult(list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            onResult(result);
                        }
                    }).launch();
        }
    }

    /**
     * 重新构造已压缩的图片返回集合
     *
     * @param images
     * @param files
     */
    private void handleCompressCallBack(List<LocalMedia> images, List<File> files) {
        if (images == null || files == null) {
            closeActivity();
            return;
        }
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        int size = images.size();
        if (files.size() == size) {
            for (int i = 0, j = size; i < j; i++) {
                // 压缩成功后的地址
                File file = files.get(i);
                String path = file.getAbsolutePath();
                LocalMedia image = images.get(i);
                // 如果是网络图片则不压缩
                boolean http = PictureMimeType.isHttp(path);
                boolean flag = !TextUtils.isEmpty(path) && http;
                boolean eqVideo = PictureMimeType.eqVideo(image.getMimeType());
                image.setCompressed(!eqVideo && !flag);
                image.setCompressPath(eqVideo || flag ? "" : path);
                if (isAndroidQ) {
                    image.setAndroidQToPath(eqVideo ? null : path);
                }
            }
        }
        onResult(images);
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     * @param mimeType
     */
    protected void startCrop(String originalPath, String mimeType) {
        if (TextUtils.isEmpty(originalPath)) {
            ToastUtils.s(this, getString(R.string.picture_not_crop_data));
            return;
        }
        // 载入裁剪样式参数配制
        UCrop.Options options = basicOptions();
        if (PictureSelectionConfig.cacheResourcesEngine != null) {
            PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<String>() {
                @Override
                public String doInBackground() {
                    return PictureSelectionConfig.cacheResourcesEngine.onCachePath(getContext(), originalPath);
                }

                @Override
                public void onSuccess(String result) {
                    PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
                    startSingleCropActivity(originalPath, result, mimeType, options);
                }
            });
        } else {
            startSingleCropActivity(originalPath, null, mimeType, options);
        }
    }

    /**
     * 单张裁剪
     *
     * @param originalPath
     * @param cachePath
     * @param mimeType
     * @param options
     */
    private void startSingleCropActivity(String originalPath, String cachePath, String mimeType, UCrop.Options options) {
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(getContext()),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_") + suffix : config.renameCropFileName);
        Uri uri;
        if (!TextUtils.isEmpty(cachePath)) {
            uri = Uri.fromFile(new File(cachePath));
        } else {
            uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        }
        UCrop.of(uri, Uri.fromFile(file))
                .withOptions(options)
                .startAnimationActivity(this, config.windowAnimationStyle != null
                        ? config.windowAnimationStyle.activityCropEnterAnimation : R.anim.picture_anim_enter);
    }

    /**
     * 多图去裁剪
     *
     * @param list
     */
    private int index = 0;

    protected void startCrop(ArrayList<CutInfo> list) {
        if (list == null || list.size() == 0) {
            ToastUtils.s(this, getString(R.string.picture_not_crop_data));
            return;
        }
        // 载入裁剪样式参数配制
        UCrop.Options options = basicOptions(list);
        int size = list.size();
        index = 0;
        if (config.chooseMode == PictureMimeType.ofAll() && config.isWithVideoImage) {
            // 视频和图片共存
            String mimeType = size > 0 ? list.get(index).getMimeType() : "";
            boolean eqVideo = PictureMimeType.eqVideo(mimeType);
            if (eqVideo) {
                // 第一个是视频就跳过直到遍历出图片为止
                for (int i = 0; i < size; i++) {
                    CutInfo cutInfo = list.get(i);
                    if (cutInfo != null && PictureMimeType.eqImage(cutInfo.getMimeType())) {
                        index = i;
                        break;
                    }
                }
            }
        }

        if (PictureSelectionConfig.cacheResourcesEngine != null) {
            PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<List<CutInfo>>() {

                @Override
                public List<CutInfo> doInBackground() {
                    for (int i = 0; i < size; i++) {
                        CutInfo cutInfo = list.get(i);
                        String cachePath = PictureSelectionConfig.cacheResourcesEngine.onCachePath(getContext(), cutInfo.getPath());
                        if (!TextUtils.isEmpty(cachePath)) {
                            cutInfo.setAndroidQToPath(cachePath);
                        }
                    }
                    return list;
                }

                @Override
                public void onSuccess(List<CutInfo> list) {
                    PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
                    if (index < size) {
                        startMultipleCropActivity(list.get(index), options);
                    }
                }
            });

        } else {
            if (index < size) {
                startMultipleCropActivity(list.get(index), options);
            }
        }
    }

    /**
     * 启动多图裁剪
     *
     * @param cutInfo
     * @param options
     */
    private void startMultipleCropActivity(CutInfo cutInfo, UCrop.Options options) {
        String path = cutInfo.getPath();
        String mimeType = cutInfo.getMimeType();
        boolean isHttp = PictureMimeType.isHttp(path);
        Uri uri;
        if (!TextUtils.isEmpty(cutInfo.getAndroidQToPath())) {
            uri = Uri.fromFile(new File(cutInfo.getAndroidQToPath()));
        } else {
            uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(path) : Uri.fromFile(new File(path));
        }
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_")
                        + suffix : config.camera ? config.renameCropFileName : StringUtils.rename(config.renameCropFileName));
        UCrop.of(uri, Uri.fromFile(file))
                .withOptions(options)
                .startAnimationMultipleCropActivity(this, config.windowAnimationStyle != null
                        ? config.windowAnimationStyle.activityCropEnterAnimation : R.anim.picture_anim_enter);
    }

    /**
     * 设置裁剪样式参数
     *
     * @return
     */
    private UCrop.Options basicOptions() {
        return basicOptions(null);
    }

    /**
     * 设置裁剪样式参数
     *
     * @return
     */
    private UCrop.Options basicOptions(ArrayList<CutInfo> list) {
        int toolbarColor = 0, statusColor = 0, titleColor = 0;
        boolean isChangeStatusBarFontColor;
        if (config.cropStyle != null) {
            if (config.cropStyle.cropTitleBarBackgroundColor != 0) {
                toolbarColor = config.cropStyle.cropTitleBarBackgroundColor;
            }
            if (config.cropStyle.cropStatusBarColorPrimaryDark != 0) {
                statusColor = config.cropStyle.cropStatusBarColorPrimaryDark;
            }
            if (config.cropStyle.cropTitleColor != 0) {
                titleColor = config.cropStyle.cropTitleColor;
            }
            isChangeStatusBarFontColor = config.cropStyle.isChangeStatusBarFontColor;
        } else {
            if (config.cropTitleBarBackgroundColor != 0) {
                toolbarColor = config.cropTitleBarBackgroundColor;
            } else {
                // 兼容老的Theme方式
                toolbarColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_toolbar_bg);
            }
            if (config.cropStatusBarColorPrimaryDark != 0) {
                statusColor = config.cropStatusBarColorPrimaryDark;
            } else {
                // 兼容老的Theme方式
                statusColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_status_color);
            }
            if (config.cropTitleColor != 0) {
                titleColor = config.cropTitleColor;
            } else {
                // 兼容老的Theme方式
                titleColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_title_color);
            }

            // 兼容单独动态设置主题方式
            isChangeStatusBarFontColor = config.isChangeStatusBarFontColor;
            if (!isChangeStatusBarFontColor) {
                // 是否改变裁剪页状态栏字体颜色 黑白切换
                isChangeStatusBarFontColor = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_statusFontColor);
            }
        }
        UCrop.Options options = config.uCropOptions == null ? new UCrop.Options() : config.uCropOptions;
        options.isOpenWhiteStatusBar(isChangeStatusBarFontColor);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setDimmedLayerColor(config.circleDimmedColor);
        options.setDimmedLayerBorderColor(config.circleDimmedBorderColor);
        options.setCircleStrokeWidth(config.circleStrokeWidth);
        options.setShowCropFrame(config.showCropFrame);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.isMultipleSkipCrop(config.isMultipleSkipCrop);
        options.setHideBottomControls(config.hideBottomControls);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setRenameCropFileName(config.renameCropFileName);
        options.isCamera(config.camera);
        options.setCutListData(list);
        options.isWithVideoImage(config.isWithVideoImage);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        options.setCropExitAnimation(config.windowAnimationStyle != null
                ? config.windowAnimationStyle.activityCropExitAnimation : 0);
        options.setNavBarColor(config.cropStyle != null ? config.cropStyle.cropNavBarColor : 0);
        options.withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y);
        options.isMultipleRecyclerAnimation(config.isMultipleRecyclerAnimation);
        if (config.cropWidth > 0 && config.cropHeight > 0) {
            options.withMaxResultSize(config.cropWidth, config.cropHeight);
        }
        return options;
    }

    /**
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        if (config.isCompress
                && !config.isCheckOriginalImage) {
            compressImage(result);
        } else {
            onResult(result);
        }
    }


    /**
     * 如果没有任何相册，先创建一个相机胶卷文件夹出来
     *
     * @param folders
     */
    protected void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = config.chooseMode == PictureMimeType.ofAudio() ?
                    getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll);
            newFolder.setName(folderName);
            newFolder.setFirstImagePath("");
            folders.add(newFolder);
        }
    }

    /**
     * 将图片插入到相机文件夹中
     *
     * @param path
     * @param imageFolders
     * @return
     */
    @Nullable
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(PictureMimeType.isContent(path) ? Objects.requireNonNull(PictureFileUtils.getPath(getContext(), Uri.parse(path))) : path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folderFile != null && folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile != null ? folderFile.getName() : "");
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        if (isAndroidQ && config.isAndroidQTransform) {
            showPleaseDialog();
            onResultToAndroidAsy(images);
        } else {
            dismissDialog();
            if (config.camera
                    && config.selectionMode == PictureConfig.MULTIPLE
                    && selectionMedias != null) {
                images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
            }
            if (config.isCheckOriginalImage) {
                int size = images.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = images.get(i);
                    media.setOriginal(true);
                    media.setOriginalPath(media.getPath());
                }
            }
            if (PictureSelectionConfig.listener != null) {
                PictureSelectionConfig.listener.onResult(images);
            } else {
                Intent intent = PictureSelector.putIntentResult(images);
                setResult(RESULT_OK, intent);
            }
            closeActivity();
        }
    }

    /**
     * 针对Android 异步处理
     *
     * @param images
     */
    private void onResultToAndroidAsy(List<LocalMedia> images) {
        PictureThreadUtils.executeByCached(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {
            @Override
            public List<LocalMedia> doInBackground() {
                // Android Q 版本做拷贝应用内沙盒适配
                int size = images.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = images.get(i);
                    if (media == null || TextUtils.isEmpty(media.getPath())) {
                        continue;
                    }
                    boolean isCopyAndroidQToPath = !media.isCut()
                            && !media.isCompressed()
                            && TextUtils.isEmpty(media.getAndroidQToPath());
                    if (isCopyAndroidQToPath && PictureMimeType.isContent(media.getPath())) {
                        String AndroidQToPath = AndroidQTransformUtils.copyPathToAndroidQ(getContext(),
                                media.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), config.cameraFileName);
                        media.setAndroidQToPath(AndroidQToPath);
                    } else if (media.isCut() && media.isCompressed()) {
                        media.setAndroidQToPath(media.getCompressPath());
                    }
                    if (config.isCheckOriginalImage) {
                        media.setOriginal(true);
                        media.setOriginalPath(media.getAndroidQToPath());
                    }
                }
                return images;
            }

            @Override
            public void onSuccess(List<LocalMedia> images) {
                PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
                dismissDialog();
                if (images != null) {
                    if (config.camera
                            && config.selectionMode == PictureConfig.MULTIPLE
                            && selectionMedias != null) {
                        images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
                    }
                    if (PictureSelectionConfig.listener != null) {
                        PictureSelectionConfig.listener.onResult(images);
                    } else {
                        Intent intent = PictureSelector.putIntentResult(images);
                        setResult(RESULT_OK, intent);
                    }
                    closeActivity();
                }
            }
        });
    }

    /**
     * Close Activity
     */
    protected void closeActivity() {
        finish();
        if (config.camera) {
            overridePendingTransition(0, R.anim.picture_anim_fade_out);
        } else {
            overridePendingTransition(0, config.windowAnimationStyle != null
                    && config.windowAnimationStyle.activityExitAnimation != 0 ?
                    config.windowAnimationStyle.activityExitAnimation : R.anim.picture_anim_exit);
        }
        // 关闭主界面后才释放回调监听
        if (getContext() instanceof PictureSelectorActivity) {
            releaseResultListener();
            if (config.openClickSound) {
                VoiceUtils.getInstance().releaseSoundPool();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        mLoadingDialog = null;
    }


    /**
     * 录音
     *
     * @param data
     */
    protected String getAudioPath(Intent data) {
        boolean compare_SDK_19 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
        if (data != null && config.chooseMode == PictureMimeType.ofAudio()) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    return compare_SDK_19 ? uri.getPath() : MediaUtils.getAudioFilePathFromUri(getContext(), uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    /**
     * start to camera、preview、crop
     */
    protected void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri;
            if (SdkVersionUtils.checkedAndroid_Q()) {
                imageUri = MediaUtils.createImageUri(getApplicationContext());
                if (imageUri != null) {
                    config.cameraPath = imageUri.toString();
                } else {
                    ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                    if (config.camera) {
                        closeActivity();
                    }
                    return;
                }
            } else {
                int chooseMode = config.chooseMode == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE
                        : config.chooseMode;
                String cameraFileName = "";
                if (!TextUtils.isEmpty(config.cameraFileName)) {
                    boolean isSuffixOfImage = PictureMimeType.isSuffixOfImage(config.cameraFileName);
                    config.cameraFileName = !isSuffixOfImage ? StringUtils.renameSuffix(config.cameraFileName, PictureMimeType.JPEG) : config.cameraFileName;
                    cameraFileName = config.camera ? config.cameraFileName : StringUtils.rename(config.cameraFileName);
                }

                File cameraFile = PictureFileUtils.createCameraFile(getApplicationContext(),
                        chooseMode, cameraFileName, config.suffixType, config.outPutCameraPath);
                if (cameraFile != null) {
                    config.cameraPath = cameraFile.getAbsolutePath();
                    imageUri = PictureFileUtils.parUri(this, cameraFile);
                } else {
                    ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                    if (config.camera) {
                        closeActivity();
                    }
                    return;
                }
            }
            config.cameraMimeType = PictureMimeType.ofImage();
            if (config.isCameraAroundState) {
                cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }


    /**
     * start to camera、video
     */
    protected void startOpenCameraVideo() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            Uri imageUri;
            if (SdkVersionUtils.checkedAndroid_Q()) {
                imageUri = MediaUtils.createVideoUri(getApplicationContext());
                if (imageUri != null) {
                    config.cameraPath = imageUri.toString();
                } else {
                    ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                    if (config.camera) {
                        closeActivity();
                    }
                    return;
                }
            } else {
                int chooseMode = config.chooseMode ==
                        PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.chooseMode;
                String cameraFileName = "";
                if (!TextUtils.isEmpty(config.cameraFileName)) {
                    boolean isSuffixOfImage = PictureMimeType.isSuffixOfImage(config.cameraFileName);
                    config.cameraFileName = isSuffixOfImage ? StringUtils.renameSuffix(config.cameraFileName, PictureMimeType.MP4) : config.cameraFileName;
                    cameraFileName = config.camera ? config.cameraFileName : StringUtils.rename(config.cameraFileName);
                }
                File cameraFile = PictureFileUtils.createCameraFile(getApplicationContext(),
                        chooseMode, cameraFileName, config.suffixType, config.outPutCameraPath);
                if (cameraFile != null) {
                    config.cameraPath = cameraFile.getAbsolutePath();
                    imageUri = PictureFileUtils.parUri(this, cameraFile);
                } else {
                    ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                    if (config.camera) {
                        closeActivity();
                    }
                    return;
                }
            }
            config.cameraMimeType = PictureMimeType.ofVideo();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (config.isCameraAroundState) {
                cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoQuality);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera audio
     */
    public void startOpenCameraAudio() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                config.cameraMimeType = PictureMimeType.ofAudio();
                startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
            }
        } else {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE);
        }
    }

    /**
     * 释放回调监听
     */
    private void releaseResultListener() {
        if (config != null) {
            PictureSelectionConfig.listener = null;
            PictureSelectionConfig.customVideoPlayCallback = null;
            PictureSelectionConfig.onPictureSelectorInterfaceListener = null;
            PictureSelectionConfig.cacheResourcesEngine = null;
            PictureThreadUtils.cancel(PictureThreadUtils.getCachedPool());
            PictureThreadUtils.cancel(PictureThreadUtils.getSinglePool());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE:
                // 录音权限
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                } else {
                    ToastUtils.s(getContext(), getString(R.string.picture_audio));
                }
                break;
        }
    }

    /**
     * 权限提示
     */
    protected void showPermissionsDialog(boolean isCamera, String errorMsg) {

    }
}
