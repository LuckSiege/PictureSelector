package com.luck.picture.lib.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.holder.BasePreviewHolder;
import com.luck.picture.lib.adapter.holder.PreviewAudioHolder;
import com.luck.picture.lib.adapter.holder.PreviewVideoHolder;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/23 1:11 下午
 * @describe：PicturePreviewAdapter2
 */
public class PicturePreviewAdapter extends RecyclerView.Adapter<BasePreviewHolder> {

    private List<LocalMedia> mData;
    private BasePreviewHolder.OnPreviewEventListener onPreviewEventListener;
    private final LinkedHashMap<Integer, BasePreviewHolder> mHolderCache = new LinkedHashMap<>();

    public BasePreviewHolder getCurrentHolder(int position) {
        return mHolderCache.get(position);
    }

    public void setData(List<LocalMedia> list) {
        this.mData = list;
    }

    public void setOnPreviewEventListener(BasePreviewHolder.OnPreviewEventListener listener) {
        this.onPreviewEventListener = listener;
    }

    @NonNull
    @Override
    public BasePreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResourceId;
        if (viewType == BasePreviewHolder.ADAPTER_TYPE_VIDEO) {
            layoutResourceId = InjectResourceSource.getLayoutResource(parent.getContext(), InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE);
            return BasePreviewHolder.generate(parent, viewType, layoutResourceId != 0 ? layoutResourceId : R.layout.ps_preview_video);
        } else if (viewType == BasePreviewHolder.ADAPTER_TYPE_AUDIO) {
            layoutResourceId = InjectResourceSource.getLayoutResource(parent.getContext(), InjectResourceSource.PREVIEW_ITEM_AUDIO_LAYOUT_RESOURCE);
            return BasePreviewHolder.generate(parent, viewType, layoutResourceId != 0 ? layoutResourceId : R.layout.ps_preview_audio);
        } else {
            layoutResourceId = InjectResourceSource.getLayoutResource(parent.getContext(), InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE);
            return BasePreviewHolder.generate(parent, viewType, layoutResourceId != 0 ? layoutResourceId : R.layout.ps_preview_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BasePreviewHolder holder, int position) {
        holder.setOnPreviewEventListener(onPreviewEventListener);
        LocalMedia media = mData.get(position);
        mHolderCache.put(position, holder);
        holder.bindData(media, position);
    }


    @Override
    public int getItemViewType(int position) {
        if (PictureMimeType.isHasVideo(mData.get(position).getMimeType())) {
            return BasePreviewHolder.ADAPTER_TYPE_VIDEO;
        } else if (PictureMimeType.isHasAudio(mData.get(position).getMimeType())) {
            return BasePreviewHolder.ADAPTER_TYPE_AUDIO;
        } else {
            return BasePreviewHolder.ADAPTER_TYPE_IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BasePreviewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BasePreviewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onViewDetachedFromWindow();
    }

    /**
     * 设置播放按钮状态
     *
     * @param position
     */
    public void setVideoPlayButtonUI(int position) {
        BasePreviewHolder currentHolder = getCurrentHolder(position);
        if (currentHolder instanceof PreviewVideoHolder) {
            PreviewVideoHolder videoHolder = (PreviewVideoHolder) currentHolder;
            if (videoHolder.ivPlayButton.getVisibility() == View.GONE) {
                videoHolder.ivPlayButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 释放当前视频相关
     */
    public void destroy() {
        for (Integer key : mHolderCache.keySet()) {
            BasePreviewHolder holder = mHolderCache.get(key);
            if (holder instanceof PreviewVideoHolder) {
                PreviewVideoHolder videoHolder = (PreviewVideoHolder) holder;
                videoHolder.releaseVideo();
            } else if (holder instanceof PreviewAudioHolder) {
                PreviewAudioHolder audioHolder = (PreviewAudioHolder) holder;
                audioHolder.releaseAudio();
            }
        }
    }
}
