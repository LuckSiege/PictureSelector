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

package com.yalantis.ucrop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yalantis.ucrop.R;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.adapter
 * email：893855882@qq.com
 * data：16/12/31
 */

public class PicturePhotoGalleryAdapter extends PictureViewHolderAdapter<PicturePhotoGalleryAdapter.ViewHolder, LocalMedia> {

    private Context context;
    private int mRowWidth;

    public PicturePhotoGalleryAdapter(Context context, List<LocalMedia> list, int screenWidth) {
        super(context, list);
        this.context = context;
        this.mRowWidth = screenWidth / 5;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflate(R.layout.picture_gf_adapter_edit_list, parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String path = "";
        LocalMedia photoInfo = getDatas().get(position);
        if (photoInfo != null) {
            path = photoInfo.getPath();
        }

        if (photoInfo.isCut()) {
            holder.iv_dot.setVisibility(View.VISIBLE);
            holder.iv_dot.setImageResource(R.drawable.crop_oval_true);
        } else {
            holder.iv_dot.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(path)
                .placeholder(R.color.grey)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.mIvPhoto);

    }

    public class ViewHolder extends PictureViewHolderAdapter.ViewHolder {
        ImageView mIvPhoto;
        ImageView mIvDelete;
        ImageView iv_dot;

        public ViewHolder(View view) {
            super(view);
            mIvPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            mIvDelete = (ImageView) view.findViewById(R.id.iv_delete);
            iv_dot = (ImageView) view.findViewById(R.id.iv_dot);
        }
    }
}
