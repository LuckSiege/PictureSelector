package com.luck.picture.lib.basic;

import static android.app.Activity.RESULT_OK;
import static android.view.KeyEvent.ACTION_UP;

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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.R;
import com.luck.picture.lib.app.PictureAppMaster;
import com.luck.picture.lib.config.Crop;
import com.luck.picture.lib.config.CustomIntentKey;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PermissionEvent;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.dialog.PhotoItemSelectedDialog;
import com.luck.picture.lib.dialog.PictureLoadingDialog;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.PictureSelectorEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.immersive.ImmersiveManager;
import com.luck.picture.lib.interfaces.OnCallbackIndexListener;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnItemClickListener;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
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
import com.luck.picture.lib.utils.FileDirMap;
import com.luck.picture.lib.utils.MediaStoreUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    protected SelectorConfig selectorConfig;

    /**
     * Loading Dialog
     */
    private Dialog mLoadingDialog;

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

    /**
     * Context
     */
    private Context context;

    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onCreateLoader() {
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

    protected Context getAppContext() {
        Context ctx = getContext();
        if (ctx != null) {
            return ctx;
        } else {
            Context appContext = PictureAppMaster.getInstance().getAppContext();
            if (appContext != null) {
                return appContext;
            }
        }
        return context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionResultCallback != null) {
            PermissionChecker.getInstance().onRequestPermissionsResult(getContext(),permissions,grantResults, mPermissionResultCallback);
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
        if (selectorConfig.onPermissionDeniedListener != null) {
            onPermissionExplainEvent(false, permissionArray);
            selectorConfig.onPermissionDeniedListener
                    .onDenied(this, permissionArray, PictureConfig.REQUEST_GO_SETTING,
                            new OnCallbackListener<Boolean>() {
                                @Override
                                public void onCall(Boolean isResult) {
                                    if (isResult) {
                                        handlePermissionSettingResult(PermissionConfig.CURRENT_REQUEST_PERMISSION);
                                    }
                                }
                            });
        } else {
            PermissionUtil.goIntentSetting(this, PictureConfig.REQUEST_GO_SETTING);
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
        if (getResourceId() != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) {
            return inflater.inflate(getResourceId(), container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectorConfig = SelectorProviders.getInstance().getSelectorConfig();
        FileDirMap.init(view.getContext());
        if (selectorConfig.viewLifecycle != null) {
            selectorConfig.viewLifecycle.onViewCreated(this, view, savedInstanceState);
        }
        if (selectorConfig.onCustomLoadingListener != null) {
            mLoadingDialog = selectorConfig.onCustomLoadingListener.create(getAppContext());
        } else {
            mLoadingDialog = new PictureLoadingDialog(getAppContext());
        }
        setRequestedOrientation();
        setTranslucentStatusBar();
        setRootViewKeyListener(requireView());
        if (selectorConfig.isOpenClickSound && !selectorConfig.isOnlyCamera) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            soundID = soundPool.load(getAppContext(), R.raw.ps_click_music, 1);
        }
    }


    /**
     * 设置透明状态栏
     */
    private void setTranslucentStatusBar() {
        if (selectorConfig.isPreviewFullScreenMode) {
            SelectMainStyle selectMainStyle = selectorConfig.selectorStyle.getSelectMainStyle();
            ImmersiveManager.translucentStatusBar(requireActivity(), selectMainStyle.isDarkStatusBarBlack());
        }
    }

    /**
     * 设置回退监听
     *
     * @param view
     */
    public void setRootViewKeyListener(View view) {
        if (selectorConfig.isNewKeyBackMode) {
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onKeyBackFragmentFinish();
                }
            });
        } else {
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
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initAppLanguage();
    }


    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        PictureWindowAnimationStyle windowAnimationStyle = selectorConfig.selectorStyle.getWindowAnimationStyle();
        Animation loadAnimation;
        if (enter) {
            if (windowAnimationStyle.activityEnterAnimation != 0) {
                loadAnimation = AnimationUtils.loadAnimation(getAppContext(), windowAnimationStyle.activityEnterAnimation);
            } else {
                loadAnimation = AnimationUtils.loadAnimation(getAppContext(), R.anim.ps_anim_alpha_enter);
            }
            setEnterAnimationDuration(loadAnimation.getDuration());
            onEnterFragment();
        } else {
            if (windowAnimationStyle.activityExitAnimation != 0) {
                loadAnimation = AnimationUtils.loadAnimation(getAppContext(), windowAnimationStyle.activityExitAnimation);
            } else {
                loadAnimation = AnimationUtils.loadAnimation(getAppContext(), R.anim.ps_anim_alpha_exit);
            }
            onExitFragment();
        }
        return loadAnimation;
    }

    public void setEnterAnimationDuration(long duration) {
        this.enterAnimDuration = duration;
    }


    public long getEnterAnimationDuration() {
        long duration = enterAnimDuration > 50 ? enterAnimDuration - 50 : enterAnimDuration;
        return duration >= 0 ? duration : 0;
    }


    @Override
    public int confirmSelect(LocalMedia currentMedia, boolean isSelected) {
        if (selectorConfig.onSelectFilterListener != null) {
            if (selectorConfig.onSelectFilterListener.onSelectFilter(currentMedia)) {
                boolean isSelectLimit = false;
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), currentMedia, selectorConfig, SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT);
                }
                if (isSelectLimit) {
                } else {
                    ToastUtils.showToast(getAppContext(), getString(R.string.ps_select_no_support));
                }
                return SelectedManager.INVALID;
            }
        }
        int checkSelectValidity = isCheckSelectValidity(currentMedia, isSelected);
        if (checkSelectValidity != SelectedManager.SUCCESS) {
            return SelectedManager.INVALID;
        }
        List<LocalMedia> selectedResult = selectorConfig.getSelectedResult();
        int resultCode;
        if (isSelected) {
            selectedResult.remove(currentMedia);
            resultCode = SelectedManager.REMOVE;
        } else {
            if (selectorConfig.selectionMode == SelectModeConfig.SINGLE) {
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
        List<LocalMedia> selectedResult = selectorConfig.getSelectedResult();
        if (selectorConfig.isWithVideoImage) {
            // 共选型模式
            int selectVideoSize = 0;
            for (int i = 0; i < selectedResult.size(); i++) {
                String mimeType = selectedResult.get(i).getMimeType();
                if (PictureMimeType.isHasVideo(mimeType)) {
                    selectVideoSize++;
                }
            }
            if (checkWithMimeTypeValidity(currentMedia,isSelected, curMimeType, selectVideoSize, curFileSize, curDuration)) {
                return SelectedManager.INVALID;
            }
        } else {
            // 单一型模式
            if (checkOnlyMimeTypeValidity(currentMedia,isSelected, curMimeType, selectorConfig.getResultFirstMimeType(), curFileSize, curDuration)) {
                return SelectedManager.INVALID;
            }
        }
        return SelectedManager.SUCCESS;
    }

    @SuppressLint({"StringFormatInvalid", "StringFormatMatches"})
    @Override
    public boolean checkWithMimeTypeValidity(LocalMedia media, boolean isSelected, String curMimeType, int selectVideoSize, long fileSize, long duration) {
        if (selectorConfig.selectMaxFileSize > 0) {
            if (fileSize > selectorConfig.selectMaxFileSize) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), media, selectorConfig,
                                    SelectLimitType.SELECT_MAX_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String maxFileSize = PictureFileUtils.formatFileSize(selectorConfig.selectMaxFileSize);
                showTipsDialog(getString(R.string.ps_select_max_size, maxFileSize));
                return true;
            }
        }
        if (selectorConfig.selectMinFileSize > 0) {
            if (fileSize < selectorConfig.selectMinFileSize) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), media, selectorConfig,
                                    SelectLimitType.SELECT_MIN_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String minFileSize = PictureFileUtils.formatFileSize(selectorConfig.selectMinFileSize);
                showTipsDialog(getString(R.string.ps_select_min_size, minFileSize));
                return true;
            }
        }

        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (selectorConfig.selectionMode == SelectModeConfig.MULTIPLE) {
                if (selectorConfig.maxVideoSelectNum <= 0) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(), media, selectorConfig, SelectLimitType.SELECT_NOT_WITH_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    // 如果视频可选数量是0
                    showTipsDialog(getString(R.string.ps_rule));
                    return true;
                }

                if (!isSelected && selectorConfig.getSelectedResult().size() >= selectorConfig.maxSelectNum) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(), media, selectorConfig, SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getString(R.string.ps_message_max_num, selectorConfig.maxSelectNum));
                    return true;
                }

                if (!isSelected && selectVideoSize >= selectorConfig.maxVideoSelectNum) {
                    // 如果选择的是视频
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(), media, selectorConfig, SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getAppContext(), curMimeType, selectorConfig.maxVideoSelectNum));
                    return true;
                }
            }

            if (!isSelected && selectorConfig.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(duration) < selectorConfig.selectMinDurationSecond) {
                // 视频小于最低指定的长度
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), media,  selectorConfig,
                                    SelectLimitType.SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_min_second, selectorConfig.selectMinDurationSecond / 1000));
                return true;
            }

            if (!isSelected && selectorConfig.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(duration) > selectorConfig.selectMaxDurationSecond) {
                // 视频时长超过了指定的长度
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(),  media, selectorConfig,
                                    SelectLimitType.SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_max_second, selectorConfig.selectMaxDurationSecond / 1000));
                return true;
            }
        } else {
            if (selectorConfig.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && selectorConfig.getSelectedResult().size() >= selectorConfig.maxSelectNum) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(),  media, selectorConfig,
                                        SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getString(R.string.ps_message_max_num, selectorConfig.maxSelectNum));
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public boolean checkOnlyMimeTypeValidity(LocalMedia media, boolean isSelected, String curMimeType, String existMimeType, long fileSize, long duration) {
        if (PictureMimeType.isMimeTypeSame(existMimeType, curMimeType)) {
            // ignore
        } else {
            if (selectorConfig.onSelectLimitTipsListener != null) {
                boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                        .onSelectLimitTips(getAppContext(), media, selectorConfig, SelectLimitType.SELECT_NOT_WITH_SELECT_LIMIT);
                if (isSelectLimit) {
                    return true;
                }
            }
            showTipsDialog(getString(R.string.ps_rule));
            return true;
        }
        if (selectorConfig.selectMaxFileSize > 0) {
            if (fileSize > selectorConfig.selectMaxFileSize) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), media, selectorConfig,
                                    SelectLimitType.SELECT_MAX_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String maxFileSize = PictureFileUtils.formatFileSize(selectorConfig.selectMaxFileSize);
                showTipsDialog(getString(R.string.ps_select_max_size, maxFileSize));
                return true;
            }
        }
        if (selectorConfig.selectMinFileSize > 0) {
            if (fileSize < selectorConfig.selectMinFileSize) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(),  media, selectorConfig,
                                    SelectLimitType.SELECT_MIN_FILE_SIZE_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                String minFileSize = PictureFileUtils.formatFileSize(selectorConfig.selectMinFileSize);
                showTipsDialog(getString(R.string.ps_select_min_size, minFileSize));
                return true;
            }
        }
        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (selectorConfig.selectionMode == SelectModeConfig.MULTIPLE) {
                selectorConfig.maxVideoSelectNum = selectorConfig.maxVideoSelectNum > 0 ? selectorConfig.maxVideoSelectNum : selectorConfig.maxSelectNum;
                if (!isSelected && selectorConfig.getSelectCount() >= selectorConfig.maxVideoSelectNum) {
                    // 如果先选择的是视频
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getAppContext(), curMimeType, selectorConfig.maxVideoSelectNum));
                    return true;
                }
            }
            if (!isSelected && selectorConfig.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(duration) < selectorConfig.selectMinDurationSecond) {
                // 视频小于最低指定的长度
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_min_second, selectorConfig.selectMinDurationSecond / 1000));
                return true;
            }

            if (!isSelected && selectorConfig.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(duration) > selectorConfig.selectMaxDurationSecond) {
                // 视频时长超过了指定的长度
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_video_max_second, selectorConfig.selectMaxDurationSecond / 1000));
                return true;
            }
        } else if (PictureMimeType.isHasAudio(curMimeType)) {
            if (selectorConfig.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && selectorConfig.getSelectedResult().size() >= selectorConfig.maxSelectNum) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getAppContext(), curMimeType, selectorConfig.maxSelectNum));
                    return true;
                }
            }

            if (!isSelected && selectorConfig.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(duration) < selectorConfig.selectMinDurationSecond) {
                // 音频小于最低指定的长度
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MIN_AUDIO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_audio_min_second, selectorConfig.selectMinDurationSecond / 1000));
                return true;
            }
            if (!isSelected && selectorConfig.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(duration) > selectorConfig.selectMaxDurationSecond) {
                // 音频时长超过了指定的长度
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MAX_AUDIO_SECOND_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_select_audio_max_second, selectorConfig.selectMaxDurationSecond / 1000));
                return true;
            }
        } else {
            if (selectorConfig.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && selectorConfig.getSelectedResult().size() >= selectorConfig.maxSelectNum) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(),  media, selectorConfig, SelectLimitType.SELECT_MAX_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getTipsMsg(getAppContext(), curMimeType, selectorConfig.maxSelectNum));
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
            tipsDialog = RemindDialog.buildDialog(getAppContext(), tips);
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
        switch (selectorConfig.chooseMode) {
            case SelectMimeType.TYPE_ALL:
                if (selectorConfig.ofAllCameraType == SelectMimeType.ofImage()) {
                    openImageCamera();
                } else if (selectorConfig.ofAllCameraType == SelectMimeType.ofVideo()) {
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
                        if (selectorConfig.onCameraInterceptListener != null) {
                            onInterceptCameraEvent(SelectMimeType.TYPE_IMAGE);
                        } else {
                            openImageCamera();
                        }
                        break;
                    case PhotoItemSelectedDialog.VIDEO_CAMERA:
                        if (selectorConfig.onCameraInterceptListener != null) {
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
                if (selectorConfig.isOnlyCamera && isCancel) {
                    onKeyBackFragmentFinish();
                }
            }
        });
        selectedDialog.show(getChildFragmentManager(), "PhotoItemSelectedDialog");
    }

    @Override
    public void openImageCamera() {
        onPermissionExplainEvent(true, PermissionConfig.CAMERA);
        if (selectorConfig.onPermissionsEventListener != null) {
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
            if (selectorConfig.onCameraInterceptListener != null) {
                onInterceptCameraEvent(SelectMimeType.TYPE_IMAGE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    ForegroundService.startForegroundService(getAppContext(), selectorConfig.isCameraForegroundService);
                    Uri imageUri = MediaStoreUtils.createCameraOutImageUri(getAppContext(), selectorConfig);
                    if (imageUri != null) {
                        if (selectorConfig.isCameraAroundState) {
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
        if (selectorConfig.onPermissionsEventListener != null) {
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
            if (selectorConfig.onCameraInterceptListener != null) {
                onInterceptCameraEvent(SelectMimeType.TYPE_VIDEO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    ForegroundService.startForegroundService(getAppContext(), selectorConfig.isCameraForegroundService);
                    Uri videoUri = MediaStoreUtils.createCameraOutVideoUri(getAppContext(), selectorConfig);
                    if (videoUri != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        if (selectorConfig.isCameraAroundState) {
                            cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
                        }
                        cameraIntent.putExtra(PictureConfig.EXTRA_QUICK_CAPTURE, selectorConfig.isQuickCapture);
                        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, selectorConfig.recordVideoMaxSecond);
                        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, selectorConfig.videoQuality);
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                }
            }
        }
    }


    @Override
    public void openSoundRecording() {
        if (selectorConfig.onRecordAudioListener != null) {
            ForegroundService.startForegroundService(getAppContext(), selectorConfig.isCameraForegroundService);
            selectorConfig.onRecordAudioListener.onRecordAudio(this, PictureConfig.REQUEST_CAMERA);
        } else {
            throw new NullPointerException(OnRecordAudioInterceptListener.class.getSimpleName() + " interface needs to be implemented for recording");
        }
    }


    /**
     * 拦截相机事件并处理返回结果
     */
    @Override
    public void onInterceptCameraEvent(int cameraMode) {
        ForegroundService.startForegroundService(getAppContext(), selectorConfig.isCameraForegroundService);
        selectorConfig.onCameraInterceptListener.openCamera(this, cameraMode, PictureConfig.REQUEST_CAMERA);
    }

    /**
     * 权限申请
     *
     * @param permissionArray
     */
    @Override
    public void onApplyPermissionsEvent(int event, String[] permissionArray) {
        selectorConfig.onPermissionsEventListener.requestPermission(this, permissionArray,
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
        if (selectorConfig.onPermissionDescriptionListener != null) {
            if (PermissionChecker.isCheckSelfPermission(getAppContext(), permissionArray)) {
                selectorConfig.onPermissionDescriptionListener.onDismiss(this);
            } else {
                if (isDisplayExplain) {
                    int permissionStatus = PermissionUtil.getPermissionStatus(requireActivity(), permissionArray[0]);
                    if (permissionStatus != PermissionUtil.REFUSE_PERMANENT) {
                        selectorConfig.onPermissionDescriptionListener.onPermissionDescription(this, permissionArray);
                    }
                } else {
                    selectorConfig.onPermissionDescriptionListener.onDismiss(this);
                }
            }
        }
    }

    /**
     * 点击选择的音效
     */
    private void playClickEffect() {
        if (soundPool != null && selectorConfig.isOpenClickSound) {
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                dispatchHandleCamera(data);
            } else if (requestCode == Crop.REQUEST_EDIT_CROP) {
                onEditMedia(data);
            } else if (requestCode == Crop.REQUEST_CROP) {
                List<LocalMedia> selectedResult = selectorConfig.getSelectedResult();
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
                        if (TextUtils.isEmpty(extra)) {
                            extra = data.getStringExtra(CustomIntentKey.EXTRA_OUTPUT_URI);
                        }
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
                    ToastUtils.showToast(getAppContext(), e.getMessage());
                }

                ArrayList<LocalMedia> result = new ArrayList<>(selectedResult);
                if (checkCompressValidity()) {
                    onCompress(result);
                } else if (checkOldCompressValidity()) {
                    onOldCompress(result);
                } else {
                    onResultEvent(result);
                }
            }
        } else if (resultCode == Crop.RESULT_CROP_ERROR) {
            Throwable throwable = data != null ? Crop.getError(data) : new Throwable("image crop error");
            if (throwable != null) {
                ToastUtils.showToast(getAppContext(), throwable.getMessage());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                if (!TextUtils.isEmpty(selectorConfig.cameraPath)) {
                    MediaUtils.deleteUri(getAppContext(), selectorConfig.cameraPath);
                    selectorConfig.cameraPath = "";
                }
            } else if (requestCode == PictureConfig.REQUEST_GO_SETTING) {
                handlePermissionSettingResult(PermissionConfig.CURRENT_REQUEST_PERMISSION);
            }
        }
        ForegroundService.stopService(getAppContext());
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
                    selectorConfig.cameraPath = outputPath;
                }
                if (TextUtils.isEmpty(selectorConfig.cameraPath)) {
                    return null;
                }
                if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) {
                    copyOutputAudioToDir();
                }
                LocalMedia media = buildLocalMedia(selectorConfig.cameraPath);
                media.setCameraSource(true);
                return media;
            }

            @Override
            public void onSuccess(LocalMedia result) {
                PictureThreadUtils.cancel(this);
                if (result != null) {
                    onScannerScanFile(result);
                    dispatchCameraMediaResult(result);
                }
                selectorConfig.cameraPath = "";
            }
        });
    }

    /**
     * copy录音文件至指定目录
     */
    private void copyOutputAudioToDir() {
        try {
            if (!TextUtils.isEmpty(selectorConfig.outPutAudioDir)) {
                InputStream inputStream = PictureMimeType.isContent(selectorConfig.cameraPath)
                        ? PictureContentResolver.openInputStream(getAppContext(), Uri.parse(selectorConfig.cameraPath)) : new FileInputStream(selectorConfig.cameraPath);
                String audioFileName;
                if (TextUtils.isEmpty(selectorConfig.outPutAudioFileName)) {
                    audioFileName = "";
                } else {
                    audioFileName = selectorConfig.isOnlyCamera
                            ? selectorConfig.outPutAudioFileName : System.currentTimeMillis() + "_" + selectorConfig.outPutAudioFileName;
                }
                File outputFile = PictureFileUtils.createCameraFile(getAppContext(),
                        selectorConfig.chooseMode, audioFileName, "", selectorConfig.outPutAudioDir);
                FileOutputStream outputStream = new FileOutputStream(outputFile.getAbsolutePath());
                if (PictureFileUtils.writeFileFromIS(inputStream, outputStream)) {
                    MediaUtils.deleteUri(getAppContext(), selectorConfig.cameraPath);
                    selectorConfig.cameraPath = outputFile.getAbsolutePath();
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
        if (data == null) {
            return null;
        }
        Uri outPutUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        String cameraPath = selectorConfig.cameraPath;
        boolean isCameraFileExists = TextUtils.isEmpty(cameraPath) || PictureMimeType.isContent(cameraPath) || new File(cameraPath).exists();
        if ((selectorConfig.chooseMode == SelectMimeType.ofAudio() || !isCameraFileExists) && outPutUri == null) {
            outPutUri = data.getData();
        }
        if (outPutUri == null) {
            return null;
        }
        return PictureMimeType.isContent(outPutUri.toString()) ? outPutUri.toString() : outPutUri.getPath();
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
            if (PictureMimeType.isHasVideo(media.getMimeType()) && PictureMimeType.isContent(media.getPath())) {
                new PictureMediaScannerConnection(getActivity(), media.getRealPath());
            }
        } else {
            String path = PictureMimeType.isContent(media.getPath()) ? media.getRealPath() : media.getPath();
            new PictureMediaScannerConnection(getActivity(), path);
            if (PictureMimeType.isHasImage(media.getMimeType())) {
                File dirFile = new File(path);
                int lastImageId = MediaUtils.getDCIMLastImageId(getAppContext(), dirFile.getParent());
                if (lastImageId != -1) {
                    MediaUtils.removeMedia(getAppContext(), lastImageId);
                }
            }
        }
    }

    /**
     * buildLocalMedia
     *
     * @param absolutePath
     */
    protected LocalMedia buildLocalMedia(String absolutePath) {
        LocalMedia media = LocalMedia.generateLocalMedia(getAppContext(), absolutePath);
        media.setChooseModel(selectorConfig.chooseMode);
        if (SdkVersionUtils.isQ() && !PictureMimeType.isContent(absolutePath)) {
            media.setSandboxPath(absolutePath);
        } else {
            media.setSandboxPath(null);
        }
        if (selectorConfig.isCameraRotateImage && PictureMimeType.isHasImage(media.getMimeType())) {
            BitmapUtils.rotateImage(getAppContext(), absolutePath);
        }
        return media;
    }

    /**
     * 验证完成选择的先决条件
     *
     * @return
     */
    private boolean checkCompleteSelectLimit() {
        if (selectorConfig.selectionMode != SelectModeConfig.MULTIPLE || selectorConfig.isOnlyCamera) {
            return false;
        }
        if (selectorConfig.isWithVideoImage) {
            // 共选型模式
            ArrayList<LocalMedia> selectedResult = selectorConfig.getSelectedResult();
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
            if (selectorConfig.minSelectNum > 0) {
                if (selectImageSize < selectorConfig.minSelectNum) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(), null, selectorConfig, SelectLimitType.SELECT_MIN_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(getString(R.string.ps_min_img_num, String.valueOf(selectorConfig.minSelectNum)));
                    return true;
                }
            }
            if (selectorConfig.minVideoSelectNum > 0) {
                if (selectVideoSize < selectorConfig.minVideoSelectNum) {
                    if (selectorConfig.onSelectLimitTipsListener != null) {
                        boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                                .onSelectLimitTips(getAppContext(), null, selectorConfig, SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT);
                        if (isSelectLimit) {
                            return true;
                        }
                    }
                    showTipsDialog(
                            getString(R.string.ps_min_video_num, String.valueOf(selectorConfig.minVideoSelectNum)));
                    return true;
                }
            }
        } else {
            // 单类型模式
            String mimeType = selectorConfig.getResultFirstMimeType();
            if (PictureMimeType.isHasImage(mimeType) && selectorConfig.minSelectNum > 0
                    && selectorConfig.getSelectCount() < selectorConfig.minSelectNum) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), null, selectorConfig, SelectLimitType.SELECT_MIN_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_min_img_num,
                        String.valueOf(selectorConfig.minSelectNum)));
                return true;
            }
            if (PictureMimeType.isHasVideo(mimeType) && selectorConfig.minVideoSelectNum > 0
                    && selectorConfig.getSelectCount() < selectorConfig.minVideoSelectNum) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), null, selectorConfig, SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_min_video_num,
                        String.valueOf(selectorConfig.minVideoSelectNum)));
                return true;
            }

            if (PictureMimeType.isHasAudio(mimeType) && selectorConfig.minAudioSelectNum > 0
                    && selectorConfig.getSelectCount() < selectorConfig.minAudioSelectNum) {
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    boolean isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), null, selectorConfig, SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT);
                    if (isSelectLimit) {
                        return true;
                    }
                }
                showTipsDialog(getString(R.string.ps_min_audio_num,
                        String.valueOf(selectorConfig.minAudioSelectNum)));
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
        if (!isAdded()) {
            return;
        }
        ArrayList<LocalMedia> selectedResult = selectorConfig.getSelectedResult();
        ArrayList<LocalMedia> result = new ArrayList<>(selectedResult);
        if (checkCropValidity()) {
            onCrop(result);
        } else if (checkOldCropValidity()) {
            onOldCrop(result);
        } else if (checkCompressValidity()) {
            onCompress(result);
        } else if (checkOldCompressValidity()) {
            onOldCompress(result);
        } else {
            onResultEvent(result);
        }
    }

    @Override
    public void onCrop(ArrayList<LocalMedia> result) {
        Uri srcUri = null;
        Uri destinationUri = null;
        ArrayList<String> dataCropSource = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            LocalMedia media = result.get(i);
            dataCropSource.add(media.getAvailablePath());
            if (srcUri == null && PictureMimeType.isHasImage(media.getMimeType())) {
                String currentCropPath = media.getAvailablePath();
                if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
                    srcUri = Uri.parse(currentCropPath);
                } else {
                    srcUri = Uri.fromFile(new File(currentCropPath));
                }
                String fileName = DateUtils.getCreateFileName("CROP_") + ".jpg";
                Context context = getAppContext();
                File externalFilesDir = new File(FileDirMap.getFileDirPath(context, SelectMimeType.TYPE_IMAGE));
                File outputFile = new File(externalFilesDir.getAbsolutePath(), fileName);
                destinationUri = Uri.fromFile(outputFile);
            }
        }
        selectorConfig.cropFileEngine.onStartCrop(this, srcUri, destinationUri, dataCropSource, Crop.REQUEST_CROP);
    }

    @Override
    public void onOldCrop(ArrayList<LocalMedia> result) {
        LocalMedia currentLocalMedia = null;
        for (int i = 0; i < result.size(); i++) {
            LocalMedia item = result.get(i);
            if (PictureMimeType.isHasImage(result.get(i).getMimeType())) {
                currentLocalMedia = item;
                break;
            }
        }
        selectorConfig.cropEngine.onStartCrop(this, currentLocalMedia, result, Crop.REQUEST_CROP);
    }

    @Override
    public void onCompress(ArrayList<LocalMedia> result) {
        showLoading();
        ConcurrentHashMap<String, LocalMedia> queue = new ConcurrentHashMap<>();
        ArrayList<Uri> source = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            LocalMedia media = result.get(i);
            String availablePath = media.getAvailablePath();
            if (PictureMimeType.isHasHttp(availablePath)) {
                continue;
            }
            if (selectorConfig.isCheckOriginalImage && selectorConfig.isOriginalSkipCompress) {
                continue;
            }
            if (PictureMimeType.isHasImage(media.getMimeType())) {
                Uri uri = PictureMimeType.isContent(availablePath) ? Uri.parse(availablePath) : Uri.fromFile(new File(availablePath));
                source.add(uri);
                queue.put(availablePath, media);
            }
        }
        if (queue.size() == 0) {
            onResultEvent(result);
        } else {
            selectorConfig.compressFileEngine.onStartCompress(getAppContext(), source, new OnKeyValueResultCallbackListener() {
                @Override
                public void onCallback(String srcPath, String compressPath) {
                    if (TextUtils.isEmpty(srcPath)) {
                        onResultEvent(result);
                    } else {
                        LocalMedia media = queue.get(srcPath);
                        if (media != null) {
                            if (SdkVersionUtils.isQ()){
                                if (!TextUtils.isEmpty(compressPath) && (compressPath.contains("Android/data/")
                                        || compressPath.contains("data/user/"))) {
                                    media.setCompressPath(compressPath);
                                    media.setCompressed(!TextUtils.isEmpty(compressPath));
                                    media.setSandboxPath(media.getCompressPath());
                                }
                            } else {
                                media.setCompressPath(compressPath);
                                media.setCompressed(!TextUtils.isEmpty(compressPath));
                            }
                            queue.remove(srcPath);
                        }
                        if (queue.size() == 0) {
                            onResultEvent(result);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onOldCompress(ArrayList<LocalMedia> result) {
        showLoading();
        if (selectorConfig.isCheckOriginalImage && selectorConfig.isOriginalSkipCompress) {
            onResultEvent(result);
        } else {
            selectorConfig.compressEngine.onStartCompress(getAppContext(), result,
                    new OnCallbackListener<ArrayList<LocalMedia>>() {
                        @Override
                        public void onCall(ArrayList<LocalMedia> result) {
                            onResultEvent(result);
                        }
                    });
        }
    }

    @Override
    public boolean checkCropValidity() {
        if (selectorConfig.cropFileEngine != null) {
            HashSet<String> filterSet = new HashSet<>();
            List<String> filters = selectorConfig.skipCropList;
            if (filters != null && filters.size() > 0) {
                filterSet.addAll(filters);
            }
            if (selectorConfig.getSelectCount() == 1) {
                String mimeType = selectorConfig.getResultFirstMimeType();
                boolean isHasImage = PictureMimeType.isHasImage(mimeType);
                if (isHasImage) {
                    if (filterSet.contains(mimeType)) {
                        return false;
                    }
                }
                return isHasImage;
            } else {
                int notSupportCropCount = 0;
                for (int i = 0; i < selectorConfig.getSelectCount(); i++) {
                    LocalMedia media = selectorConfig.getSelectedResult().get(i);
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        if (filterSet.contains(media.getMimeType())) {
                            notSupportCropCount++;
                        }
                    }
                }
                return notSupportCropCount != selectorConfig.getSelectCount();
            }
        }
        return false;
    }

    @Override
    public boolean checkOldCropValidity() {
        if (selectorConfig.cropEngine != null) {
            HashSet<String> filterSet = new HashSet<>();
            List<String> filters = selectorConfig.skipCropList;
            if (filters != null && filters.size() > 0) {
                filterSet.addAll(filters);
            }
            if (selectorConfig.getSelectCount() == 1) {
                String mimeType = selectorConfig.getResultFirstMimeType();
                boolean isHasImage = PictureMimeType.isHasImage(mimeType);
                if (isHasImage) {
                    if (filterSet.contains(mimeType)) {
                        return false;
                    }
                }
                return isHasImage;
            } else {
                int notSupportCropCount = 0;
                for (int i = 0; i < selectorConfig.getSelectCount(); i++) {
                    LocalMedia media = selectorConfig.getSelectedResult().get(i);
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        if (filterSet.contains(media.getMimeType())) {
                            notSupportCropCount++;
                        }
                    }
                }
                return notSupportCropCount != selectorConfig.getSelectCount();
            }
        }
        return false;
    }


    @Override
    public boolean checkCompressValidity() {
        if (selectorConfig.compressFileEngine != null) {
            for (int i = 0; i < selectorConfig.getSelectCount(); i++) {
                LocalMedia media = selectorConfig.getSelectedResult().get(i);
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkOldCompressValidity() {
        if (selectorConfig.compressEngine != null) {
            for (int i = 0; i < selectorConfig.getSelectCount(); i++) {
                LocalMedia media = selectorConfig.getSelectedResult().get(i);
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkTransformSandboxFile() {
        return SdkVersionUtils.isQ() && selectorConfig.uriToFileTransformEngine != null;
    }

    @Override
    public boolean checkOldTransformSandboxFile() {
        return SdkVersionUtils.isQ() && selectorConfig.sandboxFileEngine != null;
    }

    @Override
    public boolean checkAddBitmapWatermark() {
        return selectorConfig.onBitmapWatermarkListener != null;
    }

    @Override
    public boolean checkVideoThumbnail() {
        return selectorConfig.onVideoThumbnailEventListener != null;
    }

    /**
     * 处理视频的缩略图
     *
     * @param result
     */
    private void videoThumbnail(ArrayList<LocalMedia> result) {
        ConcurrentHashMap<String, LocalMedia> queue = new ConcurrentHashMap<>();
        for (int i = 0; i < result.size(); i++) {
            LocalMedia media = result.get(i);
            String availablePath = media.getAvailablePath();
            if (PictureMimeType.isHasVideo(media.getMimeType()) || PictureMimeType.isUrlHasVideo(availablePath)) {
                queue.put(availablePath, media);
            }
        }
        if (queue.size() == 0) {
            onCallBackResult(result);
        } else {
            for (Map.Entry<String, LocalMedia> entry : queue.entrySet()) {
                selectorConfig.onVideoThumbnailEventListener.onVideoThumbnail(getAppContext(), entry.getKey(), new OnKeyValueResultCallbackListener() {
                    @Override
                    public void onCallback(String srcPath, String resultPath) {
                        LocalMedia media = queue.get(srcPath);
                        if (media != null) {
                            media.setVideoThumbnailPath(resultPath);
                            queue.remove(srcPath);
                        }
                        if (queue.size() == 0) {
                            onCallBackResult(result);
                        }
                    }
                });
            }
        }
    }

    /**
     * 添加水印
     */
    private void addBitmapWatermark(ArrayList<LocalMedia> result) {
        ConcurrentHashMap<String, LocalMedia> queue = new ConcurrentHashMap<>();
        for (int i = 0; i < result.size(); i++) {
            LocalMedia media = result.get(i);
            if (PictureMimeType.isHasAudio(media.getMimeType())) {
                continue;
            }
            String availablePath = media.getAvailablePath();
            queue.put(availablePath, media);
        }
        if (queue.size() == 0) {
            dispatchWatermarkResult(result);
        } else {
            for (Map.Entry<String, LocalMedia> entry : queue.entrySet()) {
                String srcPath = entry.getKey();
                LocalMedia media = entry.getValue();
                selectorConfig.onBitmapWatermarkListener.onAddBitmapWatermark(getAppContext(),
                        srcPath, media.getMimeType(), new OnKeyValueResultCallbackListener() {
                            @Override
                            public void onCallback(String srcPath, String resultPath) {
                                if (TextUtils.isEmpty(srcPath)) {
                                    dispatchWatermarkResult(result);
                                } else {
                                    LocalMedia media = queue.get(srcPath);
                                    if (media != null) {
                                        media.setWatermarkPath(resultPath);
                                        queue.remove(srcPath);
                                    }
                                    if (queue.size() == 0) {
                                        dispatchWatermarkResult(result);
                                    }
                                }
                            }
                        });
            }
        }
    }

    /**
     * dispatchUriToFileTransformResult
     *
     * @param result
     */
    private void dispatchUriToFileTransformResult(ArrayList<LocalMedia> result) {
        showLoading();
        if (checkAddBitmapWatermark()) {
            addBitmapWatermark(result);
        } else if (checkVideoThumbnail()) {
            videoThumbnail(result);
        } else {
            onCallBackResult(result);
        }
    }


    /**
     * dispatchWatermarkResult
     *
     * @param result
     */
    private void dispatchWatermarkResult(ArrayList<LocalMedia> result) {
        if (checkVideoThumbnail()) {
            videoThumbnail(result);
        } else {
            onCallBackResult(result);
        }
    }

    /**
     * SDK > 29 把外部资源copy一份至应用沙盒内
     *
     * @param result
     */
    private void uriToFileTransform29(ArrayList<LocalMedia> result) {
        showLoading();
        ConcurrentHashMap<String, LocalMedia> queue = new ConcurrentHashMap<>();
        for (int i = 0; i < result.size(); i++) {
            LocalMedia media = result.get(i);
            queue.put(media.getPath(), media);
        }
        if (queue.size() == 0) {
            dispatchUriToFileTransformResult(result);
        } else {
            PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<ArrayList<LocalMedia>>() {

                @Override
                public ArrayList<LocalMedia> doInBackground() {
                    for (Map.Entry<String, LocalMedia> entry : queue.entrySet()) {
                        LocalMedia media = entry.getValue();
                        if (selectorConfig.isCheckOriginalImage || TextUtils.isEmpty(media.getSandboxPath())) {
                            selectorConfig.uriToFileTransformEngine.onUriToFileAsyncTransform(getAppContext(), media.getPath(), media.getMimeType(), new OnKeyValueResultCallbackListener() {
                                @Override
                                public void onCallback(String srcPath, String resultPath) {
                                    if (TextUtils.isEmpty(srcPath)) {
                                        return;
                                    }
                                    LocalMedia media = queue.get(srcPath);
                                    if (media != null) {
                                        if (TextUtils.isEmpty(media.getSandboxPath())) {
                                            media.setSandboxPath(resultPath);
                                        }
                                        if (selectorConfig.isCheckOriginalImage) {
                                            media.setOriginalPath(resultPath);
                                            media.setOriginal(!TextUtils.isEmpty(resultPath));
                                        }
                                        queue.remove(srcPath);
                                    }
                                }
                            });
                        }
                    }
                    return result;
                }

                @Override
                public void onSuccess(ArrayList<LocalMedia> result) {
                    PictureThreadUtils.cancel(this);
                    dispatchUriToFileTransformResult(result);
                }
            });
        }
    }

    /**
     * SDK > 29 把外部资源copy一份至应用沙盒内
     *
     * @param result
     */
    @Deprecated
    private void copyExternalPathToAppInDirFor29(ArrayList<LocalMedia> result) {
        showLoading();
        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<ArrayList<LocalMedia>>() {
            @Override
            public ArrayList<LocalMedia> doInBackground() {
                for (int i = 0; i < result.size(); i++) {
                    LocalMedia media = result.get(i);
                    selectorConfig.sandboxFileEngine.onStartSandboxFileTransform(getAppContext(), selectorConfig.isCheckOriginalImage, i,
                            media, new OnCallbackIndexListener<LocalMedia>() {
                                @Override
                                public void onCall(LocalMedia data, int index) {
                                    LocalMedia media = result.get(index);
                                    media.setSandboxPath(data.getSandboxPath());
                                    if (selectorConfig.isCheckOriginalImage) {
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
                dispatchUriToFileTransformResult(result);
            }
        });
    }


    /**
     * 构造原图数据
     *
     * @param result
     */
    private void mergeOriginalImage(ArrayList<LocalMedia> result) {
        if (selectorConfig.isCheckOriginalImage) {
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
        if (checkTransformSandboxFile()) {
            uriToFileTransform29(result);
        } else if (checkOldTransformSandboxFile()) {
            copyExternalPathToAppInDirFor29(result);
        } else {
            mergeOriginalImage(result);
            dispatchUriToFileTransformResult(result);
        }
    }


    /**
     * 返回结果
     */
    private void onCallBackResult(ArrayList<LocalMedia> result) {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            dismissLoading();
            if (selectorConfig.isActivityResultBack) {
                getActivity().setResult(RESULT_OK, PictureSelector.putIntentResult(result));
                onSelectFinish(RESULT_OK, result);
            } else {
                if (selectorConfig.onResultCallListener != null) {
                    selectorConfig.onResultCallListener.onResult(result);
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
        if (selectorConfig == null) {
            selectorConfig = SelectorProviders.getInstance().getSelectorConfig();
        }
        if (selectorConfig != null && selectorConfig.language != LanguageConfig.UNKNOWN_LANGUAGE) {
            PictureLanguageUtils.setAppLanguage(getActivity(), selectorConfig.language, selectorConfig.defaultLanguage);
        }
    }

    @Override
    public void onRecreateEngine() {
        createImageLoaderEngine();
        createVideoPlayerEngine();
        createCompressEngine();
        createSandboxFileEngine();
        createLoaderDataEngine();
        createResultCallbackListener();
        createLayoutResourceListener();
    }

    @Override
    public void onKeyBackFragmentFinish() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            if (selectorConfig.isActivityResultBack) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                onSelectFinish(Activity.RESULT_CANCELED, null);
            } else {
                if (selectorConfig.onResultCallListener != null) {
                    selectorConfig.onResultCallListener.onCancel();
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
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }
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
        this.context = context;
        if (getParentFragment() instanceof IBridgePictureBehavior) {
            iBridgePictureBehavior = (IBridgePictureBehavior) getParentFragment();
        } else if (context instanceof IBridgePictureBehavior) {
            iBridgePictureBehavior = (IBridgePictureBehavior) context;
        }
    }

    /**
     * setRequestedOrientation
     */
    protected void setRequestedOrientation() {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        getActivity().setRequestedOrientation(selectorConfig.requestedOrientation);
    }

    /**
     * back current Fragment
     */
    protected void onBackCurrentFragment() {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            if (!isStateSaved()) {
                if (selectorConfig.viewLifecycle != null) {
                    selectorConfig.viewLifecycle.onDestroy(this);
                }
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
                if (selectorConfig.viewLifecycle != null) {
                    selectorConfig.viewLifecycle.onDestroy(this);
                }
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
        SelectorProviders.getInstance().destroy();
    }

    /**
     * Get the image loading engine again, provided that the user implements the IApp interface in the Application
     */
    private void createImageLoaderEngine() {
        if (selectorConfig.imageEngine == null) {
            PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
            if (baseEngine != null) {
                selectorConfig.imageEngine = baseEngine.createImageLoaderEngine();
            }
        }
    }

    /**
     * Get the video player engine again, provided that the user implements the IApp interface in the Application
     */
    private void createVideoPlayerEngine(){
        if (selectorConfig.videoPlayerEngine == null) {
            PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
            if (baseEngine != null) {
                selectorConfig.videoPlayerEngine = baseEngine.createVideoPlayerEngine();
            }
        }
    }

    /**
     * Get the image loader data engine again, provided that the user implements the IApp interface in the Application
     */
    private void createLoaderDataEngine() {
        if (selectorConfig.isLoaderDataEngine) {
            if (selectorConfig.loaderDataEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    selectorConfig.loaderDataEngine = baseEngine.createLoaderDataEngine();
            }
        }

        if (selectorConfig.isLoaderFactoryEngine) {
            if (selectorConfig.loaderFactory == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    selectorConfig.loaderFactory = baseEngine.onCreateLoader();
            }
        }
    }

    /**
     * Get the image compress engine again, provided that the user implements the IApp interface in the Application
     */
    private void createCompressEngine() {
        if (selectorConfig.isCompressEngine) {
            if (selectorConfig.compressFileEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    selectorConfig.compressFileEngine = baseEngine.createCompressFileEngine();
            }
            if (selectorConfig.compressEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    selectorConfig.compressEngine = baseEngine.createCompressEngine();
            }
        }
    }


    /**
     * Get the Sandbox engine again, provided that the user implements the IApp interface in the Application
     */
    private void createSandboxFileEngine() {
        if (selectorConfig.isSandboxFileEngine) {
            if (selectorConfig.uriToFileTransformEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    selectorConfig.uriToFileTransformEngine = baseEngine.createUriToFileTransformEngine();
            }
            if (selectorConfig.sandboxFileEngine == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null)
                    selectorConfig.sandboxFileEngine = baseEngine.createSandboxFileEngine();
            }
        }
    }


    /**
     * Retrieve the result callback listener, provided that the user implements the IApp interface in the Application
     */
    private void createResultCallbackListener() {
        if (selectorConfig.isResultListenerBack) {
            if (selectorConfig.onResultCallListener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    selectorConfig.onResultCallListener = baseEngine.getResultCallbackListener();
                }
            }
        }
    }

    /**
     * Retrieve the layout callback listener, provided that the user implements the IApp interface in the Application
     */
    private void createLayoutResourceListener() {
        if (selectorConfig.isInjectLayoutResource) {
            if (selectorConfig.onLayoutResourceListener == null) {
                PictureSelectorEngine baseEngine = PictureAppMaster.getInstance().getPictureSelectorEngine();
                if (baseEngine != null) {
                    selectorConfig.onLayoutResourceListener = baseEngine.createLayoutResourceListener();
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
