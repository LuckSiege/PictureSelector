package com.luck.picture.lib.config;


import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author：luck
 * @date：2021/11/28 3:47 下午
 * @describe：Crop
 */
public class Crop {
    /**
     * <p>
     * 这里都是对应的UCrop库的UCrop类的Key
     * (https://github.com/Yalantis/uCrop/blob/develop/ucrop/src/main/java/com/yalantis/ucrop/UCrop.java)
     * 如果你使用的是PictureSelector自带的裁剪库，此处不要乱改 ！！！
     * </p>
     */
    public static final int REQUEST_EDIT_CROP = 696;

    public static final int REQUEST_CROP = 69;

    public static final int RESULT_CROP_ERROR = 96;


    private static final String EXTRA_PREFIX = "com.yalantis.ucrop";
    public static final String EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio";
    public static final String EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth";
    public static final String EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight";
    public static final String EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX";
    public static final String EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY";
    public static final String EXTRA_ERROR = EXTRA_PREFIX + ".Error";

    /**
     * Retrieve cropped image Uri from the result Intent
     *
     * @param intent crop result intent
     */
    @Nullable
    public static Uri getOutput(@NonNull Intent intent) {
        Uri outputUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        if (outputUri == null) {
            outputUri = intent.getParcelableExtra(CustomIntentKey.EXTRA_OUTPUT_URI);
        }
        return outputUri;
    }

    /**
     * custom extra data
     *
     * @param intent crop result intent
     */
    public static String getOutputCustomExtraData(@NonNull Intent intent) {
        return intent.getStringExtra(CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA);
    }

    /**
     * Retrieve the width of the cropped image
     *
     * @param intent crop result intent
     */
    public static int getOutputImageWidth(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH, -1);
    }

    /**
     * Retrieve the height of the cropped image
     *
     * @param intent crop result intent
     */
    public static int getOutputImageHeight(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, -1);
    }

    /**
     * Retrieve cropped image aspect ratio from the result Intent
     *
     * @param intent crop result intent
     * @return aspect ratio as a floating point value (x:y) - so it will be 1 for 1:1 or 4/3 for 4:3
     */
    public static float getOutputCropAspectRatio(@NonNull Intent intent) {
        return intent.getFloatExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, 0f);
    }

    /**
     * Retrieve the x of the cropped offset x
     *
     * @param intent crop result intent
     */
    public static int getOutputImageOffsetX(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_OFFSET_X, 0);
    }

    /**
     * Retrieve the y of the cropped offset y
     *
     * @param intent crop result intent
     */
    public static int getOutputImageOffsetY(@NonNull Intent intent) {
        return intent.getIntExtra(EXTRA_OUTPUT_OFFSET_Y, 0);
    }

    /**
     * Method retrieves error from the result intent.
     *
     * @param result crop result Intent
     * @return Throwable that could happen while image processing
     */
    @Nullable
    public static Throwable getError(@NonNull Intent result) {
        return (Throwable) result.getSerializableExtra(EXTRA_ERROR);
    }
}
