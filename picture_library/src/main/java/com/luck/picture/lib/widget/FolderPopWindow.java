package com.luck.picture.lib.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.listener.OnAlbumItemClickListener;
import com.luck.picture.lib.tools.AnimUtils;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.ScreenUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2017-5-25 17:02
 * @describe：文件目录PopupWindow
 */

public class FolderPopWindow extends PopupWindow {
    private Context context;
    private View window;
    private RecyclerView mRecyclerView;
    private PictureAlbumDirectoryAdapter adapter;
    private boolean isDismiss = false;
    private ImageView ivArrowView;
    private Drawable drawableUp, drawableDown;
    private int chooseMode;
    private PictureSelectionConfig config;
    private int maxHeight;
    private View rootViewBg;

    public FolderPopWindow(Context context) {
        this.context = context;
        this.config = PictureSelectionConfig.getInstance();
        this.chooseMode = config.chooseMode;
        this.window = LayoutInflater.from(context).inflate(R.layout.picture_window_folder, null);
        this.setContentView(window);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.PictureThemeWindowStyle);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_top_titleArrowUpDrawable != 0) {
                this.drawableUp = ContextCompat.getDrawable(context, PictureSelectionConfig.uiStyle.picture_top_titleArrowUpDrawable);
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleArrowDownDrawable != 0) {
                this.drawableDown = ContextCompat.getDrawable(context, PictureSelectionConfig.uiStyle.picture_top_titleArrowDownDrawable);
            }
        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureTitleUpResId != 0) {
                this.drawableUp = ContextCompat.getDrawable(context, PictureSelectionConfig.style.pictureTitleUpResId);
            }
            if (PictureSelectionConfig.style.pictureTitleDownResId != 0) {
                this.drawableDown = ContextCompat.getDrawable(context, PictureSelectionConfig.style.pictureTitleDownResId);
            }
        } else {
            if (config.isWeChatStyle) {
                this.drawableUp = ContextCompat.getDrawable(context, R.drawable.picture_icon_wechat_up);
                this.drawableDown = ContextCompat.getDrawable(context, R.drawable.picture_icon_wechat_down);
            } else {
                if (config.upResId != 0) {
                    this.drawableUp = ContextCompat.getDrawable(context, config.upResId);
                } else {
                    // 兼容老的Theme方式
                    this.drawableUp = AttrsUtils.getTypeValueDrawable(context, R.attr.picture_arrow_up_icon, R.drawable.picture_icon_arrow_up);
                }
                if (config.downResId != 0) {
                    this.drawableDown = ContextCompat.getDrawable(context, config.downResId);
                } else {
                    // 兼容老的Theme方式 picture.arrow_down.icon
                    this.drawableDown = AttrsUtils.getTypeValueDrawable(context, R.attr.picture_arrow_down_icon, R.drawable.picture_icon_arrow_down);
                }
            }
        }
        this.maxHeight = (int) (ScreenUtils.getScreenHeight(context) * 0.6);
        initView();
    }

    public void initView() {
        rootViewBg = window.findViewById(R.id.rootViewBg);
        adapter = new PictureAlbumDirectoryAdapter(config);
        mRecyclerView = window.findViewById(R.id.folder_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(adapter);
        View rootView = window.findViewById(R.id.rootView);
        rootViewBg.setOnClickListener(v -> dismiss());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            rootView.setOnClickListener(v -> dismiss());
        }
    }

    public void bindFolder(List<LocalMediaFolder> folders) {
        adapter.setChooseMode(chooseMode);
        adapter.bindFolderData(folders);
        ViewGroup.LayoutParams lp = mRecyclerView.getLayoutParams();
        lp.height = folders != null && folders.size() > 8 ? maxHeight
                : ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    public List<LocalMediaFolder> getFolderData() {
        return adapter.getFolderData();
    }

    public boolean isEmpty() {
        return adapter.getFolderData().size() == 0;
    }

    public LocalMediaFolder getFolder(int position) {
        return adapter.getFolderData().size() > 0
                && position < adapter.getFolderData().size() ? adapter.getFolderData().get(position) : null;
    }

    public void setArrowImageView(ImageView ivArrowView) {
        this.ivArrowView = ivArrowView;
    }

    @Override
    public void showAsDropDown(View anchor) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                int[] location = new int[2];
                anchor.getLocationInWindow(location);
                showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] + anchor.getHeight());
            } else {
                super.showAsDropDown(anchor);
            }
            isDismiss = false;
            ivArrowView.setImageDrawable(drawableUp);
            AnimUtils.rotateArrow(ivArrowView, true);
            rootViewBg.animate()
                    .alpha(1)
                    .setDuration(250)
                    .setStartDelay(250).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener) {
        adapter.setOnAlbumItemClickListener(listener);
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        rootViewBg.animate()
                .alpha(0)
                .setDuration(50)
                .start();
        ivArrowView.setImageDrawable(drawableDown);
        AnimUtils.rotateArrow(ivArrowView, false);
        isDismiss = true;
        FolderPopWindow.super.dismiss();
        isDismiss = false;
    }

    /**
     * 设置选中状态
     */
    public void updateFolderCheckStatus(List<LocalMedia> result) {
        try {
            List<LocalMediaFolder> folders = adapter.getFolderData();
            int size = folders.size();
            int resultSize = result.size();
            for (int i = 0; i < size; i++) {
                LocalMediaFolder folder = folders.get(i);
                folder.setCheckedNum(0);
                for (int j = 0; j < resultSize; j++) {
                    LocalMedia media = result.get(j);
                    if (folder.getName().equals(media.getParentFolderName())
                            || folder.getBucketId() == -1) {
                        folder.setCheckedNum(1);
                        break;
                    }
                }
            }
            adapter.bindFolderData(folders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
