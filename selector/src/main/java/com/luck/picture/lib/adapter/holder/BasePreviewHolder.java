package com.luck.picture.lib.adapter.holder;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public PhotoView coverImageView;

    public static BasePreviewHolder generate(ViewGroup parent, int viewType, int resource) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        if (viewType == ADAPTER_TYPE_VIDEO) {
            return new PreviewVideoHolder(itemView);
        } else {
            return new PreviewImageHolder(itemView);
        }
    }

    public BasePreviewHolder(@NonNull View itemView) {
        super(itemView);
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
        PictureSelectionConfig.imageEngine.loadImageBitmap(itemView.getContext(), path, new OnCallbackListener<Bitmap>() {
            @Override
            public void onCall(Bitmap bitmap) {
                if (PictureMimeType.isHasWebp(media.getMimeType()) || PictureMimeType.isUrlHasWebp(path)
                        || PictureMimeType.isUrlHasGif(path) || PictureMimeType.isHasGif(media.getMimeType())) {
                    PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
                } else {
                    coverImageView.setImageBitmap(bitmap);
                }
                if (MediaUtils.isLongImage(bitmap.getWidth(), bitmap.getHeight())) {
                    coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    coverImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                mPreviewEventListener.onLoadCompleteBeginScale(BasePreviewHolder.this);
            }
        });

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
