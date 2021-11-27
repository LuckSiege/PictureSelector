package com.luck.picture.lib.adapter.holder;

import android.graphics.ColorFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.StyleUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 20:50
 * @describe：preview gallery
 */
public class PreviewGalleryAdapter extends RecyclerView.Adapter<PreviewGalleryAdapter.ViewHolder> {
    private final List<LocalMedia> mData;

    public PreviewGalleryAdapter(List<LocalMedia> list) {
        this.mData = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preview_gallery_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalMedia item = mData.get(position);
        ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(holder.itemView.getContext(), item.isMaxSelectEnabledMask()
                        ? R.color.ps_color_half_white : R.color.ps_color_transparent), BlendModeCompat.SRC_ATOP);
        if (item.isChecked() && item.isMaxSelectEnabledMask()) {
            holder.viewBorder.setVisibility(View.VISIBLE);
        } else {
            holder.viewBorder.setVisibility(item.isChecked() ? View.VISIBLE : View.GONE);
        }
        String path = item.getPath();
        if (item.isEditorImage() && !TextUtils.isEmpty(item.getCutPath())) {
            path = item.getCutPath();
            holder.ivEditor.setVisibility(View.VISIBLE);
        } else {
            holder.ivEditor.setVisibility(View.GONE);
        }
        holder.ivImage.setColorFilter(colorFilter);
        if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(), path, holder.ivImage);
        }
        holder.ivPlay.setVisibility(PictureMimeType.isHasVideo(item.getMimeType()) ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.getAbsoluteAdapterPosition(), item, view);
                }
            }
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageView ivPlay;
        ImageView ivEditor;
        View viewBorder;

        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivEditor = itemView.findViewById(R.id.ivEditor);
            viewBorder = itemView.findViewById(R.id.viewBorder);
            SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
            if (StyleUtils.checkStyleValidity(selectMainStyle.getAdapterImageEditorResources())) {
                ivEditor.setImageResource(selectMainStyle.getAdapterImageEditorResources());
            }
            if (StyleUtils.checkStyleValidity(selectMainStyle.getAdapterPreviewGalleryFrameResource())) {
                viewBorder.setBackgroundResource(selectMainStyle.getAdapterPreviewGalleryFrameResource());
            }
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
        return mData.size();
    }
}
