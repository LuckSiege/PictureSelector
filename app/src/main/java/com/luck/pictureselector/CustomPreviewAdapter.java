package com.luck.pictureselector;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.adapter.holder.BasePreviewHolder;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.utils.ActivityCompatHelper;
import com.luck.picture.lib.utils.MediaUtils;

/**
 * @author：luck
 * @date：2022/2/21 4:17 下午
 * @describe：CustomPreviewAdapter
 */
public class CustomPreviewAdapter extends PicturePreviewAdapter {

    @NonNull
    @Override
    public BasePreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BasePreviewHolder.ADAPTER_TYPE_IMAGE) {
            // 这里以重写自定义图片预览为例
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_custom_preview_image, parent, false);
            return new CustomPreviewImageHolder(itemView);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    public static class CustomPreviewImageHolder extends BasePreviewHolder {
        SubsamplingScaleImageView subsamplingScaleImageView;

        public CustomPreviewImageHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void findViews(View itemView) {
            subsamplingScaleImageView = itemView.findViewById(R.id.big_preview_image);
        }

        @Override
        protected void loadImage(LocalMedia media, int maxWidth, int maxHeight) {
            if (!ActivityCompatHelper.assertValidRequest(itemView.getContext())) {
                return;
            }
            Glide.with(itemView.getContext())
                    .asBitmap()
                    .load(media.getAvailablePath())
                    .into(new CustomTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (MediaUtils.isLongImage(resource.getWidth(), resource.getHeight())) {
                                subsamplingScaleImageView.setVisibility(View.VISIBLE);
                                float scale = Math.max(screenWidth / (float) resource.getWidth(),
                                        screenHeight / (float) resource.getHeight());
                                subsamplingScaleImageView.setImage(ImageSource.cachedBitmap(resource),
                                        new ImageViewState(scale, new PointF(0, 0), 0));
                            } else {
                                subsamplingScaleImageView.setVisibility(View.GONE);
                                coverImageView.setImageBitmap(resource);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }

                    });
        }

        @Override
        protected void onClickBackPressed() {
            if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
                subsamplingScaleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPreviewEventListener != null) {
                            mPreviewEventListener.onBackPressed();
                        }
                    }
                });
            } else {
                coverImageView.setOnViewTapListener(new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (mPreviewEventListener != null) {
                            mPreviewEventListener.onBackPressed();
                        }
                    }
                });
            }
        }

        @Override
        protected void onLongPressDownload(LocalMedia media) {
            if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
                subsamplingScaleImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mPreviewEventListener != null) {
                            mPreviewEventListener.onLongPressDownload(media);
                        }
                        return false;
                    }
                });
            } else {
                coverImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mPreviewEventListener != null) {
                            mPreviewEventListener.onLongPressDownload(media);
                        }
                        return false;
                    }
                });
            }
        }
    }
}
