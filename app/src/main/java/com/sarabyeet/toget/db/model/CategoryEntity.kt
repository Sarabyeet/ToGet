package com.sarabyeet.toget.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_entity")
data class CategoryEntity (
    @PrimaryKey val id: String = "",
    val name: String = ""
    ){
    companion object {
        const val DEFAULT_CATEGORY_ID = "NONE"

        fun getDefaultCategory(): CategoryEntity {
            return CategoryEntity(id =DEFAULT_CATEGORY_ID, name = "None")
        }
    }
}