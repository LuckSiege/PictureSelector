package com.luck.picture.lib.player

import android.content.Context
import android.view.TextureView

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：Player TextureView
 */
class VideoTextureView(context: Context) : TextureView(context) {
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var mVideoRotation = 0

    fun adjustVideoSize(videoWidth: Int, videoHeight: Int, videoRotation: Int) {
        this.mVideoWidth = videoWidth
        this.mVideoHeight = videoHeight
        this.mVideoRotation = videoRotation
        this.rotation = mVideoRotation.toFloat()
        this.requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = getDefaultSize(mVideoHeight, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize
                height = heightSpecSize
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                width = widthSpecSize
                height = width * mVideoHeight / mVideoWidth
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                height = heightSpecSize
                width = height * mVideoWidth / mVideoHeight
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize
                }
            } else {
                width = mVideoWidth
                height = mVideoHeight
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize
                    width = height * mVideoWidth / mVideoHeight
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize
                    height = width * mVideoHeight / mVideoWidth
                }
            }
        }
        setMeasuredDimension(width, height)
        if ((mVideoRotation + 180) % 180 != 0) {
            val size = scaleSize(widthSpecSize, heightSpecSize, height, width)
            scaleX = size[0] / height.toFloat()
            scaleY = size[1] / width.toFloat()
        }
    }

    private fun scaleSize(
        textureWidth: Int,
        textureHeight: Int,
        realWidth: Int,
        realHeight: Int
    ): IntArray {
        val deviceRate = textureWidth.toFloat() / textureHeight.toFloat()
        val rate = realWidth.toFloat() / realHeight.toFloat()
        val width: Int
        val height: Int
        if (rate < deviceRate) {
            height = textureHeight
            width = (textureHeight * rate).toInt()
        } else {
            width = textureWidth
            height = (textureWidth / rate).toInt()
        }
        return intArrayOf(width, height)
    }
}