package com.sarabyeet.toget.ui

import com.sarabyeet.toget.db.model.CategoryEntity

interface ProfileScreenActions {
    fun onClickCategory(category: CategoryEntity)
    fun onDeleteCategory(category: CategoryEntity)
    fun onPrioritySelected(priorityName: String)
}