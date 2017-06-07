package com.luck.picture.lib.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.R;
import com.luck.picture.lib.tools.ScreenUtils;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.widget
 * email：893855882@qq.com
 * data：2017/6/7
 */

public class CustomToast {
    private Toast mToast;
    private TextView mTvToast;

    public void showToast(Context ctx, String content) {
        if (mToast == null) {
            mToast = new Toast(ctx);
            mToast.setGravity(Gravity.TOP, 0, ScreenUtils.dip2px(ctx, 50));//设置toast显示的位置，这是居中
            mToast.setDuration(Toast.LENGTH_SHORT);//设置toast显示的时长
            View _root = LayoutInflater.from(ctx).inflate(R.layout.picture_toast_custom, null);//自定义样式，自定义布局文件
            mTvToast = (TextView) _root.findViewById(R.id.tvCustomToast);
            mToast.setView(_root);//设置自定义的view
        }
        mTvToast.setText(content);//设置文本
        mToast.show();//展示toast
    }
}
