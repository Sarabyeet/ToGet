package com.sarabyeet.toget.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarabyeet.toget.db.dao.CategoryDao
import com.sarabyeet.toget.db.dao.ItemDao
import com.sarabyeet.toget.db.model.CategoryEntity
import com.sarabyeet.toget.db.model.ItemEntity

@Database(
    entities = [ItemEntity::class, CategoryEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2())
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }

    class MIGRATION_1_2: Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS category_entity (id TEXT NOT NULL, name TEXT NOT NULL, PRIMARY KEY(id))")
        }
    }
}