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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.yalantis.ucrop.model.CutInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.adapter
 * email：893855882@qq.com
 * data：16/12/31
 */

public class PicturePhotoGalleryAdapter extends RecyclerView.Adapter<PicturePhotoGalleryAdapter.ViewHolder> {

    private Context context;
    private List<CutInfo> list = new ArrayList<>();
    private LayoutInflater mInflater;

    public PicturePhotoGalleryAdapter(Context context, List<CutInfo> list) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    public void bindData(List<CutInfo> list) {
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
            holder.iv_dot.setVisibility(View.GONE);
        }

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.ucrop_color_grey)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(options)
                .into(holder.mIvPhoto);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvPhoto;
        ImageView iv_dot;

        public ViewHolder(View view) {
            super(view);
            mIvPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            iv_dot = (ImageView) view.findViewById(R.id.iv_dot);
        }
    }

}
