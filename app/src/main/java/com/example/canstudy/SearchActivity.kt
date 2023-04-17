package com.example.canstudy

import android.app.Dialog
import android.content.ClipData
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.UserDictionary.Words.addWord
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.canstudy.databinding.ActivitySearchBinding
import com.example.canstudy.databinding.DialogAddWordBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {

    private var binding: ActivitySearchBinding? = null
    private var searchBar: EditText? = null
    private var noSearchResults: TextView? = null
    private var languageSelected: String? = null
    private var btnAdd: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val dao = (application as CanStudyDatabaseApp).db.wordDao()

        noSearchResults = binding?.tvNoResultsFound
        searchBar = binding?.etSearchBar
        btnAdd = binding?.btnAddWord
        languageSelected = "English"

        setupRadioGroupListener()
        setupWordRecyclerView(dao)
        setupEnglishSearchListener()

        btnAdd?.setOnClickListener {
            addWordDialog()
        }
    }

    private fun addWordDialog() {
        val wordDialog = Dialog(this)
        val dialogBinding = DialogAddWordBinding.inflate(layoutInflater)
        wordDialog.setContentView(dialogBinding.root)
        wordDialog.setCanceledOnTouchOutside(true)

        // Make dialog match the width of the screen
        val window = wordDialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams

        dialogBinding.btnYes.setOnClickListener {
            wordDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            wordDialog.dismiss()
        }

        wordDialog.show()
    }


    private fun setupRadioGroupListener() {
        binding?.rgLanguage?.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == binding?.rbEnglish?.id) {
                binding?.etSearchBar?.setText("")
                setupEnglishSearchListener()
            } else {
                binding?.etSearchBar?.setText("")
                languageSelected = "Cantonese"
                setupCantoneseSearchListener()
            }
        }
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

    private fun setupEnglishSearchListener() {
        searchBar?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val query = s.toString()
                filterWithQuery(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCantoneseSearchListener() {
        searchBar?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val query = s.toString()
                filterWithQuery(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterWithQuery(query: String) {
        val dao = (application as CanStudyDatabaseApp).db.wordDao()
        getWordList(dao) { wordList ->
            val filteredList: ArrayList<WordModel> = ArrayList()
            for (word in wordList) {
                if (languageSelected.equals("English")) {
                    if (word.getEnglishWord().lowercase().contains(query.lowercase())) {
                        filteredList.add(word)
                    }
                } else {
                    if (word.getCantoneseWord().lowercase().contains(query.lowercase())) {
                        filteredList.add(word)
                    }
                }
            }
            toggleRecyclerView(filteredList)
            attachAdapter(filteredList)
        }
    }

    private fun getWordList(wordDao: WordDao, callback: (ArrayList<WordModel>) -> Unit) {
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

                    callback(wordList)
                }
            }
        }
    }

    private fun attachAdapter(list: ArrayList<WordModel>) {
        val wordAdapter = WordAdapter(list)
        binding?.rvSearch?.adapter = wordAdapter
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}