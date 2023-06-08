package com.luck.picture.lib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.adapter.MediaListAdapter
import com.luck.picture.lib.adapter.base.BaseMediaListAdapter
import com.luck.picture.lib.base.BaseSelectorFragment
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.SelectionMode
import com.luck.picture.lib.config.SelectorMode
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.dialog.AlbumListPopWindow
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.entity.PreviewDataWrap
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.helper.FragmentInjectManager
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.magical.RecycleItemViewParams
import com.luck.picture.lib.media.PictureMediaScannerConnection
import com.luck.picture.lib.media.ScanListener
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.utils.*
import com.luck.picture.lib.utils.DensityUtil.getStatusBarHeight
import com.luck.picture.lib.utils.FileUtils
import com.luck.picture.lib.widget.*
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelector default template style
 */
open class SelectorMainFragment : BaseSelectorFragment() {
    override fun getFragmentTag(): String {
        return SelectorMainFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return viewModel.config.layoutSource[LayoutSource.SELECTOR_MAIN]
            ?: R.layout.ps_fragment_selector
    }

    /**
     * RecyclerView
     */
    lateinit var mRecycler: RecyclerPreloadView
    var mTvDataEmpty: TextView? = null

    /**
     * TitleBar
     */
    var mStatusBar: View? = null
    var mTitleBarBackground: View? = null
    var mIvLeftBack: ImageView? = null
    var mTvTitle: TextView? = null
    var mIvTitleArrow: ImageView? = null
    var mTvCancel: TextView? = null
    var mTvCurrentDataTime: TextView? = null

    /**
     * BottomNarBar
     */
    var mBottomNarBarBackground: View? = null
    var mTvPreview: StyleTextView? = null
    var mTvOriginal: TextView? = null
    var mTvComplete: StyleTextView? = null
    var mTvSelectNum: TextView? = null

    lateinit var mAlbumWindow: AlbumListPopWindow
    lateinit var mAdapter: BaseMediaListAdapter

    private var intervalClickTime: Long = 0

    private var mDragSelectTouchListener: SlideSelectTouchListener? = null

    var titleViews: MutableList<View> = mutableListOf()
    var navBarViews: MutableList<View> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView
        mRecycler = view.findViewById(R.id.ps_recycler)
        mTvDataEmpty = view.findViewById(R.id.ps_tv_data_empty)
        mTvCurrentDataTime = view.findViewById(R.id.ps_tv_current_data_time)
        setDataEmpty()
        // TitleBar
        mStatusBar = view.findViewById(R.id.ps_status_bar)
        mTitleBarBackground = view.findViewById(R.id.ps_title_bar_bg)
        mIvLeftBack = view.findViewById(R.id.ps_iv_left_back)
        mTvTitle = view.findViewById(R.id.ps_tv_title)
        mIvTitleArrow = view.findViewById(R.id.ps_iv_arrow)
        mTvCancel = view.findViewById(R.id.ps_tv_cancel)
        setStatusBarRectSize(mStatusBar, mTitleBarBackground)
        addTitleBarViewGroup(
            mStatusBar,
            mTitleBarBackground,
            mIvLeftBack,
            mTvTitle,
            mIvTitleArrow,
            mTvCancel
        )
        // BottomNarBar
        mBottomNarBarBackground = view.findViewById(R.id.ps_bottom_nar_bar_bg)
        mTvPreview = view.findViewById(R.id.ps_tv_preview)
        mTvOriginal = view.findViewById(R.id.ps_tv_original)
        mTvComplete = view.findViewById(R.id.ps_tv_complete)
        mTvSelectNum = view.findViewById(R.id.ps_tv_select_num)
        onMergeSelectedSource()
        addNarBarViewGroup(
            mBottomNarBarBackground,
            mTvPreview,
            mTvOriginal,
            mTvComplete,
            mTvSelectNum
        )
        initAlbumWindow()
        initTitleBar()
        initNavbarBar()
        initMediaAdapter()
        checkPermissions()
        registerLiveData()
    }

    open fun setDataEmpty() {
        mTvDataEmpty?.text =
            if (viewModel.config.selectorMode == SelectorMode.AUDIO) getString(R.string.ps_audio_empty) else getString(
                R.string.ps_empty
            )
    }

    open fun onMergeSelectedSource() {
        if (viewModel.config.selectedSource.isNotEmpty()) {
            globalViewMode.selectResult.addAll(viewModel.config.selectedSource.toMutableList())
            viewModel.config.selectedSource.clear()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun registerLiveData() {
        globalViewMode.editorLiveData.observe(viewLifecycleOwner) { media ->
            val position = mAdapter.getData().indexOf(media)
            if (position >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
        }
        globalViewMode.originalLiveData.observe(viewLifecycleOwner) { isOriginal ->
            onOriginalChange(isOriginal)
        }
        globalViewMode.selectResultLiveData.observe(viewLifecycleOwner) { media ->
            val position = mAdapter.getData().indexOf(media)
            if (checkNotifyStrategy(globalViewMode.selectResult.indexOf(media) != -1)) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
                Looper.myQueue().addIdleHandler {
                    mAdapter.notifyDataSetChanged()
                    return@addIdleHandler false
                }
            } else {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
            // update selected tag
            mAlbumWindow.notifyChangedSelectTag(globalViewMode.selectResult)
            onSelectionResultChange(media)
        }
        viewModel.albumLiveData.observe(viewLifecycleOwner) { albumList ->
            Looper.myQueue().addIdleHandler {
                onAlbumSourceChange(albumList)
                return@addIdleHandler false
            }
        }
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { mediaList ->
            onMediaSourceChange(mediaList)
            SelectorLogUtils.info("主页${mediaList.size}")
        }
    }

    open fun initTitleBar() {
        mIvLeftBack?.setOnClickListener {
            onBackClick(it)
        }
        mTvCancel?.setOnClickListener {
            mIvLeftBack?.performClick()
        }
        mTvTitle?.setOnClickListener {
            onShowAlbumWindowAsDropDown()
        }
        mIvTitleArrow?.setOnClickListener {
            mTvTitle?.performClick()
        }
        mTitleBarBackground?.setOnClickListener {
            onTitleBarClick(it)
        }
    }

    open fun onBackClick(v: View) {
        onBackPressed()
    }

    open fun onShowAlbumWindowAsDropDown() {
        if (mAlbumWindow.getAlbumList().isNotEmpty()) {
            mTitleBarBackground?.let {
                mAlbumWindow.showAsDropDown(it)
            }
        }
    }

    open fun onTitleBarClick(v: View) {
        if (SystemClock.uptimeMillis() - intervalClickTime < 500 && mAdapter.getData()
                .isNotEmpty()
        ) {
            if (mAdapter.getData().size > 2 * viewModel.config.pageSize) {
                mRecycler.scrollToPosition(viewModel.config.pageSize)
                mRecycler.post {
                    mRecycler.smoothScrollToPosition(0)
                }
            } else {
                mRecycler.smoothScrollToPosition(0)
            }
        } else {
            intervalClickTime = SystemClock.uptimeMillis()
        }
    }

    open fun onOriginalClick(v: View) {
        globalViewMode.originalLiveData.value = !v.isSelected
    }

    open fun onOriginalChange(isOriginal: Boolean) {
        mTvOriginal?.isSelected = isOriginal
        globalViewMode.isOriginal = isOriginal
    }

    open fun onCompleteClick(v: View) {
        handleSelectResult()
    }

    open fun setStatusBarRectSize(statusBarRectView: View?, titleBar: View?) {
        if (viewModel.config.isPreviewFullScreenMode) {
            if (statusBarRectView?.background != null) {
                statusBarRectView.setBackgroundColor((statusBarRectView.background as ColorDrawable).color)
            } else {
                statusBarRectView?.setBackgroundColor((titleBar?.background as ColorDrawable).color)
            }
            statusBarRectView?.layoutParams?.height =
                getStatusBarHeight(requireContext())
            statusBarRectView?.visibility = View.VISIBLE
        } else {
            statusBarRectView?.layoutParams?.height = 0
            statusBarRectView?.visibility = View.GONE
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

    open fun setCurrentMediaCreateTimeText() {
        if (viewModel.config.isDisplayTimeAxis) {
            val position = mRecycler.getFirstVisiblePosition()
            if (position != RecyclerView.NO_POSITION) {
                val data = mAdapter.getData()
                if (data.size > position && data[position].dateAdded > 0) {
                    mTvCurrentDataTime?.text = DateUtils.getDataFormat(
                        requireContext(),
                        data[position].dateAdded
                    )
                }
            }
        }
    }

    open fun showCurrentMediaCreateTimeUI() {
        if (viewModel.config.isDisplayTimeAxis && mAdapter.getData().size > 0) {
            if (mTvCurrentDataTime?.alpha == 0f) {
                mTvCurrentDataTime?.animate()?.setDuration(150)?.alphaBy(1.0f)?.start()
            }
        }
    }

    open fun hideCurrentMediaCreateTimeUI() {
        if (viewModel.config.isDisplayTimeAxis && mAdapter.getData().size > 0) {
            mTvCurrentDataTime?.animate()?.setDuration(250)?.alpha(0.0f)?.start()
        }
    }

    /**
     * Users can implement a custom album list PopWindow
     */
    open fun createAlbumWindow(): AlbumListPopWindow {
        return AlbumListPopWindow(requireContext())
    }

    open fun initAlbumWindow() {
        mAlbumWindow = createAlbumWindow()
        mAlbumWindow.setOnItemClickListener(object : OnItemClickListener<LocalMediaAlbum> {
            override fun onItemClick(position: Int, data: LocalMediaAlbum) {
                onAlbumItemClick(position, data)
            }
        })
        mAlbumWindow.setOnWindowStatusListener(object : AlbumListPopWindow.OnWindowStatusListener {
            override fun onShowing(isShowing: Boolean) {
                onRotateArrowAnim(isShowing)
            }
        })
    }

    open fun onAlbumItemClick(position: Int, data: LocalMediaAlbum) {
        mAlbumWindow.dismiss()
        val currentMediaAlbum = viewModel.currentMediaAlbum ?: return
        // Repeated clicks ignore
        if (data.isEqualAlbum(currentMediaAlbum.bucketId)) {
            return
        }
        viewModel.currentMediaAlbum = data
        // Cache the current album data before switching to the next album
        mAlbumWindow.getAlbum(currentMediaAlbum.bucketId)?.let {
            it.source = mAdapter.getData().toMutableList()
            it.cachePage = viewModel.page
        }
        // Update current album
        mAdapter.setDisplayCamera(viewModel.config.isDisplayCamera && data.isAllAlbum())
        mTvTitle?.text = data.bucketDisplayName
        if (data.source.isNotEmpty()) {
            // Album already has cached data，Start loading from cached page numbers
            Looper.myQueue().addIdleHandler {
                viewModel.page = data.cachePage
                mAdapter.setDataNotifyChanged(data.source)
                mRecycler.scrollToPosition(0)
                mRecycler.setEnabledLoadMore(!data.isSandboxAlbum() && !viewModel.config.isOnlySandboxDir && data.source.isNotEmpty())
                return@addIdleHandler false
            }
        } else {
            // Never loaded, request data again
            Looper.myQueue().addIdleHandler {
                viewModel.loadMedia()
                return@addIdleHandler false
            }
        }
        if (viewModel.config.isFastSlidingSelect) {
            mDragSelectTouchListener?.setRecyclerViewHeaderCount(if (mAdapter.isDisplayCamera()) 1 else 0)
        }
    }

    open fun onRotateArrowAnim(showing: Boolean) {
        if (!viewModel.config.isOnlySandboxDir) {
            AnimUtils.rotateArrow(mIvTitleArrow, showing)
        }
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
            mTvPreview?.setOnClickListener {
                if (DoubleUtils.isFastDoubleClick()) {
                    return@setOnClickListener
                }
                onStartPreview(0, true, globalViewMode.selectResult)
            }
            mTvSelectNum?.setOnClickListener {
                mTvComplete?.performClick()
            }
            mTvComplete?.setOnClickListener {
                onCompleteClick(it)
            }
        }
    }

    open fun checkNotifyStrategy(isAddRemove: Boolean): Boolean {
        var isNotifyAll = false
        if (viewModel.config.isMaxSelectEnabledMask) {
            val selectResult = globalViewMode.selectResult
            val selectCount = selectResult.size
            if (viewModel.config.isAllWithImageVideo) {
                val maxSelectCount = viewModel.config.getSelectCount()
                if (viewModel.config.selectionMode == SelectionMode.MULTIPLE) {
                    isNotifyAll =
                        selectCount == maxSelectCount || (!isAddRemove && selectCount == maxSelectCount - 1)
                }
            } else {
                isNotifyAll = if (selectCount == 0 ||
                    (if (isAddRemove) viewModel.config.selectorMode == SelectorMode.ALL && selectCount == 1
                    else selectCount == viewModel.config.totalCount - 1)
                ) {
                    true
                } else {
                    if (MediaUtils.hasMimeTypeOfVideo(selectResult.first().mimeType)) {
                        selectResult.size == viewModel.config.maxVideoSelectNum
                    } else {
                        selectResult.size == viewModel.config.totalCount
                    }
                }
            }
        }
        return isNotifyAll
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
        mTvPreview?.setDataStyle(viewModel.config, globalViewMode.selectResult)
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

    /**
     * Users can implement custom RecyclerView related settings
     */
    open fun initRecyclerConfig(recycler: RecyclerPreloadView) {
        recycler.also { view ->
            if (view.itemDecorationCount == 0) {
                view.addItemDecoration(
                    GridSpacingItemDecoration(
                        viewModel.config.imageSpanCount,
                        DensityUtil.dip2px(requireContext(), 1F), false
                    )
                )
            }
            view.layoutManager =
                GridLayoutManager(requireContext(), viewModel.config.imageSpanCount)
            (view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }


    /**
     * Users can implement custom media lists
     */
    open fun createMediaAdapter(): BaseMediaListAdapter {
        val adapterClass =
            viewModel.config.registry.get(MediaListAdapter::class.java)
        return viewModel.factory.create(adapterClass)
    }

    open fun initMediaAdapter() {
        initRecyclerConfig(mRecycler)
        mAdapter = createMediaAdapter()
        mAdapter.setDisplayCamera(viewModel.config.isDisplayCamera)
        mRecycler.adapter = mAdapter
        setFastSlidingSelect()
        onSelectionResultChange(null)
        mRecycler.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD)
        mRecycler.setOnRecyclerViewPreloadListener(object : OnRecyclerViewPreloadMoreListener {
            override fun onPreloadMore() {
                if (mRecycler.isEnabledLoadMore()) {
                    val bucketId = viewModel.currentMediaAlbum?.bucketId ?: return
                    viewModel.loadMediaMore(bucketId)
                    SelectorLogUtils.info("加载第${viewModel.page}页")
                }
            }
        })
        mRecycler.setOnRecyclerViewScrollListener(object : OnRecyclerViewScrollListener {
            override fun onScrolled(dx: Int, dy: Int) {
                setCurrentMediaCreateTimeText()
            }

            override fun onScrollStateChanged(state: Int) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
                    showCurrentMediaCreateTimeUI()
                } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    hideCurrentMediaCreateTimeUI()
                }
            }

        })
        mAdapter.setOnGetSelectResultListener(object :
            BaseMediaListAdapter.OnGetSelectResultListener {
            override fun onSelectResult(): MutableList<LocalMedia> {
                return globalViewMode.selectResult
            }
        })
        mAdapter.setOnItemClickListener(object : OnMediaItemClickListener {
            override fun openCamera() {
                if (DoubleUtils.isFastDoubleClick()) {
                    return
                }
                openSelectedCamera()
            }

            override fun onItemClick(selectedView: View, position: Int, media: LocalMedia) {
                if (DoubleUtils.isFastDoubleClick()) {
                    return
                }
                if (viewModel.config.isPreviewZoomEffect) {
                    val isFullScreen = viewModel.config.isPreviewFullScreenMode
                    val statusBarHeight = getStatusBarHeight(requireContext())
                    RecycleItemViewParams.build(mRecycler, if (isFullScreen) 0 else statusBarHeight)
                }
                onStartPreview(position, false, mAdapter.getData())
            }

            override fun onComplete(isSelected: Boolean, position: Int, media: LocalMedia) {
                if (confirmSelect(media, isSelected) == SelectedState.SUCCESS) {
                    handleSelectResult()
                }
            }

            override fun onItemLongClick(itemView: View, position: Int, media: LocalMedia) {
                if (viewModel.config.isFastSlidingSelect) {
                    mDragSelectTouchListener?.let {
                        val activity = requireActivity()
                        val vibrator =
                            activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                        if (SdkVersionUtils.isO()) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, DEFAULT_AMPLITUDE))
                        } else {
                            vibrator.vibrate(50)
                        }
                        it.startSlideSelection(position)
                    }
                }
            }

            override fun onSelected(isSelected: Boolean, position: Int, media: LocalMedia): Int {
                return confirmSelect(media, isSelected)
            }
        })
    }

    open fun setFastSlidingSelect() {
        if (viewModel.config.isFastSlidingSelect) {
            val selectedPosition = HashSet<Int>()
            val slideSelectionHandler =
                SlideSelectionHandler(object : SlideSelectionHandler.ISelectionHandler {
                    override fun getSelection(): MutableSet<Int> {
                        globalViewMode.selectResult.forEach { media ->
                            selectedPosition.add(mAdapter.getData().indexOf(media))
                        }
                        return selectedPosition
                    }

                    override fun changeSelection(
                        start: Int,
                        end: Int,
                        isSelected: Boolean,
                        calledFromOnStart: Boolean
                    ) {
                        val adapterData = mAdapter.getData()
                        if (adapterData.size == 0 || start > adapterData.size) {
                            return
                        }
                        val media = adapterData[start]
                        mDragSelectTouchListener?.setActive(
                            confirmSelect(
                                media,
                                globalViewMode.selectResult.contains(media)
                            ) != SelectedState.INVALID
                        )
                    }
                })
            mDragSelectTouchListener = SlideSelectTouchListener()
                .setRecyclerViewHeaderCount(if (mAdapter.isDisplayCamera()) 1 else 0)
                .withSelectListener(slideSelectionHandler)
            mDragSelectTouchListener?.let {
                mRecycler.addOnItemTouchListener(it)
            }
        }
    }

    open fun checkPermissions() {
        if (PermissionChecker.isCheckReadStorage(requireContext(), viewModel.config.selectorMode)) {
            requestData()
        } else {
            val permissionArray = PermissionChecker.getReadPermissionArray(
                requireContext(),
                viewModel.config.selectorMode
            )
            showPermissionDescription(true, permissionArray)
            val onPermissionApplyListener = viewModel.config.mListenerInfo.onPermissionApplyListener
            if (onPermissionApplyListener != null) {
                showCustomPermissionApply(permissionArray)
            } else {
                PermissionChecker.requestPermissions(
                    this,
                    permissionArray,
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            requestData()
                        }

                        override fun onDenied() {
                            handlePermissionDenied(permissionArray)
                        }
                    })
            }
        }
    }


    override fun showCustomPermissionApply(permission: Array<String>) {
        viewModel.config.mListenerInfo.onPermissionApplyListener?.requestPermission(
            this,
            permission,
            object :
                OnRequestPermissionListener {
                override fun onCall(permission: Array<String>, isResult: Boolean) {
                    if (isResult) {
                        requestData()
                    } else {
                        handlePermissionDenied(permission)
                    }
                }
            })
    }

    override fun handlePermissionSettingResult(permission: Array<String>) {
        if (permission.isEmpty()) {
            return
        }
        showPermissionDescription(false, permission)
        val isHasCamera = TextUtils.equals(permission[0], PermissionChecker.CAMERA)
        val onPermissionApplyListener = viewModel.config.mListenerInfo.onPermissionApplyListener
        val isHasPermissions = onPermissionApplyListener?.hasPermissions(this, permission)
            ?: PermissionChecker.checkSelfPermission(requireContext(), permission)
        if (isHasPermissions) {
            if (isHasCamera) {
                openSelectedCamera()
            } else {
                requestData()
            }
        } else {
            if (isHasCamera) {
                ToastUtils.showMsg(requireContext(), getString(R.string.ps_camera))
            } else {
                ToastUtils.showMsg(requireContext(), getString(R.string.ps_jurisdiction))
                onBackPressed()
            }
        }
        viewModel.currentRequestPermission = arrayOf()
    }


    /**
     * Start requesting media album data
     */
    open fun requestData() {
        if (viewModel.config.isOnlySandboxDir) {
            val sandboxDir = viewModel.config.sandboxDir
            mTvTitle?.text = sandboxDir!!.substring(sandboxDir.lastIndexOf("/") + 1)
            mIvTitleArrow?.visibility = View.GONE
            mRecycler.setEnabledLoadMore(false)
            viewModel.loadAppInternalDir(sandboxDir)
        } else {
            viewModel.loadMedia()
            Looper.myQueue().addIdleHandler {
                viewModel.loadMediaAlbum()
                return@addIdleHandler false
            }
        }
    }

    /**
     * Changes in album data
     * @param albumList album data
     */
    open fun onAlbumSourceChange(albumList: MutableList<LocalMediaAlbum>) {
        if (albumList.isNotEmpty()) {
            albumList.first().isSelected = true
            viewModel.currentMediaAlbum = albumList.first()
            mAlbumWindow.setAlbumList(albumList)
            mAlbumWindow.notifyChangedSelectTag(globalViewMode.selectResult)
        }
    }

    /**
     * Changes in media data
     * @param result media data
     */
    open fun onMediaSourceChange(result: MutableList<LocalMedia>) {
        mRecycler.setEnabledLoadMore(!viewModel.config.isOnlySandboxDir && result.isNotEmpty())
        if (viewModel.page == 1) {
            mAdapter.setDataNotifyChanged(result.toMutableList())
            mRecycler.scrollToPosition(0)
            if (mAdapter.getData()
                    .isEmpty() && (viewModel.currentMediaAlbum == null || viewModel.currentMediaAlbum?.isAllAlbum() == true)
            ) {
                mTvDataEmpty?.visibility = View.VISIBLE
            } else {
                mTvDataEmpty?.visibility = View.GONE
            }
        } else {
            mAdapter.addAllDataNotifyChanged(result)
        }
    }

    /**
     * Users can override this method to configure custom previews
     *
     * @param position Preview start position
     * @param isBottomPreview Preview source from bottom
     * @param source Preview Data Source
     */
    open fun onStartPreview(
        position: Int,
        isBottomPreview: Boolean,
        source: MutableList<LocalMedia>
    ) {
        viewModel.config.previewWrap =
            onWrapPreviewData(viewModel.page, position, isBottomPreview, source)
        val factory = ClassFactory.NewInstance()
        val registry = viewModel.config.registry
        val instance = factory.create(registry.get(newPreviewInstance()))
        val fragmentTag = instance.getFragmentTag()
        FragmentInjectManager.injectSystemRoomFragment(requireActivity(), fragmentTag, instance)
    }

    /**
     * Users can override this method to configure custom previews fragments
     */
    @Suppress("UNCHECKED_CAST")
    open fun <F : SelectorPreviewFragment> newPreviewInstance(): Class<F> {
        return SelectorPreviewFragment::class.java as Class<F>
    }

    /**
     * Preview data wrap
     * @param page current request page
     * @param position Preview start position
     * @param isBottomPreview Preview source from bottom
     * @param source Preview Data Source
     */
    open fun onWrapPreviewData(
        page: Int,
        position: Int,
        isBottomPreview: Boolean,
        source: MutableList<LocalMedia>
    ): PreviewDataWrap {
        return PreviewDataWrap().apply {
            this.page = page
            this.position = position
            this.bucketId =
                viewModel.currentMediaAlbum?.bucketId ?: SelectorConstant.DEFAULT_ALL_BUCKET_ID
            this.isBottomPreview = isBottomPreview
            this.isDisplayCamera = mAdapter.isDisplayCamera()
            if (isBottomPreview) {
                this.totalCount = source.size
            } else {
                this.totalCount = viewModel.currentMediaAlbum?.totalCount ?: source.size
            }
            this.source = source.toMutableList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SelectorConstant.REQUEST_CAMERA) {
                val outputUri =
                    data?.getParcelableExtra(MediaStore.EXTRA_OUTPUT) ?: data?.data
                    ?: viewModel.outputUri
                if (outputUri != null) {
                    analysisCameraData(outputUri)
                } else {
                    throw IllegalStateException("Camera output uri is empty")
                }
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
        PictureMediaScannerConnection(context, if (isContent) realPath else null,
            object : ScanListener {
                override fun onScanFinish() {
                    viewModel.viewModelScope.launch {
                        val media = if (isContent) {
                            MediaUtils.getAssignPathMedia(context, realPath!!)
                        } else {
                            MediaUtils.getAssignFileMedia(context, realPath!!)
                        }
                        onCheckDuplicateMedia(media)
                        onMergeCameraAlbum(media)
                        onMergeCameraMedia(media)
                    }
                }
            })
    }

    /**
     * Some models may generate two duplicate photos when taking photos
     */
    open fun onCheckDuplicateMedia(media: LocalMedia) {
        if (SdkVersionUtils.isQ()) {
        } else {
            if (MediaUtils.hasMimeTypeOfImage(media.mimeType)) {
                viewModel.viewModelScope.launch {
                    media.absolutePath?.let {
                        File(it).parent?.let { parent ->
                            val context = requireContext()
                            val id = MediaUtils.getDCIMLastId(context, parent)
                            if (id != -1L) {
                                MediaUtils.remove(context, id)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Merge media data generated by the camera into the album
     */
    open fun onMergeCameraAlbum(media: LocalMedia) {
        // merge album list
        val allMediaAlbum =
            mAlbumWindow.getAlbum(SelectorConstant.DEFAULT_ALL_BUCKET_ID) ?: LocalMediaAlbum()
        allMediaAlbum.bucketId = SelectorConstant.DEFAULT_ALL_BUCKET_ID
        val defaultAlbumName = viewModel.config.defaultAlbumName
        allMediaAlbum.bucketDisplayName =
            if (TextUtils.isEmpty(defaultAlbumName)) if (MediaUtils.hasMimeTypeOfAudio(media.mimeType))
                getString(R.string.ps_all_audio) else getString(
                R.string.ps_camera_roll
            ) else defaultAlbumName
        allMediaAlbum.bucketDisplayCover = media.path
        allMediaAlbum.bucketDisplayMimeType = media.mimeType
        allMediaAlbum.source.add(0, media)
        allMediaAlbum.totalCount += 1

        val cameraMediaAlbum = mAlbumWindow.getAlbum(media.bucketId) ?: LocalMediaAlbum()
        cameraMediaAlbum.bucketId = media.bucketId
        cameraMediaAlbum.bucketDisplayName = media.bucketDisplayName
        cameraMediaAlbum.bucketDisplayCover = media.path
        cameraMediaAlbum.bucketDisplayMimeType = media.mimeType
        cameraMediaAlbum.source.add(0, media)
        cameraMediaAlbum.totalCount += 1

        if (mAlbumWindow.getAlbumList().isEmpty()) {
            val albumList = mutableListOf<LocalMediaAlbum>()
            albumList.add(0, allMediaAlbum)
            albumList.add(cameraMediaAlbum)
            albumList.first().isSelected = true
            viewModel.currentMediaAlbum = albumList.first()
            mAlbumWindow.setAlbumList(albumList)
            mTvDataEmpty?.visibility = View.GONE
        }
    }

    /**
     * Merge camera generated media data into a list
     */
    open fun onMergeCameraMedia(media: LocalMedia) {
        requireActivity().runOnUiThread {
            mAdapter.getData().add(0, media)
            if (confirmSelect(media, false) == SelectedState.SUCCESS) {
                val position = if (mAdapter.isDisplayCamera()) 1 else 0
                mAdapter.notifyItemInserted(position)
                mAdapter.notifyItemRangeChanged(position, mAdapter.getData().size - position)
            }
        }
    }


    override fun onDestroy() {
        mDragSelectTouchListener?.stopAutoScroll()
        super.onDestroy()
    }
}