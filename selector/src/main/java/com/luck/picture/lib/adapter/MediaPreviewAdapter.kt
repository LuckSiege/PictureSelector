package com.luck.picture.lib.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.base.BasePreviewMediaHolder
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.constant.MediaAdapterType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.interfaces.OnLongClickListener
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.MediaUtils

/**
 * @author：luck
 * @date：2023/1/4 4:58 下午
 * @describe：MediaPreviewAdapter
 */
open class MediaPreviewAdapter : RecyclerView.Adapter<BasePreviewMediaHolder>() {
    private lateinit var mData: MutableList<LocalMedia>
    private val config = SelectorProviders.getInstance().getConfig()
    private val holderFactory = ClassFactory.NewConstructorInstance()
    private val mViewHolderCache = LinkedHashMap<Int, BasePreviewMediaHolder>()
    private var isFirstAttachedToWindow = false
    open fun getCurrentViewHolder(position: Int): BasePreviewMediaHolder? {
        return mViewHolderCache[position]
    }

    fun getData(): MutableList<LocalMedia> {
        return mData
    }

    fun setDataNotifyChanged(data: MutableList<LocalMedia>) {
        this.mData = data
        this.notifyItemRangeChanged(0, mData.size)
    }

    /**
     * User can rewrite to realize user-defined requirements
     */
    open fun onCreateImageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): BasePreviewMediaHolder {
        val resource =
            config.layoutSource[LayoutSource.PREVIEW_ITEM_IMAGE] ?: R.layout.ps_preview_image
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(PreviewImageHolder::class.java)
        return holderFactory.create(clz, itemView)
    }

    /**
     * User can rewrite to realize user-defined requirements
     */
    open fun onCreateVideoViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): BasePreviewMediaHolder {
        val resource =
            config.layoutSource[LayoutSource.PREVIEW_ITEM_VIDEO] ?: R.layout.ps_preview_video
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(PreviewVideoHolder::class.java)
        return holderFactory.create(clz, itemView)
    }

    /**
     * User can rewrite to realize user-defined requirements
     */
    open fun onCreateAudioViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
    ): BasePreviewMediaHolder {
        val resource =
            config.layoutSource[LayoutSource.PREVIEW_ITEM_AUDIO] ?: R.layout.ps_preview_audio
        val itemView = inflater.inflate(resource, parent, false)
        val clz = config.registry.get(PreviewAudioHolder::class.java)
        return holderFactory.create(clz, itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePreviewMediaHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder: BasePreviewMediaHolder = when (viewType) {
            MediaAdapterType.TYPE_VIDEO -> onCreateVideoViewHolder(inflater, parent)
            MediaAdapterType.TYPE_AUDIO -> onCreateAudioViewHolder(inflater, parent)
            else -> onCreateImageViewHolder(inflater, parent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: BasePreviewMediaHolder, position: Int) {
        mViewHolderCache[position] = holder
        holder.setOnClickListener(mClickListener)
        holder.setOnTitleChangeListener(mTitleChangeListener)
        holder.setOnLongClickListener(mLongClickListener)
        holder.bindData(mData[position], position)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        val mimeType = mData[position].mimeType
        if (MediaUtils.hasMimeTypeOfVideo(mimeType)) {
            return MediaAdapterType.TYPE_VIDEO
        } else if (MediaUtils.hasMimeTypeOfAudio(mimeType)) {
            return MediaAdapterType.TYPE_AUDIO
        }
        return MediaAdapterType.TYPE_IMAGE
    }

    override fun onViewAttachedToWindow(holder: BasePreviewMediaHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
        if (!isFirstAttachedToWindow) {
            onAttachedToWindowListener?.onViewAttachedToWindow(holder)
            isFirstAttachedToWindow = true
        }
    }

    override fun onViewDetachedFromWindow(holder: BasePreviewMediaHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    private var onAttachedToWindowListener: OnAttachedToWindowListener? = null

    fun setOnFirstAttachedToWindowListener(l: OnAttachedToWindowListener) {
        this.onAttachedToWindowListener = l
    }

    interface OnAttachedToWindowListener {
        fun onViewAttachedToWindow(holder: BasePreviewMediaHolder)
    }

    private var mClickListener: OnClickListener? = null

    fun setOnClickListener(l: OnClickListener?) {
        this.mClickListener = l
    }

    interface OnClickListener {
        fun onClick(media: LocalMedia)
    }

    private var mLongClickListener: OnLongClickListener<LocalMedia>? = null

    fun setOnLongClickListener(l: OnLongClickListener<LocalMedia>?) {
        this.mLongClickListener = l
    }

    private var mTitleChangeListener: OnTitleChangeListener? = null

    fun setOnTitleChangeListener(l: OnTitleChangeListener?) {
        this.mTitleChangeListener = l
    }

    interface OnTitleChangeListener {
        fun onTitle(title: String?)
    }


    open fun destroy() {
        for (key in mViewHolderCache.keys) {
            mViewHolderCache[key]?.release()
        }
    }
}