package com.yalantis.ucrop.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.yalantis.ucrop.observable.ObserverListener;
import com.yalantis.ucrop.util.FunctionConfig;
import com.yalantis.ucrop.util.LocalMediaLoader;
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
public class AlbumDirectoryActivity extends BaseActivity implements View.OnClickListener, PublicTitleBar.OnTitleBarClick, AlbumDirectoryAdapter.OnItemClickListener, ObserverListener {

    private List<LocalMediaFolder> folders = new ArrayList<>();
    private AlbumDirectoryAdapter adapter;
    private RecyclerView recyclerView;
    private PublicTitleBar titleBar;
    private TextView tv_empty;
    private List<LocalMedia> selectMedias = new ArrayList<>();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("app.activity.finish")) {
                finish();
                overridePendingTransition(0, R.anim.slide_bottom_out);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        registerReceiver(receiver, "app.activity.finish");
        if (selectMedias == null)
            selectMedias = new ArrayList<>();
        titleBar = (PublicTitleBar) findViewById(R.id.titleBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        tv_empty.setOnClickListener(this);
        ImagesObservable.getInstance().add(this);
        switch (type) {
            case LocalMediaLoader.TYPE_IMAGE:
                titleBar.setTitleText(getString(R.string.select_photo));
                break;
            case LocalMediaLoader.TYPE_VIDEO:
                titleBar.setTitleText(getString(R.string.select_video));
                break;
        }

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
        initData();
    }

    /**
     * 初始化数据
     */
    protected void initData() {
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
            startEmptyImageActivity();
        }
    }

    /**
     *
     */
    private void startEmptyImageActivity() {
        List<LocalMedia> images = new ArrayList<>();
        String title = "";
        switch (type) {
            case LocalMediaLoader.TYPE_IMAGE:
                title = getString(R.string.lately_image);
                break;
            case LocalMediaLoader.TYPE_VIDEO:
                title = getString(R.string.lately_video);
                break;
        }
        startImageGridActivity(title, images);
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
        intent.putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectMedias);
        intent.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        intent.putExtra(FunctionConfig.FOLDER_NAME, folderName);
        intent.putExtra(FunctionConfig.EXTRA_IS_TOP_ACTIVITY, true);
        intent.setClass(mContext, ImageGridActivity.class);
        startActivity(intent);
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
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        clearData();
    }

    protected void clearData() {
        ImagesObservable.getInstance().remove(this);
        ImagesObservable.getInstance().clearLocalFolders();
        ImagesObservable.getInstance().clearLocalMedia();
        ImagesObservable.getInstance().clearSelectedLocalMedia();
    }

    @Override
    public void observerUpFoldersData(List<LocalMediaFolder> folders) {
        this.folders = folders;
        adapter.bindFolderData(folders);
        initData();
    }

    @Override
    public void observerUpSelectsData(List<LocalMedia> selectMedias) {
        folders = ImagesObservable.getInstance().readLocalFolders();
        this.selectMedias = selectMedias;
        if (folders != null && folders.size() > 0)
            adapter.bindFolderData(folders);
        if (selectMedias == null)
            selectMedias = new ArrayList<>();
        notifyDataCheckedStatus(selectMedias);
        if (tv_empty.getVisibility() == View.VISIBLE && adapter.getFolderData().size() > 0)
            tv_empty.setVisibility(View.GONE);
    }
}
