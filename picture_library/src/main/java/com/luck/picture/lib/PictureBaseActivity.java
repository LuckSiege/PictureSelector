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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.compress.OnCompressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.dialog.PictureLoadingDialog;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.PictureSelectorEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.immersive.NavBarUtils;
import com.luck.picture.lib.language.PictureLanguageUtils;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.model.LocalMediaPageLoader;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @describe: BaseActivity
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
     * if there more
     */
    protected boolean isHasMore = true;
    /**
     * page
     */
    protected int mPage = 1;
    /**
     * is onSaveInstanceState
     */
    protected boolean isOnSaveInstanceState;

    /**
     * Whether to use immersion, subclasses copy the method to determine whether to use immersion
     *
     * @return
     */
    @Override
    public boolean isImmersive() {
        return true;
    }

    /**
     * Whether to change the screen direction
     *
     * @return
     */
    public boolean isRequestedOrientation() {
        return true;
    }


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
        if (config == null) {
            config = getIntent() != null ? getIntent().getParcelableExtra(PictureConfig.EXTRA_CONFIG) : config;
        }
        checkConfigNull();
        PictureLanguageUtils.setAppLanguage(getContext(), config.language);
        if (!config.camera) {
            setTheme(config.themeStyleId == 0 ? R.style.picture_default_style : config.themeStyleId);
        }
        super.onCreate(savedInstanceState == null ? new Bundle() : savedInstanceState);
        newCreateEngine();
        newCreateResultCallbackListener();
        if (isRequestedOrientation()) {
            setNewRequestedOrientation();
        }
        mHandler = new Handler(Looper.getMainLooper());
        initConfig();
        if (isImmersive()) {
            immersive();
        }
        if (config.style != null && config.style.pictureNavBarColor != 0) {
            NavBarUtils.setNavBarColor(this, config.style.pictureNavBarColor);
        }
        int layoutResID = getResourceId();
        if (layoutResID != 0) {
            setContentView(layoutResID);
        }
        initWidgets();
        initPictureSelectorStyle();
        isOnSaveInstanceState = false;
    }

    /**
     * Get the image loading engine again, provided that the user implements the IApp interface in the Application
     */
    private void newCreateEngine() {
        if (PictureSelectionConfig.imageEngine == null) {
            PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
            if (baseEngine != null) {
                ImageEngine engine = baseEngine.createEngine();
                PictureSelectionConfig.imageEngine = engine;
            }
        }
    }

    /**
     * Retrieve the result callback listener, provided that the user implements the IApp interface in the Application
     */
    private void newCreateResultCallbackListener() {
        if (config.isCallbackMode) {
            if (PictureSelectionConfig.listener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    OnResultCallbackListener<LocalMedia> listener = baseEngine.getResultCallbackListener();
                    PictureSelectionConfig.listener = listener;
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if (config == null) {
            super.attachBaseContext(newBase);
        } else {
            super.attachBaseContext(PictureContextWrapper.wrap(newBase, config.language));
        }
    }

    /**
     * CheckConfigNull
     */
    private void checkConfigNull() {
        if (config == null) {
            config = PictureSelectionConfig.getInstance();
        }
    }

    /**
     * setNewRequestedOrientation
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
        selectionMedias = config.selectionMedias == null ? new ArrayList<>() : config.selectionMedias;
        if (config.style != null) {
            openWhiteStatusBar = config.style.isChangeStatusBarFontColor;
            if (config.style.pictureTitleBarBackgroundColor != 0) {
                colorPrimary = config.style.pictureTitleBarBackgroundColor;
            }
            if (config.style.pictureStatusBarColor != 0) {
                colorPrimaryDark = config.style.pictureStatusBarColor;
            }
            numComplete = config.style.isOpenCompletedNumStyle;
            config.checkNumMode = config.style.isOpenCheckNumStyle;
        } else {
            openWhiteStatusBar = config.isChangeStatusBarFontColor;
            if (!openWhiteStatusBar) {
                openWhiteStatusBar = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_statusFontColor);
            }

            numComplete = config.isOpenStyleNumComplete;
            if (!numComplete) {
                numComplete = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_style_numComplete);
            }

            config.checkNumMode = config.isOpenStyleCheckNumMode;
            if (!config.checkNumMode) {
                config.checkNumMode = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_style_checkNumMode);
            }

            if (config.titleBarBackgroundColor != 0) {
                colorPrimary = config.titleBarBackgroundColor;
            } else {
                colorPrimary = AttrsUtils.getTypeValueColor(this, R.attr.colorPrimary);
            }

            if (config.pictureStatusBarColor != 0) {
                colorPrimaryDark = config.pictureStatusBarColor;
            } else {
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

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        try {
            if (!isFinishing()) {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new PictureLoadingDialog(getContext());
                }
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                }
                mLoadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {

                @Override
                public List<LocalMedia> doInBackground() {
                    int size = result.size();
                    for (int i = 0; i < size; i++) {
                        LocalMedia media = result.get(i);
                        if (media == null) {
                            continue;
                        }
                        if (!PictureMimeType.isHasHttp(media.getPath())) {
                            String cachePath = PictureSelectionConfig.cacheResourcesEngine.onCachePath(getContext(), media.getPath());
                            media.setAndroidQToPath(cachePath);
                        }
                    }
                    return result;
                }

                @Override
                public void onSuccess(List<LocalMedia> result) {
                    compressToLuban(result);
                }
            });
        } else {
            compressToLuban(result);
        }
    }

    /**
     * compress
     *
     * @param result
     */
    private void compressToLuban(List<LocalMedia> result) {
        if (config.synOrAsy) {
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<File>>() {

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
     * handleCompressCallBack
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
                File file = files.get(i);
                if (file == null) {
                    continue;
                }
                String path = file.getAbsolutePath();
                LocalMedia image = images.get(i);
                boolean http = PictureMimeType.isHasHttp(path);
                boolean flag = !TextUtils.isEmpty(path) && http;
                boolean isHasVideo = PictureMimeType.isHasVideo(image.getMimeType());
                image.setCompressed(!isHasVideo && !flag);
                image.setCompressPath(isHasVideo || flag ? null : path);
                if (isAndroidQ) {
                    image.setAndroidQToPath(image.getCompressPath());
                }
            }
        }
        onResult(images);
    }

    /**
     * crop
     *
     * @param originalPath
     * @param mimeType
     */
    protected void startCrop(String originalPath, String mimeType) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (TextUtils.isEmpty(originalPath)) {
            ToastUtils.s(this, getString(R.string.picture_not_crop_data));
            return;
        }
        UCrop.Options options = basicOptions();
        if (PictureSelectionConfig.cacheResourcesEngine != null) {
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<String>() {
                @Override
                public String doInBackground() {
                    return PictureSelectionConfig.cacheResourcesEngine.onCachePath(getContext(), originalPath);
                }

                @Override
                public void onSuccess(String result) {
                    startSingleCropActivity(originalPath, result, mimeType, options);
                }
            });
        } else {
            startSingleCropActivity(originalPath, null, mimeType, options);
        }
    }

    /**
     * single crop
     *
     * @param originalPath
     * @param cachePath
     * @param mimeType
     * @param options
     */
    private void startSingleCropActivity(String originalPath, String cachePath, String mimeType, UCrop.Options options) {
        boolean isHttp = PictureMimeType.isHasHttp(originalPath);
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(getContext()),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_CROP_") + suffix : config.renameCropFileName);
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
     * multiple crop
     *
     * @param list
     */
    private int index = 0;

    protected void startCrop(ArrayList<CutInfo> list) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (list == null || list.size() == 0) {
            ToastUtils.s(this, getString(R.string.picture_not_crop_data));
            return;
        }
        UCrop.Options options = basicOptions(list);
        int size = list.size();
        index = 0;
        if (config.chooseMode == PictureMimeType.ofAll() && config.isWithVideoImage) {
            String mimeType = size > 0 ? list.get(index).getMimeType() : "";
            boolean isHasVideo = PictureMimeType.isHasVideo(mimeType);
            if (isHasVideo) {
                for (int i = 0; i < size; i++) {
                    CutInfo cutInfo = list.get(i);
                    if (cutInfo != null && PictureMimeType.isHasImage(cutInfo.getMimeType())) {
                        index = i;
                        break;
                    }
                }
            }
        }

        if (PictureSelectionConfig.cacheResourcesEngine != null) {
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<CutInfo>>() {

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
                    if (index < size) {
                        startMultipleCropActivity(list.get(index), size, options);
                    }
                }
            });

        } else {
            if (index < size) {
                startMultipleCropActivity(list.get(index), size, options);
            }
        }
    }

    /**
     * startMultipleCropActivity
     *
     * @param cutInfo
     * @param options
     */
    private void startMultipleCropActivity(CutInfo cutInfo, int count, UCrop.Options options) {
        String path = cutInfo.getPath();
        String mimeType = cutInfo.getMimeType();
        boolean isHttp = PictureMimeType.isHasHttp(path);
        Uri uri;
        if (!TextUtils.isEmpty(cutInfo.getAndroidQToPath())) {
            uri = Uri.fromFile(new File(cutInfo.getAndroidQToPath()));
        } else {
            uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(path) : Uri.fromFile(new File(path));
        }
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(this),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_CROP_")
                        + suffix : config.camera || count == 1 ? config.renameCropFileName : StringUtils.rename(config.renameCropFileName));
        UCrop.of(uri, Uri.fromFile(file))
                .withOptions(options)
                .startAnimationMultipleCropActivity(this, config.windowAnimationStyle != null
                        ? config.windowAnimationStyle.activityCropEnterAnimation : R.anim.picture_anim_enter);
    }

    /**
     * Set the crop style parameter
     *
     * @return
     */
    private UCrop.Options basicOptions() {
        return basicOptions(null);
    }

    /**
     * Set the crop style parameter
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
                toolbarColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_toolbar_bg);
            }
            if (config.cropStatusBarColorPrimaryDark != 0) {
                statusColor = config.cropStatusBarColorPrimaryDark;
            } else {
                statusColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_status_color);
            }
            if (config.cropTitleColor != 0) {
                titleColor = config.cropTitleColor;
            } else {
                titleColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_title_color);
            }

            isChangeStatusBarFontColor = config.isChangeStatusBarFontColor;
            if (!isChangeStatusBarFontColor) {
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
     * If you don't have any albums, first create a camera film folder to come out
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
            newFolder.setCameraFolder(true);
            newFolder.setBucketId(-1);
            newFolder.setChecked(true);
            folders.add(newFolder);
        }
    }

    /**
     * Insert the image into the camera folder
     *
     * @param path
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String path, String realPath, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(PictureMimeType.isContent(path) ? realPath : path);
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
     * Android Q
     *
     * @param images
     */
    private void onResultToAndroidAsy(List<LocalMedia> images) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {
            @Override
            public List<LocalMedia> doInBackground() {
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
                        if (!PictureMimeType.isHasHttp(media.getPath())) {
                            String AndroidQToPath = AndroidQTransformUtils.copyPathToAndroidQ(getContext(),
                                    media.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), config.cameraFileName);
                            media.setAndroidQToPath(AndroidQToPath);
                        }
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
        if (config.camera) {
            if (getContext() instanceof PictureSelectorCameraEmptyActivity
                    || getContext() instanceof PictureCustomCameraActivity) {
                releaseResultListener();
            }
        } else {
            if (getContext() instanceof PictureSelectorActivity) {
                releaseResultListener();
                if (config.openClickSound) {
                    VoiceUtils.getInstance().releaseSoundPool();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }


    /**
     * get audio path
     *
     * @param data
     */
    protected String getAudioPath(Intent data) {
        if (data != null && config.chooseMode == PictureMimeType.ofAudio()) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ? uri.getPath() : MediaUtils.getAudioFilePathFromUri(getContext(), uri);
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
                imageUri = MediaUtils.createImageUri(getApplicationContext(), config.suffixType);
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
            Uri videoUri;
            if (SdkVersionUtils.checkedAndroid_Q()) {
                videoUri = MediaUtils.createVideoUri(getApplicationContext(), config.suffixType);
                if (videoUri != null) {
                    config.cameraPath = videoUri.toString();
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
                    videoUri = PictureFileUtils.parUri(this, cameraFile);
                } else {
                    ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                    if (config.camera) {
                        closeActivity();
                    }
                    return;
                }
            }
            config.cameraMimeType = PictureMimeType.ofVideo();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            if (config.isCameraAroundState) {
                cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
            }
            cameraIntent.putExtra(PictureConfig.EXTRA_QUICK_CAPTURE, config.isQuickCapture);
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
     * Release listener
     */
    private void releaseResultListener() {
        if (config != null) {
            PictureSelectionConfig.destroy();
            LocalMediaPageLoader.setInstanceNull();
            PictureThreadUtils.cancel(PictureThreadUtils.getIoPool());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                }
            } else {
                ToastUtils.s(getContext(), getString(R.string.picture_audio));
            }
        }
    }

    /**
     * showPermissionsDialog
     *
     * @param isCamera
     * @param errorMsg
     */
    protected void showPermissionsDialog(boolean isCamera, String errorMsg) {

    }

    /**
     * Dialog
     *
     * @param content
     */
    protected void showPromptDialog(String content) {
        if (!isFinishing()) {
            PictureCustomDialog dialog = new PictureCustomDialog(getContext(), R.layout.picture_prompt_dialog);
            TextView btnOk = dialog.findViewById(R.id.btnOk);
            TextView tvContent = dialog.findViewById(R.id.tv_content);
            tvContent.setText(content);
            btnOk.setOnClickListener(v -> {
                if (!isFinishing()) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }


    /**
     * sort
     *
     * @param imageFolders
     */
    protected void sortFolder(List<LocalMediaFolder> imageFolders) {
        Collections.sort(imageFolders, (lhs, rhs) -> {
            if (lhs.getData() == null || rhs.getData() == null) {
                return 0;
            }
            int lSize = lhs.getImageNum();
            int rSize = rhs.getImageNum();
            return Integer.compare(rSize, lSize);
        });
    }
}
