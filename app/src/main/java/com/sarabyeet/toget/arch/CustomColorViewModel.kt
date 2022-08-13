package com.sarabyeet.toget.arch

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

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

    fun setPriorityName(priorityName: String, colorInt: Int, progressCallback: (Int, Int, Int) -> Unit) {
        this.priorityName = priorityName
        val color = Color.valueOf(colorInt)
        val redColor = (color.red() * 255).roundToInt()
        val greenColor = (color.green() * 255).roundToInt()
        val blueColor = (color.blue() * 255).roundToInt()

        progressCallback(redColor, greenColor, blueColor)

        _viewStateLiveData.postValue(
            ViewState(
                red = redColor,
                green = greenColor,
                blue = blueColor,
                priorityName = priorityName
            )
        )
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