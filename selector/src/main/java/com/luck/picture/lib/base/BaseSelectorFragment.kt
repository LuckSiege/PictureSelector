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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.luck.picture.lib.*
import com.luck.picture.lib.config.CameraType
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.constant.CropWrap
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.dialog.PhotoItemSelectedDialog
import com.luck.picture.lib.dialog.PictureLoadingDialog
import com.luck.picture.lib.dialog.ReminderDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.helper.ActivityCompatHelper
import com.luck.picture.lib.immersive.ImmersiveManager.translucentStatusBar
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.interfaces.OnRecordAudioListener
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.language.PictureLanguageUtils
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.permissions.PermissionUtil
import com.luck.picture.lib.provider.SelectorProviders
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
import java.io.Serializable

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

    protected val viewModel by viewModels<SelectorViewModel>()

    protected val globalViewMode by activityViewModels<GlobalViewModel>()

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
                viewModel.selectorStyle.getWindowAnimation().getEnterAnim()
            )
        } else {
            AnimationUtils.loadAnimation(
                requireContext(),
                viewModel.selectorStyle.getWindowAnimation().getExitAnim()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.config.mListenerInfo.onFragmentLifecycleListener?.onViewCreated(
            this,
            view,
            savedInstanceState
        )
        createLoadingDialog()
        setFragmentKeyBackListener()
        setTranslucentStatusBar()
    }

    /**
     * Permission Description
     */
    open fun showPermissionDescription(isDisplay: Boolean, permission: Array<String>) {
        val onPermissionDescriptionListener =
            viewModel.config.mListenerInfo.onPermissionDescriptionListener
        if (onPermissionDescriptionListener != null) {
            if (isDisplay) {
                if (PermissionChecker.checkSelfPermission(requireContext(), permission)) {
                    SpUtils.putBoolean(requireContext(), permission[0], false)
                } else {
                    if (!SpUtils.getBoolean(requireContext(), permission[0], false)) {
                        onPermissionDescriptionListener.onDescription(this, permission)
                    }
                }
            } else {
                onPermissionDescriptionListener.onDismiss(this)
            }
        }
    }

    /**
     * Permission denied
     */
    open fun handlePermissionDenied(permission: Array<String>) {
        viewModel.currentRequestPermission = permission
        if (permission.isNotEmpty()) {
            SpUtils.putBoolean(requireContext(), permission[0], true)
        }
        val onPermissionDeniedListener = viewModel.config.mListenerInfo.onPermissionDeniedListener
        if (onPermissionDeniedListener != null) {
            showPermissionDescription(false, permission)
            onPermissionDeniedListener.onDenied(
                this,
                permission,
                SelectorConstant.REQUEST_GO_SETTING,
                object : OnCallbackListener<Boolean> {
                    override fun onCall(data: Boolean) {
                        if (data) {
                            handlePermissionSettingResult(viewModel.currentRequestPermission)
                        }
                    }
                })
        } else {
            PermissionUtil.goIntentSetting(this, SelectorConstant.REQUEST_GO_SETTING)
        }
    }

    private fun setTranslucentStatusBar() {
        if (viewModel.config.isPreviewFullScreenMode && !viewModel.config.isOnlyCamera) {
            val statusBar = viewModel.selectorStyle.getStatusBar()
            translucentStatusBar(requireActivity(), statusBar.isDarkStatusBar())
        }
    }

    private fun setFragmentKeyBackListener() {
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

    open fun onKeyBackAction() {
        onBackPressed()
    }

    open fun onBackPressed() {
        viewModel.config.mListenerInfo.onFragmentLifecycleListener?.onDestroy(this)
        if (!isStateSaved) {
            if (this is SelectorMainFragment
                || (this is SelectorPreviewFragment && viewModel.previewWrap.isExternalPreview)
                || (this is SelectorSystemFragment && viewModel.config.systemGallery)
            ) {
                // Home Exit
                if (this is SelectorMainFragment) {
                    viewModel.config.mListenerInfo.onResultCallbackListener?.onCancel()
                }
                if (isNormalDefaultEnter()) {
                    requireActivity().finish()
                } else {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                SelectorProviders.getInstance().destroy()
                globalViewMode.reset()
            } else {
                // Pop the top state off the back stack. This function is asynchronous
                // it enqueues the request to pop, but the action will not be performed
                // until the application returns to its event loop.
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    /**
     * Confirm completion of selection
     */
    open fun onConfirmComplete() {
        requireActivity().runOnUiThread {
            if (!checkCompleteValidity()) {
                return@runOnUiThread
            }
            val selectResult = globalViewMode.selectResult.toMutableList()
            viewModel.viewModelScope.launch {
                val mediaConverterEngine = viewModel.config.mediaConverterEngine
                if (mediaConverterEngine != null) {
                    showLoading()
                    selectResult.forEach { media ->
                        mediaConverterEngine.converter(requireContext(), media)
                    }
                    dismissLoading()
                }
            }
            if (viewModel.config.isActivityResult) {
                requireActivity().intent?.apply {
                    putExtra(SelectorConstant.KEY_EXTRA_RESULT, selectResult as Serializable)
                    requireActivity().setResult(Activity.RESULT_OK, this)
                }
            } else {
                viewModel.config.mListenerInfo.onResultCallbackListener?.onResult(selectResult)
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
            globalViewMode.reset()
        }
    }

    /**
     * Verify legality before completion
     */
    open fun checkCompleteValidity(): Boolean {
        val selectResult = globalViewMode.selectResult
        if (viewModel.config.mListenerInfo.onConfirmListener?.onConfirm(
                viewModel.config,
                selectResult
            ) == true
        ) {
            return false
        }
        if (viewModel.config.selectorMode == SelectorMode.ALL) {
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
            if (viewModel.config.minSelectNum > 0 && imageSize <= 0) {
                showTipsDialog(
                    getString(
                        R.string.ps_min_img_num,
                        viewModel.config.minSelectNum.toString()
                    )
                )
                return false
            }
            if (viewModel.config.minVideoSelectNum > 0 && videoSize <= 0) {
                showTipsDialog(
                    getString(
                        R.string.ps_min_video_num,
                        viewModel.config.minVideoSelectNum.toString()
                    )
                )
                return false
            }
        } else {
            if (viewModel.config.minSelectNum > 0 && selectResult.size <= 0) {
                val msg = when (viewModel.config.selectorMode) {
                    SelectorMode.VIDEO -> {
                        getString(
                            R.string.ps_min_video_num,
                            viewModel.config.minSelectNum.toString()
                        )
                    }
                    SelectorMode.AUDIO -> {
                        getString(
                            R.string.ps_min_audio_num,
                            viewModel.config.minSelectNum.toString()
                        )
                    }
                    else -> {
                        getString(
                            R.string.ps_min_img_num,
                            viewModel.config.minSelectNum.toString()
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
        if (viewModel.config.selectorMode == SelectorMode.ALL) {
            if (viewModel.config.allCameraMode == SelectorMode.ALL) {
                onSelectedOnlyCameraDialog()
            } else {
                startCameraAction(viewModel.config.allCameraMode)
            }
        } else {
            startCameraAction(viewModel.config.selectorMode)
        }
    }

    /**
     * Activate camera intent based on [SelectorMode]
     */
    open fun startCameraAction(mode: SelectorMode) {
        if (mode == SelectorMode.AUDIO) {
            soundRecording()
        } else {
            val permission = arrayOf(Manifest.permission.CAMERA)
            showPermissionDescription(true, permission)
            PermissionChecker.requestPermissions(this, permission,
                object : OnPermissionResultListener {
                    override fun onGranted() {
                        if (mode == SelectorMode.VIDEO) {
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

    /**
     * sound recording
     */
    open fun soundRecording() {
        val onRecordAudioListener = viewModel.config.mListenerInfo.onRecordAudioListener
        if (onRecordAudioListener != null) {
            ForegroundService.startService(requireContext(), viewModel.config.isForegroundService)
            onRecordAudioListener.onRecordAudio(this, SelectorConstant.REQUEST_CAMERA)
        } else {
            throw NullPointerException("Please implement the ${OnRecordAudioListener::class.java.simpleName} interface to achieve recording functionality")
        }
    }

    /**
     * System camera takes pictures
     */
    open fun takePictures() {
        val context = requireContext()
        val outputDir = viewModel.config.imageOutputDir
        val defaultFileName = "${FileUtils.createFileName("IMG")}.jpg"
        val fileName =
            viewModel.config.mListenerInfo.onApplyFileNameListener?.apply(defaultFileName)
                ?: defaultFileName
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
        val onCustomCameraListener = viewModel.config.mListenerInfo.onCustomCameraListener
        if (onCustomCameraListener != null) {
            onCustomCameraListener.onCamera(
                this,
                CameraType.IMAGE, outputUri, SelectorConstant.REQUEST_CAMERA
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
                ForegroundService.startService(context, viewModel.config.isForegroundService)
            }
        }
    }

    /**
     * System camera recording video
     */
    open fun recordVideo() {
        val context = requireContext()
        val outputDir = viewModel.config.videoOutputDir
        val defaultFileName = "${FileUtils.createFileName("VID")}.mp4"
        val fileName =
            viewModel.config.mListenerInfo.onApplyFileNameListener?.apply(defaultFileName)
                ?: defaultFileName
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
        val onCustomCameraListener = viewModel.config.mListenerInfo.onCustomCameraListener
        if (onCustomCameraListener != null) {
            onCustomCameraListener.onCamera(
                this,
                CameraType.VIDEO,
                outputUri,
                SelectorConstant.REQUEST_CAMERA
            )
        } else {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
                intent.putExtra(SelectorConstant.QUICK_CAPTURE, viewModel.config.isQuickCapture)
                startActivityForResult(intent, SelectorConstant.REQUEST_CAMERA)
                ForegroundService.startService(context, viewModel.config.isForegroundService)
            }
        }
    }

    /**
     * [SelectorMode.ALL] mode, select one option for taking photos and recording videos, pop up the box
     */
    open fun onSelectedOnlyCameraDialog() {
        val selectedDialog = PhotoItemSelectedDialog.newInstance()
        selectedDialog.setOnItemClickListener(object : OnItemClickListener<View> {
            override fun onItemClick(position: Int, data: View) {
                when (position) {
                    PhotoItemSelectedDialog.IMAGE_CAMERA -> {
                        startCameraAction(SelectorMode.IMAGE)
                    }

                    PhotoItemSelectedDialog.VIDEO_CAMERA -> {
                        startCameraAction(SelectorMode.VIDEO)
                    }
                }
            }
        })
        selectedDialog.setOnDismissListener { isCancel, _ ->
            if (viewModel.config.isOnlyCamera && isCancel) {
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
            if (viewModel.config.selectionMode == SelectionMode.MULTIPLE) {
                if (onCheckSelectValidity(media, isSelected) != SelectedState.SUCCESS) {
                    return SelectedState.INVALID
                }
            }
            if (viewModel.config.mListenerInfo.onSelectFilterListener?.onSelectFilter(media) == true) {
                return SelectedState.INVALID
            }
        }
        return if (isSelected) {
            if (globalViewMode.selectResult.contains(media)) {
                globalViewMode.selectResult.remove(media)
                globalViewMode.selectResultLiveData.value = media
            }
            SelectedState.REMOVE
        } else {
            if (viewModel.config.selectionMode == SelectionMode.SINGLE) {
                if (globalViewMode.selectResult.isNotEmpty()) {
                    globalViewMode.selectResultLiveData.value =
                        globalViewMode.selectResult.first()
                    globalViewMode.selectResult.clear()
                }
            }
            if (!globalViewMode.selectResult.contains(media)) {
                globalViewMode.selectResult.add(media)
                globalViewMode.selectResultLiveData.value = media
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
        val count = globalViewMode.selectResult.size
        when (viewModel.config.selectorMode) {
            SelectorMode.ALL -> {
                if (viewModel.config.isAllWithImageVideo) {
                    // Support for selecting images and videos
                    var videoSize = 0
                    var imageSize = 0
                    globalViewMode.selectResult.forEach {
                        if (MediaUtils.hasMimeTypeOfVideo(it.mimeType)) {
                            videoSize++
                        } else if (MediaUtils.hasMimeTypeOfImage(it.mimeType)) {
                            imageSize++
                        }
                    }

                    if (viewModel.config.isAsTotalCount) {
                        // The number of maxVideoSelectNum in select all mode is included within maxSelectNum
                        if (count >= viewModel.config.totalCount) {
                            showTipsDialog(
                                getString(
                                    R.string.ps_message_max_num,
                                    viewModel.config.totalCount.toString()
                                )
                            )
                            return SelectedState.INVALID
                        }
                        if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                            // If the selected video exceeds the [config.maxVideoSelectNum] limit
                            if (videoSize >= viewModel.config.maxVideoSelectNum) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_video_max_num,
                                        viewModel.config.maxVideoSelectNum.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        }
                    } else {
                        if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                            // If the selected video exceeds the [config.maxVideoSelectNum] limit
                            if (videoSize >= viewModel.config.maxVideoSelectNum) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_video_max_num,
                                        viewModel.config.maxVideoSelectNum.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        } else if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                            // If the selected image exceeds the [config.maxSelectNum] limit
                            if (imageSize >= viewModel.config.totalCount) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_max_num,
                                        viewModel.config.totalCount.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        }
                    }
                } else {
                    // Only supports selecting images
                    if (globalViewMode.selectResult.isNotEmpty()) {
                        val first = globalViewMode.selectResult.first()
                        if (MediaUtils.hasMimeTypeOfImage(first.mimeType)) {
                            // Image has been selected
                            if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                                showTipsDialog(getString(R.string.ps_rule))
                                return SelectedState.INVALID
                            }
                            if (count >= viewModel.config.totalCount) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_max_num,
                                        viewModel.config.totalCount.toString()
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
                            if (count >= viewModel.config.totalCount) {
                                showTipsDialog(
                                    getString(
                                        R.string.ps_message_video_max_num,
                                        viewModel.config.totalCount.toString()
                                    )
                                )
                                return SelectedState.INVALID
                            }
                        }
                    }
                }
            }
            SelectorMode.IMAGE -> {
                if (count >= viewModel.config.totalCount) {
                    showTipsDialog(
                        getString(
                            R.string.ps_message_max_num, viewModel.config.totalCount.toString()
                        )
                    )
                    return SelectedState.INVALID
                }
            }
            SelectorMode.VIDEO -> {
                if (count >= viewModel.config.totalCount) {
                    showTipsDialog(
                        getString(
                            R.string.ps_message_video_max_num,
                            viewModel.config.totalCount.toString()
                        )
                    )
                    return SelectedState.INVALID
                }
            }
            SelectorMode.AUDIO -> {
                if (count >= viewModel.config.totalCount) {
                    showTipsDialog(
                        getString(
                            R.string.ps_message_audio_max_num,
                            viewModel.config.totalCount.toString()
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
        val cropEngine = viewModel.config.cropEngine
        if (cropEngine != null && isCrop()) {
            cropEngine.onCrop(this, globalViewMode.selectResult, SelectorConstant.REQUEST_CROP)
        } else {
            onConfirmComplete()
        }
    }

    /**
     * Media types that support cropping
     */
    open fun isCrop(): Boolean {
        globalViewMode.selectResult.forEach continuing@{ media ->
            if (viewModel.config.skipCropFormat.contains(media.mimeType)) {
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
        if (viewModel.config.language != Language.UNKNOWN_LANGUAGE) {
            PictureLanguageUtils.setAppLanguage(
                requireContext(),
                viewModel.config.language,
                viewModel.config.defaultLanguage
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
            PermissionChecker.onRequestPermissionsResult(grantResults, mPermissionResultListener)
            mPermissionResultListener = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectorConstant.REQUEST_CROP) {
                val selectResult = globalViewMode.selectResult
                if (selectResult.isNotEmpty()) {
                    if (selectResult.size == 1) {
                        mergeSingleCrop(data, selectResult)
                    } else {
                        mergeMultipleCrop(data, selectResult)
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == SelectorConstant.REQUEST_GO_SETTING) {
                handlePermissionSettingResult(viewModel.currentRequestPermission)
            }
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
            viewModel.config.mListenerInfo.onCustomLoadingListener?.create(requireContext())
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