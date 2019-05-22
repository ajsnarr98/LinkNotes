package com.ajsnarr.peoplenotes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry


class EntryAdapter(private val entries: MutableList<Entry>) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_editnote_entry, parent, false)
        )
    }

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.onBind(entry)
    }

    class EntryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun onBind(entry: Entry) {
            val entry_type = view.findViewById<EditText>(R.id.textinput_editnote_entrytype)
            val entry_content = view.findViewById<EditText>(R.id.edittext_editnote_content)

            entry_content.text.append(entry.content.toString())
        }
    }
}