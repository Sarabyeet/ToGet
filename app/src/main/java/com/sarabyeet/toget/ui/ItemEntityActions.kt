package com.sarabyeet.toget.ui

import com.sarabyeet.toget.db.model.ItemEntity

interface ItemEntityActions {
    fun onDeleteItemEntity(item: ItemEntity)
    fun onBumpPriority(item:ItemEntity)
}