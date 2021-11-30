package com.luck.picture.lib.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

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
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.widget.large.ImageSource;
import com.luck.picture.lib.widget.large.ImageViewState;
import com.luck.picture.lib.widget.large.SubsamplingScaleImageView;

import java.io.File;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/23 1:11 下午
 * @describe：PicturePreviewAdapter2
 */
public class PicturePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 图片
     */
    private final static int ADAPTER_TYPE_IMAGE = 1;
    /**
     * 视频
     */
    private final static int ADAPTER_TYPE_VIDEO = 2;
    private final List<LocalMedia> mData;
    private final PictureSelectionConfig config;
    private final int screenWidth, screenHeight;

    public PicturePreviewAdapter(Context context, List<LocalMedia> list, PictureSelectionConfig config) {
        this.mData = list;
        this.config = config;
        this.screenWidth = DensityUtil.getScreenWidth(context);
        this.screenHeight = DensityUtil.getScreenHeight(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ADAPTER_TYPE_VIDEO) {
            return new PreviewVideoHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ps_preview_video, parent, false));
        } else {
            return new PreviewImageHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ps_preview_image, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalMedia media = mData.get(position);
        String path;
        if (media.isCut() && !media.isCompressed()) {
            path = media.getCutPath();
        } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
            path = media.getCompressPath();
        } else if (media.isToSandboxPath()) {
            path = media.getSandboxPath();
        } else {
            path = media.getPath();
        }
        PhotoView previewView;
        if (getItemViewType(position) == ADAPTER_TYPE_VIDEO) {
            PreviewVideoHolder videoHolder = (PreviewVideoHolder) holder;
            previewView = videoHolder.previewView;
            PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(), path, previewView);
            videoHolder.ivPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (videoHolder.ivPlayButton.getVisibility() == View.VISIBLE) {
                        videoHolder.ivPlayButton.setVisibility(View.GONE);
                    }
                    if (previewView.getVisibility() == View.VISIBLE) {
                        previewView.setVisibility(View.GONE);
                    }
                    if (videoHolder.videoView.getVisibility() == View.GONE) {
                        videoHolder.videoView.setVisibility(View.VISIBLE);
                    }
                    if (PictureMimeType.isContent(path)) {
                        videoHolder.videoView.setVideoURI(Uri.parse(path));
                    } else {
                        if (TextUtils.isEmpty(media.getSandboxPath())) {
                            videoHolder.videoView.setVideoPath(media.getSandboxPath());
                        } else {
                            videoHolder.videoView.setVideoPath(media.getPath());
                        }
                    }
                    videoHolder.videoView.start();
                    mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
                }
            });
        } else {
            PreviewImageHolder imageHolder = (PreviewImageHolder) holder;
            previewView = imageHolder.previewView;
            boolean isGif = PictureMimeType.isGif(media.getMimeType());
            boolean isLongImage = MediaUtils.isLongImg(media);
            imageHolder.previewLongView.setVisibility(isLongImage && !isGif ? View.VISIBLE : View.GONE);
            if (PictureMimeType.isHasHttp(path)) {
                String mimeType = PictureMimeType.getImageMimeType(path);
                if (PictureMimeType.isGif(mimeType) || PictureMimeType.isGif(media.getMimeType())
                        || PictureMimeType.isWebp(mimeType) || PictureMimeType.isWebp(media.getMimeType())) {
                    PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(), path, previewView);
                } else {
                    PictureSelectionConfig.imageEngine.loadImageBitmap(holder.itemView.getContext(), path, new OnCallbackListener<Bitmap>() {
                        @Override
                        public void onCall(Bitmap resource) {
                            boolean isLongImage = MediaUtils.isLongImg(resource.getWidth(), resource.getHeight());
                            imageHolder.previewLongView.setVisibility(isLongImage ? View.VISIBLE : View.GONE);
                            previewView.setVisibility(isLongImage ? View.GONE : View.VISIBLE);
                            if (isLongImage) {
                                imageHolder.previewLongView.setQuickScaleEnabled(true);
                                imageHolder.previewLongView.setZoomEnabled(true);
                                imageHolder.previewLongView.setDoubleTapZoomDuration(100);
                                imageHolder.previewLongView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                imageHolder.previewLongView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                                imageHolder.previewLongView.setImage(ImageSource.cachedBitmap(resource),
                                        new ImageViewState(0, new PointF(0, 0), 0));
                            } else {
                                previewView.setImageBitmap(resource);
                            }
                        }
                    });
                }
            } else {
                if (isLongImage) {
                    Uri uri = PictureMimeType.isContent(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
                    imageHolder.previewLongView.setQuickScaleEnabled(true);
                    imageHolder.previewLongView.setZoomEnabled(true);
                    imageHolder.previewLongView.setDoubleTapZoomDuration(100);
                    imageHolder.previewLongView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                    imageHolder.previewLongView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
                    imageHolder.previewLongView.setImage(ImageSource.uri(uri), new ImageViewState(0, new PointF(0, 0), 0));
                } else {
                    PictureSelectionConfig.imageEngine.loadImage(holder.itemView.getContext(), path, previewView);
                }
            }
            imageHolder.previewLongView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPreviewEventListener != null) {
                        mPreviewEventListener.onBackPressed();
                    }
                }
            });

            previewView.setVisibility(isLongImage && !isGif ? View.GONE : View.VISIBLE);

        }

        if (config.isAutoScalePreviewImage && screenWidth < screenHeight) {
            float width = Math.min(media.getWidth(), media.getHeight());
            float height = Math.max(media.getHeight(), media.getWidth());
            if (width > 0 && height > 0) {
                // 只需让图片的宽是屏幕的宽，高乘以比例
                int displayHeight = (int) Math.ceil(width * height / width);
                //最终让图片按照宽是屏幕 高是等比例缩放的大小
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) previewView.getLayoutParams();
                layoutParams.width = screenWidth;
                layoutParams.height = displayHeight < screenHeight ? displayHeight + screenHeight : displayHeight;
                layoutParams.gravity = Gravity.CENTER;
            }
        }

        previewView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });

        previewView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (PictureMimeType.isHasVideo(mData.get(position).getMimeType())) {
            return ADAPTER_TYPE_VIDEO;
        } else {
            return ADAPTER_TYPE_IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    /**
     * 图片Holder
     */
    private static class PreviewImageHolder extends RecyclerView.ViewHolder {
        PhotoView previewView;
        SubsamplingScaleImageView previewLongView;

        public PreviewImageHolder(@NonNull View itemView) {
            super(itemView);
            previewView = itemView.findViewById(R.id.preview_image);
            previewLongView = itemView.findViewById(R.id.preview_long_image);
        }
    }

    /**
     * 视频Holder
     */
    private static class PreviewVideoHolder extends RecyclerView.ViewHolder {
        ImageView ivPlayButton;
        VideoView videoView;
        PhotoView previewView;

        public PreviewVideoHolder(@NonNull View itemView) {
            super(itemView);
            previewView = itemView.findViewById(R.id.preview_image);
            ivPlayButton = itemView.findViewById(R.id.iv_play_video);
            videoView = itemView.findViewById(R.id.video_view);
        }
    }

    /**
     * 给 VideoView绑定监听器
     *
     * @param videoHolder
     */
    private void addVideoListener(PreviewVideoHolder videoHolder) {
        videoHolder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoHolder.ivPlayButton.setVisibility(View.VISIBLE);
                videoHolder.previewView.setVisibility(View.VISIBLE);
                videoHolder.videoView.setVisibility(View.GONE);
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onPreviewVideoTitle(null);
                }
            }
        });
        videoHolder.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                if (videoHolder.ivPlayButton.getVisibility() == View.GONE) {
                    videoHolder.ivPlayButton.setVisibility(View.VISIBLE);
                }
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onPreviewVideoTitle(null);
                }
                if (videoHolder.videoView.getVisibility() == View.VISIBLE) {
                    videoHolder.videoView.setVisibility(View.GONE);
                }
                if (videoHolder.previewView.getVisibility() == View.GONE) {
                    videoHolder.previewView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        videoHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            videoHolder.videoView.setBackgroundColor(Color.TRANSPARENT);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof PreviewVideoHolder) {
            PreviewVideoHolder videoHolder = (PreviewVideoHolder) holder;
            releaseVideo(videoHolder);
        }
    }

    private PreviewVideoHolder currentVideoHolder;

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof PreviewVideoHolder) {
            currentVideoHolder = (PreviewVideoHolder) holder;
            addVideoListener(currentVideoHolder);
        }
    }

    /**
     * 释放当前视频Holder相关
     */
    public void destroyCurrentVideoHolder() {
        if (currentVideoHolder != null) {
            releaseVideo(currentVideoHolder);
        }
    }


    /**
     * 释放VideoView
     *
     * @param videoHolder
     */
    private void releaseVideo(PreviewVideoHolder videoHolder) {
        videoHolder.videoView.stopPlayback();
        videoHolder.previewView.setVisibility(View.VISIBLE);
        videoHolder.ivPlayButton.setVisibility(View.VISIBLE);
        videoHolder.videoView.setVisibility(View.GONE);
        videoHolder.videoView.setOnErrorListener(null);
        videoHolder.videoView.setOnCompletionListener(null);
        videoHolder.videoView.setOnPreparedListener(null);
    }

    private OnPreviewEventListener mPreviewEventListener;

    public void setOnPreviewEventListener(OnPreviewEventListener listener) {
        this.mPreviewEventListener = listener;
    }

    public interface OnPreviewEventListener {

        void onBackPressed();

        void onPreviewVideoTitle(String videoName);

        void onLongPressDownload(LocalMedia media);
    }
}
