package com.luck.picture.lib.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author：luck
 * @date：2020-04-18 14:11
 * @describe：AlphaInAnimationAdapter
 */
public class AlphaInAnimationAdapter extends BaseAnimationAdapter {
    private static final float DEFAULT_ALPHA_FROM = 0f;
    private final float mFrom;
    private int mPageSize;

    public AlphaInAnimationAdapter(RecyclerView.Adapter adapter, int pageSize) {
        this(adapter, DEFAULT_ALPHA_FROM);
        mPageSize = pageSize;
    }

    public AlphaInAnimationAdapter(RecyclerView.Adapter adapter, float from) {
        super(adapter);
        mFrom = from;
    }

    @Override
    protected Animator[] getAnimators(View view) {
        int itemCount = getItemCount();
        if (itemCount > mPageSize * 2) {
            return new Animator[]{ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f)};
        }
        return new Animator[]{};
    }
}
