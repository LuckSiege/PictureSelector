package com.luck.picture.lib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnRequestPermissionListener;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.ToastUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2022/1/16 10:22 下午
 * @describe：PictureSelectorSystemFragment
 */
public class PictureSelectorSystemFragment extends PictureCommonFragment {
    public static final String TAG = PictureSelectorSystemFragment.class.getSimpleName();

    public static PictureSelectorSystemFragment newInstance() {
        return new PictureSelectorSystemFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }


    private ActivityResultLauncher<String[]> mDocMultipleLauncher;

    private ActivityResultLauncher<String[]> mDocSingleLauncher;

    private ActivityResultLauncher<String> mContentsLauncher;

    private ActivityResultLauncher<String> mContentLauncher;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createSystemContracts();
        if (PermissionChecker.isCheckReadStorage(getContext())) {
            openSystemAlbum();
        } else {
            if (PictureSelectionConfig.onPermissionsEventListener != null) {
                PictureSelectionConfig.onPermissionsEventListener.requestPermission(this,
                        PermissionConfig.READ_WRITE_EXTERNAL_STORAGE, new OnRequestPermissionListener() {
                            @Override
                            public void onCall(String[] permissionArray, boolean isResult) {
                                if (isResult) {
                                    openSystemAlbum();
                                } else {
                                    handlePermissionDenied(permissionArray);
                                }
                            }
                        });
            } else {
                PermissionChecker.getInstance().requestPermissions(this,
                        PermissionConfig.READ_WRITE_EXTERNAL_STORAGE, new PermissionResultCallback() {
                            @Override
                            public void onGranted() {
                                openSystemAlbum();
                            }

                            @Override
                            public void onDenied() {
                                handlePermissionDenied(PermissionConfig.READ_WRITE_EXTERNAL_STORAGE);
                            }
                        });
            }
        }
    }

    /**
     * 打开系统相册
     */
    private void openSystemAlbum() {
        if (config.selectionMode == SelectModeConfig.SINGLE) {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                mDocSingleLauncher.launch(SelectMimeType.SYSTEM_ALL);
            } else {
                mContentLauncher.launch(getInput());
            }
        } else {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                mDocMultipleLauncher.launch(SelectMimeType.SYSTEM_ALL);
            } else {
                mContentsLauncher.launch(getInput());
            }
        }
    }

    /**
     * createSystemContracts
     */
    private void createSystemContracts() {
        if (config.selectionMode == SelectModeConfig.SINGLE) {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                createSingleDocuments();
            } else {
                createContent();
            }
        } else {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                createMultipleDocuments();
            } else {
                createMultipleContents();
            }
        }
    }

    /**
     * 同时获取图片或视频(多选)
     */
    private void createMultipleDocuments() {
        mDocMultipleLauncher = registerForActivityResult
                (new ActivityResultContracts.OpenMultipleDocuments(),
                        new ActivityResultCallback<List<Uri>>() {
                            @Override
                            public void onActivityResult(List<Uri> result) {
                                if (result == null || result.size() == 0) {
                                    onKeyBackFragmentFinish();
                                } else {
                                    for (int i = 0; i < result.size(); i++) {
                                        LocalMedia media = buildLocalMedia(result.get(i).toString());
                                        media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                                        SelectedManager.addSelectResult(media);
                                    }
                                    dispatchTransformResult();
                                }
                            }
                        });

    }


    /**
     * 同时获取图片或视频(单选)
     */
    private void createSingleDocuments() {
        mDocSingleLauncher = registerForActivityResult
                (new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result == null) {
                            onKeyBackFragmentFinish();
                        } else {
                            LocalMedia media = buildLocalMedia(result.toString());
                            media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                            int selectResultCode = confirmSelect(media, false);
                            if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                                dispatchTransformResult();
                            } else {
                                onKeyBackFragmentFinish();
                            }
                        }
                    }
                });
    }


    /**
     * 获取图片或视频
     */
    private void createMultipleContents() {
        mContentsLauncher = registerForActivityResult
                (new ActivityResultContracts.GetMultipleContents(),
                        new ActivityResultCallback<List<Uri>>() {
                            @Override
                            public void onActivityResult(List<Uri> result) {
                                if (result == null || result.size() == 0) {
                                    onKeyBackFragmentFinish();
                                } else {
                                    for (int i = 0; i < result.size(); i++) {
                                        LocalMedia media = buildLocalMedia(result.get(i).toString());
                                        media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                                        SelectedManager.addSelectResult(media);
                                    }
                                    dispatchTransformResult();
                                }
                            }
                        });

    }

    /**
     * 单选图片或视频
     */
    private void createContent() {
        mContentLauncher = registerForActivityResult
                (new ActivityResultContracts.GetContent(),
                        new ActivityResultCallback<Uri>() {
                            @Override
                            public void onActivityResult(Uri result) {
                                if (result == null) {
                                    onKeyBackFragmentFinish();
                                } else {
                                    LocalMedia media = buildLocalMedia(result.toString());
                                    media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                                    int selectResultCode = confirmSelect(media, false);
                                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                                        dispatchTransformResult();
                                    } else {
                                        onKeyBackFragmentFinish();
                                    }
                                }
                            }

                        });
    }

    /**
     * 获取选资源取类型
     *
     * @return
     */
    private String getInput() {
        if (config.chooseMode == SelectMimeType.ofVideo()) {
            return SelectMimeType.SYSTEM_VIDEO;
        } else if (config.chooseMode == SelectMimeType.ofAudio()) {
            return SelectMimeType.SYSTEM_AUDIO;
        } else {
            return SelectMimeType.SYSTEM_IMAGE;
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        boolean isHasPermissions;
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            isHasPermissions = PictureSelectionConfig.onPermissionsEventListener
                    .hasPermissions(this, permissions);
        } else {
            isHasPermissions = PermissionChecker.isCheckReadStorage(getContext());
        }
        if (isHasPermissions) {
            openSystemAlbum();
        } else {
            ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction));
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDocMultipleLauncher != null) {
            mDocMultipleLauncher.unregister();
        }
        if (mDocSingleLauncher != null) {
            mDocSingleLauncher.unregister();
        }
        if (mContentsLauncher != null) {
            mContentsLauncher.unregister();
        }
        if (mContentLauncher != null) {
            mContentLauncher.unregister();
        }
    }
}
