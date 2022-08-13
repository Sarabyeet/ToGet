package com.sarabyeet.toget.ui.fragments.home

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.ModelEmptyStateBinding
import com.sarabyeet.toget.databinding.ModelItemEntityBinding
import com.sarabyeet.toget.db.model.ItemWithCategoryEntity
import com.sarabyeet.toget.ui.ItemEntityActions
import com.sarabyeet.toget.ui.epoxy.LoadingEpoxyModel
import com.sarabyeet.toget.util.addHeaderModel
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class HomeEpoxyController(
    private val itemEntityActions: ItemEntityActions,
) : EpoxyController() {

    var viewState = ToGetEvents.HomeViewState(isLoading = true)
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
        if (viewState.isLoading) {
            LoadingEpoxyModel().id("loading_state").addTo(this)
            return
        }
        if (viewState.dataList.isEmpty()) {
            EmptyStateEpoxy().id("empty_state").addTo(this)
            return
        }

        viewState.dataList.forEach { dataItem ->
            if (dataItem.isHeader) {
                addHeaderModel(dataItem.data as String)
                return@forEach
            }

            val itemWithCategoryEntity = dataItem.data as ItemWithCategoryEntity
            ItemEntityEpoxyModel(itemWithCategoryEntity, itemEntityActions, highPriority, mediumPriority, lowPriority)
                .id(itemWithCategoryEntity.itemEntity.id)
                .addTo(this)
        }
    }


    data class ItemEntityEpoxyModel(
        val item: ItemWithCategoryEntity,
        val itemEntityActions: ItemEntityActions,
        val highPriorityColor: Int,
        val mediumPriorityColor: Int,
        val lowPriorityColor: Int,
    ) : ViewBindingKotlinModel<ModelItemEntityBinding>(R.layout.model_item_entity) {
        override fun ModelItemEntityBinding.bind() {
            titleTextView.text = item.itemEntity.title
            if (item.itemEntity.description == null) {
                descriptionTextView.isGone = true
            } else {
                descriptionTextView.isVisible = true
                descriptionTextView.text = item.itemEntity.description
            }

            priorityImageView.setOnClickListener {
                itemEntityActions.onBumpPriority(item.itemEntity)
            }

            categoryTextView.text = item.categoryEntity?.name

            root.setOnClickListener {
                itemEntityActions.onClickItem(item.itemEntity)
            }
            val color = when (item.itemEntity.priority) {
                1 -> lowPriorityColor
                2 -> mediumPriorityColor
                3 -> highPriorityColor
                else -> R.drawable.circle_red
            }
            priorityImageView.drawable.setTint(color)

            root.strokeColor = color
        }
    }

    class EmptyStateEpoxy :
        ViewBindingKotlinModel<ModelEmptyStateBinding>(R.layout.model_empty_state) {
        override fun ModelEmptyStateBinding.bind() {
            // nothing to do here
        }
    }
}