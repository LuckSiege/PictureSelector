package com.yalantis.ucrop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author：luck
 * @date：2020-01-08 19:29
 * @describe：多图裁剪
 */
public class PictureMultiCuttingActivity extends UCropActivity {
    private final static int MIN_NUM = 1;
    private RecyclerView mRecyclerView;
    private PicturePhotoGalleryAdapter mAdapter;
    private final ArrayList<LocalMedia> list = new ArrayList<>();
    private boolean isWithVideoImage;
    private int cutIndex;
    private int oldCutIndex;
    private String renameCropFilename;
    private boolean isCamera;
    private boolean isAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        // 自定义裁剪输出名
        renameCropFilename = intent.getStringExtra(UCrop.Options.EXTRA_RENAME_CROP_FILENAME);
        // 是否单独拍照
        isCamera = intent.getBooleanExtra(UCrop.Options.EXTRA_CAMERA, false);
        // 是否混选模式
        isWithVideoImage = intent.getBooleanExtra(UCrop.Options.EXTRA_WITH_VIDEO_IMAGE, false);
        // 裁剪数据
        List<LocalMedia> localMedia = getIntent().getParcelableArrayListExtra(UCrop.Options.EXTRA_CUT_CROP);
        // 列表是否显示动画效果
        isAnimation = getIntent().getBooleanExtra(UCrop.Options.EXTRA_MULTIPLE_RECYCLERANIMATION, true);
        // Crop cut list
        if (localMedia == null || localMedia.size() == 0) {
            onBackPressed();
            return;
        }
        list.addAll(localMedia);
        if (list.size() > MIN_NUM) {
            initLoadCutData();
            addPhotoRecyclerView();
        }
    }

    /**
     * 动态添加多图裁剪底部预览图片列表
     */
    private void addPhotoRecyclerView() {
        boolean isMultipleSkipCrop = getIntent().getBooleanExtra(UCrop.Options.EXTRA_SKIP_MULTIPLE_CROP, true);
        mRecyclerView = new RecyclerView(this);
        mRecyclerView.setId(R.id.id_recycler);
        mRecyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.ucrop_color_widget_background));
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        ScreenUtils.dip2px(this, 80));
        mRecyclerView.setLayoutParams(lp);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (isAnimation) {
            LayoutAnimationController animation = AnimationUtils
                    .loadLayoutAnimation(getApplicationContext(), R.anim.ucrop_layout_animation_fall_down);
            mRecyclerView.setLayoutAnimation(animation);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) Objects.requireNonNull(mRecyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        resetCutDataStatus();
        list.get(cutIndex).setCut(true);
        mAdapter = new PicturePhotoGalleryAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        if (isMultipleSkipCrop) {
            mAdapter.setOnItemClickListener(new PicturePhotoGalleryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view) {
                    LocalMedia cutInfo = list.get(position);
                    if (PictureMimeType.isHasVideo(cutInfo.getMimeType())) {
                        return;
                    }
                    if (cutIndex == position) {
                        return;
                    }
                    resetLastCropStatus();
                    cutIndex = position;
                    oldCutIndex = cutIndex;
                    resetCutData();
                }
            });
        }
        uCropPhotoBox.addView(mRecyclerView);
        changeLayoutParams(mShowBottomControls);

        // 裁剪框居于RecyclerView之上
        FrameLayout uCropFrame = findViewById(R.id.ucrop_frame);
        ((RelativeLayout.LayoutParams) uCropFrame.getLayoutParams())
                .addRule(RelativeLayout.ABOVE, R.id.id_recycler);

        // RecyclerView居于BottomControls之上
        ((RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams())
                .addRule(RelativeLayout.ABOVE, R.id.controls_wrapper);
    }

    /**
     * 重置裁剪参数
     */
    protected void resetCutData() {
        uCropPhotoBox.removeView(mRecyclerView);
        if (mBlockingView != null) {
            uCropPhotoBox.removeView(mBlockingView);
        }
        setContentView(R.layout.ucrop_activity_photobox);
        // setContentView之后重新获取一下容器id
        uCropPhotoBox = findViewById(R.id.ucrop_photobox);
        addBlockingView();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }
        LocalMedia cutInfo = list.get(cutIndex);
        String path = cutInfo.getPath();
        boolean isHttp = PictureMimeType.isHasHttp(path);
        String suffix = PictureMimeType.getLastImgType(PictureMimeType.isContent(path)
                ? PictureFileUtils.getPath(this, Uri.parse(path)) : path);
        Uri uri;
        if (!TextUtils.isEmpty(cutInfo.getAndroidQToPath())) {
            uri = Uri.fromFile(new File(cutInfo.getAndroidQToPath()));
        } else {
            uri = isHttp || PictureMimeType.isContent(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
        }
        extras.putParcelable(UCrop.EXTRA_INPUT_URI, uri);
        File file = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                getExternalFilesDir(Environment.DIRECTORY_PICTURES) : getCacheDir();
        extras.putParcelable(UCrop.EXTRA_OUTPUT_URI,
                Uri.fromFile(new File(file,
                        TextUtils.isEmpty(renameCropFilename) ? DateUtils.getCreateFileName("IMG_CROP_") + suffix : isCamera ? renameCropFilename : PictureFileUtils.rename(renameCropFilename))));
        intent.putExtras(extras);
        setupViews(intent);
        refreshPhotoRecyclerData();
        setImageData(intent);
        setInitialState();
        int scrollWidth = cutIndex * ScreenUtils.dip2px(this, 60);
        if (scrollWidth > mScreenWidth * 0.8) {
            mRecyclerView.scrollBy(ScreenUtils.dip2px(this, 60), 0);
        } else if (scrollWidth < mScreenWidth * 0.4) {
            mRecyclerView.scrollBy(ScreenUtils.dip2px(this, -60), 0);
        }
    }

    /**
     * 切换裁剪图片
     */
    private void refreshPhotoRecyclerData() {
        resetCutDataStatus();
        list.get(cutIndex).setCut(true);
        mAdapter.notifyItemChanged(cutIndex);
        uCropPhotoBox.addView(mRecyclerView);

        changeLayoutParams(mShowBottomControls);

        // 裁剪框居于RecyclerView之上
        FrameLayout uCropFrame = findViewById(R.id.ucrop_frame);
        ((RelativeLayout.LayoutParams) uCropFrame.getLayoutParams())
                .addRule(RelativeLayout.ABOVE, R.id.id_recycler);
        // RecyclerView居于BottomControls之上
        ((RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams())
                .addRule(RelativeLayout.ABOVE, R.id.controls_wrapper);
    }

    /**
     * 重置上一次选中状态
     */
    private void resetLastCropStatus() {
        int size = list.size();
        if (size > MIN_NUM) {
            if (size > oldCutIndex) {
                list.get(oldCutIndex).setCut(false);
                mAdapter.notifyItemChanged(cutIndex);
            }
        }
    }

    /**
     * 重置数据裁剪状态
     */
    private void resetCutDataStatus() {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            LocalMedia cutInfo = list.get(i);
            cutInfo.setCut(false);
        }
    }

    /**
     * 装载裁剪数据
     */
    private void initLoadCutData() {
        // Crop cut list
        if (list == null || list.size() == 0) {
            onBackPressed();
            return;
        }
        int size = list.size();
        if (isWithVideoImage) {
            getIndex(size);
        }
    }

    /**
     * 获取图片index
     *
     * @param size
     */
    private void getIndex(int size) {
        for (int i = 0; i < size; i++) {
            LocalMedia cutInfo = list.get(i);
            if (cutInfo != null && PictureMimeType.isHasImage(cutInfo.getMimeType())) {
                cutIndex = i;
                break;
            }
        }
    }

    private void changeLayoutParams(boolean mShowBottomControls) {
        if (mRecyclerView.getLayoutParams() == null) {
            return;
        }
        if (mShowBottomControls) {
            // 显示工具栏
            ((RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams())
                    .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

            ((RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams())
                    .addRule(RelativeLayout.ABOVE, R.id.wrapper_controls);
        } else {
            // 没有显示工具栏
            ((RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams())
                    .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            ((RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams())
                    .addRule(RelativeLayout.ABOVE, 0);
        }
    }

    @Override
    protected void setResultUri(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        try {
            if (list.size() < cutIndex) {
                onBackPressed();
                return;
            }
            LocalMedia info = list.get(cutIndex);
            info.setCutPath(uri.getPath());
            info.setCut(true);
            info.setCropResultAspectRatio(resultAspectRatio);
            info.setCropOffsetX(offsetX);
            info.setCropOffsetY(offsetY);
            info.setCropImageWidth(imageWidth);
            info.setCropImageHeight(imageHeight);
            info.setAndroidQToPath(SdkVersionUtils.checkedAndroid_Q() ? info.getCutPath() : info.getAndroidQToPath());
            resetLastCropStatus();
            cutIndex++;
            if (isWithVideoImage) {
                if (cutIndex < list.size() && PictureMimeType.isHasVideo(list.get(cutIndex).getMimeType())) {
                    // 一个死循环找到了图片为终止条件，这里不需要考虑全是视频的问题，因为在启动裁剪时就已经判断好了
                    while (true) {
                        if (cutIndex >= list.size()) {
                            // 最后一条了也跳出循环
                            break;
                        }
                        String newMimeType = list.get(cutIndex).getMimeType();
                        if (PictureMimeType.isHasImage(newMimeType)) {
                            // 命中图片跳出循环
                            break;
                        } else {
                            // 如果下一个是视频则继续找图片
                            cutIndex++;
                        }
                    }
                }
            }
            oldCutIndex = cutIndex;
            if (cutIndex >= list.size()) {
                for (int i = 0; i < list.size(); i++) {
                    LocalMedia media = list.get(i);
                    media.setCut(!TextUtils.isEmpty(media.getCutPath()));
                }
                setResult(RESULT_OK, new Intent().putExtra(UCrop.Options.EXTRA_OUTPUT_URI_LIST, list));
                onBackPressed();
            } else {
                resetCutData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(null);
        }
        super.onDestroy();
    }
}
