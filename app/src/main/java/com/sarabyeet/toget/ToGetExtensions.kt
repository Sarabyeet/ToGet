package com.sarabyeet.toget

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.airbnb.epoxy.EpoxyController
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