package com.sarabyeet.toget.arch

import com.sarabyeet.toget.db.AppDatabase
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.db.model.ItemWithCategoryEntity
import kotlinx.coroutines.flow.Flow

class ToGetRepository(private val database: AppDatabase) {

    // region Item Entity
    fun getAllItems(): Flow<List<ItemEntity>> {
        return database.itemDao().getAllItems()
    }

    fun getAllItemWithCategory(): Flow<List<ItemWithCategoryEntity>> {
        return database.itemDao().getAllItemWithCategory()
    }

    suspend fun insertItem(itemEntity: ItemEntity) {
        database.itemDao().insert(itemEntity)
    }

    suspend fun deleteItem(itemEntity: ItemEntity) {
        database.itemDao().delete(itemEntity)
    }

    suspend fun updateItem(itemEntity: ItemEntity) {
        database.itemDao().update(itemEntity)
    }
    // endregion Item Entity

    // region Category Entity
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return database.categoryDao().getAllCategories()
    }

    suspend fun insertCategory(categoryEntity: CategoryEntity) {
        database.categoryDao().insert(categoryEntity)
    }

    suspend fun deleteCategory(categoryEntity: CategoryEntity) {
        database.categoryDao().delete(categoryEntity)
    }

    suspend fun updateCategory(categoryEntity: CategoryEntity) {
        database.categoryDao().update(categoryEntity)
    }
    // endregion Category Entity
}