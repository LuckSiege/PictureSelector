package com.luck.picture.lib.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureAlbumAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnAlbumItemClickListener;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.util.List;

/**
 * @author：luck
 * @date：2021/11/17 2:33 下午
 * @describe：AlbumListPopWindow
 */
public class AlbumListPopWindow extends PopupWindow {
    private static final int ALBUM_MAX_COUNT = 8;
    private final Context mContext;
    private View windMask;
    private RecyclerView mRecyclerView;
    private boolean isDismiss = false;
    private int windowMaxHeight;
    private PictureAlbumAdapter mAdapter;
    private SelectorConfig selectorConfig;

    public AlbumListPopWindow(Context context, SelectorConfig config) {
        this.mContext = context;
        this.selectorConfig = config;
        setContentView(LayoutInflater.from(context).inflate(R.layout.ps_window_folder, null));
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.PictureThemeWindowStyle);
        setFocusable(true);
        setOutsideTouchable(true);
        update();
        initViews();
    }

    private void initViews() {
        windowMaxHeight = (int) (DensityUtil.getScreenHeight(mContext) * 0.6);
        mRecyclerView = getContentView().findViewById(R.id.folder_list);
        windMask = getContentView().findViewById(R.id.rootViewBg);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext));
        mAdapter = new PictureAlbumAdapter(selectorConfig);
        mRecyclerView.setAdapter(mAdapter);
        windMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        getContentView().findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SdkVersionUtils.isMinM()) {
                    dismiss();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void bindAlbumData(List<LocalMediaFolder> list) {
        mAdapter.bindAlbumData(list);
        mAdapter.notifyDataSetChanged();
        ViewGroup.LayoutParams lp = mRecyclerView.getLayoutParams();
        lp.height = list.size() > ALBUM_MAX_COUNT ? windowMaxHeight : ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    public List<LocalMediaFolder> getAlbumList() {
        return mAdapter.getAlbumList();
    }

    public LocalMediaFolder getFolder(int position) {
        return mAdapter.getAlbumList().size() > 0
                && position < mAdapter.getAlbumList().size() ? mAdapter.getAlbumList().get(position) : null;
    }

    public int getFirstAlbumImageCount() {
        return getFolderCount() > 0 ? getFolder(0).getFolderTotalNum() : 0;
    }

    public int getFolderCount() {
        return mAdapter.getAlbumList().size();
    }

    /**
     * 专辑列表桥接类
     *
     * @param listener
     */
    public void setOnIBridgeAlbumWidget(OnAlbumItemClickListener listener) {
        mAdapter.setOnIBridgeAlbumWidget(listener);
    }

    public static AlbumListPopWindow buildPopWindow(Context context, SelectorConfig config) {
        return new AlbumListPopWindow(context, config);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (getAlbumList() == null || getAlbumList().size() == 0) {
            return;
        }
        if (SdkVersionUtils.isN()) {
            int[] location = new int[2];
            anchor.getLocationInWindow(location);
            showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] + anchor.getHeight());
        } else {
            super.showAsDropDown(anchor);
        }
        isDismiss = false;
        if (windowStatusListener != null) {
            windowStatusListener.onShowPopupWindow();
        }
        windMask.animate().alpha(1).setDuration(250).setStartDelay(250).start();
        changeSelectedAlbumStyle();
    }

    /**
     * 设置选中状态
     */
    public void changeSelectedAlbumStyle() {
        List<LocalMediaFolder> folders = mAdapter.getAlbumList();
        for (int i = 0; i < folders.size(); i++) {
            LocalMediaFolder folder = folders.get(i);
            folder.setSelectTag(false);
            mAdapter.notifyItemChanged(i);
            for (int j = 0; j < selectorConfig.getSelectCount(); j++) {
                LocalMedia media = selectorConfig.getSelectedResult().get(j);
                if (TextUtils.equals(folder.getFolderName(), media.getParentFolderName())
                        || folder.getBucketId() == PictureConfig.ALL) {
                    folder.setSelectTag(true);
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        windMask.setAlpha(0F);
        if (windowStatusListener != null) {
            windowStatusListener.onDismissPopupWindow();
        }
        isDismiss = true;
        windMask.post(new Runnable() {
            @Override
            public void run() {
                AlbumListPopWindow.super.dismiss();
                isDismiss = false;
            }
        });
    }


    /**
     * AlbumListPopWindow 弹出与消失状态监听
     *
     * @param listener
     */
    public void setOnPopupWindowStatusListener(OnPopupWindowStatusListener listener) {
        this.windowStatusListener = listener;
    }

    private OnPopupWindowStatusListener windowStatusListener;

    public interface OnPopupWindowStatusListener {

        void onShowPopupWindow();

        void onDismissPopupWindow();
    }
}
