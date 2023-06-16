package com.luck.pictureselector.custom

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider.getUriForFile
import com.luck.picture.lib.adapter.PreviewVideoHolder
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils.isMaxN
import com.luck.picture.lib.utils.SdkVersionUtils.isQ
import java.io.File

/**
 * @author：luck
 * @date：2023/1/4 4:55 下午
 * @describe：CustomPreviewSystemVideoHolder
 */
class CustomPreviewSystemVideoHolder(itemView: View) : PreviewVideoHolder(itemView) {

    override fun dispatchPlay(path: String, displayName: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val isParseUri = MediaUtils.isContent(path) || MediaUtils.isHasHttp(path)
        val data: Uri = if (isQ()) {
            if (isParseUri) Uri.parse(path) else Uri.fromFile(File(path))
        } else if (isMaxN()) {
            if (isParseUri) Uri.parse(path) else getUriForFile(
                itemView.context,
                itemView.context.packageName + ".luckProvider",
                File(path)
            )
        } else {
            if (isParseUri) Uri.parse(path) else Uri.fromFile(File(path))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(data, "video/*")
        itemView.context.startActivity(intent)
    }
}