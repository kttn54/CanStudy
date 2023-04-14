package com.example.canstudy

class WordModel(
    private var id: Int,
    private var cantonese_word: String,
    private var english_word: String,
    private var correct_status: Boolean
) {
    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getCantoneseWord(): String {
        return cantonese_word
    }

    fun setCantoneseWord(canto_word: String) {
        this.cantonese_word = canto_word
    }

    fun getEnglishWord(): String {
        return english_word
    }

    fun setEnglishWord(english_word: String) {
        this.english_word = english_word
    }

    fun getCorrectStatus(): Boolean {
        return correct_status
    }

    fun setCorrectStatus(correct_status: Boolean) {
        this.correct_status = correct_status
    }

}