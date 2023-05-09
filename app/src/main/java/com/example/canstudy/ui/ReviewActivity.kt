package com.example.canstudy.ui

import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.canstudy.CanStudyApp
import com.example.canstudy.databinding.ActivityReviewBinding
import com.example.canstudy.db.SwipeToDeleteCallback
import com.example.canstudy.db.adapter.ReviewAdapter
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * A class that displays wrong words from the TestActivity for the user to review.
 */

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var tvNoWrongWordsFound: TextView
    private lateinit var btnToggleVisibility: Button
    private lateinit var btnShuffle: Button
    private lateinit var toggleTranslation: String
    private lateinit var wrongWordList: ArrayList<Int>
    private lateinit var rvReview: RecyclerView
    private lateinit var dao: WordDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        btnToggleVisibility.setOnClickListener { toggleTranslation(toggleTranslation, dao, wrongWordList) }
        btnShuffle.setOnClickListener { shuffleWordList(wrongWordList) }
    }

    /**
     * A function that toggles the translation for the English words.
     */
    private fun toggleTranslation(toggle: String, dao: WordDao, list: ArrayList<Int>) {
        if (toggle == "off") {
            toggleTranslation = "on"
            setupWordRecyclerView(dao, list, toggleTranslation)
        } else {
            toggleTranslation = "off"
            setupWordRecyclerView(dao, list, toggleTranslation)
        }
    }

    /**
     * A function that shuffles the order of the wrong words.
     */
    private fun shuffleWordList(wordList: ArrayList<Int>) {
        val shuffledWordList: ArrayList<Int> = ArrayList(wordList)
        shuffledWordList.shuffle()
        setupWordRecyclerView(dao, shuffledWordList, "off")

    }

    /**
     * A function that initialises the word list and UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarReviewActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Review"
        }
        binding.toolbarReviewActivity.setNavigationOnClickListener { onBackPressed() }

        tvNoWrongWordsFound = binding.tvNoWrongWordsFound
        btnToggleVisibility = binding.btnToggleVisibility
        btnShuffle = binding.btnShuffle
        rvReview = binding.rvReview

        toggleTranslation = "off"

        val intent = intent
        wrongWordList = intent.getIntegerArrayListExtra("key") as ArrayList<Int>
        dao = (application as CanStudyApp).db.wordDao()
        if (!wrongWordList.isNullOrEmpty()) {
            setupWordRecyclerView(dao, wrongWordList, toggleTranslation)
        } else {
            tvNoWrongWordsFound.visibility = VISIBLE
        }
    }

    /**
     * A function that initialises the RecyclerView for the wrong word list and handles the swipe functionality.
     */
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

        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                wrongWordList.removeAt(position)
                rvReview.adapter?.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvReview)
    }

    private fun attachAdapter(list: ArrayList<WordEntity>, toggleTranslation: String) {
        val reviewAdapter = ReviewAdapter(list, toggleTranslation)
        rvReview.adapter = reviewAdapter
    }
}
