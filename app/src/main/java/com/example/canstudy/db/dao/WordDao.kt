package com.example.canstudy.db.dao

import androidx.room.*
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Insert
    suspend fun addWord(wordEntity: WordEntity)

    @Update
    suspend fun update(wordEntity: WordEntity)

    @Delete
    suspend fun delete(wordEntity: WordEntity)

    @Query("SELECT * FROM `CantoWords`")
    fun readAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM `CantoWords` where id=:id")
    fun readWordById(id:Int):Flow<WordEntity>

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