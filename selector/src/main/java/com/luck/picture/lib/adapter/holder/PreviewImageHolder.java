package com.luck.picture.lib.adapter.holder;

import android.view.View;

import androidx.annotation.NonNull;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;

/**
 * @author：luck
 * @date：2021/12/15 5:11 下午
 * @describe：PreviewImageHolder
 */
public class PreviewImageHolder extends BasePreviewHolder {

    public PreviewImageHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    protected void findViews(View itemView) {
    }

    @Override
    protected void loadImage(LocalMedia media, int maxWidth, int maxHeight) {
        if (selectorConfig.imageEngine != null) {
            String availablePath = media.getAvailablePath();
            if (maxWidth == PictureConfig.UNSET && maxHeight == PictureConfig.UNSET) {
                selectorConfig.imageEngine.loadImage(itemView.getContext(), availablePath, coverImageView);
            } else {
                selectorConfig.imageEngine.loadImage(itemView.getContext(), coverImageView, availablePath, maxWidth, maxHeight);
            }
        }
    }

    @Override
    protected void onClickBackPressed() {
        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onLongPressDownload(LocalMedia media) {
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
