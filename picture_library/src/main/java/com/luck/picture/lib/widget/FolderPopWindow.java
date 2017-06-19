package com.luck.picture.lib.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.StringUtils;

import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.widget
 * email：893855882@qq.com
 * data：2017/5/25
 */

public class FolderPopWindow extends PopupWindow implements View.OnClickListener {
    private int popupHeight, popupWidth;
    private Context context;
    private View window;
    private RecyclerView recyclerView;
    private PictureAlbumDirectoryAdapter adapter;
    private Animation animationIn, animationOut;
    private boolean isDismiss = false;
    private FrameLayout id_ll_root;
    private TextView picture_title;
    private Drawable drawableUp, drawableDown;
    private int mimeType;

    public FolderPopWindow(Context context, int mimeType) {
        this.context = context;


        initView();
        setPopConfig();

    }

    private void setPopConfig() {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.WindowStyle);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(Color.argb(0, 0, 0, 0));
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);// 设置外部触摸会关闭窗口
        this.setAnimationStyle(R.style.WindowStyle);
        drawableUp = AttrsUtils.getTypeValuePopWindowImg(context, R.attr.picture_arrow_up_icon);
        drawableDown = AttrsUtils.getTypeValuePopWindowImg(context, R.attr.picture_arrow_down_icon);
        animationIn = AnimationUtils.loadAnimation(context, R.anim.photo_album_show);
        animationOut = AnimationUtils.loadAnimation(context, R.anim.photo_album_dismiss);
        //获取自身的长宽高
        window.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = window.getMeasuredHeight();
        popupWidth = window.getMeasuredWidth();
    }

    public void initView() {
        window = LayoutInflater.from(context).inflate(R.layout.picture_window_folder, null);
        this.setContentView(window);
        id_ll_root = (FrameLayout) window.findViewById(R.id.id_ll_root);
        adapter = new PictureAlbumDirectoryAdapter(context);
        recyclerView = (RecyclerView) window.findViewById(R.id.folder_list);
        recyclerView.getLayoutParams().height = (int) (ScreenUtils.getScreenHeight(context) * 0.8);
        recyclerView.addItemDecoration(new RecycleViewDivider(
                context, LinearLayoutManager.HORIZONTAL, ScreenUtils.dip2px(context, 0), ContextCompat.getColor(context, R.color.transparent)));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        id_ll_root.setOnClickListener(this);
    }

    public void bindFolder(List<LocalMediaFolder> folders) {
        adapter.setMimeType(mimeType);
        adapter.bindFolderData(folders);
    }

    public void setPictureTitleView(TextView picture_title) {
        this.picture_title = picture_title;
    }

    @Override
    public void showAsDropDown(View anchor) {
        try {

            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];

            showAtLocation(anchor, Gravity.NO_GRAVITY, x - popupWidth / 2, y - popupHeight);

            isDismiss = false;
            recyclerView.startAnimation(animationIn);
            StringUtils.modifyTextViewDrawable(picture_title, drawableUp, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnItemClickListener(PictureAlbumDirectoryAdapter.OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void dismiss() {
        DebugUtil.i("PopWindow:", "dismiss");
        if (isDismiss) {
            return;
        }
        StringUtils.modifyTextViewDrawable(picture_title, drawableDown, 2);
        isDismiss = true;
        recyclerView.startAnimation(animationOut);
        dismiss();
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    dismiss4Pop();
                } else {
                    FolderPopWindow.super.dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 在android4.1.1和4.1.2版本关闭PopWindow
     */
    private void dismiss4Pop() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FolderPopWindow.super.dismiss();
            }
        });
    }


    /**
     * 设置选中状态
     */
    public void notifyDataCheckedStatus(List<LocalMedia> medias) {
        try {
            // 获取选中图片
            List<LocalMediaFolder> folders = adapter.getFolderData();
            for (LocalMediaFolder folder : folders) {
                folder.setCheckedNum(0);
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_ll_root) {
            dismiss();
        }
    }

}
