package com.luck.pictureselector

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.SelectorNumberMainFragment
import com.luck.picture.lib.SelectorNumberPreviewFragment
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnExternalPreviewListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.Language
import com.luck.picture.lib.model.PictureSelector
import com.luck.picture.lib.style.SelectorStyle
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.utils.SelectorLogUtils
import com.luck.picture.lib.utils.ToastUtils
import com.luck.pictureselector.adapter.GridImageAdapter
import com.luck.pictureselector.custom.CustomPreviewExoVideoHolder
import com.luck.pictureselector.custom.CustomPreviewIjkVideoHolder
import com.luck.pictureselector.custom.CustomPreviewImageHolder


class MainActivity : AppCompatActivity() {
    private var maxSelectNum: Int = 9
    private var maxSelectVideoNum: Int = 1
    private lateinit var mRecycler: RecyclerView
    private lateinit var mAdapter: GridImageAdapter
    private lateinit var rbDefaultPlayer: RadioButton
    private lateinit var rbExoPlayer: RadioButton
    private lateinit var rbIjkPlayer: RadioButton
    private lateinit var rbDefaultStyle: RadioButton
    private lateinit var rbWhiteStyle: RadioButton
    private lateinit var rbNumNewStyle: RadioButton
    private lateinit var rbDefaultWindowAnim: RadioButton
    private lateinit var rbWindowUpAnim: RadioButton
    private lateinit var rbCallback: RadioButton
    private lateinit var rbLauncher: RadioButton
    private lateinit var rbRequestCode: RadioButton
    private lateinit var launcherResult: ActivityResultLauncher<Intent>
    private var selectorMode: SelectorMode = SelectorMode.ALL
    private var selectionMode: SelectionMode = SelectionMode.MULTIPLE
    private var mData: MutableList<LocalMedia> = mutableListOf()
    private var language: Language = Language.SYSTEM_LANGUAGE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ImageView>(R.id.left_back).setOnClickListener {
            finish()
        }
        val minus = findViewById<ImageView>(R.id.minus)
        val plus = findViewById<ImageView>(R.id.plus)
        val videoMinus = findViewById<ImageView>(R.id.video_minus)
        val videoPlus = findViewById<ImageView>(R.id.video_plus)
        val tvSelectNum = findViewById<TextView>(R.id.tv_select_num)
        val tvVideoNum = findViewById<TextView>(R.id.tv_select_video_num)
        val checkGif = findViewById<CheckBox>(R.id.check_gif)
        val checkCrop = findViewById<CheckBox>(R.id.check_crop)
        val checkTimeAxis = findViewById<CheckBox>(R.id.check_time_axis)
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
        val checkPreviewDownload = findViewById<CheckBox>(R.id.check_preview_download)
        val checkLongImage = findViewById<CheckBox>(R.id.check_long_image)
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
        findViewById<RadioGroup>(R.id.rgb_type).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_all -> {
                    selectorMode = SelectorMode.ALL
                }
                R.id.rb_image -> {
                    selectorMode = SelectorMode.IMAGE
                }
                R.id.rb_video -> {
                    selectorMode = SelectorMode.VIDEO
                }
                R.id.rb_audio -> {
                    selectorMode = SelectorMode.AUDIO
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

        rbCallback = findViewById(R.id.rb_callback)
        rbLauncher = findViewById(R.id.rb_launcher)
        rbRequestCode = findViewById(R.id.rb_request_code)

        rbDefaultWindowAnim = findViewById(R.id.rb_default_window_anim)
        rbWindowUpAnim = findViewById(R.id.rb_window_up_anim)

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
        mAdapter.setOnItemClickListener(object : GridImageAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val uiStyle = SelectorStyle()
                val preview = PictureSelector.create(this@MainActivity).openPreview()
                preview.setImageEngine(GlideEngine.create())
                when {
                    rbDefaultStyle.isChecked -> {
                        uiStyle.getStatusBar().of(
                            false,
                            Color.parseColor("#393a3e"),
                            Color.parseColor("#393a3e")
                        )
                    }
                    rbWhiteStyle.isChecked -> {
                        uiStyle.getStatusBar().of(
                            true,
                            Color.parseColor("#FFFFFF"),
                            Color.parseColor("#FFFFFF")
                        )
                        preview.inflateCustomLayout(
                            LayoutSource.SELECTOR_EXTERNAL_PREVIEW,
                            R.layout.ps_fragment_white_external_preview
                        )
                    }
                    rbNumNewStyle.isChecked -> {
                        uiStyle.getStatusBar().of(
                            false,
                            Color.parseColor("#393a3e"),
                            Color.parseColor("#393a3e")
                        )
                    }
                }
                if (rbDefaultWindowAnim.isChecked) {
                    uiStyle.getWindowAnimation()
                        .of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
                } else if (rbWindowUpAnim.isChecked) {
                    uiStyle.getWindowAnimation()
                        .of(R.anim.ps_anim_up_in, R.anim.ps_anim_down_out)
                }
                preview.setSelectorUIStyle(uiStyle)
                preview.isPreviewZoomEffect(
                    checkPreviewEffect.isChecked, false, mRecycler
                )
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
                preview.forPreviewActivity(position, mAdapter.getData())
            }

            override fun openPicture() {
                when {
                    checkSystem.isChecked -> {
                        PictureSelector.create(this@MainActivity)
                            .openSystemGallery(selectorMode)
                            .forSystemResult(object : OnResultCallbackListener {
                                override fun onResult(result: List<LocalMedia>) {
                                    showDisplayResult(result)
                                }

                                override fun onCancel() {
                                    SelectorLogUtils.info("onCancel")
                                }
                            })
                    }
                    checkOnlyCamera.isChecked -> {
                        PictureSelector.create(this@MainActivity)
                            .openCamera(selectorMode)
                            .setAllOfCameraMode(SelectorMode.IMAGE)
                            .forResult(object : OnResultCallbackListener {
                                override fun onResult(result: List<LocalMedia>) {
                                    showDisplayResult(result)
                                }

                                override fun onCancel() {
                                    SelectorLogUtils.info("onCancel")
                                }
                            })
                    }
                    else -> {
                        val uiStyle = SelectorStyle()
                        val gallery = PictureSelector.create(this@MainActivity)
                            .openGallery(selectorMode)
                        gallery.setMaxSelectNum(
                            maxSelectNum,
                            maxSelectVideoNum,
                            checkMergeTotal.isChecked
                        )
                        when {
                            rbDefaultStyle.isChecked -> {
                                uiStyle.getStatusBar().of(
                                    false,
                                    Color.parseColor("#393a3e"),
                                    Color.parseColor("#393a3e")
                                )
                            }
                            rbWhiteStyle.isChecked -> {
                                uiStyle.getStatusBar().of(
                                    true,
                                    Color.parseColor("#FFFFFF"),
                                    Color.parseColor("#FFFFFF")
                                )
                                gallery.inflateCustomLayout(
                                    LayoutSource.SELECTOR_MAIN,
                                    R.layout.ps_fragment_white_selector
                                )
                                gallery.inflateCustomLayout(
                                    LayoutSource.SELECTOR_PREVIEW,
                                    R.layout.ps_fragment_white_preview
                                )
                            }
                            rbNumNewStyle.isChecked -> {
                                uiStyle.getStatusBar().of(
                                    false,
                                    Color.parseColor("#393a3e"),
                                    Color.parseColor("#393a3e")
                                )
                                gallery.registry(SelectorNumberMainFragment::class.java)
                                gallery.registry(SelectorNumberPreviewFragment::class.java)
                            }
                        }
                        if (rbDefaultWindowAnim.isChecked) {
                            uiStyle.getWindowAnimation()
                                .of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
                        } else if (rbWindowUpAnim.isChecked) {
                            uiStyle.getWindowAnimation()
                                .of(R.anim.ps_anim_up_in, R.anim.ps_anim_down_out)
                        }
                        gallery.setSelectorUIStyle(uiStyle)
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
                            else -> {
                                gallery.registry(PreviewVideoHolder::class.java)
                            }
                        }
                        if (checkCustomCamera.isChecked) {
                            gallery.registry(CustomCameraActivity::class.java)
                        }
                        gallery.setLanguage(language)
                        gallery.setImageEngine(GlideEngine.create())
                        gallery.setMediaConverterEngine(MediaConverter.create())
                        gallery.setCropEngine(if (checkCrop.isChecked) UCropEngine() else null)
                        gallery.isPreviewZoomEffect(
                            checkPreviewEffect.isChecked,
                            checkPreviewFull.isChecked
                        )
                        gallery.isGif(checkGif.isChecked)
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
}