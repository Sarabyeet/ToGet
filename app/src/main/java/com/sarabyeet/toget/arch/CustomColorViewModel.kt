package com.sarabyeet.toget.arch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomColorViewModel : ViewModel() {

    data class ViewState(
        val red: Int = 0,
        val green: Int = 0,
        val blue: Int = 0,
        val priorityName: String = "",
    ) {
        fun getFormattedTitle(): String {
            return "$priorityName ($red, $green, $blue)"
        }
    }

    private var _viewStateLiveData = MutableLiveData<ViewState>()
    val viewStateLiveData: LiveData<ViewState> = _viewStateLiveData

    private lateinit var priorityName: String

    fun setPriorityName(priorityName: String) {
        this.priorityName = priorityName
    }

    fun onRedChange(value: Int) {
        val viewState = _viewStateLiveData.value ?: ViewState()
        _viewStateLiveData.postValue(viewState.copy(red = value))
    }

    fun onGreenChange(value: Int) {
        val viewState = _viewStateLiveData.value ?: ViewState()
        _viewStateLiveData.postValue(viewState.copy(green = value))
    }

    fun onBlueChange(value: Int) {
        val viewState = _viewStateLiveData.value ?: ViewState()
        _viewStateLiveData.postValue(viewState.copy(blue = value))
    }
}