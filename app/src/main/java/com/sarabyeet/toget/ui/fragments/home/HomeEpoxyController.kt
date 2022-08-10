package com.sarabyeet.toget.ui.fragments.home

import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.addHeaderModel
import com.sarabyeet.toget.arch.ToGetEvents
import com.sarabyeet.toget.databinding.ModelEmptyStateBinding
import com.sarabyeet.toget.databinding.ModelItemEntityBinding
import com.sarabyeet.toget.db.model.ItemWithCategoryEntity
import com.sarabyeet.toget.ui.ItemEntityActions
import com.sarabyeet.toget.ui.epoxy.LoadingEpoxyModel
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class HomeEpoxyController(
    private val itemEntityActions: ItemEntityActions,
) : EpoxyController() {

    var viewState = ToGetEvents.HomeViewState(isLoading = true)
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
            ItemEntityEpoxyModel(itemWithCategoryEntity, itemEntityActions)
                .id(itemWithCategoryEntity.itemEntity.id)
                .addTo(this)
        }
    }


    data class ItemEntityEpoxyModel(
        val item: ItemWithCategoryEntity,
        val itemEntityActions: ItemEntityActions,
    ) : ViewBindingKotlinModel<ModelItemEntityBinding>(R.layout.model_item_entity) {
        override fun ModelItemEntityBinding.bind() {
            titleTextView.text = item.itemEntity.title
            if (item.itemEntity.description == null) {
                descriptionTextView.isGone = true
            } else {
                descriptionTextView.isVisible = true
                descriptionTextView.text = item.itemEntity.description
            }

            priorityTextView.setOnClickListener {
                itemEntityActions.onBumpPriority(item.itemEntity)
            }

            categoryTextView.text = item.categoryEntity?.name

            root.setOnClickListener {
                itemEntityActions.onClickItem(item.itemEntity)
            }
            val colorDrawable = when (item.itemEntity.priority) {
                1 -> R.drawable.circle_green
                2 -> R.drawable.circle_yellow
                3 -> R.drawable.circle_red
                else -> R.drawable.circle_red
            }
            priorityTextView.setBackgroundResource(colorDrawable)

            val cardStrokeColor = when (item.itemEntity.priority) {
                1 -> R.color.Green
                2 -> R.color.Yellow
                3 -> R.color.Red
                else -> R.color.purple_200
            }
            root.strokeColor = ContextCompat.getColor(root.context, cardStrokeColor)
        }
    }

    class EmptyStateEpoxy :
        ViewBindingKotlinModel<ModelEmptyStateBinding>(R.layout.model_empty_state) {
        override fun ModelEmptyStateBinding.bind() {
            // nothing to do here
        }
    }
}