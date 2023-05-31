package com.luck.picture.lib.config

import com.luck.picture.lib.*
import com.luck.picture.lib.adapter.MediaAlbumAdapter
import com.luck.picture.lib.adapter.MediaListAdapter
import com.luck.picture.lib.adapter.MediaPreviewAdapter
import com.luck.picture.lib.dialog.AlbumListPopWindow

/**
 * @author：luck
 * @date：2021/11/23 6:53 下午
 * @describe：PictureSelector Layout Source,
 * When reloading these layouts,the View # ID must remain consistent
 */
enum class LayoutSource {
    /**
     * User Overload [SelectorMainFragment]#[R.layout.ps_fragment_selector]
     */
    SELECTOR_MAIN,

    /**
     * User Overload [SelectorNumberMainFragment]#[R.layout.ps_fragment_number_selector]
     */
    SELECTOR_NUMBER_MAIN,

    /**
     * User Overload [SelectorPreviewFragment]#[R.layout.ps_fragment_preview]
     */
    SELECTOR_PREVIEW,

    /**
     * User Overload [SelectorNumberPreviewFragment]#[R.layout.ps_fragment_number_preview]
     */
    SELECTOR_NUMBER_PREVIEW,

    /**
     * User Overload [SelectorNumberPreviewFragment.GalleryAdapter]#[R.layout.ps_preview_gallery_item]
     */
    SELECTOR_NUMBER_PREVIEW_GALLERY,

    /**
     * User Overload [SelectorExternalPreviewFragment]#[R.layout.ps_fragment_external_preview]
     */
    SELECTOR_EXTERNAL_PREVIEW,

    /**
     * User Overload [AlbumListPopWindow]#[R.layout.ps_album_window]
     */
    ALBUM_WINDOW,

    /**
     * User Overload [MediaAlbumAdapter]#[R.layout.ps_album_item]
     */
    ALBUM_WINDOW_ITEM,

    /**
     * User Overload [MediaListAdapter]#[R.layout.ps_item_grid_camera]
     */
    ADAPTER_ITEM_CAMERA,

    /**
     * User Overload [MediaListAdapter]#[R.layout.ps_item_grid_image]
     */
    ADAPTER_ITEM_IMAGE,

    /**
     * User Overload [MediaListAdapter]#[R.layout.ps_item_grid_video]
     */
    ADAPTER_ITEM_VIDEO,

    /**
     * User Overload [MediaListAdapter]#[R.layout.ps_item_grid_audio]
     */
    ADAPTER_ITEM_AUDIO,

    /**
     * User Overload [MediaPreviewAdapter]#[R.layout.ps_preview_image]
     */
    PREVIEW_ITEM_IMAGE,

    /**
     * User Overload [MediaPreviewAdapter]#[R.layout.ps_preview_video]
     */
    PREVIEW_ITEM_VIDEO,

    /**
     * User Overload [MediaPreviewAdapter]#[R.layout.ps_preview_audio]
     */
    PREVIEW_ITEM_AUDIO,
}