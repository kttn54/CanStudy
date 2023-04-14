package com.example.canstudy

import android.content.ClipData
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canstudy.databinding.ActivitySearchBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {

    private var binding: ActivitySearchBinding? = null
    private var wordAdapter: WordAdapter? = null
    private var wordList: ArrayList<WordModel>? = null
    private var searchBar: EditText? = null
    private var noSearchResults: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val dao = (application as CanStudyDatabaseApp).db.wordDao()
        setupWordRecyclerView(dao)

        noSearchResults = binding?.tvNoResultsFound
        searchBar = binding?.etSearchBar
        searchBar?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val query = s.toString().lowercase(Locale.getDefault())
                filterWithQuery(query)
            }

            override fun afterTextChanged(s: Editable?) {
                //filterData(s.toString())
            }
        })
    }

    private fun attachAdapter(list: ArrayList<WordModel>) {
        val wordAdapter = WordAdapter(list)
        binding?.rvSearch?.adapter = wordAdapter
    }

    private fun onQueryChanged(filterQuery: String): ArrayList<WordModel> {
        val filteredList = ArrayList<WordModel>()
        val dao = (application as CanStudyDatabaseApp).db.wordDao()
        val words = getWordList(dao)
        for (word in words) {
            println(word)
            if(word.getEnglishWord().lowercase(Locale.getDefault()).contains(filterQuery)) {
                filteredList.add(word)
            }
        }
        return filteredList
    }

    // fix this
    private fun filterWithQuery(query: String) {
        if (query.isNotEmpty()) {
            val filteredList: ArrayList<WordModel> = onQueryChanged(query)
            println("filter)")
            attachAdapter(filteredList)
            toggleRecyclerView(filteredList)
        } else if (query.isEmpty()) {
            val dao = (application as CanStudyDatabaseApp).db.wordDao()
            setupWordRecyclerView(dao)
            // attachAdapter(wordList)
        }
    }

    private fun toggleRecyclerView(wordList: ArrayList<WordModel>) {
        if (wordList.isEmpty()) {
            binding?.rvSearch?.visibility = View.INVISIBLE
            binding?.tvNoResultsFound?.visibility = View.VISIBLE
        } else {
            binding?.rvSearch?.visibility = View.VISIBLE
            binding?.tvNoResultsFound?.visibility = View.INVISIBLE
        }
    }


    /* FROM CHAT-GPT: potentially not using
    private fun filterData(query: String) {
        val words = getWordList(dao)
        val filteredList = words.filter { item ->
            item.contains(query, ignoreCase = true)
        }
        wordAdapter.updateData(filteredList)
    }

    private fun updateData(newDataList: List<ClipData.Item>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
    */

    private fun getWordList(wordDao: WordDao): ArrayList<WordModel> {
        val wordList = ArrayList<WordModel>()
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    binding?.rvSearch?.layoutManager = LinearLayoutManager(this@SearchActivity)

                    for (word in allWordsList) {
                        val newWord = WordModel(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.CORRECT_STATUS
                        )
                        wordList.add(newWord)
                    }
                }
            }
        }
        return wordList
    }

    private fun setupWordRecyclerView(wordDao: WordDao) {
        lifecycleScope.launch {
            wordDao.readAll().collect { allWordsList ->
                if (allWordsList.isNotEmpty()) {
                    binding?.rvSearch?.layoutManager = LinearLayoutManager(this@SearchActivity)
                    val words = ArrayList<WordModel>()
                    for (word in allWordsList) {
                        val newWord = WordModel(
                            word.ID,
                            word.CANTO_WORD,
                            word.ENGLISH_WORD,
                            word.CORRECT_STATUS
                        )
                        words.add(newWord)
                    }
                    attachAdapter(words)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}