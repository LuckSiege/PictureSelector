package com.luck.picture.lib.basic;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.PictureOnlyCameraFragment;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.config.Crop;
import com.luck.picture.lib.config.CustomField;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.dialog.PhotoItemSelectedDialog;
import com.luck.picture.lib.dialog.PictureLoadingDialog;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.PictureSelectorEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.immersive.ImmersiveManager;
import com.luck.picture.lib.interfaces.OnCallbackIndexListener;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnItemClickListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.language.PictureLanguageUtils;
import com.luck.picture.lib.loader.IBridgeMediaLoader;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.permissions.PermissionUtil;
import com.luck.picture.lib.service.ForegroundService;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.thread.PictureThreadUtils;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.BitmapUtils;
import com.luck.picture.lib.utils.MediaStoreUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.ValueOf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：PictureCommonFragment
 */
public abstract class PictureCommonFragment extends Fragment implements IPictureSelectorCommonEvent {

    /**
     * PermissionResultCallback
     */
    private PermissionResultCallback mPermissionResultCallback;

    /**
     * page
     */
    protected int mPage = 1;

    /**
     * Media Loader engine
     */
    protected IBridgeMediaLoader mLoader;

    /**
     * IBridgePictureBehavior
     */
    protected IBridgePictureBehavior iBridgePictureBehavior;

    /**
     * PictureSelector Config
     */
    protected PictureSelectionConfig config;

    /**
     * Loading Dialog
     */
    private PictureLoadingDialog mLoadingDialog;

    /**
     * click sound
     */
    private SoundPool soundPool;

    /**
     * click sound effect id
     */
    private int soundID;


    @Override
    public int getResourceId() {
        return 0;
    }


    @Override
    public void onCheckOriginalChange() {

    }

    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {

    }


    @Override
    public void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia) {

    }

    @Override
    public void onFixedSelectedChange(LocalMedia oldLocalMedia) {

    }

    @Override
    public void sendChangeSubSelectPositionEvent(boolean adapterChange) {

    }

    @Override
    public void handlePermissionSettingResult() {

    }

    @Override
    public void onEditMedia(Intent intent) {

    }

    @Override
    public void onExitFragment() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionResultCallback != null) {
            PermissionChecker.getInstance().onRequestPermissionsResult(grantResults, mPermissionResultCallback);
            mPermissionResultCallback = null;
        }
    }

    /**
     * Set PermissionResultCallback
     *
     * @param callback
     */
    public void setPermissionsResultAction(PermissionResultCallback callback) {
        mPermissionResultCallback = callback;
    }

    @Override
    public void handlePermissionDenied(String[] permissionArray) {
        boolean isReadWrite = permissionArray == PermissionConfig.READ_WRITE_EXTERNAL_STORAGE
                || permissionArray == PermissionConfig.WRITE_EXTERNAL_STORAGE;
        PermissionUtil.goIntentSetting(this, isReadWrite, PictureConfig.REQUEST_GO_SETTING);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getResourceId() != 0) {
            return inflater.inflate(getResourceId(), container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingDialog = new PictureLoadingDialog(getContext());
        if (savedInstanceState != null) {
            config = savedInstanceState.getParcelable(PictureConfig.EXTRA_PICTURE_SELECTOR_CONFIG);
        }
        if (config == null) {
            config = PictureSelectionConfig.getInstance();
        }
        if (config.isPreviewFullScreenMode) {
            SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
            boolean isDarkStatusBarBlack = selectMainStyle.isDarkStatusBarBlack();
            ImmersiveManager.translucentStatusBar(getActivity(), true, isDarkStatusBarBlack);
        }
        if (config.isOpenClickSound && !config.isOnlyCamera) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundID = soundPool.load(getContext(), R.raw.ps_click_music, 1);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initAppLanguage();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (config != null) {
            outState.putParcelable(PictureConfig.EXTRA_PICTURE_SELECTOR_CONFIG, config);
        }
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), windowAnimationStyle.activityEnterAnimation);
        } else {
            onExitFragment();
            return AnimationUtils.loadAnimation(getActivity(), windowAnimationStyle.activityExitAnimation);
        }
    }



    @Override
    public int confirmSelect(LocalMedia currentMedia, boolean isSelected) {
        String curMimeType = currentMedia.getMimeType();
        long curDuration = currentMedia.getDuration();
        List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
        if (config.selectionMode == SelectModeConfig.MULTIPLE) {
            if (config.isWithVideoImage) {
                // 共选型模式
                int selectVideoSize = 0;
                for (int i = 0; i < selectedResult.size(); i++) {
                    String mimeType = selectedResult.get(i).getMimeType();
                    if (PictureMimeType.isHasVideo(mimeType)) {
                        selectVideoSize++;
                    }
                }
                if (checkWithMimeTypeValidity(isSelected, curMimeType, selectVideoSize, curDuration)) {
                    return SelectedManager.INVALID;
                }
            } else {
                // 单一型模式
                if (checkOnlyMimeTypeValidity(isSelected, curMimeType, SelectedManager.getTopResultMimeType(), curDuration)) {
                    return SelectedManager.INVALID;
                }
            }
        }
        int resultCode;
        if (isSelected) {
            selectedResult.remove(currentMedia);
            resultCode = SelectedManager.REMOVE;
        } else {
            if (config.selectionMode == SelectModeConfig.SINGLE) {
                if (selectedResult.size() > 0) {
                    sendFixedSelectedChangeEvent(selectedResult.get(0));
                    selectedResult.clear();
                }
            }
            selectedResult.add(currentMedia);
            currentMedia.setNum(selectedResult.size());
            resultCode = SelectedManager.ADD_SUCCESS;
            playClickEffect();
        }
        sendSelectedChangeEvent(resultCode == SelectedManager.ADD_SUCCESS, currentMedia);
        return resultCode;
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    @Override
    public boolean checkWithMimeTypeValidity(boolean isSelected, String curMimeType, int selectVideoSize, long duration) {
        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (config.maxVideoSelectNum <= 0) {
                // 如果视频可选数量是0
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_rule));
                return true;
            }

            if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_message_max_num, config.maxSelectNum));
                return true;
            }

            if (!isSelected && selectVideoSize >= config.maxVideoSelectNum) {
                // 如果选择的是视频
                RemindDialog.showTipsDialog(getContext(), getTipsMsg(getContext(), curMimeType, config.maxVideoSelectNum));
                return true;
            }

            if (!isSelected && config.videoMinSecond > 0 && duration < config.videoMinSecond) {
                // 视频小于最低指定的长度
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_choose_min_seconds, config.videoMinSecond / 1000));
                return true;
            }

            if (!isSelected && config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
                // 视频时长超过了指定的长度
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_choose_max_seconds, config.videoMaxSecond / 1000));
                return true;
            }
        } else {
            if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_message_max_num, config.maxSelectNum));
                return true;
            }
        }
        return false;
    }


    @SuppressLint("StringFormatInvalid")
    @Override
    public boolean checkOnlyMimeTypeValidity(boolean isSelected, String curMimeType, String existMimeType, long duration) {
        boolean isSameMimeType = PictureMimeType.isMimeTypeSame(existMimeType, curMimeType);
        if (!isSameMimeType) {
            RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_rule));
            return true;
        }
        if (PictureMimeType.isHasVideo(existMimeType) && config.maxVideoSelectNum > 0) {
            if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxVideoSelectNum) {
                // 如果先选择的是视频
                RemindDialog.showTipsDialog(getContext(), getTipsMsg(getContext(), existMimeType, config.maxVideoSelectNum));
                return true;
            }
            if (!isSelected && config.videoMinSecond > 0 && duration < config.videoMinSecond) {
                // 视频小于最低指定的长度
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_choose_min_seconds, config.videoMinSecond / 1000));
                return true;
            }

            if (!isSelected && config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
                // 视频时长超过了指定的长度
                RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_choose_max_seconds, config.videoMaxSecond / 1000));
                return true;
            }
        } else {
            if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                RemindDialog.showTipsDialog(getContext(), getTipsMsg(getContext(), existMimeType, config.maxSelectNum));
                return true;
            }
            if (PictureMimeType.isHasVideo(curMimeType)) {
                if (!isSelected && config.videoMinSecond > 0 && duration < config.videoMinSecond) {
                    // 视频小于最低指定的长度
                    RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_choose_min_seconds, config.videoMinSecond / 1000));
                    return true;
                }
                if (!isSelected && config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
                    // 视频时长超过了指定的长度
                    RemindDialog.showTipsDialog(getContext(), getString(R.string.ps_choose_max_seconds, config.videoMaxSecond / 1000));
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 根据类型获取相应的Toast文案
     *
     * @param context
     * @param mimeType
     * @param maxSelectNum
     * @return
     */
    @SuppressLint("StringFormatInvalid")
    private static String getTipsMsg(Context context, String mimeType, int maxSelectNum) {
        if (PictureMimeType.isHasVideo(mimeType)) {
            return context.getString(R.string.ps_message_video_max_num, String.valueOf(maxSelectNum));
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            return context.getString(R.string.ps_message_audio_max_num, String.valueOf(maxSelectNum));
        } else {
            return context.getString(R.string.ps_message_max_num, String.valueOf(maxSelectNum));
        }
    }

    @Override
    public void sendSelectedChangeEvent(boolean isAddRemove, LocalMedia currentMedia) {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof PictureCommonFragment) {
                    ((PictureCommonFragment) fragment).onSelectedChange(isAddRemove, currentMedia);
                }
            }
        }
    }

    @Override
    public void sendFixedSelectedChangeEvent(LocalMedia currentMedia) {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof PictureCommonFragment) {
                    ((PictureCommonFragment) fragment).onFixedSelectedChange(currentMedia);
                }
            }
        }
    }

    @Override
    public void sendSelectedOriginalChangeEvent() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof PictureCommonFragment) {
                    ((PictureCommonFragment) fragment).onCheckOriginalChange();
                }
            }
        }
    }

    @Override
    public void openSelectedCamera() {
        switch (config.chooseMode) {
            case SelectMimeType.TYPE_ALL:
                if (config.ofAllCameraType == SelectMimeType.ofImage()) {
                    openImageCamera();
                } else if (config.ofAllCameraType == SelectMimeType.ofVideo()) {
                    openVideoCamera();
                } else {
                    onSelectedOnlyCamera();
                }
                break;
            case SelectMimeType.TYPE_IMAGE:
                openImageCamera();
                break;
            case SelectMimeType.TYPE_VIDEO:
                openVideoCamera();
                break;
            case SelectMimeType.TYPE_AUDIO:
                openSoundRecording();
                break;
            default:
                break;
        }
    }


    @Override
    public void onSelectedOnlyCamera() {
        PhotoItemSelectedDialog selectedDialog = PhotoItemSelectedDialog.newInstance();
        selectedDialog.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case PhotoItemSelectedDialog.IMAGE_CAMERA:
                        if (PictureSelectionConfig.cameraInterceptListener != null) {
                            interceptCameraEvent(SelectMimeType.TYPE_IMAGE);
                        } else {
                            openImageCamera();
                        }
                        break;
                    case PhotoItemSelectedDialog.VIDEO_CAMERA:
                        if (PictureSelectionConfig.cameraInterceptListener != null) {
                            interceptCameraEvent(SelectMimeType.TYPE_VIDEO);
                        } else {
                            openVideoCamera();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        selectedDialog.show(getChildFragmentManager(), "PhotoItemSelectedDialog");
    }

    @Override
    public void openImageCamera() {
        if (PictureSelectionConfig.permissionsEventListener != null) {
            PictureSelectionConfig.permissionsEventListener.requestPermission(this, PermissionConfig.CAMERA,
                    new OnCallbackListener<Boolean>() {
                        @Override
                        public void onCall(Boolean isResult) {
                            if (isResult) {
                                startCameraImageCapture();
                            } else {
                                handlePermissionDenied(PermissionConfig.CAMERA);
                            }
                        }
                    });
        } else {
            PermissionChecker.getInstance().requestPermissions(this, PermissionConfig.CAMERA,
                    new PermissionResultCallback() {
                        @Override
                        public void onGranted() {
                            startCameraImageCapture();
                        }

                        @Override
                        public void onDenied() {
                            handlePermissionDenied(PermissionConfig.CAMERA);
                        }
                    });
        }
    }

    /**
     * Start ACTION_IMAGE_CAPTURE
     */
    private void startCameraImageCapture() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            ForegroundService.startForegroundService(getContext());
            if (PictureSelectionConfig.cameraInterceptListener != null) {
                interceptCameraEvent(SelectMimeType.TYPE_IMAGE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    Uri imageUri = MediaStoreUtils.createCameraOutImageUri(getContext(), config);
                    if (imageUri != null) {
                        if (config.isCameraAroundState) {
                            cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
                        }
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                }
            }
        }
    }


    @Override
    public void openVideoCamera() {
        if (PictureSelectionConfig.permissionsEventListener != null) {
            PictureSelectionConfig.permissionsEventListener.requestPermission(this, PermissionConfig.CAMERA,
                    new OnCallbackListener<Boolean>() {
                        @Override
                        public void onCall(Boolean isResult) {
                            if (isResult) {
                                startCameraVideoCapture();
                            } else {
                                handlePermissionDenied(PermissionConfig.CAMERA);
                            }
                        }
                    });
        } else {
            PermissionChecker.getInstance().requestPermissions(this, PermissionConfig.CAMERA,
                    new PermissionResultCallback() {
                        @Override
                        public void onGranted() {
                            startCameraVideoCapture();
                        }

                        @Override
                        public void onDenied() {
                            handlePermissionDenied(PermissionConfig.CAMERA);
                        }
                    });
        }
    }

    /**
     * Start ACTION_VIDEO_CAPTURE
     */
    private void startCameraVideoCapture() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            ForegroundService.startForegroundService(getContext());
            if (PictureSelectionConfig.cameraInterceptListener != null) {
                interceptCameraEvent(SelectMimeType.TYPE_VIDEO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    Uri videoUri = MediaStoreUtils.createCameraOutVideoUri(getContext(), config);
                    if (videoUri != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        if (config.isCameraAroundState) {
                            cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
                        }
                        cameraIntent.putExtra(PictureConfig.EXTRA_QUICK_CAPTURE, config.isQuickCapture);
                        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.recordVideoMaxSecond);
                        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoQuality);
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                }
            }
        }
    }


    @Override
    public void openSoundRecording() {
        if (PictureSelectionConfig.permissionsEventListener != null) {
            PictureSelectionConfig.permissionsEventListener.requestPermission(this, PermissionConfig.RECORD_AUDIO,
                    new OnCallbackListener<Boolean>() {
                        @Override
                        public void onCall(Boolean isResult) {
                            if (isResult) {
                                startCameraRecordSound();
                            } else {
                                handlePermissionDenied(PermissionConfig.RECORD_AUDIO);
                            }
                        }
                    });
        } else {
            PermissionChecker.getInstance().requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionResultCallback() {
                        @Override
                        public void onGranted() {
                            startCameraRecordSound();
                        }

                        @Override
                        public void onDenied() {
                            handlePermissionDenied(PermissionConfig.RECORD_AUDIO);
                        }
                    });
        }
    }

    /**
     * Start RECORD_SOUND_ACTION
     */
    private void startCameraRecordSound() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            ForegroundService.startForegroundService(getContext());
            if (PictureSelectionConfig.cameraInterceptListener != null) {
                interceptCameraEvent(SelectMimeType.TYPE_AUDIO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                }
            }
        }
    }


    /**
     * 拦截相机事件并处理返回结果
     */
    private void interceptCameraEvent(int cameraMode) {
        ForegroundService.startForegroundService(getContext());
        PictureSelectionConfig.cameraInterceptListener.openCamera(this, cameraMode, PictureConfig.REQUEST_CAMERA);
    }

    /**
     * 点击选择的音效
     */
    private void playClickEffect() {
        if (soundPool != null && config.isOpenClickSound) {
            soundPool.play(soundID, 0.1F, 0.5F, 0, 1, 1);
        }
    }

    /**
     * 释放音效资源
     */
    private void releaseSoundPool() {
        try {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ForegroundService.stopService(getContext());
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                dispatchHandleCamera(data);
            } else if (requestCode == Crop.REQUEST_EDIT_CROP) {
                onEditMedia(data);
            } else if (requestCode == Crop.REQUEST_CROP) {
                List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
                try {
                    if (selectedResult.size() == 1) {
                        LocalMedia media = selectedResult.get(0);
                        Uri output = Crop.getOutput(data);
                        media.setCutPath(output != null ? output.getPath() : "");
                        media.setCut(!TextUtils.isEmpty(media.getCutPath()));
                        media.setCropImageWidth(Crop.getOutputImageWidth(data));
                        media.setCropImageHeight(Crop.getOutputImageHeight(data));
                        media.setCropOffsetX(Crop.getOutputImageOffsetX(data));
                        media.setCropOffsetY(Crop.getOutputImageOffsetY(data));
                        media.setCropResultAspectRatio(Crop.getOutputCropAspectRatio(data));
                        media.setCustomData(Crop.getOutputCustomExtraData(data));
                        media.setSandboxPath(media.getCutPath());
                    } else {
                        String extra = data.getStringExtra(MediaStore.EXTRA_OUTPUT);
                        JSONArray array = new JSONArray(extra);
                        if (array.length() == selectedResult.size()) {
                            for (int i = 0; i < selectedResult.size(); i++) {
                                LocalMedia media = selectedResult.get(i);
                                JSONObject item = array.optJSONObject(i);
                                media.setCutPath(item.optString(CustomField.EXTRA_OUT_PUT_PATH));
                                media.setCut(!TextUtils.isEmpty(media.getCutPath()));
                                media.setCropImageWidth(item.optInt(CustomField.EXTRA_IMAGE_WIDTH));
                                media.setCropImageHeight(item.optInt(CustomField.EXTRA_IMAGE_HEIGHT));
                                media.setCropOffsetX(item.optInt(CustomField.EXTRA_OFFSET_X));
                                media.setCropOffsetY(item.optInt(CustomField.EXTRA_OFFSET_Y));
                                media.setCropResultAspectRatio((float) item.optDouble(CustomField.EXTRA_ASPECT_RATIO));
                                media.setCustomData(item.optString(CustomField.EXTRA_CUSTOM_EXTRA_DATA));
                                media.setSandboxPath(media.getCutPath());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                ArrayList<LocalMedia> result = new ArrayList<>(selectedResult);
                if (checkCompressValidity()) {
                    showLoading();
                    PictureSelectionConfig.compressEngine.onStartCompress(getContext(), result,
                            new OnCallbackListener<ArrayList<LocalMedia>>() {
                                @Override
                                public void onCall(ArrayList<LocalMedia> result) {
                                    onResultEvent(result);
                                }
                            });
                } else {
                    onResultEvent(result);
                }
            }
        } else if (resultCode == Crop.RESULT_CROP_ERROR) {
            Throwable throwable = data != null ? Crop.getError(data) : new Throwable("image crop error");
            if (throwable != null) {
                Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                MediaUtils.deleteUri(getContext(), config.cameraPath);
            } else if (requestCode == PictureConfig.REQUEST_GO_SETTING) {
                handlePermissionSettingResult();
            }
        }
    }

    /**
     * 相机事件回调处理
     */
    private void dispatchHandleCamera(Intent intent) {
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<LocalMedia>() {

            @Override
            public LocalMedia doInBackground() {
                String outputPath = getOutputPath(intent);
                if (!TextUtils.isEmpty(outputPath)){
                    config.cameraPath = outputPath;
                }
                if (TextUtils.isEmpty(config.cameraPath)) {
                    return null;
                }
                return buildLocalMedia();
            }

            @Override
            public void onSuccess(LocalMedia result) {
                PictureThreadUtils.cancel(this);
                if (result != null) {
                    dispatchCameraMediaResult(result);
                    onScannerScanFile(result);
                }
            }
        });
    }

    /**
     * 尝试匹配查找自定义相机返回的路径
     *
     * @param data
     * @return
     */
    protected String getOutputPath(Intent data) {
        String outputPath = null;
        if (data != null) {
            Uri outPutUri = config.chooseMode ==
                    SelectMimeType.ofAudio() ? data.getData()
                    : data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            if (outPutUri != null) {
                outputPath = PictureMimeType.isContent(outPutUri.toString())
                        ? outPutUri.toString() : outPutUri.getPath();
            }
        }
        return outputPath;
    }

    /**
     * 刷新相册
     *
     * @param localMedia 要刷新的对象
     */
    private void onScannerScanFile(LocalMedia localMedia) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        if (SdkVersionUtils.isQ()) {
            if (PictureMimeType.isHasVideo(localMedia.getMimeType()) && PictureMimeType.isContent(config.cameraPath)) {
                new PictureMediaScannerConnection(getActivity(), localMedia.getRealPath());
            }
        } else {
            new PictureMediaScannerConnection(getActivity(), config.cameraPath);
            if (PictureMimeType.isHasImage(localMedia.getMimeType())) {
                int lastImageId = MediaUtils.getDCIMLastImageId(getContext());
                if (lastImageId != -1) {
                    MediaUtils.removeMedia(getContext(), lastImageId);
                }
            }
        }
    }

    /**
     * buildLocalMedia
     */
    private LocalMedia buildLocalMedia() {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return null;
        }
        long id, bucketId;
        File cameraFile;
        Uri mimeTypeUri;
        if (PictureMimeType.isContent(config.cameraPath)) {
            Uri cameraUri = Uri.parse(config.cameraPath);
            mimeTypeUri = cameraUri;
            String path = PictureFileUtils.getPath(getActivity(), cameraUri);
            cameraFile = new File(path);
            int lastIndexOf = config.cameraPath.lastIndexOf("/") + 1;
            id = lastIndexOf > 0 ? ValueOf.toLong(config.cameraPath.substring(lastIndexOf)) : System.currentTimeMillis();
            bucketId = MediaUtils.generateCameraBucketId(getContext(), cameraFile, "");
        } else {
            cameraFile = new File(config.cameraPath);
            mimeTypeUri = Uri.fromFile(cameraFile);
            id = System.currentTimeMillis();
            bucketId = MediaUtils.generateCameraBucketId(getContext(), cameraFile, config.outPutCameraDir);
        }
        String mimeType = MediaUtils.getMimeTypeFromMediaContentUri(getActivity(), mimeTypeUri);
        if (config.isCameraRotateImage && PictureMimeType.isHasImage(mimeType) && !PictureMimeType.isContent(config.cameraPath)) {
            BitmapUtils.rotateImage(getContext(), config.cameraPath);
        }
        MediaExtraInfo mediaExtraInfo;
        if (PictureMimeType.isHasVideo(mimeType)) {
            mediaExtraInfo = MediaUtils.getVideoSize(getContext(), config.cameraPath);
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            mediaExtraInfo = MediaUtils.getAudioSize(getContext(), config.cameraPath);
        } else {
            mediaExtraInfo = MediaUtils.getImageSize(getContext(), config.cameraPath);
        }
        String folderName = MediaUtils.generateCameraFolderName(config.cameraPath, mimeType, config.outPutCameraDir);
        LocalMedia media = LocalMedia.parseLocalMedia(id, config.cameraPath, cameraFile.getAbsolutePath(),
                cameraFile.getName(), folderName, mediaExtraInfo.getDuration(), config.chooseMode,
                mimeType, mediaExtraInfo.getWidth(), mediaExtraInfo.getHeight(), cameraFile.length(), bucketId,
                cameraFile.lastModified() / 1000);
        if (SdkVersionUtils.isQ()) {
            media.setSandboxPath(PictureMimeType.isContent(config.cameraPath) ? null : config.cameraPath);
        }
        return media;
    }


    /**
     * 分发处理结果，比如压缩、裁剪、沙盒路径转换
     */
    protected void dispatchTransformResult() {
        ArrayList<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
        ArrayList<LocalMedia> result = new ArrayList<>(selectedResult);
        if (checkCropValidity()) {
            LocalMedia currentLocalMedia = null;
            for (int i = 0; i < result.size(); i++) {
                LocalMedia item = result.get(i);
                if (PictureMimeType.isHasImage(result.get(i).getMimeType())) {
                    currentLocalMedia = item;
                    break;
                }
            }
            PictureSelectionConfig.cropEngine.onStartCrop(this, currentLocalMedia, result, Crop.REQUEST_CROP);
        } else if (checkCompressValidity()) {
            showLoading();
            PictureSelectionConfig.compressEngine.onStartCompress(getContext(), result,
                    new OnCallbackListener<ArrayList<LocalMedia>>() {
                        @Override
                        public void onCall(ArrayList<LocalMedia> result) {
                            onResultEvent(result);
                        }
                    });

        } else {
            onResultEvent(result);
        }
    }

    /**
     * 验证裁剪的可行性
     *
     * @return
     */
    private boolean checkCropValidity() {
        if (PictureSelectionConfig.cropEngine != null) {
            if (SelectedManager.getCount() == 1) {
                return PictureMimeType.isHasImage(SelectedManager.getTopResultMimeType());
            } else {
                for (int i = 0; i < SelectedManager.getCount(); i++) {
                    LocalMedia media = SelectedManager.getSelectedResult().get(i);
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 验证压缩的可行性
     *
     * @return
     */
    private boolean checkCompressValidity() {
        if (PictureSelectionConfig.compressEngine != null) {
            for (int i = 0; i < SelectedManager.getCount(); i++) {
                LocalMedia media = SelectedManager.getSelectedResult().get(i);
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回处理完成后的选择结果
     */
    @Override
    public void onResultEvent(ArrayList<LocalMedia> result) {
        if (PictureSelectionConfig.sandboxFileEngine != null) {
            showLoading();
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<ArrayList<LocalMedia>>() {
                @Override
                public ArrayList<LocalMedia> doInBackground() {
                    for (int i = 0; i < result.size(); i++) {
                        LocalMedia media = result.get(i);
                        PictureSelectionConfig.sandboxFileEngine.onStartSandboxFileTransform(getContext(), i,
                                media, new OnCallbackIndexListener<LocalMedia>() {
                                    @Override
                                    public void onCall(LocalMedia data, int index) {
                                        LocalMedia media = result.get(index);
                                        media.setSandboxPath(data.getSandboxPath());
                                        if (config.isCheckOriginalImage) {
                                            media.setOriginalPath(data.getSandboxPath());
                                            media.setOriginal(!TextUtils.isEmpty(data.getSandboxPath()));
                                        }
                                    }
                                });
                    }
                    return result;
                }

                @Override
                public void onSuccess(ArrayList<LocalMedia> result) {
                    PictureThreadUtils.cancel(this);
                    callBackResult(result);
                }
            });
        } else {
            callBackResult(result);
        }
    }

    /**
     * 返回结果
     */
    private void callBackResult(ArrayList<LocalMedia> result) {
        dismissLoading();
        if (PictureSelectionConfig.resultCallListener != null) {
            PictureSelectionConfig.resultCallListener.onResult(result);
        }
        SelectorResult selectorResult = getResult(RESULT_OK, result);
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            getActivity().setResult(selectorResult.mResultCode, selectorResult.mResultData);
        }
        if (config.isOnlyCamera) {
            if (!ActivityCompatHelper.isDestroy(getActivity())) {
                getActivity().getSupportFragmentManager().popBackStack();
                if (config.isActivityResultBack && iBridgePictureBehavior == null) {
                    throw new IllegalArgumentException(getActivity().toString()
                            + " please must implement IBridgePictureBehavior onSelectFinish");
                }
                if (iBridgePictureBehavior != null) {
                    iBridgePictureBehavior.onSelectFinish(true, selectorResult);
                }
            }
        } else {
            boolean isForcedExit = this instanceof PictureSelectorPreviewFragment;
            iBridgePictureBehavior.onSelectFinish(isForcedExit, selectorResult);
        }
        PictureSelectionConfig.destroy();
    }

    /**
     * set app language
     */
    @Override
    public void initAppLanguage() {
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        if (config.language != LanguageConfig.UNKNOWN_LANGUAGE && !config.isOnlyCamera) {
            PictureLanguageUtils.setAppLanguage(getActivity(), config.language);
        }
    }

    @Override
    public void onRecreateEngine() {
        createImageLoaderEngine();
        createCompressEngine();
        createSandboxFileEngine();
        createLoaderDataEngine();
        createResultCallbackListener();
        createLayoutResourceListener();
    }

    @Override
    public void onDestroy() {
        releaseSoundPool();
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        try {
            if (ActivityCompatHelper.isDestroy(getActivity())) {
                return;
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void dismissLoading() {
        try {
            if (ActivityCompatHelper.isDestroy(getActivity())) {
                return;
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        initAppLanguage();
        onRecreateEngine();
        super.onAttach(context);

        if (getParentFragment() instanceof IBridgePictureBehavior) {
            iBridgePictureBehavior = (IBridgePictureBehavior) getParentFragment();
        } else if (context instanceof IBridgePictureBehavior) {
            iBridgePictureBehavior = (IBridgePictureBehavior) context;
        } else {
            if (this instanceof PictureOnlyCameraFragment || this instanceof PictureSelectorPreviewFragment) {
                /**
                 * {@link PictureSelector.openCamera or startPreview}
                 * <p>
                 *     不需要使用到IBridgePictureBehavior，可以忽略
                 * </p>
                 */
            } else {
                throw new IllegalArgumentException(context.toString()
                        + " please must implement IBridgePictureBehavior");
            }
        }
    }

    /**
     * Get the image loading engine again, provided that the user implements the IApp interface in the Application
     */
    private void createImageLoaderEngine() {
        if (PictureSelectionConfig.imageEngine == null) {
            PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
            if (baseEngine != null)
                PictureSelectionConfig.imageEngine = baseEngine.createImageLoaderEngine();
        }
    }


    /**
     * Get the image loader data engine again, provided that the user implements the IApp interface in the Application
     */
    private void createLoaderDataEngine() {
        if (PictureSelectionConfig.getInstance().isLoaderDataEngine) {
            if (PictureSelectionConfig.loaderDataEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    PictureSelectionConfig.loaderDataEngine = baseEngine.createLoaderDataEngine();
            }
        }
    }

    /**
     * Get the image compress engine again, provided that the user implements the IApp interface in the Application
     */
    private void createCompressEngine() {
        if (PictureSelectionConfig.getInstance().isCompressEngine) {
            if (PictureSelectionConfig.compressEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    PictureSelectionConfig.compressEngine = baseEngine.createCompressEngine();
            }
        }
    }


    /**
     * Get the Sandbox engine again, provided that the user implements the IApp interface in the Application
     */
    private void createSandboxFileEngine() {
        if (PictureSelectionConfig.getInstance().isSandboxFileEngine) {
            if (PictureSelectionConfig.sandboxFileEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    PictureSelectionConfig.sandboxFileEngine = baseEngine.createSandboxFileEngine();
            }
        }
    }


    /**
     * Retrieve the result callback listener, provided that the user implements the IApp interface in the Application
     */
    private void createResultCallbackListener() {
        if (PictureSelectionConfig.getInstance().isResultBack) {
            if (PictureSelectionConfig.resultCallListener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    PictureSelectionConfig.resultCallListener = baseEngine.getResultCallbackListener();
                }
            }
        }
    }

    /**
     * Retrieve the layout callback listener, provided that the user implements the IApp interface in the Application
     */
    private void createLayoutResourceListener() {
        if (PictureSelectionConfig.getInstance().isInjectLayoutResource) {
            if (PictureSelectionConfig.layoutResourceListener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    PictureSelectionConfig.layoutResourceListener = baseEngine.createLayoutResourceListener();
                }
            }
        }
    }

    /**
     * generate result
     *
     * @param data result
     * @return
     */
    protected SelectorResult getResult(int resultCode, ArrayList<LocalMedia> data) {
        return new SelectorResult(resultCode, PictureSelector.putIntentResult(data));
    }

    /**
     * SelectorResult
     */
    public static class SelectorResult {

        public int mResultCode;
        public Intent mResultData;

        public SelectorResult(int resultCode, Intent data) {
            mResultCode = resultCode;
            mResultData = data;
        }
    }
}
