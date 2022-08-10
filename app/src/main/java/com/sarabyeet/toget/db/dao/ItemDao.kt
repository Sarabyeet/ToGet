package com.sarabyeet.toget.db.dao

import androidx.room.*
import com.sarabyeet.toget.db.model.ItemEntity
import com.sarabyeet.toget.db.model.ItemWithCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item_entity")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Transaction
    @Query("SELECT * FROM item_entity")
    fun getAllItemWithCategory(): Flow<List<ItemWithCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemEntity: ItemEntity)

    @Delete
    suspend fun delete(itemEntity: ItemEntity)

    @Update
    suspend fun update(itemEntity: ItemEntity)
}