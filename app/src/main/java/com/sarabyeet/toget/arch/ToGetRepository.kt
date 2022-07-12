package com.sarabyeet.toget.arch

import com.sarabyeet.toget.db.AppDatabase
import com.sarabyeet.toget.db.model.ItemEntity
import kotlinx.coroutines.flow.Flow

class ToGetRepository(private val database: AppDatabase) {

    fun getAllItems(): Flow<List<ItemEntity>> {
        return database.itemDao().getAllItems()
    }

    suspend fun insertItem(itemEntity: ItemEntity) {
        database.itemDao().insert(itemEntity)
    }

    suspend fun deleteItem(itemEntity: ItemEntity){
        database.itemDao().delete(itemEntity)
    }

    suspend fun updateItem(itemEntity: ItemEntity){
        database.itemDao().update(itemEntity)
    }

}