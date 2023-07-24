package com.luck.picture.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/17 10:46 上午
 * @describe：BottomNavBar
 */
public class BottomNavBar extends RelativeLayout implements View.OnClickListener {
    protected TextView tvPreview;
    protected TextView tvImageEditor;
    private CheckBox originalCheckbox;
    protected SelectorConfig config;

    public BottomNavBar(Context context) {
        super(context);
        init();
    }

    public BottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflateLayout();
        setClickable(true);
        setFocusable(true);
        config = SelectorProviders.getInstance().getSelectorConfig();
        tvPreview = findViewById(R.id.ps_tv_preview);
        tvImageEditor = findViewById(R.id.ps_tv_editor);
        originalCheckbox = findViewById(R.id.cb_original);
        tvPreview.setOnClickListener(this);
        tvImageEditor.setVisibility(GONE);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
        originalCheckbox.setChecked(config.isCheckOriginalImage);
        originalCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                config.isCheckOriginalImage = isChecked;
                originalCheckbox.setChecked(config.isCheckOriginalImage);
                if (bottomNavBarListener != null) {
                    bottomNavBarListener.onCheckOriginalChange();
                    if (isChecked && config.getSelectCount() == 0) {
                        bottomNavBarListener.onFirstCheckOriginalSelectedChange();
                    }
                }
            }
        });
        handleLayoutUI();
    }

    protected void inflateLayout() {
        inflate(getContext(), R.layout.ps_bottom_nav_bar, this);
    }

    protected void handleLayoutUI(){

    }

    public void setBottomNavBarStyle() {
        if (config.isDirectReturnSingle) {
            setVisibility(GONE);
            return;
        }
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        BottomNavBarStyle bottomBarStyle = selectorStyle.getBottomBarStyle();
        if (config.isOriginalControl) {
            originalCheckbox.setVisibility(View.VISIBLE);
            int originalDrawableLeft = bottomBarStyle.getBottomOriginalDrawableLeft();
            if (StyleUtils.checkStyleValidity(originalDrawableLeft)) {
                originalCheckbox.setButtonDrawable(originalDrawableLeft);
            }
            String bottomOriginalText = StyleUtils.checkStyleValidity(bottomBarStyle.getBottomOriginalTextResId())
                    ? getContext().getString(bottomBarStyle.getBottomOriginalTextResId()) : bottomBarStyle.getBottomOriginalText();
            if (StyleUtils.checkTextValidity(bottomOriginalText)) {
                originalCheckbox.setText(bottomOriginalText);
            }
            int originalTextSize = bottomBarStyle.getBottomOriginalTextSize();
            if (StyleUtils.checkSizeValidity(originalTextSize)) {
                originalCheckbox.setTextSize(originalTextSize);
            }
            int originalTextColor = bottomBarStyle.getBottomOriginalTextColor();
            if (StyleUtils.checkStyleValidity(originalTextColor)) {
                originalCheckbox.setTextColor(originalTextColor);
            }
        }

        int narBarHeight = bottomBarStyle.getBottomNarBarHeight();
        if (StyleUtils.checkSizeValidity(narBarHeight)) {
            getLayoutParams().height = narBarHeight;
        } else {
            getLayoutParams().height = DensityUtil.dip2px(getContext(), 46);
        }

        int backgroundColor = bottomBarStyle.getBottomNarBarBackgroundColor();
        if (StyleUtils.checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor);
        }

        int previewNormalTextColor = bottomBarStyle.getBottomPreviewNormalTextColor();
        if (StyleUtils.checkStyleValidity(previewNormalTextColor)) {
            tvPreview.setTextColor(previewNormalTextColor);
        }
        int previewTextSize = bottomBarStyle.getBottomPreviewNormalTextSize();
        if (StyleUtils.checkSizeValidity(previewTextSize)) {
            tvPreview.setTextSize(previewTextSize);
        }
        String bottomPreviewText = StyleUtils.checkStyleValidity(bottomBarStyle.getBottomPreviewNormalTextResId())
                ? getContext().getString(bottomBarStyle.getBottomPreviewNormalTextResId()) : bottomBarStyle.getBottomPreviewNormalText();
        if (StyleUtils.checkTextValidity(bottomPreviewText)) {
            tvPreview.setText(bottomPreviewText);
        }

        String editorText = StyleUtils.checkStyleValidity(bottomBarStyle.getBottomEditorTextResId())
                ? getContext().getString(bottomBarStyle.getBottomEditorTextResId()) : bottomBarStyle.getBottomEditorText();
        if (StyleUtils.checkTextValidity(editorText)) {
            tvImageEditor.setText(editorText);
        }
        int editorTextSize = bottomBarStyle.getBottomEditorTextSize();
        if (StyleUtils.checkSizeValidity(editorTextSize)) {
            tvImageEditor.setTextSize(editorTextSize);
        }
        int editorTextColor = bottomBarStyle.getBottomEditorTextColor();
        if (StyleUtils.checkStyleValidity(editorTextColor)) {
            tvImageEditor.setTextColor(editorTextColor);
        }

        int originalDrawableLeft = bottomBarStyle.getBottomOriginalDrawableLeft();
        if (StyleUtils.checkStyleValidity(originalDrawableLeft)) {
            originalCheckbox.setButtonDrawable(originalDrawableLeft);
        }

        String originalText = StyleUtils.checkStyleValidity(bottomBarStyle.getBottomOriginalTextResId())
                ? getContext().getString(bottomBarStyle.getBottomOriginalTextResId()) : bottomBarStyle.getBottomOriginalText();
        if (StyleUtils.checkTextValidity(originalText)) {
            originalCheckbox.setText(originalText);
        }

        int originalTextSize = bottomBarStyle.getBottomOriginalTextSize();
        if (StyleUtils.checkSizeValidity(originalTextSize)) {
            originalCheckbox.setTextSize(originalTextSize);
        }

        int originalTextColor = bottomBarStyle.getBottomOriginalTextColor();
        if (StyleUtils.checkStyleValidity(originalTextColor)) {
            originalCheckbox.setTextColor(originalTextColor);
        }
    }

    /**
     * 原图选项发生变化
     */
    public void setOriginalCheck() {
        originalCheckbox.setChecked(config.isCheckOriginalImage);
    }

    /**
     * 选择结果发生变化
     */
    public void setSelectedChange() {
        calculateFileTotalSize();
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        BottomNavBarStyle bottomBarStyle = selectorStyle.getBottomBarStyle();
        if (config.getSelectCount() > 0) {
            tvPreview.setEnabled(true);
            int previewSelectTextColor = bottomBarStyle.getBottomPreviewSelectTextColor();
            if (StyleUtils.checkStyleValidity(previewSelectTextColor)) {
                tvPreview.setTextColor(previewSelectTextColor);
            } else {
                tvPreview.setTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_fa632d));
            }
            String previewSelectText = StyleUtils.checkStyleValidity(bottomBarStyle.getBottomPreviewSelectTextResId())
                    ? getContext().getString(bottomBarStyle.getBottomPreviewSelectTextResId()) : bottomBarStyle.getBottomPreviewSelectText();
            if (StyleUtils.checkTextValidity(previewSelectText)) {
                int formatCount = StyleUtils.getFormatCount(previewSelectText);
                if (formatCount == 1) {
                    tvPreview.setText(String.format(previewSelectText, config.getSelectCount()));
                } else if (formatCount == 2) {
                    tvPreview.setText(String.format(previewSelectText, config.getSelectCount(), config.maxSelectNum));
                } else {
                    tvPreview.setText(previewSelectText);
                }
            } else {
                tvPreview.setText(getContext().getString(R.string.ps_preview_num, config.getSelectCount()));
            }
        } else {
            tvPreview.setEnabled(false);
            int previewNormalTextColor = bottomBarStyle.getBottomPreviewNormalTextColor();
            if (StyleUtils.checkStyleValidity(previewNormalTextColor)) {
                tvPreview.setTextColor(previewNormalTextColor);
            } else {
                tvPreview.setTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
            }
            String previewText = StyleUtils.checkStyleValidity(bottomBarStyle.getBottomPreviewNormalTextResId())
                    ? getContext().getString(bottomBarStyle.getBottomPreviewNormalTextResId()) : bottomBarStyle.getBottomPreviewNormalText();
            if (StyleUtils.checkTextValidity(previewText)) {
                tvPreview.setText(previewText);
            } else {
                tvPreview.setText(getContext().getString(R.string.ps_preview));
            }
        }
    }

    /**
     * 计算原图大小
     */
    private void calculateFileTotalSize() {
        if (config.isOriginalControl) {
            long totalSize = 0;
            for (int i = 0; i < config.getSelectCount(); i++) {
                LocalMedia media = config.getSelectedResult().get(i);
                totalSize += media.getSize();
            }
            if (totalSize > 0) {
                String fileSize = PictureFileUtils.formatAccurateUnitFileSize(totalSize);
                originalCheckbox.setText(getContext().getString(R.string.ps_original_image, fileSize));
            } else {
                originalCheckbox.setText(getContext().getString(R.string.ps_default_original_image));
            }
        } else {
            originalCheckbox.setText(getContext().getString(R.string.ps_default_original_image));
        }
    }

    @Override
    public void onClick(View view) {
        if (bottomNavBarListener == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.ps_tv_preview) {
            bottomNavBarListener.onPreview();
        }
    }

    protected OnBottomNavBarListener bottomNavBarListener;

    /**
     * 预览NarBar的功能事件回调
     *
     * @param listener
     */
    public void setOnBottomNavBarListener(OnBottomNavBarListener listener) {
        this.bottomNavBarListener = listener;
    }

    public static class OnBottomNavBarListener {
        /**
         * 预览
         */
        public void onPreview() {

        }

        /**
         * 编辑图片
         */
        public void onEditImage() {

        }

        /**
         * 原图发生变化
         */
        public void onCheckOriginalChange() {

        }

        /**
         * 首次选择原图并加入选择结果
         */
        public void onFirstCheckOriginalSelectedChange(){

        }
    }
}
