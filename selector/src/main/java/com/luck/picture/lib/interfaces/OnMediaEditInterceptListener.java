package com.luck.picture.lib.interfaces;

import androidx.fragment.app.Fragment;

import com.luck.picture.lib.config.CustomField;
import com.luck.picture.lib.entity.LocalMedia;

/**
 * @author：luck
 * @date：2021/11/27 5:44 下午
 * @describe：OnMediaEditInterceptListener
 */
public interface OnMediaEditInterceptListener {
    /**
     * Custom crop image engine
     * <p>
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the {@link LocalMedia} object;
     *
     * </p>
     *
     * <p>
     * 1、LocalMedia media = new LocalMedia();
     * media.setEditorImage(true);
     * media.setCut(true);
     * media.setCutPath("Your edit path"); or media.setCustomData("Your edit path");
     * or
     * media.setCustomData("Your custom data");
     * </p>
     * <p>
     * If you implement your own Editing function function, you need to assign the following values in
     * Intent.putExtra() {@link CustomField.EXTRA_OUT_PUT_PATH}
     * Intent.putExtra() {@link CustomField.EXTRA_IMAGE_WIDTH}
     * Intent.putExtra() {@link CustomField.EXTRA_IMAGE_HEIGHT}
     * ... more {@link CustomField}
     * <p>
     * If you have customized additional data, please put it in Intent.putExtra()
     * {@link CustomField.EXTRA_CUSTOM_EXTRA_DATA}
     * </p>
     *
     * @param fragment
     * @param currentLocalMedia current edit LocalMedia
     * @param requestCode       Activity or fragment result code
     */
    void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode);
}
