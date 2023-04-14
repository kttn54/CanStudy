package com.example.canstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.canstudy.databinding.ActivityStudyBinding

class StudyActivity : AppCompatActivity() {

    private var binding : ActivityStudyBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}