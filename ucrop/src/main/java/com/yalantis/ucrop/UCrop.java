package com.yalantis.ucrop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yalantis.ucrop.model.AspectRatio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * Builder class to ease Intent setup.
 */
public class UCrop {
    public static final int REQUEST_CROP = 69;
    public static final int RESULT_ERROR = 96;
    public static final int MIN_SIZE = 10;

    private static final String EXTRA_PREFIX = BuildConfig.LIBRARY_PACKAGE_NAME;
    public static final String EXTRA_CROP_TOTAL_DATA_SOURCE = EXTRA_PREFIX + ".CropTotalDataSource";
    public static final String EXTRA_CROP_INPUT_ORIGINAL = EXTRA_PREFIX + ".CropInputOriginal";

    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";
    public static final String EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio";
    public static final String EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth";
    public static final String EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight";
    public static final String EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX";
    public static final String EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY";
    public static final String EXTRA_ERROR = EXTRA_PREFIX + ".Error";

    public static final String EXTRA_ASPECT_RATIO_X = EXTRA_PREFIX + ".AspectRatioX";
    public static final String EXTRA_ASPECT_RATIO_Y = EXTRA_PREFIX + ".AspectRatioY";

    public static final String EXTRA_MAX_SIZE_X = EXTRA_PREFIX + ".MaxSizeX";
    public static final String EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY";

    private Intent mCropIntent;
    private Bundle mCropOptionsBundle;

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     * @param totalSource crop data source for list
     */
    public static UCrop of(@NonNull Uri source, @NonNull Uri destination, ArrayList<String> totalSource) {
        if (totalSource == null || totalSource.size() <= 0) {
            throw new IllegalArgumentException("Missing required parameters, count cannot be less than 1");
        }
        if (totalSource.size() == 1) {
            return new UCrop(source, destination);
        }
        return new UCrop(source, destination, totalSource);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static <T> UCrop of(@NonNull Uri source, @NonNull Uri destination) {
        return new UCrop(source, destination);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    private UCrop(@NonNull Uri source, @NonNull Uri destination) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     * @param totalSource crop total data
     */
    private UCrop(@NonNull Uri source, @NonNull Uri destination, ArrayList<String> totalSource) {
        mCropIntent = new Intent();
        mCropOptionsBundle = new Bundle();
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source);
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
        mCropOptionsBundle.putStringArrayList(EXTRA_CROP_TOTAL_DATA_SOURCE, totalSource);
    }

    /**
     * Set Multiple Crop gallery Preview Image Engine
     *
     * @param engine
     * @return
     */
    public void setImageEngine(UCropImageEngine engine) {
        ArrayList<String> dataSource = mCropOptionsBundle.getStringArrayList(EXTRA_CROP_TOTAL_DATA_SOURCE);
        boolean isUseBitmap = mCropOptionsBundle.getBoolean(UCrop.Options.EXTRA_CROP_CUSTOM_LOADER_BITMAP, false);
        if ((dataSource != null && dataSource.size() > 1) || isUseBitmap) {
            if (engine == null) {
                throw new NullPointerException("Missing ImageEngine,please implement UCrop.setImageEngine");
            }
        }
        UCropDevelopConfig.imageEngine = engine;
    }

    /**
     * Set an aspect ratio for crop bounds.
     * User won't see the menu with other ratios options.
     *
     * @param x aspect ratio X
     * @param y aspect ratio Y
     */
    public UCrop withAspectRatio(float x, float y) {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
        return this;
    }

    /**
     * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
     * User won't see the menu with other ratios options.
     */
    public UCrop useSourceImageAspectRatio() {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
        return this;
    }

    /**
     * Set maximum size for result cropped image. Maximum size cannot be less then {@value MIN_SIZE}
     *
     * @param width  max cropped image width
     * @param height max cropped image height
     */
    public UCrop withMaxResultSize(@IntRange(from = MIN_SIZE) int width, @IntRange(from = MIN_SIZE) int height) {
        if (width < MIN_SIZE) {
            width = MIN_SIZE;
        }

        if (height < MIN_SIZE) {
            height = MIN_SIZE;
        }

        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_X, width);
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_Y, height);
        return this;
    }

    public UCrop withOptions(@NonNull Options options) {
        mCropOptionsBundle.putAll(options.getOptionBundle());
        return this;
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(@NonNull Activity activity) {
        start(activity, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public void startEdit(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Get Intent to start {@link UCropActivity}
     *
     * @return Intent for {@link UCropActivity}
     */
    public Intent getIntent(@NonNull Context context) {
        ArrayList<String> dataSource = mCropOptionsBundle.getStringArrayList(EXTRA_CROP_TOTAL_DATA_SOURCE);
        if (dataSource != null && dataSource.size() > 1) {
            mCropIntent.setClass(context, UCropMultipleActivity.class);
        } else {
            mCropIntent.setClass(context, UCropActivity.class);
        }
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }

    /**
     * Get Fragment {@link UCropFragment}
     *
     * @return Fragment of {@link UCropFragment}
     */
    public UCropFragment getFragment() {
        return UCropFragment.newInstance(mCropOptionsBundle);
    }

    public UCropFragment getFragment(Bundle bundle) {
        mCropOptionsBundle = bundle;
        return getFragment();
    }

    /**
     * Retrieve cropped image Uri from the result Intent
     *
     * @param intent crop result intent
     */
    @Nullable
    public static Uri getOutput(@NonNull Intent intent) {
        return intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
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

    /**
     * Class that helps to setup advanced configs that are not commonly used.
     * Use it with method {@link #withOptions(Options)}
     */
    public static class Options {

        public static final String EXTRA_COMPRESSION_FORMAT_NAME = EXTRA_PREFIX + ".CompressionFormatName";
        public static final String EXTRA_COMPRESSION_QUALITY = EXTRA_PREFIX + ".CompressionQuality";

        public static final String EXTRA_CROP_OUTPUT_DIR = EXTRA_PREFIX + ".CropOutputDir";

        public static final String EXTRA_CROP_OUTPUT_FILE_NAME = EXTRA_PREFIX + ".CropOutputFileName";

        public static final String EXTRA_CROP_FORBID_GIF_WEBP = EXTRA_PREFIX + ".ForbidCropGifWebp";

        public static final String EXTRA_CROP_FORBID_SKIP = EXTRA_PREFIX + ".ForbidSkipCrop";

        public static final String EXTRA_DARK_STATUS_BAR_BLACK = EXTRA_PREFIX + ".isDarkStatusBarBlack";

        public static final String EXTRA_DRAG_IMAGES = EXTRA_PREFIX + ".isDragImages";

        public static final String EXTRA_CROP_CUSTOM_LOADER_BITMAP = EXTRA_PREFIX + ".CustomLoaderCropBitmap";

        public static final String EXTRA_CROP_DRAG_CENTER = EXTRA_PREFIX + ".DragSmoothToCenter";

        public static final String EXTRA_ALLOWED_GESTURES = EXTRA_PREFIX + ".AllowedGestures";

        public static final String EXTRA_MAX_BITMAP_SIZE = EXTRA_PREFIX + ".MaxBitmapSize";
        public static final String EXTRA_MAX_SCALE_MULTIPLIER = EXTRA_PREFIX + ".MaxScaleMultiplier";
        public static final String EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = EXTRA_PREFIX + ".ImageToCropBoundsAnimDuration";

        public static final String EXTRA_DIMMED_LAYER_COLOR = EXTRA_PREFIX + ".DimmedLayerColor";
        public static final String EXTRA_CIRCLE_STROKE_COLOR = EXTRA_PREFIX + ".CircleStrokeColor";
        public static final String EXTRA_CIRCLE_DIMMED_LAYER = EXTRA_PREFIX + ".CircleDimmedLayer";

        public static final String EXTRA_SHOW_CROP_FRAME = EXTRA_PREFIX + ".ShowCropFrame";
        public static final String EXTRA_CROP_FRAME_COLOR = EXTRA_PREFIX + ".CropFrameColor";
        public static final String EXTRA_CROP_FRAME_STROKE_WIDTH = EXTRA_PREFIX + ".CropFrameStrokeWidth";

        public static final String EXTRA_SHOW_CROP_GRID = EXTRA_PREFIX + ".ShowCropGrid";

        public static final String EXTRA_CROP_GRID_ROW_COUNT = EXTRA_PREFIX + ".CropGridRowCount";
        public static final String EXTRA_CROP_GRID_COLUMN_COUNT = EXTRA_PREFIX + ".CropGridColumnCount";
        public static final String EXTRA_CROP_GRID_COLOR = EXTRA_PREFIX + ".CropGridColor";
        public static final String EXTRA_CROP_GRID_STROKE_WIDTH = EXTRA_PREFIX + ".CropGridStrokeWidth";
        public static final String EXTRA_CIRCLE_STROKE_WIDTH_LAYER = EXTRA_PREFIX + ".CircleStrokeWidth";
        public static final String EXTRA_GALLERY_BAR_BACKGROUND = EXTRA_PREFIX + ".GalleryBarBackground";

        public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor";
        public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor";
        public static final String EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE = EXTRA_PREFIX + ".UcropColorControlsWidgetActive";

        public static final String EXTRA_UCROP_WIDGET_COLOR_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarWidgetColor";
        public static final String EXTRA_UCROP_TITLE_TEXT_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleText";
        public static final String EXTRA_UCROP_TITLE_TEXT_SIZE_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleTextSize";
        public static final String EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCancelDrawable";
        public static final String EXTRA_UCROP_WIDGET_CROP_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCropDrawable";

        public static final String EXTRA_UCROP_LOGO_COLOR = EXTRA_PREFIX + ".UcropLogoColor";

        public static final String EXTRA_HIDE_BOTTOM_CONTROLS = EXTRA_PREFIX + ".HideBottomControls";
        public static final String EXTRA_FREE_STYLE_CROP = EXTRA_PREFIX + ".FreeStyleCrop";

        public static final String EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT = EXTRA_PREFIX + ".AspectRatioSelectedByDefault";
        public static final String EXTRA_ASPECT_RATIO_OPTIONS = EXTRA_PREFIX + ".AspectRatioOptions";
        public static final String EXTRA_SKIP_CROP_MIME_TYPE = EXTRA_PREFIX + ".SkipCropMimeType";

        public static final String EXTRA_MULTIPLE_ASPECT_RATIO = EXTRA_PREFIX + ".MultipleAspectRatio";

        public static final String EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR = EXTRA_PREFIX + ".UcropRootViewBackgroundColor";


        private final Bundle mOptionBundle;

        public Options() {
            mOptionBundle = new Bundle();
        }

        @NonNull
        public Bundle getOptionBundle() {
            return mOptionBundle;
        }

        /**
         * Set one of {@link android.graphics.Bitmap.CompressFormat} that will be used to save resulting Bitmap.
         */
        public void setCompressionFormat(@NonNull Bitmap.CompressFormat format) {
            mOptionBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name());
        }

        /**
         * Set one of {@link context.getExternalFilesDir()} The path that will be used to save
         * when clipping multiple drawings
         * Valid when multiple pictures are cropped
         */
        public void setCropOutputPathDir(@NonNull String dir) {
            mOptionBundle.putString(EXTRA_CROP_OUTPUT_DIR, dir);
        }

        /**
         * File name after clipping output
         * Valid when multiple pictures are cropped
         * <p>
         * When multiple pictures are cropped, the front will automatically keep up with the timestamp
         * </p>
         */
        public void setCropOutputFileName(@NonNull String fileName) {
            mOptionBundle.putString(EXTRA_CROP_OUTPUT_FILE_NAME, fileName);
        }

        /**
         * @param isForbidSkipCrop - It is forbidden to skip when cutting multiple drawings
         */
        public void isForbidSkipMultipleCrop(boolean isForbidSkipCrop) {
            mOptionBundle.putBoolean(EXTRA_CROP_FORBID_SKIP, isForbidSkipCrop);
        }

        /**
         * Get the bitmap of the uCrop resource using the custom loader
         *
         * @param isUseBitmap
         */
        public void isUseCustomLoaderBitmap(boolean isUseBitmap) {
            mOptionBundle.putBoolean(EXTRA_CROP_CUSTOM_LOADER_BITMAP, isUseBitmap);
        }

        /**
         * isDragCenter
         *
         * @param isDragCenter Crop and drag automatically center
         */
        public void isCropDragSmoothToCenter(boolean isDragCenter) {
            mOptionBundle.putBoolean(EXTRA_CROP_DRAG_CENTER, isDragCenter);
        }

        /**
         * @param isForbidCropGifWebp - Do you need to support clipping dynamic graphs gif or webp
         */
        public void isForbidCropGifWebp(boolean isForbidCropGifWebp) {
            mOptionBundle.putBoolean(EXTRA_CROP_FORBID_GIF_WEBP, isForbidCropGifWebp);
        }

        /**
         * Set compression quality [0-100] that will be used to save resulting Bitmap.
         */
        public void setCompressionQuality(@IntRange(from = 0) int compressQuality) {
            mOptionBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality);
        }

        /**
         * Choose what set of gestures will be enabled on each tab - if any.
         */
        public void setAllowedGestures(@UCropActivity.GestureTypes int tabScale,
                                       @UCropActivity.GestureTypes int tabRotate,
                                       @UCropActivity.GestureTypes int tabAspectRatio) {
            mOptionBundle.putIntArray(EXTRA_ALLOWED_GESTURES, new int[]{tabScale, tabRotate, tabAspectRatio});
        }

        /**
         * This method sets multiplier that is used to calculate max image scale from min image scale.
         *
         * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
         */
        public void setMaxScaleMultiplier(@FloatRange(from = 1.0, fromInclusive = false) float maxScaleMultiplier) {
            mOptionBundle.putFloat(EXTRA_MAX_SCALE_MULTIPLIER, maxScaleMultiplier);
        }

        /**
         * This method sets animation duration for image to wrap the crop bounds
         *
         * @param durationMillis - duration in milliseconds
         */
        public void setImageToCropBoundsAnimDuration(@IntRange(from = MIN_SIZE) int durationMillis) {
            mOptionBundle.putInt(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, durationMillis);
        }

        /**
         * Setter for max size for both width and height of bitmap that will be decoded from an input Uri and used in the view.
         *
         * @param maxBitmapSize - size in pixels
         */
        public void setMaxBitmapSize(@IntRange(from = MIN_SIZE) int maxBitmapSize) {
            mOptionBundle.putInt(EXTRA_MAX_BITMAP_SIZE, maxBitmapSize);
        }

        /**
         * @param color - desired color of dimmed area around the crop bounds
         */
        public void setDimmedLayerColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_DIMMED_LAYER_COLOR, color);
        }

        /**
         * @param color - desired color of dimmed stroke area around the crop bounds
         */
        public void setCircleStrokeColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CIRCLE_STROKE_COLOR, color);
        }

        /**
         * @param isCircle - set it to true if you want dimmed layer to have an circle inside
         */
        public void setCircleDimmedLayer(boolean isCircle) {
            mOptionBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER, isCircle);
        }

        /**
         * @param show - set to true if you want to see a crop frame rectangle on top of an image
         */
        public void setShowCropFrame(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_FRAME, show);
        }

        /**
         * @param color - desired color of crop frame
         */
        public void setCropFrameColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_COLOR, color);
        }

        /**
         * @param width - desired width of crop frame line in pixels
         */
        public void setCropFrameStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_STROKE_WIDTH, width);
        }

        /**
         * @param show - set to true if you want to see a crop grid/guidelines on top of an image
         */
        public void setShowCropGrid(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_GRID, show);
        }

        /**
         * @param count - crop grid rows count.
         */
        public void setCropGridRowCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_ROW_COUNT, count);
        }

        /**
         * @param count - crop grid columns count.
         */
        public void setCropGridColumnCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLUMN_COUNT, count);
        }

        /**
         * @param color - desired color of crop grid/guidelines
         */
        public void setCropGridColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLOR, color);
        }

        /**
         * @param width - desired width of crop grid lines in pixels
         */
        public void setCropGridStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_STROKE_WIDTH, width);
        }

        /**
         * @param width Set the circular clipping border
         */
        public void setCircleStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CIRCLE_STROKE_WIDTH_LAYER, width);
        }

        /**
         * @param color - desired resolved color of the gallery bar background
         */
        public void setCropGalleryBarBackgroundResources(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_GALLERY_BAR_BACKGROUND, color);
        }

        /**
         * @param color - desired resolved color of the toolbar
         */
        public void setToolbarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color);
        }

        /**
         * @param color - desired resolved color of the statusbar
         */
        public void setStatusBarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color);
        }


        /**
         * @param Is the font of the status bar black
         */
        public void isDarkStatusBarBlack(boolean isDarkStatusBarBlack) {
            mOptionBundle.putBoolean(EXTRA_DARK_STATUS_BAR_BLACK, isDarkStatusBarBlack);
        }

        /**
         * Can I drag and drop images when crop
         *
         * @param isDragImages
         */
        public void isDragCropImages(boolean isDragImages) {
            mOptionBundle.putBoolean(EXTRA_DRAG_IMAGES, isDragImages);
        }

        /**
         * @param color - desired resolved color of the active and selected widget and progress wheel middle line (default is white)
         */
        public void setActiveControlsWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, color);
        }

        /**
         * @param color - desired resolved color of Toolbar text and buttons (default is darker orange)
         */
        public void setToolbarWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, color);
        }

        /**
         * @param text - desired text for Toolbar title
         */
        public void setToolbarTitle(@Nullable String text) {
            mOptionBundle.putString(EXTRA_UCROP_TITLE_TEXT_TOOLBAR, text);
        }

        /**
         * @param textSize - desired text for Toolbar title
         */
        public void setToolbarTitleSize(int textSize) {
            if (textSize > 0) {
                mOptionBundle.putInt(EXTRA_UCROP_TITLE_TEXT_SIZE_TOOLBAR, textSize);
            }
        }

        /**
         * @param drawable - desired drawable for the Toolbar left cancel icon
         */
        public void setToolbarCancelDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, drawable);
        }

        /**
         * @param drawable - desired drawable for the Toolbar right crop icon
         */
        public void setToolbarCropDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, drawable);
        }

        /**
         * @param color - desired resolved color of logo fill (default is darker grey)
         */
        public void setLogoColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_LOGO_COLOR, color);
        }

        /**
         * @param hide - set to true to hide the bottom controls (shown by default)
         */
        public void setHideBottomControls(boolean hide) {
            mOptionBundle.putBoolean(EXTRA_HIDE_BOTTOM_CONTROLS, hide);
        }

        /**
         * @param enabled - set to true to let user resize crop bounds (disabled by default)
         */
        public void setFreeStyleCropEnabled(boolean enabled) {
            mOptionBundle.putBoolean(EXTRA_FREE_STYLE_CROP, enabled);
        }

        /**
         * Pass an ordered list of desired aspect ratios that should be available for a user.
         *
         * @param selectedByDefault - index of aspect ratio option that is selected by default (starts with 0).
         * @param aspectRatio       - list of aspect ratio options that are available to user
         */
        public void setAspectRatioOptions(int selectedByDefault, AspectRatio... aspectRatio) {
            if (selectedByDefault >= aspectRatio.length) {
                throw new IllegalArgumentException(String.format(Locale.US,
                        "Index [selectedByDefault = %d] (0-based) cannot be higher or equal than aspect ratio options count [count = %d].",
                        selectedByDefault, aspectRatio.length));
            }
            mOptionBundle.putInt(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, selectedByDefault);
            mOptionBundle.putParcelableArrayList(EXTRA_ASPECT_RATIO_OPTIONS, new ArrayList<Parcelable>(Arrays.asList(aspectRatio)));
        }

        /**
         * Skip crop mimeType
         *
         * @param mimeTypes Use example {@link { image/gift or image/webp ... }}
         * @return
         */
        public void setSkipCropMimeType(String... mimeTypes) {
            if (mimeTypes != null && mimeTypes.length > 0) {
                mOptionBundle.putStringArrayList(EXTRA_SKIP_CROP_MIME_TYPE, new ArrayList<>(Arrays.asList(mimeTypes)));
            }
        }

        /**
         * @param color - desired background color that should be applied to the root view
         */
        public void setRootViewBackgroundColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, color);
        }

        /**
         * Set an aspect ratio for crop bounds.
         * User won't see the menu with other ratios options.
         *
         * @param x aspect ratio X
         * @param y aspect ratio Y
         */
        public void withAspectRatio(float x, float y) {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
        }

        /**
         * The corresponding crop scale of each graph in multi graph crop
         *
         * @param aspectRatio - The corresponding crop scale of each graph in multi graph crop
         */
        public void setMultipleCropAspectRatio(AspectRatio... aspectRatio) {
            float aspectRatioX = mOptionBundle.getFloat(EXTRA_ASPECT_RATIO_X, 0);
            float aspectRatioY = mOptionBundle.getFloat(EXTRA_ASPECT_RATIO_Y, 0);
            if (aspectRatio.length > 0 && aspectRatioX <= 0 && aspectRatioY <= 0) {
                withAspectRatio(aspectRatio[0].getAspectRatioX(), aspectRatio[0].getAspectRatioY());
            }
            mOptionBundle.putParcelableArrayList(EXTRA_MULTIPLE_ASPECT_RATIO, new ArrayList<Parcelable>(Arrays.asList(aspectRatio)));
        }


        /**
         * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
         * User won't see the menu with other ratios options.
         */
        public void useSourceImageAspectRatio() {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
        }

        /**
         * Set maximum size for result cropped image.
         *
         * @param width  max cropped image width
         * @param height max cropped image height
         */
        public void withMaxResultSize(@IntRange(from = MIN_SIZE) int width, @IntRange(from = MIN_SIZE) int height) {
            mOptionBundle.putInt(EXTRA_MAX_SIZE_X, width);
            mOptionBundle.putInt(EXTRA_MAX_SIZE_Y, height);
        }

    }

}
