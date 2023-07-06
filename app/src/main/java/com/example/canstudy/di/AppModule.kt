package com.example.canstudy.di

import android.content.Context
import com.example.canstudy.db.WordDatabase
import com.example.canstudy.db.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): WordDatabase {
        return WordDatabase.getInstance(context)
    }

    @Provides
    fun provideWordDao(wordDatabase: WordDatabase): WordDao {
        return wordDatabase.wordDao()
    }
}