package com.example.canstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.canstudy.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

    private var binding : ActivityTestBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}