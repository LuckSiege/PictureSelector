package com.luck.picture.lib.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:54 下午
 * @describe：CameraViewHolder
 */
public class CameraViewHolder extends BaseRecyclerMediaHolder {

    public CameraViewHolder(View itemView) {
        super(itemView);
        TextView tvCamera = itemView.findViewById(R.id.tvCamera);
        selectorConfig = SelectorProviders.getInstance().getSelectorConfig();
        SelectMainStyle adapterStyle = selectorConfig.selectorStyle.getSelectMainStyle();
        int background = adapterStyle.getAdapterCameraBackgroundColor();
        if (StyleUtils.checkStyleValidity(background)) {
            tvCamera.setBackgroundColor(background);
        }
        int drawableTop = adapterStyle.getAdapterCameraDrawableTop();
        if (StyleUtils.checkStyleValidity(drawableTop)) {
            tvCamera.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawableTop, 0, 0);
        }
        String text = StyleUtils.checkStyleValidity(adapterStyle.getAdapterCameraTextResId())
                ? itemView.getContext().getString(adapterStyle.getAdapterCameraTextResId()) : adapterStyle.getAdapterCameraText();
        if (StyleUtils.checkTextValidity(text)) {
            tvCamera.setText(text);
        } else {
            if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) {
                tvCamera.setText(itemView.getContext().getString(R.string.ps_tape));
            }
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
