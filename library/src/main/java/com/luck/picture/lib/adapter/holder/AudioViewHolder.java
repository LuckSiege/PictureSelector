package com.luck.picture.lib.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.tools.DateUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:59 下午
 * @describe：AudioViewHolder
 */
public class AudioViewHolder extends BaseRecyclerMediaHolder {
    private final TextView tvDuration;

    public AudioViewHolder(@NonNull View itemView, PictureSelectionConfig config) {
        super(itemView, config);
        tvDuration = itemView.findViewById(R.id.tv_duration);
        ImageView ivMask = itemView.findViewById(R.id.iv_mask);
        SelectMainStyle adapterStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
        int drawableLeft = adapterStyle.getAdapterDurationDrawableLeft();
        if (StyleUtils.checkStyleValidity(drawableLeft)) {
            tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft, 0, 0, 0);
        }
        int textSize = adapterStyle.getAdapterDurationTextSize();
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvDuration.setTextSize(textSize);
        }
        int textColor = adapterStyle.getAdapterDurationTextColor();
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvDuration.setTextColor(textColor);
        }

        int shadowBackground = adapterStyle.getAdapterDurationBackgroundResources();
        if (StyleUtils.checkStyleValidity(shadowBackground)) {
            tvDuration.setBackgroundResource(shadowBackground);
        }

        int[] durationShadowGravity = adapterStyle.getAdapterDurationShadowGravity();
        if (StyleUtils.checkArrayValidity(durationShadowGravity)) {
            if (ivMask.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) ivMask.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                for (int i : durationShadowGravity) {
                    ((RelativeLayout.LayoutParams) ivMask.getLayoutParams()).addRule(i);
                }
            }
        }

        int[] durationGravity = adapterStyle.getAdapterDurationGravity();
        if (StyleUtils.checkArrayValidity(durationGravity)) {
            if (ivMask.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) tvDuration.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                for (int i : durationGravity) {
                    ((RelativeLayout.LayoutParams) tvDuration.getLayoutParams()).addRule(i);
                }
            }
        }
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        tvDuration.setText(DateUtils.formatDurationTime(media.getDuration()));
        ivPicture.setImageResource(R.drawable.ps_audio_placeholder);
    }
}
