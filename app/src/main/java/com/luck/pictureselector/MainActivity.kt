package com.luck.pictureselector

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.Registry
import com.luck.picture.lib.SelectorNumberMainFragment
import com.luck.picture.lib.SelectorNumberPreviewFragment
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.model.PictureSelector
import com.luck.picture.lib.model.SelectionMainModel
import com.luck.picture.lib.model.SelectionPreviewModel
import com.luck.picture.lib.style.SelectorStyle
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SelectorLogUtils
import com.luck.picture.lib.utils.ToastUtils
import com.luck.pictureselector.adapter.GridImageAdapter
import com.luck.pictureselector.custom.CustomMediaPreviewAdapter
import com.luck.pictureselector.custom.CustomPreviewExoVideoHolder
import com.luck.pictureselector.custom.CustomPreviewIjkVideoHolder


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

    private var selectorMode: SelectorMode = SelectorMode.ALL
    private var selectionMode: SelectionMode = SelectionMode.MULTIPLE
    private var mData: MutableList<LocalMedia> = mutableListOf()
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
        val checkLoopVideo = findViewById<CheckBox>(R.id.check_loop_video)
        val checkAutoVideo = findViewById<CheckBox>(R.id.check_auto_video)
        val checkPauseVideo = findViewById<CheckBox>(R.id.check_pause_video)
        val checkSystem = findViewById<CheckBox>(R.id.check_system)
        val checkPreviewFull = findViewById<CheckBox>(R.id.check_full)
        val checkOriginal = findViewById<CheckBox>(R.id.check_original)
        val checkEmptyBack = findViewById<CheckBox>(R.id.check_empty_back)
        val checkPreviewEffect = findViewById<CheckBox>(R.id.check_effect)
        val checkOnlyCamera = findViewById<CheckBox>(R.id.check_only_camera)
        val checkMergeTotal = findViewById<CheckBox>(R.id.check_merge_total)
        val checkEnabledMask = findViewById<CheckBox>(R.id.check_enabled_mask)
        val checkPreviewImage = findViewById<CheckBox>(R.id.check_preview_image)
        val checkPreviewVideo = findViewById<CheckBox>(R.id.check_preview_video)
        val checkPreviewAudio = findViewById<CheckBox>(R.id.check_preview_audio)
        val checkPreviewDelete = findViewById<CheckBox>(R.id.check_preview_delete)
        val checkPreviewDownload = findViewById<CheckBox>(R.id.check_preview_download)
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

        rbDefaultStyle = findViewById(R.id.rb_default)
        rbWhiteStyle = findViewById(R.id.rb_white)
        rbNumNewStyle = findViewById(R.id.rb_num_new)

        rbDefaultPlayer = findViewById(R.id.rb_default_player)
        rbExoPlayer = findViewById(R.id.rb_exo_player)
        rbIjkPlayer = findViewById(R.id.rb_ijk_player)

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
                val registry = Registry()
                val preview = PictureSelector.create(this@MainActivity).openPreview()
                preview.setImageEngine(GlideEngine.create())
                preview.registry(buildExternalPreviewStyle(registry, preview))
                preview.isPreviewZoomEffect(
                    checkPreviewEffect.isChecked, false, mRecycler
                )
                preview.isDisplayDelete(checkPreviewDelete.isChecked)
                preview.isLongPressDownload(checkPreviewDownload.isChecked)
                preview.forPreview(position, mAdapter.getData())
            }

            override fun openPicture() {
                when {
                    checkSystem.isChecked -> {
                    }
                    checkOnlyCamera.isChecked -> {
                    }
                    else -> {
                        val registry = Registry()
                        val gallery = PictureSelector.create(this@MainActivity)
                            .openGallery(selectorMode)
                        gallery.setMaxSelectNum(
                            maxSelectNum,
                            maxSelectVideoNum,
                            checkMergeTotal.isChecked
                        )
                        buildGalleryStyle(registry, gallery)
                        buildCustomPlayer(registry)
                        gallery.setImageEngine(GlideEngine.create())
                        gallery.setMediaConverterEngine(MediaConverter.create())
                        gallery.setCropEngine(if (checkCrop.isChecked) UCropEngine() else null)
                        gallery.registry(registry)
                        gallery.isPreviewZoomEffect(
                            checkPreviewEffect.isChecked,
                            checkPreviewFull.isChecked
                        )
                        gallery.isGif(checkGif.isChecked)
                        gallery.isEmptyResultBack(checkEmptyBack.isChecked)
                        gallery.isOriginalControl(checkOriginal.isChecked)
                        gallery.isMaxSelectEnabledMask(checkEnabledMask.isChecked)
                        gallery.isPreviewImage(checkPreviewImage.isChecked)
                        gallery.isPreviewVideo(checkPreviewVideo.isChecked)
                        gallery.isPreviewAudio(checkPreviewAudio.isChecked)
                        gallery.isAutoVideoPlay(checkAutoVideo.isChecked)
                        gallery.isLoopAutoVideoPlay(checkLoopVideo.isChecked)
                        gallery.isVideoPauseResumePlay(checkPauseVideo.isChecked)
                        gallery.forResult(object : OnResultCallbackListener {
                            override fun onResult(result: List<LocalMedia>) {
                                showDisplayResult(result)
                            }

                            override fun onCancel() {
                                SelectorLogUtils.info("onCancel")
                            }
                        })
                    }
                }
            }
        })
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

    /**
     * 选择器预览风格样式
     */
    private fun buildExternalPreviewStyle(
        registry: Registry, previewModel: SelectionPreviewModel
    ): Registry {
        val statusBarColor: Int
        val navigationBarColor: Int
        var isDarkStatusBar = false
        when {
            rbWhiteStyle.isChecked -> {
                isDarkStatusBar = true
                statusBarColor = Color.parseColor("#FFFFFF")
                navigationBarColor = Color.parseColor("#FFFFFF")
                previewModel.inflateCustomLayout(
                    LayoutSource.SELECTOR_EXTERNAL_PREVIEW,
                    R.layout.ps_fragment_white_external_preview
                )
            }
            rbNumNewStyle.isChecked -> {
                statusBarColor = Color.parseColor("#393a3e")
                navigationBarColor = Color.parseColor("#393a3e")
            }
            else -> {
                statusBarColor = Color.parseColor("#393a3e")
                navigationBarColor = Color.parseColor("#393a3e")
            }
        }
        val selectorStyle = SelectorStyle()
        selectorStyle.getStatusBar().of(isDarkStatusBar, statusBarColor, navigationBarColor)
        if (rbDefaultWindowAnim.isChecked) {
            selectorStyle.getWindowAnimation().of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
        } else if (rbWindowUpAnim.isChecked) {
            selectorStyle.getWindowAnimation().of(R.anim.ps_anim_up_in, R.anim.ps_anim_down_out)
        }
        previewModel.setSelectorUIStyle(selectorStyle)
        return registry
    }

    /**
     * 选择器风格样式
     */
    private fun buildGalleryStyle(registry: Registry, mainModel: SelectionMainModel) {
        val statusBarColor: Int
        val navigationBarColor: Int
        var isDarkStatusBar = false
        when {
            rbWhiteStyle.isChecked -> {
                isDarkStatusBar = true
                statusBarColor = Color.parseColor("#FFFFFF")
                navigationBarColor = Color.parseColor("#FFFFFF")
                mainModel.inflateCustomLayout(
                    LayoutSource.SELECTOR_MAIN,
                    R.layout.ps_fragment_white_selector
                )
                mainModel.inflateCustomLayout(
                    LayoutSource.SELECTOR_PREVIEW,
                    R.layout.ps_fragment_white_preview
                )
                mainModel.inflateCustomLayout(
                    LayoutSource.SELECTOR_EXTERNAL_PREVIEW,
                    R.layout.ps_fragment_white_external_preview
                )
            }
            rbNumNewStyle.isChecked -> {
                statusBarColor = Color.parseColor("#393a3e")
                navigationBarColor = Color.parseColor("#393a3e")
                registry.register(SelectorNumberMainFragment::class.java)
                registry.register(SelectorNumberPreviewFragment::class.java)
            }
            else -> {
                statusBarColor = Color.parseColor("#393a3e")
                navigationBarColor = Color.parseColor("#393a3e")
            }
        }
        val selectorStyle = SelectorStyle()
        selectorStyle.getStatusBar().of(isDarkStatusBar, statusBarColor, navigationBarColor)
        if (rbDefaultWindowAnim.isChecked) {
            selectorStyle.getWindowAnimation().of(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
        } else if (rbWindowUpAnim.isChecked) {
            selectorStyle.getWindowAnimation().of(R.anim.ps_anim_up_in, R.anim.ps_anim_down_out)
        }
        mainModel.setSelectorUIStyle(selectorStyle)
    }

    /**
     * 自定义视频播放器引擎的VideoHolder
     */
    private fun buildCustomPlayer(registry: Registry) {
        when {
            rbExoPlayer.isChecked -> {
                registry.register(CustomPreviewExoVideoHolder::class.java)
            }
            rbIjkPlayer.isChecked -> {
                registry.register(CustomPreviewIjkVideoHolder::class.java)
            }
            else -> {
                registry.register(PreviewVideoHolder::class.java)
            }
        }
    }
}