package com.sarabyeet.toget.ui

import com.sarabyeet.toget.db.model.ItemEntity

interface ItemEntityActions {
    fun onBumpPriority(item:ItemEntity)
}