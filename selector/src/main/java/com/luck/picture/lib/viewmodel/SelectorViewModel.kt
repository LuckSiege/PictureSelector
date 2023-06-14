package com.luck.picture.lib.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.entity.PreviewDataWrap
import com.luck.picture.lib.loader.impl.MediaPagingLoaderImpl
import com.luck.picture.lib.media.SelectorMediaScannerConnection
import com.luck.picture.lib.media.ScanListener
import com.luck.picture.lib.provider.SelectorProviders
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：Basic ViewModel
 */
private const val KEY_PAGE = "key_page"
private const val KEY_OUTPUT_DATA = "key_output_uri"

class SelectorViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {
    /**
     * Page of request
     */
    var page: Int = 1

    /**
     *  camera output uri
     */
    fun setOutputUri(uri: Uri?) {
        state[KEY_OUTPUT_DATA] = uri
    }

    fun getOutputUri(): Uri? {
        return state[KEY_OUTPUT_DATA]
    }

    /**
     * Preview data wrap
     */
    var previewWrap: PreviewDataWrap = PreviewDataWrap()

    /**
     * Current Selected album
     */
    var currentMediaAlbum: LocalMediaAlbum? = null

    /**
     * Current apply permission
     */
    var currentRequestPermission = arrayOf<String>()

    /**
     * PictureSelector Config
     */
    private val config = SelectorProviders.getInstance().getSelectorConfig()

    /**
     * Media loader
     */
    private val mediaLoader = config.dataLoader ?: MediaPagingLoaderImpl(application)

    /**
     * Media list data LiveData
     */
    private val _medias = MutableLiveData<MutableList<LocalMedia>>()
    val mediaLiveData: LiveData<MutableList<LocalMedia>> get() = _medias

    /**
     *  Album list data LiveData
     */
    private val _albums = MutableLiveData<MutableList<LocalMediaAlbum>>()
    val albumLiveData: LiveData<MutableList<LocalMediaAlbum>> get() = _albums

    /**
     * Refresh System Album
     */
    fun scanFile(path: String?, l: ScanListener?) {
        SelectorMediaScannerConnection(getApplication(), path, object : ScanListener {
            override fun onScanFinish() {
                l?.onScanFinish()
            }
        })
    }

    /**
     * Query local album list [LocalMediaAlbum]
     */
    fun loadMediaAlbum() {
        viewModelScope.launch {
            _albums.postValue(mediaLoader.loadMediaAlbum())
        }
    }


    /**
     * Query the data on the first page of [LocalMedia]
     */
    fun loadMedia() {
        val bucketId = currentMediaAlbum?.bucketId ?: SelectorConstant.DEFAULT_ALL_BUCKET_ID
        viewModelScope.launch {
            page = 1
            _medias.postValue(
                mediaLoader.loadMedia(bucketId, SelectorConstant.DEFAULT_MAX_PAGE_SIZE)
            )
        }
    }

    /**
     * Query more data of [LocalMedia]
     */
    fun loadMediaMore(bucketId: Long) {
        viewModelScope.launch {
            page++
            _medias.postValue(mediaLoader.loadMediaMore(bucketId, page, config.pageSize))
        }
    }

    /**
     * Query media resources in the specified directory
     */
    fun loadAppInternalDir(@NotNull sandboxDir: String) {
        viewModelScope.launch {
            page = 1
            _medias.postValue(mediaLoader.loadAppInternalDir(sandboxDir))
        }
    }
}