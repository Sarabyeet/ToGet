package com.sarabyeet.toget.ui.fragments.home

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyController
import com.sarabyeet.toget.R
import com.sarabyeet.toget.databinding.ModelEmptyStateBinding
import com.sarabyeet.toget.databinding.ModelItemEntityBinding
import com.sarabyeet.toget.databinding.ModelItemHeaderBinding
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.ui.ItemEntityActions
import com.sarabyeet.toget.ui.epoxy.LoadingEpoxyModel
import com.sarabyeet.travelapp.ui.epoxy.ViewBindingKotlinModel

class HomeEpoxyController(
    private val itemEntityActions: ItemEntityActions,
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
            LoadingEpoxyModel().id("loading_state").addTo(this)
            return
        }
        if (itemEntityList.isEmpty()) {
            EmptyStateEpoxy().id("empty_state").addTo(this)
            return
        }

        var currentPriority = -1

        itemEntityList.sortedByDescending { it.priority }.forEach { itemEntity ->

            if (itemEntity.priority != currentPriority){
                currentPriority = itemEntity.priority
                val text = getHeaderTextForPriority(itemEntity.priority)
                ItemHeaderEpoxy(text).id("${text}_priority").addTo(this)
            }
            ItemEntityEpoxyModel(itemEntity, itemEntityActions).id(itemEntity.id).addTo(this)
        }
    }

    private fun getHeaderTextForPriority(priority: Int): String {
        return when(priority){
            1 -> "Low"
            2 -> "Medium"
            else -> "High"
        }
    }

    data class ItemEntityEpoxyModel(
        val itemEntity: ItemEntity,
        val itemEntityActions: ItemEntityActions,
    ) : ViewBindingKotlinModel<ModelItemEntityBinding>(R.layout.model_item_entity) {
        override fun ModelItemEntityBinding.bind() {
            titleTextView.text = itemEntity.title
            if (itemEntity.description == null) {
                descriptionTextView.isGone = true
            } else {
                descriptionTextView.isVisible = true
                descriptionTextView.text = itemEntity.description
            }

            priorityTextView.setOnClickListener {
                itemEntityActions.onBumpPriority(itemEntity)
            }

            root.setOnClickListener {
                itemEntityActions.onClickItem(itemEntity)
                Log.d("rootOn", "On card view clicked")
            }
            val colorDrawable = when (itemEntity.priority) {
                1 -> R.drawable.circle_green
                2 -> R.drawable.circle_yellow
                3 -> R.drawable.circle_red
                else -> R.drawable.circle_red
            }
            priorityTextView.setBackgroundResource(colorDrawable)

            val cardStrokeColor = when (itemEntity.priority) {
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

    data class ItemHeaderEpoxy(
        val headerText: String,
    ) : ViewBindingKotlinModel<ModelItemHeaderBinding>(R.layout.model_item_header) {
        override fun ModelItemHeaderBinding.bind() {
            headerTitle.text = headerText
        }
    }

}