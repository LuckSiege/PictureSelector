package com.luck.picture.lib.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.adapter.holder.BaseRecyclerMediaHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * @author：luck
 * @date：2016-12-30 12:02
 * @describe：PictureImageGridAdapter
 */
public class PictureImageGridAdapter extends RecyclerView.Adapter<BaseRecyclerMediaHolder> {

    private boolean showCamera;
    private List<LocalMedia> mData = new ArrayList<>();
    private final PictureSelectionConfig mConfig;

    public PictureImageGridAdapter(PictureSelectionConfig mConfig) {
        this.mConfig = mConfig;
        this.showCamera = mConfig.isDisplayCamera;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataAndDataSetChanged(List<LocalMedia> result) {
        if (result != null) {
            this.mData = result;
            notifyDataSetChanged();
        }
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public List<LocalMedia> getData() {
        return mData;
    }

    public boolean isDataEmpty() {
        return mData.size() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return PictureConfig.ADAPTER_TYPE_CAMERA;
        } else {
            int adapterPosition = showCamera ? position - 1 : position;
            String mimeType = mData.get(adapterPosition).getMimeType();
            if (PictureMimeType.isHasVideo(mimeType)) {
                return PictureConfig.ADAPTER_TYPE_VIDEO;
            } else if (PictureMimeType.isHasAudio(mimeType)) {
                return PictureConfig.ADAPTER_TYPE_AUDIO;
            }
            return PictureConfig.ADAPTER_TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public BaseRecyclerMediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseRecyclerMediaHolder.generate(parent, viewType, getItemResourceId(viewType), mConfig);
    }

    /**
     * getItemResourceId
     *
     * @param viewType
     * @return
     */
    private int getItemResourceId(int viewType) {
        switch (viewType) {
            case PictureConfig.ADAPTER_TYPE_CAMERA:
                return R.layout.ps_item_grid_camera;
            case PictureConfig.ADAPTER_TYPE_VIDEO:
                return R.layout.ps_item_grid_video;
            case PictureConfig.ADAPTER_TYPE_AUDIO:
                return R.layout.ps_item_grid_audio;
            default:
                return R.layout.ps_item_grid_image;
        }
    }

    @Override
    public void onBindViewHolder(@NotNull final BaseRecyclerMediaHolder holder, final int position) {
        if (getItemViewType(position) == PictureConfig.ADAPTER_TYPE_CAMERA) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.openCameraClick();
                    }
                }
            });
        } else {
            int adapterPosition = showCamera ? position - 1 : position;
            LocalMedia media = mData.get(adapterPosition);
            holder.bindData(media, adapterPosition);
            holder.setOnItemClickListener(listener);
        }
    }


    @Override
    public int getItemCount() {
        return showCamera ? mData.size() + 1 : mData.size();
    }


    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        /**
         * 拍照
         */
        void openCameraClick();

        /**
         * 列表item点击事件
         *
         * @param selectedView     所产生点击事件的View
         * @param position 当前下标
         * @param media    当前LocalMedia对象
         */
        void onItemClick(View selectedView, int position, LocalMedia media);

        /**
         * 列表勾选点击事件
         *
         * @param selectedView     所产生点击事件的View
         * @param position 当前下标
         * @param media    当前LocalMedia对象
         */
        int onSelected(View selectedView, int position, LocalMedia media);
    }
}
