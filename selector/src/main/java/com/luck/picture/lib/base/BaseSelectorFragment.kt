package com.luck.picture.lib.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.luck.picture.lib.R
import com.luck.picture.lib.SelectorCameraFragment
import com.luck.picture.lib.SelectorExternalPreviewFragment
import com.luck.picture.lib.SelectorMainFragment
import com.luck.picture.lib.SelectorPreviewFragment
import com.luck.picture.lib.SelectorSupporterActivity
import com.luck.picture.lib.SelectorSystemFragment
import com.luck.picture.lib.SelectorTransparentActivity
import com.luck.picture.lib.app.SelectorAppMaster
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.constant.CropWrap
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.dialog.PhotoItemSelectedDialog
import com.luck.picture.lib.dialog.PictureLoadingDialog
import com.luck.picture.lib.dialog.ReminderDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.ActivityCompatHelper
import com.luck.picture.lib.immersive.ImmersiveManager.translucentStatusBar
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.interfaces.OnRecordAudioListener
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.language.PictureLanguageUtils
import com.luck.picture.lib.media.ScanListener
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.permissions.PermissionUtil
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.provider.TempDataProvider
import com.luck.picture.lib.registry.ImageCaptureComponent
import com.luck.picture.lib.registry.SoundCaptureComponent
import com.luck.picture.lib.registry.VideoCaptureComponent
import com.luck.picture.lib.service.ForegroundService
import com.luck.picture.lib.utils.FileUtils
import com.luck.picture.lib.utils.MediaStoreUtils
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SpUtils
import com.luck.picture.lib.viewmodel.GlobalViewModel
import com.luck.picture.lib.viewmodel.SelectorViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：BaseSelectorFragment
 */
abstract class BaseSelectorFragment : Fragment() {

    abstract fun getFragmentTag(): String
    abstract fun getResourceId(): Int
    open fun isNormalDefaultEnter(): Boolean {
        return requireActivity() is SelectorSupporterActivity || requireActivity() is SelectorTransparentActivity
    }

    /**
     * Permission custom apply
     */
    open fun showCustomPermissionApply(permission: Array<String>) {}

    /**
     * Jump to the permission setting interface processing results
     */
    open fun handlePermissionSettingResult(permission: Array<String>) {}

    /**
     * Changes in selection results
     * @param change，If [change] is empty refresh all
     */
    open fun onSelectionResultChange(change: LocalMedia?) {}

    /**
     * Loading Dialog
     */
    private var mLoadingDialog: Dialog? = null

    /**
     * tipsDialog
     */
    private var tipsDialog: Dialog? = null

    protected val config = SelectorProviders.getInstance().getConfig()

    protected val factory = ClassFactory.NewInstance()

    protected var isSavedInstanceState = false

    protected val viewModel by lazy {
        val activity = requireActivity()
        val savedStateViewModelFactory = SavedStateViewModelFactory(activity.application, this)
        return@lazy ViewModelProvider(
            this,
            savedStateViewModelFactory
        )[SelectorViewModel::class.java]
    }

    protected val globalViewMode by lazy {
        val activity = requireActivity()
        val savedStateViewModelFactory = SavedStateViewModelFactory(activity.application, activity)
        return@lazy ViewModelProvider(
            activity,
            savedStateViewModelFactory
        )[GlobalViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getResourceId(), container, false);
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(
                requireContext(),
                config.windowAnimStyle.getEnterAnimRes()
            )
        } else {
            AnimationUtils.loadAnimation(
                requireContext(),
                config.windowAnimStyle.getExitAnimRes()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRequestedOrientation()
        setTranslucentStatusBar()
        isSavedInstanceState = savedInstanceState != null
        viewModel.onRestoreInstanceState(savedInstanceState)
        config.mListenerInfo.onFragmentLifecycleListener?.onViewCreated(
            this,
            view,
            savedInstanceState
        )
        restoreEngine()
        createLoadingDialog()
        setFragmentKeyBackListener()
    }

    fun getSelectResult(): MutableList<LocalMedia> {
        return TempDataProvider.getInstance().selectResult
    }

    open fun setRequestedOrientation() {
        requireActivity().requestedOrientation = config.activityOrientation
    }

    open fun restoreEngine() {
        if (config.imageEngine == null) {
            SelectorAppMaster.getInstance().getSelectorEngine()?.apply {
                config.imageEngine = this.createImageLoaderEngine()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        restoreEngine()
        viewModel.onSaveInstanceState()
    }

    /**
     * Permission Description
     */
    open fun showPermissionDescription(isDisplay: Boolean, permission: Array<String>) {
        val onPermissionDescriptionListener =
            config.mListenerInfo.onPermissionDescriptionListener
        if (onPermissionDescriptionListener != null) {
            if (PermissionChecker.checkSelfPermission(requireContext(), permission)) {
                onPermissionDescriptionListener.onDismiss(this)
            } else{
                if (isDisplay) {
                    val permissionStatus =
                        PermissionUtil.getPermissionStatus(requireActivity(), permission[0])
                    if (permissionStatus != PermissionUtil.REFUSE_PERMANENT) {
                        onPermissionDescriptionListener.onDescription(this,permission)
                    }
                } else {
                    onPermissionDescriptionListener.onDismiss(this)
                }
            }
        }
    }

    /**
     * Permission denied
     */
    open fun handlePermissionDenied(permission: Array<String>) {
        TempDataProvider.getInstance().currentRequestPermission = permission
        if (permission.isNotEmpty()) {
            SpUtils.putBoolean(requireContext(), permission[0], true)
        }
        val onPermissionDeniedListener = config.mListenerInfo.onPermissionDeniedListener
        if (onPermissionDeniedListener != null) {
            showPermissionDescription(false, permission)
            onPermissionDeniedListener.onDenied(
                this,
                permission,
                SelectorConstant.REQUEST_GO_SETTING,
                object : OnCallbackListener<Boolean> {
                    override fun onCall(data: Boolean) {
                        if (data) {
                            handlePermissionSettingResult(TempDataProvider.getInstance().currentRequestPermission)
                        }
                    }
                })
        } else {
            PermissionUtil.goIntentSetting(this, SelectorConstant.REQUEST_GO_SETTING)
        }
    }

    private fun setTranslucentStatusBar() {
        if (config.isPreviewFullScreenMode && !config.isOnlyCamera) {
            translucentStatusBar(
                requireActivity(),
                config.statusBarStyle.isDarkStatusBar()
            )
        }
    }

    open fun setFragmentKeyBackListener() {
        if (config.isNewKeyBackMode) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onKeyBackAction()
                }
            })
        } else {
            requireView().isFocusableInTouchMode = true
            requireView().requestFocus()
            requireView().setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    onKeyBackAction()
                    return@OnKeyListener true
                }
                false
            })
        }
    }

    open fun onKeyBackAction() {
        onBackPressed()
    }

    open fun onBackPressed() {
        config.mListenerInfo.onFragmentLifecycleListener?.onDestroy(this)
        if (!isStateSaved) {
            if (isRootExit()) {
                // Home Exit
                if (this is SelectorMainFragment || this is SelectorCameraFragment) {
                    config.mListenerInfo.onResultCallbackListener?.onCancel()
                }
                if (isNormalDefaultEnter()) {
                    requireActivity().finish()
                } else {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                SelectorProviders.getInstance().destroy()
            } else {
                // Pop the top state off the back stack. This function is asynchronous
                // it enqueues the request to pop, but the action will not be performed
                // until the application returns to its event loop.
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    open fun isRootExit(): Boolean {
        return this is SelectorMainFragment
                || (this is SelectorCameraFragment && config.isOnlyCamera)
                || (this is SelectorPreviewFragment && this is SelectorExternalPreviewFragment)
                || (this is SelectorSystemFragment && config.systemGallery)
    }

    /**
     * Confirm completion of selection
     */
    open fun onConfirmComplete() {
        requireActivity().runOnUiThread {
            if (!checkCompleteValidity()) {
                return@runOnUiThread
            }
            val selectResult = getSelectResult().toMutableList()
            viewModel.viewModelScope.launch {
                val mediaConverterEngine = config.mediaConverterEngine
                if (mediaConverterEngine != null) {
                    showLoading()
                    selectResult.forEach { media ->
                        mediaConverterEngine.converter(requireContext(), media)
                    }
                    dismissLoading()
                }

                if (config.isActivityResult) {
                    requireActivity().intent?.apply {
                        val result = arrayListOf<LocalMedia>()
                        result.addAll(selectResult)
                        this.putParcelableArrayListExtra(SelectorConstant.KEY_EXTRA_RESULT, result)
                        requireActivity().setResult(Activity.RESULT_OK, this)
                    }
                } else {
                    config.mListenerInfo.onResultCallbackListener?.onResult(selectResult)
                }
                if (!isStateSaved) {
                    if (isNormalDefaultEnter()) {
                        requireActivity().finish()
                    } else {
                        requireActivity().supportFragmentManager.fragments.forEach { _ ->
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
                SelectorProviders.getInstance().destroy()
            }
        }
    }

    /**
     * Verify legality before completion
     */
    open fun checkCompleteValidity(): Boolean {
        val selectResult = getSelectResult()
        if (config.mListenerInfo.onConfirmListener?.onConfirm(
                requireContext(),
                selectResult
            ) == true
        ) {
            return false
        }
        if (config.mediaType == MediaType.ALL) {
            var videoSize = 0
            var imageSize = 0
            selectResult.forEach {
                when {
                    MediaUtils.hasMimeTypeOfVideo(it.mimeType) -> {
                        videoSize++
                    }
                    MediaUtils.hasMimeTypeOfImage(it.mimeType) -> {
                        imageSize++
                    }
                }
            }
            if (config.minSelectNum > 0 && imageSize < config.minSelectNum) {
                showTipsDialog(
                    getString(
                        R.string.ps_min_img_num,
                        config.minSelectNum.toString()
                    )
                )
                return false
            }
            if (config.minVideoSelectNum > 0 && videoSize < config.minVideoSelectNum) {
                showTipsDialog(
                    getString(
                        R.string.ps_min_video_num,
                        config.minVideoSelectNum.toString()
                    )
                )
                return false
            }
        } else {
            if (config.minSelectNum > 0 && selectResult.size <= 0) {
                val msg = when (config.mediaType) {
                    MediaType.VIDEO -> {
                        getString(
                            R.string.ps_min_video_num,
                            config.minSelectNum.toString()
                        )
                    }
                    MediaType.AUDIO -> {
                        getString(
                            R.string.ps_min_audio_num,
                            config.minSelectNum.toString()
                        )
                    }
                    else -> {
                        getString(
                            R.string.ps_min_img_num,
                            config.minSelectNum.toString()
                        )
                    }
                }
                showTipsDialog(msg)
                return false
            }
        }
        return true
    }

    /**
     * Turn on the camera
     */
    open fun openSelectedCamera() {
        if (config.mediaType == MediaType.ALL) {
            if (config.allCameraMediaType == MediaType.ALL) {
                onSelectedOnlyCameraDialog()
            } else {
                startCameraAction(config.allCameraMediaType)
            }
        } else {
            startCameraAction(config.mediaType)
        }
    }

    /**
     * Activate camera intent based on [MediaType]
     */
    open fun startCameraAction(mode: MediaType) {
        if (mode == MediaType.AUDIO) {
            soundRecording()
        } else {
            val permission = arrayOf(Manifest.permission.CAMERA)
            if (PermissionChecker.checkSelfPermission(requireContext(), permission)) {
                if (mode == MediaType.VIDEO) {
                    recordVideo()
                } else {
                    takePictures()
                }
            } else {
                showPermissionDescription(true, permission)
                val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
                if (onPermissionApplyListener != null) {
                    showCustomPermissionApply(permission)
                } else {
                    PermissionChecker.requestPermissions(this, permission,
                        object : OnPermissionResultListener {
                            override fun onGranted() {
                                showPermissionDescription(false, permission)
                                if (mode == MediaType.VIDEO) {
                                    recordVideo()
                                } else {
                                    takePictures()
                                }
                            }

                            override fun onDenied() {
                                handlePermissionDenied(permission)
                            }
                        })
                }
            }
        }
    }

    /**
     * sound recording
     */
    open fun soundRecording() {
        val context = requireContext()
        val outputDir = config.audioOutputDir
        if (TextUtils.isEmpty(outputDir)) {
        } else {
            // Use custom storage path
            val defaultFileName = "${FileUtils.createFileName("AUD")}.amr"
            val applyFileNameListener = config.mListenerInfo.onReplaceFileNameListener
            val fileName = applyFileNameListener?.apply(defaultFileName) ?: defaultFileName
            viewModel.outputUri = Uri.fromFile(File(outputDir, fileName))
        }
        val soundCaptureComponent = config.registry.get(SoundCaptureComponent::class.java)
        if (soundCaptureComponent.isAssignableFrom(SoundCaptureComponent::class.java)) {
            val onRecordAudioListener = config.mListenerInfo.onRecordAudioListener
            if (onRecordAudioListener != null) {
                ForegroundService.startService(context, config.isForegroundService)
                onRecordAudioListener.onRecordAudio(this, SelectorConstant.REQUEST_CAMERA)
            } else {
                throw NullPointerException("Please implement the ${OnRecordAudioListener::class.java.simpleName} interface to achieve recording functionality")
            }
        } else {
            val soundCaptureActivity = factory.create(soundCaptureComponent)
            val intent = Intent(context, soundCaptureActivity::class.java)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.outputUri)
            startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
            ForegroundService.startService(context, config.isForegroundService)
        }
    }

    /**
     * System camera takes pictures
     */
    open fun takePictures() {
        val context = requireContext()
        val outputDir = config.imageOutputDir
        val defaultFileName = "${FileUtils.createFileName("IMG")}.jpg"
        val applyFileNameListener = config.mListenerInfo.onReplaceFileNameListener
        val fileName = applyFileNameListener?.apply(defaultFileName) ?: defaultFileName
        val outputUri: Uri?
        if (TextUtils.isEmpty(outputDir)) {
            // Use default storage path
            outputUri = MediaStoreUtils.insertImage(context, fileName)!!
            viewModel.outputUri = outputUri
        } else {
            // Use custom storage path
            val outputFile = File(outputDir, fileName)
            outputUri = MediaUtils.parUri(context, outputFile)
            viewModel.outputUri = Uri.fromFile(outputFile)
        }
        val customCameraListener = config.mListenerInfo.onCustomCameraListener
        if (customCameraListener != null) {
            customCameraListener.onCamera(
                this,
                MediaType.IMAGE,
                outputUri,
                SelectorConstant.REQUEST_CAMERA
            )
        } else {
            val imageCaptureComponent = config.registry.get(ImageCaptureComponent::class.java)
            if (imageCaptureComponent.isAssignableFrom(ImageCaptureComponent::class.java)) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(context.packageManager) != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                    startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
                    ForegroundService.startService(context, config.isForegroundService)
                }
            } else {
                val imageCaptureActivity = factory.create(imageCaptureComponent)
                val intent = Intent(context, imageCaptureActivity::class.java)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.outputUri)
                startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
                ForegroundService.startService(context, config.isForegroundService)
            }
        }
    }

    /**
     * System camera recording video
     */
    open fun recordVideo() {
        val context = requireContext()
        val outputDir = config.videoOutputDir
        val defaultFileName = "${FileUtils.createFileName("VID")}.mp4"
        val applyFileNameListener = config.mListenerInfo.onReplaceFileNameListener
        val fileName = applyFileNameListener?.apply(defaultFileName) ?: defaultFileName
        val outputUri: Uri?
        if (TextUtils.isEmpty(outputDir)) {
            // Use default storage path
            outputUri = MediaStoreUtils.insertVideo(context, fileName)!!
            viewModel.outputUri = outputUri
        } else {
            // Use custom storage path
            val outputFile = File(outputDir, fileName)
            outputUri = MediaUtils.parUri(context, outputFile)
            viewModel.outputUri = Uri.fromFile(outputFile)
        }
        val customCameraListener = config.mListenerInfo.onCustomCameraListener
        if (customCameraListener != null) {
            customCameraListener.onCamera(
                this,
                MediaType.VIDEO,
                outputUri,
                SelectorConstant.REQUEST_CAMERA
            )
        } else {
            val videoCaptureComponent = config.registry.get(VideoCaptureComponent::class.java)
            if (videoCaptureComponent.isAssignableFrom(VideoCaptureComponent::class.java)) {
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                if (intent.resolveActivity(context.packageManager) != null) {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                    intent.putExtra(SelectorConstant.QUICK_CAPTURE, config.isQuickCapture)
                    startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
                    ForegroundService.startService(context, config.isForegroundService)
                }
            } else {
                val videoCaptureActivity = factory.create(videoCaptureComponent)
                val intent = Intent(context, videoCaptureActivity::class.java)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.outputUri)
                startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
                ForegroundService.startService(context, config.isForegroundService)
            }
        }
    }

    /**
     * [MediaType.ALL] mode, select one option for taking photos and recording videos, pop up the box
     */
    open fun onSelectedOnlyCameraDialog() {
        val selectedDialog = PhotoItemSelectedDialog.newInstance()
        selectedDialog.setOnItemClickListener(object : OnItemClickListener<View> {
            override fun onItemClick(position: Int, data: View) {
                when (position) {
                    PhotoItemSelectedDialog.IMAGE_CAMERA -> {
                        startCameraAction(MediaType.IMAGE)
                    }

                    PhotoItemSelectedDialog.VIDEO_CAMERA -> {
                        startCameraAction(MediaType.VIDEO)
                    }
                }
            }
        })
        selectedDialog.setOnDismissListener { isCancel, _ ->
            if (config.isOnlyCamera && isCancel) {
                onBackPressed()
            }
        }
        selectedDialog.show(childFragmentManager, "PhotoItemSelectedDialog")
    }

    /**
     * Confirm media selection
     * @param media The media object for the current operation
     * @param isSelected Select Status
     */
    open fun confirmSelect(media: LocalMedia, isSelected: Boolean): Int {
        if (!isSelected) {
            if (config.selectionMode == SelectionMode.MULTIPLE) {
                if (onCheckSelectValidity(media, isSelected) != SelectedState.SUCCESS) {
                    return SelectedState.INVALID
                }
            }
            if (config.mListenerInfo.onSelectFilterListener?.onSelectFilter(
                    requireContext(),
                    media
                ) == true
            ) {
                return SelectedState.INVALID
            }
        }
        return if (isSelected) {
            if (getSelectResult().contains(media)) {
                getSelectResult().remove(media)
                globalViewMode.setSelectResultLiveData(media)
            }
            SelectedState.REMOVE
        } else {
            if (config.selectionMode == SelectionMode.SINGLE) {
                if (getSelectResult().isNotEmpty()) {
                    globalViewMode.setSelectResultLiveData(getSelectResult().first())
                    getSelectResult().clear()
                }
            }
            if (!getSelectResult().contains(media)) {
                getSelectResult().add(media)
                globalViewMode.setSelectResultLiveData(media)
            }
            SelectedState.SUCCESS
        }
    }

    /**
     * Verify the legitimacy of selecting media
     * @param media The media object for the current operation
     * @param isSelected Select Status
     */
    open fun onCheckSelectValidity(media: LocalMedia, isSelected: Boolean): Int {
        val count = getSelectResult().size
        when (config.mediaType) {
            MediaType.ALL -> {
                if (config.isAllWithImageVideo) {
                    // Support for selecting images and videos
                    var videoSize = 0
                    var imageSize = 0
                    getSelectResult().forEach {
                        if (MediaUtils.hasMimeTypeOfVideo(it.mimeType)) {
                            videoSize++
                        } else if (MediaUtils.hasMimeTypeOfImage(it.mimeType)) {
                            imageSize++
                        }
                    }

                    if (config.isAsTotalCount) {
                        // The number of maxVideoSelectNum in select all mode is included within maxSelectNum
                        if (count >= config.totalCount) {
                            showTipsDialog(
                                getString(
                                    R.string.ps_message_max_num,
                                    config.totalCount.toString()
                                )
                            )
                            return SelectedState.INVALID
                        }
                        if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                            // If the selected video exceeds the [config.maxVideoSelectNum] limit
                            if (videoSize >= config.maxVideoSelectNum) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_video_max_num,
                                        config.maxVideoSelectNum.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        }
                    } else {
                        if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                            // If the selected video exceeds the [config.maxVideoSelectNum] limit
                            if (videoSize >= config.maxVideoSelectNum) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_video_max_num,
                                        config.maxVideoSelectNum.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        } else if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                            // If the selected image exceeds the [config.maxSelectNum] limit
                            if (imageSize >= config.totalCount) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_max_num,
                                        config.totalCount.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        }
                    }
                } else {
                    // Only supports selecting images
                    if (getSelectResult().isNotEmpty()) {
                        val first = getSelectResult().first()
                        if (MediaUtils.hasMimeTypeOfImage(first.mimeType)) {
                            // Image has been selected
                            if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                                showTipsDialog(getString(R.string.ps_rule))
                                return SelectedState.INVALID
                            }
                            if (count >= config.totalCount) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_max_num,
                                        config.totalCount.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        } else if (MediaUtils.hasMimeTypeOfVideo(first.mimeType)) {
                            // Video has been selected
                            if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                                showTipsDialog(getString(R.string.ps_rule))
                                return SelectedState.INVALID
                            }
                            if (count >= config.totalCount) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_video_max_num,
                                        config.totalCount.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        }
                    }
                }
            }
            MediaType.IMAGE -> {
                if (count >= config.totalCount) {
                    showTipsDialog(
                        getString(
                            R.string.ps_message_max_num, config.totalCount.toString()
                        )
                    )
                    return SelectedState.INVALID
                }
            }
            MediaType.VIDEO -> {
                if (count >= config.totalCount) {
                    showTipsDialog(
                        getString(
                            R.string.ps_message_video_max_num,
                            config.totalCount.toString()
                        )
                    )
                    return SelectedState.INVALID
                }
            }
            MediaType.AUDIO -> {
                if (count >= config.totalCount) {
                    showTipsDialog(
                        getString(
                            R.string.ps_message_audio_max_num,
                            config.totalCount.toString()
                        )
                    )
                    return SelectedState.INVALID
                }
            }
        }
        return SelectedState.SUCCESS
    }

    private fun showTipsDialog(tips: String) {
        try {
            if (ActivityCompatHelper.isDestroy(activity)) {
                return
            }
            if (tipsDialog != null && tipsDialog?.isShowing == true) {
                return
            }
            tipsDialog = ReminderDialog.buildDialog(requireContext(), tips)
            tipsDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Process the selection results based on user API settings
     */
    open fun handleSelectResult() {
        val cropEngine = config.cropEngine
        if (cropEngine != null && isCrop()) {
            cropEngine.onCrop(
                this,
                getSelectResult(),
                SelectorConstant.REQUEST_CROP
            )
        } else {
            onConfirmComplete()
        }
    }

    /**
     * Media types that support cropping
     */
    open fun isCrop(): Boolean {
        getSelectResult().forEach continuing@{ media ->
            if (config.skipCropFormat.contains(media.mimeType)) {
                return@continuing
            }
            if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                return true
            }
        }
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initAppLanguage()
    }

    override fun onAttach(context: Context) {
        initAppLanguage()
        super.onAttach(context)
    }

    open fun initAppLanguage() {
        if (config.language != Language.UNKNOWN_LANGUAGE) {
            PictureLanguageUtils.setAppLanguage(
                requireContext(),
                config.language,
                config.defaultLanguage
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mPermissionResultListener != null) {
            PermissionChecker.onRequestPermissionsResult(
                requireActivity(),
                grantResults,
                permissions,
                mPermissionResultListener
            )
            mPermissionResultListener = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val context = requireContext()
        ForegroundService.stopService(context)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectorConstant.REQUEST_CAMERA) {
                val schemeFile = viewModel.outputUri?.scheme.equals("file")
                val outputUri = if (schemeFile) {
                    viewModel.outputUri
                } else {
                    data?.getParcelableExtra(MediaStore.EXTRA_OUTPUT) ?: data?.data
                    ?: viewModel.outputUri
                }
                if (outputUri != null) {
                    if (config.mediaType == MediaType.AUDIO && schemeFile && data?.data != null) {
                        copyAudioUriToFile(data.data!!)
                    } else {
                        analysisCameraData(outputUri)
                    }
                } else {
                    throw IllegalStateException("Camera output uri is empty")
                }
            } else if (requestCode == SelectorConstant.REQUEST_CROP) {
                val selectResult = getSelectResult()
                if (selectResult.isNotEmpty()) {
                    if (selectResult.size == 1) {
                        mergeSingleCrop(data, selectResult)
                    } else {
                        mergeMultipleCrop(data, selectResult)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            onResultCanceled(requestCode, resultCode)
        }
    }

    /**
     * copy audio to output file
     */
    open fun copyAudioUriToFile(data: Uri) {
        requireContext().contentResolver.openInputStream(data)?.use { inputStream ->
            viewModel.outputUri?.let { outputUri ->
                if (FileUtils.writeFileFromIS(inputStream, FileOutputStream(outputUri.path))) {
                    analysisCameraData(outputUri)
                    MediaUtils.deleteUri(requireContext(), data)
                }
                FileUtils.close(inputStream)
            }
        }
    }

    /**
     * Analyzing Camera Generated Data
     */
    open fun analysisCameraData(uri: Uri) {
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
                    onMergeCameraResult(media)
                }
            }
        })
    }

    /**
     * Merge Camera Output Results
     */
    open fun onMergeCameraResult(media: LocalMedia?) {

    }

    /**
     * Activity Result Canceled
     */
    open fun onResultCanceled(requestCode: Int, resultCode: Int) {
        if (requestCode == SelectorConstant.REQUEST_GO_SETTING) {
            handlePermissionSettingResult(TempDataProvider.getInstance().currentRequestPermission)
        }
    }

    /**
     * Merge Cropping single images data
     */
    open fun mergeSingleCrop(data: Intent?, selectResult: MutableList<LocalMedia>) {
        val media = selectResult.first()
        val outputUri = if (data?.hasExtra(CropWrap.CROP_OUTPUT_URI) == true) {
            data.getParcelableExtra<Uri>(CropWrap.CROP_OUTPUT_URI)
        } else {
            data?.getParcelableExtra(MediaStore.EXTRA_OUTPUT)
        }
        media.cropWidth = data?.getIntExtra(CropWrap.CROP_IMAGE_WIDTH, 0) ?: 0
        media.cropHeight = data?.getIntExtra(CropWrap.CROP_IMAGE_HEIGHT, 0) ?: 0
        media.cropOffsetX = data?.getIntExtra(CropWrap.CROP_OFFSET_X, 0) ?: 0
        media.cropOffsetY = data?.getIntExtra(CropWrap.CROP_OFFSET_Y, 0) ?: 0
        media.cropAspectRatio = data?.getFloatExtra(CropWrap.CROP_ASPECT_RATIO, 0F) ?: 0F
        media.cropPath = if (MediaUtils.isContent(outputUri.toString())) {
            outputUri.toString()
        } else {
            outputUri?.path
        }
        onConfirmComplete()
    }

    /**
     * Merge Cropping multiple images data
     */
    open fun mergeMultipleCrop(data: Intent?, selectResult: MutableList<LocalMedia>) {
        val json = data?.getStringExtra(MediaStore.EXTRA_OUTPUT)
        if (json == null || TextUtils.isEmpty(json)) {
            return
        }
        val array = JSONArray(json)
        if (array.length() == selectResult.size) {
            selectResult.forEachIndexed { i, media ->
                val item = array.optJSONObject(i)
                media.cropPath = item.optString(CropWrap.DEFAULT_CROP_OUTPUT_PATH)
                media.cropWidth = item.optInt(CropWrap.DEFAULT_CROP_IMAGE_WIDTH)
                media.cropHeight = item.optInt(CropWrap.DEFAULT_CROP_IMAGE_HEIGHT)
                media.cropOffsetX = item.optInt(CropWrap.DEFAULT_CROP_OFFSET_X)
                media.cropOffsetY = item.optInt(CropWrap.DEFAULT_CROP_OFFSET_Y)
                media.cropAspectRatio = item.optDouble(CropWrap.DEFAULT_CROP_ASPECT_RATIO).toFloat()
            }
            onConfirmComplete()
        } else {
            throw IllegalStateException("Multiple image cropping results do not match selection results:::${array.length()}!=${selectResult.size}")
        }
    }

    /**
     * Permission Listener
     */
    private var mPermissionResultListener: OnPermissionResultListener? = null

    /**
     * Set Permission Listener
     */
    open fun setOnPermissionResultListener(listener: OnPermissionResultListener?) {
        this.mPermissionResultListener = listener
    }

    /**
     * Loading dialog
     */
    open fun createLoadingDialog() {
        mLoadingDialog =
            config.mListenerInfo.onCustomLoadingListener?.create(requireContext())
                ?: PictureLoadingDialog(requireContext())
    }

    fun showLoading() {
        try {
            if (!ActivityCompatHelper.isDestroy(activity)) {
                mLoadingDialog?.let { dialog ->
                    if (!dialog.isShowing) {
                        dialog.show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissLoading() {
        try {
            if (!ActivityCompatHelper.isDestroy(activity)) {
                mLoadingDialog?.let { dialog ->
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}