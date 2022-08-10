package com.sarabyeet.toget.ui.fragments.profile

import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.addHeaderModel
import com.sarabyeet.toget.databinding.ModelCategoryEntityBinding
import com.sarabyeet.toget.databinding.ModelEmptyButtonBinding
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.ui.CategoryEntityActions
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class ProfileEpoxyController(
    private val onCategoryEmptyStateClicked: () -> Unit,
    private val categoryEntityActions: CategoryEntityActions,
) : EpoxyController() {

    var categories = emptyList<CategoryEntity>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        addHeaderModel("Categories")

        categories.forEach {
            CategoryEntityEpoxy(it, categoryEntityActions).id(it.id).addTo(this)
        }

        EmptyButtonModel("Add Category", onCategoryEmptyStateClicked)
            .id("add-category")
            .addTo(this)
    }
}

data class CategoryEntityEpoxy(
    val category: CategoryEntity,
    val categoryEntityActions: CategoryEntityActions,
) : ViewBindingKotlinModel<ModelCategoryEntityBinding>(R.layout.model_category_entity) {
    override fun ModelCategoryEntityBinding.bind() {
        categoryTextView.text = category.name
        root.setOnClickListener {
            categoryEntityActions.onClickCategory(category)
        }
    }
}

data class EmptyButtonModel(
    val buttonText: String,
    val onClicked: () -> Unit,
) : ViewBindingKotlinModel<ModelEmptyButtonBinding>(R.layout.model_empty_button) {
    override fun ModelEmptyButtonBinding.bind() {
        modelEmptyButton.text = buttonText
        modelEmptyButton.setOnClickListener { onClicked.invoke() }
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }
}