package com.luck.picture.lib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.constant.SelectedState
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.interfaces.OnLongClickListener
import com.luck.picture.lib.provider.TempDataProvider
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.StyleUtils.getColorFilter
import com.luck.picture.lib.widget.HorizontalItemDecoration
import com.luck.picture.lib.widget.WrapContentLinearLayoutManager
import java.util.*

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：SelectorNumPreviewFragment
 */
open class SelectorNumberPreviewFragment : SelectorPreviewFragment() {
    override fun getFragmentTag(): String {
        return SelectorNumberPreviewFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return config.layoutSource[LayoutSource.SELECTOR_NUMBER_PREVIEW]
            ?: R.layout.ps_fragment_number_preview
    }

    override fun startSelectedAnim(selectedView: View) {

    }

    private lateinit var rvGallery: RecyclerView
    private var galleryAdapter: GalleryAdapter? = null
    private var needScaleBig = true
    private var needScaleSmall = false
    private var moveFromPosition = -1
    private var moveToPosition = -1

    override fun initViews(view: View) {
        super.initViews(view)
        rvGallery = view.findViewById(R.id.ps_rv_gallery)
        mTvSelected?.let {
            titleViews.remove(it)
        }
        mTvComplete?.let {
            navBarViews.remove(it)
        }
        rvGallery.visibility =
            if (TempDataProvider.getInstance().selectResult.isEmpty()) View.GONE else View.VISIBLE
        addTitleBarViewGroup(mTvComplete)
        addNarBarViewGroup(rvGallery, mTvSelected)
    }

    override fun onCompleteClick(v: View) {
        if (TempDataProvider.getInstance().selectResult.isEmpty()) {
            val media = TempDataProvider.getInstance().previewWrap.source[viewPager.currentItem]
            if (confirmSelect(media, false) == SelectedState.SUCCESS) {
                mTvSelected?.isSelected = true
                handleSelectResult()
            }
        } else {
            super.onCompleteClick(v)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initWidgets() {
        super.initWidgets()
        (rvGallery.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        rvGallery.addItemDecoration(
            HorizontalItemDecoration(Integer.MAX_VALUE, DensityUtil.dip2px(requireContext(), 6F))
        )
        val layoutManager: WrapContentLinearLayoutManager = object : WrapContentLinearLayoutManager(
            requireContext()
        ) {
            override fun smoothScrollToPosition(
                recyclerView: RecyclerView,
                state: RecyclerView.State?,
                position: Int
            ) {
                super.smoothScrollToPosition(recyclerView, state, position)
                val smoothScroller: LinearSmoothScroller =
                    object : LinearSmoothScroller(recyclerView.context) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return 300f / displayMetrics.densityDpi
                        }
                    }
                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }

        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvGallery.layoutManager = layoutManager
        if (TempDataProvider.getInstance().selectResult.isNotEmpty()) {
            rvGallery.layoutAnimation = AnimationUtils
                .loadLayoutAnimation(requireContext(), R.anim.ps_anim_layout_fall_enter)
        }
        galleryAdapter =
            GalleryAdapter(
                config,
                TempDataProvider.getInstance().previewWrap.isBottomPreview,
                if (TempDataProvider.getInstance().previewWrap.isBottomPreview) TempDataProvider.getInstance().selectResult.toMutableList() else TempDataProvider.getInstance().selectResult
            )
        galleryAdapter?.currentMedia =
            TempDataProvider.getInstance().previewWrap.source[viewPager.currentItem]
        galleryAdapter?.selectResult = TempDataProvider.getInstance().selectResult
        rvGallery.adapter = galleryAdapter
        galleryAdapter?.setOnItemClickListener(object : OnItemClickListener<LocalMedia> {
            override fun onItemClick(position: Int, data: LocalMedia) {
                val currentItem = TempDataProvider.getInstance().previewWrap.source.indexOf(data)
                if (currentItem >= 0) {
                    viewPager.setCurrentItem(currentItem, false)
                }
            }
        })
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                viewHolder.itemView.alpha = 0.7F
                return makeMovementFlags(
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                            or ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    0
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition
                moveFromPosition = fromPosition
                moveToPosition = toPosition
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        if (TempDataProvider.getInstance().previewWrap.isBottomPreview) {
                            Collections.swap(
                                TempDataProvider.getInstance().previewWrap.source,
                                i,
                                i + 1
                            )
                            galleryAdapter?.data?.let { Collections.swap(it, i, i + 1) }
                        }
                        if (TempDataProvider.getInstance().selectResult.size > i + 1) {
                            Collections.swap(TempDataProvider.getInstance().selectResult, i, i + 1)
                        }
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        if (TempDataProvider.getInstance().previewWrap.isBottomPreview) {
                            Collections.swap(
                                TempDataProvider.getInstance().previewWrap.source,
                                i,
                                i - 1
                            )
                            galleryAdapter?.data?.let { Collections.swap(it, i, i - 1) }
                        }
                        if (TempDataProvider.getInstance().selectResult.size > i) {
                            Collections.swap(TempDataProvider.getInstance().selectResult, i, i - 1)
                        }
                    }
                }
                galleryAdapter?.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                holder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (needScaleBig) {
                    needScaleBig = false
                    val animatorSet = AnimatorSet()
                    animatorSet.playTogether(
                        ObjectAnimator.ofFloat(holder.itemView, "scaleX", 1.0f, 1.1f),
                        ObjectAnimator.ofFloat(holder.itemView, "scaleY", 1.0f, 1.1f)
                    )
                    animatorSet.duration = 50
                    animatorSet.interpolator = LinearInterpolator()
                    animatorSet.start()
                    animatorSet.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            needScaleSmall = true
                            animatorSet.removeListener(this)
                        }
                    })
                }
                super.onChildDraw(c, recyclerView, holder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                viewHolder.itemView.alpha = 1.0F
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
                            animatorSet.removeListener(this)
                        }
                    })
                }
                super.clearView(recyclerView, viewHolder)
                galleryAdapter?.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                if (TempDataProvider.getInstance().previewWrap.isBottomPreview) {
                    val currentItem =
                        TempDataProvider.getInstance().previewWrap.source.indexOf(galleryAdapter?.currentMedia)
                    if (viewPager.currentItem != currentItem && currentItem != RecyclerView.NO_POSITION) {
                        mAdapter.notifyDataSetChanged()
                        viewPager.setCurrentItem(currentItem, false)
                    }
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(rvGallery)
        galleryAdapter?.setOnLongItemClickListener(object : OnLongClickListener<LocalMedia> {
            override fun onLongClick(
                holder: RecyclerView.ViewHolder,
                position: Int,
                data: LocalMedia
            ) {
                val activity = requireActivity()
                val vibrator =
                    activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                if (SdkVersionUtils.isO()) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            50,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator.vibrate(50)
                }
                galleryAdapter?.let {
                    if (holder.layoutPosition != it.itemCount - 1) {
                        itemTouchHelper.startDrag(holder)
                    }
                }
            }
        })
    }

    override fun onMergeEditorData(data: Intent?) {
        super.onMergeEditorData(data)
        val media = TempDataProvider.getInstance().previewWrap.source[viewPager.currentItem]
        val position = galleryAdapter?.data?.indexOf(media) ?: 0
        if (position >= 0) {
            galleryAdapter?.notifyItemChanged(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSelectionResultChange(change: LocalMedia?) {
        super.onSelectionResultChange(change)
        if (galleryAdapter != null) {
            mTvComplete?.isEnabled = true
            galleryAdapter?.selectResult = TempDataProvider.getInstance().selectResult
            galleryAdapter?.notifyDataSetChanged()
            if (TempDataProvider.getInstance().selectResult.contains(change)) {
                val lastPosition = galleryAdapter?.itemCount ?: 0 - 1
                if (lastPosition >= 0) {
                    rvGallery.smoothScrollToPosition(lastPosition)
                }
            }
            if (!TempDataProvider.getInstance().previewWrap.isBottomPreview) {
                rvGallery.visibility =
                    if (galleryAdapter?.selectResult?.isEmpty() == true) View.GONE else View.VISIBLE
            }
            selectResultSort()
        }
    }

    private fun selectResultSort() {
        if (moveFromPosition != -1 && moveToPosition != -1) {
            if (moveFromPosition < moveToPosition) {
                for (i in moveFromPosition until moveToPosition) {
                    if (TempDataProvider.getInstance().selectResult.size > i + 1) {
                        Collections.swap(TempDataProvider.getInstance().selectResult, i, i + 1)
                    }
                }
            } else {
                for (i in moveFromPosition downTo moveToPosition + 1) {
                    if (TempDataProvider.getInstance().selectResult.size > i) {
                        Collections.swap(TempDataProvider.getInstance().selectResult, i, i - 1)
                    }
                }
            }
            moveFromPosition = -1
            moveToPosition = -1
        }
    }

    override fun onViewPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        super.onViewPageScrolled(position, positionOffset, positionOffsetPixels)
        if (TempDataProvider.getInstance().previewWrap.source.size > position) {
            updateGallerySelected(
                if (positionOffsetPixels < screenWidth / 2) position
                else position + 1
            )
        }
    }

    override fun onViewPageSelected(position: Int) {
        super.onViewPageSelected(position)
        updateGallerySelected(position)
    }

    private fun updateGallerySelected(position: Int) {
        // update old selected position
        val oldPosition = galleryAdapter?.data?.indexOf(galleryAdapter?.currentMedia) ?: -1
        if (oldPosition >= 0) {
            galleryAdapter?.notifyItemChanged(oldPosition)
        }
        // update new selected position
        val media = TempDataProvider.getInstance().previewWrap.source[position]
        galleryAdapter?.currentMedia = media
        val newPosition = galleryAdapter?.data?.indexOf(media) ?: -1
        if (newPosition >= 0) {
            galleryAdapter?.notifyItemChanged(newPosition)
        }
    }

    private class GalleryAdapter(
        var config: SelectorConfig,
        var isBottomPreview: Boolean,
        var data: MutableList<LocalMedia>
    ) :
        RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
        var currentMedia: LocalMedia? = null
        var selectResult: MutableList<LocalMedia>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val resource = config.layoutSource[LayoutSource.SELECTOR_NUMBER_PREVIEW_GALLERY]
                ?: R.layout.ps_preview_gallery_item
            return GalleryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(resource, parent, false)
            )
        }

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            val media = data[position]
            if (isBottomPreview) {
                val colorFilter = getColorFilter(
                    holder.itemView.context,
                    if (selectResult?.contains(media) == true) R.color.ps_color_transparent else R.color.ps_color_half_white
                )
                holder.ivCover.colorFilter = colorFilter
            }
            holder.viewBorder.visibility =
                if (isSelected(currentMedia, media)) View.VISIBLE else View.INVISIBLE
            holder.ivEditor.visibility = if (media.isEditor()) View.VISIBLE else View.GONE
            holder.ivPlay.visibility =
                if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) View.VISIBLE else View.GONE
            config.imageEngine?.loadListImage(
                holder.ivCover.context,
                media.getAvailablePath(),
                holder.ivCover
            )
            holder.itemView.setOnClickListener {
                mItemClickListener?.onItemClick(position, media)
            }
            holder.itemView.setOnLongClickListener {
                mLongClickListener?.onLongClick(holder, position, media)
                true
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ivCover: ImageView = itemView.findViewById(R.id.iv_image)
            val ivEditor: ImageView = itemView.findViewById(R.id.iv_editor)
            val ivPlay: ImageView = itemView.findViewById(R.id.iv_play)
            val viewBorder: View = itemView.findViewById(R.id.view_border)
        }

        fun isSelected(currentMedia: LocalMedia?, media: LocalMedia): Boolean {
            return TextUtils.equals(currentMedia?.path, media.path) || currentMedia?.id == media.id
        }

        private var mItemClickListener: OnItemClickListener<LocalMedia>? = null

        fun setOnItemClickListener(l: OnItemClickListener<LocalMedia>) {
            this.mItemClickListener = l
        }

        private var mLongClickListener: OnLongClickListener<LocalMedia>? = null
        fun setOnLongItemClickListener(l: OnLongClickListener<LocalMedia>?) {
            this.mLongClickListener = l
        }
    }
}