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

        val migration1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Define how to migrate the database from version 1 to version 2
                // For example, you can add a new column or modify an existing column
                database.execSQL("ALTER TABLE WordEntity ADD COLUMN new_column_name TEXT")
            }
        }

        // this will keep a reference to any database returned via getInstance.
        // this will help avoid repeatedly initialising the database which is performance heavy
        @Volatile
        private var INSTANCE: WordDatabase? = null

        /*
        This is an example of Singleton pattern, where there is only one instance and
        provides a global point of access to that instance.
        Multiple instances of a database can lead to issues with concurrency and
        cause inconsistencies with data
        This method will check if we already have an instance already.
        If we do, use it. Otherwise, create it.
         */

        /*
        The synchronized block ensures that the database is only initialised once
        Only one thread may enter the synchronized function at a time
        This prevents any concurrency issues that could occur when accessing the database

        The if block wipes and rebuilds the database instead of migrating if no migration object exists
        fallbacktoDestructiveMigration ensures that the database is wiped and rebuilt
         if no migration object exists
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
                        .createFromAsset("database/CantoWords_shortened.db")
                        .addMigrations(
                            migration1to2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

