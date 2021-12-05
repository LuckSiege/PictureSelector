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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
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
        this.mContext = itemView.getContext();
        this.config = config;
        this.isSelectNumberStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle();
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
        SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
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
        String path = media.getPath();
        if (media.isEditorImage() && media.isCut()) {
            path = media.getCutPath();
        }
        if (PictureMimeType.isHasAudio(media.getMimeType())) {
            ivPicture.setImageResource(R.drawable.ps_trans_1px);
        } else if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadGridImage(ivPicture.getContext(), path, ivPicture);
        }

        selectedMedia(isSelected(media));

        if (isSelectNumberStyle) {
            notifySelectNumberStyle(media);
        }

        if (config.isMaxSelectEnabledMask && config.selectionMode == SelectModeConfig.MULTIPLE) {
            dispatchHandleMask(media);
        }

        tvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCheck.performClick();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isMaxSelectEnabledMask()) {
                    return;
                }
                if (listener != null) {
                    int resultCode = listener.onSelected(tvCheck, position, media);
                    if (resultCode == SelectedManager.INVALID) {
                        return;
                    }
                    selectedMedia(isSelected(media));
                    if (resultCode == SelectedManager.ADD_SUCCESS) {
                        AnimUtils.zoom(ivPicture, config.zoomAnim);
                    } else {
                        AnimUtils.disZoom(ivPicture, config.zoomAnim);
                    }
                }
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isMaxSelectEnabledMask()) {
                    return;
                }
                if (listener != null) {
                    listener.onItemClick(tvCheck, position, media);
                }
            }
        });
    }


    /**
     * 处理到达选择条件后的蒙层效果
     */
    private void dispatchHandleMask(LocalMedia media) {
        boolean isEnabledMask = false;
        if (SelectedManager.getCount() > 0 && !SelectedManager.getSelectedResult().contains(media)) {
            if (config.isWithVideoImage) {
                isEnabledMask = SelectedManager.getCount() == config.maxSelectNum;
            } else {
                if (PictureMimeType.isHasVideo(SelectedManager.getTopResultMimeType())) {
                    isEnabledMask = SelectedManager.getCount() == config.maxVideoSelectNum
                            || PictureMimeType.isHasImage(media.getMimeType());
                } else {
                    isEnabledMask = SelectedManager.getCount() == config.maxSelectNum
                            || PictureMimeType.isHasVideo(media.getMimeType());
                }
            }
        }
        if (isEnabledMask) {
            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ContextCompat.getColor(mContext, R.color.ps_color_half_white),
                    BlendModeCompat.SRC_ATOP);
            ivPicture.setColorFilter(colorFilter);
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

        } else {
            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(isChecked ?
                    ContextCompat.getColor(mContext, R.color.ps_color_80) :
                    ContextCompat.getColor(mContext, R.color.ps_color_20), BlendModeCompat.SRC_ATOP);
            ivPicture.setColorFilter(colorFilter);
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
        for (int i = 0; i < SelectedManager.getCount(); i++) {
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
