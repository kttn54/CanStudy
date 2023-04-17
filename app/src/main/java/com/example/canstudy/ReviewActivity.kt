package com.example.canstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.canstudy.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {

    private var binding : ActivityReviewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}