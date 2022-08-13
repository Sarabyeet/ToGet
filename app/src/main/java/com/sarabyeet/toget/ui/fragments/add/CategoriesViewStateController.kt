package com.sarabyeet.toget.ui.fragments.add

import android.content.res.ColorStateList
import android.graphics.Typeface
import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.ModelCategoryViewStateItemBinding
import com.sarabyeet.toget.util.getAttrColor
import com.sarabyeet.toget.ui.epoxy.LoadingEpoxyModel
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class CategoriesViewStateController(
    private val onItemSelected: (String) -> Unit,
) : EpoxyController() {

    var viewState = ToGetEvents.CategoriesViewState()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        if (viewState.isLoading) {
            LoadingEpoxyModel().id("loading-state").addTo(this)
            return
        }

        viewState.itemList.forEach { item ->
            CategoryViewStateItem(item, onItemSelected).id(item.categoryEntity.id).addTo(this)
        }
    }

    data class CategoryViewStateItem(
        val item: ToGetEvents.CategoriesViewState.Item,
        val onClick: (String) -> Unit,
    ) : ViewBindingKotlinModel<ModelCategoryViewStateItemBinding>(R.layout.model_category_view_state_item) {
        override fun ModelCategoryViewStateItemBinding.bind() {

            val colorRes =
                if (item.isSelected)
                    com.airbnb.viewmodeladapter.R.attr.colorSecondary
                else
                    androidx.appcompat.R.attr.colorPrimary
            categoryTextView.text = item.categoryEntity.name
            categoryTextView.setTextColor(root.getAttrColor(colorRes))

            val setTypeface = if (item.isSelected) Typeface.BOLD else Typeface.ITALIC
            categoryTextView.setTypeface(categoryTextView.typeface, setTypeface)

            root.setStrokeColor(ColorStateList.valueOf(root.getAttrColor(colorRes)))
            root.setOnClickListener { onClick.invoke(item.categoryEntity.id) }
            root.strokeWidth = if (item.isSelected) 6 else 3
        }
    }

}