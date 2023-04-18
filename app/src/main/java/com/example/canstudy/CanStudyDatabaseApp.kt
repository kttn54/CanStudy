package com.example.canstudy

import android.app.Application

class CanStudyApp: Application() {
    val db by lazy {
        WordDatabase.getInstance(this)
    }
}