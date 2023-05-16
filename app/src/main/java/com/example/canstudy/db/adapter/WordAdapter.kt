package com.example.canstudy.db.adapter

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.canstudy.R
import com.example.canstudy.databinding.ItemWordRowBinding
import com.example.canstudy.db.entity.WordEntity

class WordAdapter(private var wordList: ArrayList<WordEntity>): RecyclerView.Adapter<WordAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemWordRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val llWordItemMain = binding.llWordItemMain
        val tvCantoneseWord = binding.tvCantoneseWord
        val tvEnglishWord = binding.tvEnglishWord
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWordRowBinding.inflate(LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: WordEntity = wordList[position]
        holder.tvEnglishWord.text = model.getEnglishWord()
        holder.tvCantoneseWord.text = model.getCantoWord()

        if (position % 2 == 0) {
            holder.llWordItemMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorLightGray
                )
            )
        } else {
            holder.llWordItemMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorWhite
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return wordList.size
    }
}