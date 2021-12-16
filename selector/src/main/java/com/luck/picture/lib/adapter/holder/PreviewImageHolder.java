package com.luck.picture.lib.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.View;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.large.ImageSource;
import com.luck.picture.lib.large.ImageViewState;
import com.luck.picture.lib.large.SubsamplingScaleImageView;
import com.luck.picture.lib.utils.MediaUtils;

/**
 * @author：luck
 * @date：2021/12/15 5:11 下午
 * @describe：PreviewImageHolder
 */
public class PreviewImageHolder extends BasePreviewHolder {
    public SubsamplingScaleImageView previewLongView;

    public PreviewImageHolder(@NonNull View itemView, PictureSelectionConfig config) {
        super(itemView, config);
        previewLongView = itemView.findViewById(R.id.preview_long_image);
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
            previewLongView.setVisibility(View.VISIBLE);
            coverImageView.setVisibility(View.GONE);
        } else {
            coverImageView.setVisibility(View.VISIBLE);
            previewLongView.setVisibility(View.GONE);
        }
        previewLongView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onLoadLargeSourceImage(ImageSource imageSource) {
        previewLongView.setVisibility(View.VISIBLE);
        coverImageView.setVisibility(View.GONE);
        previewLongView.setQuickScaleEnabled(true);
        previewLongView.setZoomEnabled(true);
        previewLongView.setDoubleTapZoomDuration(100);
        previewLongView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        previewLongView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        previewLongView.setImage(imageSource, new ImageViewState(0, new PointF(0, 0), 0));
        mPreviewEventListener.onLoadCompleteBeginScale(this);
    }

    @Override
    protected void onLoadSourceImage(Bitmap resource) {
        coverImageView.setVisibility(View.VISIBLE);
        previewLongView.setVisibility(View.GONE);
        coverImageView.setImageBitmap(resource);
        mPreviewEventListener.onLoadCompleteBeginScale(this);
    }
}
