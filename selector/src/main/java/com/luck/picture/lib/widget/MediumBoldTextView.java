package com.luck.picture.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.luck.picture.lib.R;

/**
 * @author：luck
 * @date：2020/8/25 10:32 AM
 * @describe：MediumBoldTextView
 */
public class MediumBoldTextView extends AppCompatTextView {
    private float mStrokeWidth = 0.6F;

    public MediumBoldTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public MediumBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PsMediumBoldTextView);
            mStrokeWidth = array.getFloat(R.styleable.PsMediumBoldTextView_stroke_Width, mStrokeWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        if (paint.getStrokeWidth() != mStrokeWidth) {
            paint.setStrokeWidth(mStrokeWidth);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        super.onDraw(canvas);
    }

    public void setStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
        invalidate();
    }
}
