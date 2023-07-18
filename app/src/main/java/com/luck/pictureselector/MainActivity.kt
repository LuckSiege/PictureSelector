package com.luck.pictureselector

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.luck.lib.camerax.SimpleCameraX
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.animators.AlphaInAnimationAdapter
import com.luck.picture.lib.animators.BaseAnimationAdapter
import com.luck.picture.lib.animators.SlideInBottomAnimationAdapter
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.constant.FileSizeUnitConstant
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.helper.ActivityCompatHelper
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.model.PictureSelector
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.style.StatusBarStyle
import com.luck.picture.lib.style.WindowAnimStyle
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SelectorLogUtils
import com.luck.picture.lib.utils.ToastUtils
import com.luck.picture.lib.widget.MediumBoldTextView
import com.luck.pictureselector.adapter.GridImageAdapter
import com.luck.pictureselector.custom.CustomPreviewExoVideoHolder
import com.luck.pictureselector.custom.CustomPreviewIjkVideoHolder
import com.luck.pictureselector.custom.CustomPreviewImageHolder
import com.luck.pictureselector.custom.CustomPreviewSystemVideoHolder
import com.luck.pictureselector.listener.DragListener
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import java.io.File
import java.util.*

private const val TAG_DESCRIPTION_VIEW = "TAG_DESCRIPTION_VIEW"

class MainActivity : AppCompatActivity() {
    private var maxSelectNum: Int = 9
    private var maxSelectVideoNum: Int = 1
    private var imageSpanCount: Int = 4
    private var pageSize: Int = SelectorConstant.DEFAULT_MAX_PAGE_SIZE
    private lateinit var mRecycler: RecyclerView
    private lateinit var mAdapter: GridImageAdapter
    private lateinit var rbDefaultPlayer: RadioButton
    private lateinit var rbExoPlayer: RadioButton
    private lateinit var rbIjkPlayer: RadioButton
    private lateinit var rbSystemPlayer: RadioButton
    private lateinit var rbDefaultStyle: RadioButton
    private lateinit var rbWhiteStyle: RadioButton
    private lateinit var rbNumNewStyle: RadioButton
    private lateinit var rbDefaultWindowAnim: RadioButton
    private lateinit var rbWindowUpAnim: RadioButton
    private lateinit var rbCallback: RadioButton
    private lateinit var rbLauncher: RadioButton
    private lateinit var rbRequestCode: RadioButton
    private lateinit var rbDefaultListAnim: RadioButton
    private lateinit var rbAlphaListAnim: RadioButton
    private lateinit var rbScaleListAnim: RadioButton
    private lateinit var tvDeleteText: TextView
    private lateinit var launcherResult: ActivityResultLauncher<Intent>
    private var mediaType: MediaType = MediaType.ALL
    private var selectionMode: SelectionMode = SelectionMode.MULTIPLE
    private var mData: MutableList<LocalMedia> = mutableListOf()
    private var language: Language = Language.SYSTEM_LANGUAGE
    private var soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
    private var needScaleBig = true
    private var needScaleSmall = false
    private var isHasLiftDelete = false
    private var soundID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ImageView>(R.id.left_back).setOnClickListener {
            finish()
        }
        val minus = findViewById<ImageView>(R.id.minus)
        val plus = findViewById<ImageView>(R.id.plus)
        val rowMinus = findViewById<ImageView>(R.id.row_minus)
        val rowPlus = findViewById<ImageView>(R.id.row_plus)

        val pageMinus = findViewById<ImageView>(R.id.page_minus)
        val tvPageNum = findViewById<TextView>(R.id.tv_page_num)
        val pagePlus = findViewById<ImageView>(R.id.page_plus)

        val tvRowNum = findViewById<TextView>(R.id.tv_row_num)
        val videoMinus = findViewById<ImageView>(R.id.video_minus)
        val videoPlus = findViewById<ImageView>(R.id.video_plus)
        val tvSelectNum = findViewById<TextView>(R.id.tv_select_num)
        val tvVideoNum = findViewById<TextView>(R.id.tv_select_video_num)
        val checkGif = findViewById<CheckBox>(R.id.check_gif)
        val checkWebp = findViewById<CheckBox>(R.id.check_webp)
        val checkBmp = findViewById<CheckBox>(R.id.check_bmp)
        val checkHeic = findViewById<CheckBox>(R.id.check_heic)
        val checkCrop = findViewById<CheckBox>(R.id.check_crop)
        val checkEditor = findViewById<CheckBox>(R.id.check_editor)
        val checkFilter = findViewById<CheckBox>(R.id.check_filter)
        val checkOutput = findViewById<CheckBox>(R.id.check_output)
        val checkOnlyDir = findViewById<CheckBox>(R.id.check_only_dir)
        val checkTimeAxis = findViewById<CheckBox>(R.id.check_time_axis)
        val checkLifecycle = findViewById<CheckBox>(R.id.check_lifecycle)
        val checkLoopVideo = findViewById<CheckBox>(R.id.check_loop_video)
        val checkAutoVideo = findViewById<CheckBox>(R.id.check_auto_video)
        val checkPauseVideo = findViewById<CheckBox>(R.id.check_pause_video)
        val checkSystem = findViewById<CheckBox>(R.id.check_system)
        val checkFastSelect = findViewById<CheckBox>(R.id.check_fast_select)
        val checkPreviewFull = findViewById<CheckBox>(R.id.check_full)
        val checkOriginal = findViewById<CheckBox>(R.id.check_original)
        val checkEmptyBack = findViewById<CheckBox>(R.id.check_empty_back)
        val checkPreviewEffect = findViewById<CheckBox>(R.id.check_effect)
        val checkOnlyCamera = findViewById<CheckBox>(R.id.check_only_camera)
        val checkMergeTotal = findViewById<CheckBox>(R.id.check_merge_total)
        val checkEnabledMask = findViewById<CheckBox>(R.id.check_enabled_mask)
        val checkCustomCamera = findViewById<CheckBox>(R.id.check_custom_camera)
        val checkPreviewImage = findViewById<CheckBox>(R.id.check_preview_image)
        val checkPreviewVideo = findViewById<CheckBox>(R.id.check_preview_video)
        val checkPreviewAudio = findViewById<CheckBox>(R.id.check_preview_audio)
        val checkPreviewDelete = findViewById<CheckBox>(R.id.check_preview_delete)
        val checkDisplayCamera = findViewById<CheckBox>(R.id.check_display_camera)
        val checkCameraServices = findViewById<CheckBox>(R.id.check_camera_services)
        val checkPreviewDownload = findViewById<CheckBox>(R.id.check_preview_download)
        val checkApplyPermission = findViewById<CheckBox>(R.id.check_apply_permission)
        val checkLongImage = findViewById<CheckBox>(R.id.check_long_image)
        soundID = soundPool.load(this, R.raw.ps_click_audio, 1)
        launcherResult = createActivityResultLauncher()
        minus.setOnClickListener {
            if (maxSelectNum > 1) {
                maxSelectNum--
            }
            tvSelectNum.text = maxSelectNum.toString()
            mAdapter.selectMax = maxSelectNum + maxSelectVideoNum
        }
        plus.setOnClickListener {
            maxSelectNum++
            tvSelectNum.text = maxSelectNum.toString()
            mAdapter.selectMax = maxSelectNum + maxSelectVideoNum
        }
        rowMinus.setOnClickListener {
            if (imageSpanCount > 1) {
                imageSpanCount--
            }
            tvRowNum.text = imageSpanCount.toString()
        }
        rowPlus.setOnClickListener {
            imageSpanCount++
            tvRowNum.text = imageSpanCount.toString()
        }
        videoMinus.setOnClickListener {
            if (maxSelectVideoNum > 0) {
                maxSelectVideoNum--
            }
            tvVideoNum.text = maxSelectVideoNum.toString()
            mAdapter.selectMax = maxSelectVideoNum + maxSelectNum
        }

        videoPlus.setOnClickListener {
            maxSelectVideoNum++
            tvVideoNum.text = maxSelectVideoNum.toString()
            mAdapter.selectMax = maxSelectVideoNum + maxSelectNum
        }

        pagePlus.setOnClickListener {
            pageSize += SelectorConstant.DEFAULT_MAX_PAGE_SIZE
            tvPageNum.text = pageSize.toString()
        }

        pageMinus.setOnClickListener {
            if (pageSize > SelectorConstant.DEFAULT_MAX_PAGE_SIZE) {
                pageSize -= SelectorConstant.DEFAULT_MAX_PAGE_SIZE
            }
            tvPageNum.text = pageSize.toString()
        }

        findViewById<RadioGroup>(R.id.rgb_type).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_all -> {
                    mediaType = MediaType.ALL
                }
                R.id.rb_image -> {
                    mediaType = MediaType.IMAGE
                }
                R.id.rb_video -> {
                    mediaType = MediaType.VIDEO
                }
                R.id.rb_audio -> {
                    mediaType = MediaType.AUDIO
                }
            }
        }

        findViewById<RadioGroup>(R.id.rgb_language).setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_system -> {
                    language = Language.SYSTEM_LANGUAGE
                }
                R.id.rb_chinese -> {
                    language = Language.CHINESE
                }
                R.id.rb_tw -> {
                    language = Language.TRADITIONAL_CHINESE
                }
                R.id.rb_us -> {
                    language = Language.ENGLISH
                }
                R.id.rb_ka -> {
                    language = Language.KOREA
                }
                R.id.rb_spanish -> {
                    language = Language.SPANISH
                }
                R.id.rb_de -> {
                    language = Language.GERMANY
                }
                R.id.rb_fr -> {
                    language = Language.FRANCE
                }
                R.id.rb_japan -> {
                    language = Language.JAPAN
                }
                R.id.rb_portugal -> {
                    language = Language.PORTUGAL
                }
                R.id.rb_ar -> {
                    language = Language.AR
                }
                R.id.rb_ru -> {
                    language = Language.RU
                }
                R.id.rb_cs -> {
                    language = Language.CS
                }
                R.id.rb_kk -> {
                    language = Language.KK
                }
            }
        }

        rbDefaultStyle = findViewById(R.id.rb_default)
        rbWhiteStyle = findViewById(R.id.rb_white)
        rbNumNewStyle = findViewById(R.id.rb_num_new)

        rbDefaultPlayer = findViewById(R.id.rb_default_player)
        rbExoPlayer = findViewById(R.id.rb_exo_player)
        rbIjkPlayer = findViewById(R.id.rb_ijk_player)
        rbSystemPlayer = findViewById(R.id.rb_system_player)

        rbCallback = findViewById(R.id.rb_callback)
        rbLauncher = findViewById(R.id.rb_launcher)
        rbRequestCode = findViewById(R.id.rb_request_code)

        rbDefaultListAnim = findViewById(R.id.rb_default_list_anim)
        rbAlphaListAnim = findViewById(R.id.rb_alpha_list_anim)
        rbScaleListAnim = findViewById(R.id.rb_scale_list_anim)

        rbDefaultWindowAnim = findViewById(R.id.rb_default_window_anim)
        rbWindowUpAnim = findViewById(R.id.rb_window_up_anim)

        tvDeleteText = findViewById(R.id.tv_delete_text)

        findViewById<RadioGroup>(R.id.rgb_selected).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_single -> {
                    selectionMode = SelectionMode.SINGLE
                }
                R.id.rb_only_single -> {
                    selectionMode = SelectionMode.ONLY_SINGLE
                }
                R.id.rb_multiple -> {
                    selectionMode = SelectionMode.MULTIPLE
                }
            }
        }

        mRecycler = findViewById(R.id.recycler)
        val manager = FullyGridLayoutManager(
            this,
            4, GridLayoutManager.VERTICAL, false
        )
        mRecycler.layoutManager = manager
        if (mRecycler.itemAnimator != null) {
            (mRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        mRecycler.addItemDecoration(
            GridSpacingItemDecoration(
                4,
                dip2px(this, 8f), false
            )
        )
        mAdapter = GridImageAdapter(this, mData)
        mAdapter.selectMax = maxSelectNum + maxSelectVideoNum
        mRecycler.adapter = mAdapter
        mAdapter.setItemLongClickListener { holder, position, v ->
            val itemViewType = holder.itemViewType
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                mItemTouchHelper.startDrag(holder)
            }
        }
        mItemTouchHelper.attachToRecyclerView(mRecycler)
        mAdapter.setOnItemClickListener(object : GridImageAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val preview = PictureSelector.create(this@MainActivity).openPreview()
                preview.setImageEngine(GlideEngine.create())
                preview.setStatusBarStyle(buildStatusBar())
                preview.setWindowAnimStyle(buildWindowAnim())
                if (rbWhiteStyle.isChecked) {
                    preview.inflateCustomLayout(
                        LayoutSource.SELECTOR_EXTERNAL_PREVIEW,
                        R.layout.ps_fragment_white_external_preview
                    )
                }
                preview.isPreviewZoomEffect(checkPreviewEffect.isChecked, false, mRecycler)
                preview.isDisplayDelete(checkPreviewDelete.isChecked)
                preview.isLongPressDownload(checkPreviewDownload.isChecked)
                preview.setOnExternalPreviewListener(object : OnExternalPreviewListener {
                    override fun onDelete(context: Context, position: Int, media: LocalMedia) {
                        mAdapter.remove(position)
                        mAdapter.notifyItemRemoved(position)
                    }

                    override fun onLongPressDownload(context: Context, media: LocalMedia): Boolean {
                        return false
                    }
                })
                preview.forPreview(position, mAdapter.getData(), true)
            }

            override fun openPicture() {
                when {
                    checkSystem.isChecked -> {
                        val systemGallery = PictureSelector.create(this@MainActivity)
                            .openSystemGallery(mediaType)
                        systemGallery.setSelectionMode(selectionMode)
                        systemGallery.setCropEngine(if (checkCrop.isChecked) UCropEngine() else null)
                        systemGallery.setMediaConverterEngine(MediaConverter.create())
                        systemGallery.setOnPermissionDescriptionListener(
                            getPermissionDescriptionListener
                        )
                        systemGallery.setOnPermissionsApplyListener(if (checkApplyPermission.isChecked) getPermissionsInterceptListener else null)
                        when {
                            rbCallback.isChecked -> {
                                systemGallery.forResult(getResultCallbackListener, true)
                            }
                            rbLauncher.isChecked -> {
                                systemGallery.forResult(launcherResult)
                            }
                            rbRequestCode.isChecked -> {
                                systemGallery.forResult(SelectorConstant.CHOOSE_REQUEST)
                            }
                        }
                    }
                    checkOnlyCamera.isChecked -> {
                        val onlyCamera = PictureSelector.create(this@MainActivity)
                            .openCamera(mediaType)
                        if (checkCustomCamera.isChecked) {
                            onlyCamera.registry(CustomCameraActivity::class.java)
                        }
                        onlyCamera.setAllOfCameraMode(MediaType.IMAGE)
                        if (checkOutput.isChecked) {
                            when (mediaType) {
                                MediaType.IMAGE -> {
                                    onlyCamera.setOutputImageDir(getCustomImagePath())
                                }
                                MediaType.VIDEO -> {
                                    onlyCamera.setOutputVideoDir(getCustomVideoPath())
                                }
                                MediaType.AUDIO -> {
                                    onlyCamera.setOutputAudioDir(getCustomAudioPath())
                                }
                                else -> {
                                    onlyCamera.setOutputAudioDir(getCustomAllPath())
                                    onlyCamera.setOutputImageDir(getCustomAllPath())
                                    onlyCamera.setOutputVideoDir(getCustomAllPath())
                                }
                            }
                        }
                        onlyCamera.isCameraForegroundService(checkCameraServices.isChecked)
                        onlyCamera.setMediaConverterEngine(MediaConverter.create())
                        onlyCamera.setCropEngine(if (checkCrop.isChecked) UCropEngine() else null)
                        onlyCamera.setOnRecordAudioListener(getRecordAudioListener)
                        onlyCamera.setOnSelectFilterListener(if (checkFilter.isChecked) geSelectFilterListener else null)
                        onlyCamera.setOnPermissionDescriptionListener(
                            getPermissionDescriptionListener
                        )
                        onlyCamera.setOnPermissionsApplyListener(if (checkApplyPermission.isChecked) getPermissionsInterceptListener else null)
                        when {
                            rbCallback.isChecked -> {
                                onlyCamera.forResult(getResultCallbackListener, true)
                            }
                            rbLauncher.isChecked -> {
                                onlyCamera.forResult(launcherResult)
                            }
                            rbRequestCode.isChecked -> {
                                onlyCamera.forResult(SelectorConstant.CHOOSE_REQUEST)
                            }
                        }
                    }
                    else -> {
                        val gallery = PictureSelector.create(this@MainActivity)
                            .openGallery(mediaType)
                        gallery.setImageSpanCount(imageSpanCount)
                        gallery.setMaxSelectNum(
                            maxSelectNum,
                            maxSelectVideoNum,
                            checkMergeTotal.isChecked
                        )
                        if (rbWhiteStyle.isChecked) {
                            gallery.inflateCustomLayout(
                                LayoutSource.SELECTOR_MAIN,
                                R.layout.ps_fragment_white_selector
                            )
                            gallery.inflateCustomLayout(
                                LayoutSource.SELECTOR_PREVIEW,
                                R.layout.ps_fragment_white_preview
                            )
                        }
                        gallery.isNewNumTemplate(rbNumNewStyle.isChecked)
                        gallery.setStatusBarStyle(buildStatusBar())
                        gallery.setWindowAnimStyle(buildWindowAnim())
                        if (checkLongImage.isChecked) {
                            gallery.registry(
                                CustomPreviewImageHolder::class.java,
                                LayoutSource.PREVIEW_ITEM_IMAGE,
                                R.layout.ps_custom_preview_image
                            )
                        }
                        when {
                            rbExoPlayer.isChecked -> {
                                gallery.registry(CustomPreviewExoVideoHolder::class.java)
                            }
                            rbIjkPlayer.isChecked -> {
                                gallery.registry(CustomPreviewIjkVideoHolder::class.java)
                            }
                            rbSystemPlayer.isChecked -> {
                                gallery.registry(CustomPreviewSystemVideoHolder::class.java)
                            }
                            else -> {
                                gallery.registry(PreviewVideoHolder::class.java)
                            }
                        }
                        if (checkCustomCamera.isChecked) {
                            gallery.registry(CustomCameraActivity::class.java)
                        }
                        gallery.setOnAnimationAdapterWrapListener(object :
                            OnAnimationAdapterWrapListener {
                            override fun wrap(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>): BaseAnimationAdapter? {
                                return when {
                                    rbAlphaListAnim.isChecked -> {
                                        AlphaInAnimationAdapter(adapter)
                                    }
                                    rbScaleListAnim.isChecked -> {
                                        SlideInBottomAnimationAdapter(adapter)
                                    }
                                    else -> {
                                        null
                                    }
                                }
                            }
                        })
                        gallery.setLanguage(language)
                        gallery.setPageSize(pageSize)
                        gallery.setImageEngine(GlideEngine.create())
                        gallery.setMediaConverterEngine(MediaConverter.create())
                        gallery.setCropEngine(if (checkCrop.isChecked) UCropEngine() else null)
                        gallery.setOnEditorMediaListener(if (checkEditor.isChecked) getEditorMediaListener else null)
                        gallery.setOnFragmentLifecycleListener(if (checkLifecycle.isChecked) getFragmentLifecycleListener else null)
                        gallery.setOnSelectFilterListener(if (checkFilter.isChecked) geSelectFilterListener else null)
                        gallery.setOnPermissionDescriptionListener(getPermissionDescriptionListener)
                        gallery.setOnPermissionsApplyListener(if (checkApplyPermission.isChecked) getPermissionsInterceptListener else null)
                        if (checkOutput.isChecked) {
                            when (mediaType) {
                                MediaType.IMAGE -> {
                                    gallery.setOutputImageDir(getCustomImagePath())
                                    gallery.setQuerySandboxDir(
                                        getCustomImagePath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                                MediaType.VIDEO -> {
                                    gallery.setOutputVideoDir(getCustomVideoPath())
                                    gallery.setQuerySandboxDir(
                                        getCustomVideoPath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                                MediaType.AUDIO -> {
                                    gallery.setOutputAudioDir(getCustomAudioPath())
                                    gallery.setQuerySandboxDir(
                                        getCustomAudioPath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                                else -> {
                                    gallery.setOutputImageDir(getCustomAllPath())
                                    gallery.setOutputVideoDir(getCustomAllPath())
                                    gallery.setQuerySandboxDir(
                                        getCustomAllPath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                            }
                        } else if (checkOnlyDir.isChecked) {
                            when (mediaType) {
                                MediaType.IMAGE -> {
                                    gallery.setQuerySandboxDir(
                                        getCustomImagePath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                                MediaType.VIDEO -> {
                                    gallery.setQuerySandboxDir(
                                        getCustomVideoPath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                                MediaType.AUDIO -> {
                                    gallery.setQuerySandboxDir(
                                        getCustomAudioPath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                                else -> {
                                    gallery.setQuerySandboxDir(
                                        getCustomAllPath(),
                                        checkOnlyDir.isChecked
                                    )
                                }
                            }
                        }
                        gallery.setSelectionMode(selectionMode)
                        gallery.isPreviewZoomEffect(
                            checkPreviewEffect.isChecked,
                            checkPreviewFull.isChecked
                        )
                        gallery.isGif(checkGif.isChecked)
                        gallery.isWebp(checkWebp.isChecked)
                        gallery.isBmp(checkBmp.isChecked)
                        gallery.isHeic(checkHeic.isChecked)
                        gallery.setSelectedData(mAdapter.getData())
                        gallery.isCameraForegroundService(checkCameraServices.isChecked)
                        gallery.setOnRecordAudioListener(getRecordAudioListener)
                        gallery.isDisplayCamera(checkDisplayCamera.isChecked)
                        gallery.isFastSlidingSelect(checkFastSelect.isChecked)
                        gallery.isDisplayTimeAxis(checkTimeAxis.isChecked)
                        gallery.isEmptyResultBack(checkEmptyBack.isChecked)
                        gallery.isOriginalControl(checkOriginal.isChecked)
                        gallery.isMaxSelectEnabledMask(checkEnabledMask.isChecked)
                        gallery.isPreviewImage(checkPreviewImage.isChecked)
                        gallery.isPreviewVideo(checkPreviewVideo.isChecked)
                        gallery.isPreviewAudio(checkPreviewAudio.isChecked)
                        gallery.isAutoPlay(checkAutoVideo.isChecked)
                        gallery.isLoopAutoVideoPlay(checkLoopVideo.isChecked)
                        gallery.isVideoPauseResumePlay(checkPauseVideo.isChecked)
                        when {
                            rbCallback.isChecked -> {
                                gallery.forResult(getResultCallbackListener)
                            }
                            rbLauncher.isChecked -> {
                                gallery.forResult(launcherResult)
                            }
                            rbRequestCode.isChecked -> {
                                gallery.forResult(SelectorConstant.CHOOSE_REQUEST)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun buildStatusBar(): StatusBarStyle {
        return StatusBarStyle().apply {
            when {
                rbDefaultStyle.isChecked -> {
                    of(
                        false,
                        Color.parseColor("#393a3e"),
                        Color.parseColor("#393a3e")
                    )
                }
                rbWhiteStyle.isChecked -> {
                    of(
                        true,
                        Color.parseColor("#FFFFFF"),
                        Color.parseColor("#FFFFFF")
                    )
                }
                rbNumNewStyle.isChecked -> {
                    of(
                        false,
                        Color.parseColor("#393a3e"),
                        Color.parseColor("#393a3e")
                    )
                }
            }
        }
    }

    private fun buildWindowAnim(): WindowAnimStyle {
        return WindowAnimStyle().apply {
            if (rbDefaultWindowAnim.isChecked) {
                of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
            } else if (rbWindowUpAnim.isChecked) {
                of(R.anim.ps_anim_up_in, R.anim.ps_anim_down_out)
            }
        }
    }

    private val mItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        override fun getMovementFlags(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            val itemViewType = viewHolder.itemViewType
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.alpha = 0.7f
            }
            return makeMovementFlags(
                ItemTouchHelper.DOWN or ItemTouchHelper.UP
                        or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            try {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition
                val itemViewType = target.itemViewType
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (fromPosition < toPosition) {
                        for (i in fromPosition until toPosition) {
                            Collections.swap(mAdapter.getData(), i, i + 1)
                        }
                    } else {
                        for (i in fromPosition downTo toPosition + 1) {
                            Collections.swap(mAdapter.getData(), i, i - 1)
                        }
                    }
                    mAdapter.notifyItemMoved(fromPosition, toPosition)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dx: Float,
            dy: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemViewType = viewHolder.itemViewType
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (needScaleBig) {
                    needScaleBig = false
                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(
                        ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0f, 1.1f),
                        ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0f, 1.1f)
                    )
                    animatorSet.duration = 50
                    animatorSet.interpolator = LinearInterpolator()
                    animatorSet.start()
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            needScaleSmall = true
                        }
                    })
                }
                if (dy >= tvDeleteText.top - viewHolder.itemView.bottom) {
                    mDragListener.deleteState(true)
                    if (isHasLiftDelete) {
                        viewHolder.itemView.visibility = View.INVISIBLE
                        mAdapter.delete(viewHolder.absoluteAdapterPosition)
                    }
                } else {
                    mDragListener.deleteState(false)
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dx,
                    dy,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            val itemViewType = viewHolder?.itemViewType ?: GridImageAdapter.TYPE_CAMERA
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
                    mDragListener.dragState(true)
                }
                super.onSelectedChanged(viewHolder, actionState)
            }
        }

        override fun getAnimationDuration(
            recyclerView: RecyclerView,
            animationType: Int,
            animateDx: Float,
            animateDy: Float
        ): Long {
            isHasLiftDelete = true
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            val itemViewType = viewHolder.itemViewType
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.alpha = 1.0f
                if (needScaleSmall) {
                    needScaleSmall = false
                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(
                        ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1f, 1.0f),
                        ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1f, 1.0f)
                    )
                    animatorSet.interpolator = LinearInterpolator()
                    animatorSet.duration = 50
                    animatorSet.start()
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            needScaleBig = true
                        }
                    })
                }
                super.clearView(recyclerView, viewHolder)
                mAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                resetState()
            }
        }
    })

    private fun resetState() {
        isHasLiftDelete = false
        mDragListener.deleteState(false)
        mDragListener.dragState(false)
    }

    private val mDragListener: DragListener = object : DragListener {
        override fun deleteState(isDelete: Boolean) {
            if (isDelete) {
                if (!TextUtils.equals(
                        getString(R.string.app_let_go_drag_delete),
                        tvDeleteText.text
                    )
                ) {
                    tvDeleteText.text = getString(R.string.app_let_go_drag_delete)
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_dump_delete,
                        0,
                        0
                    )
                }
            } else {
                if (!TextUtils.equals(getString(R.string.app_drag_delete), tvDeleteText.text)) {
                    tvDeleteText.text = getString(R.string.app_drag_delete)
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_normal_delete,
                        0,
                        0
                    )
                }
            }
        }

        override fun dragState(isStart: Boolean) {
            if (isStart) {
                if (tvDeleteText.alpha == 0f) {
                    tvDeleteText.animate().alpha(1F).setDuration(120).start()
                }
            } else {
                if (tvDeleteText.alpha == 1f) {
                    tvDeleteText.animate().alpha(0F).setDuration(120).start()
                }
            }
        }
    }

    private val getEditorMediaListener = object : OnEditorMediaListener {

        override fun onEditorMedia(fragment: Fragment, media: LocalMedia, requestCode: Int) {
            if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) {
                ToastUtils.showMsg(fragment.requireContext(), "视频编辑功能请自行实现")
                return
            }
            val path = media.getAvailablePath() ?: return
            val sourceUri =
                if (MediaUtils.isContent(path)) Uri.parse(path) else Uri.fromFile(File(path))
            val destinationUri = Uri.fromFile(
                File(fragment.requireContext().cacheDir, "${System.currentTimeMillis()}.jpg")
            )
            val uCrop = UCrop.of<UCrop>(sourceUri, destinationUri)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(
                    context: Context,
                    url: String,
                    imageView: ImageView
                ) {
                    if (!ActivityCompatHelper.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).load(url).override(180, 180)
                        .into(imageView)
                }

                override fun loadImage(
                    context: Context,
                    url: Uri,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: UCropImageEngine.OnCallbackListener<Bitmap>?
                ) {
                    if (!ActivityCompatHelper.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).asBitmap().load(url)
                        .override(maxWidth, maxHeight)
                        .into(object : CustomTarget<Bitmap?>() {

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                call?.onCall(null)
                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap?>?
                            ) {
                                call?.onCall(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                call?.onCall(null)
                            }
                        })
                }
            })
            uCrop.startEdit(fragment.requireContext(), fragment, requestCode)
        }
    }

    private val getPermissionsInterceptListener = object : OnPermissionApplyListener {
        override fun requestPermission(
            fragment: Fragment,
            permissionArray: Array<String>,
            call: OnRequestPermissionListener
        ) {
            PermissionChecker.requestPermissions(fragment,
                permissionArray,
                object : OnPermissionResultListener {
                    override fun onGranted() {
                        call.onCall(permissionArray, true)
                    }

                    override fun onDenied() {
                        call.onCall(permissionArray, false)
                    }
                })
        }

        override fun hasPermissions(
            fragment: Fragment,
            permissionArray: Array<String>
        ): Boolean {
            return PermissionChecker.checkSelfPermission(
                fragment.requireContext(),
                permissionArray
            )
        }
    }

    private val getPermissionDescriptionListener = object : OnPermissionDescriptionListener {
        override fun onDescription(
            fragment: Fragment,
            permissionArray: Array<String>
        ) {
            val viewGroup = fragment.requireView() as ViewGroup
            val dp10 = dip2px(viewGroup.context, 10f)
            val dp15 = dip2px(viewGroup.context, 15f)
            val view = MediumBoldTextView(viewGroup.context)
            view.tag = TAG_DESCRIPTION_VIEW
            view.textSize = 14f
            view.setTextColor(Color.parseColor("#333333"))
            view.setPadding(dp10, dp15, dp10, dp15)
            val title: String
            val explain: String
            when {
                TextUtils.equals(
                    permissionArray[0],
                    Manifest.permission.CAMERA
                ) -> {
                    title = "相机权限使用说明"
                    explain = "相机权限使用说明\n用户app用于拍照/录视频"
                }
                TextUtils.equals(
                    permissionArray[0],
                    Manifest.permission.RECORD_AUDIO
                ) -> {
                    title = "录音权限使用说明"
                    explain = "录音权限使用说明\n用户app用于采集声音"
                }
                else -> {
                    title = "存储权限使用说明"
                    explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息"
                }
            }
            val startIndex = 0
            val endOf = startIndex + title.length
            val builder = SpannableStringBuilder(explain)
            builder.setSpan(
                AbsoluteSizeSpan(
                    dip2px(
                        viewGroup.context,
                        16f
                    )
                ), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            builder.setSpan(
                ForegroundColorSpan(-0xcccccd),
                startIndex,
                endOf,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            view.text = builder
            view.background = ContextCompat.getDrawable(
                viewGroup.context,
                R.drawable.ps_demo_permission_desc_bg
            )
            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topToBottom = R.id.ps_title_bar
            layoutParams.leftToLeft = ConstraintSet.PARENT_ID
            layoutParams.leftMargin = dp10
            layoutParams.rightMargin = dp10
            viewGroup.addView(view, layoutParams)
        }

        override fun onDismiss(fragment: Fragment) {
            val viewGroup = fragment.requireView() as ViewGroup
            viewGroup.removeView(viewGroup.findViewWithTag(TAG_DESCRIPTION_VIEW))
        }
    }

    private val geSelectFilterListener = object : OnSelectFilterListener {
        override fun onSelectFilter(context: Context, media: LocalMedia): Boolean {
            if (media.size > 10 * FileSizeUnitConstant.MB) {
                ToastUtils.showMsg(context, "文件大于10M")
                return true
            }
            // 选择的时候可以添加一些音效...
            soundPool.play(soundID, 0.1f, 0.5f, 0, 1, 1f)
            return false
        }
    }

    private val getFragmentLifecycleListener = object : OnFragmentLifecycleListener {
        override fun onViewCreated(
            fragment: Fragment,
            view: View?,
            savedInstanceState: Bundle?
        ) {
            ToastUtils.showMsg(fragment.requireContext(), "创建:$fragment")
        }

        override fun onDestroy(fragment: Fragment) {
            ToastUtils.showMsg(fragment.requireContext(), "销毁:$fragment")
        }
    }

    private val getCustomCameraListener = object : OnCustomCameraListener {
        override fun onCamera(
            fragment: Fragment,
            type: MediaType,
            outputUri: Uri,
            requestCode: Int
        ) {
            val camera = SimpleCameraX.of()
            camera.isAutoRotation(true)
            camera.setCameraMode(0)
            camera.setVideoFrameRate(25)
            camera.setVideoBitRate(3 * 1024 * 1024)
            camera.isDisplayRecordChangeTime(true)
            camera.setImageEngine { context, url, imageView ->
                Glide.with(context).load(url).into(imageView)
            }
            camera.start(fragment.requireActivity(), fragment, requestCode)
        }
    }

    private val getRecordAudioListener = object : OnRecordAudioListener {

        override fun onRecordAudio(fragment: Fragment, requestCode: Int) {
            startRecordSoundAction(fragment, requestCode)
        }
    }

    /**
     * 启动录音意图
     *
     * @param fragment
     * @param requestCode
     */
    private fun startRecordSoundAction(fragment: Fragment, requestCode: Int) {
        val recordAudioIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (recordAudioIntent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            fragment.startActivityForResult(recordAudioIntent, requestCode)
        } else {
            ToastUtils.showMsg(
                fragment.requireContext(),
                "The system is missing a recording component"
            )
        }
    }

    private val getResultCallbackListener = object : OnResultCallbackListener {
        override fun onResult(result: List<LocalMedia>) {
            showDisplayResult(result)
        }

        override fun onCancel() {
            SelectorLogUtils.info("onCancel")
        }
    }

    private fun createActivityResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                showDisplayResult(PictureSelector.obtainSelectResults(result.data))
            } else if (resultCode == RESULT_CANCELED) {
                if (rbLauncher.isChecked) {
                    SelectorLogUtils.info("Launcher onCancel")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectorConstant.CHOOSE_REQUEST) {
                showDisplayResult(PictureSelector.obtainSelectResults(data))
            } else if (requestCode == SelectorConstant.REQUEST_CAMERA) {
                showDisplayResult(PictureSelector.obtainSelectResults(data))
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (rbRequestCode.isChecked) {
                SelectorLogUtils.info("Activity Result onCancel")
            }
        }
    }

    /**
     * 显示选择结果
     */
    private fun showDisplayResult(result: List<LocalMedia>) {
        runOnUiThread {
            val isMaxSize = result.size == mAdapter.selectMax
            val oldSize: Int = mAdapter.getData().size
            mAdapter.notifyItemRangeRemoved(
                0,
                if (isMaxSize) oldSize + 1 else oldSize
            )
            mAdapter.getData().clear()
            mAdapter.getData().addAll(result)
            mAdapter.notifyItemRangeInserted(0, result.size)
        }
    }

    /**
     * 创建自定义图片和视频输出目录
     */
    private fun getCustomAllPath(): String {
        val allDir = this@MainActivity.getExternalFilesDir("")
        val customFile = File(allDir?.absolutePath, "AllFiles")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }


    /**
     * 创建自定义图片输出目录
     */
    private fun getCustomImagePath(): String {
        val picturesDir = this@MainActivity.getExternalFilesDir("")
        val customFile = File(picturesDir?.absolutePath, "ImageFiles")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    /**
     * 创建自定义视频输出目录
     */
    private fun getCustomVideoPath(): String {
        val moviesDir = this@MainActivity.getExternalFilesDir("")
        val customFile = File(moviesDir?.absolutePath, "VideoFiles")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    /**
     * 创建自定义音频输出目录
     */
    private fun getCustomAudioPath(): String {
        val musicDir = this@MainActivity.getExternalFilesDir("")
        val customFile = File(musicDir?.absolutePath, "AudioFiles")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}