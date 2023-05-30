package com.luck.pictureselector

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.helper.ActivityCompatHelper

/**
 * @author：luck
 * @date：2022-5-24 22:30
 * @describe：Glide图片加载
 */
class GlideEngine : ImageEngine {

    override fun loadImage(context: Context, url: String?, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url).into(imageView)
    }

    override fun loadImage(
        context: Context,
        url: String?,
        width: Int,
        height: Int,
        imageView: ImageView
    ) {
        Glide.with(context).load(url).override(width, height).into(imageView)
    }

    override fun loadAlbumCover(context: Context, url: String?, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url)
            .override(180, 180)
            .transform(CenterCrop(), RoundedCorners(8))
            .placeholder(R.drawable.ps_image_placeholder)
            .into(imageView)
    }

    override fun loadListImage(context: Context, url: String?, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url)
            .override(300, 300)
            .centerCrop()
            .placeholder(R.drawable.ps_image_placeholder)
            .into(imageView)
    }

    override fun pauseRequests(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).pauseRequests()
    }

    override fun resumeRequests(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).resumeRequests()
    }

    companion object {
        fun create() = InstanceHelper.engine
    }

    object InstanceHelper {
        val engine = GlideEngine()
    }
}