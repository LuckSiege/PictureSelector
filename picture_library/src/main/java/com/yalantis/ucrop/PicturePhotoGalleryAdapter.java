/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.yalantis.ucrop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import java.util.List;

/**
 * @author：luck
 * @date：2016-12-31 22:22
 * @describe：图片列表
 */


public class PicturePhotoGalleryAdapter extends RecyclerView.Adapter<PicturePhotoGalleryAdapter.ViewHolder> {
    private final List<LocalMedia> list;

    public PicturePhotoGalleryAdapter(List<LocalMedia> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ucrop_picture_gf_adapter_edit_list,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocalMedia photoInfo = list.get(position);
        String path = photoInfo.getPath();
        if (photoInfo.isCut()) {
            holder.iv_dot.setVisibility(View.VISIBLE);
            holder.iv_dot.setImageResource(R.drawable.ucrop_oval_true);
        } else {
            holder.iv_dot.setVisibility(View.INVISIBLE);
        }
        boolean isHasVideo = PictureMimeType.isHasVideo(photoInfo.getMimeType());
        if (isHasVideo) {
            holder.mIvPhoto.setVisibility(View.GONE);
            holder.mIvVideo.setVisibility(View.VISIBLE);
            holder.mIvVideo.setImageResource(R.drawable.ucrop_ic_default_video);
        } else {
            holder.mIvPhoto.setVisibility(View.VISIBLE);
            holder.mIvVideo.setVisibility(View.GONE);
            holder.tvGif.setVisibility(PictureMimeType.isGif(photoInfo.getMimeType()) ? View.VISIBLE : View.GONE);
            if (PictureSelectionConfig.imageEngine != null) {
                PictureSelectionConfig.imageEngine.loadGridImage(holder.itemView.getContext(), path, holder.mIvPhoto);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.getAbsoluteAdapterPosition(), v);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvPhoto;
        ImageView iv_dot;
        ImageView mIvVideo;
        TextView tvGif;

        public ViewHolder(View view) {
            super(view);
            mIvPhoto = view.findViewById(R.id.iv_photo);
            mIvVideo = view.findViewById(R.id.iv_video);
            iv_dot = view.findViewById(R.id.iv_dot);
            tvGif = view.findViewById(R.id.tv_gif);
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
