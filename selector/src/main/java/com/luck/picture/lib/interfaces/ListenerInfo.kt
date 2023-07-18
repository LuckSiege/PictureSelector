package com.luck.picture.lib.interfaces


/**
 * @author：luck
 * @date：2022/8/12 7:19 下午
 * @describe：Listeners
 */
class ListenerInfo {
    var onConfirmListener: OnConfirmListener? = null
    var onEditorMediaListener: OnEditorMediaListener? = null
    var onQueryFilterListener: OnQueryFilterListener? = null
    var onRecordAudioListener: OnRecordAudioListener? = null
    var onCustomCameraListener: OnCustomCameraListener? = null
    var onSelectFilterListener: OnSelectFilterListener? = null
    var onReplaceFileNameListener: OnReplaceFileNameListener? = null
    var onCustomLoadingListener: OnCustomLoadingListener? = null
    var onResultCallbackListener: OnResultCallbackListener? = null
    var onCustomAnimationListener: OnCustomAnimationListener? = null
    var onExternalPreviewListener: OnExternalPreviewListener? = null
    var onPermissionDeniedListener: OnPermissionDeniedListener? = null
    var onFragmentLifecycleListener: OnFragmentLifecycleListener? = null
    var onPermissionApplyListener: OnPermissionApplyListener? = null
    var onAnimationAdapterWrapListener: OnAnimationAdapterWrapListener? = null
    var onPermissionDescriptionListener: OnPermissionDescriptionListener? = null

    fun destroy() {
        this.onEditorMediaListener = null
        this.onRecordAudioListener = null
        this.onQueryFilterListener = null
        this.onSelectFilterListener = null
        this.onCustomCameraListener = null
        this.onCustomLoadingListener = null
        this.onReplaceFileNameListener = null
        this.onResultCallbackListener = null
        this.onExternalPreviewListener = null
        this.onCustomAnimationListener = null
        this.onPermissionApplyListener = null
        this.onPermissionDeniedListener = null
        this.onFragmentLifecycleListener = null
        this.onAnimationAdapterWrapListener = null
        this.onPermissionDescriptionListener = null
    }
}