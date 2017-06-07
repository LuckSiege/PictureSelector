package com.luck.picture.lib.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.luck.picture.lib.R;
import com.luck.picture.lib.anim.OptAnimationLoader;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.VoiceUtils;
import com.luck.picture.lib.widget.CustomToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dee on 15/11/19.
 */
public class PictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private boolean showCamera = true;
    private OnPhotoSelectChangedListener imageSelectChangedListener;
    private int maxSelectNum;
    private List<LocalMedia> images = new ArrayList<LocalMedia>();
    private List<LocalMedia> selectImages = new ArrayList<LocalMedia>();
    private boolean enablePreview;
    private int selectMode = PictureConfig.MULTIPLE;
    private boolean enablePreviewVideo = false;
    private boolean is_checked_num;
    private boolean enableVoice;
    private int overrideWidth, overrideHeight;
    private Animation animation;
    private PictureSelectionConfig config;
    private boolean isRemove;

    public PictureImageGridAdapter(Context context, PictureSelectionConfig config) {
        this.context = context;
        this.config = config;
        this.selectMode = config.selectionMode;
        this.showCamera = config.isCamera;
        this.maxSelectNum = config.maxSelectNum;
        this.enablePreview = config.enablePreview;
        this.enablePreviewVideo = config.enPreviewVideo;
        this.is_checked_num = config.checkNumMode;
        this.overrideWidth = config.overrideWidth;
        this.overrideHeight = config.overrideHeight;
        this.enableVoice = config.openClickSound;
        isRemove = config.isRemove;
        animation = OptAnimationLoader.loadAnimation(context, R.anim.modal_in);
        overrideWidth = overrideWidth <= 0 ? 180 : overrideWidth;
        overrideHeight = overrideHeight <= 0 ? 180 : overrideHeight;
        DebugUtil.i("image glide wh:", overrideWidth + ":" + overrideHeight);
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void bindImagesData(List<LocalMedia> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> images) {
        // 这里重新构构造一个新集合，不然会产生已选集合一变，结果集合也会添加的问题
        List<LocalMedia> selection = new ArrayList<>();
        for (LocalMedia media : images) {
            selection.add(media);
        }
        this.selectImages = selection;
        subSelectPosition();
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    public List<LocalMedia> getSelectedImages() {
        if (selectImages == null) {
            selectImages = new ArrayList<>();
        }
        return selectImages;
    }

    public List<LocalMedia> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return PictureConfig.TYPE_CAMERA;
        } else {
            return PictureConfig.TYPE_PICTURE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PictureConfig.TYPE_CAMERA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item_camera, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_image_grid_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == PictureConfig.TYPE_CAMERA) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageSelectChangedListener != null) {
                        imageSelectChangedListener.onTakePhoto();
                    }
                }
            });
        } else {
            final ViewHolder contentHolder = (ViewHolder) holder;
            DebugUtil.i("onBindViewHolder:", "this is refresh position--->" + position);
            final LocalMedia image = images.get(showCamera ? position - 1 : position);
            image.position = contentHolder.getAdapterPosition();
            String path = image.getPath();
            final String pictureType = image.getPictureType();
            contentHolder.ll_check.setVisibility(selectMode ==
                    PictureConfig.SINGLE ? View.GONE : View.VISIBLE);
            if (is_checked_num) {
                notifyCheckChanged(contentHolder, image);
            }
            selectImage(contentHolder, isSelected(image), false);

            final int picture = PictureMimeType.isPictureType(pictureType);
            boolean gif = PictureMimeType.isGif(pictureType);
            contentHolder.tv_isGif.setVisibility(gif ? View.VISIBLE : View.GONE);
            contentHolder.tv_duration.setVisibility(picture == PictureConfig.TYPE_VIDEO
                    ? View.VISIBLE : View.GONE);
            int width = image.getWidth();
            int height = image.getHeight();
            int h = width * 5;
            contentHolder.tv_long_chart.setVisibility(height > h ? View.VISIBLE : View.GONE);
            long duration = image.getDuration();
            contentHolder.tv_duration.setText(DateUtils.timeParse(duration));

            Glide.with(context).load(path).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop().placeholder(R.drawable.image_placeholder)
                    .override(overrideWidth, overrideHeight).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    contentHolder.iv_picture.setImageBitmap(resource);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    if (isRemove) {
                        images.remove(image);
                        notifyDataSetChanged();
                        if (images.size() == 0) {
                            new CustomToast().showToast(context,
                                    context.getString(R.string.picture_warning));
                        }
                    }
                }
            });

            if (enablePreview || enablePreviewVideo) {
                contentHolder.ll_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeCheckboxState(contentHolder, image);
                    }
                });

            }
            contentHolder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (picture == PictureConfig.TYPE_IMAGE && enablePreview) {
                        int index = showCamera ? position - 1 : position;
                        imageSelectChangedListener.onPictureClick(image, index);
                    } else if (picture == PictureConfig.TYPE_VIDEO && enablePreviewVideo) {
                        int index = showCamera ? position - 1 : position;
                        imageSelectChangedListener.onPictureClick(image, index);
                    } else {
                        changeCheckboxState(contentHolder, image);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return showCamera ? images.size() + 1 : images.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        View headerView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_picture;
        TextView check;
        TextView tv_duration, tv_isGif, tv_long_chart;
        View contentView;
        LinearLayout ll_check;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            iv_picture = (ImageView) itemView.findViewById(R.id.iv_picture);
            check = (TextView) itemView.findViewById(R.id.check);
            ll_check = (LinearLayout) itemView.findViewById(R.id.ll_check);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            tv_isGif = (TextView) itemView.findViewById(R.id.tv_isGif);
            tv_long_chart = (TextView) itemView.findViewById(R.id.tv_long_chart);
        }
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(ViewHolder viewHolder, LocalMedia imageBean) {
        viewHolder.check.setText("");
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(imageBean.getPath())) {
                imageBean.setNum(media.getNum());
                media.setPosition(imageBean.getPosition());
                viewHolder.check.setText(String.valueOf(imageBean.getNum()));
            }
        }
    }

    /**
     * 改变图片选中状态
     *
     * @param contentHolder
     * @param image
     */

    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.check.isSelected();
        String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
        if (!TextUtils.isEmpty(pictureType)) {
            boolean toEqual = PictureMimeType.mimeToEqual(pictureType, image.getPictureType());
            if (!toEqual) {
                Toast.makeText(context, context.getString(R.string.picture_rule), Toast.LENGTH_LONG)
                        .show();
                return;
            }
        }
        if (selectImages.size() >= maxSelectNum && !isChecked) {
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            String str = eqImg ? context.getString(R.string.picture_message_max_num, maxSelectNum)
                    : context.getString(R.string.picture_message_video_max_num, maxSelectNum);
            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            return;
        }

        if (isChecked) {
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(image.getPath())) {
                    selectImages.remove(media);
                    DebugUtil.i("selectImages remove::", config.selectionMedias.size() + "");
                    subSelectPosition();
                    break;
                }
            }
        } else {
            selectImages.add(image);
            DebugUtil.i("selectImages add::", config.selectionMedias.size() + "");
            image.setNum(selectImages.size());
            VoiceUtils.playVoice(context, enableVoice);
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isChecked, true);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        if (is_checked_num) {
            int size = selectImages.size();
            for (int index = 0, length = size; index < length; index++) {
                LocalMedia media = selectImages.get(index);
                media.setNum(index + 1);
                notifyItemChanged(media.position);
            }
        }
    }

    public void selectImage(ViewHolder holder, boolean isChecked, boolean isAnim) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            if (isAnim) {
                if (animation != null)
                    holder.check.startAnimation(animation);
            }
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (context, R.color.image_overlay_true), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.iv_picture.setColorFilter(ContextCompat.getColor
                    (context, R.color.image_overlay_false), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public interface OnPhotoSelectChangedListener {
        void onTakePhoto();

        void onChange(List<LocalMedia> selectImages);


        void onPictureClick(LocalMedia media, int position);
    }

    public void setOnPhotoSelectChangedListener(OnPhotoSelectChangedListener
                                                        imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }

}
