package com.luck.picture.lib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 20:50
 * @describe：微信风格选择后图片预览
 */
public class PictureWeChatPreviewGalleryAdapter
        extends RecyclerView.Adapter<PictureWeChatPreviewGalleryAdapter.ViewHolder> {
    private List<LocalMedia> list;
    private PictureSelectionConfig config;

    public PictureWeChatPreviewGalleryAdapter(PictureSelectionConfig config) {
        super();
        this.config = config;
    }

    public void setNewData(List<LocalMedia> data) {
        this.list = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    public void addSingleMediaToData(LocalMedia media) {
        if (this.list != null) {
            list.clear();
            list.add(media);
            notifyDataSetChanged();
        }
    }

    public void removeMediaToData(LocalMedia media) {
        if (this.list != null && this.list.size() > 0) {
            this.list.remove(media);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picture_wechat_preview_gallery, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalMedia item = getItem(position);
        if (item != null) {
            holder.viewBorder.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
            if (config != null && config.imageEngine != null) {
                config.imageEngine.loadImage(holder.itemView.getContext(), item.getPath(), holder.ivImage);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null && holder.getAdapterPosition() >= 0) {
                    listener.onItemClick(holder.getAdapterPosition(), getItem(position), v);
                }
            });
        }
    }

    public LocalMedia getItem(int position) {
        return list != null && list.size() > 0 ? list.get(position) : null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        View viewBorder;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            viewBorder = itemView.findViewById(R.id.viewBorder);
        }
    }

    private OnItemClickListener listener;

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, LocalMedia media, View v);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
