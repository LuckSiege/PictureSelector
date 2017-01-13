package com.yalantis.ucrop.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.adapter.AlbumDirectoryAdapter;
import com.yalantis.ucrop.decoration.RecycleViewDivider;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.entity.LocalMediaFolder;
import com.yalantis.ucrop.observable.ImagesObservable;
import com.yalantis.ucrop.util.LocalMediaLoader;
import com.yalantis.ucrop.util.PictureConfig;
import com.yalantis.ucrop.util.ToolbarUtil;
import com.yalantis.ucrop.util.Utils;
import com.yalantis.ucrop.widget.PublicTitleBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class AlbumDirectoryActivity extends BaseActivity implements View.OnClickListener, PublicTitleBar.OnTitleBarClick, AlbumDirectoryAdapter.OnItemClickListener {

    private List<LocalMediaFolder> folders = new ArrayList<>();
    private AlbumDirectoryAdapter adapter;
    private RecyclerView recyclerView;
    private PublicTitleBar titleBar;
    private TextView tv_empty;
    private List<LocalMedia> selectMedias = new ArrayList<>();

    public static void startPhoto(Activity activity, PictureConfig options) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        if (options == null) {
            options = new PictureConfig();
        }
        Intent intent = new Intent(activity, AlbumDirectoryActivity.class);
        intent.putExtra(PictureConfig.EXTRA_MAX_SELECT_NUM, options.getMaxSelectNum());
        intent.putExtra(PictureConfig.EXTRA_MAX_SPAN_COUNT, options.getImageSpanCount());
        intent.putExtra(PictureConfig.EXTRA_CROP_MODE, options.getCopyMode());
        intent.putExtra(PictureConfig.EXTRA_SELECT_MODE, options.getSelectMode());
        intent.putExtra(PictureConfig.EXTRA_SHOW_CAMERA, options.isShowCamera());
        intent.putExtra(PictureConfig.EXTRA_ENABLE_PREVIEW, options.isEnablePreview());
        intent.putExtra(PictureConfig.EXTRA_ENABLE_CROP, options.isEnableCrop());
        intent.putExtra(PictureConfig.EXTRA_TYPE, options.getType());
        intent.putExtra(PictureConfig.EXTRA_COMPRESS, options.isCompress());
        intent.putExtra(PictureConfig.EXTRA_ENABLE_PREVIEW_VIDEO, options.isPreviewVideo());
        intent.putExtra(PictureConfig.BACKGROUND_COLOR, options.getThemeStyle());
        intent.putExtra(PictureConfig.CHECKED_DRAWABLE, options.getCheckedBoxDrawable());
        intent.putExtra(PictureConfig.EXTRA_CROP_W, options.getCropW());
        intent.putExtra(PictureConfig.EXTRA_CROP_H, options.getCropH());
        intent.putExtra(PictureConfig.EXTRA_VIDEO_SECOND, options.getRecordVideoSecond());
        intent.putExtra(PictureConfig.EXTRA_DEFINITION, options.getRecordVideoDefinition());
        intent.putExtra(PictureConfig.EXTRA_IS_CHECKED_NUM, options.isCheckNumMode());
        intent.putExtra(PictureConfig.EXTRA_PREVIEW_COLOR, options.getPreviewColor());
        intent.putExtra(PictureConfig.EXTRA_COMPLETE_COLOR, options.getCompleteColor());
        intent.putExtra(PictureConfig.EXTRA_BOTTOM_BG_COLOR, options.getBottomBgColor());
        intent.putExtra(PictureConfig.EXTRA_PREVIEW_BOTTOM_BG_COLOR, options.getPreviewBottomBgColor());
        intent.putExtra(PictureConfig.EXTRA_COMPRESS_QUALITY, options.getCompressQuality());
        intent.putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) options.getSelectMedia());
        activity.startActivityForResult(intent, PictureConfig.REQUEST_IMAGE);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        type = getIntent().getIntExtra(PictureConfig.EXTRA_TYPE, 1);// 1 图片 2视频
        showCamera = getIntent().getBooleanExtra(PictureConfig.EXTRA_SHOW_CAMERA, true);// 是否显示拍摄
        enablePreview = getIntent().getBooleanExtra(PictureConfig.EXTRA_ENABLE_PREVIEW, true);// 是否显示预览
        selectMode = getIntent().getIntExtra(PictureConfig.EXTRA_SELECT_MODE, PictureConfig.MODE_MULTIPLE);// 选择模式,单选or多选
        enableCrop = getIntent().getBooleanExtra(PictureConfig.EXTRA_ENABLE_CROP, false);// 是否裁剪
        maxSelectNum = getIntent().getIntExtra(PictureConfig.EXTRA_MAX_SELECT_NUM, PictureConfig.SELECT_MAX_NUM);// 图片最大选择数量
        copyMode = getIntent().getIntExtra(PictureConfig.EXTRA_CROP_MODE, PictureConfig.COPY_MODEL_DEFAULT);// 裁剪模式
        enablePreviewVideo = getIntent().getBooleanExtra(PictureConfig.EXTRA_ENABLE_PREVIEW_VIDEO, false);// 是否预览视频
        backgroundColor = getIntent().getIntExtra(PictureConfig.BACKGROUND_COLOR, 0);
        cb_drawable = getIntent().getIntExtra(PictureConfig.CHECKED_DRAWABLE, 0);
        isCompress = getIntent().getBooleanExtra(PictureConfig.EXTRA_COMPRESS, false);
        spanCount = getIntent().getIntExtra(PictureConfig.EXTRA_MAX_SPAN_COUNT, 4);
        cropW = getIntent().getIntExtra(PictureConfig.EXTRA_CROP_W, 0);
        cropH = getIntent().getIntExtra(PictureConfig.EXTRA_CROP_H, 0);
        recordVideoSecond = getIntent().getIntExtra(PictureConfig.EXTRA_VIDEO_SECOND, 0);
        definition = getIntent().getIntExtra(PictureConfig.EXTRA_DEFINITION, PictureConfig.HIGH);
        is_checked_num = getIntent().getBooleanExtra(PictureConfig.EXTRA_IS_CHECKED_NUM, false);
        previewColor = getIntent().getIntExtra(PictureConfig.EXTRA_PREVIEW_COLOR, R.color.tab_color_true);
        completeColor = getIntent().getIntExtra(PictureConfig.EXTRA_COMPLETE_COLOR, R.color.tab_color_true);
        bottomBgColor = getIntent().getIntExtra(PictureConfig.EXTRA_BOTTOM_BG_COLOR, R.color.color_fa);
        previewBottomBgColor = getIntent().getIntExtra(PictureConfig.EXTRA_PREVIEW_BOTTOM_BG_COLOR, R.color.bar_grey_90);
        compressQuality = getIntent().getIntExtra(PictureConfig.EXTRA_COMPRESS_QUALITY, 100);
        selectMedias = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        if (selectMedias == null)
            selectMedias = new ArrayList<>();
        titleBar = (PublicTitleBar) findViewById(R.id.titleBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        tv_empty.setOnClickListener(this);

        titleBar.setTitleText(getString(R.string.select_photo));

        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        titleBar.setTitleBarBackgroundColor(backgroundColor);
        titleBar.setRightText(getString(R.string.cancel));
        titleBar.setOnTitleBarClickListener(this);
        adapter = new AlbumDirectoryAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecycleViewDivider(
                mContext, LinearLayoutManager.HORIZONTAL, Utils.dip2px(this, 0.5f), ContextCompat.getColor(this, R.color.line_color)));
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        // 先判断手机是否有读取权限，主要是针对6.0已上系统
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            readLocalMedia();
        } else {
            requestPermission(PictureConfig.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }


    /**
     * 设置选中状态
     */
    private void notifyDataCheckedStatus(List<LocalMedia> medias) {
        try {
            // 获取选中图片
            if (medias == null) {
                medias = new ArrayList<>();
            }

            List<LocalMediaFolder> folders = adapter.getFolderData();
            for (LocalMediaFolder folder : folders) {
                // 只重置之前有选中过的文件夹，因为有可能也取消选中的
                if (folder.isChecked()) {
                    folder.setCheckedNum(0);
                    folder.setChecked(false);
                }
            }

            if (medias.size() > 0) {
                for (LocalMediaFolder folder : folders) {
                    int num = 0;// 记录当前相册下有多少张是选中的
                    List<LocalMedia> images = folder.getImages();
                    for (LocalMedia media : images) {
                        String path = media.getPath();
                        for (LocalMedia m : medias) {
                            if (path.equals(m.getPath())) {
                                num++;
                                folder.setChecked(true);
                                folder.setCheckedNum(num);
                            }
                        }
                    }
                }
            }
            adapter.bindFolderData(folders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void readLocalMedia() {
        /**
         * 根据type决定，查询本地图片或视频。
         */
        new LocalMediaLoader(this, type).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {

            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    tv_empty.setVisibility(View.GONE);
                    adapter.bindFolderData(folders);
                    notifyDataCheckedStatus(selectMedias);
                } else {
                    tv_empty.setVisibility(View.VISIBLE);
                    switch (type) {
                        case LocalMediaLoader.TYPE_IMAGE:
                            tv_empty.setText(getString(R.string.no_photo));
                            break;
                        case LocalMediaLoader.TYPE_VIDEO:
                            tv_empty.setText(getString(R.string.no_video));
                            break;
                    }

                }
            }
        });
    }


    @Override
    public void onLeftClick() {

    }

    @Override
    public void onRightClick() {
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_empty) {
            List<LocalMedia> images = new ArrayList<>();
            startImageGridActivity(titleBar.getTitleText(), images);
        }
    }

    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        if (images != null && images.size() > 0) {
            startImageGridActivity(folderName, images);
        }
    }


    private void startImageGridActivity(String folderName, final List<LocalMedia> images) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        Intent intent = new Intent();
        List<LocalMediaFolder> folders = adapter.getFolderData();
        ImagesObservable.getInstance().saveLocalMedia(images);
        ImagesObservable.getInstance().saveLocalFolders(folders);
        intent.putExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectMedias);
        intent.putExtra(PictureConfig.FOLDER_NAME, folderName);
        intent.putExtra(PictureConfig.EXTRA_ENABLE_PREVIEW, enablePreview);
        intent.putExtra(PictureConfig.EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(PictureConfig.EXTRA_SELECT_MODE, selectMode);
        intent.putExtra(PictureConfig.EXTRA_ENABLE_CROP, enableCrop);
        intent.putExtra(PictureConfig.EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(PictureConfig.EXTRA_TYPE, type);
        intent.putExtra(PictureConfig.EXTRA_CROP_MODE, copyMode);
        intent.putExtra(PictureConfig.EXTRA_ENABLE_PREVIEW_VIDEO, enablePreviewVideo);
        intent.putExtra(PictureConfig.BACKGROUND_COLOR, backgroundColor);
        intent.putExtra(PictureConfig.CHECKED_DRAWABLE, cb_drawable);
        intent.putExtra(PictureConfig.EXTRA_COMPRESS, isCompress);
        intent.putExtra(PictureConfig.EXTRA_CROP_W, cropW);
        intent.putExtra(PictureConfig.EXTRA_CROP_H, cropH);
        intent.putExtra(PictureConfig.EXTRA_MAX_SPAN_COUNT, spanCount);
        intent.putExtra(PictureConfig.EXTRA_VIDEO_SECOND, recordVideoSecond);
        intent.putExtra(PictureConfig.EXTRA_DEFINITION, definition);
        intent.putExtra(PictureConfig.EXTRA_IS_CHECKED_NUM, is_checked_num);
        intent.putExtra(PictureConfig.EXTRA_PREVIEW_COLOR, previewColor);
        intent.putExtra(PictureConfig.EXTRA_COMPLETE_COLOR, completeColor);
        intent.putExtra(PictureConfig.EXTRA_BOTTOM_BG_COLOR, bottomBgColor);
        intent.putExtra(PictureConfig.EXTRA_PREVIEW_BOTTOM_BG_COLOR, previewBottomBgColor);
        intent.putExtra(PictureConfig.EXTRA_COMPRESS_QUALITY, compressQuality);
        intent.setClass(mContext, ImageGridActivity.class);
        startActivityForResult(intent, PictureConfig.REQUEST_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_IMAGE:
                    // 单选图片裁剪完调用
                    int type = data.getIntExtra("type", 0);
                    if (type == 1) {
                        folders = ImagesObservable.getInstance().readLocalFolders();
                        selectMedias = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
                        if (folders != null && folders.size() > 0)
                            adapter.bindFolderData(folders);
                        if (selectMedias == null)
                            selectMedias = new ArrayList<>();
                        notifyDataCheckedStatus(selectMedias);
                        if (tv_empty.getVisibility() == View.VISIBLE && adapter.getFolderData().size() > 0)
                            tv_empty.setVisibility(View.GONE);
                    } else {
                        ArrayList<LocalMedia> images = (ArrayList<LocalMedia>) data.getSerializableExtra(PictureConfig.REQUEST_OUTPUT);
                        if (images == null)
                            images = new ArrayList<>();
                        setResult(RESULT_OK, new Intent().putExtra(PictureConfig.REQUEST_OUTPUT, images));
                        finish();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagesObservable.getInstance().clearLocalFolders();
        ImagesObservable.getInstance().clearLocalMedia();
        ImagesObservable.getInstance().clearSelectedLocalMedia();
    }


}
