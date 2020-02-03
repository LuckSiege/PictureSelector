package com.luck.picture.lib;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: Activity基类
 */
public abstract class PictureBaseActivity extends AppCompatActivity implements Handler.Callback {
    private static final int MSG_CHOOSE_RESULT_SUCCESS = 200;
    private static final int MSG_ASY_COMPRESSION_RESULT_SUCCESS = 300;
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
        super.onCreate(savedInstanceState);
        if (isRequestedOrientation()) {
            setNewRequestedOrientation();
        }
        mHandler = new Handler(Looper.getMainLooper(), this);
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
    protected void onSaveInstanceState(Bundle outState) {
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
        if (config.synOrAsy) {
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                try {
                    List<File> files =
                            Luban.with(getContext())
                                    .loadMediaData(result)
                                    .isCamera(config.camera)
                                    .setTargetDir(config.compressSavePath)
                                    .setCompressQuality(config.compressQuality)
                                    .setFocusAlpha(config.focusAlpha)
                                    .setNewCompressFileName(config.renameCompressFileName)
                                    .ignoreBy(config.minimumCompressSize).get();

                    // 线程切换
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ASY_COMPRESSION_RESULT_SUCCESS,
                            new Object[]{result, files}));
                } catch (Exception e) {
                    onResult(result);
                    e.printStackTrace();
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
                image.setCompressed(eqVideo || flag ? false : true);
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
     */
    protected void startCrop(String originalPath) {
        if (TextUtils.isEmpty(originalPath)) {
            ToastUtils.s(this, getString(R.string.picture_not_crop_data));
            return;
        }
        UCrop.Options options = config.uCropOptions == null ? new UCrop.Options() : config.uCropOptions;
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
        options.isOpenWhiteStatusBar(isChangeStatusBarFontColor);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setDimmedLayerColor(config.circleDimmedColor);
        options.setDimmedLayerBorderColor(config.circleDimmedBorderColor);
        options.setCircleStrokeWidth(config.circleStrokeWidth);
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setShowCropFrame(config.showCropFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setHideBottomControls(config.hideBottomControls);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        options.setCropExitAnimation(config.windowAnimationStyle != null
                ? config.windowAnimationStyle.activityCropExitAnimation : 0);
        options.setNavBarColor(config.cropStyle != null ? config.cropStyle.cropNavBarColor : 0);
        options.withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y);
        if (config.cropWidth > 0 && config.cropHeight > 0) {
            options.withMaxResultSize(config.cropWidth, config.cropHeight);
        }
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        Uri uri = isHttp || isAndroidQ ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        String mimeType = PictureMimeType.getMimeTypeFromMediaContentUri(this, uri);
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_") + suffix : config.renameCropFileName);
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
    protected void startCrop(ArrayList<CutInfo> list) {
        if (list == null || list.size() == 0) {
            ToastUtils.s(this, getString(R.string.picture_not_crop_data));
            return;
        }
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
        int index = 0;
        int size = list.size();
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
        String path = size > 0 && size > index ? list.get(index).getPath() : "";
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        boolean isHttp = PictureMimeType.isHttp(path);
        Uri uri = isHttp || isAndroidQ ? Uri.parse(path) : Uri.fromFile(new File(path));
        String mimeType = PictureMimeType.getMimeTypeFromMediaContentUri(this, uri);
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
        File imageFile = new File(path.startsWith("content://") ? PictureFileUtils.getPath(getContext(), Uri.parse(path)) : path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
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
            if (config.listener != null) {
                config.listener.onResult(images);
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
        AsyncTask.SERIAL_EXECUTOR.execute(() -> {
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
                if (isCopyAndroidQToPath) {
                    String pathToAndroidQ = AndroidQTransformUtils.getPathToAndroidQ(getContext(),
                            config.cameraFileName, media);
                    media.setAndroidQToPath(pathToAndroidQ);
                    if (config.isCheckOriginalImage) {
                        media.setOriginal(true);
                        media.setOriginalPath(media.getAndroidQToPath());
                    }
                } else if (media.isCut() && media.isCompressed()) {
                    media.setAndroidQToPath(media.getCompressPath());
                } else {
                    if (config.isCheckOriginalImage) {
                        media.setOriginal(true);
                        media.setOriginalPath(media.getAndroidQToPath());
                    }
                }
            }
            // 线程切换
            mHandler.sendMessage(mHandler.obtainMessage(MSG_CHOOSE_RESULT_SUCCESS, images));
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
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     */
    protected void removeMedia(int id) {
        try {
            ContentResolver cr = getContentResolver();
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Images.Media._ID + "=?";
            cr.delete(uri, selection, new String[]{Long.toString(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @param mimeType
     * @return
     */
    protected int getLastImageId(String mimeType) {
        try {
            //selection: 指定查询条件
            String absolutePath = PictureFileUtils.getDCIMCameraPath(this, mimeType);
            String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
            String selection = MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            Cursor data = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    selection, selectionArgs, ORDER_BY);
            if (data != null && data.getCount() > 0 && data.moveToFirst()) {
                int id = data.getInt(data.getColumnIndex(MediaStore.Images.Media._ID));
                long date = data.getLong(data.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int duration = DateUtils.dateDiffer(date);
                data.close();
                // DCIM文件下最近时间1s以内的图片，可以判定是最新生成的重复照片
                return duration <= 1 ? id : -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 录音
     *
     * @param data
     */
    @Nullable
    protected String getAudioPath(Intent data) {
        boolean compare_SDK_19 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
        if (data != null && config.chooseMode == PictureMimeType.ofAudio()) {
            try {
                Uri uri = data.getData();
                final String audioPath;
                if (compare_SDK_19) {
                    audioPath = uri.getPath();
                } else {
                    audioPath = getAudioFilePathFromUri(uri);
                }
                return audioPath;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    @Nullable
    protected String getAudioFilePathFromUri(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getContentResolver()
                    .query(uri, null, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            path = cursor.getString(index);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
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
                        chooseMode, cameraFileName, config.suffixType);
                config.cameraPath = cameraFile.getAbsolutePath();

                imageUri = PictureFileUtils.parUri(this, cameraFile);
            }
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
                        chooseMode, cameraFileName, config.suffixType);
                config.cameraPath = cameraFile.getAbsolutePath();
                imageUri = PictureFileUtils.parUri(this, cameraFile);
            }
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
                startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
            }
        } else {
            PermissionChecker.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE);
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_CHOOSE_RESULT_SUCCESS:
                // 选择完成回调
                List<LocalMedia> images = (List<LocalMedia>) msg.obj;
                dismissDialog();
                if (images != null) {
                    if (config.camera
                            && config.selectionMode == PictureConfig.MULTIPLE
                            && selectionMedias != null) {
                        images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
                    }
                    if (config.listener != null) {
                        config.listener.onResult(images);
                    } else {
                        Intent intent = PictureSelector.putIntentResult(images);
                        setResult(RESULT_OK, intent);
                    }
                    closeActivity();
                }
                break;
            case MSG_ASY_COMPRESSION_RESULT_SUCCESS:
                // 异步压缩回调
                if (msg.obj != null && msg.obj instanceof Object[]) {
                    Object[] objects = (Object[]) msg.obj;
                    if (objects.length > 0) {
                        List<LocalMedia> result = (List<LocalMedia>) objects[0];
                        List<File> files = (List<File>) objects[1];
                        handleCompressCallBack(result, files);
                    }
                }
                break;
        }
        return false;
    }

    /**
     * 释放回调监听
     */
    private void releaseResultListener() {
        if (config != null) {
            config.listener = null;
            config.customVideoPlayCallback = null;
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
}
