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

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.CutInfo;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.MimeType;
import com.yalantis.ucrop.util.SdkUtils;

import java.io.File;
import java.util.List;

/**
 * @author：luck
 * @date：2016-12-31 22:22
 * @describe：图片列表
 */


public class PicturePhotoGalleryAdapter extends RecyclerView.Adapter<PicturePhotoGalleryAdapter.ViewHolder> {
    private final int maxImageWidth = 200;
    private final int maxImageHeight = 220;
    private Context context;
    private List<CutInfo> list;
    private LayoutInflater mInflater;

    public PicturePhotoGalleryAdapter(Context context, List<CutInfo> list) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    public void setData(List<CutInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = mInflater.inflate(R.layout.ucrop_picture_gf_adapter_edit_list,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String path = "";
        CutInfo photoInfo = list.get(position);
        if (photoInfo != null) {
            path = photoInfo.getPath();
        }
        if (photoInfo.isCut()) {
            holder.iv_dot.setVisibility(View.VISIBLE);
            holder.iv_dot.setImageResource(R.drawable.ucrop_oval_true);
        } else {
            holder.iv_dot.setVisibility(View.INVISIBLE);
        }
        boolean isHasVideo = MimeType.isHasVideo(photoInfo.getMimeType());
        if (isHasVideo) {
            holder.mIvPhoto.setVisibility(View.GONE);
            holder.mIvVideo.setVisibility(View.VISIBLE);
            holder.mIvVideo.setImageResource(R.drawable.ucrop_ic_default_video);
        } else {
            holder.mIvPhoto.setVisibility(View.VISIBLE);
            holder.mIvVideo.setVisibility(View.GONE);
            Uri uri = SdkUtils.isQ() || MimeType.isHttp(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
            holder.tvGif.setVisibility(MimeType.isGif(photoInfo.getMimeType()) ? View.VISIBLE : View.GONE);
            BitmapLoadUtils.decodeBitmapInBackground(context, uri, photoInfo.getHttpOutUri(), maxImageWidth,
                    maxImageHeight, new BitmapLoadCallback() {
                        @Override
                        public void onBitmapLoaded(@NonNull Bitmap bitmap,
                                                   @NonNull ExifInfo exifInfo,
                                                   @NonNull String imageInputPath,
                                                   @Nullable String imageOutputPath) {
                            if (holder.mIvPhoto != null && bitmap != null) {
                                holder.mIvPhoto.setImageBitmap(bitmap);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Exception bitmapWorkerException) {
                            if (holder.mIvPhoto != null) {
                                holder.mIvPhoto.setImageResource(R.color.ucrop_color_ba3);
                            }
                        }
                    });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(holder.getAdapterPosition(), v);
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
