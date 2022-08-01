package com.sarabyeet.toget.ui.epoxy

import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.ModelItemHeaderBinding
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

data class ItemHeaderEpoxy(
    val headerText: String,
) : ViewBindingKotlinModel<ModelItemHeaderBinding>(R.layout.model_item_header) {
    override fun ModelItemHeaderBinding.bind() {
        headerTitle.text = headerText
    }
}