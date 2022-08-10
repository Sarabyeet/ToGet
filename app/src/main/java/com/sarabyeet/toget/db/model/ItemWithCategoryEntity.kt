package com.sarabyeet.toget.db.model

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithCategoryEntity (
    @Embedded
    val itemEntity: ItemEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )

    /**
     * Since we have already populated our data base the app tries to put a null value in this non-null field
     * which causes a crash, Item Entity table has values of category id set to "" (Empty String) by default
     * which matches none of the UUID in Category Entity table, that's why we will have to make this a nullable
     * field here.
     * */
    val categoryEntity: CategoryEntity?
)