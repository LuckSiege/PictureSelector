package com.luck.picture.lib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureCustomDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnPhotoSelectChangedListener;
import com.luck.picture.lib.tools.AnimUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.tools.ValueOf;
import com.luck.picture.lib.tools.VoiceUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author：luck
 * @date：2016-12-30 12:02
 * @describe：PictureImageGridAdapter
 */
public class PictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private boolean showCamera;
    private OnPhotoSelectChangedListener<LocalMedia> imageSelectChangedListener;
    private List<LocalMedia> data = new ArrayList<>();
    private List<LocalMedia> selectData = new ArrayList<>();
    private final PictureSelectionConfig config;

    public PictureImageGridAdapter(Context context, PictureSelectionConfig config) {
        this.context = context;
        this.config = config;
        this.showCamera = config.isCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 全量刷新
     *
     * @param data
     */
    public void bindData(List<LocalMedia> data) {
        this.data = data == null ? new ArrayList<>() : data;
        this.notifyDataSetChanged();
    }


    public void bindSelectData(List<LocalMedia> images) {
        // 这里重新构构造一个新集合，不然会产生已选集合一变，结果集合也会添加的问题
        List<LocalMedia> selection = new ArrayList<>();
        int size = images.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = images.get(i);
            selection.add(media);
        }
        this.selectData = selection;
        if (!config.isSingleDirectReturn) {
            subSelectPosition();
            if (imageSelectChangedListener != null) {
                imageSelectChangedListener.onChange(selectData);
            }
        }
    }

    public List<LocalMedia> getSelectedData() {
        return selectData == null ? new ArrayList<>() : selectData;
    }

    public int getSelectedSize() {
        return selectData == null ? 0 : selectData.size();
    }

    public List<LocalMedia> getData() {
        return data == null ? new ArrayList<>() : data;
    }

    public boolean isDataEmpty() {
        return data == null || data.size() == 0;
    }

    public void clear() {
        if (getSize() > 0) {
            data.clear();
        }
    }

    public int getSize() {
        return data == null ? 0 : data.size();
    }

    public LocalMedia getItem(int position) {
        return getSize() > 0 ? data.get(position) : null;
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
            return new CameraViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.picture_image_grid_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == PictureConfig.TYPE_CAMERA) {
            CameraViewHolder headerHolder = (CameraViewHolder) holder;
            headerHolder.itemView.setOnClickListener(v -> {
                if (imageSelectChangedListener != null) {
                    imageSelectChangedListener.onTakePhoto();
                }
            });
        } else {
            ViewHolder contentHolder = (ViewHolder) holder;
            LocalMedia image = data.get(showCamera ? position - 1 : position);
            image.position = contentHolder.getAbsoluteAdapterPosition();
            String mimeType = image.getMimeType();
            if (config.checkNumMode) {
                notifyCheckChanged(contentHolder, image);
            }
            if (config.isSingleDirectReturn) {
                contentHolder.tvCheck.setVisibility(View.GONE);
                contentHolder.btnCheck.setVisibility(View.GONE);
            } else {
                selectImage(contentHolder, isSelected(image));
                contentHolder.tvCheck.setVisibility(View.VISIBLE);
                contentHolder.btnCheck.setVisibility(View.VISIBLE);
                // 启用了蒙层效果
                if (config.isMaxSelectEnabledMask) {
                    dispatchHandleMask(contentHolder, image);
                }
            }
            String path = image.getPath();
            if (image.isEditorImage() && !TextUtils.isEmpty(image.getCutPath())) {
                contentHolder.ivEditor.setVisibility(View.VISIBLE);
                path = image.getCutPath();
            } else {
                contentHolder.ivEditor.setVisibility(View.GONE);
            }
            boolean isGif = PictureMimeType.isGif(mimeType);
            boolean isWebp = PictureMimeType.isWebp(mimeType);
            boolean isLongImg = MediaUtils.isLongImg(image);
            if ((isGif || isWebp) && !isLongImg) {
                contentHolder.tvImageMimeType.setVisibility(View.VISIBLE);
                contentHolder.tvImageMimeType.setText(isGif ? context.getString(R.string.picture_gif_tag) : context.getString(R.string.picture_webp_tag));
            } else {
                contentHolder.tvImageMimeType.setVisibility(View.GONE);
            }
            if (PictureMimeType.isHasImage(image.getMimeType())) {
                if (image.loadLongImageStatus == PictureConfig.NORMAL) {
                    image.isLongImage = isLongImg;
                    image.loadLongImageStatus = PictureConfig.LOADED;
                }
                contentHolder.tvLongChart.setVisibility(image.isLongImage ? View.VISIBLE : View.GONE);
            } else {
                image.loadLongImageStatus = PictureConfig.NORMAL;
                contentHolder.tvLongChart.setVisibility(View.GONE);
            }
            boolean isHasVideo = PictureMimeType.isHasVideo(mimeType);
            if (isHasVideo || PictureMimeType.isHasAudio(mimeType)) {
                contentHolder.tvDuration.setVisibility(View.VISIBLE);
                contentHolder.tvDuration.setText(DateUtils.formatDurationTime(image.getDuration()));
                if (PictureSelectionConfig.uiStyle != null) {
                    if (isHasVideo) {
                        if (PictureSelectionConfig.uiStyle.picture_adapter_item_video_textLeftDrawable != 0) {
                            contentHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                                    (PictureSelectionConfig.uiStyle.picture_adapter_item_video_textLeftDrawable,
                                            0, 0, 0);
                        } else {
                            contentHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                                    (R.drawable.picture_icon_video, 0, 0, 0);
                        }
                    } else {
                        if (PictureSelectionConfig.uiStyle.picture_adapter_item_audio_textLeftDrawable != 0) {
                            contentHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                                    (PictureSelectionConfig.uiStyle.picture_adapter_item_audio_textLeftDrawable,
                                            0, 0, 0);
                        } else {
                            contentHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                                    (R.drawable.picture_icon_audio, 0, 0, 0);
                        }
                    }
                } else {
                    contentHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                            (isHasVideo ? R.drawable.picture_icon_video : R.drawable.picture_icon_audio,
                                    0, 0, 0);
                }
            } else {
                contentHolder.tvDuration.setVisibility(View.GONE);
            }
            if (config.chooseMode == PictureMimeType.ofAudio()) {
                contentHolder.ivPicture.setImageResource(R.drawable.picture_audio_placeholder);
            } else {
                if (PictureSelectionConfig.imageEngine != null) {
                    PictureSelectionConfig.imageEngine.loadGridImage(context, path, contentHolder.ivPicture);
                }
            }

            if (config.enablePreview || config.enPreviewVideo || config.enablePreviewAudio) {
                contentHolder.btnCheck.setOnClickListener(v -> {
                    if (config.isMaxSelectEnabledMask) {
                        if (config.isWithVideoImage) {
                            int selectedCount = getSelectedSize();
                            int videoSize = 0;
                            for (int i = 0; i < selectedCount; i++) {
                                LocalMedia media = selectData.get(i);
                                if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                    videoSize++;
                                }
                            }
                            String errorMsg;
                            boolean isNotOption;
                            if (PictureMimeType.isHasVideo(image.getMimeType())) {
                                isNotOption = !contentHolder.tvCheck.isSelected() && videoSize >= config.maxVideoSelectNum;
                                errorMsg = StringUtils.getMsg(context, image.getMimeType(), config.maxVideoSelectNum);
                            } else {
                                isNotOption = !contentHolder.tvCheck.isSelected() && selectedCount >= config.maxSelectNum;
                                errorMsg = StringUtils.getMsg(context, image.getMimeType(), config.maxSelectNum);
                            }
                            if (isNotOption) {
                                showPromptDialog(errorMsg);
                                return;
                            }
                        } else {
                            if (!contentHolder.tvCheck.isSelected() && getSelectedSize() >= config.maxSelectNum) {
                                String msg = StringUtils.getMsg(context, image.getMimeType(), config.maxSelectNum);
                                showPromptDialog(msg);
                                return;
                            }
                        }
                    }
                    // If the original path does not exist or the path does exist but the file does not exist
                    String newPath = image.getRealPath();
                    if (!TextUtils.isEmpty(newPath) && !new File(newPath).exists()) {
                        ToastUtils.s(context, PictureMimeType.s(context, mimeType));
                        return;
                    }
                    changeCheckboxState(contentHolder, image);
                });
            }
            contentHolder.contentView.setOnClickListener(v -> {
                if (config.isMaxSelectEnabledMask) {
                    if (image.isMaxSelectEnabledMask()) {
                        return;
                    }
                }
                // If the original path does not exist or the path does exist but the file does not exist
                String newPath = image.getRealPath();
                if (!TextUtils.isEmpty(newPath) && !new File(newPath).exists()) {
                    ToastUtils.s(context, PictureMimeType.s(context, mimeType));
                    return;
                }
                int index = showCamera ? position - 1 : position;
                if (index == -1) {
                    return;
                }
                boolean eqResult =
                        PictureMimeType.isHasImage(mimeType) && config.enablePreview
                                || config.isSingleDirectReturn
                                || PictureMimeType.isHasVideo(mimeType) && (config.enPreviewVideo
                                || config.selectionMode == PictureConfig.SINGLE)
                                || PictureMimeType.isHasAudio(mimeType) && (config.enablePreviewAudio
                                || config.selectionMode == PictureConfig.SINGLE);
                if (eqResult) {
                    if (PictureMimeType.isHasVideo(image.getMimeType())) {
                        if (config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                            // The video is less than the minimum specified length
                            showPromptDialog(context.getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                            return;
                        }
                        if (config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                            // The length of the video exceeds the specified length
                            showPromptDialog(context.getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
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

    /**
     * Handle mask effects
     *
     * @param contentHolder
     * @param item
     */
    private void dispatchHandleMask(ViewHolder contentHolder, LocalMedia item) {
        if (config.isWithVideoImage && config.maxVideoSelectNum > 0) {
            if (getSelectedSize() >= config.maxSelectNum) {
                boolean isSelected = contentHolder.tvCheck.isSelected();
                ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(isSelected ?
                                ContextCompat.getColor(context, R.color.picture_color_80) :
                                ContextCompat.getColor(context, R.color.picture_color_half_white),
                        BlendModeCompat.SRC_ATOP);
                contentHolder.ivPicture.setColorFilter(colorFilter);
                item.setMaxSelectEnabledMask(!isSelected);
            } else {
                item.setMaxSelectEnabledMask(false);
            }
        } else {
            LocalMedia media = selectData.size() > 0 ? selectData.get(0) : null;
            if (media != null) {
                boolean isSelected = contentHolder.tvCheck.isSelected();
                if (config.chooseMode == PictureMimeType.ofAll()) {
                    if (PictureMimeType.isHasImage(media.getMimeType())) {
                        // All videos are not optional
                        if (!isSelected && !PictureMimeType.isHasImage(item.getMimeType())) {
                            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor
                                    (context, PictureMimeType.isHasVideo(item.getMimeType()) ? R.color.picture_color_half_white : R.color.picture_color_20), BlendModeCompat.SRC_ATOP);
                            contentHolder.ivPicture.setColorFilter(colorFilter);
                        }
                        item.setMaxSelectEnabledMask(PictureMimeType.isHasVideo(item.getMimeType()));
                    } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                        // All images are not optional
                        if (!isSelected && !PictureMimeType.isHasVideo(item.getMimeType())) {
                            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor
                                    (context, PictureMimeType.isHasImage(item.getMimeType()) ? R.color.picture_color_half_white : R.color.picture_color_20), BlendModeCompat.SRC_ATOP);
                            contentHolder.ivPicture.setColorFilter(colorFilter);
                        }
                        item.setMaxSelectEnabledMask(PictureMimeType.isHasImage(item.getMimeType()));
                    }
                } else {
                    if (config.chooseMode == PictureMimeType.ofVideo() && config.maxVideoSelectNum > 0) {
                        if (!isSelected && getSelectedSize() == config.maxVideoSelectNum) {
                            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor
                                    (context, R.color.picture_color_half_white), BlendModeCompat.SRC_ATOP);
                            contentHolder.ivPicture.setColorFilter(colorFilter);
                        }
                        item.setMaxSelectEnabledMask(!isSelected && getSelectedSize() == config.maxVideoSelectNum);
                    } else {
                        if (!isSelected && getSelectedSize() == config.maxSelectNum) {
                            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor
                                    (context, R.color.picture_color_half_white), BlendModeCompat.SRC_ATOP);
                            contentHolder.ivPicture.setColorFilter(colorFilter);
                        }
                        item.setMaxSelectEnabledMask(!isSelected && getSelectedSize() == config.maxSelectNum);
                    }
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return showCamera ? data.size() + 1 : data.size();
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder {
        TextView tvCamera;

        public CameraViewHolder(View itemView) {
            super(itemView);
            tvCamera = itemView.findViewById(R.id.tvCamera);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_camera_backgroundColor != 0) {
                    itemView.setBackgroundColor(PictureSelectionConfig.uiStyle.picture_adapter_item_camera_backgroundColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_camera_textSize != 0) {
                    tvCamera.setTextSize(PictureSelectionConfig.uiStyle.picture_adapter_item_camera_textSize);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_camera_textColor != 0) {
                    tvCamera.setTextColor(PictureSelectionConfig.uiStyle.picture_adapter_item_camera_textColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_camera_text != 0) {
                    tvCamera.setText(itemView.getContext().getString(PictureSelectionConfig.uiStyle.picture_adapter_item_camera_text));
                } else {
                    tvCamera.setText(config.chooseMode == PictureMimeType.ofAudio() ? context.getString(R.string.picture_tape)
                            : context.getString(R.string.picture_take_picture));
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_camera_textTopDrawable != 0) {
                    tvCamera.setCompoundDrawablesWithIntrinsicBounds(0, PictureSelectionConfig.uiStyle.picture_adapter_item_camera_textTopDrawable, 0, 0);
                }
            } else {
                tvCamera.setText(config.chooseMode == PictureMimeType.ofAudio() ? context.getString(R.string.picture_tape)
                        : context.getString(R.string.picture_take_picture));
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        ImageView ivEditor;
        TextView tvCheck;
        TextView tvDuration, tvImageMimeType, tvLongChart;
        View contentView;
        View btnCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            btnCheck = itemView.findViewById(R.id.btnCheck);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvImageMimeType = itemView.findViewById(R.id.tv_image_mime_type);
            tvLongChart = itemView.findViewById(R.id.tv_long_chart);
            ivEditor = itemView.findViewById(R.id.ivEditor);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_check_style != 0) {
                    tvCheck.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_check_style);
                }
                if (PictureSelectionConfig.uiStyle.picture_check_textSize != 0) {
                    tvCheck.setTextSize(PictureSelectionConfig.uiStyle.picture_check_textSize);
                }
                if (PictureSelectionConfig.uiStyle.picture_check_textColor != 0) {
                    tvCheck.setTextColor(PictureSelectionConfig.uiStyle.picture_check_textColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_textSize > 0) {
                    tvDuration.setTextSize(PictureSelectionConfig.uiStyle.picture_adapter_item_textSize);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_textColor != 0) {
                    tvDuration.setTextColor(PictureSelectionConfig.uiStyle.picture_adapter_item_textColor);
                }

                if (PictureSelectionConfig.uiStyle.picture_adapter_item_tag_text != 0) {
                    tvImageMimeType.setText(itemView.getContext().getString(PictureSelectionConfig.uiStyle.picture_adapter_item_tag_text));
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_show) {
                    tvImageMimeType.setVisibility(View.VISIBLE);
                } else {
                    tvImageMimeType.setVisibility(View.GONE);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_background != 0) {
                    tvImageMimeType.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_background);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_editor_tag_icon != 0) {
                    ivEditor.setImageResource(PictureSelectionConfig.uiStyle.picture_adapter_item_editor_tag_icon);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_textColor != 0) {
                    tvImageMimeType.setTextColor(PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_textColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_textSize != 0) {
                    tvImageMimeType.setTextSize(PictureSelectionConfig.uiStyle.picture_adapter_item_gif_tag_textSize);
                }
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureCheckedStyle != 0) {
                    tvCheck.setBackgroundResource(PictureSelectionConfig.style.pictureCheckedStyle);
                }
                if (PictureSelectionConfig.style.picture_adapter_item_editor_tag_icon != 0) {
                    ivEditor.setImageResource(PictureSelectionConfig.style.picture_adapter_item_editor_tag_icon);
                }
            } else {
                Drawable checkedStyleDrawable = AttrsUtils.getTypeValueDrawable(itemView.getContext(), R.attr.picture_checked_style, R.drawable.picture_checkbox_selector);
                tvCheck.setBackground(checkedStyleDrawable);
            }
        }
    }

    public boolean isSelected(LocalMedia image) {
        int size = selectData.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectData.get(i);
            if (media == null || TextUtils.isEmpty(media.getPath())) {
                continue;
            }
            if (TextUtils.equals(media.getPath(), image.getPath())
                    || media.getId() == image.getId()) {
                setLocalMediaCropInfo(media, image);
                return true;
            }
        }
        return false;
    }

    /**
     * 编辑模式下-设置裁剪相关信息
     *
     * @param selectedMedia
     * @param curMedia
     */
    private void setLocalMediaCropInfo(LocalMedia selectedMedia, LocalMedia curMedia) {
        if (selectedMedia.isEditorImage() && !curMedia.isEditorImage()) {
            curMedia.setCut(selectedMedia.isCut());
            curMedia.setCutPath(selectedMedia.getCutPath());
            curMedia.setCropImageWidth(selectedMedia.getCropImageWidth());
            curMedia.setCropImageHeight(selectedMedia.getCropImageHeight());
            curMedia.setCropOffsetX(selectedMedia.getCropOffsetX());
            curMedia.setCropOffsetY(selectedMedia.getCropOffsetY());
            curMedia.setCropResultAspectRatio(selectedMedia.getCropResultAspectRatio());
            curMedia.setAndroidQToPath(selectedMedia.getAndroidQToPath());
            curMedia.setEditorImage(selectedMedia.isEditorImage());
        }
    }

    /**
     * Update button status
     */
    private void notifyCheckChanged(ViewHolder viewHolder, LocalMedia imageBean) {
        viewHolder.tvCheck.setText("");
        int size = selectData.size();
        for (int i = 0; i < size; i++) {
            LocalMedia media = selectData.get(i);
            if (media.getPath().equals(imageBean.getPath())
                    || media.getId() == imageBean.getId()) {
                imageBean.setNum(media.getNum());
                media.setPosition(imageBean.getPosition());
                viewHolder.tvCheck.setText(ValueOf.toString(imageBean.getNum()));
            }
        }
    }

    /**
     * Update the selected status of the image
     *
     * @param contentHolder
     * @param image
     */

    @SuppressLint("StringFormatMatches")
    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.tvCheck.isSelected();
        int count = selectData.size();
        String mimeType = count > 0 ? selectData.get(0).getMimeType() : "";
        if (config.isWithVideoImage) {
            // isWithVideoImage mode
            int videoSize = 0;
            for (int i = 0; i < count; i++) {
                LocalMedia media = selectData.get(i);
                if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    videoSize++;
                }
            }

            if (PictureMimeType.isHasVideo(image.getMimeType())) {
                if (config.maxVideoSelectNum <= 0) {
                    showPromptDialog(context.getString(R.string.picture_rule));
                    return;
                }

                if (count >= config.maxSelectNum && !isChecked) {
                    showPromptDialog(context.getString(R.string.picture_message_max_num, config.maxSelectNum));
                    return;
                }

                if (videoSize >= config.maxVideoSelectNum && !isChecked) {
                    showPromptDialog(StringUtils.getMsg(context, image.getMimeType(), config.maxVideoSelectNum));
                    return;
                }

                if (!isChecked && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                    showPromptDialog(context.getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                    return;
                }

                if (!isChecked && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                    showPromptDialog(context.getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                    return;
                }
            } else {
                if (count >= config.maxSelectNum && !isChecked) {
                    showPromptDialog(context.getString(R.string.picture_message_max_num, config.maxSelectNum));
                    return;
                }
            }
        } else {
            if (!TextUtils.isEmpty(mimeType)) {
                boolean mimeTypeSame = PictureMimeType.isMimeTypeSame(mimeType, image.getMimeType());
                if (!mimeTypeSame) {
                    showPromptDialog(context.getString(R.string.picture_rule));
                    return;
                }
            }
            if (PictureMimeType.isHasVideo(mimeType) && config.maxVideoSelectNum > 0) {
                if (count >= config.maxVideoSelectNum && !isChecked) {
                    showPromptDialog(StringUtils.getMsg(context, mimeType, config.maxVideoSelectNum));
                    return;
                }
                if (!isChecked && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                    showPromptDialog(context.getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                    return;
                }

                if (!isChecked && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                    showPromptDialog(context.getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                    return;
                }
            } else {
                if (count >= config.maxSelectNum && !isChecked) {
                    showPromptDialog(StringUtils.getMsg(context, mimeType, config.maxSelectNum));
                    return;
                }
                if (PictureMimeType.isHasVideo(image.getMimeType())) {
                    if (!isChecked && config.videoMinSecond > 0 && image.getDuration() < config.videoMinSecond) {
                        showPromptDialog(context.getString(R.string.picture_choose_min_seconds, config.videoMinSecond / 1000));
                        return;
                    }

                    if (!isChecked && config.videoMaxSecond > 0 && image.getDuration() > config.videoMaxSecond) {
                        showPromptDialog(context.getString(R.string.picture_choose_max_seconds, config.videoMaxSecond / 1000));
                        return;
                    }
                }
            }
        }

        if (isChecked) {
            for (int i = 0; i < count; i++) {
                LocalMedia media = selectData.get(i);
                if (media == null || TextUtils.isEmpty(media.getPath())) {
                    continue;
                }
                if (media.getPath().equals(image.getPath())
                        || media.getId() == image.getId()) {
                    selectData.remove(media);
                    subSelectPosition();
                    AnimUtils.disZoom(contentHolder.ivPicture, config.zoomAnim);
                    break;
                }
            }
        } else {
            // The radio
            if (config.selectionMode == PictureConfig.SINGLE) {
                singleRadioMediaImage();
            }

            selectData.add(image);
            image.setNum(selectData.size());
            VoiceUtils.getInstance().play();
            AnimUtils.zoom(contentHolder.ivPicture, config.zoomAnim);
            contentHolder.tvCheck.startAnimation(AnimationUtils.loadAnimation(context, R.anim.picture_anim_modal_in));
        }

        boolean isRefreshAll = false;
        if (config.isMaxSelectEnabledMask) {
            if (config.chooseMode == PictureMimeType.ofAll()) {
                // ofAll
                if (config.isWithVideoImage && config.maxVideoSelectNum > 0) {
                    if (getSelectedSize() >= config.maxSelectNum) {
                        isRefreshAll = true;
                    }
                    if (isChecked) {
                        // delete
                        if (getSelectedSize() == config.maxSelectNum - 1) {
                            isRefreshAll = true;
                        }
                    }
                } else {
                    if (!isChecked && getSelectedSize() == 1) {
                        // add
                        isRefreshAll = true;
                    }
                    if (isChecked && getSelectedSize() == 0) {
                        // delete
                        isRefreshAll = true;
                    }
                }
            } else {
                // ofImage or ofVideo or ofAudio
                if (config.chooseMode == PictureMimeType.ofVideo() && config.maxVideoSelectNum > 0) {
                    if (!isChecked && getSelectedSize() == config.maxVideoSelectNum) {
                        // add
                        isRefreshAll = true;
                    }
                    if (isChecked && getSelectedSize() == config.maxVideoSelectNum - 1) {
                        // delete
                        isRefreshAll = true;
                    }
                } else {
                    if (!isChecked && getSelectedSize() == config.maxSelectNum) {
                        // add
                        isRefreshAll = true;
                    }
                    if (isChecked && getSelectedSize() == config.maxSelectNum - 1) {
                        // delete
                        isRefreshAll = true;
                    }
                }
            }
        }

        if (isRefreshAll) {
            notifyDataSetChanged();
        } else {
            notifyItemChanged(contentHolder.getAbsoluteAdapterPosition());
        }

        selectImage(contentHolder, !isChecked);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectData);
        }
    }

    /**
     * Radio mode
     */
    private void singleRadioMediaImage() {
        if (selectData != null
                && selectData.size() > 0) {
            LocalMedia media = selectData.get(0);
            notifyItemChanged(media.position);
            selectData.clear();
        }
    }

    /**
     * Update the selection order
     */
    private void subSelectPosition() {
        if (config.checkNumMode) {
            int size = selectData.size();
            for (int index = 0; index < size; index++) {
                LocalMedia media = selectData.get(index);
                media.setNum(index + 1);
                notifyItemChanged(media.position);
            }
        }
    }

    /**
     * Select the image and animate it
     *
     * @param holder
     * @param isChecked
     */
    public void selectImage(ViewHolder holder, boolean isChecked) {
        holder.tvCheck.setSelected(isChecked);
        ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(isChecked ?
                        ContextCompat.getColor(context, R.color.picture_color_80) :
                        ContextCompat.getColor(context, R.color.picture_color_20),
                BlendModeCompat.SRC_ATOP);
        holder.ivPicture.setColorFilter(colorFilter);
    }

    /**
     * Tips
     */
    private void showPromptDialog(String content) {
        if (PictureSelectionConfig.onChooseLimitCallback != null) {
            PictureSelectionConfig.onChooseLimitCallback.onChooseLimit(context, content);
        } else {
            PictureCustomDialog dialog = new PictureCustomDialog(context, R.layout.picture_prompt_dialog);
            TextView btnOk = dialog.findViewById(R.id.btnOk);
            TextView tvContent = dialog.findViewById(R.id.tv_content);
            tvContent.setText(content);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }


    /**
     * Binding listener
     *
     * @param imageSelectChangedListener
     */
    public void setOnPhotoSelectChangedListener(OnPhotoSelectChangedListener<LocalMedia>
                                                        imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }
}
