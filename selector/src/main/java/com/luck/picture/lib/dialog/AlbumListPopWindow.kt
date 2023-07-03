package com.luck.picture.lib.dialog

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.MediaAlbumAdapter
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.provider.SelectorProviders
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.SdkVersionUtils.isMinM
import com.luck.picture.lib.utils.SdkVersionUtils.isN
import com.luck.picture.lib.widget.WrapContentLinearLayoutManager

/**
 * @author：luck
 * @date：2021/11/17 2:33 下午
 * @describe：AlbumListPopWindow
 */
open class AlbumListPopWindow(context: Context) : PopupWindow() {
    val config = SelectorProviders.getInstance().getConfig()
    var windMask: View
    var rvList: RecyclerView
    var rootView: RelativeLayout
    var bodyLayout: ViewGroup
    lateinit var mediaAlbumAdapter: MediaAlbumAdapter
    var defaultMaxCount = 10
    var isExecuteDismiss = false

    init {
        val resource = config.layoutSource[LayoutSource.ALBUM_WINDOW] ?: R.layout.ps_album_window
        this.contentView = LayoutInflater.from(context).inflate(resource, null)
        this.bodyLayout = contentView.findViewById(R.id.round_group)
        this.rootView = contentView.findViewById(R.id.rootView)
        this.rvList = contentView.findViewById(R.id.album_list)
        this.windMask = contentView.findViewById(R.id.view_mask)
        this.initViews(contentView)
        this.width = RelativeLayout.LayoutParams.MATCH_PARENT
        this.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        this.animationStyle = 0
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.update()
        this.rootView.setOnClickListener {
            if (isMinM()) {
                dismiss()
            }
        }
        this.windMask.setOnClickListener {
            dismiss()
        }
        this.initRecyclerView()
    }

    open fun initViews(contentView: View) {

    }

    open fun initRecyclerView() {
        rvList.layoutManager = WrapContentLinearLayoutManager(rvList.context)
        (rvList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        rvList.itemAnimator = null
        mediaAlbumAdapter = MediaAlbumAdapter(config)
        rvList.adapter = mediaAlbumAdapter
    }

    open fun setAlbumList(albumList: MutableList<LocalMediaAlbum>) {
        mediaAlbumAdapter.setAlbumList(albumList)
        val windowMaxHeight = (DensityUtil.getScreenHeight(rvList.context) * 0.6).toInt()
        val layoutParams = rvList.layoutParams
        layoutParams.height =
            if (albumList.size > defaultMaxCount) windowMaxHeight else ViewGroup.LayoutParams.WRAP_CONTENT
    }

    fun notifyItemRangeChanged() {
        mediaAlbumAdapter.notifyItemRangeChanged(0, this.mediaAlbumAdapter.itemCount)
    }

    fun getAlbumList(): MutableList<LocalMediaAlbum> {
        return mediaAlbumAdapter.getAlbumList()
    }

    fun getAlbum(bucketId: Long): LocalMediaAlbum? {
        return mediaAlbumAdapter.getAlbum(bucketId)
    }

    fun setOnItemClickListener(listener: OnItemClickListener<LocalMediaAlbum>?) {
        this.mediaAlbumAdapter.setOnItemClickListener(listener)
    }

    open fun notifyChangedSelectTag(result: MutableList<LocalMedia>) {
        val albumList = mediaAlbumAdapter.getAlbumList()
        for (i in albumList.indices) {
            val mediaAlbum = albumList[i]
            mediaAlbum.isSelectedTag = false
            mediaAlbumAdapter.notifyItemChanged(i)
            for (j in 0 until result.size) {
                val media = result[j]
                if (TextUtils.equals(mediaAlbum.bucketDisplayName, media.bucketDisplayName)
                    || mediaAlbum.isAllAlbum()
                ) {
                    mediaAlbum.isSelectedTag = true
                    mediaAlbumAdapter.notifyItemChanged(i)
                    break
                }
            }
        }
    }

    override fun showAsDropDown(anchor: View) {
        if (isN()) {
            val location = IntArray(2)
            anchor.getLocationInWindow(location)
            showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] + anchor.height)
        } else {
            super.showAsDropDown(anchor)
        }
        isExecuteDismiss = false
        windowStatusListener?.onShowing(true)
        bodyLayout.startAnimation(showAnimation(anchor.context))
        windMask.animate().alpha(1F).setDuration(bodyLayout.animation.duration).start()
    }

    override fun dismiss() {
        if (isExecuteDismiss) {
            return
        }
        isExecuteDismiss = true
        windowStatusListener?.onShowing(false)
        bodyLayout.startAnimation(hideAnimation(bodyLayout.context))
        windMask.animate().alpha(0F).setDuration(bodyLayout.animation.duration).start()
        bodyLayout.postDelayed({
            super.dismiss()
            isExecuteDismiss = false
        }, bodyLayout.animation.duration)
    }

    open fun showAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.ps_anim_album_show)
    }

    open fun hideAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.ps_anim_album_dismiss)
    }

    private var windowStatusListener: OnWindowStatusListener? = null

    fun setOnWindowStatusListener(listener: OnWindowStatusListener) {
        this.windowStatusListener = listener
    }

    interface OnWindowStatusListener {
        fun onShowing(isShowing: Boolean)
    }
}