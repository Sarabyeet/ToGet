package com.sarabyeet.toget.arch

import com.sarabyeet.toget.db.model.CategoryEntity

sealed class ToGetEvents {
    /** One Time event used by kotlin channels */
    data class DbTransaction(val isSaved: Boolean) : ToGetEvents()

    // region Category View State
    data class CategoriesViewState(
        val isLoading: Boolean = false,
        val itemList: List<Item> = emptyList(),
    ) {
        data class Item(
            val categoryEntity: CategoryEntity = CategoryEntity(),
            val isSelected: Boolean = false,
        )

        fun getCategoryId(): String {
            return itemList.find { it.isSelected }?.categoryEntity?.id
                ?: CategoryEntity.DEFAULT_CATEGORY_ID
        }
    }
    // endregion Category View State

    // region Home View State
    data class HomeViewState(
        val dataList: List<DataItem<*>> = emptyList(),
        val isLoading: Boolean = false,
        val sort: Sort = Sort.NONE,
    ) {
        data class DataItem<T>(
            val data: T,
            val isHeader: Boolean = false,
        )

        enum class Sort(val displayName: String) {
            NONE("None"),
            CATEGORY("Category"),
            NEWEST("Newest"),
            OLDEST("Oldest"),
        }
    }
    // endregion Home View State
}
