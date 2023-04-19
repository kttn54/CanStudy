package com.example.canstudy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canstudy.CanStudyApp
import com.example.canstudy.databinding.ActivityReviewBinding
import com.example.canstudy.db.adapter.ReviewAdapter
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var tvNoWrongWordsFound: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        val intent = intent
        val wrongWordList = intent.getIntegerArrayListExtra("key")
        val dao = (application as CanStudyApp).db.wordDao()

        if (wrongWordList != null) {
            setupWordRecyclerView(dao, wrongWordList)
        } else {
            tvNoWrongWordsFound.visibility = VISIBLE
        }
    }

    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarReviewActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Review"
        }

        tvNoWrongWordsFound = binding.tvNoWrongWordsFound

        binding?.toolbarReviewActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupWordRecyclerView(wordDao: WordDao, wrongWordIDList: ArrayList<Int>) {
        val wrongWordIDList = wrongWordIDList
        val wrongWordList = ArrayList<WordEntity>()
        lifecycleScope.launch {
            binding.rvReview.layoutManager = LinearLayoutManager(this@ReviewActivity)
            // first() collects the first result from the query. This converts the query from a
            // Flow<WordEntity> to a WordEntity type.
            for (id in wrongWordIDList) {
                val newWord = wordDao.readWordById(id).first()
                wrongWordList.add(newWord)
            }
            attachAdapter(wrongWordList)
        }
    }

    private fun attachAdapter(list: ArrayList<WordEntity>) {
        val reviewAdapter = ReviewAdapter(list)
        binding.rvReview.adapter = reviewAdapter
    }
}