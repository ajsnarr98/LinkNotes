package com.ajsnarr.peoplenotes.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.notes.EditNoteActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.w3c.dom.Text

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
        val full_card = holder.view.findViewById<View>(R.id.result_card)
        val icon = holder.view.findViewById<ImageView>(R.id.note_icon)
        val title = holder.view.findViewById<TextView>(R.id.title)
        val chipGroup = holder.view.findViewById<ChipGroup>(R.id.chip_group)

        val note = results[position]

        title.text = note.name

        full_card.setOnClickListener {
            actionListener.onResultClick(note)
        }

        for (tag in note.tags) {
            // add a new chip with text matching the tag

            chipGroup.addView(Chip(context).apply {
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

        icon.setImageResource(R.drawable.default_profile) // TODO - load image
    }

    class ResultViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
