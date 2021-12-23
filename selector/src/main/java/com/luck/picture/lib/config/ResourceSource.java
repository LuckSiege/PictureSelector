package com.luck.picture.lib.config;

import com.luck.picture.lib.PictureSelectorFragment;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.adapter.PictureAlbumAdapter;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;

/**
 * @author：luck
 * @date：2021/12/23 10:50 上午
 * @describe：ResourceSource
 */
public final class ResourceSource {
    /**
     * {@link PictureSelectorFragment}  layout
     */
    public static final int MAIN_SELECTOR_LAYOUT_RESOURCE = 1;
    /**
     * {@link PictureSelectorPreviewFragment} preview layout
     */
    public static final int PREVIEW_LAYOUT_RESOURCE = 2;
    /**
     * {@link PictureImageGridAdapter}  image adapter item layout
     */
    public static final int MAIN_ADAPTER_ITEM_IMAGE_LAYOUT_RESOURCE = 3;
    /**
     * {@link PictureImageGridAdapter}  video adapter item layout
     */
    public static final int MAIN_ADAPTER_ITEM_VIDEO_LAYOUT_RESOURCE = 4;
    /**
     * {@link PictureImageGridAdapter}  audio adapter item layout
     */
    public static final int MAIN_ADAPTER_ITEM_AUDIO_LAYOUT_RESOURCE = 5;

    /**
     * {@link PictureAlbumAdapter} adapter item layout
     */
    public static final int ALBUM_ADAPTER_ITEM_LAYOUT_RESOURCE = 6;

    /**
     * {@link PicturePreviewAdapter} preview adapter item layout
     */
    public static final int PREVIEW_ADAPTER_ITEM_IMAGE_LAYOUT_RESOURCE = 7;

    /**
     * {@link PicturePreviewAdapter} preview adapter item layout
     */
    public static final int PREVIEW_ADAPTER_ITEM_VIDEO_LAYOUT_RESOURCE = 8;
}
