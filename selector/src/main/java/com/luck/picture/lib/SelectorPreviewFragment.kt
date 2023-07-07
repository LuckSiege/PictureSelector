package com.luck.picture.lib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.adapter.PreviewAudioHolder
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.base.BaseSelectorFragment
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.constant.CropWrap
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.entity.PreviewDataWrap
import com.luck.picture.lib.magical.MagicalView
import com.luck.picture.lib.magical.OnMagicalViewListener
import com.luck.picture.lib.magical.RecycleItemViewParams
import com.luck.picture.lib.provider.TempDataProvider
import com.luck.picture.lib.utils.*
import com.luck.picture.lib.widget.StyleTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：SelectorPreviewFragment
 */
open class SelectorPreviewFragment : BaseSelectorFragment() {
    override fun getFragmentTag(): String {
        return SelectorPreviewFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return config.layoutSource[LayoutSource.SELECTOR_PREVIEW]
            ?: R.layout.ps_fragment_preview
    }

    var screenWidth = 0
    var screenHeight = 0

    var mStatusBar: View? = null
    var mTitleBar: ViewGroup? = null
    var mIvLeftBack: ImageView? = null
    var mTvTitle: TextView? = null
    var mMagicalView: MagicalView? = null
    lateinit var viewPager: ViewPager2
    lateinit var mAdapter: MediaPreviewAdapter

    var mTvEditor: TextView? = null
    var mTvOriginal: TextView? = null
    var mTvSelected: TextView? = null
    var mTvComplete: StyleTextView? = null
    var mTvSelectNum: TextView? = null
    var mBottomNarBar: ViewGroup? = null
    var titleViews: MutableList<View> = mutableListOf()
    var navBarViews: MutableList<View> = mutableListOf()
    var isPause = false
    var isAnimationStart = false
    var isPlayPageSelected = false

    open fun getCurrentAlbum(): LocalMediaAlbum {
        return TempDataProvider.getInstance().currentMediaAlbum
    }

    open fun getPreviewWrap(): PreviewDataWrap {
        return TempDataProvider.getInstance().previewWrap
    }

    private fun setPreviewWrap(previewWrap: PreviewDataWrap) {
        TempDataProvider.getInstance().previewWrap = previewWrap
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (isHasMagicalEffect()) null else super.onCreateAnimation(transit, enter, nextAnim)
    }

    private fun isHasMagicalEffect(): Boolean {
        val source = getPreviewWrap().source
        val media = if (source.size > viewPager.currentItem) source[viewPager.currentItem] else null
        return !MediaUtils.hasMimeTypeOfAudio(media?.mimeType)
                && !getPreviewWrap().isBottomPreview
                && config.isPreviewZoomEffect
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenWidth = DensityUtil.getRealScreenWidth(requireContext())
        screenHeight = DensityUtil.getScreenHeight(requireContext())
        initViews(view)
        attachPreview()
        initTitleBar()
        initNavbarBar()
        initMagicalView()
        initViewPagerData()
        registerLiveData()
        initWidgets()
    }

    private fun registerLiveData() {
        globalViewMode.getSelectResultLiveData().observe(viewLifecycleOwner) { change ->
            onSelectionResultChange(change)
        }
        globalViewMode.getOriginalLiveData().observe(viewLifecycleOwner) { isOriginal ->
            onOriginalChange(isOriginal)
        }
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { result ->
            onMediaSourceChange(result)
        }
    }

    open fun initViews(view: View) {
        // TitleBar
        mStatusBar = view.findViewById(R.id.ps_status_bar)
        mTitleBar = view.findViewById(R.id.ps_title_bar)
        mIvLeftBack = view.findViewById(R.id.ps_iv_left_back)
        mTvTitle = view.findViewById(R.id.ps_tv_title)
        mTvSelected = view.findViewById(R.id.ps_tv_selected)
        setStatusBarRectSize(mStatusBar)
        mTitleBar?.let {
            titleViews.add(it)
        }
        // BottomNarBar
        mBottomNarBar = view.findViewById(R.id.ps_bottom_nar_bar)
        mTvEditor = view.findViewById(R.id.ps_tv_editor)
        mTvOriginal = view.findViewById(R.id.ps_tv_original)
        mTvComplete = view.findViewById(R.id.ps_tv_complete)
        mTvSelectNum = view.findViewById(R.id.ps_tv_select_num)
        mBottomNarBar?.let {
            navBarViews.add(it)
        }

        // MagicalView
        mMagicalView = view.findViewById(R.id.magical)
    }

    open fun initWidgets() {

    }

    open fun attachPreview() {
        if (config.previewWrap.source.isNotEmpty()) {
            setPreviewWrap(config.previewWrap.copy())
            viewModel.page = getPreviewWrap().page
            config.previewWrap.source.clear()
        }
    }

    open fun setStatusBarRectSize(statusBarRectView: View?) {
        if (config.isPreviewFullScreenMode) {
            statusBarRectView?.layoutParams?.height =
                DensityUtil.getStatusBarHeight(requireContext())
            statusBarRectView?.visibility = View.VISIBLE
        } else {
            statusBarRectView?.layoutParams?.height = 0
            statusBarRectView?.visibility = View.GONE
        }
    }

    open fun initTitleBar() {
        setTitleText(getPreviewWrap().position + 1)
        mIvLeftBack?.setOnClickListener {
            onBackClick(it)
        }
        mTvSelected?.setOnClickListener {
            onSelectedClick(it)
        }
    }

    open fun startSelectedAnim(selectedView: View) {
        selectedView.startAnimation(
            AnimationUtils.loadAnimation(selectedView.context, R.anim.ps_anim_modal_in)
        )
    }

    open fun setTitleText(position: Int) {
        mTvTitle?.text =
            requireContext().getString(
                R.string.ps_preview_image_num,
                position,
                getPreviewWrap().totalCount
            )
    }

    open fun initNavbarBar() {
        val media = getPreviewWrap().source[getPreviewWrap().position]
        mTvEditor?.visibility =
            if (!MediaUtils.hasMimeTypeOfAudio(media.mimeType) && config.mListenerInfo.onEditorMediaListener != null) View.VISIBLE else View.GONE
        mTvEditor?.setOnClickListener {
            onEditorClick(it)
        }
        mTvOriginal?.visibility =
            if (config.isOriginalControl) View.VISIBLE else View.GONE
        mTvOriginal?.setOnClickListener { tvOriginal ->
            onOriginalClick(tvOriginal)
        }
        mTvSelectNum?.setOnClickListener {
            mTvComplete?.performClick()
        }
        mTvComplete?.setOnClickListener {
            onCompleteClick(it)
        }
    }


    open fun onBackClick(v: View) {
        if (isHasMagicalEffect()) {
            mMagicalView?.backToMin()
        } else {
            onBackPressed()
        }
    }

    open fun onOriginalClick(v: View) {
        globalViewMode.setOriginalLiveData(!v.isSelected)
    }

    open fun onOriginalChange(isOriginal: Boolean) {
        mTvOriginal?.isSelected = isOriginal
    }

    open fun onSelectedClick(v: View) {
        val media = getPreviewWrap().source[viewPager.currentItem]
        val resultCode =
            confirmSelect(media, v.isSelected)
        if (resultCode == SelectedState.INVALID) {
            return
        }
        val isSelected = resultCode == SelectedState.SUCCESS
        if (isSelected) {
            startSelectedAnim(v)
        }
        v.isSelected = isSelected
        if (config.selectionMode == SelectionMode.ONLY_SINGLE) {
            handleSelectResult()
        }
    }

    open fun onFirstViewAttachedToWindow(holder: BasePreviewMediaHolder) {
        if (isSavedInstanceState) {
            return
        }
        if (isHasMagicalEffect()) {
            startZoomEffect(
                holder,
                getPreviewWrap().source[getPreviewWrap().position]
            )
        }
    }

    open fun onCompleteClick(v: View) {
        handleSelectResult()
    }

    open fun onEditorClick(v: View) {
        config.mListenerInfo.onEditorMediaListener?.onEditorMedia(
            this,
            getPreviewWrap().source[viewPager.currentItem],
            SelectorConstant.REQUEST_EDITOR_CROP
        )
    }

    open fun onPreviewItemClick(media: LocalMedia) {
        if (config.isPreviewFullScreenMode) {
            previewFullScreenMode()
        } else {
            if (isHasMagicalEffect()) {
                mMagicalView?.backToMin()
            } else {
                onBackPressed()
            }
        }
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
        mTvComplete?.setDataStyle(config, getSelectResult())
        mTvSelectNum?.visibility =
            if (getSelectResult().isNotEmpty()) View.VISIBLE else View.GONE
        mTvSelectNum?.text = getSelectResult().size.toString()

        var totalSize: Long = 0
        getSelectResult().forEach { media ->
            totalSize += media.size
        }
        if (totalSize > 0) {
            mTvOriginal?.text = getString(
                R.string.ps_original_image,
                FileUtils.formatAccurateUnitFileSize(totalSize)
            )
        } else {
            mTvOriginal?.text = getString(R.string.ps_default_original_image)
        }
    }

    override fun onKeyBackAction() {
        when {
            isFullScreen() -> {
                previewFullScreenMode()
            }
            isHasMagicalEffect() -> {
                mMagicalView?.backToMin()
            }
            else -> {
                onBackPressed()
            }
        }
    }


    /**
     * Users can implement custom preview adapter
     */
    open fun createMediaAdapter(): MediaPreviewAdapter {
        val adapterClass =
            config.registry.get(MediaPreviewAdapter::class.java)
        return factory.create(adapterClass)
    }

    open fun initViewPagerData() {
        mAdapter = createMediaAdapter()
        mAdapter.setDataNotifyChanged(getPreviewWrap().source)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = mAdapter
        val context = requireContext()
        val marginPageTransformer = MarginPageTransformer(DensityUtil.dip2px(context, 3F))
        viewPager.setPageTransformer(marginPageTransformer)
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        viewPager.setCurrentItem(getPreviewWrap().position, false)
        onSelectionResultChange(null)
        mAdapter.setOnFirstAttachedToWindowListener(object :
            MediaPreviewAdapter.OnAttachedToWindowListener {
            override fun onViewAttachedToWindow(holder: BasePreviewMediaHolder) {
                onFirstViewAttachedToWindow(holder)
            }
        })
        mAdapter.setOnClickListener(object : MediaPreviewAdapter.OnClickListener {
            override fun onClick(media: LocalMedia) {
                onPreviewItemClick(media)
            }
        })
        mAdapter.setOnTitleChangeListener(object : MediaPreviewAdapter.OnTitleChangeListener {
            override fun onTitle(title: String?) {
                onTitleChange(title)
            }
        })
    }

    open fun onTitleChange(title: String?) {
        if (TextUtils.isEmpty(title)) {
            setTitleText(getPreviewWrap().position + 1)
        } else {
            mTvTitle?.text = title
        }
    }

    open fun onMediaSourceChange(result: MutableList<LocalMedia>) {
        val oldStartPosition: Int = getPreviewWrap().source.size
        getPreviewWrap().source.addAll(result.toMutableList())
        val itemCount: Int = getPreviewWrap().source.size
        mAdapter.notifyItemRangeChanged(oldStartPosition, itemCount)
        SelectorLogUtils.info("预览:第${viewModel.page}页数据,现有数据->${mAdapter.getData().size}条")
    }

    open fun startZoomEffect(holder: BasePreviewMediaHolder, media: LocalMedia) {
        viewPager.alpha = 0F
        holder.imageCover.scaleType =
            if (media.width == 0 && media.height == 0) ImageView.ScaleType.FIT_CENTER else ImageView.ScaleType.CENTER_CROP
        viewModel.viewModelScope.launch {
            val mediaRealSize = getMediaRealSizeFromMedia(media)
            val width = mediaRealSize[0]
            val height = mediaRealSize[1]
            mMagicalView?.changeRealScreenHeight(width, height, false)
            val viewParams =
                RecycleItemViewParams.getItemViewParams(if (getPreviewWrap().isDisplayCamera) viewPager.currentItem + 1 else viewPager.currentItem)
            if (viewParams == null || width == 0 && height == 0) {
                mMagicalView?.startNormal(width, height, false)
                mMagicalView?.setBackgroundAlpha(1F)
                navBarViews.forEach {
                    it.alpha = 1F
                }
            } else {
                mMagicalView?.setViewParams(
                    viewParams.left,
                    viewParams.top,
                    viewParams.width,
                    viewParams.height,
                    width,
                    height
                )
                mMagicalView?.start(false)
            }
            val objectAnimator = ObjectAnimator.ofFloat(viewPager, "alpha", 0F, 1F)
            objectAnimator.duration = 50
            objectAnimator.start()
        }
    }


    open fun setMagicalViewParams(position: Int) {
        if (isHasMagicalEffect()) {
            viewModel.viewModelScope.launch {
                val media = getPreviewWrap().source[position]
                val mediaSize = getMediaRealSizeFromMedia(media)
                val width = mediaSize[0]
                val height = mediaSize[1]
                mMagicalView?.changeRealScreenHeight(width, height, true)
                val viewParams =
                    RecycleItemViewParams.getItemViewParams(if (getPreviewWrap().isDisplayCamera) position + 1 else position)
                if (viewParams == null || width == 0 || height == 0) {
                    mMagicalView?.setViewParams(0, 0, 0, 0, width, height)
                } else {
                    mMagicalView?.setViewParams(
                        viewParams.left,
                        viewParams.top,
                        viewParams.width,
                        viewParams.height,
                        width,
                        height
                    )
                }
            }
        }
    }


    private suspend fun getMediaRealSizeFromMedia(media: LocalMedia): IntArray {
        var realWidth = media.width
        var realHeight = media.height
        if (MediaUtils.isLongImage(realWidth, realHeight)) {
            return intArrayOf(screenWidth, screenHeight)
        }
        if (MediaUtils.hasMimeTypeOfAudio(media.mimeType)) {
            return intArrayOf(realWidth, realHeight)
        }
        if ((realWidth <= 0 || realHeight <= 0) || (realWidth > realHeight)) {
            withContext(Dispatchers.IO) {
                media.absolutePath?.let { realPath ->
                    MediaUtils.getMediaInfo(requireContext(), media.mimeType, realPath).let {
                        if (it.width > 0) {
                            realWidth = it.width
                        }
                        if (it.height > 0) {
                            realHeight = it.height
                        }
                    }
                }
            }
        }
        if ((media.isCrop() || media.isEditor()) && media.cropWidth > 0 && media.cropHeight > 0) {
            realWidth = media.cropWidth
            realHeight = media.cropHeight
        }
        return intArrayOf(realWidth, realHeight)
    }

    private val pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                onViewPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                onViewPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onViewPageSelected(position)
            }
        }

    /**
     * Called when the scroll state changes. Useful for discovering when the user begins dragging,
     * when a fake drag is started, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle. state can be one of SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING or SCROLL_STATE_SETTLING.
     */
    open fun onViewPageScrollStateChanged(state: Int) {

    }

    /**
     * This method will be invoked when the current page is scrolled, either as part of a programmatically initiated smooth scroll or a user initiated touch scroll.
     * Params:
     * position – Position index of the first page currently being displayed. Page position+1 will be visible if positionOffset is nonzero.
     * positionOffset – Value from [0, 1) indicating the offset from the page at position.
     * positionOffsetPixels – Value in pixels indicating the offset from position.
     */
    open fun onViewPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        if (getPreviewWrap().source.size > position) {
            val currentMedia: LocalMedia =
                if (positionOffsetPixels < screenWidth / 2) getPreviewWrap().source[position]
                else getPreviewWrap().source[position + 1]
            mTvSelected?.isSelected =
                getSelectResult().contains(currentMedia)
        }
    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not necessarily complete.
     * Params:
     * position – Position index of the new selected page.
     */
    open fun onViewPageSelected(position: Int) {
        getPreviewWrap().position = position
        setTitleText(position + 1)
        setMagicalViewParams(position)
        if (isLoadMoreThreshold(position)) {
            loadMediaMore()
        }
        if (isPlayPageSelected) {
            if (config.isAutoPlay) {
                autoPlayAudioAndVideo()
            } else {
                val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem)
                if (currentHolder is PreviewVideoHolder) {
                    if (currentHolder.ivPlay.visibility == View.GONE) {
                        currentHolder.ivPlay.visibility = View.VISIBLE
                    }
                }
            }
        }
        isPlayPageSelected = true
    }

    /**
     * Load more thresholds
     */
    open fun isLoadMoreThreshold(position: Int): Boolean {
        if (getCurrentAlbum().totalCount == mAdapter.getData().size) {
            return false
        }
        if (!getPreviewWrap().isBottomPreview && !config.isOnlySandboxDir && !getPreviewWrap().isExternalPreview) {
            return position == (mAdapter.itemCount - 1) - 10 || position == mAdapter.itemCount - 1
        }
        return false
    }

    /**
     * Load more
     */
    open fun loadMediaMore() {
        viewModel.loadMediaMore(getPreviewWrap().bucketId)
        SelectorLogUtils.info("预览:开始请求第${viewModel.page}页数据")
    }

    open fun initMagicalView() {
        viewPager = ViewPager2(requireContext())
        mMagicalView?.setMagicalContent(viewPager)
        if (isHasMagicalEffect()) {
            val alpha = if (isSavedInstanceState) 1F else 0F
            mMagicalView?.setBackgroundAlpha(alpha)
            navBarViews.forEach {
                it.alpha = alpha
            }
        } else {
            mMagicalView?.setBackgroundAlpha(1.0F)
        }
        if (config.mediaType == MediaType.AUDIO || (getPreviewWrap().source.isNotEmpty() && MediaUtils.hasMimeTypeOfAudio(
                getPreviewWrap().source.first().mimeType
            ))
        ) {
            mMagicalView?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ps_color_white
                )
            )
        } else {
            mMagicalView?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.ps_color_black)
            )
        }

        mMagicalView?.setOnMagicalViewListener(object : OnMagicalViewListener {
            override fun onBeginBackMinAnim() {
                onMojitoBeginBackMinAnim()
            }

            override fun onBeginBackMinMagicalFinish(isResetSize: Boolean) {
                onMojitoBeginBackMinFinish(isResetSize)
            }

            override fun onBeginMagicalAnimComplete(
                mojitoView: MagicalView,
                showImmediately: Boolean
            ) {
                onMojitoBeginAnimComplete(mojitoView, showImmediately)
            }

            override fun onBackgroundAlpha(alpha: Float) {
                onMojitoBackgroundAlpha(alpha)
            }

            override fun onMagicalViewFinish() {
                onMojitoMagicalViewFinish()
            }

        })
    }

    open fun onMojitoBeginBackMinAnim() {
        val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem) ?: return
        if (currentHolder.imageCover.visibility == View.GONE) {
            currentHolder.imageCover.visibility = View.VISIBLE
        }
        if (currentHolder is PreviewVideoHolder) {
            if (currentHolder.ivPlay.visibility == View.VISIBLE) {
                currentHolder.ivPlay.visibility = View.GONE
            }
            currentHolder.controller?.let { controller ->
                if ((controller as View).alpha != 0F) {
                    controller.animate().alpha(0F).setDuration(125).start()
                }
            }
        }
    }

    open fun onMojitoBeginAnimComplete(mojitoView: MagicalView?, showImmediately: Boolean) {
        val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem) ?: return
        val media = getPreviewWrap().source[viewPager.currentItem]
        val isResetSize =
            (media.isCrop() || media.isEditor()) && media.cropWidth > 0 && media.cropHeight > 0
        val realWidth = if (isResetSize) media.cropWidth else media.width
        val realHeight = if (isResetSize) media.cropHeight else media.height
        if (MediaUtils.isLongImage(realWidth, realHeight)) {
            currentHolder.imageCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            currentHolder.imageCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        if (config.isAutoPlay) {
            autoPlayAudioAndVideo()
        } else {
            if (currentHolder is PreviewVideoHolder) {
                if (currentHolder.ivPlay.visibility == View.GONE && !isPlaying()) {
                    currentHolder.ivPlay.visibility = View.VISIBLE
                }
            }
        }
    }

    open fun onMojitoBackgroundAlpha(alpha: Float) {
        mMagicalView?.setBackgroundAlpha(alpha)
        navBarViews.forEach {
            it.alpha = alpha
        }
    }

    open fun onMojitoMagicalViewFinish() {
        onBackPressed()
    }

    open fun onMojitoBeginBackMinFinish(isResetSize: Boolean) {
        val itemViewParams =
            RecycleItemViewParams.getItemViewParams(
                if (getPreviewWrap().isDisplayCamera) viewPager.currentItem + 1
                else viewPager.currentItem
            ) ?: return
        val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem) ?: return
        val layoutParams = currentHolder.imageCover.layoutParams
        layoutParams?.width = itemViewParams.width
        layoutParams?.height = itemViewParams.height
        currentHolder.imageCover.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    open fun autoPlayAudioAndVideo() {
        Looper.myQueue().addIdleHandler {
            val currentViewHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem)
            if (currentViewHolder is PreviewVideoHolder) {
                if (!currentViewHolder.mediaPlayer.isPlaying()) {
                    currentViewHolder.ivPlay.performClick()
                }
            } else if (currentViewHolder is PreviewAudioHolder) {
                if (!currentViewHolder.mediaPlayer.isPlaying()) {
                    currentViewHolder.controller.getViewPlay()?.performClick()
                }
            }
            return@addIdleHandler false
        }
    }

    open fun isFullScreen(): Boolean {
        return mTitleBar?.translationY != 0F
    }

    open fun previewFullScreenMode() {
        if (isAnimationStart) {
            return
        }
        val viewAnimSet = AnimatorSet()
        val isInitTitleBar = !isFullScreen()
        // begin titleBar translationY Animator
        val titleBarHeight = mTitleBar?.height?.toFloat() ?: 0F
        titleViews.forEach { v ->
            viewAnimSet.play(
                ObjectAnimator.ofFloat(
                    v, "translationY",
                    if (isInitTitleBar) 0F else -titleBarHeight,
                    if (isInitTitleBar) -titleBarHeight else 0F
                )
            )
        }
        // begin NavBar alpha Animator
        navBarViews.forEach { v ->
            viewAnimSet.play(
                ObjectAnimator.ofFloat(
                    v,
                    "alpha",
                    if (isInitTitleBar) 1F else 0F,
                    if (isInitTitleBar) 0F else 1F
                )
            )
        }
        viewAnimSet.duration = 350
        viewAnimSet.start()
        isAnimationStart = true
        viewAnimSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                viewAnimSet.removeListener(this)
                isAnimationStart = false
                if (SdkVersionUtils.isP() && isAdded) {
                    showHideStatusBar(isInitTitleBar)
                }
            }
        })
    }

    open fun showHideStatusBar(isInitTitleBar: Boolean) {
        val window = requireActivity().window
        if (isInitTitleBar) {
            // hide
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        } else {
            // show
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.viewModelScope.launch {
            if (isHasMagicalEffect() && getPreviewWrap().source.size > viewPager.currentItem) {
                val media = getPreviewWrap().source[viewPager.currentItem]
                val mediaRealSize = getMediaRealSizeFromMedia(media)
                changeViewParams(mediaRealSize)
            }
        }
    }

    open fun changeViewParams(size: IntArray) {
        val viewParams =
            RecycleItemViewParams.getItemViewParams(if (getPreviewWrap().isDisplayCamera) viewPager.currentItem + 1 else viewPager.currentItem)
        if (viewParams == null || size[0] == 0 || size[1] == 0) {
            mMagicalView?.setViewParams(0, 0, 0, 0, size[0], size[1])
            mMagicalView?.resetStartNormal(size[0], size[1], false)
        } else {
            mMagicalView?.setViewParams(
                viewParams.left,
                viewParams.top,
                viewParams.width,
                viewParams.height,
                size[0],
                size[1]
            )
            mMagicalView?.resetStart()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectorConstant.REQUEST_EDITOR_CROP) {
                onMergeEditorData(data)
            }
        }
    }

    open fun onMergeEditorData(data: Intent?) {
        val media = getPreviewWrap().source[viewPager.currentItem]
        val outputUri = if (data?.hasExtra(CropWrap.CROP_OUTPUT_URI) == true) {
            data.getParcelableExtra(CropWrap.CROP_OUTPUT_URI)
        } else {
            data?.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        }
        media.cropWidth = data?.getIntExtra(CropWrap.CROP_IMAGE_WIDTH, 0) ?: 0
        media.cropHeight = data?.getIntExtra(CropWrap.CROP_IMAGE_HEIGHT, 0) ?: 0
        media.cropOffsetX = data?.getIntExtra(CropWrap.CROP_OFFSET_X, 0) ?: 0
        media.cropOffsetY = data?.getIntExtra(CropWrap.CROP_OFFSET_Y, 0) ?: 0
        media.cropAspectRatio = data?.getFloatExtra(CropWrap.CROP_ASPECT_RATIO, 0F) ?: 0F
        media.editorPath = if (MediaUtils.isContent(outputUri.toString())) {
            outputUri.toString()
        } else {
            outputUri?.path
        }
        media.editorData = data?.getStringExtra(CropWrap.DEFAULT_EXTRA_DATA)
        if (!getSelectResult().contains(media)) {
            mTvSelected?.performClick()
        }
        mAdapter.notifyItemChanged(viewPager.currentItem)
        globalViewMode.setEditorLiveData(media)
    }

    override fun onResume() {
        super.onResume()
        if (isPause) {
            resumePausePlay()
            isPause = false
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying()) {
            resumePausePlay()
            isPause = true
        }
    }

    open fun resumePausePlay() {
        val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem) ?: return
        if (currentHolder is PreviewVideoHolder) {
            if (currentHolder.mediaPlayer.isPlaying()) {
                currentHolder.mediaPlayer.pause()
            } else {
                currentHolder.mediaPlayer.resume()
            }
        }
        if (currentHolder is PreviewAudioHolder) {
            if (currentHolder.mediaPlayer.isPlaying()) {
                currentHolder.mediaPlayer.pause()
            } else {
                currentHolder.mediaPlayer.resume()
            }
        }
    }


    open fun isPlaying(): Boolean {
        val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem)
        if (currentHolder is PreviewVideoHolder) {
            return currentHolder.mediaPlayer.isPlaying()
        }
        if (currentHolder is PreviewAudioHolder) {
            return currentHolder.mediaPlayer.isPlaying()
        }
        return false
    }

    override fun onDestroy() {
        mAdapter.destroy()
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}