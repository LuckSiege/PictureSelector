package com.luck.picture.lib

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.os.*
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
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
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.config.SelectionMode
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
import com.luck.picture.lib.permissions.OnPermissionResultListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.provider.TempDataProvider
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
        return config.layoutSource[LayoutSource.SELECTOR_MAIN]
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
    var mTitleBar: ViewGroup? = null
    var mIvLeftBack: ImageView? = null
    var mTvTitle: TextView? = null
    var mIvTitleArrow: ImageView? = null
    var mTvCancel: TextView? = null
    var mTvCurrentDataTime: TextView? = null

    /**
     * BottomNarBar
     */
    var mBottomNarBar: ViewGroup? = null
    var mTvPreview: StyleTextView? = null
    var mTvOriginal: TextView? = null
    var mTvComplete: StyleTextView? = null
    var mTvSelectNum: TextView? = null

    private val anyLock = Any()

    private var isCameraCallback = false

    lateinit var mAlbumWindow: AlbumListPopWindow
    lateinit var mAdapter: BaseMediaListAdapter

    private var intervalClickTime: Long = 0

    private var mDragSelectTouchListener: SlideSelectTouchListener? = null

    open fun getCurrentAlbum(): LocalMediaAlbum {
        return TempDataProvider.getInstance().currentMediaAlbum
    }

    private fun setCurrentAlbum(album: LocalMediaAlbum) {
        TempDataProvider.getInstance().currentMediaAlbum = album
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        onMergeSelectedSource()
        initAlbumWindow()
        initTitleBar()
        initNavbarBar()
        initMediaAdapter()
        checkPermissions()
        registerLiveData()
        initWidgets()
    }

    open fun initViews(view: View) {
        // RecyclerView
        mRecycler = view.findViewById(R.id.ps_recycler)
        mTvDataEmpty = view.findViewById(R.id.ps_tv_data_empty)
        mTvCurrentDataTime = view.findViewById(R.id.ps_tv_current_data_time)
        setDataEmpty()
        // TitleBar
        mStatusBar = view.findViewById(R.id.ps_status_bar)
        mTitleBar = view.findViewById(R.id.ps_title_bar)
        mIvLeftBack = view.findViewById(R.id.ps_iv_left_back)
        mTvTitle = view.findViewById(R.id.ps_tv_title)
        mIvTitleArrow = view.findViewById(R.id.ps_iv_arrow)
        mTvCancel = view.findViewById(R.id.ps_tv_cancel)
        setStatusBarRectSize(mStatusBar)

        // BottomNarBar
        mBottomNarBar = view.findViewById(R.id.ps_bottom_nar_bar)
        mTvPreview = view.findViewById(R.id.ps_tv_preview)
        mTvOriginal = view.findViewById(R.id.ps_tv_original)
        mTvComplete = view.findViewById(R.id.ps_tv_complete)
        mTvSelectNum = view.findViewById(R.id.ps_tv_select_num)
    }

    open fun initWidgets() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        TempDataProvider.getInstance().albumSource = mAlbumWindow.getAlbumList()
        TempDataProvider.getInstance().mediaSource = mAdapter.getData().toMutableList()
    }

    open fun setDataEmpty() {
        mTvDataEmpty?.text =
            if (config.mediaType == MediaType.AUDIO) getString(R.string.ps_audio_empty) else getString(
                R.string.ps_empty
            )
    }

    open fun onMergeSelectedSource() {
        if (config.selectedSource.isNotEmpty()) {
            getSelectResult().addAll(config.selectedSource.toMutableList())
            config.selectedSource.clear()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun registerLiveData() {
        globalViewMode.getEditorLiveData().observe(viewLifecycleOwner) { media ->
            val position = mAdapter.getData().indexOf(media)
            if (position >= 0) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
        }
        globalViewMode.getOriginalLiveData().observe(viewLifecycleOwner) { isOriginal ->
            onOriginalChange(isOriginal)
        }
        globalViewMode.getSelectResultLiveData().observe(viewLifecycleOwner) { media ->
            val position = mAdapter.getData().indexOf(media)
            if (checkNotifyStrategy(getSelectResult().indexOf(media) != -1)) {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
                Looper.myQueue().addIdleHandler {
                    mAdapter.notifyDataSetChanged()
                    return@addIdleHandler false
                }
            } else {
                mAdapter.notifyItemChanged(if (mAdapter.isDisplayCamera()) position + 1 else position)
            }
            // update selected tag
            mAlbumWindow.notifyChangedSelectTag(getSelectResult())
            onSelectionResultChange(media)
        }
        viewModel.albumLiveData.observe(viewLifecycleOwner) { albumList ->
            onAlbumSourceChange(albumList)
        }
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { mediaList ->
            onMediaSourceChange(mediaList)
            SelectorLogUtils.info("当前数量->${mAdapter.getData().size}")
        }
    }

    open fun initTitleBar() {
        setDefaultAlbumTitle(getCurrentAlbum().bucketDisplayName)
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
        mTitleBar?.setOnClickListener {
            onTitleBarClick(it)
        }
    }

    open fun onBackClick(v: View) {
        onBackPressed()
    }

    open fun onShowAlbumWindowAsDropDown() {
        if (mAlbumWindow.getAlbumList().isNotEmpty() && !config.isOnlySandboxDir) {
            mTitleBar?.let {
                mAlbumWindow.showAsDropDown(it)
            }
        }
    }

    open fun onTitleBarClick(v: View) {
        if (SystemClock.uptimeMillis() - intervalClickTime < 500 && mAdapter.getData()
                .isNotEmpty()
        ) {
            if (mAdapter.getData().size > 2 * config.pageSize) {
                mRecycler.scrollToPosition(config.pageSize)
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
        globalViewMode.setOriginalLiveData(!v.isSelected)
    }

    open fun onOriginalChange(isOriginal: Boolean) {
        mTvOriginal?.isSelected = isOriginal
    }

    open fun onCompleteClick(v: View) {
        handleSelectResult()
    }

    open fun setStatusBarRectSize(statusBarRectView: View?) {
        if (config.isPreviewFullScreenMode) {
            statusBarRectView?.layoutParams?.height =
                getStatusBarHeight(requireContext())
            statusBarRectView?.visibility = View.VISIBLE
        } else {
            statusBarRectView?.layoutParams?.height = 0
            statusBarRectView?.visibility = View.GONE
        }
    }

    open fun setCurrentMediaCreateTimeText() {
        if (config.isDisplayTimeAxis) {
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
        if (config.isDisplayTimeAxis && mAdapter.getData().size > 0) {
            if (mTvCurrentDataTime?.alpha == 0f) {
                mTvCurrentDataTime?.animate()?.setDuration(150)?.alphaBy(1.0f)?.start()
            }
        }
    }

    open fun hideCurrentMediaCreateTimeUI() {
        if (config.isDisplayTimeAxis && mAdapter.getData().size > 0) {
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
        // Repeated clicks ignore
        val oldCurrentAlbum = getCurrentAlbum()
        if (data.isEqualAlbum(oldCurrentAlbum.bucketId)) {
            return
        }
        // Cache the current album data before switching to the next album
        mAlbumWindow.getAlbum(oldCurrentAlbum.bucketId)?.let {
            val source = mAdapter.getData().toMutableList()
            if (source.isNotEmpty() && source.first().id == SelectorConstant.INVALID_DATA) {
                // ignore
            } else {
                it.source = source
                it.cachePage = viewModel.page
            }
        }
        // Update current album
        setCurrentAlbum(data)
        mAdapter.setDisplayCamera(isDisplayCamera())
        setDefaultAlbumTitle(data.bucketDisplayName)
        if (data.cachePage > 0 && data.source.isNotEmpty()) {
            // Album already has cached data，Start loading from cached page numbers
            viewModel.page = data.cachePage
            mAdapter.setDataNotifyChanged(data.source)
            mRecycler.scrollToPosition(0)
            mRecycler.setEnabledLoadMore(!data.isSandboxAlbum() && !config.isOnlySandboxDir && data.source.isNotEmpty())
        } else {
            // Never loaded, request data again
            viewModel.loadMedia(data.bucketId)
        }
        if (config.isFastSlidingSelect) {
            mDragSelectTouchListener?.setRecyclerViewHeaderCount(if (mAdapter.isDisplayCamera()) 1 else 0)
        }
    }

    open fun onRotateArrowAnim(showing: Boolean) {
        if (!config.isOnlySandboxDir) {
            AnimUtils.rotateArrow(mIvTitleArrow, showing)
        }
    }

    open fun initNavbarBar() {
        if (config.selectionMode == SelectionMode.ONLY_SINGLE) {
            mBottomNarBar?.visibility = View.GONE
        } else {
            if (config.isOnlyCamera || config.systemGallery) {
            } else {
                mTvOriginal?.visibility =
                    if (config.isOriginalControl) View.VISIBLE else View.GONE
                mTvOriginal?.setOnClickListener { tvOriginal ->
                    onOriginalClick(tvOriginal)
                }
            }
            mTvPreview?.setOnClickListener {
                if (DoubleUtils.isFastDoubleClick()) {
                    return@setOnClickListener
                }
                onStartPreview(0, true, getSelectResult())
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
        if (config.isMaxSelectEnabledMask) {
            val selectResult = getSelectResult()
            val selectCount = selectResult.size
            if (config.isAllWithImageVideo) {
                val maxSelectCount = config.getSelectCount()
                if (config.selectionMode == SelectionMode.MULTIPLE) {
                    isNotifyAll =
                        selectCount == maxSelectCount || (!isAddRemove && selectCount == maxSelectCount - 1)
                }
            } else {
                isNotifyAll = if (selectCount == 0 ||
                    (if (isAddRemove) config.mediaType == MediaType.ALL && selectCount == 1
                    else selectCount == config.totalCount - 1)
                ) {
                    true
                } else {
                    if (MediaUtils.hasMimeTypeOfVideo(selectResult.first().mimeType)) {
                        selectResult.size == config.maxVideoSelectNum
                    } else {
                        selectResult.size == config.totalCount
                    }
                }
            }
        }
        return isNotifyAll
    }

    override fun onSelectionResultChange(change: LocalMedia?) {
        val selectResult = getSelectResult()
        mTvPreview?.setDataStyle(config, selectResult)
        mTvComplete?.setDataStyle(config, selectResult)
        mTvSelectNum?.visibility =
            if (selectResult.isNotEmpty()) View.VISIBLE else View.GONE
        mTvSelectNum?.text = selectResult.size.toString()

        var totalSize: Long = 0
        selectResult.forEach { media ->
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
                        config.imageSpanCount,
                        DensityUtil.dip2px(requireContext(), 1F), false
                    )
                )
            }
            view.layoutManager =
                WrapContentGridLayoutManager(requireContext(), config.imageSpanCount)
            (view.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }


    /**
     * Users can implement custom media lists
     */
    open fun createMediaAdapter(): BaseMediaListAdapter {
        val adapterClass =
            config.registry.get(MediaListAdapter::class.java)
        return factory.create(adapterClass)
    }

    @Suppress("UNCHECKED_CAST")
    open fun initMediaAdapter() {
        initRecyclerConfig(mRecycler)
        mAdapter = createMediaAdapter()
        mAdapter.setDisplayCamera(isDisplayCamera())
        mRecycler.adapter =
            config.mListenerInfo.onAnimationAdapterWrapListener?.wrap(mAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)
                ?: mAdapter
        setFastSlidingSelect()
        onSelectionResultChange(null)
        mRecycler.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD)
        mRecycler.setOnRecyclerViewPreloadListener(object : OnRecyclerViewPreloadMoreListener {
            override fun onPreloadMore() {
                if (mRecycler.isEnabledLoadMore()) {
                    if (isLoadMoreThreshold()) {
                        viewModel.loadMediaMore(getCurrentAlbum().bucketId)
                        SelectorLogUtils.info("加载第${viewModel.page}页")
                    }
                }
            }
        })

        mRecycler.setOnRecyclerViewScrollStateListener(object : OnRecyclerViewScrollStateListener {
            override fun onScrollFast() {
                config.imageEngine?.pauseRequests(requireContext())
            }

            override fun onScrollSlow() {
                config.imageEngine?.resumeRequests(requireContext())
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
                return getSelectResult()
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
                if (config.isPreviewZoomEffect) {
                    val isFullScreen = config.isPreviewFullScreenMode
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
                if (config.isFastSlidingSelect) {
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

    open fun isDisplayCamera(): Boolean {
        return config.isDisplayCamera && getCurrentAlbum().isAllAlbum()
                || (config.isOnlySandboxDir && getCurrentAlbum().isSandboxAlbum())
    }

    /**
     * Load more thresholds
     */
    open fun isLoadMoreThreshold(): Boolean {
        if (getCurrentAlbum().totalCount == mAdapter.getData().size) {
            return false
        }
        return true
    }

    /**
     *  duplicate media data
     */
    open fun duplicateMediaSource(result: MutableList<LocalMedia>) {
        if (isCameraCallback && getCurrentAlbum().isAllAlbum()) {
            isCameraCallback = false
            synchronized(anyLock) {
                val data = mAdapter.getData()
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val media = iterator.next()
                    if (data.contains(media)) {
                        iterator.remove()
                        SelectorLogUtils.info("有重复的:${media.getAvailablePath()}")
                    }
                }
            }
        }
    }

    open fun setFastSlidingSelect() {
        if (config.isFastSlidingSelect) {
            val selectedPosition = HashSet<Int>()
            val slideSelectionHandler =
                SlideSelectionHandler(object : SlideSelectionHandler.ISelectionHandler {
                    override fun getSelection(): MutableSet<Int> {
                        getSelectResult().forEach { media ->
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
                                getSelectResult().contains(media)
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
        if (PermissionChecker.isCheckReadStorage(requireContext(), config.mediaType)) {
            if (isNeedRestore()) {
                restoreMemoryData()
            } else {
                requestData()
            }
        } else {
            val permissionArray = PermissionChecker.getReadPermissionArray(
                requireContext(),
                config.mediaType
            )
            showPermissionDescription(true, permissionArray)
            val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
            if (onPermissionApplyListener != null) {
                showCustomPermissionApply(permissionArray)
            } else {
                PermissionChecker.requestPermissions(
                    this,
                    permissionArray,
                    object : OnPermissionResultListener {
                        override fun onGranted() {
                            showPermissionDescription(false, permissionArray)
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
        config.mListenerInfo.onPermissionApplyListener?.requestPermission(
            this,
            permission,
            object :
                OnRequestPermissionListener {
                override fun onCall(permission: Array<String>, isResult: Boolean) {
                    if (isResult) {
                        showPermissionDescription(false, permission)
                        if (permission.first() == Manifest.permission.CAMERA) {
                            openSelectedCamera()
                        } else {
                            requestData()
                        }
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
        val onPermissionApplyListener = config.mListenerInfo.onPermissionApplyListener
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
        TempDataProvider.getInstance().currentRequestPermission = arrayOf()
    }


    /**
     * Start requesting media album data
     */
    open fun requestData() {
        if (config.isOnlySandboxDir) {
            val sandboxDir =
                config.sandboxDir ?: throw NullPointerException("config.sandboxDir cannot be empty")
            val dir = File(sandboxDir)
            setDefaultAlbumTitle(dir.name)
            setCurrentAlbum(LocalMediaAlbum().apply {
                this.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
                this.bucketDisplayName = dir.name
            })
            mIvTitleArrow?.visibility = View.GONE
            mRecycler.setEnabledLoadMore(false)
            viewModel.loadAppInternalDir(sandboxDir)
        } else {
            onPreloadFakeData()
            Looper.myQueue().addIdleHandler {
                viewModel.loadMediaAlbum()
                viewModel.loadMedia(getCurrentAlbum().bucketId)
                return@addIdleHandler false
            }
        }
    }

    /**
     * set fake data transition
     */
    open fun onPreloadFakeData() {
        if (isSavedInstanceState) {
            return
        }
        val pre = mutableListOf<LocalMedia>()
        for (i in 0 until SelectorConstant.DEFAULT_MAX_PAGE_SIZE) {
            pre.add(LocalMedia().apply {
                this.id = SelectorConstant.INVALID_DATA
            })
        }
        mAdapter.setDataNotifyChanged(pre)
    }

    /**
     * set default album title
     */
    open fun setDefaultAlbumTitle(title: String?) {
        mTvTitle?.text =
            config.defaultAlbumName ?: title ?: if (config.mediaType == MediaType.AUDIO)
                getString(R.string.ps_all_audio) else getString(
                R.string.ps_camera_roll
            )
    }

    /**
     * Restore data after system recycling
     */
    open fun restoreMemoryData() {
        val albumSource = TempDataProvider.getInstance().albumSource.toMutableList()
        onAlbumSourceChange(albumSource)
        val mediaSource = TempDataProvider.getInstance().mediaSource.toMutableList()
        onMediaSourceChange(mediaSource)
        TempDataProvider.getInstance().albumSource.clear()
        TempDataProvider.getInstance().mediaSource.clear()
        if (config.isOnlySandboxDir) {
            val sandboxDir =
                config.sandboxDir ?: throw NullPointerException("config.sandboxDir cannot be empty")
            val dir = File(sandboxDir)
            setDefaultAlbumTitle(dir.name)
            setCurrentAlbum(LocalMediaAlbum().apply {
                this.bucketId = SelectorConstant.DEFAULT_DIR_BUCKET_ID
                this.bucketDisplayName = dir.name
            })
            mIvTitleArrow?.visibility = View.GONE
            mRecycler.setEnabledLoadMore(false)
        }
    }

    /**
     * Restore Memory Data
     */
    open fun isNeedRestore(): Boolean {
        return isSavedInstanceState
    }

    /**
     * Changes in album data
     * @param albumList album data
     */
    open fun onAlbumSourceChange(albumList: MutableList<LocalMediaAlbum>) {
        if (albumList.isNotEmpty()) {
            setCurrentAlbum(albumList.first())
            albumList.forEach { album ->
                if (album.bucketId == getCurrentAlbum().bucketId) {
                    album.isSelected = true
                    return@forEach
                }
            }
            mAlbumWindow.setAlbumList(albumList)
            mAlbumWindow.notifyChangedSelectTag(getSelectResult())
        }
    }

    /**
     * Changes in media data
     * @param result media data
     */
    open fun onMediaSourceChange(result: MutableList<LocalMedia>) {
        duplicateMediaSource(result)
        mRecycler.setEnabledLoadMore(!config.isOnlySandboxDir && result.isNotEmpty())
        if (viewModel.page == 1) {
            mAdapter.setDataNotifyChanged(result.toMutableList())
            mRecycler.scrollToPosition(0)
            if (mAdapter.getData()
                    .isEmpty() && (getCurrentAlbum().isAllAlbum()
                        || (config.isOnlySandboxDir && getCurrentAlbum().isSandboxAlbum()))
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
        config.previewWrap =
            onWrapPreviewData(viewModel.page, position, isBottomPreview, source)
        val factory = ClassFactory.NewInstance()
        val registry = config.registry
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
            this.bucketId = getCurrentAlbum().bucketId
            this.isBottomPreview = isBottomPreview
            this.isDisplayCamera = mAdapter.isDisplayCamera()
            if (config.isOnlySandboxDir) {
                this.totalCount = source.size
            } else {
                this.totalCount = if (isBottomPreview) source.size else getCurrentAlbum().totalCount
            }
            this.source = source.toMutableList()
        }
    }

    override fun onMergeCameraResult(media: LocalMedia?) {
        if (media != null) {
            isCameraCallback = true
            onCheckDuplicateMedia(media)
            onMergeCameraAlbum(media)
            onMergeCameraMedia(media)
        } else {
            SelectorLogUtils.info("analysisCameraData: Parsing LocalMedia object as empty")
        }
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
        val bucketDisplayName =
            config.defaultAlbumName ?: if (MediaUtils.hasMimeTypeOfAudio(media.mimeType))
                getString(R.string.ps_all_audio) else getString(
                R.string.ps_camera_roll
            )
        allMediaAlbum.bucketDisplayName = bucketDisplayName
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
            setCurrentAlbum(albumList.first())
            mAlbumWindow.setAlbumList(albumList)
            mTvDataEmpty?.visibility = View.GONE
        } else {
            val cameraAlbum = mAlbumWindow.getAlbum(cameraMediaAlbum.bucketId)
            if (cameraAlbum == null) {
                mAlbumWindow.getAlbumList().add(cameraMediaAlbum)
            }
        }
        mAlbumWindow.notifyItemRangeChanged()
    }

    /**
     * Merge camera generated media data into a list
     */
    open fun onMergeCameraMedia(media: LocalMedia) {
        requireActivity().runOnUiThread {
            mAdapter.getData().add(0, media)
            confirmSelect(media, false)
            val position = if (mAdapter.isDisplayCamera()) 1 else 0
            mAdapter.notifyItemInserted(position)
            mAdapter.notifyItemRangeChanged(position, mAdapter.getData().size)
        }
    }


    override fun onDestroy() {
        mDragSelectTouchListener?.stopAutoScroll()
        super.onDestroy()
    }
}