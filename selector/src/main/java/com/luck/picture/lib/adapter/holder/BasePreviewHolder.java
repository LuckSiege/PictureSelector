package com.luck.picture.lib.adapter.holder;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.utils.BitmapUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.MediaUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:17 下午
 * @describe：BasePreviewHolder
 */
public abstract class BasePreviewHolder extends RecyclerView.ViewHolder {
    /**
     * 图片
     */
    public final static int ADAPTER_TYPE_IMAGE = 1;
    /**
     * 视频
     */
    public final static int ADAPTER_TYPE_VIDEO = 2;

    /**
     * 音频
     */
    public final static int ADAPTER_TYPE_AUDIO = 3;

    protected final int screenWidth;
    protected final int screenHeight;
    protected final int screenAppInHeight;
    protected LocalMedia media;
    protected final SelectorConfig selectorConfig;
    public PhotoView coverImageView;

    public static BasePreviewHolder generate(ViewGroup parent, int viewType, int resource) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        if (viewType == ADAPTER_TYPE_VIDEO) {
            return new PreviewVideoHolder(itemView);
        } else if (viewType == ADAPTER_TYPE_AUDIO) {
            return new PreviewAudioHolder(itemView);
        } else {
            return new PreviewImageHolder(itemView);
        }
    }

    public BasePreviewHolder(@NonNull View itemView) {
        super(itemView);
        this.selectorConfig = SelectorProviders.getInstance().getSelectorConfig();
        this.screenWidth = DensityUtil.getRealScreenWidth(itemView.getContext());
        this.screenHeight = DensityUtil.getScreenHeight(itemView.getContext());
        this.screenAppInHeight = DensityUtil.getRealScreenHeight(itemView.getContext());
        this.coverImageView = itemView.findViewById(R.id.preview_image);
        findViews(itemView);
    }

    /**
     * findViews
     *
     * @param itemView
     */
    protected abstract void findViews(View itemView);

    /**
     * load image cover
     *
     * @param media
     * @param maxWidth
     * @param maxHeight
     */
    protected abstract void loadImage(final LocalMedia media, int maxWidth, int maxHeight);

    /**
     * 点击返回事件
     */
    protected abstract void onClickBackPressed();

    /**
     * 长按事件
     */
    protected abstract void onLongPressDownload(LocalMedia media);

    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    public void bindData(LocalMedia media, int position) {
        this.media = media;
        int[] size = getRealSizeFromMedia(media);
        int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);
        loadImage(media, maxImageSize[0], maxImageSize[1]);
        setScaleDisplaySize(media);
        setCoverScaleType(media);
        onClickBackPressed();
        onLongPressDownload(media);
    }

    protected int[] getRealSizeFromMedia(LocalMedia media) {
        if (media.isCut() && media.getCropImageWidth() > 0 && media.getCropImageHeight() > 0) {
            return new int[]{media.getCropImageWidth(), media.getCropImageHeight()};
        } else {
            return new int[]{media.getWidth(), media.getHeight()};
        }
    }

    protected void setCoverScaleType(LocalMedia media) {
        if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
            coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    protected void setScaleDisplaySize(LocalMedia media) {
        if (!selectorConfig.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.getWidth() > 0 && media.getHeight() > 0) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) coverImageView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = screenAppInHeight;
                layoutParams.gravity = Gravity.CENTER;
            }
        }
    }

    /**
     * onViewAttachedToWindow
     */
    public void onViewAttachedToWindow() {

    }

    /**
     * onViewDetachedFromWindow
     */
    public void onViewDetachedFromWindow() {

    }

    /**
     * resume and pause play
     */
    public void resumePausePlay() {

    }

    /**
     * play ing
     */
    public boolean isPlaying() {
        return false;
    }

    /**
     * release
     */
    public void release() {

    }

    protected OnPreviewEventListener mPreviewEventListener;

    public void setOnPreviewEventListener(OnPreviewEventListener listener) {
        this.mPreviewEventListener = listener;
    }

    public interface OnPreviewEventListener {

        void onBackPressed();

        void onPreviewVideoTitle(String videoName);

        void onLongPressDownload(LocalMedia media);
    }
}
