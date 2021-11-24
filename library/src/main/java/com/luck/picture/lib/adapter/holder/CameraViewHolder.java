package com.luck.picture.lib.adapter.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.style.MediaAdapterStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:54 下午
 * @describe：CameraViewHolder
 */
public class CameraViewHolder extends BaseRecyclerMediaHolder {

    public CameraViewHolder(@NonNull View itemView) {
        super(itemView);
        TextView tvCamera = itemView.findViewById(R.id.tvCamera);
        MediaAdapterStyle adapterStyle = PictureSelectionConfig.selectorStyle.getAdapterStyle();
        int background = adapterStyle.getAdapterCameraBackground();
        if (StyleUtils.checkStyleValidity(background)) {
            tvCamera.setBackgroundColor(background);
        }
        int drawableTop = adapterStyle.getAdapterCameraDrawableTop();
        if (StyleUtils.checkStyleValidity(drawableTop)) {
            tvCamera.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawableTop, 0, 0);
        }
        String text = adapterStyle.getAdapterCameraText();
        if (StyleUtils.checkTextValidity(text)) {
            tvCamera.setText(text);
        }
        int textSize = adapterStyle.getAdapterCameraTextSize();
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvCamera.setTextSize(textSize);
        }
        int textColor = adapterStyle.getAdapterCameraTextColor();
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvCamera.setTextColor(textColor);
        }
    }
}
