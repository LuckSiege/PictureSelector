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
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yalantis.ucrop.callback.BitmapLoadShowCallback;
import com.yalantis.ucrop.model.CutInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.FileUtils;

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
    private boolean isAndroidQ;

    public PicturePhotoGalleryAdapter(Context context, List<CutInfo> list) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
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

        Uri uri = isAndroidQ ? Uri.parse(path) : Uri.fromFile(new File(path));
        holder.tvGif.setVisibility(FileUtils.isGif(photoInfo.getMimeType()) ? View.VISIBLE : View.GONE);
        BitmapLoadUtils.decodeBitmapInBackground(context, uri, maxImageWidth,
                maxImageHeight,
                new BitmapLoadShowCallback() {

                    @Override
                    public void onBitmapLoaded(@NonNull Bitmap bitmap) {
                        if (holder.mIvPhoto != null) {
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getAdapterPosition(), v);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvPhoto;
        ImageView iv_dot;
        TextView tvGif;

        public ViewHolder(View view) {
            super(view);
            mIvPhoto = view.findViewById(R.id.iv_photo);
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
