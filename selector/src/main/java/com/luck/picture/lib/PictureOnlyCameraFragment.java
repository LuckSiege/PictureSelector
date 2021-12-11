package com.luck.picture.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.utils.ActivityCompatHelper;

/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：PictureOnlyCameraFragment
 */
public class PictureOnlyCameraFragment extends PictureCommonFragment {
    public static final String TAG = PictureOnlyCameraFragment.class.getSimpleName();

    public static PictureOnlyCameraFragment newInstance() {
        return new PictureOnlyCameraFragment();
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        openSelectedCamera();
    }

    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        SelectedManager.getSelectedResult().add(media);
        dispatchTransformResult();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                if (!ActivityCompatHelper.isDestroy(getActivity())) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        }
    }

    @Override
    public void handlePermissionSettingResult() {
        if (PermissionChecker.isCheckCamera(getContext())) {
            openSelectedCamera();
        } else {
            Toast.makeText(getContext(), getString(R.string.ps_camera), Toast.LENGTH_LONG).show();
            if (!ActivityCompatHelper.isDestroy(getActivity())) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}
