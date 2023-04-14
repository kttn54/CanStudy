package com.example.canstudy

import android.content.Context
import android.graphics.Typeface.createFromAsset
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase: RoomDatabase() {
    abstract fun wordDao():WordDao

    companion object {

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
        fun getInstance(context: Context): WordDatabase {

            // use the synchronized block to ensure the database is only initialised once
            // only one thread may enter the synchronized function at a time
            // this prevents any concurrency issues that could occur when accessing the database
            synchronized(this) {
                var instance = INSTANCE

                // this wipes and rebuilds instead of migrating if no migration object exists
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WordDatabase::class.java,
                        "word_database"
                    ).fallbackToDestructiveMigration()
                        .createFromAsset("database/CantoWords_shortened.db").build()
                    //fallbacktoDestructiveMigration ensures that the database is wiped and rebuilt
                    //if no migration object exists

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}