package com.sarabyeet.toget.ui.fragments.home

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.ModelItemEntityBinding
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.ui.ItemEntityActions
import com.sarabyeet.toget.ui.epoxy.LoadingEpoxyModel
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class HomeEpoxyController(
    private val itemEntityActions: ItemEntityActions
) : EpoxyController() {

    var isLoading: Boolean = true
        set(value) {
            field = value
            if (field) {
                requestModelBuild()
            }
        }

    var itemEntityList = ArrayList<ItemEntity>()
        set(value) {
            field = value
            isLoading = false
            requestModelBuild()
        }

    override fun buildModels() {
        if (isLoading) {
            LoadingEpoxyModel().id("loading_model").addTo(this)
            return
        }
        if (itemEntityList.isEmpty()) {
            //todo show empty state
            return
        }

        itemEntityList.forEachIndexed { index, itemEntity ->
            ItemEntityEpoxyModel(itemEntity, itemEntityActions).id("$index").addTo(this)
        }
    }

    data class ItemEntityEpoxyModel(
        val itemEntity: ItemEntity,
        val itemEntityActions: ItemEntityActions
    ) : ViewBindingKotlinModel<ModelItemEntityBinding>(R.layout.model_item_entity) {
        override fun ModelItemEntityBinding.bind() {
            titleTextView.text = itemEntity.title
            if (itemEntity.description == null){
                descriptionTextView.isGone = true
            } else {
                descriptionTextView.isVisible = true
                descriptionTextView.text = itemEntity.description
            }
            deleteButton.setOnClickListener {
                itemEntityActions.onDeleteItemEntity(itemEntity)
            }
            priorityTextView.setOnClickListener {
                itemEntityActions.onBumpPriority(itemEntity)
            }

            val colorDrawable = when (itemEntity.priority){
                1 -> R.drawable.circle_green
                2 -> R.drawable.circle_yellow
                3 -> R.drawable.circle_red
                else -> R.drawable.circle_red
            }
            priorityTextView.setBackgroundResource(colorDrawable)
        }
    }
}