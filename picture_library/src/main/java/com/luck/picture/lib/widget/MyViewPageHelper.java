package com.luck.picture.lib.widget;

import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * @author：luck
 * @date：2020-04-11 14:43
 * @describe：MyViewPageHelper
 */
public class MyViewPageHelper {
    ViewPager viewPager;

    MScroller scroller;

    public MyViewPageHelper(ViewPager viewPager) {
        this.viewPager = viewPager;
        init();
    }

    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    public MScroller getScroller() {
        return scroller;
    }


    public void setCurrentItem(int item, boolean smooth) {
        int current = viewPager.getCurrentItem();
        //如果页面相隔大于1,就设置页面切换的动画的时间为0
        if (Math.abs(current - item) > 1) {
            scroller.setNoDuration(true);
            viewPager.setCurrentItem(item, smooth);
            scroller.setNoDuration(false);
        } else {
            scroller.setNoDuration(false);
            viewPager.setCurrentItem(item, smooth);
        }
    }

    private void init() {
        scroller = new MScroller(viewPager.getContext());
        Class<ViewPager> cl = ViewPager.class;
        try {
            Field field = cl.getDeclaredField("mScroller");
            field.setAccessible(true);
            //利用反射设置mScroller域为自己定义的MScroller
            field.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
