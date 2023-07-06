package com.example.canstudy.db.dao

import androidx.room.*
import com.example.canstudy.db.entity.CantoWordEntity
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

    @Query("SELECT * FROM `CantoWords` where id = :id")
    fun readWordById(id:Int):Flow<WordEntity>

    @Query("SELECT CANTO_WORD FROM `CantoWords` where id = :id")
    fun readCantoWordById(id:Int):Flow<CantoWordEntity>

    @Query("SELECT * FROM `CantoWords` where NUMBER_OF_WORDS = 1")
    fun readWordByLengthOneWord(): Flow<List<WordEntity>>

    @Query("SELECT * FROM `CantoWords` where NUMBER_OF_WORDS = 2 or NUMBER_OF_WORDS = 3")
    fun readWordByLengthTwoOrThreeWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM `CantoWords` where NUMBER_OF_WORDS > 3")
    fun readWordByLengthFourPlusWords(): Flow<List<WordEntity>>
}