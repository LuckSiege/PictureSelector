package com.luck.picture.lib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
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
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.constant.CropWrap
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.magical.MagicalView
import com.luck.picture.lib.magical.OnMagicalViewListener
import com.luck.picture.lib.magical.RecycleItemViewParams
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.FileUtils
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils.isP
import com.luck.picture.lib.utils.SelectorLogUtils
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
        return viewModel.config.layoutSource[LayoutSource.SELECTOR_PREVIEW]
            ?: R.layout.ps_fragment_preview
    }

    var screenWidth = 0
    var screenHeight = 0

    var mStatusBar: View? = null
    var mTitleBarBackground: View? = null
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
    var mBottomNarBarBackground: View? = null

    var titleViews: MutableList<View> = mutableListOf()
    var navBarViews: MutableList<View> = mutableListOf()
    var isPause = false
    var isAnimationStart = false
    var isEnableStickResult = true
    var isSaveInstanceState = false

    open fun enableStickResult(): Boolean {
        if (isEnableStickResult) {
            isEnableStickResult = false
            return true
        }
        return isEnableStickResult
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (isHasMagicalEffect()) null else super.onCreateAnimation(transit, enter, nextAnim)
    }

    private fun isHasMagicalEffect(): Boolean {
        val source = viewModel.previewWrap.source
        val media = if (source.size > viewPager.currentItem) source[viewPager.currentItem] else null
        return !MediaUtils.hasMimeTypeOfAudio(media?.mimeType)
                && !viewModel.previewWrap.isBottomPreview
                && viewModel.config.isPreviewZoomEffect
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSaveInstanceState = savedInstanceState != null
        screenWidth = DensityUtil.getRealScreenWidth(requireContext())
        screenHeight = DensityUtil.getScreenHeight(requireContext())
        // TitleBar
        mStatusBar = view.findViewById(R.id.ps_status_bar)
        mTitleBarBackground = view.findViewById(R.id.ps_title_bar_bg)
        mIvLeftBack = view.findViewById(R.id.ps_iv_left_back)
        mTvTitle = view.findViewById(R.id.ps_tv_title)
        mTvSelected = view.findViewById(R.id.ps_tv_selected)
        addTitleBarViewGroup(mStatusBar, mTitleBarBackground, mIvLeftBack, mTvTitle, mTvSelected)
        setStatusBarRectSize(mStatusBar, mTitleBarBackground)
        // BottomNarBar
        mBottomNarBarBackground = view.findViewById(R.id.ps_bottom_nar_bar_bg)
        mTvEditor = view.findViewById(R.id.ps_tv_editor)
        mTvOriginal = view.findViewById(R.id.ps_tv_original)
        mTvComplete = view.findViewById(R.id.ps_tv_complete)
        mTvSelectNum = view.findViewById(R.id.ps_tv_select_num)
        isEnableStickResult = globalViewMode.selectResult.isNotEmpty()
        addNarBarViewGroup(
            mBottomNarBarBackground,
            mTvEditor,
            mTvOriginal,
            mTvSelectNum,
            mTvComplete
        )

        // MagicalView
        mMagicalView = view.findViewById(R.id.magical)
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
        globalViewMode.selectResultLiveData.observe(viewLifecycleOwner) { change ->
            if (enableStickResult()) {
                return@observe
            }
            onSelectionResultChange(change)
        }
        globalViewMode.originalLiveData.observe(viewLifecycleOwner) { isOriginal ->
            onOriginalChange(isOriginal)
        }
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { result ->
            onMediaSourceChange(result)
        }
    }

    /**
     * TitleBar Child View
     */
    open fun addTitleBarViewGroup(vararg viewArray: View?) {
        viewArray.forEach { item ->
            item?.let { view ->
                titleViews.add(view)
            }
        }
    }

    /**
     * Bottom NarBar Child View
     */
    open fun addNarBarViewGroup(vararg viewArray: View?) {
        viewArray.forEach { item ->
            item?.let { view ->
                navBarViews.add(view)
            }
        }
    }

    open fun initViews(view: View) {

    }

    open fun initWidgets() {

    }

    open fun attachPreview() {
        viewModel.previewWrap = viewModel.config.previewWrap.copy()
        viewModel.page = viewModel.previewWrap.page
        viewModel.config.previewWrap.source.clear()
    }


    open fun initTitleBar() {
        setTitleText(viewModel.previewWrap.position + 1)
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
                viewModel.previewWrap.totalCount
            )
    }

    open fun initNavbarBar() {
        if (viewModel.config.selectionMode == SelectionMode.ONLY_SINGLE) {
            navBarViews.forEach { view ->
                view.visibility = View.GONE
            }
        } else {
            if (viewModel.config.isOnlyCamera) {
                globalViewMode.isOriginal = viewModel.config.isOriginalControl
            } else {
                mTvOriginal?.visibility =
                    if (viewModel.config.isOriginalControl) View.VISIBLE else View.GONE
                mTvOriginal?.setOnClickListener { tvOriginal ->
                    onOriginalClick(tvOriginal)
                }
            }
            mTvSelectNum?.setOnClickListener {
                mTvComplete?.performClick()
            }
            mTvComplete?.setOnClickListener {
                onCompleteClick(it)
            }
            val media = viewModel.previewWrap.source[viewModel.previewWrap.position]
            mTvEditor?.visibility =
                if (MediaUtils.hasMimeTypeOfImage(media.mimeType) && viewModel.config.mListenerInfo.onEditorMediaListener != null) View.VISIBLE else View.GONE
            mTvEditor?.setOnClickListener {
                onEditorClick(it)
            }
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
        globalViewMode.originalLiveData.value = !v.isSelected
    }

    open fun onOriginalChange(isOriginal: Boolean) {
        mTvOriginal?.isSelected = isOriginal
        globalViewMode.isOriginal = isOriginal
    }

    open fun onSelectedClick(v: View) {
        val media = viewModel.previewWrap.source[viewPager.currentItem]
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
        if (viewModel.config.selectionMode == SelectionMode.ONLY_SINGLE) {
            handleSelectResult()
        }
    }

    open fun onFirstViewAttachedToWindow(holder: BasePreviewMediaHolder) {
        if (isHasMagicalEffect()) {
            startZoomEffect(holder, viewModel.previewWrap.source[viewModel.previewWrap.position])
        }
    }

    open fun onCompleteClick(v: View) {
        handleSelectResult()
    }

    open fun onEditorClick(v: View) {
        viewModel.config.mListenerInfo.onEditorMediaListener?.onEditorMedia(
            this,
            viewModel.previewWrap.source[viewPager.currentItem],
            SelectorConstant.REQUEST_EDITOR_CROP
        )
    }

    open fun onPreviewItemClick(media: LocalMedia) {
        if (viewModel.config.isPreviewFullScreenMode) {
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
        mTvComplete?.setDataStyle(viewModel.config, globalViewMode.selectResult)
        mTvSelectNum?.visibility =
            if (globalViewMode.selectResult.isNotEmpty()) View.VISIBLE else View.GONE
        mTvSelectNum?.text = globalViewMode.selectResult.size.toString()

        var totalSize: Long = 0
        globalViewMode.selectResult.forEach { media ->
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

    open fun setStatusBarRectSize(statusBarRectView: View?, titleBar: View?) {
        if (viewModel.config.isPreviewFullScreenMode) {
            if (statusBarRectView?.background != null) {
                statusBarRectView.setBackgroundColor((statusBarRectView.background as ColorDrawable).color)
            } else {
                statusBarRectView?.setBackgroundColor((titleBar?.background as ColorDrawable).color)
            }
            statusBarRectView?.layoutParams?.height =
                DensityUtil.getStatusBarHeight(requireContext())
            statusBarRectView?.visibility = View.VISIBLE
        } else {
            statusBarRectView?.layoutParams?.height = 0
            statusBarRectView?.visibility = View.GONE
        }
    }


    /**
     * Users can implement custom preview adapter
     */
    open fun createMediaAdapter(): MediaPreviewAdapter {
        val adapterClass =
            viewModel.config.registry.get(MediaPreviewAdapter::class.java)
        return viewModel.factory.create(adapterClass)
    }

    open fun initViewPagerData() {
        mAdapter = createMediaAdapter()
        mAdapter.setDataNotifyChanged(viewModel.previewWrap.source)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = mAdapter
        val context = requireContext()
        val marginPageTransformer = MarginPageTransformer(DensityUtil.dip2px(context, 3F))
        viewPager.setPageTransformer(marginPageTransformer)
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        viewPager.setCurrentItem(viewModel.previewWrap.position, false)
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
            setTitleText(viewModel.previewWrap.position + 1)
        } else {
            mTvTitle?.text = title
        }
    }

    open fun onMediaSourceChange(result: MutableList<LocalMedia>) {
        val oldStartPosition: Int = viewModel.previewWrap.source.size
        viewModel.previewWrap.source.addAll(result.toMutableList())
        val itemCount: Int = viewModel.previewWrap.source.size
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
                RecycleItemViewParams.getItemViewParams(if (viewModel.previewWrap.isDisplayCamera) viewPager.currentItem + 1 else viewPager.currentItem)
            if (viewParams == null || width == 0 && height == 0) {
                mMagicalView?.startNormal(width, height, false)
                mMagicalView?.setBackgroundAlpha(1F)
                navBarViews.forEach { v ->
                    v.alpha = 1F
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
                val media = viewModel.previewWrap.source[position]
                val mediaSize = getMediaRealSizeFromMedia(media)
                val width = mediaSize[0]
                val height = mediaSize[1]
                mMagicalView?.changeRealScreenHeight(width, height, true)
                val viewParams =
                    RecycleItemViewParams.getItemViewParams(if (viewModel.previewWrap.isDisplayCamera) position + 1 else position)
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
                    val context = requireContext()
                    MediaUtils.getMediaInfo(
                        context,
                        context.contentResolver,
                        media.mimeType,
                        realPath
                    ).let {
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
        if (media.isCrop() && media.cropWidth > 0 && media.cropHeight > 0) {
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
        if (viewModel.previewWrap.source.size > position) {
            val currentMedia: LocalMedia =
                if (positionOffsetPixels < screenWidth / 2) viewModel.previewWrap.source[position]
                else viewModel.previewWrap.source[position + 1]
            mTvSelected?.isSelected =
                globalViewMode.selectResult.contains(currentMedia)
        }
    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not necessarily complete.
     * Params:
     * position – Position index of the new selected page.
     */
    open fun onViewPageSelected(position: Int) {
        viewModel.previewWrap.position = position
        setTitleText(position + 1)
        setMagicalViewParams(position)
        if (isLoadMore(position)) {
            loadMediaMore()
        }
        if (viewModel.config.isAutoPlay) {
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

    /**
     * Load more thresholds
     */
    open fun isLoadMore(position: Int): Boolean {
        if (!viewModel.previewWrap.isBottomPreview && !viewModel.config.isOnlySandboxDir && !viewModel.previewWrap.isExternalPreview) {
            return position == (mAdapter.itemCount - 1) - 10 || position == mAdapter.itemCount - 1
        }
        return false
    }

    /**
     * Load more
     */
    open fun loadMediaMore() {
        viewModel.loadMediaMore(viewModel.previewWrap.bucketId)
        SelectorLogUtils.info("预览:开始请求第${viewModel.page}页数据")
    }

    open fun initMagicalView() {
        viewPager = ViewPager2(requireContext())
        mMagicalView?.setMagicalContent(viewPager)
        if (isHasMagicalEffect()) {
            val alpha = if (isSaveInstanceState) 1F else 0F
            mMagicalView?.setBackgroundAlpha(alpha)
            navBarViews.forEach { v ->
                v.alpha = alpha
            }
        } else {
            mMagicalView?.setBackgroundAlpha(1.0F)
        }
        if (viewModel.config.selectorMode == SelectorMode.AUDIO || (viewModel.previewWrap.source.isNotEmpty() && MediaUtils.hasMimeTypeOfAudio(
                viewModel.previewWrap.source.first().mimeType
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
        }
    }

    open fun onMojitoBeginAnimComplete(mojitoView: MagicalView?, showImmediately: Boolean) {
        val currentHolder = mAdapter.getCurrentViewHolder(viewPager.currentItem) ?: return
        val media = viewModel.previewWrap.source[viewPager.currentItem]
        val isResetSize = media.isCrop() && media.cropWidth > 0 && media.cropHeight > 0
        val realWidth = if (isResetSize) media.cropWidth else media.width
        val realHeight = if (isResetSize) media.cropHeight else media.height
        if (MediaUtils.isLongImage(realWidth, realHeight)) {
            currentHolder.imageCover.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            currentHolder.imageCover.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        if (viewModel.config.isAutoPlay) {
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
        navBarViews.forEach { v ->
            v.alpha = alpha
        }
    }

    open fun onMojitoMagicalViewFinish() {
        onBackPressed()
    }

    open fun onMojitoBeginBackMinFinish(isResetSize: Boolean) {
        val itemViewParams =
            RecycleItemViewParams.getItemViewParams(
                if (viewModel.previewWrap.isDisplayCamera) viewPager.currentItem + 1
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
                    currentViewHolder.ivPlay.performClick()
                }
            }
            return@addIdleHandler false
        }
    }

    open fun isFullScreen(): Boolean {
        return mTitleBarBackground?.translationY != 0F
    }

    open fun previewFullScreenMode() {
        if (isAnimationStart) {
            return
        }
        val viewAnimSet = AnimatorSet()
        val isInitTitleBar = !isFullScreen()

        val alphaFrom: Float = if (isInitTitleBar) 1F else 0F
        val alphaTo = if (isInitTitleBar) 0F else 1F

        // begin statusBar Rect alpha Animator
        val statusBarAlpha = ObjectAnimator.ofFloat(mStatusBar, "alpha", alphaFrom, alphaTo)
        statusBarAlpha.duration = if (isInitTitleBar) 50 else 220

        // begin titleBar translationY Animator
        val titleBarHeight = mTitleBarBackground?.height?.toFloat() ?: 0F
        val statusBarRectHeight =
            if (viewModel.config.isPreviewFullScreenMode) mStatusBar?.measuredHeight ?: 0 else 0
        val titleBarFrom: Float = if (isInitTitleBar) 0F else -titleBarHeight
        val titleBarTo = if (isInitTitleBar) -(titleBarHeight + statusBarRectHeight) else 0F
        titleViews.forEach { v ->
            val translationY = ObjectAnimator.ofFloat(v, "translationY", titleBarFrom, titleBarTo)
            translationY.duration = 300
            val play = viewAnimSet.play(translationY)
            if (isInitTitleBar) {
                play.before(statusBarAlpha)
            } else {
                play.after(statusBarAlpha)
            }
        }
        // begin NavBar alpha Animator
        navBarViews.forEach { v ->
            val navBarAlpha = ObjectAnimator.ofFloat(v, "alpha", alphaFrom, alphaTo)
            navBarAlpha.duration = 350
            viewAnimSet.play(navBarAlpha)
        }
        viewAnimSet.start()
        isAnimationStart = true
        viewAnimSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                viewAnimSet.removeListener(this)
                isAnimationStart = false
                if (isP() && isAdded) {
                    val window = requireActivity().window
                    val lp = window.attributes
                    if (isInitTitleBar) {
                        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                        lp.layoutInDisplayCutoutMode =
                            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        window.attributes = lp
                        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                    } else {
                        lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                        window.attributes = lp
                        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                    }
                }
            }
        })

        if (isInitTitleBar) {
            showFullScreenStatusBar()
        } else {
            hideFullScreenStatusBar()
        }
    }

    open fun showFullScreenStatusBar() {
        titleViews.forEach { v ->
            v.isEnabled = false
        }
        navBarViews.forEach { v ->
            v.isEnabled = false
        }
    }

    open fun hideFullScreenStatusBar() {
        titleViews.forEach { v ->
            v.isEnabled = true
        }
        navBarViews.forEach { v ->
            v.isEnabled = true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.viewModelScope.launch {
            if (isHasMagicalEffect() && viewModel.previewWrap.source.size > viewPager.currentItem) {
                val media = viewModel.previewWrap.source[viewPager.currentItem]
                val mediaRealSize = getMediaRealSizeFromMedia(media)
                changeViewParams(mediaRealSize)
            }
        }
    }

    open fun changeViewParams(size: IntArray) {
        val viewParams =
            RecycleItemViewParams.getItemViewParams(if (viewModel.previewWrap.isDisplayCamera) viewPager.currentItem + 1 else viewPager.currentItem)
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
        val media = viewModel.previewWrap.source[viewPager.currentItem]
        val outputUri = if (data?.hasExtra(CropWrap.CROP_OUTPUT_URI) == true) {
            data.getParcelableExtra(CropWrap.CROP_OUTPUT_URI)
        } else {
            data?.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        }
        media.editorPath = if (MediaUtils.isContent(outputUri.toString())) {
            outputUri.toString()
        } else {
            outputUri?.path
        }
        media.editorData = data?.getStringExtra(CropWrap.DEFAULT_EXTRA_DATA)
        if (!globalViewMode.selectResult.contains(media)) {
            mTvSelected?.performClick()
        }
        mAdapter.notifyItemChanged(viewPager.currentItem)
        globalViewMode.editorLiveData.value = media
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