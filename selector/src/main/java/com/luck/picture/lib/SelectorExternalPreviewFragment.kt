package com.luck.picture.lib

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.config.LayoutSource
import com.luck.picture.lib.dialog.PictureCommonDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnLongClickListener
import com.luck.picture.lib.provider.TempDataProvider
import com.luck.picture.lib.utils.FileUtils
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.net.URL
import kotlin.coroutines.resume

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：For external preview
 */
open class SelectorExternalPreviewFragment : SelectorPreviewFragment() {

    override fun getFragmentTag(): String {
        return SelectorExternalPreviewFragment::class.java.simpleName
    }

    override fun getResourceId(): Int {
        return config.layoutSource[LayoutSource.SELECTOR_EXTERNAL_PREVIEW]
            ?: R.layout.ps_fragment_external_preview
    }

    private lateinit var ivDelete: ImageView


    override fun initViews(view: View) {
        super.initViews(view)
        ivDelete = view.findViewById(R.id.ps_iv_delete)
        ivDelete.visibility =
            if (config.previewWrap.isDisplayDelete) View.VISIBLE else View.GONE
    }

    override fun initWidgets() {
        ivDelete.setOnClickListener {
            delete()
        }
        mAdapter.setOnLongClickListener(object : OnLongClickListener<LocalMedia> {
            override fun onLongClick(
                holder: RecyclerView.ViewHolder,
                position: Int,
                data: LocalMedia
            ) {
                if (getPreviewWrap().isDownload) {
                    if (config.mListenerInfo.onExternalPreviewListener?.onLongPressDownload(
                            requireContext(),
                            data
                        ) != true
                    ) {
                        download(data)
                    }
                }
            }
        })
    }

    open fun delete() {
        val currentItem = viewPager.currentItem
        val media = getPreviewWrap().source[currentItem]
        config.mListenerInfo.onExternalPreviewListener?.onDelete(
            requireContext(),
            currentItem,
            media
        )
        getPreviewWrap().source.removeAt(currentItem)
        getPreviewWrap().totalCount -= 1
        getPreviewWrap().position = viewPager.currentItem
        if (getPreviewWrap().totalCount > 0) {
            viewPager.setCurrentItem(viewPager.currentItem, false)
            setTitleText(getPreviewWrap().position + 1)
            mAdapter.notifyItemRangeChanged(0, getPreviewWrap().source.size)
        } else {
            onBackPressed()
        }
    }

    open fun download(media: LocalMedia) {
        val availablePath = media.getAvailablePath() ?: return
        val mimeType = if (MediaUtils.isHasHttp(availablePath)) {
            media.mimeType ?: MediaUtils.getUrlMimeType(availablePath) ?: return
        } else {
            media.mimeType ?: MediaUtils.getMimeType(availablePath) ?: return
        }
        val content =
            when {
                MediaUtils.hasMimeTypeOfVideo(mimeType) -> {
                    getString(R.string.ps_prompt_video_content)
                }
                MediaUtils.hasMimeTypeOfAudio(mimeType) -> {
                    getString(R.string.ps_prompt_audio_content);
                }
                else -> {
                    getString(R.string.ps_prompt_image_content);
                }
            }
        val context = requireContext()
        PictureCommonDialog.showDialog(context, getString(R.string.ps_prompt), content)
            .setOnDialogEventListener(object : PictureCommonDialog.OnDialogEventListener {
                override fun onConfirm() {
                    viewModel.viewModelScope.launch {
                        if (MediaUtils.isHasHttp(availablePath)) {
                            showLoading()
                        }
                        val result = downloadFile(availablePath, mimeType)
                        if (TextUtils.isEmpty(result)) {
                            ToastUtils.showMsg(context, getString(R.string.ps_save_error))
                        } else {
                            viewModel.scanFile(result, null)
                            ToastUtils.showMsg(
                                context, "${getString(R.string.ps_save_success)}\n$result"
                            )
                        }
                        if (MediaUtils.isHasHttp(availablePath)) {
                            dismissLoading()
                        }
                    }
                }
            })
    }

    private suspend fun downloadFile(path: String, mimeType: String): String {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine {
                val context = requireContext()
                val contentResolver = context.contentResolver
                createInsertUri(context, path, mimeType)?.let { outputUri ->
                    contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                        when {
                            MediaUtils.isHasHttp(path) -> {
                                URL(path).openStream()
                            }
                            MediaUtils.isContent(path) -> {
                                contentResolver.openInputStream(Uri.parse(path))
                            }
                            else -> {
                                FileInputStream(path)
                            }
                        }?.use { inputStream ->
                            if (FileUtils.writeFileFromIS(inputStream, outputStream)) {
                                it.resume(MediaUtils.getPath(context, outputUri) ?: "")
                            } else {
                                it.resume("")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createInsertUri(
        context: Context,
        path: String,
        mimeType: String
    ): Uri? {
        val values = ContentValues(3)
        when {
            MediaUtils.hasMimeTypeOfVideo(mimeType) -> {
                val postfix = MediaUtils.getPostfix(context, path, "mp4")
                val fileName = "${FileUtils.createFileName("VID")}.$postfix"
                values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/$postfix")
                if (SdkVersionUtils.isQ()) {
                    values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
                    values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                } else {
                    val dir = if (TextUtils.equals(
                            Environment.getExternalStorageState(),
                            Environment.MEDIA_MOUNTED
                        )
                    ) Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES
                    ) else context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                    values.put(MediaStore.MediaColumns.DATA, "${dir}${File.separator}${fileName}")
                }
                return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    context.contentResolver.insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                } else {
                    context.contentResolver.insert(
                        MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                        values
                    )
                }
            }
            MediaUtils.hasMimeTypeOfAudio(mimeType) -> {
                val postfix = MediaUtils.getPostfix(context, path, "amr")
                val fileName = "${FileUtils.createFileName("AUD")}.$postfix"
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/$postfix")
                if (SdkVersionUtils.isQ()) {
                    values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis())
                    values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                } else {
                    val dir = if (TextUtils.equals(
                            Environment.getExternalStorageState(),
                            Environment.MEDIA_MOUNTED
                        )
                    ) Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC
                    ) else context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    values.put(MediaStore.MediaColumns.DATA, "${dir}${File.separator}${fileName}")
                }
                return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    context.contentResolver.insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                } else {
                    context.contentResolver.insert(
                        MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                        values
                    )
                }
            }
            else -> {
                val postfix = MediaUtils.getPostfix(context, path, "jpg")
                val fileName = "${FileUtils.createFileName("IMG")}.$postfix"
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/$postfix")
                if (SdkVersionUtils.isQ()) {
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
                } else {
                    val dir = if (TextUtils.equals(
                            Environment.getExternalStorageState(),
                            Environment.MEDIA_MOUNTED
                        )
                    ) Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ) else context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    values.put(MediaStore.MediaColumns.DATA, "${dir}${File.separator}${fileName}")
                }
                return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                } else {
                    context.contentResolver.insert(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        values
                    )
                }
            }
        }
    }

}