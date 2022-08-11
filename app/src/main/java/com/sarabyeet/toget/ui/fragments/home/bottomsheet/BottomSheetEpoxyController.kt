package com.sarabyeet.toget.ui.fragments.home.bottomsheet

import android.graphics.Typeface
import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.ModelSortOrderItemBinding
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class BottomSheetEpoxyController(
    private val sort: Array<ToGetEvents.HomeViewState.Sort>,
    private val currentSort: ToGetEvents.HomeViewState.Sort,
    private val selectedCallback: (ToGetEvents.HomeViewState.Sort) -> Unit,
) : EpoxyController() {
    override fun buildModels() {

        sort.forEach {
            val selectedSort = it.name == currentSort.name
            SortOrderEpoxyModel(it, selectedSort, selectedCallback).id(it.displayName).addTo(this)
        }
    }

    data class SortOrderEpoxyModel(
        val sort: ToGetEvents.HomeViewState.Sort,
        val isSelected: Boolean,
        val selectedCallback: (ToGetEvents.HomeViewState.Sort) -> Unit,
    ) : ViewBindingKotlinModel<ModelSortOrderItemBinding>(R.layout.model_sort_order_item) {
        override fun ModelSortOrderItemBinding.bind() {
            textView.text = sort.displayName
            root.setOnClickListener { selectedCallback.invoke(sort) }

            // Highlighting the selected sort
            highLight.isVisible = isSelected
            textView.textSize = if (isSelected) 18F else 14F
            val typeface = if (isSelected) Typeface.BOLD else Typeface.NORMAL
            textView.setTypeface(textView.typeface, typeface)
        }
    }
}