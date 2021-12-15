package com.luck.picture.lib.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.large.ImageSource;
import com.luck.picture.lib.large.ImageViewState;
import com.luck.picture.lib.large.SubsamplingScaleImageView;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.utils.DensityUtil;
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
    private final int screenWidth, screenHeight;
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
        this.screenWidth = DensityUtil.getScreenWidth(itemView.getContext());
        this.screenHeight = DensityUtil.getScreenHeight(itemView.getContext());
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
                PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
            } else {
                PictureSelectionConfig.imageEngine.loadImageBitmap(itemView.getContext(), path, new OnCallbackListener<Bitmap>() {
                    @Override
                    public void onCall(Bitmap resource) {
                        onLoadLongImage(resource, null);
                    }
                });
            }
        } else {
            if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
                Uri uri = PictureMimeType.isContent(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
                onLoadLongImage(null, uri);
            } else {
                PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), path, coverImageView);
            }
        }

        if (config.isAutoScalePreviewImage && screenWidth < screenHeight) {
            float width = Math.min(media.getWidth(), media.getHeight());
            float height = Math.max(media.getHeight(), media.getWidth());
            if (width > 0 && height > 0) {
                // 只需让图片的宽是屏幕的宽，高乘以比例
                int displayHeight = (int) Math.ceil(width * height / width);
                //最终让图片按照宽是屏幕 高是等比例缩放的大小
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) coverImageView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = displayHeight < screenHeight ? displayHeight + screenHeight : displayHeight;
                layoutParams.gravity = Gravity.CENTER;
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
     * 加载长图
     */
    protected void onLoadLongImage(Bitmap bitmap, Uri uri) {

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
