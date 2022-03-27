package com.luck.picture.lib.basic;

import static android.app.Activity.RESULT_OK;
import static android.view.KeyEvent.ACTION_UP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.R;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.config.Crop;
import com.luck.picture.lib.config.CustomIntentKey;
import com.luck.picture.lib.config.PermissionEvent;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectLimitType;
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
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnRequestPermissionListener;
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
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.MediaStoreUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.SpUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.luck.picture.lib.utils.ValueOf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：PictureCommonFragment
 */
public abstract class PictureCommonFragment extends Fragment implements IPictureSelectorCommonEvent {
    public static final String TAG = PictureCommonFragment.class.getSimpleName();
    /**
     * PermissionResultCallback
     */
    private PermissionResultCallback mPermissionResultCallback;

    /**
     * IBridgePictureBehavior
     */
    protected IBridgePictureBehavior iBridgePictureBehavior;

    /**
     * page
     */
    protected int mPage = 1;

    /**
     * Media Loader engine
     */
    protected IBridgeMediaLoader mLoader;

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

    /**
     * fragment enter anim duration
     */
    private long enterAnimDuration;

    /**
     * tipsDialog
     */
    protected Dialog tipsDialog;

    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return 0;
    }


    @Override
    public void onFragmentResume() {

    }

    @Override
    public void reStartSavedInstance(Bundle savedInstanceState) {

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
    public void handlePermissionSettingResult(String[] permissions) {

    }

    @Override
    public void onEditMedia(Intent intent) {

    }

    @Override
    public void onEnterFragment() {

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
        PermissionConfig.CURRENT_REQUEST_PERMISSION = permissionArray;
        if (permissionArray != null && permissionArray.length > 0) {
            SpUtils.putBoolean(getContext(),permissionArray[0], true);
        }
        if (PictureSelectionConfig.onPermissionDeniedListener != null) {
            onPermissionExplainEvent(false, null);
            PictureSelectionConfig.onPermissionDeniedListener
                    .onDenied(this, permissionArray, PictureConfig.REQUEST_GO_SETTING,
                            new OnCallbackListener<Boolean>() {
                                @Override
                                public void onCall(Boolean isResult) {
                                    if (isResult) {
                                        handlePermissionSettingResult(PermissionConfig.CURRENT_REQUEST_PERMISSION);
                                        PermissionConfig.CURRENT_REQUEST_PERMISSION = null;
                                    }
                                }
                            });
        } else {
            boolean isReadWrite = false;
            if (permissionArray != null && permissionArray.length > 0) {
                for (String s : permissionArray) {
                    isReadWrite = TextUtils.equals(s, Manifest.permission.READ_EXTERNAL_STORAGE)
                            || TextUtils.equals(s, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
            PermissionUtil.goIntentSetting(this, isReadWrite, PictureConfig.REQUEST_GO_SETTING);
        }
    }

    /**
     * 使用PictureSelector 默认方式进入
     *
     * @return
     */
    protected boolean isNormalDefaultEnter() {
        return getActivity() instanceof PictureSelectorSupporterActivity || getActivity() instanceof PictureSelectorTransparentActivity;
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
        setTranslucentStatusBar();
        setRootViewKeyListener(requireView());
        if (config.isOpenClickSound && !config.isOnlyCamera) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundID = soundPool.load(getContext(), R.raw.ps_click_music, 1);
        }
    }


    /**
     * 设置透明状态栏
     */
    private void setTranslucentStatusBar() {
        if (config.isPreviewFullScreenMode) {
            SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
            ImmersiveManager.translucentStatusBar(getActivity(), selectMainStyle.isDarkStatusBarBlack());
        }
    }

    /**
     * 设置回退监听
     *
     * @param view
     */
    public void setRootViewKeyListener(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == ACTION_UP) {
                    onKeyBackFragmentFinish();
                    return true;
                }
                return false;
            }
        });
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
        Animation loadAnimation;
        if (enter) {
            if (windowAnimationStyle.activityEnterAnimation != 0) {
                loadAnimation = AnimationUtils.loadAnimation(getContext(), windowAnimationStyle.activityEnterAnimation);
            } else {
                loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_alpha_enter);
            }
            setEnterAnimationDuration(loadAnimation.getDuration());
            onEnterFragment();
        } else {
            if (windowAnimationStyle.activityExitAnimation != 0) {
                loadAnimation = AnimationUtils.loadAnimation(getContext(), windowAnimationStyle.activityExitAnimation);
            } else {
                loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_alpha_exit);
            }
            onExitFragment();
        }
        return loadAnimation;
    }

    public void setEnterAnimationDuration(long duration) {
        this.enterAnimDuration = duration;
    }


    public long getEnterAnimationDuration() {
        final long DIFFERENCE = 50;
        long duration = enterAnimDuration > DIFFERENCE ? enterAnimDuration - DIFFERENCE : enterAnimDuration;
        return duration >= 0 ? duration : 0;
    }


    @Override
    public int confirmSelect(LocalMedia currentMedia, boolean isSelected) {
        if (PictureSelectionConfig.onSelectFilterListener != null) {
            if (PictureSelectionConfig.onSelectFilterListener.onSelectFilter(currentMedia)) {
                boolean isSelectLimit = false;
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT);
                }
                if (isSelectLimit) {
                } else {
                    ToastUtils.showToast(getContext(), getString(R.string.ps_select_no_support));
                }
                return SelectedManager.INVALID;
            }
        }
        int checkSelectValidity = isCheckSelectValidity(currentMedia, isSelected);
        if (checkSelectValidity != SelectedManager.SUCCESS) {
            return SelectedManager.INVALID;
        }
        List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
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

    /**
     * 验证选择的合法性
     *
     * @param currentMedia 当前选中资源
     * @param isSelected   选中或是取消
     * @return
     */
    protected int isCheckSelectValidity(LocalMedia currentMedia, boolean isSelected) {
        String curMimeType = currentMedia.getMimeType();
        long curDuration = currentMedia.getDuration();
        long curFileSize = currentMedia.getSize();
        List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
        if (config.isWithVideoImage) {
            // 共选型模式
            int selectVideoSize = 0;
            for (int i = 0; i < selectedResult.size(); i++) {
                String mimeType = selectedResult.get(i).getMimeType();
                if (PictureMimeType.isHasVideo(mimeType)) {
                    selectVideoSize++;
                }
            }
            if (checkWithMimeTypeValidity(isSelected, curMimeType, selectVideoSize, curFileSize, curDuration)) {
                return SelectedManager.INVALID;
            }
        } else {
            // 单一型模式
            if (checkOnlyMimeTypeValidity(isSelected, curMimeType, SelectedManager.getTopResultMimeType(), curFileSize, curDuration)) {
                return SelectedManager.INVALID;
            }
        }
        return SelectedManager.SUCCESS;
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    @Override
    public boolean checkWithMimeTypeValidity(boolean isSelected, String curMimeType, int selectVideoSize, long fileSize, long duration) {
        if (config.selectMaxFileSize > 0) {
            if (fileSize > config.selectMaxFileSize) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config,
                                    SelectLimitType.SELECT_MAX_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String maxFileSize = PictureFileUtils.formatFileSize(config.selectMaxFileSize, 1);
                showTipsDialog(getString(R.string.ps_select_max_size, maxFileSize));
                return true;
            }
        }
        if (config.selectMinFileSize > 0) {
            if (fileSize < config.selectMinFileSize) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config,
                                    SelectLimitType.SELECT_MIN_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String minFileSize = PictureFileUtils.formatFileSize(config.selectMinFileSize, 1);
                showTipsDialog(getString(R.string.ps_select_min_size, minFileSize));
                return true;
            }
        }

        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (config.selectionMode == SelectModeConfig.MULTIPLE) {
                if (config.maxVideoSelectNum <= 0) {
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_NOT_WITH_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    // 如果视频可选数量是0
                    showTipsDialog(getString(R.string.ps_rule));
                    return true;
                }

                if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getString(R.string.ps_message_max_num, config.maxSelectNum));
                    return true;
                }

                if (!isSelected && selectVideoSize >= config.maxVideoSelectNum) {
                    // 如果选择的是视频
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getContext(), curMimeType, config.maxVideoSelectNum));
                    return true;
                }
            }

            if (!isSelected && config.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(duration) < config.selectMinDurationSecond) {
                // 视频小于最低指定的长度
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config,
                                    SelectLimitType.SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_min_second, config.selectMinDurationSecond / 1000));
                return true;
            }

            if (!isSelected && config.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(duration) > config.selectMaxDurationSecond) {
                // 视频时长超过了指定的长度
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config,
                                    SelectLimitType.SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_max_second, config.selectMaxDurationSecond / 1000));
                return true;
            }
        } else {
            if (config.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config,
                                        SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getString(R.string.ps_message_max_num, config.maxSelectNum));
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public boolean checkOnlyMimeTypeValidity(boolean isSelected, String curMimeType, String existMimeType, long fileSize, long duration) {
        if (PictureMimeType.isMimeTypeSame(existMimeType, curMimeType)) {
            // ignore
        } else {
            if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                        .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_NOT_WITH_SELECT_LIMIT);
                if (isSelectLimit) {
                    return true;
                }
            }
            showTipsDialog(getString(R.string.ps_rule));
            return true;
        }
        if (config.selectMaxFileSize > 0) {
            if (fileSize > config.selectMaxFileSize) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config,
                                    SelectLimitType.SELECT_MAX_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String maxFileSize = PictureFileUtils.formatFileSize(config.selectMaxFileSize, 1);
                showTipsDialog(getString(R.string.ps_select_max_size, maxFileSize));
                return true;
            }
        }
        if (config.selectMinFileSize > 0) {
            if (fileSize < config.selectMinFileSize) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config,
                                    SelectLimitType.SELECT_MIN_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String minFileSize = PictureFileUtils.formatFileSize(config.selectMinFileSize, 1);
                showTipsDialog(getString(R.string.ps_select_min_size, minFileSize));
                return true;
            }
        }
        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (config.selectionMode == SelectModeConfig.MULTIPLE) {
                config.maxVideoSelectNum = config.maxVideoSelectNum > 0 ? config.maxVideoSelectNum : config.maxSelectNum;
                if (!isSelected && SelectedManager.getSelectCount() >= config.maxVideoSelectNum) {
                    // 如果先选择的是视频
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getContext(), curMimeType, config.maxVideoSelectNum));
                    return true;
                }
            }
            if (!isSelected && config.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(duration) < config.selectMinDurationSecond) {
                // 视频小于最低指定的长度
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_min_second, config.selectMinDurationSecond / 1000));
                return true;
            }

            if (!isSelected && config.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(duration) > config.selectMaxDurationSecond) {
                // 视频时长超过了指定的长度
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_max_second, config.selectMaxDurationSecond / 1000));
                return true;
            }
        } else if (PictureMimeType.isHasAudio(curMimeType)) {
            if (config.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getContext(), curMimeType, config.maxSelectNum));
                    return true;
                }
            }

            if (!isSelected && config.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(duration) < config.selectMinDurationSecond) {
                // 音频小于最低指定的长度
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_AUDIO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_audio_min_second, config.selectMinDurationSecond / 1000));
                return true;
            }
            if (!isSelected && config.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(duration) > config.selectMaxDurationSecond) {
                // 音频时长超过了指定的长度
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_AUDIO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_audio_max_second, config.selectMaxDurationSecond / 1000));
                return true;
            }
        } else {
            if (config.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && SelectedManager.getSelectedResult().size() >= config.maxSelectNum) {
                    if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getContext(), curMimeType, config.maxSelectNum));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 提示Dialog
     *
     * @param tips
     */
    private void showTipsDialog(String tips) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        try {
            if (tipsDialog != null && tipsDialog.isShowing()) {
                return;
            }
            tipsDialog = RemindDialog.buildDialog(getContext(), tips);
            tipsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        if (PictureSelectionConfig.onCameraInterceptListener != null) {
                            onInterceptCameraEvent(SelectMimeType.TYPE_IMAGE);
                        } else {
                            openImageCamera();
                        }
                        break;
                    case PhotoItemSelectedDialog.VIDEO_CAMERA:
                        if (PictureSelectionConfig.onCameraInterceptListener != null) {
                            onInterceptCameraEvent(SelectMimeType.TYPE_VIDEO);
                        } else {
                            openVideoCamera();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        selectedDialog.setOnDismissListener(new PhotoItemSelectedDialog.OnDismissListener() {
            @Override
            public void onDismiss(boolean isCancel, DialogInterface dialog) {
                if (config.isOnlyCamera) {
                    if (isCancel) {
                        onKeyBackFragmentFinish();
                    }
                }
            }
        });
        selectedDialog.show(getChildFragmentManager(), "PhotoItemSelectedDialog");
    }

    @Override
    public void openImageCamera() {
        onPermissionExplainEvent(true, PermissionConfig.CAMERA);
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            onApplyPermissionsEvent(PermissionEvent.EVENT_IMAGE_CAMERA, PermissionConfig.CAMERA);
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
    protected void startCameraImageCapture() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            onPermissionExplainEvent(false, null);
            if (PictureSelectionConfig.onCameraInterceptListener != null) {
                onInterceptCameraEvent(SelectMimeType.TYPE_IMAGE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    ForegroundService.startForegroundService(getContext());
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
        onPermissionExplainEvent(true, PermissionConfig.CAMERA);
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            onApplyPermissionsEvent(PermissionEvent.EVENT_VIDEO_CAMERA, PermissionConfig.CAMERA);
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
    protected void startCameraVideoCapture() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            onPermissionExplainEvent(false, null);
            if (PictureSelectionConfig.onCameraInterceptListener != null) {
                onInterceptCameraEvent(SelectMimeType.TYPE_VIDEO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    ForegroundService.startForegroundService(getContext());
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
        if (PictureSelectionConfig.onRecordAudioListener != null) {
            ForegroundService.startForegroundService(getContext());
            PictureSelectionConfig.onRecordAudioListener.onRecordAudio(this, PictureConfig.REQUEST_CAMERA);
        } else {
            throw new NullPointerException(OnRecordAudioInterceptListener.class.getSimpleName() + " interface needs to be implemented for recording");
        }
    }



    /**
     * 拦截相机事件并处理返回结果
     */
    @Override
    public void onInterceptCameraEvent(int cameraMode) {
        ForegroundService.startForegroundService(getContext());
        PictureSelectionConfig.onCameraInterceptListener.openCamera(this, cameraMode, PictureConfig.REQUEST_CAMERA);
    }

    /**
     * 权限申请
     *
     * @param permissionArray
     */
    @Override
    public void onApplyPermissionsEvent(int event, String[] permissionArray) {
        PictureSelectionConfig.onPermissionsEventListener.requestPermission(this, permissionArray,
                new OnRequestPermissionListener() {
                    @Override
                    public void onCall(String[] permissionArray, boolean isResult) {
                        if (isResult) {
                            if (event == PermissionEvent.EVENT_VIDEO_CAMERA) {
                                startCameraVideoCapture();
                            } else {
                                startCameraImageCapture();
                            }
                        } else {
                            handlePermissionDenied(permissionArray);
                        }
                    }
                });
    }

    /**
     * 权限说明
     *
     * @param permissionArray
     */
    @Override
    public void onPermissionExplainEvent(boolean isDisplayExplain, String[] permissionArray) {
        if (PictureSelectionConfig.onPermissionDescriptionListener != null) {
            if (isDisplayExplain) {
                if (PermissionChecker.isCheckSelfPermission(getContext(), permissionArray)) {
                    SpUtils.putBoolean(getContext(),permissionArray[0], false);
                } else {
                    if (!SpUtils.getBoolean(getContext(),permissionArray[0], false)) {
                        PictureSelectionConfig.onPermissionDescriptionListener.onPermissionDescription(this, permissionArray);
                    }
                }
            } else {
                PictureSelectionConfig.onPermissionDescriptionListener.onDismiss(this);
            }
        }
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
                                media.setCutPath(item.optString(CustomIntentKey.EXTRA_OUT_PUT_PATH));
                                media.setCut(!TextUtils.isEmpty(media.getCutPath()));
                                media.setCropImageWidth(item.optInt(CustomIntentKey.EXTRA_IMAGE_WIDTH));
                                media.setCropImageHeight(item.optInt(CustomIntentKey.EXTRA_IMAGE_HEIGHT));
                                media.setCropOffsetX(item.optInt(CustomIntentKey.EXTRA_OFFSET_X));
                                media.setCropOffsetY(item.optInt(CustomIntentKey.EXTRA_OFFSET_Y));
                                media.setCropResultAspectRatio((float) item.optDouble(CustomIntentKey.EXTRA_ASPECT_RATIO));
                                media.setCustomData(item.optString(CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA));
                                media.setSandboxPath(media.getCutPath());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToast(getContext(), e.getMessage());
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
                ToastUtils.showToast(getContext(), throwable.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                MediaUtils.deleteUri(getContext(), config.cameraPath);
            } else if (requestCode == PictureConfig.REQUEST_GO_SETTING) {
                handlePermissionSettingResult(PermissionConfig.CURRENT_REQUEST_PERMISSION);
                PermissionConfig.CURRENT_REQUEST_PERMISSION = null;
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
                if (!TextUtils.isEmpty(outputPath)) {
                    config.cameraPath = outputPath;
                }
                if (TextUtils.isEmpty(config.cameraPath)) {
                    return null;
                }
                if (config.chooseMode == SelectMimeType.ofAudio()) {
                    copyOutputAudioToDir();
                }
                return buildLocalMedia(config.cameraPath);
            }

            @Override
            public void onSuccess(LocalMedia result) {
                PictureThreadUtils.cancel(this);
                if (result != null) {
                    onScannerScanFile(result);
                    dispatchCameraMediaResult(result);
                }
            }
        });
    }

    /**
     * copy录音文件至指定目录
     */
    private void copyOutputAudioToDir() {
        try {
            if (!TextUtils.isEmpty(config.outPutAudioDir) && PictureMimeType.isContent(config.cameraPath)) {
                InputStream inputStream = PictureContentResolver.getContentResolverOpenInputStream(getContext(),
                        Uri.parse(config.cameraPath));
                String audioFileName;
                if (TextUtils.isEmpty(config.outPutAudioFileName)) {
                    audioFileName = "";
                } else {
                    audioFileName = config.isOnlyCamera
                            ? config.outPutAudioFileName : System.currentTimeMillis() + "_" + config.outPutAudioFileName;
                }
                File outputFile = PictureFileUtils.createCameraFile(getContext(),
                        config.chooseMode, audioFileName, "", config.outPutAudioDir);
                FileOutputStream outputStream = new FileOutputStream(outputFile.getAbsolutePath());
                boolean isCopyStatus = PictureFileUtils.writeFileFromIS(inputStream, outputStream);
                if (isCopyStatus) {
                    MediaUtils.deleteUri(getContext(), config.cameraPath);
                    config.cameraPath = outputFile.getAbsolutePath();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 尝试匹配查找自定义相机返回的路径
     *
     * @param data
     * @return
     */
    protected String getOutputPath(Intent data) {
        String outputPath = null;
        Uri outPutUri;
        if (data != null) {
            if (config.chooseMode == SelectMimeType.ofAudio()) {
                outPutUri = data.getData() != null ? data.getData() : data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            } else {
                outPutUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            }
            if (outPutUri != null) {
                outputPath = PictureMimeType.isContent(outPutUri.toString()) ? outPutUri.toString() : outPutUri.getPath();
            }
        }
        return outputPath;
    }

    /**
     * 刷新相册
     *
     * @param media 要刷新的对象
     */
    private void onScannerScanFile(LocalMedia media) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        if (SdkVersionUtils.isQ()) {
            if (PictureMimeType.isHasVideo(media.getMimeType()) && PictureMimeType.isContent(config.cameraPath)) {
                new PictureMediaScannerConnection(getActivity(), media.getRealPath());
            }
        } else {
            String path = PictureMimeType.isContent(config.cameraPath) ? media.getRealPath() : config.cameraPath;
            new PictureMediaScannerConnection(getActivity(), path);
            if (PictureMimeType.isHasImage(media.getMimeType())) {
                File dirFile = new File(path);
                int lastImageId = MediaUtils.getDCIMLastImageId(getContext(), dirFile.getParent());
                if (lastImageId != -1) {
                    MediaUtils.removeMedia(getContext(), lastImageId);
                }
            }
        }
    }

    /**
     * buildLocalMedia
     *
     * @param generatePath
     */
    protected LocalMedia buildLocalMedia(String generatePath) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return null;
        }
        long id = 0, bucketId;
        File cameraFile;
        String mimeType;
        if (PictureMimeType.isContent(generatePath)) {
            Uri cameraUri = Uri.parse(generatePath);
            String path = PictureFileUtils.getPath(getActivity(), cameraUri);
            cameraFile = new File(path);
            mimeType = MediaUtils.getMimeTypeFromMediaUrl(cameraFile.getAbsolutePath());
            if (PictureFileUtils.isMediaDocument(cameraUri)) {
                String documentId = DocumentsContract.getDocumentId(cameraUri);
                if (!TextUtils.isEmpty(documentId)) {
                    String[] split = documentId.split(":");
                    if (split.length > 1) {
                        id = ValueOf.toLong(split[1]);
                    }
                }
            } else if (PictureFileUtils.isDownloadsDocument(cameraUri)) {
                id = ValueOf.toLong(DocumentsContract.getDocumentId(cameraUri));
            } else {
                int lastIndexOf = generatePath.lastIndexOf("/") + 1;
                id = lastIndexOf > 0 ? ValueOf.toLong(generatePath.substring(lastIndexOf)) : System.currentTimeMillis();
            }
            if (PictureMimeType.isHasAudio(mimeType)) {
                bucketId = MediaUtils.generateSoundsBucketId(getContext(), cameraFile, "");
            } else {
                bucketId = MediaUtils.generateCameraBucketId(getContext(), cameraFile, "");
            }
        } else {
            cameraFile = new File(generatePath);
            mimeType = MediaUtils.getMimeTypeFromMediaUrl(cameraFile.getAbsolutePath());
            id = System.currentTimeMillis();
            if (PictureMimeType.isHasAudio(mimeType)) {
                bucketId = MediaUtils.generateSoundsBucketId(getContext(), cameraFile, config.outPutCameraDir);
            } else {
                bucketId = MediaUtils.generateCameraBucketId(getContext(), cameraFile, config.outPutCameraDir);
            }
        }
        if (PictureMimeType.isHasImage(mimeType)) {
            if (config.isCameraRotateImage) {
                BitmapUtils.rotateImage(getContext(), generatePath);
            }
        }
        MediaExtraInfo mediaExtraInfo;
        if (PictureMimeType.isHasVideo(mimeType)) {
            mediaExtraInfo = MediaUtils.getVideoSize(getContext(), generatePath);
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            mediaExtraInfo = MediaUtils.getAudioSize(getContext(), generatePath);
        } else {
            mediaExtraInfo = MediaUtils.getImageSize(getContext(), generatePath);
        }
        String folderName = MediaUtils.generateCameraFolderName(cameraFile.getAbsolutePath());
        LocalMedia media = LocalMedia.parseLocalMedia(id, generatePath, cameraFile.getAbsolutePath(),
                cameraFile.getName(), folderName, mediaExtraInfo.getDuration(), config.chooseMode,
                mimeType, mediaExtraInfo.getWidth(), mediaExtraInfo.getHeight(), cameraFile.length(), bucketId,
                cameraFile.lastModified() / 1000);
        if (SdkVersionUtils.isQ()) {
            media.setSandboxPath(PictureMimeType.isContent(generatePath) ? null : generatePath);
        }
        return media;
    }

    /**
     * 验证完成选择的先决条件
     *
     * @return
     */
    private boolean checkCompleteSelectLimit() {
        if (config.selectionMode != SelectModeConfig.MULTIPLE || config.isOnlyCamera) {
            return false;
        }
        if (config.isWithVideoImage) {
            // 共选型模式
            ArrayList<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
            int selectImageSize = 0;
            int selectVideoSize = 0;
            for (int i = 0; i < selectedResult.size(); i++) {
                String mimeType = selectedResult.get(i).getMimeType();
                if (PictureMimeType.isHasVideo(mimeType)) {
                    selectVideoSize++;
                } else {
                    selectImageSize++;
                }
            }
            if (config.minSelectNum > 0) {
                if (selectImageSize < config.minSelectNum) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                    showTipsDialog(getString(R.string.ps_min_img_num, String.valueOf(config.minSelectNum)));
                    return true;
                }
            }
            if (config.minVideoSelectNum > 0) {
                if (selectVideoSize < config.minVideoSelectNum) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                    showTipsDialog(
                            getString(R.string.ps_min_video_num, String.valueOf(config.minVideoSelectNum)));
                    return true;
                }
            }
        } else {
            // 单类型模式
            String mimeType = SelectedManager.getTopResultMimeType();
            if (PictureMimeType.isHasImage(mimeType) && config.minSelectNum > 0
                    && SelectedManager.getSelectCount() < config.minSelectNum) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_min_img_num,
                        String.valueOf(config.minSelectNum)));
                return true;
            }
            if (PictureMimeType.isHasVideo(mimeType) && config.minVideoSelectNum > 0
                    && SelectedManager.getSelectCount() < config.minVideoSelectNum) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_min_video_num,
                        String.valueOf(config.minVideoSelectNum)));
                return true;
            }

            if (PictureMimeType.isHasAudio(mimeType) && config.minAudioSelectNum > 0
                    && SelectedManager.getSelectCount() < config.minAudioSelectNum) {
                if (PictureSelectionConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = PictureSelectionConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getContext(), config, SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_min_audio_num,
                        String.valueOf(config.minAudioSelectNum)));
                return true;
            }
        }
        return false;
    }

    /**
     * 分发处理结果，比如压缩、裁剪、沙盒路径转换
     */
    protected void dispatchTransformResult() {
        if (checkCompleteSelectLimit()) {
            return;
        }
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
            HashSet<String> filterSet = new HashSet<>();
            List<String> filters = config.skipCropList;
            if (filters != null && filters.size() > 0) {
                filterSet.addAll(filters);
            }
            if (SelectedManager.getSelectCount() == 1) {
                String mimeType = SelectedManager.getTopResultMimeType();
                boolean isHasImage = PictureMimeType.isHasImage(mimeType);
                if (isHasImage) {
                    if (filterSet.contains(mimeType)) {
                        return false;
                    }
                }
                return isHasImage;
            } else {
                int notSupportCropCount = 0;
                for (int i = 0; i < SelectedManager.getSelectCount(); i++) {
                    LocalMedia media = SelectedManager.getSelectedResult().get(i);
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        if (filterSet.contains(media.getMimeType())) {
                            notSupportCropCount++;
                        }
                    }
                }
                return notSupportCropCount != SelectedManager.getSelectCount();
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
            for (int i = 0; i < SelectedManager.getSelectCount(); i++) {
                LocalMedia media = SelectedManager.getSelectedResult().get(i);
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * SDK > 29 把外部资源copy一份至应用沙盒内
     *
     * @param result
     */
    private void copyExternalPathToAppInDirFor29(ArrayList<LocalMedia> result) {
        showLoading();
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<ArrayList<LocalMedia>>() {
            @Override
            public ArrayList<LocalMedia> doInBackground() {
                for (int i = 0; i < result.size(); i++) {
                    LocalMedia media = result.get(i);
                    PictureSelectionConfig.sandboxFileEngine.onStartSandboxFileTransform(getContext(), config.isCheckOriginalImage, i,
                            media, new OnCallbackIndexListener<LocalMedia>() {
                                @Override
                                public void onCall(LocalMedia data, int index) {
                                    LocalMedia media = result.get(index);
                                    media.setSandboxPath(data.getSandboxPath());
                                    if (config.isCheckOriginalImage) {
                                        media.setOriginalPath(data.getOriginalPath());
                                        media.setOriginal(!TextUtils.isEmpty(data.getOriginalPath()));
                                    }
                                }
                            });
                }
                return result;
            }

            @Override
            public void onSuccess(ArrayList<LocalMedia> result) {
                PictureThreadUtils.cancel(this);
                onCallBackResult(result);
            }
        });
    }

    /**
     * 构造原图数据
     *
     * @param result
     */
    private void mergeOriginalImage(ArrayList<LocalMedia> result) {
        if (config.isCheckOriginalImage) {
            for (int i = 0; i < result.size(); i++) {
                LocalMedia media = result.get(i);
                media.setOriginal(true);
                media.setOriginalPath(media.getPath());
            }
        }
    }

    /**
     * 返回处理完成后的选择结果
     */
    @Override
    public void onResultEvent(ArrayList<LocalMedia> result) {
        if (PictureSelectionConfig.sandboxFileEngine != null) {
            copyExternalPathToAppInDirFor29(result);
        } else {
            mergeOriginalImage(result);
            onCallBackResult(result);
        }
    }


    /**
     * 返回结果
     */
    private void onCallBackResult(ArrayList<LocalMedia> result) {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            dismissLoading();
            if (config.isActivityResultBack) {
                getActivity().setResult(RESULT_OK, PictureSelector.putIntentResult(result));
                onSelectFinish(RESULT_OK, result);
            } else {
                if (PictureSelectionConfig.onResultCallListener != null) {
                    PictureSelectionConfig.onResultCallListener.onResult(result);
                }
            }
            onExitPictureSelector();
        }
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
    public void onKeyBackFragmentFinish() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            if (config.isActivityResultBack) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                onSelectFinish(Activity.RESULT_CANCELED, null);
            } else {
                if (PictureSelectionConfig.onResultCallListener != null) {
                    PictureSelectionConfig.onResultCallListener.onCancel();
                }
            }
            onExitPictureSelector();
        }
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
        }
    }

    /**
     * back current Fragment
     */
    protected void onBackCurrentFragment() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof PictureCommonFragment) {
                ((PictureCommonFragment) fragment).onFragmentResume();
            }
        }
    }

    /**
     * onSelectFinish
     *
     * @param resultCode
     * @param result
     */
    protected void onSelectFinish(int resultCode, ArrayList<LocalMedia> result) {
        if (null != iBridgePictureBehavior) {
            SelectorResult selectorResult = getResult(resultCode, result);
            iBridgePictureBehavior.onSelectFinish(selectorResult);
        }
    }

    /**
     * exit PictureSelector
     */
    protected void onExitPictureSelector() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            if (isNormalDefaultEnter()) {
                getActivity().finish();
            } else {
                List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragments.size(); i++) {
                    Fragment fragment = fragments.get(i);
                    if (fragment instanceof PictureCommonFragment) {
                        onBackCurrentFragment();
                    }
                }
            }
        }
        PictureSelectionConfig.destroy();
    }

    /**
     * Get the image loading engine again, provided that the user implements the IApp interface in the Application
     */
    private void createImageLoaderEngine() {
        if (PictureSelectionConfig.imageEngine == null) {
            PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
            if (baseEngine != null) {
                PictureSelectionConfig.imageEngine = baseEngine.createImageLoaderEngine();
            }
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
        if (PictureSelectionConfig.getInstance().isResultListenerBack) {
            if (PictureSelectionConfig.onResultCallListener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    PictureSelectionConfig.onResultCallListener = baseEngine.getResultCallbackListener();
                }
            }
        }
    }

    /**
     * Retrieve the layout callback listener, provided that the user implements the IApp interface in the Application
     */
    private void createLayoutResourceListener() {
        if (PictureSelectionConfig.getInstance().isInjectLayoutResource) {
            if (PictureSelectionConfig.onLayoutResourceListener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    PictureSelectionConfig.onLayoutResourceListener = baseEngine.createLayoutResourceListener();
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
        return new SelectorResult(resultCode, data != null ? PictureSelector.putIntentResult(data) : null);
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
