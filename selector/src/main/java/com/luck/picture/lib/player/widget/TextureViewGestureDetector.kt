package com.luck.picture.lib.player.widget

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.ViewConfiguration
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Does a whole lot of gesture detecting.
 */
internal class TextureViewGestureDetector(context: Context?, listener: OnTextureViewGestureListener) {
    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0
    private val mDetector: ScaleGestureDetector
    private var mVelocityTracker: VelocityTracker? = null
    var isDragging = false
    private var mLastTouchX = 0f
    private var mLastTouchY = 0f
    private val mTouchSlop: Float
    private val mMinimumVelocity: Float
    private val mListener: OnTextureViewGestureListener
    private fun getActiveX(ev: MotionEvent): Float {
        return try {
            ev.getX(mActivePointerIndex)
        } catch (e: Exception) {
            ev.x
        }
    }

    private fun getActiveY(ev: MotionEvent): Float {
        return try {
            ev.getY(mActivePointerIndex)
        } catch (e: Exception) {
            ev.y
        }
    }

    val isScaling: Boolean
        get() = mDetector.isInProgress

    fun onTouchEvent(ev: MotionEvent): Boolean {
        return try {
            mDetector.onTouchEvent(ev)
            processTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // Fix for support lib bug, happening when onDestroy is called
            true
        }
    }

    private fun processTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mVelocityTracker = VelocityTracker.obtain()
                if (null != mVelocityTracker) {
                    mVelocityTracker?.addMovement(ev)
                }
                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    isDragging = sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
                }
                if (isDragging) {
                    mListener.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y
                    if (null != mVelocityTracker) {
                        mVelocityTracker?.addMovement(ev)
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                if (isDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        mVelocityTracker?.addMovement(ev)
                        mVelocityTracker?.computeCurrentVelocity(1000)
                        val vX = mVelocityTracker?.xVelocity ?: 0F
                        val vY = mVelocityTracker?.yVelocity ?: 0F

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (abs(vX).coerceAtLeast(abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY)
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex =
                    TextureViewUtils.getPointerIndex(ev.action)
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                }
            }
        }
        mActivePointerIndex = ev
            .findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID) mActivePointerId else 0)
        return true
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        val configuration = ViewConfiguration
            .get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
        mListener = listener
        val mScaleListener: ScaleGestureDetector.OnScaleGestureListener =
            object : ScaleGestureDetector.OnScaleGestureListener {
                private var lastFocusX = 0f
                private var lastFocusY = 0f
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor
                    if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) return false
                    if (scaleFactor >= 0) {
                        mListener.onScale(
                            scaleFactor,
                            detector.focusX,
                            detector.focusY,
                            detector.focusX - lastFocusX,
                            detector.focusY - lastFocusY
                        )
                        lastFocusX = detector.focusX
                        lastFocusY = detector.focusY
                    }
                    return true
                }

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    lastFocusX = detector.focusX
                    lastFocusY = detector.focusY
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    // NO-OP
                }
            }
        mDetector = ScaleGestureDetector(context, mScaleListener)
    }
}