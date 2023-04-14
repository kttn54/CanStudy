package com.example.canstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.canstudy.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private var binding : ActivityGameBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}