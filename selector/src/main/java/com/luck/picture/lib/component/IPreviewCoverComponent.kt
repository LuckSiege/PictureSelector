package com.luck.picture.lib.component

import android.widget.ImageView

/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：Preview components with cover
 */
interface IPreviewCoverComponent : IBasePreviewComponent {
    fun getImageCover(): ImageView
}