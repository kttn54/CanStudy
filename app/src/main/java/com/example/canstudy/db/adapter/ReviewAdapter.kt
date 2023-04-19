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
                    R.color.colorWhite
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    fun deleteItem(position: Int) {
        if (position >= 0 && position < wordList.size) {
            Log.e("asdf", "ReviewAdapter position is $position, word removed is ${wordList[position]}")
            wordList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

/*
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

 */
