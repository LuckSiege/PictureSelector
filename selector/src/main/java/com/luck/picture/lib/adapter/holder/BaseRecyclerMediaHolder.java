package com.luck.picture.lib.adapter.holder;

import android.content.Context;
import android.graphics.ColorFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.AnimUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ValueOf;

import java.util.List;

/**
 * @author：luck
 * @date：2021/11/20 3:17 下午
 * @describe：BaseRecyclerMediaHolder
 */
public class BaseRecyclerMediaHolder extends RecyclerView.ViewHolder {
    public ImageView ivPicture;
    public TextView tvCheck;
    public View btnCheck;
    public Context mContext;
    public PictureSelectionConfig config;
    public boolean isSelectNumberStyle;
    public boolean isHandleMask;
    private ColorFilter defaultColorFilter, selectColorFilter, maskWhiteColorFilter;

    public static BaseRecyclerMediaHolder generate(ViewGroup parent, int viewType, int resource, PictureSelectionConfig config) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        switch (viewType) {
            case PictureImageGridAdapter.ADAPTER_TYPE_CAMERA:
                return new CameraViewHolder(itemView);
            case PictureImageGridAdapter.ADAPTER_TYPE_VIDEO:
                return new VideoViewHolder(itemView, config);
            case PictureImageGridAdapter.ADAPTER_TYPE_AUDIO:
                return new AudioViewHolder(itemView, config);
            default:
                return new ImageViewHolder(itemView, config);
        }
    }

    public BaseRecyclerMediaHolder(@NonNull View itemView) {
        super(itemView);
    }

    public BaseRecyclerMediaHolder(@NonNull View itemView, PictureSelectionConfig config) {
        super(itemView);
        this.config = config;
        this.mContext = itemView.getContext();
        defaultColorFilter = StyleUtils.getColorFilter(mContext, R.color.ps_color_20);
        selectColorFilter = StyleUtils.getColorFilter(mContext, R.color.ps_color_80);
        maskWhiteColorFilter = StyleUtils.getColorFilter(mContext, R.color.ps_color_half_white);
        SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
        isSelectNumberStyle = selectMainStyle.isSelectNumberStyle();
        ivPicture = itemView.findViewById(R.id.ivPicture);
        tvCheck = itemView.findViewById(R.id.tvCheck);
        btnCheck = itemView.findViewById(R.id.btnCheck);
        if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
            tvCheck.setVisibility(View.GONE);
            btnCheck.setVisibility(View.GONE);
        } else {
            tvCheck.setVisibility(View.VISIBLE);
            btnCheck.setVisibility(View.VISIBLE);
        }

        isHandleMask = !config.isDirectReturnSingle
                && (config.selectionMode == SelectModeConfig.SINGLE || config.selectionMode == SelectModeConfig.MULTIPLE);

        int textSize = selectMainStyle.getAdapterSelectTextSize();
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvCheck.setTextSize(textSize);
        }
        int textColor = selectMainStyle.getAdapterSelectTextColor();
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvCheck.setTextColor(textColor);
        }
        int adapterSelectBackground = selectMainStyle.getSelectBackground();
        if (StyleUtils.checkStyleValidity(adapterSelectBackground)) {
            tvCheck.setBackgroundResource(adapterSelectBackground);
        }
        int[] selectStyleGravity = selectMainStyle.getAdapterSelectStyleGravity();
        if (StyleUtils.checkArrayValidity(selectStyleGravity)) {
            if (tvCheck.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) tvCheck.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_END);
                for (int i : selectStyleGravity) {
                    ((RelativeLayout.LayoutParams) tvCheck.getLayoutParams()).addRule(i);
                }
            }
            if (btnCheck.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) btnCheck.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_END);
                for (int i : selectStyleGravity) {
                    ((RelativeLayout.LayoutParams) btnCheck.getLayoutParams()).addRule(i);
                }
            }

            int clickArea = selectMainStyle.getAdapterSelectClickArea();
            if (StyleUtils.checkSizeValidity(clickArea)) {
                ViewGroup.LayoutParams clickAreaParams = btnCheck.getLayoutParams();
                clickAreaParams.width = clickArea;
                clickAreaParams.height = clickArea;
            }
        }
    }

    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    public void bindData(LocalMedia media, int position) {
        media.position = getAbsoluteAdapterPosition();

        selectedMedia(isSelected(media));

        if (isSelectNumberStyle) {
            notifySelectNumberStyle(media);
        }

        if (isHandleMask && config.isMaxSelectEnabledMask) {
            dispatchHandleMask(media);
        }

        String path = media.getPath();
        if (media.isEditorImage()) {
            path = media.getCutPath();
        }

        loadCover(path);

        tvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCheck.performClick();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isMaxSelectEnabledMask() || listener == null) {
                    return;
                }
                int resultCode = listener.onSelected(tvCheck, position, media);
                if (resultCode == SelectedManager.INVALID) {
                    return;
                }
                if (resultCode == SelectedManager.ADD_SUCCESS) {
                    if (config.isSelectZoomAnim) {
                        if (PictureSelectionConfig.onItemSelectAnimListener != null) {
                            PictureSelectionConfig.onItemSelectAnimListener.onSelectItemAnim(ivPicture, true);
                        } else {
                            AnimUtils.selectZoom(ivPicture);
                        }
                    }
                } else if (resultCode == SelectedManager.REMOVE) {
                    if (config.isSelectZoomAnim) {
                        if (PictureSelectionConfig.onItemSelectAnimListener != null) {
                            PictureSelectionConfig.onItemSelectAnimListener.onSelectItemAnim(ivPicture, false);
                        }
                    }
                }
                selectedMedia(isSelected(media));
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onItemLongClick(v, position);
                }
                return false;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isMaxSelectEnabledMask() || listener == null) {
                    return;
                }
                boolean isPreview = PictureMimeType.isHasImage(media.getMimeType()) && config.isEnablePreviewImage
                        || config.isDirectReturnSingle
                        || PictureMimeType.isHasVideo(media.getMimeType()) && (config.isEnablePreviewVideo
                        || config.selectionMode == SelectModeConfig.SINGLE)
                        || PictureMimeType.isHasAudio(media.getMimeType()) && (config.isEnablePreviewAudio
                        || config.selectionMode == SelectModeConfig.SINGLE);
                if (isPreview) {
                    listener.onItemClick(tvCheck, position, media);
                } else {
                    btnCheck.performClick();
                }
            }
        });
    }

    /**
     * 加载资源封面
     */
    protected void loadCover(String path) {
        if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadGridImage(ivPicture.getContext(), path, ivPicture);
        }
    }


    /**
     * 处理到达选择条件后的蒙层效果
     */
    private void dispatchHandleMask(LocalMedia media) {
        boolean isEnabledMask = false;
        if (SelectedManager.getSelectCount() > 0 && !SelectedManager.getSelectedResult().contains(media)) {
            if (config.isWithVideoImage) {
                if (config.selectionMode == SelectModeConfig.SINGLE) {
                    isEnabledMask = SelectedManager.getSelectCount() == Integer.MAX_VALUE;
                } else {
                    isEnabledMask = SelectedManager.getSelectCount() == config.maxSelectNum;
                }
            } else {
                if (PictureMimeType.isHasVideo(SelectedManager.getTopResultMimeType())) {
                    int maxSelectNum;
                    if (config.selectionMode == SelectModeConfig.SINGLE) {
                        maxSelectNum = Integer.MAX_VALUE;
                    } else {
                        maxSelectNum = config.maxVideoSelectNum > 0
                                ? config.maxVideoSelectNum : config.maxSelectNum;
                    }
                    isEnabledMask = SelectedManager.getSelectCount() == maxSelectNum
                            || PictureMimeType.isHasImage(media.getMimeType());
                } else {
                    int maxSelectNum;
                    if (config.selectionMode == SelectModeConfig.SINGLE) {
                        maxSelectNum = Integer.MAX_VALUE;
                    } else {
                        maxSelectNum = config.maxSelectNum;
                    }
                    isEnabledMask = SelectedManager.getSelectCount() == maxSelectNum
                            || PictureMimeType.isHasVideo(media.getMimeType());
                }
            }
        }
        if (isEnabledMask) {
            ivPicture.setColorFilter(maskWhiteColorFilter);
            media.setMaxSelectEnabledMask(true);
        } else {
            media.setMaxSelectEnabledMask(false);
        }
    }

    /**
     * 设置选中缩放动画
     *
     * @param isChecked
     */
    private void selectedMedia(boolean isChecked) {
        if (tvCheck.isSelected() != isChecked) {
            tvCheck.setSelected(isChecked);
        }
        if (config.isDirectReturnSingle) {
            ivPicture.setColorFilter(defaultColorFilter);
        } else {
            ivPicture.setColorFilter(isChecked ? selectColorFilter : defaultColorFilter);
        }
    }

    /**
     * 检查LocalMedia是否被选中
     *
     * @param currentMedia
     * @return
     */
    private boolean isSelected(LocalMedia currentMedia) {
        List<LocalMedia> selectedResult = SelectedManager.getSelectedResult();
        boolean isSelected = selectedResult.contains(currentMedia);
        if (isSelected) {
            LocalMedia compare = currentMedia.getCompareLocalMedia();
            if (compare != null && compare.isEditorImage()) {
                currentMedia.setCutPath(compare.getCutPath());
                currentMedia.setCut(!TextUtils.isEmpty(compare.getCutPath()));
                currentMedia.setEditorImage(compare.isEditorImage());
            }
        }
        return isSelected;
    }

    /**
     * 对选择数量进行编号排序
     */
    private void notifySelectNumberStyle(LocalMedia currentMedia) {
        tvCheck.setText("");
        for (int i = 0; i < SelectedManager.getSelectCount(); i++) {
            LocalMedia media = SelectedManager.getSelectedResult().get(i);
            if (TextUtils.equals(media.getPath(), currentMedia.getPath())
                    || media.getId() == currentMedia.getId()) {
                currentMedia.setNum(media.getNum());
                media.setPosition(currentMedia.getPosition());
                tvCheck.setText(ValueOf.toString(currentMedia.getNum()));
            }
        }
    }

    private PictureImageGridAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(PictureImageGridAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
