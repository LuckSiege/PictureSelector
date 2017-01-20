package com.yalantis.ucrop.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yalantis.ucrop.MultiUCrop;
import com.yalantis.ucrop.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.adapter.PicturePhotoGalleryAdapter;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.compress.CompressConfig;
import com.yalantis.ucrop.compress.CompressImageOptions;
import com.yalantis.ucrop.compress.CompressInterface;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.util.FunctionConfig;
import com.yalantis.ucrop.util.PictureConfig;
import com.yalantis.ucrop.util.ToolbarUtil;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class PictureMultiCuttingActivity extends PictureBaseActivity {
    private RecyclerView recyclerView;
    private PicturePhotoGalleryAdapter adapter;
    private List<LocalMedia> images = new ArrayList<>();
    public static final int DEFAULT_COMPRESS_QUALITY = 100;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private ImageButton left_back;
    private TextView tv_right;
    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;
    private SweetAlertDialog dialog;
    private int cutIndex = 0;

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private static final String TAG = "UCropActivity";

    private int mLogoColor;

    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private RelativeLayout rl_title;
    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_multi_cutting);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        cutIndex = intent.getIntExtra(FunctionConfig.EXTRA_CUT_INDEX, 0);
        for (LocalMedia media : images) {
            media.setCut(false);
        }
        images.get(cutIndex).setCut(true);// 默认装载第一张图片
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new PicturePhotoGalleryAdapter(mContext, images);
        recyclerView.setAdapter(adapter);
        // 预览图 一页5个,裁剪到第6个的时候滚动到最新位置，不然预览图片看不到
        if (cutIndex >= 5) {
            recyclerView.scrollToPosition(cutIndex);
        }
        setupViews(intent);
        setImageData(intent);
    }

    /**
     * 多图裁剪
     */
    protected void startMultiCopy(String path) {
        // 去裁剪
        MultiUCrop uCrop = MultiUCrop.of(Uri.parse(path), Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")));
        MultiUCrop.Options options = new MultiUCrop.Options();
        switch (copyMode) {
            case FunctionConfig.COPY_MODEL_DEFAULT:
                options.withAspectRatio(0, 0);
                break;
            case FunctionConfig.COPY_MODEL_1_1:
                options.withAspectRatio(1, 1);
                break;
            case FunctionConfig.COPY_MODEL_3_2:
                options.withAspectRatio(3, 2);
                break;
            case FunctionConfig.COPY_MODEL_3_4:
                options.withAspectRatio(3, 4);
                break;
            case FunctionConfig.COPY_MODEL_16_9:
                options.withAspectRatio(16, 9);
                break;
        }
        options.setLocalMedia(images);
        options.setPosition(cutIndex);
        options.setCompressionQuality(compressQuality);
        options.withMaxResultSize(cropW, cropH);
        options.background_color(backgroundColor);
        options.putLocalMedias(config);
        uCrop.withOptions(options);
        uCrop.start(PictureMultiCuttingActivity.this);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }


    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private void setSelectImageData(String path) {
        Uri inputUri = Uri.parse(path);
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg"));
        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private void setImageData(@NonNull Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        Uri outputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
        processOptions(intent);

        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    private void setupViews(@NonNull Intent intent) {
        tv_right = (TextView) findViewById(R.id.tv_right);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        tv_right.setText("确定");
        left_back = (ImageButton) findViewById(R.id.left_back);
        left_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropAndSaveImage();
            }
        });
        mLogoColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_default_logo));
        int backgroundColor = intent.getIntExtra(FunctionConfig.BACKGROUND_COLOR, 0);
        rl_title.setBackgroundColor(backgroundColor);
        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        initiateRootViews();
    }

    /**
     * This method extracts {@link com.yalantis.ucrop.UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {
        // Bitmap compression options
        String compressionFormatName = intent.getStringExtra(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }
        mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

        mCompressQuality = intent.getIntExtra(UCrop.Options.EXTRA_COMPRESSION_QUALITY, PictureMultiCuttingActivity.DEFAULT_COMPRESS_QUALITY);


        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(UCrop.Options.EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE));

        mOverlayView.setDimmedColor(intent.getIntExtra(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(R.color.ucrop_color_default_dimmed)));
        mOverlayView.setCircleDimmedLayer(intent.getBooleanExtra(UCrop.Options.EXTRA_CIRCLE_DIMMED_LAYER, OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options
        float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
        float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioX > 0 && aspectRatioY > 0) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size()) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioX() /
                    aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioY());
        } else {
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }
    }

    private void initiateRootViews() {
        mUCropView = (UCropView) findViewById(R.id.ucrop);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);

        ((ImageView) findViewById(R.id.image_view_logo)).setColorFilter(mLogoColor, PorterDuff.Mode.SRC_ATOP);

    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            finish();
        }

    };


    protected void cropAndSaveImage() {
        showDialog("处理中...");
        supportInvalidateOptionsMenu();
        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int imageWidth, int imageHeight) {
                setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), imageWidth, imageHeight);
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                setResultError(t);
            }
        });

    }


    protected void setResultUri(Uri uri, float resultAspectRatio, int imageWidth, int imageHeight) {
        try {
            for (LocalMedia media : images) {
                media.setCut(false);
            }

            images.get(cutIndex).setCut(true);
            cutIndex++;
            if (cutIndex >= images.size()) {
                // 裁剪完成，看是否压缩
                if (isCompress) {
                    dismiss();
                    compressImage(images);
                } else {
                    onResult(images);
                }
            } else {
                finish();
                startMultiCopy(images.get(cutIndex).getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
    }

    /**
     * 处理图片压缩
     */
    private void compressImage(List<LocalMedia> result) {
        showDialog("处理中...");
        CompressConfig compress_config = CompressConfig.ofDefaultConfig();
        compress_config.enablePixelCompress(config.isEnablePixelCompress());
        compress_config.enableQualityCompress(config.isEnableQualityCompress());
        CompressImageOptions.compress(this, compress_config, result, new CompressInterface.CompressListener() {
            @Override
            public void onCompressSuccess(List<LocalMedia> images) {
                // 压缩成功回调
                onResult(images);
                dismiss();
            }

            @Override
            public void onCompressError(List<LocalMedia> images, String msg) {
                // 压缩失败回调 返回原图
                onResult(images);
                dismiss();
            }
        }).compress();
    }

    public void onResult(List<LocalMedia> images) {
        // 因为这里是单一实例的结果集，重新用变量接收一下在返回，不然会产生结果集被单一实例清空的问题
        List<LocalMedia> result = new ArrayList<>();
        for (LocalMedia media : images) {
            result.add(media);
        }
        PictureConfig.OnSelectResultCallback resultCallback = PictureConfig.getPictureConfig().getResultCallback();
        if (resultCallback != null) {
            resultCallback.onSelectSuccess(result);
            // 释放静态变量
            PictureConfig.getPictureConfig().resultCallback = null;
            PictureConfig.pictureConfig = null;
        }
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
        sendBroadcast("app.activity.finish");
    }


    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    private void showDialog(String msg) {
        dialog = new SweetAlertDialog(PictureMultiCuttingActivity.this);
        dialog.setTitleText(msg);
        dialog.show();
    }

    private void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }
}
