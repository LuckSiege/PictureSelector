package com.luck.picture.lib;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
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
import com.luck.picture.lib.language.LocaleTransform;
import com.luck.picture.lib.language.PictureLanguageUtils;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
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
    protected String cameraPath;
    protected String originalPath;
    protected PictureLoadingDialog dialog;
    protected PictureLoadingDialog compressDialog;
    protected List<LocalMedia> selectionMedias;
    protected Handler mHandler;
    protected View container;

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
     * 获取布局文件
     *
     * @return
     */
    public abstract int getResourceId();

    protected void initWidgets() {

    }

    protected void initPictureSelectorStyle() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(PictureConfig.EXTRA_CONFIG);
            cameraPath = savedInstanceState.getString(PictureConfig.BUNDLE_CAMERA_PATH);
            originalPath = savedInstanceState.getString(PictureConfig.BUNDLE_ORIGINAL_PATH);
        } else {
            config = PictureSelectionConfig.getInstance();
        }
        setTheme(config.themeStyleId);
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
    }

    /**
     * 设置屏幕方向
     */
    protected void setNewRequestedOrientation() {
        if (config != null) {
            setRequestedOrientation(config.requestedOrientation);
        }
    }

    /**
     * 获取Context上下文
     *
     * @return
     */
    protected Context getContext() {
        return this;
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        // 设置语言
        if (config.language >= 0) {
            PictureLanguageUtils.applyLanguage(this, LocaleTransform.getLanguage(config.language));
        } else {
            PictureLanguageUtils.setDefaultLanguage(this);
        }
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PictureConfig.BUNDLE_CAMERA_PATH, cameraPath);
        outState.putString(PictureConfig.BUNDLE_ORIGINAL_PATH, originalPath);
        outState.putParcelable(PictureConfig.EXTRA_CONFIG, config);
    }


    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog = new PictureLoadingDialog(getContext());
            dialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        try {
            if (dialog != null
                    && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } catch (Exception e) {
            dialog = null;
            e.printStackTrace();
        }
    }

    /**
     * compress loading dialog
     */
    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog = new PictureLoadingDialog(this);
            compressDialog.show();
        }
    }

    /**
     * dismiss compress dialog
     */
    protected void dismissCompressDialog() {
        try {
            if (!isFinishing()
                    && compressDialog != null
                    && compressDialog.isShowing()) {
                compressDialog.dismiss();
                compressDialog = null;
            }
        } catch (Exception e) {
            compressDialog = null;
            e.printStackTrace();
        }
    }


    /**
     * compressImage
     */
    protected void compressImage(final List<LocalMedia> result) {
        showCompressDialog();
        if (config.synOrAsy) {
            AsyncTask.SERIAL_EXECUTOR.execute(() -> {
                try {
                    List<File> files =
                            Luban.with(getContext())
                                    .loadMediaData(result)
                                    .setTargetDir(config.compressSavePath)
                                    .setCompressQuality(config.compressQuality)
                                    .setRenameListener(filePath -> config.renameCompressFileName)
                                    .ignoreBy(config.minimumCompressSize).get();
                    // 线程切换
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ASY_COMPRESSION_RESULT_SUCCESS,
                            new Object[]{result, files}));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            Luban.with(this)
                    .loadMediaData(result)
                    .ignoreBy(config.minimumCompressSize)
                    .setCompressQuality(config.compressQuality)
                    .setTargetDir(config.compressSavePath)
                    .setRenameListener(filePath -> config.renameCompressFileName)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(List<LocalMedia> list) {
                            BroadcastManager.getInstance(getApplicationContext())
                                    .action(BroadcastAction.ACTION_CLOSE_PREVIEW).broadcast();
                            onResult(list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            BroadcastManager.getInstance(getApplicationContext())
                                    .action(BroadcastAction.ACTION_CLOSE_PREVIEW).broadcast();
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
                String path = file.getPath();
                LocalMedia image = images.get(i);
                // 如果是网络图片则不压缩
                boolean http = PictureMimeType.isHttp(path);
                boolean flag = !TextUtils.isEmpty(path) && http;
                image.setCompressed(flag ? false : true);
                image.setCompressPath(flag ? "" : path);
                if (isAndroidQ) {
                    image.setAndroidQToPath(path);
                }
            }
        }
        BroadcastManager.getInstance(getApplicationContext())
                .action(BroadcastAction.ACTION_CLOSE_PREVIEW).broadcast();
        onResult(images);
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
        UCrop.Options options = new UCrop.Options();
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
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        String imgType = isAndroidQ ? PictureMimeType
                .getLastImgSuffix(PictureMimeType.getMimeType(getContext(), Uri.parse(originalPath)))
                : PictureMimeType.getLastImgType(originalPath);
        Uri uri = isHttp || isAndroidQ ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_") + imgType : config.renameCropFileName);
        UCrop.of(uri, Uri.fromFile(file))
                .withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y)
                .withMaxResultSize(config.cropWidth, config.cropHeight)
                .withOptions(options)
                .startAnimation(this, config.windowAnimationStyle != null
                        ? config.windowAnimationStyle.activityCropEnterAnimation : 0);
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
        UCropMulti.Options options = new UCropMulti.Options();
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
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setShowCropFrame(config.showCropFrame);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.setHideBottomControls(config.hideBottomControls);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setCutListData(list);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        options.setCropExitAnimation(config.windowAnimationStyle != null
                ? config.windowAnimationStyle.activityCropExitAnimation : 0);
        options.setNavBarColor(config.cropStyle != null ? config.cropStyle.cropNavBarColor : 0);
        String path = list.size() > 0 ? list.get(0).getPath() : "";
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        boolean isHttp = PictureMimeType.isHttp(path);
        String imgType = isAndroidQ ? PictureMimeType
                .getLastImgSuffix(PictureMimeType.getMimeType(getContext(), Uri.parse(path)))
                : PictureMimeType.getLastImgType(path);
        Uri uri = isHttp || isAndroidQ ? Uri.parse(path) : Uri.fromFile(new File(path));
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_")
                        + imgType : config.renameCropFileName);
        UCropMulti.of(uri, Uri.fromFile(file))
                .withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y)
                .withMaxResultSize(config.cropWidth, config.cropHeight)
                .withOptions(options)
                .startAnimation(this, config.windowAnimationStyle != null
                        ? config.windowAnimationStyle.activityCropEnterAnimation : 0);
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
     * 如果没有任何相册，先创建一个最近相册出来
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
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
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
        boolean isVideo = PictureMimeType.eqVideo(images != null && images.size() > 0
                ? images.get(0).getMimeType() : "");
        if (isAndroidQ && !isVideo) {
            showCompressDialog();
        }
        if (isAndroidQ) {
            onResultToAndroidAsy(images);
        } else {
            dismissCompressDialog();
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
            Intent intent = PictureSelector.putIntentResult(images);
            setResult(RESULT_OK, intent);
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
                    media.setAndroidQToPath(getPathToAndroidQ(media));
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
     * 复制一份至自己应用沙盒内
     *
     * @param media
     * @return
     */
    private String getPathToAndroidQ(LocalMedia media) {
        if (PictureMimeType.eqVideo(media.getMimeType())) {
            return AndroidQTransformUtils.parseVideoPathToAndroidQ
                    (getApplicationContext(), media.getPath(), config.cameraFileName, media.getMimeType());
        } else if (PictureMimeType.eqAudio(media.getMimeType())) {
            return AndroidQTransformUtils.parseAudioPathToAndroidQ
                    (getApplicationContext(), media.getPath(), config.cameraFileName, media.getMimeType());
        } else {
            return AndroidQTransformUtils.parseImagePathToAndroidQ
                    (getApplicationContext(), media.getPath(), config.cameraFileName, media.getMimeType());
        }
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissCompressDialog();
        dismissDialog();
    }


    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     * @param eqVideo
     */
    @Deprecated
    protected void removeImage(int id, boolean eqVideo) {
        try {
            ContentResolver cr = getContentResolver();
            Uri uri = eqVideo ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = eqVideo ? MediaStore.Video.Media._ID + "=?"
                    : MediaStore.Images.Media._ID + "=?";
            cr.delete(uri,
                    selection,
                    new String[]{Long.toString(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    cameraPath = imageUri.toString();
                }
            } else {
                int chooseMode = config.chooseMode == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE
                        : config.chooseMode;
                File cameraFile = PictureFileUtils.createCameraFile(getApplicationContext(),
                        chooseMode, config.cameraFileName, config.suffixType);
                cameraPath = cameraFile.getAbsolutePath();
                imageUri = PictureFileUtils.parUri(this, cameraFile);
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
                    cameraPath = imageUri.toString();
                }
            } else {
                int chooseMode = config.chooseMode ==
                        PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.chooseMode;
                File cameraFile = PictureFileUtils.createCameraFile(getApplicationContext(), chooseMode, config.cameraFileName,
                        config.suffixType);
                cameraPath = cameraFile.getAbsolutePath();
                imageUri = PictureFileUtils.parUri(this, cameraFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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

    /**
     * 多张图片裁剪
     *
     * @param data
     */
    protected void multiCropHandleResult(Intent data) {
        List<LocalMedia> medias = new ArrayList<>();
        List<CutInfo> mCuts = UCropMulti.getOutput(data);
        int size = mCuts.size();
        boolean isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
        for (int i = 0; i < size; i++) {
            CutInfo c = mCuts.get(i);
            LocalMedia media = new LocalMedia();
            media.setCut(TextUtils.isEmpty(c.getCutPath()) ? false : true);
            media.setPath(c.getPath());
            media.setCutPath(c.getCutPath());
            media.setMimeType(c.getMimeType());
            media.setWidth(c.getImageWidth());
            media.setHeight(c.getImageHeight());
            media.setSize(new File(TextUtils.isEmpty(c.getCutPath())
                    ? c.getPath() : c.getCutPath()).length());
            media.setChooseModel(config.chooseMode);
            if (isAndroidQ) {
                media.setAndroidQToPath(c.getCutPath());
            }
            medias.add(media);
        }
        handlerResult(medias);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_CHOOSE_RESULT_SUCCESS:
                // 选择完成回调
                List<LocalMedia> images = (List<LocalMedia>) msg.obj;
                dismissCompressDialog();
                if (images != null) {
                    if (config.camera
                            && config.selectionMode == PictureConfig.MULTIPLE
                            && selectionMedias != null) {
                        images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
                    }
                    Intent intent = PictureSelector.putIntentResult(images);
                    setResult(RESULT_OK, intent);
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
