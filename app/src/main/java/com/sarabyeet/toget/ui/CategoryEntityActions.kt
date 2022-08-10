package com.sarabyeet.toget.ui

import com.sarabyeet.toget.db.model.CategoryEntity

interface CategoryEntityActions {
    fun onClickCategory(category: CategoryEntity)
}