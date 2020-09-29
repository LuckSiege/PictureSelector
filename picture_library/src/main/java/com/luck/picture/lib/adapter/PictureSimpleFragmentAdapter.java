package com.luck.picture.lib.adapter;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.JumpUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @data：2018/1/27 下午7:50
 * @describe:PictureSimpleFragmentAdapter
 */

public class PictureSimpleFragmentAdapter extends PagerAdapter {
    private List<LocalMedia> data;
    private OnCallBackActivity onBackPressed;
    private PictureSelectionConfig config;
    /**
     * Maximum number of cached images
     */
    private static final int MAX_CACHE_SIZE = 20;
    /**
     * To cache the view
     */
    private SparseArray<View> mCacheView;

    public void clear() {
        if (null != mCacheView) {
            mCacheView.clear();
            mCacheView = null;
        }
    }

    public void removeCacheView(int position) {
        if (mCacheView != null && position < mCacheView.size()) {
            mCacheView.removeAt(position);
        }
    }

    public interface OnCallBackActivity {
        /**
         * Close Activity
         */
        void onActivityBackPressed();
    }

    public PictureSimpleFragmentAdapter(PictureSelectionConfig config,
                                        OnCallBackActivity onBackPressed) {
        super();
        this.config = config;
        this.onBackPressed = onBackPressed;
        this.mCacheView = new SparseArray<>();
    }

    /**
     * bind data
     *
     * @param data
     */
    public void bindData(List<LocalMedia> data) {
        this.data = data;
    }

    /**
     * get data
     *
     * @return
     */
    public List<LocalMedia> getData() {
        return data == null ? new ArrayList<>() : data;
    }

    public int getSize() {
        return data == null ? 0 : data.size();
    }

    public void remove(int currentItem) {
        if (getSize() > currentItem) {
            data.remove(currentItem);
        }
    }

    public LocalMedia getItem(int position) {
        return getSize() > 0 && position < getSize() ? data.get(position) : null;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
        if (mCacheView.size() > MAX_CACHE_SIZE) {
            mCacheView.remove(position);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        View contentView = mCacheView.get(position);
        if (contentView == null) {
            contentView = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.picture_image_preview, container, false);
            mCacheView.put(position, contentView);
        }
        PhotoView imageView = contentView.findViewById(R.id.preview_image);
        SubsamplingScaleImageView longImg = contentView.findViewById(R.id.longImg);
        ImageView ivPlay = contentView.findViewById(R.id.iv_play);
        LocalMedia media = getItem(position);
        if (media != null) {
            final String mimeType = media.getMimeType();
            final String path;
            if (media.isCut() && !media.isCompressed()) {
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                path = media.getCompressPath();
            } else {
                path = media.getPath();
            }
            boolean isGif = PictureMimeType.isGif(mimeType);
            boolean isHasVideo = PictureMimeType.isHasVideo(mimeType);
            ivPlay.setVisibility(isHasVideo ? View.VISIBLE : View.GONE);
            ivPlay.setOnClickListener(v -> {
                if (PictureSelectionConfig.customVideoPlayCallback != null) {
                    PictureSelectionConfig.customVideoPlayCallback.startPlayVideo(media);
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(PictureConfig.EXTRA_PREVIEW_VIDEO, true);
                    bundle.putString(PictureConfig.EXTRA_VIDEO_PATH, path);
                    intent.putExtras(bundle);
                    JumpUtils.startPictureVideoPlayActivity(container.getContext(), bundle, PictureConfig.PREVIEW_VIDEO_CODE);
                }
            });
            boolean eqLongImg = MediaUtils.isLongImg(media);
            imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
            imageView.setOnViewTapListener((view, x, y) -> {
                if (onBackPressed != null) {
                    onBackPressed.onActivityBackPressed();
                }
            });
            longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
            longImg.setOnClickListener(v -> {
                if (onBackPressed != null) {
                    onBackPressed.onActivityBackPressed();
                }
            });

            if (isGif && !media.isCompressed()) {
                if (config != null && PictureSelectionConfig.imageEngine != null) {
                    PictureSelectionConfig.imageEngine.loadAsGifImage
                            (contentView.getContext(), path, imageView);
                }
            } else {
                if (config != null && PictureSelectionConfig.imageEngine != null) {
                    if (eqLongImg) {
                        displayLongPic(PictureMimeType.isContent(path)
                                ? Uri.parse(path) : Uri.fromFile(new File(path)), longImg);
                    } else {
                        PictureSelectionConfig.imageEngine.loadImage
                                (contentView.getContext(), path, imageView);
                    }
                }
            }
        }

        (container).addView(contentView, 0);
        return contentView;
    }

    /**
     * load long image
     *
     * @param uri
     * @param longImg
     */
    private void displayLongPic(Uri uri, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.uri(uri), new ImageViewState(0, new PointF(0, 0), 0));
    }
}
