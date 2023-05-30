package com.luck.picture.lib.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.luck.picture.lib.constant.SelectorConstant
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaAlbum
import com.luck.picture.lib.entity.PreviewDataWrap
import com.luck.picture.lib.factory.ClassFactory
import com.luck.picture.lib.loader.impl.MediaPagingLoaderImpl
import com.luck.picture.lib.provider.SelectorProviders
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：Basic ViewModel
 */
class SelectorViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Page of request
     */
    var page: Int = 1

    /**
     *  camera output uri
     */
    var outputUri: Uri? = null

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
    val config = SelectorProviders.getInstance().getSelectorConfig()

    /**
     * Class Factory
     */
    val factory = ClassFactory.NewInstance()

    /**
     * PictureSelector UI style
     */
    val selectorStyle = config.selectorStyle

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