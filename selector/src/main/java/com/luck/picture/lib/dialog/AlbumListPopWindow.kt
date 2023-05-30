package com.luck.picture.lib.dialog

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.MediaAlbumAdapter
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.SdkVersionUtils.isMinM
import com.luck.picture.lib.utils.SdkVersionUtils.isN
import com.luck.picture.lib.widget.WrapContentLinearLayoutManager

/**
 * @author：luck
 * @date：2021/11/17 2:33 下午
 * @describe：AlbumListPopWindow
 */
class AlbumListPopWindow(context: Context, private var config: SelectorConfig) : PopupWindow() {
    private var windMask: View
    private var rvList: RecyclerView
    private var rootView: RelativeLayout
    private var isExecuteDismiss: Boolean = false
    private lateinit var mediaAlbumAdapter: MediaAlbumAdapter
    private var defaultMaxCount = 10

    init {
        val resource = config.layoutSource[LayoutSource.ALBUM_WINDOW] ?: R.layout.ps_album_window
        contentView = LayoutInflater.from(context).inflate(resource, null)
        rootView = contentView.findViewById(R.id.rootView)
        rvList = contentView.findViewById(R.id.album_list)
        windMask = contentView.findViewById(R.id.view_mask)
        width = RelativeLayout.LayoutParams.MATCH_PARENT
        height = RelativeLayout.LayoutParams.WRAP_CONTENT
        animationStyle = R.style.PictureThemeWindowStyle
        isFocusable = true
        isOutsideTouchable = true
        update()
        rootView.setOnClickListener {
            if (isMinM()) {
                dismiss()
            }
        }
        windMask.setOnClickListener {
            dismiss()
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        rvList.layoutManager = WrapContentLinearLayoutManager(rvList.context)
        (rvList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        rvList.itemAnimator = null
        mediaAlbumAdapter = MediaAlbumAdapter(config)
        rvList.adapter = mediaAlbumAdapter
    }

    fun setAlbumList(albumList: MutableList<LocalMediaAlbum>) {
        mediaAlbumAdapter.setAlbumList(albumList)
        val windowMaxHeight = (DensityUtil.getScreenHeight(rvList.context) * 0.6).toInt()
        val layoutParams = rvList.layoutParams
        layoutParams.height =
            if (albumList.size > defaultMaxCount) windowMaxHeight else ViewGroup.LayoutParams.WRAP_CONTENT
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

    fun notifyChangedSelectTag(result: MutableList<LocalMedia>) {
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
        windMask.animate().alpha(1F).setDuration(250).setStartDelay(250).start()
        isExecuteDismiss = false
        windowStatusListener?.onShowing(true)
    }

    override fun dismiss() {
        if (isExecuteDismiss) {
            return
        }
        windMask.alpha = 0F;
        windowStatusListener?.onShowing(false)
        isExecuteDismiss = true
        windMask.post {
            super.dismiss()
            isExecuteDismiss = false;
        }
    }

    private var windowStatusListener: OnWindowStatusListener? = null

    fun setOnWindowStatusListener(listener: OnWindowStatusListener) {
        this.windowStatusListener = listener
    }

    interface OnWindowStatusListener {
        fun onShowing(isShowing: Boolean)
    }
}