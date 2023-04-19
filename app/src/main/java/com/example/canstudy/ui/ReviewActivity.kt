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
        setupWordRecyclerView(dao, shuffledWordList, "off")

    }

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

        val swipeHandler = object : SwipeToDeleteCallback(ReviewAdapter(wrongWordList, toggleTranslation)) {
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //TODO: add custom drawing here
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                Log.e("asdf", "ReviewActivity position is $position, word removed is ${wrongWordList[position]}")
                (rvReview.adapter as ReviewAdapter).deleteItem(position)
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

open class SwipeToDeleteCallback(private val adapter: ReviewAdapter) : ItemTouchHelper.Callback() {

    // This function defines the swipe and drag behaviour of an item in a RecyclerView.
    // DragFlag of 0 means the item is not draggable.
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    // This function does nothing as we only want the swipe functionality.
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position)
    }
}
