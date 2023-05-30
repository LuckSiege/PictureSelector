package com.luck.picture.lib.engine

import android.content.Context
import android.widget.ImageView

/**
 * @author：luck
 * @date：2019-11-13 16:59
 * @describe：ImageEngine
 */
interface ImageEngine {
    /**
     * Load images
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context, url: String?, imageView: ImageView)

    /**
     * Load images, support maximum size
     *
     * @param context
     * @param url
     * @param width
     * @param height
     * @param imageView
     */
    fun loadImage(context: Context, url: String?, width: Int, height: Int, imageView: ImageView)

    /**
     * Load album list thumbnails
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadAlbumCover(context: Context, url: String?, imageView: ImageView)

    /**
     * Load List Thumbnails
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadListImage(context: Context, url: String?, imageView: ImageView)

    /**
     * When the recyclerview slides quickly, the callback can be used to pause the loading of resources
     *
     * @param context
     */
    fun pauseRequests(context: Context)

    /**
     * When the recyclerview is slow or stops sliding, the callback can do some operations to restore resource loading
     *
     * @param context
     */
    fun resumeRequests(context: Context)
}