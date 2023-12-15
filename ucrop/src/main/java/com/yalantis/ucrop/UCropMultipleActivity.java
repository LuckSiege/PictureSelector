package com.yalantis.ucrop;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yalantis.ucrop.decoration.GridSpacingItemDecoration;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.model.CustomIntentKey;
import com.yalantis.ucrop.statusbar.ImmersiveManager;
import com.yalantis.ucrop.util.DensityUtil;
import com.yalantis.ucrop.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author：luck
 * @date：2021/11/28 7:59 下午
 * @describe：UCropMultipleActivity
 */
public class UCropMultipleActivity extends AppCompatActivity implements UCropFragmentCallback {
    private String mToolbarTitle;
    private int mToolbarTitleSize;
    // Enables dynamic coloring
    private int mToolbarColor;
    private int mStatusBarColor;
    @DrawableRes
    private int mToolbarCancelDrawable;
    @DrawableRes
    private int mToolbarCropDrawable;
    private int mToolbarWidgetColor;
    private boolean mShowLoader;
    private final List<UCropFragment> fragments = new ArrayList<>();
    private UCropFragment uCropCurrentFragment;
    private int currentFragmentPosition;
    private ArrayList<String> uCropSupportList;
    private ArrayList<String> uCropNotSupportList;
    private final LinkedHashMap<String, JSONObject> uCropTotalQueue = new LinkedHashMap<>();
    private String outputCropFileName;
    private UCropGalleryAdapter galleryAdapter;
    private boolean isForbidCropGifWebp;
    private boolean isSkipCropForbid;
    private ArrayList<AspectRatio> aspectRatioList;
    private final HashSet<String> filterSet = new HashSet<>();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersive();
        setContentView(R.layout.ucrop_activity_multiple);
        setupViews(getIntent());
        initCropFragments(getIntent());
    }

    private void immersive() {
        Intent intent = getIntent();
        boolean isDarkStatusBarBlack = intent.getBooleanExtra(UCrop.Options.EXTRA_DARK_STATUS_BAR_BLACK, false);
        mStatusBarColor = intent.getIntExtra(UCrop.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_statusbar));
        ImmersiveManager.immersiveAboveAPI23(this, mStatusBarColor, mStatusBarColor, isDarkStatusBarBlack);
    }

    private void initCropFragments(Intent intent) {
        isSkipCropForbid = intent.getBooleanExtra(UCrop.Options.EXTRA_CROP_FORBID_SKIP, false);
        ArrayList<String> totalCropData = intent.getStringArrayListExtra(UCrop.EXTRA_CROP_TOTAL_DATA_SOURCE);
        if (totalCropData == null || totalCropData.size() == 0) {
            throw new IllegalArgumentException("Missing required parameters, count cannot be less than 1");
        }
        uCropSupportList = new ArrayList<>();
        uCropNotSupportList = new ArrayList<>();
        for (int i = 0; i < totalCropData.size(); i++) {
            String path = totalCropData.get(i);
            uCropTotalQueue.put(path, new JSONObject());
            String realPath = FileUtils.isContent(path) ? FileUtils.getPath(this, Uri.parse(path)) : path;
            String mimeType = getPathToMimeType(path);
            if (FileUtils.isUrlHasVideo(realPath) || FileUtils.isHasVideo(mimeType) || FileUtils.isHasAudio(mimeType)) {
                // not crop type
                uCropNotSupportList.add(path);
            } else {
                uCropSupportList.add(path);
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    continue;
                }
                Uri inputUri = FileUtils.isContent(path) || FileUtils.isHasHttp(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
                String postfix = FileUtils.getPostfixDefaultJPEG(UCropMultipleActivity.this,
                        isForbidCropGifWebp, inputUri);
                String fileName = TextUtils.isEmpty(outputCropFileName) ? FileUtils.getCreateFileName("CROP_" + (i + 1)) + postfix
                        : (i + 1) + FileUtils.getCreateFileName() + "_" + outputCropFileName;
                Uri destinationUri = Uri.fromFile(new File(getSandboxPathDir(), fileName));
                extras.putParcelable(UCrop.EXTRA_INPUT_URI, inputUri);
                extras.putParcelable(UCrop.EXTRA_OUTPUT_URI, destinationUri);
                AspectRatio aspectRatio = aspectRatioList != null && aspectRatioList.size() > i ? aspectRatioList.get(i) : null;
                if (aspectRatio != null) {
                    extras.putFloat(UCrop.EXTRA_ASPECT_RATIO_X, aspectRatio.getAspectRatioX());
                    extras.putFloat(UCrop.EXTRA_ASPECT_RATIO_Y, aspectRatio.getAspectRatioY());
                }
                UCropFragment uCropFragment = UCropFragment.newInstance(extras);
                fragments.add(uCropFragment);
            }
        }

        if (uCropSupportList.size() == 0) {
            throw new IllegalArgumentException("No clipping data sources are available");
        }
        setGalleryAdapter();
        UCropFragment uCropFragment = fragments.get(getCropSupportPosition());
        switchCropFragment(uCropFragment, getCropSupportPosition());
        galleryAdapter.setCurrentSelectPosition(getCropSupportPosition());
    }

    /**
     * getCropSupportPosition
     *
     * @return
     */
    private int getCropSupportPosition() {
        int position = 0;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return position;
        }
        ArrayList<String> skipCropMimeType = extras.getStringArrayList(UCrop.Options.EXTRA_SKIP_CROP_MIME_TYPE);
        if (skipCropMimeType != null && skipCropMimeType.size() > 0) {
            position = -1;
            filterSet.addAll(skipCropMimeType);
            for (int i = 0; i < uCropSupportList.size(); i++) {
                String path = uCropSupportList.get(i);
                String mimeType = getPathToMimeType(path);
                position++;
                if (!filterSet.contains(mimeType)) {
                    break;
                }
            }
            if (position == -1 || position > fragments.size()) {
                position = 0;
            }
        }
        return position;
    }

    /**
     * getPathToMimeType
     *
     * @param path
     * @return
     */
    private String getPathToMimeType(String path) {
        String mimeType;
        if (FileUtils.isContent(path)) {
            mimeType = FileUtils.getMimeTypeFromMediaContentUri(this, Uri.parse(path));
        } else {
            mimeType = FileUtils.getMimeTypeFromMediaContentUri(this, Uri.fromFile(new File(path)));
        }
        return mimeType;
    }


    /**
     * switch crop fragment tab
     *
     * @param targetFragment target fragment
     * @param position       target index
     */
    private void switchCropFragment(UCropFragment targetFragment, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (uCropCurrentFragment != null) {
                transaction.hide(uCropCurrentFragment);
            }
            transaction.add(R.id.fragment_container, targetFragment, UCropFragment.TAG + "-" + position);
        } else {
            transaction.hide(uCropCurrentFragment).show(targetFragment);
            targetFragment.fragmentReVisible();
        }
        currentFragmentPosition = position;
        uCropCurrentFragment = targetFragment;
        transaction.commitAllowingStateLoss();
    }

    private void setGalleryAdapter() {
        RecyclerView galleryRecycle = findViewById(R.id.recycler_gallery);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        galleryRecycle.setLayoutManager(layoutManager);
        if (galleryRecycle.getItemDecorationCount() == 0) {
            galleryRecycle.addItemDecoration(new GridSpacingItemDecoration(Integer.MAX_VALUE,
                    DensityUtil.dip2px(this, 6), true));
        }
        LayoutAnimationController animation = AnimationUtils
                .loadLayoutAnimation(this, R.anim.ucrop_layout_animation_fall_down);
        galleryRecycle.setLayoutAnimation(animation);
        int galleryBarBackground = getIntent().getIntExtra(UCrop.Options.EXTRA_GALLERY_BAR_BACKGROUND,
                R.drawable.ucrop_gallery_bg);
        galleryRecycle.setBackgroundResource(galleryBarBackground);
        galleryAdapter = new UCropGalleryAdapter(uCropSupportList);
        galleryAdapter.setOnItemClickListener(new UCropGalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (isSkipCropForbid) {
                    return;
                }
                String path = uCropSupportList.get(position);
                String mimeType = getPathToMimeType(path);
                if (filterSet.contains(mimeType)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.ucrop_not_crop), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (galleryAdapter.getCurrentSelectPosition() == position) {
                    return;
                }
                galleryAdapter.notifyItemChanged(galleryAdapter.getCurrentSelectPosition());
                galleryAdapter.setCurrentSelectPosition(position);
                galleryAdapter.notifyItemChanged(position);
                UCropFragment uCropFragment = fragments.get(position);
                switchCropFragment(uCropFragment, position);
            }
        });
        galleryRecycle.setAdapter(galleryAdapter);
    }

    /**
     * create crop output path dir
     *
     * @return
     */
    private String getSandboxPathDir() {
        File customFile;
        String outputDir = getIntent().getStringExtra(UCrop.Options.EXTRA_CROP_OUTPUT_DIR);
        if (outputDir == null || "".equals(outputDir)) {
            customFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), "Sandbox");
        } else {
            customFile = new File(outputDir);
        }
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    private void setupViews(@NonNull Intent intent) {
        aspectRatioList = getIntent().getParcelableArrayListExtra(UCrop.Options.EXTRA_MULTIPLE_ASPECT_RATIO);
        isForbidCropGifWebp = intent.getBooleanExtra(UCrop.Options.EXTRA_CROP_FORBID_GIF_WEBP, false);
        outputCropFileName = intent.getStringExtra(UCrop.Options.EXTRA_CROP_OUTPUT_FILE_NAME);
        mStatusBarColor = intent.getIntExtra(UCrop.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_statusbar));
        mToolbarColor = intent.getIntExtra(UCrop.Options.EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar));

        mToolbarWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar_widget));
        mToolbarCancelDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, R.drawable.ucrop_ic_cross);
        mToolbarCropDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CROP_DRAWABLE, R.drawable.ucrop_ic_done);
        mToolbarTitle = intent.getStringExtra(UCrop.Options.EXTRA_UCROP_TITLE_TEXT_TOOLBAR);
        mToolbarTitleSize = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_TITLE_TEXT_SIZE_TOOLBAR, 18);
        mToolbarTitle = mToolbarTitle != null ? mToolbarTitle : getResources().getString(R.string.ucrop_label_edit_photo);

        setupAppBar();
    }


    /**
     * Configures and styles both status bar and toolbar.
     */
    private void setupAppBar() {
        setStatusBarColor(mStatusBarColor);

        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor);
        toolbar.setTitleTextColor(mToolbarWidgetColor);

        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTextColor(mToolbarWidgetColor);
        toolbarTitle.setText(mToolbarTitle);
        toolbarTitle.setTextSize(mToolbarTitleSize);

        // Color buttons inside the Toolbar
        Drawable stateButtonDrawable = AppCompatResources.getDrawable(this, mToolbarCancelDrawable).mutate();
        ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mToolbarWidgetColor, BlendModeCompat.SRC_ATOP);
        stateButtonDrawable.setColorFilter(colorFilter);
        toolbar.setNavigationIcon(stateButtonDrawable);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    @Override
    public void loadingProgress(boolean showLoader) {
        mShowLoader = showLoader;
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                int realPosition = currentFragmentPosition + uCropNotSupportList.size();
                int realTotalSize = uCropNotSupportList.size() + uCropSupportList.size() - 1;
                mergeCropResult(result.mResultData);
                if (realPosition == realTotalSize) {
                    onCropCompleteFinish();
                } else {
                    int nextFragmentPosition = currentFragmentPosition + 1;
                    String path = uCropSupportList.get(nextFragmentPosition);
                    String mimeType = getPathToMimeType(path);
                    boolean isCropCompleteFinish = false;
                    while (filterSet.contains(mimeType)) {
                        if (nextFragmentPosition == realTotalSize) {
                            isCropCompleteFinish = true;
                            break;
                        } else {
                            nextFragmentPosition += 1;
                            path = uCropSupportList.get(nextFragmentPosition);
                            mimeType = getPathToMimeType(path);
                        }
                    }
                    if (isCropCompleteFinish) {
                        onCropCompleteFinish();
                    } else {
                        UCropFragment uCropFragment = fragments.get(nextFragmentPosition);
                        switchCropFragment(uCropFragment, nextFragmentPosition);
                        galleryAdapter.notifyItemChanged(galleryAdapter.getCurrentSelectPosition());
                        galleryAdapter.setCurrentSelectPosition(nextFragmentPosition);
                        galleryAdapter.notifyItemChanged(galleryAdapter.getCurrentSelectPosition());
                    }
                }
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }
    }

    /**
     * onCropCompleteFinish
     */
    private void onCropCompleteFinish() {
        JSONArray array = new JSONArray();
        for (Map.Entry<String, JSONObject> stringJSONObjectEntry : uCropTotalQueue.entrySet()) {
            JSONObject object = stringJSONObjectEntry.getValue();
            array.put(object);
        }
        Intent intent = new Intent();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, array.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * merge crop result
     *
     * @param intent
     */
    private void mergeCropResult(Intent intent) {
        try {
            String key = intent.getStringExtra(UCrop.EXTRA_CROP_INPUT_ORIGINAL);
            JSONObject uCropObject = uCropTotalQueue.get(key);
            Uri output = UCrop.getOutput(intent);
            uCropObject.put(CustomIntentKey.EXTRA_OUT_PUT_PATH, output != null ? output.getPath() : "");
            uCropObject.put(CustomIntentKey.EXTRA_IMAGE_WIDTH, UCrop.getOutputImageWidth(intent));
            uCropObject.put(CustomIntentKey.EXTRA_IMAGE_HEIGHT, UCrop.getOutputImageHeight(intent));
            uCropObject.put(CustomIntentKey.EXTRA_OFFSET_X, UCrop.getOutputImageOffsetX(intent));
            uCropObject.put(CustomIntentKey.EXTRA_OFFSET_Y, UCrop.getOutputImageOffsetY(intent));
            uCropObject.put(CustomIntentKey.EXTRA_ASPECT_RATIO, UCrop.getOutputCropAspectRatio(intent));
            uCropTotalQueue.put(key, uCropObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(UCropMultipleActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(UCropMultipleActivity.this, "Unexpected error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        UCropDevelopConfig.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.ucrop_menu_activity, menu);

        // Change crop & loader menu icons color to match the rest of the UI colors

        MenuItem menuItemLoader = menu.findItem(R.id.menu_loader);
        Drawable menuItemLoaderIcon = menuItemLoader.getIcon();
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate();
                ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mToolbarWidgetColor, BlendModeCompat.SRC_ATOP);
                menuItemLoaderIcon.setColorFilter(colorFilter);
                menuItemLoader.setIcon(menuItemLoaderIcon);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            ((Animatable) menuItemLoader.getIcon()).start();
        }

        MenuItem menuItemCrop = menu.findItem(R.id.menu_crop);
        Drawable menuItemCropIcon = ContextCompat.getDrawable(this, mToolbarCropDrawable);
        if (menuItemCropIcon != null) {
            menuItemCropIcon.mutate();
            ColorFilter colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mToolbarWidgetColor, BlendModeCompat.SRC_ATOP);
            menuItemCropIcon.setColorFilter(colorFilter);
            menuItemCrop.setIcon(menuItemCropIcon);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_crop).setVisible(!mShowLoader);
        menu.findItem(R.id.menu_loader).setVisible(mShowLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_crop) {
            if (uCropCurrentFragment != null && uCropCurrentFragment.isAdded()) {
                uCropCurrentFragment.cropAndSaveImage();
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
