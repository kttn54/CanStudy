package com.example.canstudy.viewModel

import com.example.canstudy.db.dao.WordDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val wordDao: WordDao
) {


}