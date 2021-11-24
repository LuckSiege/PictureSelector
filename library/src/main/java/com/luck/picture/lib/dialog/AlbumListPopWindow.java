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
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnAlbumItemClickListener;
import com.luck.picture.lib.manager.SelectedManager;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.utils.DensityUtil;

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

    public AlbumListPopWindow(Context context) {
        this.mContext = context;
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
        mAdapter = new PictureAlbumAdapter();
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
                if (SdkVersionUtils.isMinLollipop()) {
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

    /**
     * 上一次选中的相册
     */
    private LocalMediaFolder lastFolder;

    public void setLastFolder(LocalMediaFolder lastFolder) {
        this.lastFolder = lastFolder;
    }

    public LocalMediaFolder getLastFolder() {
        return lastFolder;
    }

    public LocalMediaFolder getFolder(int position) {
        return mAdapter.getAlbumList().size() > 0
                && position < mAdapter.getAlbumList().size() ? mAdapter.getAlbumList().get(position) : null;
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

    public static AlbumListPopWindow buildPopWindow(Context context) {
        return new AlbumListPopWindow(context);
    }

    @Override
    public void showAsDropDown(View anchor) {
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
    @SuppressLint("NotifyDataSetChanged")
    public void changeSelectedAlbumStyle() {
        List<LocalMediaFolder> folders = mAdapter.getAlbumList();
        int size = folders.size();
        int selectedSize = SelectedManager.getSelectedResult().size();
        for (int i = 0; i < size; i++) {
            LocalMediaFolder folder = folders.get(i);
            folder.setCheckedNum(0);
            for (int j = 0; j < selectedSize; j++) {
                LocalMedia media = SelectedManager.getSelectedResult().get(j);
                if (TextUtils.equals(folder.getName(), media.getParentFolderName()) || folder.getBucketId() == -1) {
                    folder.setCheckedNum(1);
                    break;
                }
            }
        }
        mAdapter.bindAlbumData(folders);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        windMask.animate().alpha(0).setDuration(50).start();
        if (windowStatusListener != null) {
            windowStatusListener.onDismissPopupWindow();
        }
        isDismiss = true;
        AlbumListPopWindow.super.dismiss();
        isDismiss = false;
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
