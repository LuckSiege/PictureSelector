package com.luck.picture.lib.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * =====================================
 * 作    者: 陈嘉桐 445263848@qq.com
 * 版    本：1.0.4
 * 创建日期：2017/4/26
 * 描    述：向下箭头的退出按钮
 * =====================================
 */
public class ReturnButton extends View {

    private int size;

    private int center_X;
    private int center_Y;
    private float strokeWidth;

    private Paint paint;
    Path path;

    public ReturnButton(Context context, int size) {
        this(context);
        this.size = size;
        center_X = size / 2;
        center_Y = size / 2;

        strokeWidth = size / 15f;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        path = new Path();
    }

    public ReturnButton(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(size, size / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(strokeWidth, strokeWidth / 2);
        path.lineTo(center_X, center_Y - strokeWidth / 2);
        path.lineTo(size - strokeWidth, strokeWidth / 2);
        canvas.drawPath(path, paint);
    }
}
