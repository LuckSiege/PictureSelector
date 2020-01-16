package com.luck.picture.lib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.AnimUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.VoiceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author：luck
 * @date：2016-12-30 12:02
 * @describe：图片列表
 */
public class PictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private boolean showCamera;
    private OnPhotoSelectChangedListener imageSelectChangedListener;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private PictureSelectionConfig config;
    /**
     * 单选图片
     */
    private boolean isGo;

    public PictureImageGridAdapter(Context context, PictureSelectionConfig config) {
        this.context = context;
        this.config = config;
        this.showCamera = config.isCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void bindImagesData(List<LocalMedia> images) {
        this.images = images == null ? new ArrayList<>() : images;
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> images) {
        // 这里重新构构造一个新集合，不然会产生已选集合一变，结果集合也会添加的问题
        List<LocalMedia> selection = new ArrayList<>();
        int size = images.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = images.get(i);
            selection.add(media);
        }
        this.selectImages = selection;
        if (!config.isSingleDirectReturn) {
            subSelectPosition();
            if (imageSelectChangedListener != null) {
                imageSelectChangedListener.onChange(selectImages);
            }
        }
    }

    public List<LocalMedia> getSelectedImages() {
        return selectImages == null ? new ArrayList<>() : selectImages;
    }

    public List<LocalMedia> getImages() {
        return images == null ? new ArrayList<>() : images;
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
            View view = LayoutInflater.from(context).inflate(R.layout.picture_item_camera, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.picture_image_grid_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == PictureConfig.TYPE_CAMERA) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerView.setOnClickListener(v -> {
                if (imageSelectChangedListener != null) {
                    imageSelectChangedListener.onTakePhoto();
                }
            });
        } else {
            final ViewHolder contentHolder = (ViewHolder) holder;
            final LocalMedia image = images.get(showCamera ? position - 1 : position);
            image.position = contentHolder.getAdapterPosition();
            final String path = image.getPath();
            final String mimeType = image.getMimeType();
            if (config.checkNumMode) {
                notifyCheckChanged(contentHolder, image);
            }
            if (!config.isSingleDirectReturn) {
                selectImage(contentHolder, isSelected(image));
            }
            boolean gif = PictureMimeType.isGif(mimeType);
            contentHolder.tvCheck.setVisibility(config.isSingleDirectReturn ? View.GONE : View.VISIBLE);
            contentHolder.btnCheck.setVisibility(config.isSingleDirectReturn ? View.GONE : View.VISIBLE);
            contentHolder.tvIsGif.setVisibility(gif ? View.VISIBLE : View.GONE);
            boolean eqImage = PictureMimeType.eqImage(image.getMimeType());
            if (eqImage) {
                boolean eqLongImg = MediaUtils.isLongImg(image);
                contentHolder.tvLongChart.setVisibility(eqLongImg ? View.VISIBLE : View.GONE);
            } else {
                contentHolder.tvLongChart.setVisibility(View.GONE);
            }
            boolean eqVideo = PictureMimeType.eqVideo(mimeType);
            boolean eqAudio = PictureMimeType.eqAudio(mimeType);
            if (eqVideo || eqAudio) {
                contentHolder.tvDuration.setVisibility(View.VISIBLE);
                contentHolder.tvDuration.setText(DateUtils.formatDurationTime(image.getDuration()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    contentHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                            (eqVideo ? R.drawable.picture_icon_video : R.drawable.picture_icon_audio,
                                    0, 0, 0);
                }
            } else {
                contentHolder.tvDuration.setVisibility(View.GONE);
            }
            if (config.chooseMode == PictureMimeType.ofAudio()) {
                contentHolder.ivPicture.setImageResource(R.drawable.picture_audio_placeholder);
            } else {
                if (config.imageEngine != null) {
                    config.imageEngine.loadGridImage(context, path, contentHolder.ivPicture);
                }
            }
            if (config.enablePreview || config.enPreviewVideo || config.enablePreviewAudio) {
                contentHolder.btnCheck.setOnClickListener(v -> {
                    // 如原图路径不存在或者路径存在但文件不存在
                    String newPath = SdkVersionUtils.checkedAndroid_Q()
                            ? PictureFileUtils.getPath(context, Uri.parse(path)) : path;
                    if (!new File(newPath).exists()) {
                        ToastUtils.s(context, PictureMimeType.s(context, mimeType));
                        return;
                    }
                    if (SdkVersionUtils.checkedAndroid_Q()) {
                        image.setRealPath(newPath);
                    }
                    changeCheckboxState(contentHolder, image);
                });
            }
            contentHolder.contentView.setOnClickListener(v -> {
                // 如原图路径不存在或者路径存在但文件不存在
                String newPath = SdkVersionUtils.checkedAndroid_Q()
                        ? PictureFileUtils.getPath(context, Uri.parse(path)) : path;
                if (!new File(newPath).exists()) {
                    ToastUtils.s(context, PictureMimeType.s(context, mimeType));
                    return;
                }
                int index = showCamera ? position - 1 : position;
                if (index == -1) {
                    return;
                }
                if (SdkVersionUtils.checkedAndroid_Q()) {
                    image.setRealPath(newPath);
                }
                boolean eqResult =
                        PictureMimeType.eqImage(mimeType) && config.enablePreview
                                || PictureMimeType.eqVideo(mimeType) && (config.enPreviewVideo
                                || config.selectionMode == PictureConfig.SINGLE)
                                || PictureMimeType.eqAudio(mimeType) && (config.enablePreviewAudio
                                || config.selectionMode == PictureConfig.SINGLE);
                if (eqResult) {
                    if (PictureMimeType.eqVideo(image.getMimeType())) {
                        if (config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                            // 视频小于最低指定的长度
                            ToastUtils.s(context,
                                    contentHolder.itemView.getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                            return;
                        }
                        if (config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                            // 视频时长超过了指定的长度
                            ToastUtils.s(context,
                                    contentHolder.itemView.getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                            return;
                        }
                    }
                    imageSelectChangedListener.onPictureClick(image, index);
                } else {
                    changeCheckboxState(contentHolder, image);
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
        TextView tvCamera;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
            tvCamera = itemView.findViewById(R.id.tvCamera);
            String title = config.chooseMode == PictureMimeType.ofAudio() ?
                    context.getString(R.string.picture_tape)
                    : context.getString(R.string.picture_take_picture);
            tvCamera.setText(title);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        TextView tvCheck;
        TextView tvDuration, tvIsGif, tvLongChart;
        View contentView;
        View btnCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            btnCheck = itemView.findViewById(R.id.btnCheck);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvIsGif = itemView.findViewById(R.id.tv_isGif);
            tvLongChart = itemView.findViewById(R.id.tv_long_chart);
            if (config.style != null) {
                if (config.style.pictureCheckedStyle != 0) {
                    tvCheck.setBackgroundResource(config.style.pictureCheckedStyle);
                }
            }
        }
    }

    public boolean isSelected(LocalMedia image) {
        int size = selectImages.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectImages.get(i);
            if (media == null || TextUtils.isEmpty(media.getPath())) {
                continue;
            }
            if (media.getPath()
                    .equals(image.getPath())
                    || media.getId() == image.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(ViewHolder viewHolder, LocalMedia imageBean) {
        viewHolder.tvCheck.setText("");
        int size = selectImages.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectImages.get(i);
            if (media.getPath().equals(imageBean.getPath())
                    || media.getId() == imageBean.getId()) {
                imageBean.setNum(media.getNum());
                media.setPosition(imageBean.getPosition());
                viewHolder.tvCheck.setText(String.valueOf(imageBean.getNum()));
            }
        }
    }

    /**
     * 改变图片选中状态
     *
     * @param contentHolder
     * @param image
     */

    @SuppressLint("StringFormatMatches")
    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.tvCheck.isSelected();
        int size = selectImages.size();
        String mimeType = size > 0 ? selectImages.get(0).getMimeType() : "";

        if (config.isWithVideoImage) {
            // 混选模式
            int videoSize = 0;
            int imageSize = 0;
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectImages.get(i);
                if (PictureMimeType.eqVideo(media.getMimeType())) {
                    videoSize++;
                } else {
                    imageSize++;
                }
            }
            if (PictureMimeType.eqVideo(image.getMimeType())) {
                if (config.maxVideoSelectNum > 0
                        && videoSize >= config.maxVideoSelectNum && !isChecked) {
                    // 如果选择的是视频
                    ToastUtils.s(context, StringUtils.getMsg(context, image.getMimeType(), config.maxVideoSelectNum));
                    return;
                }

                if (!isChecked && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                    // 视频小于最低指定的长度
                    ToastUtils.s(context,
                            contentHolder.itemView.getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                    return;
                }

                if (!isChecked && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                    // 视频时长超过了指定的长度
                    ToastUtils.s(context,
                            contentHolder.itemView.getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                    return;
                }
            }
            if (PictureMimeType.eqImage(image.getMimeType()) && imageSize >= config.maxSelectNum && !isChecked) {
                ToastUtils.s(context, StringUtils.getMsg(context, image.getMimeType(), config.maxSelectNum));
                return;
            }


        } else {
            // 非混选模式
            if (!TextUtils.isEmpty(mimeType)) {
                boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, image.getMimeType());
                if (!mimeTypeSame) {
                    ToastUtils.s(context, context.getString(R.string.picture_rule));
                    return;
                }
            }
            if (PictureMimeType.eqVideo(mimeType)) {
                if (config.maxVideoSelectNum > 0
                        && size >= config.maxVideoSelectNum && !isChecked) {
                    // 如果先选择的是视频
                    ToastUtils.s(context, StringUtils.getMsg(context, mimeType, config.maxVideoSelectNum));
                    return;
                }
                if (!isChecked && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                    // 视频小于最低指定的长度
                    ToastUtils.s(context,
                            contentHolder.itemView.getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                    return;
                }

                if (!isChecked && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                    // 视频时长超过了指定的长度
                    ToastUtils.s(context,
                            contentHolder.itemView.getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                    return;
                }
            } else {
                if (size >= config.maxSelectNum && !isChecked) {
                    ToastUtils.s(context, StringUtils.getMsg(context, mimeType, config.maxSelectNum));
                    return;
                }
                if (PictureMimeType.eqVideo(image.getMimeType())) {
                    if (!isChecked && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        // 视频小于最低指定的长度
                        ToastUtils.s(context,
                                contentHolder.itemView.getContext().getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!isChecked && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        // 视频时长超过了指定的长度
                        ToastUtils.s(context,
                                contentHolder.itemView.getContext().getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                }
            }
        }

        if (isChecked) {
            for (int i = 0; i < size; i++) {
                LocalMedia media = selectImages.get(i);
                if (media == null || TextUtils.isEmpty(media.getPath())) {
                    continue;
                }
                if (media.getPath().equals(image.getPath())
                        || media.getId() == image.getId()) {
                    selectImages.remove(media);
                    subSelectPosition();
                    AnimUtils.disZoom(contentHolder.ivPicture, config.zoomAnim);
                    break;
                }
            }
        } else {
            // 如果是单选，则清空已选中的并刷新列表(作单一选择)
            if (config.selectionMode == PictureConfig.SINGLE) {
                singleRadioMediaImage();
            }
            selectImages.add(image);
            image.setNum(selectImages.size());
            VoiceUtils.getInstance().play();
            AnimUtils.zoom(contentHolder.ivPicture, config.zoomAnim);
            contentHolder.tvCheck.startAnimation(AnimationUtils.loadAnimation(context, R.anim.picture_anim_modal_in));
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isChecked);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    /**
     * 单选模式
     */
    private void singleRadioMediaImage() {
        if (selectImages != null
                && selectImages.size() > 0) {
            isGo = true;
            LocalMedia media = selectImages.get(0);
            notifyItemChanged(config.isCamera ? media.position :
                    isGo ? media.position : media.position > 0 ? media.position - 1 : 0);
            selectImages.clear();
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        if (config.checkNumMode) {
            int size = selectImages.size();
            for (int index = 0, length = size; index < length; index++) {
                LocalMedia media = selectImages.get(index);
                media.setNum(index + 1);
                notifyItemChanged(media.position);
            }
        }
    }

    /**
     * 选中的图片并执行动画
     *
     * @param holder
     * @param isChecked
     */
    public void selectImage(ViewHolder holder, boolean isChecked) {
        holder.tvCheck.setSelected(isChecked);
        if (isChecked) {
            holder.ivPicture.setColorFilter(ContextCompat.getColor
                    (context, R.color.picture_color_80), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.ivPicture.setColorFilter(ContextCompat.getColor
                    (context, R.color.picture_color_20), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public interface OnPhotoSelectChangedListener {
        /**
         * 拍照回调
         */
        void onTakePhoto();

        /**
         * 已选Media回调
         *
         * @param selectImages
         */
        void onChange(List<LocalMedia> selectImages);

        /**
         * 图片预览回调
         *
         * @param media
         * @param position
         */
        void onPictureClick(LocalMedia media, int position);
    }

    public void setOnPhotoSelectChangedListener(OnPhotoSelectChangedListener
                                                        imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }


}
