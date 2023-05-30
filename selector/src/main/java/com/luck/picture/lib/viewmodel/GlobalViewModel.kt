package com.luck.picture.lib.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/11/22 5:58 下午
 * @describe：ViewModel with Global Changes
 */
class GlobalViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Use original resources
     */
    var isOriginal: Boolean = false

    /**
     * select result
     */
    val selectResult = mutableListOf<LocalMedia>()

    /**
     *  The original drawing options have changed
     */
    val originalLiveData = MutableLiveData<Boolean>()

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
        isOriginal = false
    }
}