package com.luck.picture.lib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.luck.picture.lib.engine.PictureSelectorEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.immersive.NavBarUtils;
import com.luck.picture.lib.language.PictureLanguageUtils;
import com.luck.picture.lib.listener.OnCallbackListener;
import com.luck.picture.lib.model.LocalMediaPageLoader;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.tools.AndroidQTransformUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.VoiceUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
    protected List<LocalMedia> selectionMedias = new ArrayList<>();
    protected Handler mHandler = new Handler(Looper.getMainLooper());
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
        config = PictureSelectionConfig.getInstance();
        PictureLanguageUtils.setAppLanguage(getContext(), config.language);
        setTheme(config.themeStyleId == 0 ? R.style.picture_default_style : config.themeStyleId);
        super.onCreate(savedInstanceState);
        newCreateEngine();
        newCreateResultCallbackListener();
        if (isRequestedOrientation()) {
            setNewRequestedOrientation();
        }
        initConfig();
        if (isImmersive()) {
            immersive();
        }
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_navBarColor != 0) {
                NavBarUtils.setNavBarColor(this, PictureSelectionConfig.uiStyle.picture_navBarColor);
            }
        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureNavBarColor != 0) {
                NavBarUtils.setNavBarColor(this, PictureSelectionConfig.style.pictureNavBarColor);
            }
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
            if (baseEngine != null) PictureSelectionConfig.imageEngine = baseEngine.createEngine();
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
                    PictureSelectionConfig.listener = baseEngine.getResultCallbackListener();
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
        if (config.selectionMedias != null) {
            selectionMedias.clear();
            selectionMedias.addAll(config.selectionMedias);
        }
        if (PictureSelectionConfig.uiStyle != null) {
            openWhiteStatusBar = PictureSelectionConfig.uiStyle.picture_statusBarChangeTextColor;
            if (PictureSelectionConfig.uiStyle.picture_top_titleBarBackgroundColor != 0) {
                colorPrimary = PictureSelectionConfig.uiStyle.picture_top_titleBarBackgroundColor;
            }
            if (PictureSelectionConfig.uiStyle.picture_statusBarBackgroundColor != 0) {
                colorPrimaryDark = PictureSelectionConfig.uiStyle.picture_statusBarBackgroundColor;
            }
            numComplete = PictureSelectionConfig.uiStyle.picture_switchSelectTotalStyle;

            config.checkNumMode = PictureSelectionConfig.uiStyle.picture_switchSelectNumberStyle;

        } else if (PictureSelectionConfig.style != null) {
            openWhiteStatusBar = PictureSelectionConfig.style.isChangeStatusBarFontColor;
            if (PictureSelectionConfig.style.pictureTitleBarBackgroundColor != 0) {
                colorPrimary = PictureSelectionConfig.style.pictureTitleBarBackgroundColor;
            }
            if (PictureSelectionConfig.style.pictureStatusBarColor != 0) {
                colorPrimaryDark = PictureSelectionConfig.style.pictureStatusBarColor;
            }
            numComplete = PictureSelectionConfig.style.isOpenCompletedNumStyle;
            config.checkNumMode = PictureSelectionConfig.style.isOpenCheckNumStyle;
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
    protected void compressImage(List<LocalMedia> result) {
        if (PictureSelectionConfig.compressEngine != null) {
            PictureSelectionConfig.compressEngine.onCompress(getContext(), result, new OnCallbackListener<List<LocalMedia>>() {
                @Override
                public void onCall(List<LocalMedia> result) {
                    onResult(result);
                }
            });
        } else {
            showPleaseDialog();
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
            PictureThreadUtils.executeBySingle(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {

                @Override
                public List<LocalMedia> doInBackground() throws Exception {
                    return Luban.with(getContext())
                            .loadMediaData(result)
                            .isCamera(config.camera)
                            .setTargetDir(config.compressSavePath)
                            .setCompressQuality(config.compressQuality)
                            .isAutoRotating(config.isAutoRotating)
                            .setFocusAlpha(config.focusAlpha)
                            .setNewCompressFileName(config.renameCompressFileName)
                            .ignoreBy(config.minimumCompressSize).get();
                }

                @Override
                public void onSuccess(List<LocalMedia> result) {
                    onResult(result);
                }
            });
        } else {
            Luban.with(this)
                    .loadMediaData(result)
                    .ignoreBy(config.minimumCompressSize)
                    .isCamera(config.camera)
                    .setCompressQuality(config.compressQuality)
                    .setTargetDir(config.compressSavePath)
                    .isAutoRotating(config.isAutoRotating)
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
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        if (config.isCompress) {
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
     * @param firstPath
     * @param firstMimeType
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String firstPath, String realPath, String firstMimeType, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(PictureMimeType.isContent(firstPath) ? realPath : firstPath);
        File folderFile = imageFile.getParentFile();
        for (LocalMediaFolder folder : imageFolders) {
            if (folderFile != null && folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile != null ? folderFile.getName() : "");
        newFolder.setFirstImagePath(firstPath);
        newFolder.setFirstMimeType(firstMimeType);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        if (SdkVersionUtils.checkedAndroid_Q() && config.isAndroidQTransform) {
            onResultToAndroidAsy(images);
        } else {
            dismissDialog();
            if (config.camera && config.selectionMode == PictureConfig.MULTIPLE) {
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
            exit();
        }
    }

    /**
     * Android Q
     *
     * @param images
     */
    private void onResultToAndroidAsy(List<LocalMedia> images) {
        int size = images.size();
        boolean isNextCopyAndroidQToPath = false;
        for (int i = 0; i < size; i++) {
            LocalMedia media = images.get(i);
            if (media == null || TextUtils.isEmpty(media.getPath())) {
                continue;
            }
            if (config.isCheckOriginalImage || (!media.isCut() && !media.isCompressed() && TextUtils.isEmpty(media.getAndroidQToPath()))) {
                isNextCopyAndroidQToPath = true;
                break;
            }
        }
        if (isNextCopyAndroidQToPath) {
            startThreadCopySandbox(images);
        } else {
            normalResult(images);
        }
    }

    /**
     * normal return result
     *
     * @param images
     */
    private void normalResult(List<LocalMedia> images) {
        int size = images.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = images.get(i);
            if (media == null || TextUtils.isEmpty(media.getPath())) {
                continue;
            }
            if (media.isCut() && media.isCompressed()) {
                media.setAndroidQToPath(media.getCompressPath());
            }
            if (config.isCheckOriginalImage) {
                media.setOriginal(true);
                media.setOriginalPath(media.getAndroidQToPath());
            }
        }
        if (config.camera && config.selectionMode == PictureConfig.MULTIPLE) {
            images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
        }
        if (PictureSelectionConfig.listener != null) {
            PictureSelectionConfig.listener.onResult(images);
        } else {
            Intent intent = PictureSelector.putIntentResult(images);
            setResult(RESULT_OK, intent);
        }
        exit();
    }

    /**
     * start thread copy file to Sandbox
     *
     * @param images
     */
    private void startThreadCopySandbox(List<LocalMedia> images) {
        showPleaseDialog();
        PictureThreadUtils.executeBySingle(new PictureThreadUtils.SimpleTask<List<LocalMedia>>() {
            @Override
            public List<LocalMedia> doInBackground() {
                int size = images.size();
                for (int i = 0; i < size; i++) {
                    LocalMedia media = images.get(i);
                    if (media == null || TextUtils.isEmpty(media.getPath())) {
                        continue;
                    }
                    boolean isCopyAndroidQToPath = !media.isCut() && !media.isCompressed() && TextUtils.isEmpty(media.getAndroidQToPath());
                    boolean isCopyPath = false;
                    if (isCopyAndroidQToPath && PictureMimeType.isContent(media.getPath())) {
                        if (!PictureMimeType.isHasHttp(media.getPath())) {
                            String AndroidQToPath = AndroidQTransformUtils.copyPathToAndroidQ(getContext(), media.getId(),
                                    media.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), config.cameraFileName);
                            media.setAndroidQToPath(AndroidQToPath);
                            isCopyPath = true;
                        }
                    } else if (media.isCut() && media.isCompressed()) {
                        media.setAndroidQToPath(media.getCompressPath());
                    }
                    if (config.isCheckOriginalImage) {
                        media.setOriginal(true);
                        if (isCopyPath) {
                            media.setOriginalPath(media.getAndroidQToPath());
                        } else {
                            String originalPath = AndroidQTransformUtils.copyPathToAndroidQ(getContext(), media.getId(),
                                    media.getPath(), media.getWidth(), media.getHeight(), media.getMimeType(), config.cameraFileName);
                            media.setOriginalPath(originalPath);
                        }
                    }
                }
                return images;
            }

            @Override
            public void onSuccess(List<LocalMedia> images) {
                dismissDialog();
                if (images != null) {
                    if (config.camera && config.selectionMode == PictureConfig.MULTIPLE) {
                        images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
                    }
                    if (PictureSelectionConfig.listener != null) {
                        PictureSelectionConfig.listener.onResult(images);
                    } else {
                        Intent intent = PictureSelector.putIntentResult(images);
                        setResult(RESULT_OK, intent);
                    }
                    exit();
                }
            }
        });
    }

    /**
     * Close Activity
     */
    protected void exit() {
        finish();
        if (config.camera) {
            overridePendingTransition(0, R.anim.picture_anim_fade_out);
            if (getContext() instanceof PictureSelectorCameraEmptyActivity
                    || getContext() instanceof PictureCustomCameraActivity) {
                releaseResultListener();
            }
        } else {
            overridePendingTransition(0,
                    PictureSelectionConfig.windowAnimationStyle.activityExitAnimation);
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
        try {
            Uri uri = data.getData();
            if (uri == null) {
                return "";
            }
            if (PictureMimeType.isContent(uri.toString())) {
                return uri.toString();
            } else {
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            String cameraFileName = null;
            int chooseMode = config.chooseMode == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE : config.chooseMode;
            if (!TextUtils.isEmpty(config.cameraFileName)) {
                boolean isSuffixOfImage = PictureMimeType.isSuffixOfImage(config.cameraFileName);
                config.cameraFileName = !isSuffixOfImage ? StringUtils.renameSuffix(config.cameraFileName, PictureMimeType.JPG) : config.cameraFileName;
                cameraFileName = config.camera ? config.cameraFileName : StringUtils.rename(config.cameraFileName);
            }
            if (SdkVersionUtils.checkedAndroid_Q()) {
                if (TextUtils.isEmpty(config.outPutCameraPath)) {
                    imageUri = MediaUtils.createImageUri(this, config.cameraFileName, config.suffixType);
                } else {
                    File cameraFile = PictureFileUtils.createCameraFile(this,
                            chooseMode, cameraFileName, config.suffixType, config.outPutCameraPath);
                    config.cameraPath = cameraFile.getAbsolutePath();
                    imageUri = PictureFileUtils.parUri(this, cameraFile);
                }
                if (imageUri != null) {
                    config.cameraPath = imageUri.toString();
                }
            } else {
                File cameraFile = PictureFileUtils.createCameraFile(this, chooseMode, cameraFileName, config.suffixType, config.outPutCameraPath);
                config.cameraPath = cameraFile.getAbsolutePath();
                imageUri = PictureFileUtils.parUri(this, cameraFile);
            }
            if (imageUri == null) {
                ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                if (config.camera) {
                    exit();
                }
                return;
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
            String cameraFileName = null;
            int chooseMode = config.chooseMode == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.chooseMode;
            if (!TextUtils.isEmpty(config.cameraFileName)) {
                boolean isSuffixOfImage = PictureMimeType.isSuffixOfImage(config.cameraFileName);
                config.cameraFileName = isSuffixOfImage ? StringUtils.renameSuffix(config.cameraFileName, PictureMimeType.MP4) : config.cameraFileName;
                cameraFileName = config.camera ? config.cameraFileName : StringUtils.rename(config.cameraFileName);
            }
            if (SdkVersionUtils.checkedAndroid_Q()) {
                if (TextUtils.isEmpty(config.outPutCameraPath)) {
                    videoUri = MediaUtils.createVideoUri(this, config.cameraFileName, config.suffixType);
                } else {
                    File cameraFile = PictureFileUtils.createCameraFile(this, chooseMode, cameraFileName, config.suffixType, config.outPutCameraPath);
                    config.cameraPath = cameraFile.getAbsolutePath();
                    videoUri = PictureFileUtils.parUri(this, cameraFile);
                }
                if (videoUri != null) {
                    config.cameraPath = videoUri.toString();
                }
            } else {
                File cameraFile = PictureFileUtils.createCameraFile(this, chooseMode, cameraFileName, config.suffixType, config.outPutCameraPath);
                config.cameraPath = cameraFile.getAbsolutePath();
                videoUri = PictureFileUtils.parUri(this, cameraFile);
            }
            if (videoUri == null) {
                ToastUtils.s(getContext(), "open is camera error，the uri is empty ");
                if (config.camera) {
                    exit();
                }
                return;
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
        try {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    config.cameraMimeType = PictureMimeType.ofAudio();
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        Uri audioUri = MediaUtils.createAudioUri(this, config.suffixType);
                        if (audioUri == null) {
                            ToastUtils.s(getContext(), "open is audio error，the uri is empty ");
                            if (config.camera) {
                                exit();
                            }
                            return;
                        }
                        config.cameraPath = audioUri.toString();
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, audioUri);
                    }
                    startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                } else {
                    ToastUtils.s(getContext(), "System recording is not supported");
                }
            } else {
                PermissionChecker.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, PictureConfig.APPLY_AUDIO_PERMISSIONS_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.s(getContext(), e.getMessage());
        }

    }

    /**
     * Release listener
     */
    private void releaseResultListener() {
        if (config != null) {
            PictureSelectionConfig.destroy();
            LocalMediaPageLoader.setInstanceNull();
            PictureThreadUtils.cancel(PictureThreadUtils.getSinglePool());
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
     * @param permissions
     * @param errorMsg
     */
    protected void showPermissionsDialog(boolean isCamera, String[] permissions, String errorMsg) {

    }

    /**
     * Dialog
     *
     * @param content
     */
    protected void showPromptDialog(String content) {
        if (!isFinishing()) {
            if (PictureSelectionConfig.onChooseLimitCallback != null) {
                PictureSelectionConfig.onChooseLimitCallback.onChooseLimit(getContext(), content);
            } else {
                PictureCustomDialog dialog = new PictureCustomDialog(getContext(), R.layout.picture_prompt_dialog);
                TextView btnOk = dialog.findViewById(R.id.btnOk);
                TextView tvContent = dialog.findViewById(R.id.tv_content);
                tvContent.setText(content);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isFinishing()) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
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
