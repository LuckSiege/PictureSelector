package com.luck.picture.lib.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：ViewModel with Global Changes
 */
private const val KEY_EDITOR_LIVE_DATA = "key_editor_live_data"
private const val KEY_RESULT_LIVE_DATA = "key_result_live_data"
private const val KEY_ORIGINAL_LIVE_DATA = "key_original_live_data"

class GlobalViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    /**
     *  The original drawing options have changed
     */
    fun getOriginalLiveData(): MutableLiveData<Boolean> {
        return state.getLiveData(KEY_ORIGINAL_LIVE_DATA)
    }

    fun setOriginalLiveData(isOriginal: Boolean) {
        getOriginalLiveData().value = isOriginal
    }

    /**
     *  selected result change
     */
    fun getSelectResultLiveData(): MutableLiveData<LocalMedia> {
        return state.getLiveData(KEY_RESULT_LIVE_DATA)
    }

    fun setSelectResultLiveData(media: LocalMedia) {
        getSelectResultLiveData().value = media
    }

    /**
     *  editor result change
     */
    fun getEditorLiveData(): MutableLiveData<LocalMedia> {
        return state.getLiveData(KEY_EDITOR_LIVE_DATA)
    }

    fun setEditorLiveData(media: LocalMedia) {
        getEditorLiveData().value = media
    }
}