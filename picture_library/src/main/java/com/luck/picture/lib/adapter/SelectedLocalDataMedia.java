package com.luck.picture.lib.adapter;

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

public class SelectedLocalDataMedia extends RecyclerView
        .Adapter<SelectedLocalDataMedia.SelectedLocalDataMediaViewHolder> {

    private List<LocalMedia> list = new ArrayList<>();

    @NonNull
    @Override
    public SelectedLocalDataMedia.SelectedLocalDataMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View inflate = View.inflate(parent.getContext(), R.layout.selected_local_data, null);

        SelectedLocalDataMediaViewHolder selectedLocalDataMediaViewHolder
                = new SelectedLocalDataMediaViewHolder(inflate);
        return selectedLocalDataMediaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedLocalDataMedia
            .SelectedLocalDataMediaViewHolder holder, int position) {

        if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadGridImageRound(holder.itemView.getContext(),
                    list.get(position).getPath(), holder.ivCover);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<LocalMedia> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class SelectedLocalDataMediaViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivClose;
        private ImageView ivCover;
        public SelectedLocalDataMediaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivClose = itemView.findViewById(R.id.iv_close);
            ivCover = itemView.findViewById(R.id.iv_cover);
        }
    }
}
