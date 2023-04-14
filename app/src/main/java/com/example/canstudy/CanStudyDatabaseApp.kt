package com.example.canstudy

import android.app.Application

class CanStudyDatabaseApp: Application() {
    val db by lazy {
        WordDatabase.getInstance(this)
    }
}