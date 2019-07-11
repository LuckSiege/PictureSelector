package com.yalantis.ucrop.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.callback.OverlayViewChangeListener;
import com.yalantis.ucrop.util.RectUtils;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * This view is used for drawing the overlay on top of the image. It may have frame, crop guidelines and dimmed area.
 * This must have LAYER_TYPE_SOFTWARE to draw itself properly.
 */
public class OverlayView extends View {
    public static final boolean DEFAULT_DRAG_FRAME = true;
    public static final boolean DEFAULT_SHOW_CROP_FRAME = true;
    public static final boolean DEFAULT_SHOW_CROP_GRID = true;
    public static final boolean DEFAULT_CIRCLE_DIMMED_LAYER = false;
    public static final boolean DEFAULT_FREESTYLE_CROP_ENABLED = false;
    public static final int DEFAULT_CROP_GRID_ROW_COUNT = 2;
    public static final int DEFAULT_CROP_GRID_COLUMN_COUNT = 2;
    private boolean mIsDragFrame = DEFAULT_DRAG_FRAME;
    private final RectF mCropViewRect = new RectF();
    private final RectF mTempRect = new RectF();

    private int mCropGridRowCount, mCropGridColumnCount;
    private float mTargetAspectRatio;
    private float[] mGridPoints = null;
    private boolean mShowCropFrame, mShowCropGrid;
    private boolean mCircleDimmedLayer;
    private int mDimmedColor;
    private Path mCircularPath = new Path();
    private Paint mDimmedStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFrameCornersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected int mThisWidth, mThisHeight;

    private boolean mIsFreestyleCropEnabled = DEFAULT_FREESTYLE_CROP_ENABLED;
    protected float[] mCropGridCorners;
    protected float[] mCropGridCenter;
    private float mPreviousTouchX = -1, mPreviousTouchY = -1;
    private int mCurrentTouchCornerIndex = -1;
    private int mTouchPointThreshold;
    private int mCropRectMinSize;
    private int mCropRectCornerTouchAreaLineLength;

    private OverlayViewChangeListener mCallback;

    private boolean mShouldSetupCropBounds;

    {
        mTouchPointThreshold = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_corner_touch_threshold);
        mCropRectMinSize = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_min_size);
        mCropRectCornerTouchAreaLineLength = getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_rect_corner_touch_area_line_length);
    }

    public OverlayView(Context context) {
        this(context, null);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OverlayViewChangeListener getOverlayViewChangeListener() {
        return mCallback;
    }

    public void setOverlayViewChangeListener(OverlayViewChangeListener callback) {
        mCallback = callback;
    }

    @NonNull
    public RectF getCropViewRect() {
        return mCropViewRect;
    }

    public boolean isFreestyleCropEnabled() {
        return mIsFreestyleCropEnabled;
    }

    public void setFreestyleCropEnabled(boolean freestyleCropEnabled) {
        mIsFreestyleCropEnabled = freestyleCropEnabled;
    }

    public boolean ismIsDragFrame() {
        return mIsDragFrame;
    }

    public void setDragFrame(boolean mIsDragFrame) {
        this.mIsDragFrame = mIsDragFrame;
    }

    /**
     * Setter for {@link #mCircleDimmedLayer} variable.
     *
     * @param circleDimmedLayer - set it to true if you want dimmed layer to be an circle
     */
    public void setCircleDimmedLayer(boolean circleDimmedLayer) {
        mCircleDimmedLayer = circleDimmedLayer;
    }

    /**
     * Setter for crop grid rows count.
     * Resets {@link #mGridPoints} variable because it is not valid anymore.
     */
    public void setCropGridRowCount(@IntRange(from = 0) int cropGridRowCount) {
        mCropGridRowCount = cropGridRowCount;
        mGridPoints = null;
    }

    /**
     * Setter for crop grid columns count.
     * Resets {@link #mGridPoints} variable because it is not valid anymore.
     */
    public void setCropGridColumnCount(@IntRange(from = 0) int cropGridColumnCount) {
        mCropGridColumnCount = cropGridColumnCount;
        mGridPoints = null;
    }

    /**
     * Setter for {@link #mShowCropFrame} variable.
     *
     * @param showCropFrame - set to true if you want to see a crop frame rectangle on top of an image
     */
    public void setShowCropFrame(boolean showCropFrame) {
        mShowCropFrame = showCropFrame;
    }

    /**
     * Setter for {@link #mShowCropGrid} variable.
     *
     * @param showCropGrid - set to true if you want to see a crop grid on top of an image
     */
    public void setShowCropGrid(boolean showCropGrid) {
        mShowCropGrid = showCropGrid;
    }

    /**
     * Setter for {@link #mDimmedColor} variable.
     *
     * @param dimmedColor - desired color of dimmed area around the crop bounds
     */
    public void setDimmedColor(@ColorInt int dimmedColor) {
        mDimmedColor = dimmedColor;
    }

    /**
     * Setter for crop frame stroke width
     */
    public void setCropFrameStrokeWidth(@IntRange(from = 0) int width) {
        mCropFramePaint.setStrokeWidth(width);
    }

    /**
     * Setter for crop grid stroke width
     */
    public void setCropGridStrokeWidth(@IntRange(from = 0) int width) {
        mCropGridPaint.setStrokeWidth(width);
    }

    /**
     * Setter for crop frame color
     */
    public void setCropFrameColor(@ColorInt int color) {
        mCropFramePaint.setColor(color);
    }

    /**
     * Setter for crop grid color
     */
    public void setCropGridColor(@ColorInt int color) {
        mCropGridPaint.setColor(color);
    }

    /**
     * This method sets aspect ratio for crop bounds.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    public void setTargetAspectRatio(final float targetAspectRatio) {
        mTargetAspectRatio = targetAspectRatio;
        if (mThisWidth > 0) {
            setupCropBounds();
            postInvalidate();
        } else {
            mShouldSetupCropBounds = true;
        }
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * {@link #mCropViewRect} is used to draw crop bounds - uses padding.
     */
    public void setupCropBounds() {
        int height = (int) (mThisWidth / mTargetAspectRatio);
        if (height > mThisHeight) {
            int width = (int) (mThisHeight * mTargetAspectRatio);
            int halfDiff = (mThisWidth - width) / 2;
            mCropViewRect.set(getPaddingLeft() + halfDiff, getPaddingTop(),
                    getPaddingLeft() + width + halfDiff, getPaddingTop() + mThisHeight);
        } else {
            int halfDiff = (mThisHeight - height) / 2;
            mCropViewRect.set(getPaddingLeft(), getPaddingTop() + halfDiff,
                    getPaddingLeft() + mThisWidth, getPaddingTop() + height + halfDiff);
        }

        if (mCallback != null) {
            mCallback.onCropRectUpdated(mCropViewRect);
        }

        updateGridPoints();
    }

    private void updateGridPoints() {
        mCropGridCorners = RectUtils.getCornersFromRect(mCropViewRect);
        mCropGridCenter = RectUtils.getCenterFromRect(mCropViewRect);

        mGridPoints = null;
        mCircularPath.reset();
        mCircularPath.addCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.f, Path.Direction.CW);
    }

    protected void init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;

            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false;
                setTargetAspectRatio(mTargetAspectRatio);
            }
        }
    }

    /**
     * Along with image there are dimmed layer, crop bounds and crop guidelines that must be drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDimmedLayer(canvas);
        drawCropGrid(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCropViewRect.isEmpty() || !mIsFreestyleCropEnabled) return false;

        float x = event.getX();
        float y = event.getY();

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            if (mPreviousTouchX < 0) {
                mPreviousTouchX = x;
                mPreviousTouchY = y;
            }
            mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y);
            return mCurrentTouchCornerIndex != -1;
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 1 && mCurrentTouchCornerIndex != -1) {

                x = Math.min(Math.max(x, getPaddingLeft()), getWidth() - getPaddingRight());
                y = Math.min(Math.max(y, getPaddingTop()), getHeight() - getPaddingBottom());

                updateCropViewRect(x, y);

                mPreviousTouchX = x;
                mPreviousTouchY = y;

                return true;
            }
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            mPreviousTouchX = -1;
            mPreviousTouchY = -1;
            mCurrentTouchCornerIndex = -1;

            if (mCallback != null) {
                mCallback.onCropRectUpdated(mCropViewRect);
            }
        }

        return false;
    }

    /**
     * * The order of the corners is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     */
    private void updateCropViewRect(float touchX, float touchY) {
        mTempRect.set(mCropViewRect);

        switch (mCurrentTouchCornerIndex) {
            // resize rectangle
            case 0:
                // 是否可拖动裁剪框
                if (mIsDragFrame) {
                    mTempRect.set(touchX, touchY, mCropViewRect.right, mCropViewRect.bottom);
                }
                break;
            case 1:
                // 是否可拖动裁剪框
                if (mIsDragFrame) {
                    mTempRect.set(mCropViewRect.left, touchY, touchX, mCropViewRect.bottom);
                }
                break;
            case 2:
                // 是否可拖动裁剪框
                if (mIsDragFrame) {
                    mTempRect.set(mCropViewRect.left, mCropViewRect.top, touchX, touchY);
                }
                break;
            case 3:
                // 是否可拖动裁剪框
                if (mIsDragFrame) {
                    mTempRect.set(touchX, mCropViewRect.top, mCropViewRect.right, touchY);
                }
                break;
            // move rectangle
            case 4:
                mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY);
                if (mTempRect.left > getLeft() && mTempRect.top > getTop()
                        && mTempRect.right < getRight() && mTempRect.bottom < getBottom()) {
                    mCropViewRect.set(mTempRect);
                    updateGridPoints();
                    postInvalidate();
                }
                return;
        }

        boolean changeHeight = mTempRect.height() >= mCropRectMinSize;
        boolean changeWidth = mTempRect.width() >= mCropRectMinSize;
        mCropViewRect.set(
                changeWidth ? mTempRect.left : mCropViewRect.left,
                changeHeight ? mTempRect.top : mCropViewRect.top,
                changeWidth ? mTempRect.right : mCropViewRect.right,
                changeHeight ? mTempRect.bottom : mCropViewRect.bottom);

        if (changeHeight || changeWidth) {
            updateGridPoints();
            postInvalidate();
        }
    }

    /**
     * * The order of the corners in the float array is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     *
     * @return - index of corner that is being dragged
     */
    private int getCurrentTouchIndex(float touchX, float touchY) {
        int closestPointIndex = -1;
        double closestPointDistance = mTouchPointThreshold;
        for (int i = 0; i < 8; i += 2) {
            double distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
                    + Math.pow(touchY - mCropGridCorners[i + 1], 2));
            if (distanceToCorner < closestPointDistance) {
                closestPointDistance = distanceToCorner;
                closestPointIndex = i / 2;
            }
        }
        if (closestPointIndex < 0 && mCropViewRect.contains(touchX, touchY)) {
            return 4;
        }

//        for (int i = 0; i <= 8; i += 2) {
//
//            double distanceToCorner;
//            if (i < 8) { // corners
//                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
//                        + Math.pow(touchY - mCropGridCorners[i + 1], 2));
//            } else { // center
//                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCenter[0], 2)
//                        + Math.pow(touchY - mCropGridCenter[1], 2));
//            }
//            if (distanceToCorner < closestPointDistance) {
//                closestPointDistance = distanceToCorner;
//                closestPointIndex = i / 2;
//            }
//        }
        return closestPointIndex;
    }

    /**
     * This method draws dimmed area around the crop bounds.
     *
     * @param canvas - valid canvas object
     */
    protected void drawDimmedLayer(@NonNull Canvas canvas) {
        canvas.save();
        if (mCircleDimmedLayer) {
            canvas.clipPath(mCircularPath, Region.Op.DIFFERENCE);
        } else {
            canvas.clipRect(mCropViewRect, Region.Op.DIFFERENCE);
        }
        canvas.drawColor(mDimmedColor);
        canvas.restore();

        if (mCircleDimmedLayer) { // Draw 1px stroke to fix antialias
            canvas.drawCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                    Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.f, mDimmedStrokePaint);
        }
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    protected void drawCropGrid(@NonNull Canvas canvas) {
        if (mShowCropGrid) {
            if (mGridPoints == null && !mCropViewRect.isEmpty()) {

                mGridPoints = new float[(mCropGridRowCount) * 4 + (mCropGridColumnCount) * 4];

                int index = 0;
                for (int i = 0; i < mCropGridRowCount; i++) {
                    mGridPoints[index++] = mCropViewRect.left;
                    mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                    mGridPoints[index++] = mCropViewRect.right;
                    mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                }

                for (int i = 0; i < mCropGridColumnCount; i++) {
                    mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                    mGridPoints[index++] = mCropViewRect.top;
                    mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                    mGridPoints[index++] = mCropViewRect.bottom;
                }
            }

            if (mGridPoints != null) {
                canvas.drawLines(mGridPoints, mCropGridPaint);
            }
        }

        if (mShowCropFrame) {
            canvas.drawRect(mCropViewRect, mCropFramePaint);
        }

        if (mIsFreestyleCropEnabled) {
            canvas.save();

            mTempRect.set(mCropViewRect);
            mTempRect.inset(mCropRectCornerTouchAreaLineLength, -mCropRectCornerTouchAreaLineLength);
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

            mTempRect.set(mCropViewRect);
            mTempRect.inset(-mCropRectCornerTouchAreaLineLength, mCropRectCornerTouchAreaLineLength);
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

            canvas.drawRect(mCropViewRect, mCropFrameCornersPaint);

            canvas.restore();
        }
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    @SuppressWarnings("deprecation")
    protected void processStyledAttributes(@NonNull TypedArray a) {
        mCircleDimmedLayer = a.getBoolean(R.styleable.ucrop_UCropView_ucrop_circle_dimmed_layer, DEFAULT_CIRCLE_DIMMED_LAYER);
        mDimmedColor = a.getColor(R.styleable.ucrop_UCropView_ucrop_dimmed_color,
                getResources().getColor(R.color.ucrop_color_default_dimmed));
        mDimmedStrokePaint.setColor(mDimmedColor);
        mDimmedStrokePaint.setStyle(Paint.Style.STROKE);
        mDimmedStrokePaint.setStrokeWidth(1);

        initCropFrameStyle(a);
        mShowCropFrame = a.getBoolean(R.styleable.ucrop_UCropView_ucrop_show_frame, DEFAULT_SHOW_CROP_FRAME);

        initCropGridStyle(a);
        mShowCropGrid = a.getBoolean(R.styleable.ucrop_UCropView_ucrop_show_grid, DEFAULT_SHOW_CROP_GRID);
    }

    /**
     * This method setups Paint object for the crop bounds.
     */
    @SuppressWarnings("deprecation")
    private void initCropFrameStyle(@NonNull TypedArray a) {
        int cropFrameStrokeSize = a.getDimensionPixelSize(R.styleable.ucrop_UCropView_ucrop_frame_stroke_size,
                getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width));
        int cropFrameColor = a.getColor(R.styleable.ucrop_UCropView_ucrop_frame_color,
                getResources().getColor(R.color.ucrop_color_default_crop_frame));
        mCropFramePaint.setStrokeWidth(cropFrameStrokeSize);
        mCropFramePaint.setColor(cropFrameColor);
        mCropFramePaint.setStyle(Paint.Style.STROKE);

        mCropFrameCornersPaint.setStrokeWidth(cropFrameStrokeSize * 3);
        mCropFrameCornersPaint.setColor(cropFrameColor);
        mCropFrameCornersPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * This method setups Paint object for the crop guidelines.
     */
    @SuppressWarnings("deprecation")
    private void initCropGridStyle(@NonNull TypedArray a) {
        int cropGridStrokeSize = a.getDimensionPixelSize(R.styleable.ucrop_UCropView_ucrop_grid_stroke_size,
                getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width));
        int cropGridColor = a.getColor(R.styleable.ucrop_UCropView_ucrop_grid_color,
                getResources().getColor(R.color.ucrop_color_default_crop_grid));
        mCropGridPaint.setStrokeWidth(cropGridStrokeSize);
        mCropGridPaint.setColor(cropGridColor);

        mCropGridRowCount = a.getInt(R.styleable.ucrop_UCropView_ucrop_grid_row_count, DEFAULT_CROP_GRID_ROW_COUNT);
        mCropGridColumnCount = a.getInt(R.styleable.ucrop_UCropView_ucrop_grid_column_count, DEFAULT_CROP_GRID_COLUMN_COUNT);
    }

}
