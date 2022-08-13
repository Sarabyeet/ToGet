package com.sarabyeet.toget.ui.fragments.profile

import com.airbnb.epoxy.EpoxyController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.ModelCategoryEntityBinding
import com.sarabyeet.toget.databinding.ModelCustomColorItemBinding
import com.sarabyeet.toget.databinding.ModelEmptyButtonBinding
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.ui.ProfileScreenActions
import com.sarabyeet.toget.util.addHeaderModel
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class ProfileEpoxyController(
    private val onCategoryEmptyStateClicked: () -> Unit,
    private val profileScreenActions: ProfileScreenActions,
) : EpoxyController() {

    var categories = emptyList<CategoryEntity>()
        set(value) {
            field = value
            requestModelBuild()
        }

    var highPriority = 0
        set(value) {
            field = value
            requestModelBuild()
        }

    var mediumPriority = 0
        set(value) {
            field = value
            requestModelBuild()
        }

    var lowPriority = 0
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        addHeaderModel("Categories")

        categories.forEach {
            CategoryEntityEpoxy(it, profileScreenActions).id(it.id).addTo(this)
        }

        EmptyButtonModel("Add Category", onCategoryEmptyStateClicked)
            .id("add-category")
            .addTo(this)

        addHeaderModel("Priorities")

        CustomColorItemModel("High", highPriority, profileScreenActions).id("high-priority")
            .addTo(this)
        CustomColorItemModel("Medium", mediumPriority, profileScreenActions).id("medium-priority")
            .addTo(this)
        CustomColorItemModel("Low", lowPriority, profileScreenActions).id("low-priority")
            .addTo(this)
    }
}

data class CategoryEntityEpoxy(
    val category: CategoryEntity,
    val profileScreenActions: ProfileScreenActions,
) : ViewBindingKotlinModel<ModelCategoryEntityBinding>(R.layout.model_category_entity) {
    override fun ModelCategoryEntityBinding.bind() {
        categoryTextView.text = category.name
        root.setOnClickListener {
            profileScreenActions.onClickCategory(category)
        }
        root.setOnLongClickListener {
            MaterialAlertDialogBuilder(it.context)
                .setTitle("Delete ${category.name}?")
                .setNegativeButton(it.resources.getString(R.string.cancel)) { _, _ ->

                }
                .setPositiveButton(it.resources.getString(R.string.ok)) { _, _ ->
                    profileScreenActions.onDeleteCategory(category)
                }
                .show()
            return@setOnLongClickListener true
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

data class CustomColorItemModel(
    val displayText: String,
    val color: Int,
    val profileScreenActions: ProfileScreenActions,
) : ViewBindingKotlinModel<ModelCustomColorItemBinding>(R.layout.model_custom_color_item) {
    override fun ModelCustomColorItemBinding.bind() {
        titleTextView.text = displayText
        priorityImageView.drawable.setTint(color)
        root.strokeColor = color
        root.setOnClickListener {
            profileScreenActions.onPrioritySelected(displayText)
        }
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }
}
