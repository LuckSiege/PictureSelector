package com.luck.picture.lib.adapter.holder;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.utils.BitmapUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.MediaUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:17 下午
 * @describe：BasePreviewHolder
 */
public class BasePreviewHolder extends RecyclerView.ViewHolder {
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
    protected final PictureSelectionConfig config;
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
        this.config = PictureSelectionConfig.getInstance();
        this.screenWidth = DensityUtil.getRealScreenWidth(itemView.getContext());
        this.screenHeight = DensityUtil.getScreenHeight(itemView.getContext());
        this.screenAppInHeight = DensityUtil.getRealScreenHeight(itemView.getContext());
        findViews(itemView);
    }

    protected void findViews(View itemView) {
        this.coverImageView = itemView.findViewById(R.id.preview_image);
    }

    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    public void bindData(LocalMedia media, int position) {
        this.media = media;
        int[] size = getSize(media);
        int[] maxImageSize = BitmapUtils.getMaxImageSize(size[0], size[1]);
        loadImageBitmap(media, maxImageSize[0], maxImageSize[1]);
        setScaleDisplaySize(media);
        setOnClickEventListener();
        setOnLongClickEventListener();
    }

    protected void setOnClickEventListener() {
        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    protected void setOnLongClickEventListener() {
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

    protected void loadImageBitmap(final LocalMedia media, int maxWidth, int maxHeight) {
        if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadImageBitmap(itemView.getContext(), media.getAvailablePath(), maxWidth, maxHeight,
                    new OnCallbackListener<Bitmap>() {
                        @Override
                        public void onCall(Bitmap bitmap) {
                            loadBitmapCallback(media, bitmap);
                        }
                    });
        }
    }

    protected void loadBitmapCallback(LocalMedia media, Bitmap bitmap) {
        String path = media.getAvailablePath();
        if (bitmap == null) {
            mPreviewEventListener.onLoadError();
        } else {
            if (PictureMimeType.isHasWebp(media.getMimeType()) || PictureMimeType.isUrlHasWebp(path)
                    || PictureMimeType.isUrlHasGif(path) || PictureMimeType.isHasGif(media.getMimeType())) {
                if (PictureSelectionConfig.imageEngine != null) {
                    coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
                }
            } else {
                setImageViewBitmap(bitmap);
            }
            if (media.getWidth() <= 0) {
                media.setWidth(bitmap.getWidth());
            }
            if (media.getHeight() <= 0) {
                media.setHeight(bitmap.getHeight());
            }
            int width, height;
            ImageView.ScaleType scaleType;
            if (MediaUtils.isLongImage(bitmap.getWidth(), bitmap.getHeight())) {
                scaleType = ImageView.ScaleType.CENTER_CROP;
                width = screenWidth;
                height = screenHeight;
            } else {
                scaleType = ImageView.ScaleType.FIT_CENTER;
                int[] size = getSize(media);
                boolean isHaveSize = bitmap.getWidth() > 0 && bitmap.getHeight() > 0;
                width = isHaveSize ? bitmap.getWidth() : size[0];
                height = isHaveSize ? bitmap.getHeight() : size[1];
            }
            mPreviewEventListener.onLoadComplete(width, height, new OnCallbackListener<Boolean>() {
                @Override
                public void onCall(Boolean isBeginEffect) {
                    coverImageView.setScaleType(isBeginEffect ? ImageView.ScaleType.CENTER_CROP : scaleType);
                }
            });
        }
    }

    protected void setImageViewBitmap(Bitmap bitmap) {
        coverImageView.setImageBitmap(bitmap);
    }

    protected int[] getSize(LocalMedia media) {
        if (media.isCut() && media.getCropImageWidth() > 0 && media.getCropImageHeight() > 0) {
            return new int[]{media.getCropImageWidth(), media.getCropImageHeight()};
        } else {
            return new int[]{media.getWidth(), media.getHeight()};
        }
    }

    protected void setScaleDisplaySize(LocalMedia media) {
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            if (media.getWidth() > 0 && media.getHeight() > 0) {
                float ratio = (float) media.getWidth() / (float) media.getHeight();
                int displayHeight = (int) (screenWidth / ratio);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) coverImageView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = displayHeight > screenHeight ? screenAppInHeight : screenHeight;
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

    protected OnPreviewEventListener mPreviewEventListener;

    public void setOnPreviewEventListener(OnPreviewEventListener listener) {
        this.mPreviewEventListener = listener;
    }

    public interface OnPreviewEventListener {

        void onLoadComplete(int width, int height, OnCallbackListener<Boolean> call);

        void onLoadError();

        void onBackPressed();

        void onPreviewVideoTitle(String videoName);

        void onLongPressDownload(LocalMedia media);
    }
}
