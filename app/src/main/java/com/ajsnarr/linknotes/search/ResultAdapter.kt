package com.ajsnarr.linknotes.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.linknotes.R
import com.ajsnarr.linknotes.data.Note
import com.ajsnarr.linknotes.databinding.ItemSearchResultBinding
import com.google.android.material.chip.Chip

class ResultAdapter(val context: Context, val actionListener: ActionListener)
    : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    var results: List<Note> = mutableListOf()

    interface ActionListener {
        fun onResultClick(note: Note)
    }

    fun updateResults(results: List<Note>) {
        this.results = results
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun getItemCount() = results.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {

        val note = results[position]

        holder.binding.title.text = note.name

        holder.binding.resultCard.setOnClickListener {
            actionListener.onResultClick(note)
        }

        for (tag in note.tags) {
            // add a new chip with text matching the tag

            holder.binding.chipGroup.addView(Chip(context).apply {
                isClickable = false

                text = tag.text
                textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5f, resources.displayMetrics)
                textAlignment = View.TEXT_ALIGNMENT_CENTER

                chipMinHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
                chipStartPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, resources.displayMetrics)
                chipEndPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, resources.displayMetrics)
                chipBackgroundColor = ColorStateList.valueOf(Color.rgb(tag.color.r, tag.color.g, tag.color.b))
            })
        }

        holder.binding.icon.setImageResource(R.drawable.default_profile) // TODO - load image
    }

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSearchResultBinding.bind(view)
    }
}