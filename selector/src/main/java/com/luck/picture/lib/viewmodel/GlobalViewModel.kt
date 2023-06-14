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
private const val KEY_ORIGINAL_LIVE_DATA = "key_original_live_data"

class GlobalViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    /**
     *  The original drawing options have changed
     */
    fun getOriginalLiveData(): MutableLiveData<Boolean> {
        if (!state.contains(KEY_ORIGINAL_LIVE_DATA)) {
            state[KEY_ORIGINAL_LIVE_DATA] = false
        }
        return state.getLiveData(KEY_ORIGINAL_LIVE_DATA, false)
    }

    fun setOriginalLiveData(isOriginal: Boolean) {
        getOriginalLiveData().value = isOriginal
    }

    /**
     * select result
     */
    val selectResult = mutableListOf<LocalMedia>()

    /**
     *  selected result change
     */
    val selectResultLiveData = MutableLiveData<LocalMedia>()

    /**
     *  editor result change
     */
    val editorLiveData = MutableLiveData<LocalMedia>()

    /**
     * reset data
     */
    fun reset() {
        selectResult.clear()
    }
}