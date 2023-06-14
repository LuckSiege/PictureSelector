package com.luck.picture.lib

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.viewModelScope
import com.luck.picture.lib.base.BaseSelectorFragment
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.media.ScanListener
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils.isQ
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.launch

/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：SelectorCameraFragment
 */
open class SelectorCameraFragment : BaseSelectorFragment() {

    override fun getFragmentTag(): String {
        return SelectorCameraFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return R.layout.ps_empty
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            if (isQ()) {
                openSelectedCamera()
            } else {
                PermissionChecker.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            openSelectedCamera()
                        }

                        override fun onDenied() {
                            handlePermissionDenied(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        }
                    })
            }
        }
    }

    override fun onResultCanceled(requestCode: Int, resultCode: Int) {
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == SelectorConstant.REQUEST_GO_SETTING) {
                handlePermissionSettingResult(viewModel.currentRequestPermission)
            } else {
                onBackPressed()
            }
        }
    }

    override fun analysisCameraData(uri: Uri) {
        val context = requireContext()
        val isContent = uri.scheme.equals("content")
        val realPath = if (isContent) {
            MediaUtils.getPath(context, uri)
        } else {
            uri.path
        }
        if (TextUtils.isEmpty(realPath)) {
            return
        }
        viewModel.scanFile(if (isContent) realPath else null, object : ScanListener {
            override fun onScanFinish() {
                viewModel.viewModelScope.launch {
                    val media = if (isContent) {
                        MediaUtils.getAssignPathMedia(context, realPath!!)
                    } else {
                        MediaUtils.getAssignFileMedia(context, realPath!!)
                    }
                    if (confirmSelect(media, false) == SelectedState.SUCCESS) {
                        handleSelectResult()
                    } else {
                        onBackPressed()
                    }
                }
            }
        })
    }


    override fun handlePermissionSettingResult(permission: Array<String>) {
        if (permission.isEmpty()) {
            return
        }
        val context = requireContext()
        showPermissionDescription(false, permission)
        var isHasPermissions: Boolean
        val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
        if (onPermissionApplyListener != null) {
            isHasPermissions = onPermissionApplyListener.hasPermissions(this, permission)
        } else {
            isHasPermissions =
                PermissionChecker.checkSelfPermission(context, arrayOf(Manifest.permission.CAMERA))
            if (isQ()) {
            } else {
                isHasPermissions = PermissionChecker.checkSelfPermission(
                    context,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
            }
        }
        if (isHasPermissions) {
            openSelectedCamera()
        } else {
            if (!PermissionChecker.checkSelfPermission(
                    context,
                    arrayOf(Manifest.permission.CAMERA)
                )
            ) {
                ToastUtils.showMsg(context, getString(R.string.ps_camera))
            } else {
                if (!PermissionChecker.checkSelfPermission(
                        context,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                ) {
                    ToastUtils.showMsg(requireContext(), getString(R.string.ps_jurisdiction))
                }
            }
            onBackPressed()
        }
        viewModel.currentRequestPermission = arrayOf()
    }
}