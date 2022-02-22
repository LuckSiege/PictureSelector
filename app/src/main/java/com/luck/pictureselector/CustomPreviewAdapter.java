package com.luck.pictureselector;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.adapter.holder.BasePreviewHolder;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
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
            super.findViews(itemView);
            subsamplingScaleImageView = itemView.findViewById(R.id.big_preview_image);
        }

        @Override
        protected void loadImageBitmap(LocalMedia media, int maxWidth, int maxHeight) {
            int[] size = getSize(media);
            if (MediaUtils.isLongImage(size[0], size[1])) {
                super.loadImageBitmap(media, PictureConfig.UNSET, PictureConfig.UNSET);
            } else {
                super.loadImageBitmap(media, maxWidth, maxHeight);
            }
        }

        @Override
        protected void loadBitmapCallback(LocalMedia media, Bitmap bitmap) {
            String path = media.getAvailablePath();
            if (bitmap == null) {
                mPreviewEventListener.onLoadError();
            } else {
                boolean isHasWebp = PictureMimeType.isHasWebp(media.getMimeType()) || PictureMimeType.isUrlHasWebp(path);
                boolean isHasGif = PictureMimeType.isUrlHasGif(path) || PictureMimeType.isHasGif(media.getMimeType());
                if (isHasWebp || isHasGif) {
                    PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
                } else {
                    setImageViewBitmap(bitmap);
                }
                int width, height;
                ImageView.ScaleType scaleType;
                if (MediaUtils.isLongImage(bitmap.getWidth(), bitmap.getHeight())) {
                    subsamplingScaleImageView.setVisibility(View.VISIBLE);
                    scaleType = ImageView.ScaleType.FIT_CENTER;
                    width = screenWidth;
                    height = screenHeight;
                } else {
                    subsamplingScaleImageView.setVisibility(View.GONE);
                    scaleType = ImageView.ScaleType.FIT_CENTER;
                    int[] size = getSize(media);
                    boolean isHaveSize = bitmap.getWidth() > 0 && bitmap.getHeight() > 0;
                    width = isHaveSize ? bitmap.getWidth() : size[0];
                    height = isHaveSize ? bitmap.getHeight() : size[1];
                }
                mPreviewEventListener.onLoadComplete(width, height, new OnCallbackListener<Boolean>() {
                    @Override
                    public void onCall(Boolean isBeginEffect) {
                        if (isBeginEffect) {
                            coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            coverImageView.setScaleType(scaleType);
                        }
                    }
                });
            }
        }

        @Override
        protected void setImageViewBitmap(Bitmap bitmap) {
            if (MediaUtils.isLongImage(bitmap.getWidth(), bitmap.getHeight())) {
                float scale = Math.max(screenWidth / (float) bitmap.getWidth(), screenHeight / (float) bitmap.getHeight());
                subsamplingScaleImageView.setImage(ImageSource.cachedBitmap(bitmap), new ImageViewState(scale, new PointF(0, 0), 0));
            } else {
                super.setImageViewBitmap(bitmap);
            }
        }

        @Override
        protected void setOnClickEventListener() {
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
                super.setOnClickEventListener();
            }
        }

        @Override
        protected void setOnLongClickEventListener() {
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
                super.setOnLongClickEventListener();
            }
        }
    }
}
