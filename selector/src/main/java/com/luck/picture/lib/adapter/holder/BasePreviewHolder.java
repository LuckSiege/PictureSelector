package com.luck.picture.lib.adapter.holder;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.large.ImageSource;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.utils.MediaUtils;

import java.io.File;

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
    private final PictureSelectionConfig config;
    public PhotoView coverImageView;

    public static BasePreviewHolder generate(ViewGroup parent, int viewType, int resource, PictureSelectionConfig config) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        if (viewType == ADAPTER_TYPE_VIDEO) {
            return new PreviewVideoHolder(itemView, config);
        } else {
            return new PreviewImageHolder(itemView, config);
        }
    }

    public BasePreviewHolder(@NonNull View itemView, PictureSelectionConfig config) {
        super(itemView);
        this.config = config;
        this.coverImageView = itemView.findViewById(R.id.preview_image);
    }


    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    public void bindData(LocalMedia media, int position) {
        String path = media.getAvailablePath();
        if (PictureMimeType.isHasHttp(path)) {
            String mimeType = PictureMimeType.getImageMimeType(path);
            if (PictureMimeType.isGif(mimeType) || PictureMimeType.isGif(media.getMimeType())
                    || PictureMimeType.isWebp(mimeType) || PictureMimeType.isWebp(media.getMimeType())) {
                if (config.isPreviewScaleMode) {
                    PictureSelectionConfig.imageEngine.loadImageBitmap(itemView.getContext(), path, new OnCallbackListener<Bitmap>() {
                        @Override
                        public void onCall(Bitmap bitmap) {
                            if (bitmap != null) {
                                coverImageView.setImageBitmap(bitmap);
                                mPreviewEventListener.onLoadCompleteBeginScale(BasePreviewHolder.this);
                            }
                        }
                    });
                } else {
                    PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
                }
            } else {
                PictureSelectionConfig.imageEngine.loadImageBitmap(itemView.getContext(), path, new OnCallbackListener<Bitmap>() {
                    @Override
                    public void onCall(Bitmap resource) {
                        if (MediaUtils.isLongImg(resource.getWidth(), resource.getHeight())) {
                            onLoadLargeSourceImage(ImageSource.cachedBitmap(resource));
                        } else {
                            onLoadSourceImage(resource);
                        }
                    }
                });
            }
        } else {
            if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
                Uri uri = PictureMimeType.isContent(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
                ImageSource imageSource = ImageSource.uri(uri);
                onLoadLargeSourceImage(imageSource);
            } else {
                if (config.isPreviewScaleMode) {
                    PictureSelectionConfig.imageEngine.loadImageBitmap(itemView.getContext(), path, new OnCallbackListener<Bitmap>() {
                        @Override
                        public void onCall(Bitmap bitmap) {
                            if (bitmap != null) {
                                coverImageView.setImageBitmap(bitmap);
                                mPreviewEventListener.onLoadCompleteBeginScale(BasePreviewHolder.this);
                            }
                        }
                    });
                } else {
                    PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
                }
            }
        }

        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });

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

    /**
     * 加载大图资源
     */
    protected void onLoadLargeSourceImage(ImageSource imageSource) {

    }

    /**
     * 加载普通资源
     */
    protected void onLoadSourceImage(Bitmap resource) {

    }


    protected OnPreviewEventListener mPreviewEventListener;

    public void setOnPreviewEventListener(OnPreviewEventListener listener) {
        this.mPreviewEventListener = listener;
    }

    public interface OnPreviewEventListener {

        void onLoadCompleteBeginScale(BasePreviewHolder holder);

        void onBackPressed();

        void onPreviewVideoTitle(String videoName);

        void onLongPressDownload(LocalMedia media);
    }
}
