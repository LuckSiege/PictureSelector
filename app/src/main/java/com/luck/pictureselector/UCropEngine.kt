package com.luck.pictureselector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.helper.ActivityCompatHelper
import com.luck.picture.lib.utils.MediaUtils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import java.io.File

/**
 * @author：luck
 * @date：2022-5-24 22:30
 * @describe：图片裁剪
 */
class UCropEngine : CropEngine {

    override fun onCrop(fragment: Fragment, dataSource: MutableList<LocalMedia>, requestCode: Int) {
        val first = dataSource.first()
        val path = first.getAvailablePath() ?: return
        val sourceUri = if (MediaUtils.isContent(path)) Uri.parse(path) else Uri.fromFile(File(path))
        val destinationUri = Uri.fromFile(
            File(fragment.requireContext().cacheDir, "${System.currentTimeMillis()}.jpg")
        )
        val totalSource = arrayListOf<String>()
        dataSource.forEach { media ->
            totalSource.add(media.getAvailablePath()!!)
        }
        val uCrop = UCrop.of(sourceUri, destinationUri, totalSource)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(
                context: Context,
                url: String,
                imageView: ImageView
            ) {
                if (!ActivityCompatHelper.assertValidRequest(context)) {
                    return
                }
                Glide.with(context).load(url).override(180, 180)
                    .into(imageView)
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap>?
            ) {
                if (!ActivityCompatHelper.assertValidRequest(context)) {
                    return
                }
                Glide.with(context).asBitmap().load(url)
                    .override(maxWidth, maxHeight)
                    .into(object : CustomTarget<Bitmap?>() {

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            call?.onCall(null)
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            call?.onCall(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            call?.onCall(null)
                        }
                    })
            }
        })
        uCrop.start(fragment.requireContext(), fragment, requestCode)
    }
}