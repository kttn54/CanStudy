package com.example.canstudy.db.adapter

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.canstudy.R
import com.example.canstudy.databinding.ItemWordRowBinding
import com.example.canstudy.db.entity.WordEntity

class ReviewAdapter(private var wordList: ArrayList<WordEntity>, private var toggleTranslation: String): RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemWordRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val llWordItemMain = binding.llWordItemMain
        val tvCantoneseWord = binding.tvCantoneseWord
        val tvEnglishWord = binding.tvEnglishWord
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWordRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: WordEntity = wordList[position]
        holder.tvEnglishWord.text = model.getEnglishWord()
        holder.tvCantoneseWord.text = "Show translation"
        holder.tvCantoneseWord.setTypeface(null, Typeface.ITALIC)

        holder.tvCantoneseWord.setOnClickListener {
            if (holder.tvCantoneseWord.text == "Show translation") {
                holder.tvCantoneseWord.text = model.getCantoWord()
                holder.tvCantoneseWord.setTypeface(null, Typeface.NORMAL)
            } else {
                holder.tvCantoneseWord.text = "Show translation"
                holder.tvCantoneseWord.setTypeface(null, Typeface.ITALIC)
            }
        }

        if (toggleTranslation == "on") {
            holder.tvCantoneseWord.text = model.getCantoWord()
            holder.tvCantoneseWord.setTypeface(null, Typeface.NORMAL)
        } else if (toggleTranslation == "off") {
            holder.tvCantoneseWord.text = "Show translation"
            holder.tvCantoneseWord.setTypeface(null, Typeface.ITALIC)
        }

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
                    R.color.dumpling_white
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    fun deleteItem(position: Int) {
        if (position >= 0 && position < wordList.size) {
            wordList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
