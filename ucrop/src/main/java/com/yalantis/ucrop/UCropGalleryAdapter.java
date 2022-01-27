package com.yalantis.ucrop;

import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author：luck
 * @date：2016-12-31 22:22
 * @describe：UCropGalleryAdapter
 */

public class UCropGalleryAdapter extends RecyclerView.Adapter<UCropGalleryAdapter.ViewHolder> {
    private final List<String> list;
    private int currentSelectPosition;

    public UCropGalleryAdapter(List<String> list) {
        this.list = list;
    }

    public void setCurrentSelectPosition(int currentSelectPosition) {
        this.currentSelectPosition = currentSelectPosition;
    }

    public int getCurrentSelectPosition() {
        return currentSelectPosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucrop_gallery_adapter_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String path = list.get(position);
        if (UCropDevelopConfig.imageEngine != null) {
            UCropDevelopConfig.imageEngine.loadImage(holder.itemView.getContext(), path, holder.mIvPhoto);
        }
        ColorFilter colorFilter;
        if (currentSelectPosition == position) {
            holder.mViewCurrentSelect.setVisibility(View.VISIBLE);
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat
                    (ContextCompat.getColor(holder.itemView.getContext(), R.color.ucrop_color_80),
                            BlendModeCompat.SRC_ATOP);
        } else {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat
                    (ContextCompat.getColor(holder.itemView.getContext(), R.color.ucrop_color_20),
                            BlendModeCompat.SRC_ATOP);
            holder.mViewCurrentSelect.setVisibility(View.GONE);
        }
        holder.mIvPhoto.setColorFilter(colorFilter);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAbsoluteAdapterPosition(), v);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvPhoto;
        View mViewCurrentSelect;

        public ViewHolder(View view) {
            super(view);
            mIvPhoto = view.findViewById(R.id.iv_photo);
            mViewCurrentSelect = view.findViewById(R.id.view_current_select);
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }
}
