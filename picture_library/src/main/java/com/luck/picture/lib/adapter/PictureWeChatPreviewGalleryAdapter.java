package com.luck.picture.lib.adapter;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2019-11-30 20:50
 * @describe：WeChat style selected after image preview
 */
public class PictureWeChatPreviewGalleryAdapter
        extends RecyclerView.Adapter<PictureWeChatPreviewGalleryAdapter.ViewHolder> {
    private List<LocalMedia> mList = new ArrayList<>();
    private final PictureSelectionConfig config;

    public PictureWeChatPreviewGalleryAdapter(PictureSelectionConfig config) {
        super();
        this.config = config;
    }

    public void setNewData(List<LocalMedia> data, boolean isBottomPreview) {
        if (data != null) {
            if (isBottomPreview) {
                // 底部预览按钮进来重新加到一处新的集合区，因为取消选中不影响GalleryAdapter数据源
                mList.clear();
                mList.addAll(data);
            } else {
                // 非底部预览按钮进来GalleryAdapter数据源与已选中的数据共享同一片内存区域的数据
                mList = data;
            }
            notifyDataSetChanged();
        }
    }

    public void addSingleMediaToData(LocalMedia media) {
        mList.clear();
        mList.add(media);
        notifyDataSetChanged();
    }

    public void removeMediaToData(LocalMedia media) {
        if (mList.size() > 0) {
            mList.remove(media);
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
        ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(holder.itemView.getContext(), item.isMaxSelectEnabledMask() ? R.color.picture_color_half_white : R.color.picture_color_transparent), BlendModeCompat.SRC_ATOP);
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
        if (config != null && PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(), path, holder.ivImage);
        }
        holder.ivPlay.setVisibility(PictureMimeType.isHasVideo(item.getMimeType()) ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.getAbsoluteAdapterPosition() >= 0) {
                listener.onItemClick(holder.getAbsoluteAdapterPosition(), getItem(position), v);
            }
        });
    }

    public LocalMedia getItem(int position) {
        return mList.size() > 0 ? mList.get(position) : null;
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
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_bottom_gallery_frameBackground != 0) {
                    viewBorder.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_bottom_gallery_frameBackground);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_editor_tag_icon != 0) {
                    ivEditor.setImageResource(PictureSelectionConfig.uiStyle.picture_adapter_item_editor_tag_icon);
                }
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.picture_adapter_item_editor_tag_icon != 0) {
                    ivEditor.setImageResource(PictureSelectionConfig.style.picture_adapter_item_editor_tag_icon);
                }
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
        return mList.size();
    }
}
