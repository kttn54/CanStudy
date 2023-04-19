package com.example.canstudy.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
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
    private lateinit var btnToggleVisibility: Button
    private lateinit var btnShuffle: Button
    private lateinit var toggleTranslation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        toggleTranslation = "off"
        val intent = intent
        val wrongWordList: ArrayList<Int> = intent.getIntegerArrayListExtra("key") as ArrayList<Int>
        val dao = (application as CanStudyApp).db.wordDao()
        if (!wrongWordList.isNullOrEmpty()) {
            setupWordRecyclerView(dao, wrongWordList, toggleTranslation)
        } else {
            tvNoWrongWordsFound.visibility = VISIBLE
        }

        btnToggleVisibility.setOnClickListener {
            toggleTranslation(toggleTranslation, dao, wrongWordList)

        }
        btnShuffle.setOnClickListener { shuffleWordList(wrongWordList) }
    }

    private fun toggleTranslation(toggle: String, dao: WordDao, list: ArrayList<Int>) {
        if (toggle == "off") {
            toggleTranslation = "on"
            setupWordRecyclerView(dao, list, toggleTranslation)
        } else {
            toggleTranslation = "off"
            setupWordRecyclerView(dao, list, toggleTranslation)
        }
    }

    private fun shuffleWordList(wordList: ArrayList<Int>) {
        val shuffledWordList: ArrayList<Int> = ArrayList(wordList)
        shuffledWordList.shuffle()
        val dao = (application as CanStudyApp).db.wordDao()
        setupWordRecyclerView(dao, shuffledWordList, "off")

    }

    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarReviewActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Review"
        }
        tvNoWrongWordsFound = binding.tvNoWrongWordsFound
        btnToggleVisibility = binding.btnToggleVisibility
        btnShuffle = binding.btnShuffle

        binding.toolbarReviewActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupWordRecyclerView(wordDao: WordDao, wrongWordIDList: ArrayList<Int>, toggleTranslation: String) {
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
            attachAdapter(wrongWordList, toggleTranslation)
        }
    }

    private fun attachAdapter(list: ArrayList<WordEntity>, toggleTranslation: String) {
        val reviewAdapter = ReviewAdapter(list, toggleTranslation)
        binding.rvReview.adapter = reviewAdapter
    }
}