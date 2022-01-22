package com.luck.picture.lib.utils;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author：luck
 * @date：2022/1/22 2:25 下午
 * @describe：TouchUtils
 */
public class TouchUtils {

    public static final int UNKNOWN = 0;
    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int RIGHT = 4;
    public static final int DOWN = 8;

    private TouchUtils() {
    }

    /**
     * addOnItemTouchListener
     *
     * @param rv
     * @param itemTouchListener
     */
    public static void addOnItemTouchListener(RecyclerView rv, OnRvItemTouchListener itemTouchListener) {
        rv.addOnItemTouchListener(itemTouchListener);
    }

    public static abstract class OnRvItemTouchListener implements RecyclerView.OnItemTouchListener {
        private static final int MIN_SLIDE_ITEM_SIZE_TIMES = 3;
        private int startDownX, startDownY;
        private int direction;

        public abstract void onDown(RecyclerView rv, int x, int y, MotionEvent event);

        public abstract void onMove(RecyclerView rv, View itemView, int adapterPosition,
                                    int direction, MotionEvent event);

        public abstract void onCancel();

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
            boolean isIntercept = false;
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startDownX = (int) event.getX();
                    startDownY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX();
                    float moveY = event.getY();
                    float dx = moveX - startDownX;
                    float dy = moveY - startDownY;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        View itemView = rv.findChildViewUnder(event.getX(), event.getY());
                        if (itemView != null) {
                            int centerPoint = itemView.getWidth() / MIN_SLIDE_ITEM_SIZE_TIMES;
                            if (Math.abs(dx) > centerPoint) {
                                isIntercept = true;
                            }
                        }
                    }
                    break;
            }
            return isIntercept;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startDownX = (int) event.getX();
                startDownY = (int) event.getY();
                onDown(rv, startDownX, startDownY, event);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float moveX = event.getX();
                float moveY = event.getY();
                float dx = moveX - startDownX;
                float dy = moveY - startDownY;
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        direction = RIGHT;
                    } else {
                        direction = LEFT;
                    }
                    startDownX = (int) event.getX();
                    startDownY = (int) event.getY();
                }
                View itemView = rv.findChildViewUnder(event.getX(), event.getY());
                int adapterPosition = itemView != null ? rv.getChildAdapterPosition(itemView) : RecyclerView.NO_POSITION;
                onMove(rv, itemView, adapterPosition, direction, event);
            } else {
                direction = UNKNOWN;
                onCancel();
            }
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
