package com.luck.picture.lib.config;

import android.content.Context;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureAlbumAdapter;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.adapter.holder.PreviewGalleryAdapter;

/**
 * @author：luck
 * @date：2021/12/23 10:50 上午
 * @describe：InjectResourceSource
 */
public final class InjectResourceSource {
    /**
     * default layout
     */
    public static final int DEFAULT_LAYOUT_RESOURCE = 0;

    /**
     * {@link PictureSelectorFragment}  layout
     * {@link R.layout.ps_fragment_selector}
     */
    public static final int MAIN_SELECTOR_LAYOUT_RESOURCE = 1;
    /**
     * {@link PictureSelectorPreviewFragment} preview layout
     * {@link R.layout.ps_fragment_preview}
     */
    public static final int PREVIEW_LAYOUT_RESOURCE = 2;
    /**
     * {@link PictureImageGridAdapter}  image adapter item layout
     * {@link R.layout.ps_item_grid_image}
     */
    public static final int MAIN_ITEM_IMAGE_LAYOUT_RESOURCE = 3;
    /**
     * {@link PictureImageGridAdapter}  video adapter item layout
     * {@link R.layout.ps_item_grid_video}
     */
    public static final int MAIN_ITEM_VIDEO_LAYOUT_RESOURCE = 4;
    /**
     * {@link PictureImageGridAdapter}  audio adapter item layout
     * {@link R.layout.ps_item_grid_audio}
     */
    public static final int MAIN_ITEM_AUDIO_LAYOUT_RESOURCE = 5;

    /**
     * {@link PictureAlbumAdapter} adapter item layout
     * {@link R.layout.ps_album_folder_item}
     */
    public static final int ALBUM_ITEM_LAYOUT_RESOURCE = 6;

    /**
     * {@link PicturePreviewAdapter} preview adapter item layout
     * {@link R.layout.ps_preview_image}
     */
    public static final int PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE = 7;

    /**
     * {@link PicturePreviewAdapter} preview adapter item layout
     * {@link R.layout.ps_preview_video}
     */
    public static final int PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE = 8;

    /**
     * {@link PreviewGalleryAdapter} preview gallery adapter item layout
     * {@link R.layout.ps_preview_gallery_item}
     */
    public static final int PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE = 9;

    /**
     * {@link PicturePreviewAdapter} preview adapter item layout
     * {@link R.layout.ps_preview_audio}
     */
    public static final int PREVIEW_ITEM_AUDIO_LAYOUT_RESOURCE = 10;

    /**
     * getLayoutResource
     *
     * @param context
     * @param resourceSource {@link InjectResourceSource}
     * @return
     */
    public static int getLayoutResource(Context context, int resourceSource, SelectorConfig selectorConfig) {
        if (selectorConfig != null && selectorConfig.onLayoutResourceListener != null) {
            return selectorConfig.onLayoutResourceListener.getLayoutResourceId(context, resourceSource);
        }
        return DEFAULT_LAYOUT_RESOURCE;
    }
}
