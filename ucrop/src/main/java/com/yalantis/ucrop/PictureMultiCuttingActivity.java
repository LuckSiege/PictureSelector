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

import com.yalantis.ucrop.model.CutInfo;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.MimeType;
import com.yalantis.ucrop.util.ScreenUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author：luck
 * @date：2020-01-08 19:29
 * @describe：多图裁剪
 */
public class PictureMultiCuttingActivity extends UCropActivity {
    private final static int MIN_NUM = 1;
    private RecyclerView mRecyclerView;
    private PicturePhotoGalleryAdapter mAdapter;
    private ArrayList<CutInfo> list;
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
        list = getIntent().getParcelableArrayListExtra(UCrop.Options.EXTRA_CUT_CROP);
        // 列表是否显示动画效果
        isAnimation = getIntent().getBooleanExtra(UCrop.Options.EXTRA_MULTIPLE_RECYCLERANIMATION, true);
        // Crop cut list
        if (list == null || list.size() == 0) {
            onBackPressed();
            return;
        }
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
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator())
                .setSupportsChangeAnimations(false);
        resetCutDataStatus();
        list.get(cutIndex).setCut(true);
        mAdapter = new PicturePhotoGalleryAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
        if (isMultipleSkipCrop) {
            mAdapter.setOnItemClickListener(new PicturePhotoGalleryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view) {
                    CutInfo cutInfo = list.get(position);
                    if (MimeType.isHasVideo(cutInfo.getMimeType())) {
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
        CutInfo cutInfo = list.get(cutIndex);
        String path = cutInfo.getPath();
        boolean isHttp = MimeType.isHttp(path);
        String suffix = MimeType.getLastImgType(MimeType.isContent(path)
                ? FileUtils.getPath(this, Uri.parse(path)) : path);
        Uri uri;
        if (!TextUtils.isEmpty(cutInfo.getAndroidQToPath())) {
            uri = Uri.fromFile(new File(cutInfo.getAndroidQToPath()));
        } else {
            uri = isHttp || MimeType.isContent(path) ? Uri.parse(path) : Uri.fromFile(new File(path));
        }
        extras.putParcelable(UCrop.EXTRA_INPUT_URI, uri);
        File file = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                getExternalFilesDir(Environment.DIRECTORY_PICTURES) : getCacheDir();
        extras.putParcelable(UCrop.EXTRA_OUTPUT_URI,
                Uri.fromFile(new File(file,
                        TextUtils.isEmpty(renameCropFilename) ? FileUtils.getCreateFileName("IMG_CROP_") + suffix : isCamera ? renameCropFilename : FileUtils.rename(renameCropFilename))));
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
            CutInfo cutInfo = list.get(i);
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
        for (int i = 0; i < size; i++) {
            CutInfo cutInfo = list.get(i);
            boolean isHttp = MimeType.isHttp(cutInfo.getPath());
            if (!isHttp) {
                continue;
            }
            String path = list.get(i).getPath();
            String imgType = MimeType.getLastImgType(path);
            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(imgType)) {
                continue;
            }
            File file = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES) : getCacheDir();
            File newFile = new File(file, "temporary_thumbnail_" + i + imgType);
            String mimeType = MimeType.getImageMimeType(path);
            cutInfo.setMimeType(mimeType);
            cutInfo.setHttpOutUri(Uri.fromFile(newFile));
        }
    }

    /**
     * 获取图片index
     *
     * @param size
     */
    private void getIndex(int size) {
        for (int i = 0; i < size; i++) {
            CutInfo cutInfo = list.get(i);
            if (cutInfo != null && MimeType.isHasImage(cutInfo.getMimeType())) {
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
            CutInfo info = list.get(cutIndex);
            info.setCutPath(uri.getPath());
            info.setCut(true);
            info.setResultAspectRatio(resultAspectRatio);
            info.setOffsetX(offsetX);
            info.setOffsetY(offsetY);
            info.setImageWidth(imageWidth);
            info.setImageHeight(imageHeight);
            resetLastCropStatus();
            cutIndex++;
            if (isWithVideoImage) {
                if (cutIndex < list.size() && MimeType.isHasVideo(list.get(cutIndex).getMimeType())) {
                    // 一个死循环找到了图片为终止条件，这里不需要考虑全是视频的问题，因为在启动裁剪时就已经判断好了
                    while (true) {
                        if (cutIndex >= list.size()) {
                            // 最后一条了也跳出循环
                            break;
                        }
                        String newMimeType = list.get(cutIndex).getMimeType();
                        if (MimeType.isHasImage(newMimeType)) {
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
                setResult(RESULT_OK, new Intent()
                        .putExtra(UCrop.Options.EXTRA_OUTPUT_URI_LIST, list)
                );
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
