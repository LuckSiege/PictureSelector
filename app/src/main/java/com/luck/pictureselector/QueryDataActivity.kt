package com.luck.pictureselector

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.luck.picture.lib.config.MediaType
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener
import com.luck.picture.lib.loader.MediaLoader
import com.luck.picture.lib.model.PictureSelector
import com.luck.picture.lib.utils.DateUtils.formatDurationTime
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SelectorLogUtils
import com.luck.picture.lib.widget.RecyclerPreloadView
import kotlinx.coroutines.launch

class QueryDataActivity : AppCompatActivity() {
    private lateinit var mRecycler: RecyclerPreloadView
    private var mData: MutableList<LocalMedia> = mutableListOf()
    private var mediaType: MediaType = MediaType.ALL
    private lateinit var loader: MediaLoader
    private val pageSize = 60
    private var page = 1

    /**
     * Media list data LiveData
     */
    private val mediaLiveData = MutableLiveData<MutableList<LocalMedia>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_data)
        mRecycler = findViewById(R.id.recycler)
        mRecycler.addItemDecoration(
            GridSpacingItemDecoration(
                4,
                dip2px(this, 1f), false
            )
        )
        mRecycler.layoutManager = GridLayoutManager(this, 4)
        val itemAnimator = mRecycler.itemAnimator
        if (itemAnimator != null) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            mRecycler.itemAnimator = null
        }
        val mediaListAdapter = MediaListAdapter(mData)
        mRecycler.adapter = mediaListAdapter

        findViewById<Button>(R.id.btn_all).setOnClickListener {
            Glide.with(this).resumeRequests()
            page = 1
            mediaType = MediaType.ALL
            lifecycleScope.launch {
                loader = PictureSelector.create(this@QueryDataActivity)
                    .dataSource(mediaType)
                    .buildMediaLoader()
                mediaLiveData.postValue(loader.loadMedia(pageSize))
            }
        }
        findViewById<Button>(R.id.btn_image).setOnClickListener {
            Glide.with(this).resumeRequests()
            page = 1
            mediaType = MediaType.IMAGE
            lifecycleScope.launch {
                loader = PictureSelector.create(this@QueryDataActivity)
                    .dataSource(mediaType)
                    .buildMediaLoader()
                mediaLiveData.postValue(loader.loadMedia(pageSize))
            }
        }
        findViewById<Button>(R.id.btn_video).setOnClickListener {
            Glide.with(this).resumeRequests()
            page = 1
            mediaType = MediaType.VIDEO
            lifecycleScope.launch {
                loader = PictureSelector.create(this@QueryDataActivity)
                    .dataSource(mediaType)
                    .buildMediaLoader()
                mediaLiveData.postValue(loader.loadMedia(pageSize))
            }
        }
        findViewById<Button>(R.id.btn_audio).setOnClickListener {
            Glide.with(this).pauseAllRequests()
            page = 1
            mediaType = MediaType.AUDIO
            lifecycleScope.launch {
                loader = PictureSelector.create(this@QueryDataActivity)
                    .dataSource(mediaType)
                    .buildMediaLoader()
                mediaLiveData.postValue(loader.loadMedia(pageSize))
            }
        }

        lifecycleScope.launch {
            page = 1
            loader = PictureSelector.create(this@QueryDataActivity)
                .dataSource(mediaType)
                .buildMediaLoader()
            mediaLiveData.postValue(loader.loadMedia(pageSize))
        }

        mediaLiveData.observe(this) { mediaList ->
            if (page == 1) {
                val oldItemCount = mData.size
                mData.clear()
                mediaListAdapter.notifyItemRangeChanged(0, oldItemCount)
                mData.addAll(mediaList)
                mediaListAdapter.notifyItemRangeChanged(0, mData.size)
                mRecycler.smoothScrollToPosition(0)
            } else {
                val positionStar = mData.size
                mData.addAll(mediaList)
                mediaListAdapter.notifyItemRangeChanged(positionStar, mData.size)
            }
        }

        mRecycler.setEnabledLoadMore(true)
        mRecycler.setOnRecyclerViewPreloadListener(object : OnRecyclerViewPreloadMoreListener {
            override fun onPreloadMore() {
                lifecycleScope.launch {
                    page++
                    mediaLiveData.postValue(
                        loader.loadMediaMore(
                            SelectorConstant.DEFAULT_ALL_BUCKET_ID,
                            page,
                            pageSize
                        )
                    )
                }
            }
        })
    }

    class MediaListAdapter(private val list: List<LocalMedia>) :
        RecyclerView.Adapter<MediaListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gv_filter_image, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.mIvDel.visibility = View.GONE
            val media = list[position]
            val path = media.path
            val duration = media.duration
            viewHolder.mTvDuration.visibility =
                if (MediaUtils.hasMimeTypeOfVideo(media.mimeType)) View.VISIBLE else View.GONE
            if (MediaUtils.hasMimeTypeOfAudio(media.mimeType)) {
                viewHolder.mTvDuration.visibility = View.VISIBLE
                viewHolder.mTvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ps_ic_audio,
                    0,
                    0,
                    0
                )
                viewHolder.mImg.setImageResource(R.drawable.ps_audio_placeholder)
            } else {
                viewHolder.mTvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ps_ic_video,
                    0,
                    0,
                    0
                )
                Glide.with(viewHolder.itemView.context)
                    .load(if (MediaUtils.isContent(path!!)) Uri.parse(path) else path)
                    .centerCrop()
                    .placeholder(R.drawable.ps_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(viewHolder.mImg)
            }
            viewHolder.mTvDuration.text = formatDurationTime(duration)
            viewHolder.itemView.setOnClickListener {
                SelectorLogUtils.info("${media.mimeType}")
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var mImg: ImageView = view.findViewById(R.id.fiv)
            var mIvDel: ImageView = view.findViewById(R.id.iv_del)
            var mTvDuration: TextView = view.findViewById(R.id.tv_duration)

        }
    }
}