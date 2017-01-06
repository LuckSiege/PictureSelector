package com.yalantis.ucrop.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yalantis.ucrop.R;

/**
 * author：luck
 * project：PublicTitleBar
 * package：com.luck.publictitlebar.titlebar
 * email：893855882@qq.com
 * data：16/12/30
 */
public class PublicTitleBar extends RelativeLayout implements View.OnClickListener {
    private LinearLayout leftView, rightView;
    private RelativeLayout rlViewGroup;
    private LayoutParams leftParams, titleParams, rightParams, rlViewGroupParams;
    private TextView leftText, titleText, rightText;
    private String defaultTitle = "标题栏";
    private int defaultTitleBarBackground = Color.parseColor("#393a3e");// 默认titleBar背景色
    private int defaultTitleBarHeight = 48;// 默认titleBar高度
    private float defaultTitleSize = 18;// 默认title字体大小
    private int defaultTitleEndWidth = 210;// 中间标题文字宽度，超过则省略号代替
    private int defaultTextColor = Color.parseColor("#FFFFFF");// 默认文字颜色
    private int defaultResources = R.drawable.back;
    private float defaultLeftOrRight = 14;// 默认左右两边字体大小

    public PublicTitleBar(Context context) {
        super(context);

    }

    public PublicTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PublicTitleBar);
        initView(context, a);
    }

    /**
     * 初始化布局
     */
    private void initView(Context context, TypedArray a) {
        // titleBar背景色
        defaultTitleBarBackground = a.getColor(R.styleable.PublicTitleBar_titleBarBackground, defaultTitleBarBackground);
        boolean left_display = a.getBoolean(R.styleable.PublicTitleBar_left_display, true);// 是否显示左边按钮，默认显示
        boolean right_display = a.getBoolean(R.styleable.PublicTitleBar_right_display, false);// 是否显示右边按钮
        int rightRes = a.getResourceId(R.styleable.PublicTitleBar_rightRes, 0);// 右边图标
        int height = (int) a.getDimension(R.styleable.PublicTitleBar_titleBarHeight, defaultTitleBarHeight);
        String titleTxt = a.getString(R.styleable.PublicTitleBar_titleText);
        defaultTextColor = a.getColor(R.styleable.PublicTitleBar_titleColor, defaultTextColor);
        defaultTitleSize = a.getDimension(R.styleable.PublicTitleBar_titleSize, defaultTitleSize);
        int leftColor = a.getColor(R.styleable.PublicTitleBar_leftTextColor, defaultTextColor);
        String rightTxt = a.getString(R.styleable.PublicTitleBar_rightText);
        int rightColor = a.getColor(R.styleable.PublicTitleBar_rightTextColor, defaultTextColor);
        int leftRes = a.getResourceId(R.styleable.PublicTitleBar_leftRes, defaultResources);
        rlViewGroupParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        rlViewGroup = new RelativeLayout(context);
        leftView = new LinearLayout(context);
        rightView = new LinearLayout(context);
        rlViewGroup.setLayoutParams(rlViewGroupParams);
        leftView.setId(R.id.left_id);
        rightView.setId(R.id.right_id);
        leftView.setOnClickListener(this);
        rightView.setOnClickListener(this);
        if (isNull(titleTxt)) {
            titleTxt = defaultTitle;
        }
        //左边按钮
        leftParams = new LayoutParams(height, ViewGroup.LayoutParams.MATCH_PARENT);
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftView.setLayoutParams(leftParams);
        leftText = new TextView(context);
        leftText.setBackgroundResource(leftRes);
        leftView.setGravity(Gravity.CENTER);
        leftText.setTextColor(leftColor);
        leftText.setTextSize(defaultLeftOrRight);
        leftView.addView(leftText);
        if (left_display) {
            // 显示右边按钮
            leftView.setVisibility(VISIBLE);
        } else {
            // 隐藏右边按钮
            leftView.setVisibility(INVISIBLE);
        }
        // 中间标题
        titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleText = new TextView(context);
        titleText.setText(titleTxt);
        titleText.setTextSize(defaultTitleSize);
        titleText.setTextColor(defaultTextColor);
        titleText.setEllipsize(TextUtils.TruncateAt.END);
        titleText.setMaxWidth(dip2px(context, defaultTitleEndWidth));
        titleText.setSingleLine(true);
        titleText.setGravity(Gravity.CENTER);
        titleText.setTextColor(ContextCompat.getColor(context, R.color.ucrop_color_toolbar_widget));
        titleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleText.setLayoutParams(titleParams);


        //右边按钮
        rightParams = new LayoutParams(height, ViewGroup.LayoutParams.MATCH_PARENT);
        rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rightParams.rightMargin = 10;
        rightView.setLayoutParams(rightParams);

        rightText = new TextView(context);
        rightText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rightText.setText(rightTxt);
        rightText.setTextColor(rightColor);
        rightText.setTextSize(defaultLeftOrRight);
        rightText.setSingleLine(true);
        rightView.setGravity(Gravity.CENTER);
        rightView.addView(rightText);
        if (right_display) {
            // 显示右边按钮
            rightView.setVisibility(VISIBLE);
            // 设置了右边图标，则在右边文字无效
            if (rightRes > 0) {
                rightText.setText("");
                rightText.setBackgroundResource(rightRes);
            }
        } else {
            // 隐藏右边按钮
            rightView.setVisibility(INVISIBLE);
        }

        // 将view添加到布局
        rlViewGroup.addView(leftView);
        rlViewGroup.addView(titleText);
        rlViewGroup.addView(rightView);

        rlViewGroup.setLayoutParams(rlViewGroupParams);

        addView(rlViewGroup);

        setBackgroundColor(defaultTitleBarBackground);// 设置titleBar背景色
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, height)));

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.left_id) {
            if (titleBarClick != null) {
                titleBarClick.onLeftClick();
            }

        } else if (i == R.id.right_id) {
            if (titleBarClick != null) {
                titleBarClick.onRightClick();
            }
        }
    }

    /**
     * 外部设置标题内容
     *
     * @param titleTxt
     */
    public void setTitleText(String titleTxt) {
        if (titleTxt != null)
            titleText.setText(titleTxt);
    }

    /**
     * 设置标题栏背景色
     *
     * @param color 色值
     */
    public void setTitleBarBackgroundColor(int color) {
        setBackgroundColor(color);// 设置titleBar背景色
    }


    /**
     * 外部获取标题内容
     */
    public String getTitleText() {
        return titleText.getText().toString().trim();
    }

    /**
     * 外部设置右边标题内容
     *
     * @param rightStr
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setRightText(String rightStr) {
        if (rightText != null)
            if (!isNull(rightStr)) {
                rightView.setVisibility(VISIBLE);
                rightText.setText(rightStr);
                rightText.setBackground(null);
            }
    }

    /**
     * 外部设置左边标题内容
     *
     * @param leftStr
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setLeftText(String leftStr) {
        if (rightText != null)
            if (!isNull(leftStr)) {
                leftView.setVisibility(VISIBLE);
                leftText.setText(leftStr);
                leftText.setBackground(null);
            }
    }

    /**
     * 是否显示右边按钮
     *
     * @param orVGone
     */
    public void setRightOrVGone(boolean orVGone) {
        if (rightView != null) {
            if (orVGone) {
                String trim = rightText.getText().toString().trim();
                if (!isNull(trim)) {
                    rightText.setText("");
                }
                rightView.setVisibility(VISIBLE);
            } else {
                rightView.setVisibility(INVISIBLE);
            }
        }
    }

    /**
     * 是否显示左边按钮
     *
     * @param orVGone
     */
    public void setLeftOrVGone(boolean orVGone) {
        if (leftView != null) {
            if (orVGone) {
                String trim = leftText.getText().toString().trim();
                if (!isNull(trim)) {
                    leftText.setText("");
                }
                leftView.setVisibility(VISIBLE);
            } else {
                leftView.setVisibility(INVISIBLE);
            }
        }
    }


    /**
     * 向外部提供一个可点击的接口，处理相应逻辑
     */
    public OnTitleBarClick titleBarClick;


    public interface OnTitleBarClick {
        void onLeftClick();

        void onRightClick();
    }

    public void setOnTitleBarClickListener(OnTitleBarClick titleBarClick) {
        this.titleBarClick = titleBarClick;
    }


    /**
     * dp转px
     */
    public static int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * px转dp
     */
    public static int px2dip(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断 一个字段的值否为空
     *
     * @param s
     * @return
     * @author Michael.Zhang 2013-9-7 下午4:39:00
     */
    public static boolean isNull(String s) {
        if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }
}
