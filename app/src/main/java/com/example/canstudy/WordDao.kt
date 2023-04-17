package com.example.canstudy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert
    suspend fun addWord(word: WordModel)

    @Query("SELECT * FROM `CantoWords`")
    // TODO
    fun readAll(): Flow<List<WordEntity>>

    /*
    fun readAll(): Flow<List<WordEntity>> declares the function itself.
    It returns a Flow of a List of WordEntity objects, which means that the results of the
    query will be emitted as a stream of lists, with each list containing zero or more
    WordEntity objects.

    The Flow type is a reactive stream type in Kotlin that is used to represent a
    stream of values that are emitted over time. It allows for asynchronous processing
    and can be used to handle large amounts of data in a memory-efficient manner.
     */
}