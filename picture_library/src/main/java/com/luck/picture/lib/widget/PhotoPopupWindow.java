package com.luck.picture.lib.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.luck.picture.lib.R;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.widget
 * email：893855882@qq.com
 * data：2017/6/2
 */

public class PhotoPopupWindow extends PopupWindow implements View.OnClickListener {
    private TextView picture_tv_photo, picture_tv_video, picture_tv_cancel;
    private LinearLayout ll_root;
    private FrameLayout fl_content;
    private Animation animationIn, animationOut;
    private boolean isDismiss = false;

    public PhotoPopupWindow(Context context) {
        super(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.picture_camera_pop_layout, null);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable());
        this.setContentView(inflate);
        animationIn = AnimationUtils.loadAnimation(context, R.anim.up_in);
        animationOut = AnimationUtils.loadAnimation(context, R.anim.down_out);
        ll_root = (LinearLayout) inflate.findViewById(R.id.ll_root);
        fl_content = (FrameLayout) inflate.findViewById(R.id.fl_content);
        picture_tv_photo = (TextView) inflate.findViewById(R.id.picture_tv_photo);
        picture_tv_cancel = (TextView) inflate.findViewById(R.id.picture_tv_cancel);
        picture_tv_video = (TextView) inflate.findViewById(R.id.picture_tv_video);
        picture_tv_video.setOnClickListener(this);
        picture_tv_cancel.setOnClickListener(this);
        picture_tv_photo.setOnClickListener(this);
        fl_content.setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View parent) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                int[] location = new int[2];
                parent.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1] + parent.getHeight();
                this.showAtLocation(parent, Gravity.BOTTOM, x, y);
            } else {
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            }

            isDismiss = false;
            ll_root.startAnimation(animationIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        ll_root.startAnimation(animationOut);
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
                    PhotoPopupWindow.super.dismiss();
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
                PhotoPopupWindow.super.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_tv_photo) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(0);
                PhotoPopupWindow.super.dismiss();
            }
        }
        if (id == R.id.picture_tv_video) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(1);
                PhotoPopupWindow.super.dismiss();
            }
        }
        dismiss();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int positon);
    }
}
