package com.luck.picture.lib.component

import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
/**
 * @author：luck
 * @date：2023/1/4 3:52 下午
 * @describe：AbsPreviewComponent
 */
interface IBasePreviewComponent {
    fun bindData(config: SelectorConfig, media: LocalMedia)
    fun onViewAttachedToWindow()
    fun onViewDetachedFromWindow()
    fun release()
}