package com.example.canstudy.db.entity

import androidx.room.Entity

@Entity(tableName = "CantoWords")
data class CantoWordEntity(
    val CANTO_WORD: String
) {
    fun getCantoWord(): String {
        return CANTO_WORD
    }
}