package com.example.canstudy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CantoWords")
data class WordEntity(
    @PrimaryKey(autoGenerate=true)
    val ID: Int,
    val CANTO_WORD: String,
    val ENGLISH_WORD: String,
    val CORRECT_STATUS: Boolean
) {
    fun getId(): Int {
        return ID
    }

    fun getCantoWord(): String {
        return CANTO_WORD
    }

    fun getEnglishWord(): String {
        return ENGLISH_WORD
    }

    fun getCorrectStatus(): Boolean {
        return CORRECT_STATUS
    }

}



