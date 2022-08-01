package com.sarabyeet.toget

import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.ui.epoxy.ItemHeaderEpoxy

fun EpoxyController.addHeaderModel(headerText: String){
    ItemHeaderEpoxy(headerText).id("${headerText}_priority").addTo(this)
}