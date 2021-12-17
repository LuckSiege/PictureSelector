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

/**
 * @author：luck
 * @date：2021/12/15 5:11 下午
 * @describe：PreviewImageHolder
 */
public class PreviewImageHolder extends BasePreviewHolder {
    public SubsamplingScaleImageView largePreviewView;

    public PreviewImageHolder(@NonNull View itemView, PictureSelectionConfig config) {
        super(itemView, config);
        largePreviewView = itemView.findViewById(R.id.preview_long_image);
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        largePreviewView.setOnClickListener(new View.OnClickListener() {
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
        largePreviewView.setQuickScaleEnabled(true);
        largePreviewView.setZoomEnabled(true);
        largePreviewView.setDoubleTapZoomDuration(100);
        largePreviewView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        largePreviewView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        largePreviewView.setImage(imageSource, new ImageViewState(0, new PointF(0, 0), 0));
    }

    @Override
    protected void onLoadSourceImage(Bitmap resource) {
        coverImageView.setImageBitmap(resource);
        mPreviewEventListener.onLoadCompleteBeginScale(this);
    }
}
