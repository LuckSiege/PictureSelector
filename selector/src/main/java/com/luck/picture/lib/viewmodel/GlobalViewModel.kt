package com.luck.picture.lib.viewmodel

import android.app.Application
import android.os.Bundle
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
private const val KEY_SELECT_RESULT_LIVE_DATA = "key_select_result_live_data"

class GlobalViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    /**
     *  The original drawing options have changed
     */
    fun getOriginalLiveData(): MutableLiveData<Boolean> {
        return state.getLiveData(KEY_ORIGINAL_LIVE_DATA, false)
    }

    fun setOriginalLiveData(isOriginal: Boolean) {
        getOriginalLiveData().value = isOriginal
    }

    /**
     * select result
     */
    var selectResult = mutableListOf<LocalMedia>()

    /**
     *  selected result change
     */
    val selectResultLiveData = MutableLiveData<LocalMedia>()

    /**
     *  editor result change
     */
    val editorLiveData = MutableLiveData<LocalMedia>()

    fun saveResult() {
        state[KEY_SELECT_RESULT_LIVE_DATA] = selectResult
    }

    fun restoreResult(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            selectResult = state[KEY_SELECT_RESULT_LIVE_DATA] ?: selectResult
        }
    }

    /**
     * reset data
     */
    fun reset() {
        selectResult.clear()
    }
}