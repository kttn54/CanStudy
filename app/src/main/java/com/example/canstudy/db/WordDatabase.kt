package com.example.canstudy.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity

@Database(entities = [WordEntity::class], version = 2, exportSchema = true)
abstract class WordDatabase: RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        val migrationOnetoTwo = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE CantoWords ADD COLUMN Changes TEXT")
            }
        }

        // @Volatile will keep a reference to any database returned via getInstance, avoiding repeatedly
        // initialising the database.
        @Volatile
        private var INSTANCE: WordDatabase? = null

        /**
         * This function is a Singleton pattern, ensuring there is only one instance of the database.
         * It synchronises access to the database creation code to prevent concurrency issues
         * in the case that multiple threads try to intialise the database simulatneously.
         */
        fun getInstance(context: Context): WordDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        WordDatabase::class.java,
                        "CantoWords"
                    )
                        .createFromAsset("database/CantoWords_v3.db")
                        .addMigrations(
                            migrationOnetoTwo)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

