package com.yalantis.ucrop.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yalantis.ucrop.MultiUCrop;
import com.yalantis.ucrop.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.EventEntity;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.rxbus2.RxBus;
import com.yalantis.ucrop.rxbus2.Subscribe;
import com.yalantis.ucrop.rxbus2.ThreadMode;
import com.yalantis.ucrop.util.LightStatusBarUtils;
import com.yalantis.ucrop.util.ToolbarUtil;
import com.yalantis.ucrop.util.Utils;
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

public class PictureMultiCuttingActivity extends FragmentActivity {
    private RecyclerView recyclerView;
    private PicturePhotoGalleryAdapter adapter;
    private List<LocalMedia> images = new ArrayList<>();
    public static final int DEFAULT_COMPRESS_QUALITY = 100;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private ImageView picture_left_back;
    private TextView tv_right;
    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;
    private SweetAlertDialog dialog;
    private int cutIndex = 0;
    private Context mContext;
    protected int maxSizeX, maxSizeY;
    private int backgroundColor = 0;
    private boolean isCompress;
    private boolean circularCut;
    private int statusBar;
    private boolean isImmersive;

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
    private TextView picture_title;
    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private int copyMode = 0;// 裁剪模式
    private int leftDrawable;
    private int title_color, right_color;

    //EventBus 3.0 回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case 2773:
                // 关闭activity
                dismiss();
                finish();
                overridePendingTransition(0, R.anim.hold);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_multi_cutting);
        mContext = this;
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        images = (List<LocalMedia>) getIntent().getSerializableExtra("previewSelectList");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        cutIndex = intent.getIntExtra("cutIndex", 0);
        copyMode = intent.getIntExtra("copyMode", 0);
        isCompress = intent.getBooleanExtra("isCompress", false);
        isImmersive = intent.getBooleanExtra("isImmersive", false);
        if (isImmersive) {
            LightStatusBarUtils.setLightStatusBar(this, true);
        }
        circularCut = intent.getBooleanExtra("isCircularCut", false);
        leftDrawable = intent.getIntExtra("leftDrawable", R.drawable.picture_back);
        title_color = getIntent().getIntExtra("titleColor", R.color.ucrop_color_widget_background);
        right_color = getIntent().getIntExtra("rightColor", R.color.ucrop_color_widget_background);

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
            case 0:
                options.withAspectRatio(0, 0);
                break;
            case 11:
                options.withAspectRatio(1, 1);
                break;
            case 32:
                options.withAspectRatio(3, 2);
                break;
            case 34:
                options.withAspectRatio(3, 4);
                break;
            case 169:
                options.withAspectRatio(16, 9);
                break;
        }
        // 圆形裁剪
        if (circularCut) {
            options.setCircleDimmedLayer(true);// 是否为椭圆
            options.setShowCropFrame(false);// 外部矩形
            options.setShowCropGrid(false);// 内部网格
            options.withAspectRatio(1, 1);
        }
        options.setLocalMedia(images);
        options.setPosition(cutIndex);
        options.setCompressionQuality(mCompressQuality);
        options.withMaxResultSize(maxSizeX, maxSizeY);
        options.background_color(backgroundColor);
        options.copyMode(copyMode);
        options.setLeftBackDrawable(leftDrawable);
        options.setTitleColor(title_color);
        options.setRightColor(right_color);
        options.setIsCompress(isCompress);
        options.setCircularCut(circularCut);
        options.setStatusBar(statusBar);
        options.setImmersiver(isImmersive);
        uCrop.withOptions(options);
        uCrop.start(PictureMultiCuttingActivity.this);
        overridePendingTransition(R.anim.fade, R.anim.hold);
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
        picture_title = (TextView) findViewById(R.id.picture_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        tv_right.setText(getString(R.string.picture_determine));
        picture_title.setTextColor(title_color);
        tv_right.setTextColor(right_color);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        picture_left_back.setImageResource(leftDrawable);
        picture_left_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isFastDoubleClick()) {
                    cropAndSaveImage();
                }
            }
        });
        mLogoColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_default_logo));
        backgroundColor = intent.getIntExtra("backgroundColor", 0);
        statusBar = getIntent().getIntExtra("statusBar", backgroundColor);
        rl_title.setBackgroundColor(backgroundColor);
        ToolbarUtil.setColorNoTranslucent(this, statusBar);
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
        maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
        maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

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
        tv_right.setEnabled(false);
        showPleaseDialog(getString(R.string.picture_please));
        supportInvalidateOptionsMenu();
        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int imageWidth, int imageHeight) {
                setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), imageWidth, imageHeight);
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                setResultError(t);
                tv_right.setEnabled(true);
            }
        });

    }


    protected void setResultUri(Uri uri, float resultAspectRatio, int imageWidth, int imageHeight) {
        try {
            images.get(cutIndex).setCutPath(uri.getPath());
            images.get(cutIndex).setCut(true);
            cutIndex++;
            if (cutIndex >= images.size()) {
                // 裁剪完成，看是否压缩
                tv_right.setEnabled(false);
                for (LocalMedia media : images) {
                    media.setCut(true);
                }

                EventEntity obj = new EventEntity(2775, images);
                RxBus.getDefault().post(obj);

                // 如果有压缩则先关闭activity，等PictureImageGridActivity 压缩完成在通知我关闭
                if (!isCompress) {
                    finish();
                    overridePendingTransition(0, R.anim.hold);
                }
            } else {
                tv_right.setEnabled(true);
                finish();
                startMultiCopy(images.get(cutIndex).getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isCompress) {
            dismiss();
        }
    }

    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
        dismiss();
    }

    private void showPleaseDialog(String msg) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
    }
}
