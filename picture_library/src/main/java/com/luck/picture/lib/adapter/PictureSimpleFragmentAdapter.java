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

import androidx.viewpager.widget.PagerAdapter;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.JumpUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.io.File;
import java.util.List;

/**
 * @author：luck
 * @data：2018/1/27 下午7:50
 * @描述:图片预览
 */

public class PictureSimpleFragmentAdapter extends PagerAdapter {
    private List<LocalMedia> images;
    private OnCallBackActivity onBackPressed;
    private PictureSelectionConfig config;
    /**
     * 最大缓存图片数量
     */
    private static final int MAX_CACHE_SIZE = 20;
    /**
     * 缓存view
     */
    private SparseArray<View> mCacheView;

    public void clear() {
        if (null != mCacheView) {
            mCacheView.clear();
            mCacheView = null;
        }
    }

    public interface OnCallBackActivity {
        /**
         * 关闭预览Activity
         */
        void onActivityBackPressed();
    }

    public PictureSimpleFragmentAdapter(PictureSelectionConfig config, List<LocalMedia> images,
                                        OnCallBackActivity onBackPressed) {
        super();
        this.config = config;
        this.images = images;
        this.onBackPressed = onBackPressed;
        this.mCacheView = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return images != null ? images.size() : 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
        if (mCacheView.size() > MAX_CACHE_SIZE) {
            mCacheView.remove(position);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = mCacheView.get(position);
        if (contentView == null) {
            contentView = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.picture_image_preview, container, false);
            // 常规图控件
            final PhotoView imageView = contentView.findViewById(R.id.preview_image);
            // 长图控件
            final SubsamplingScaleImageView longImg = contentView.findViewById(R.id.longImg);
            // 视频播放按钮
            ImageView ivPlay = contentView.findViewById(R.id.iv_play);
            LocalMedia media = images.get(position);
            if (media != null) {
                final String mimeType = media.getMimeType();
                boolean eqVideo = PictureMimeType.eqVideo(mimeType);
                ivPlay.setVisibility(eqVideo ? View.VISIBLE : View.GONE);
                final String path;
                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    path = media.getPath();
                }
                boolean isGif = PictureMimeType.isGif(mimeType);
                final boolean eqLongImg = MediaUtils.isLongImg(media);
                imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
                longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
                // 压缩过的gif就不是gif了
                if (isGif && !media.isCompressed()) {
                    if (config != null && config.imageEngine != null) {
                        config.imageEngine.loadAsGifImage
                                (contentView.getContext(), path, imageView);
                    }
                } else {
                    if (config != null && config.imageEngine != null) {
                        if (eqLongImg) {
                            displayLongPic(SdkVersionUtils.checkedAndroid_Q()
                                    ? Uri.parse(path) : Uri.fromFile(new File(path)), longImg);
                        } else {
                            config.imageEngine.loadImage
                                    (contentView.getContext(), path, imageView);
                        }
                    }
                }
                imageView.setOnViewTapListener((view, x, y) -> {
                    if (onBackPressed != null) {
                        onBackPressed.onActivityBackPressed();
                    }
                });
                longImg.setOnClickListener(v -> {
                    if (onBackPressed != null) {
                        onBackPressed.onActivityBackPressed();
                    }
                });
                ivPlay.setOnClickListener(v -> {
                    if (config.customVideoPlayCallback != null) {
                        config.customVideoPlayCallback.startPlayVideo(media);
                    } else {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(PictureConfig.EXTRA_PREVIEW_VIDEO, true);
                        bundle.putString(PictureConfig.EXTRA_VIDEO_PATH, path);
                        intent.putExtras(bundle);
                        JumpUtils.startPictureVideoPlayActivity(container.getContext(), bundle, PictureConfig.PREVIEW_VIDEO_CODE);
                    }
                });
            }
            mCacheView.put(position, contentView);
        }
        (container).addView(contentView, 0);
        return contentView;
    }

    /**
     * 加载长图
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
