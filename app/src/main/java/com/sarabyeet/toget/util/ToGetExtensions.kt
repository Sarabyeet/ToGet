package com.sarabyeet.toget.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyController
import com.google.android.material.color.MaterialColors
import com.sarabyeet.toget.ui.epoxy.ItemHeaderEpoxy

fun EpoxyController.addHeaderModel(headerText: String){
    ItemHeaderEpoxy(headerText).id("${headerText}_priority").addTo(this)
}

fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

@ColorInt
fun View.getAttrColor(attrResId: Int): Int {
    return MaterialColors.getColor(this, attrResId)
}
