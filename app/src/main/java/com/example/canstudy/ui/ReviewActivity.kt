package com.example.canstudy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canstudy.CanStudyApp
import com.example.canstudy.databinding.ActivityReviewBinding
import com.example.canstudy.db.adapter.WordAdapter
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding?.toolbarReviewActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Review"
        }
        binding?.toolbarReviewActivity?.setNavigationOnClickListener { onBackPressed() }

        val intent = intent
        val wrongWordList = intent.getIntegerArrayListExtra("key")
        val dao = (application as CanStudyApp).db.wordDao()

        setupWordRecyclerView(dao)
    }

    private fun setupWordRecyclerView(wordDao: WordDao) {
        val wordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    binding.rvReview.layoutManager = LinearLayoutManager(this@ReviewActivity)
                    for (word in allWordsList) {
                        val newWord = WordEntity(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.CORRECT_STATUS
                        )
                        wordList.add(newWord)
                    }
                    attachAdapter(wordList)
                }
            }
        }
    }

    private fun attachAdapter(list: ArrayList<WordEntity>) {
        val wordAdapter = WordAdapter(list)
        binding.rvReview.adapter = wordAdapter
    }
}