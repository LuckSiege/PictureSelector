package com.luck.picture.lib.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.luck.picture.lib.base.BaseSelectorFragment
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.helper.ActivityCompatHelper
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.SdkVersionUtils.isTIRAMISU
import com.luck.picture.lib.utils.SpUtils.putBoolean

/**
 * @author：luck
 * @date：2021/11/18 10:07 上午
 * @describe：PermissionChecker
 */
object PermissionChecker {
    private const val REQUEST_CODE = 10086
    const val CAMERA = Manifest.permission.CAMERA
    private const val READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO"
    private const val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
    private const val READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO"
    private const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    private const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    fun requestPermissions(
        fragment: Fragment,
        permissionArray: Array<String>,
        listenerOn: OnPermissionResultListener?
    ) {
        val groupList: MutableList<Array<String>> = ArrayList()
        groupList.add(permissionArray)
        requestPermissions(fragment, groupList, listenerOn)
    }

    private fun requestPermissions(
        fragment: Fragment,
        permissionGroupList: List<Array<String>>,
        onPermissionResultListener: OnPermissionResultListener?
    ) {
        if (ActivityCompatHelper.isDestroy(fragment.activity)) {
            return
        }
        if (fragment is BaseSelectorFragment) {
            if (Build.VERSION.SDK_INT < 23) {
                onPermissionResultListener?.onGranted()
                return
            }
            val activity: Activity = fragment.requireActivity()
            val permissionList: MutableList<String> = mutableListOf()
            for (permissionArray in permissionGroupList) {
                for (permission in permissionArray) {
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionList.add(permission)
                    }
                }
            }
            if (permissionList.size > 0) {
                fragment.setOnPermissionResultListener(onPermissionResultListener)
                val requestArray = permissionList.toTypedArray()
                fragment.requestPermissions(requestArray, REQUEST_CODE)
                ActivityCompat.requestPermissions(activity, requestArray, REQUEST_CODE)
            } else {
                onPermissionResultListener?.onGranted()
            }
        }
    }

    fun onRequestPermissionsResult(activity: Activity,grantResults: IntArray?, permissions: Array<out String>,action: OnPermissionResultListener?) {
        for (permission in permissions) {
            val should = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            putBoolean(activity, permission, should)
        }
        if (PermissionUtil.isAllGranted(grantResults)) {
            action?.onGranted()
        } else {
            action?.onDenied()
        }
    }

    fun isCheckReadStorage(context: Context, mediaType: MediaType): Boolean {
        return if (isTIRAMISU()) {
            when (mediaType) {
                MediaType.IMAGE -> {
                    isCheckReadImages(context)
                }
                MediaType.VIDEO -> {
                    isCheckReadVideo(context)
                }
                MediaType.AUDIO -> {
                    isCheckReadAudio(context)
                }
                else -> {
                    isCheckReadImages(context) && isCheckReadVideo(
                        context
                    )
                }
            }
        } else {
            isCheckReadExternalStorage(context)
        }
    }

    /**
     * 检查读取图片权限是否存在
     */
    @RequiresApi(api = 33)
    fun isCheckReadImages(context: Context): Boolean {
        return checkSelfPermission(
            context,
            arrayOf(READ_MEDIA_IMAGES)
        )
    }

    /**
     * 检查读取视频权限是否存在
     */
    @RequiresApi(api = 33)
    fun isCheckReadVideo(context: Context): Boolean {
        return checkSelfPermission(
            context,
            arrayOf(READ_MEDIA_VIDEO)
        )
    }

    /**
     * 检查读取音频权限是否存在
     */
    @RequiresApi(api = 33)
    fun isCheckReadAudio(context: Context): Boolean {
        return checkSelfPermission(
            context,
            arrayOf(READ_MEDIA_AUDIO)
        )
    }


    private fun isCheckReadExternalStorage(context: Context): Boolean {
        return checkSelfPermission(
            context,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    }

    fun checkSelfPermission(ctx: Context, permissions: Array<String>): Boolean {
        var isAllGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    ctx.applicationContext,
                    permission
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                isAllGranted = false
                break
            }
        }
        return isAllGranted
    }


    fun getReadPermissionArray(context: Context, mediaType: MediaType): Array<String> {
        if (isTIRAMISU()) {
            val targetSdkVersion = context.applicationInfo.targetSdkVersion
            return if (mediaType == MediaType.IMAGE) {
                if (targetSdkVersion >= SdkVersionUtils.TIRAMISU) arrayOf(READ_MEDIA_IMAGES) else arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_EXTERNAL_STORAGE
                )
            } else if (mediaType == MediaType.VIDEO) {
                if (targetSdkVersion >= SdkVersionUtils.TIRAMISU) arrayOf(READ_MEDIA_VIDEO) else arrayOf(
                    READ_MEDIA_VIDEO,
                    READ_EXTERNAL_STORAGE
                )
            } else if (mediaType == MediaType.AUDIO) {
                if (targetSdkVersion >= SdkVersionUtils.TIRAMISU) arrayOf(READ_MEDIA_AUDIO) else arrayOf(
                    READ_MEDIA_AUDIO,
                    READ_EXTERNAL_STORAGE
                )
            } else {
                if (targetSdkVersion >= SdkVersionUtils.TIRAMISU) arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO
                ) else arrayOf(
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_EXTERNAL_STORAGE
                )
            }
        }
        return arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
    }
}