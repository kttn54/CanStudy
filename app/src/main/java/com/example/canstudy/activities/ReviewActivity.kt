package com.example.canstudy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.canstudy.CanStudyApp
import com.example.canstudy.R
import com.example.canstudy.databinding.ActivityReviewBinding
import com.example.canstudy.db.SwipeToDeleteCallback
import com.example.canstudy.db.adapter.ReviewAdapter
import com.example.canstudy.db.dao.WordDao
import com.example.canstudy.db.entity.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A class that displays wrong words from the TestActivity for the user to review.
 */

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding
    private lateinit var toggleTranslation: String
    private lateinit var wrongWordList: ArrayList<Int>
    private lateinit var dao: WordDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialiseActivity()

        binding.btnToggleVisibility.setOnClickListener { toggleTranslation(toggleTranslation, dao, wrongWordList) }
        binding.btnShuffle.setOnClickListener { shuffleWordList(wrongWordList) }
    }

    /**
     * A function that initialises the word list and UI components.
     */
    private fun initialiseActivity() {
        setSupportActionBar(binding.toolbarReviewActivity)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.review_toolbar_title)
        }
        binding.toolbarReviewActivity.setNavigationOnClickListener { onBackPressed() }

        toggleTranslation = "off"

        val intent = intent
        wrongWordList = intent.getIntegerArrayListExtra("wrongWordList") ?: arrayListOf()

        dao = (application as CanStudyApp).db.wordDao()

        if (wrongWordList.isNotEmpty()) {
            setupWordRecyclerView(dao, wrongWordList, toggleTranslation)
        } else {
            binding.tvNoWrongWordsFound.visibility = VISIBLE
        }
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
     * A function that initialises the RecyclerView for the wrong word list and handles the swipe functionality.
     */
    private fun setupWordRecyclerView(wordDao: WordDao, wrongWordIDList: ArrayList<Int>, toggleTranslation: String) {
        val wrongWordIDList = wrongWordIDList
        val wrongWordEntityList = ArrayList<WordEntity>()
        lifecycleScope.launch(Dispatchers.IO) {
            binding.rvReview.layoutManager = LinearLayoutManager(this@ReviewActivity)
            // first() collects the first result from the query. This converts the query from a Flow<WordEntity> to a WordEntity type.

            // Database operation
            for (id in wrongWordIDList) {
                val newWord = wordDao.readWordById(id).first()
                wrongWordEntityList.add(newWord)
            }

            // Swich back to the main dispatcher to update UI
            withContext(Dispatchers.Main) {
                attachAdapter(wrongWordEntityList, toggleTranslation)
            }
        }

        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                wrongWordEntityList.removeAt(position)
                wrongWordList.removeAt(position)
                binding.rvReview.adapter?.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.rvReview)
    }

    /**
     * A function that attaches the relevant data to the adapter for the RecyclerView.
     */
    private fun attachAdapter(list: ArrayList<WordEntity>, toggleTranslation: String) {
        val reviewAdapter = ReviewAdapter(list, toggleTranslation)
        binding.rvReview.adapter = reviewAdapter
    }
}
