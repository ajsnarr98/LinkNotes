package com.ajsnarr.linknotes.notes


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajsnarr.linknotes.BaseActivity
import com.ajsnarr.linknotes.R
import com.ajsnarr.linknotes.data.Note
import com.ajsnarr.linknotes.data.UUID
import com.ajsnarr.linknotes.databinding.ActivityViewnoteBinding
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

open class ViewNoteActivity : BaseActivity() {

    companion object {

        const val NOTE_INTENT_KEY = "note"

        fun getIntent(context: Context, noteId: UUID): Intent {
            return Intent(context, ViewNoteActivity::class.java).apply {
                putExtra(NOTE_INTENT_KEY, noteId)
            }
        }

        fun getIntent(context: Context, note: Note): Intent {
            return if (note.id != null && note.id.isNotEmpty())
                getIntent(context, note.id)
            else
                throw IllegalArgumentException("Given note cannot have a null id")
        }
    }

    lateinit var viewModel: ViewNoteViewModel

    private lateinit var binding : ActivityViewnoteBinding

    protected lateinit var recyclerAdapter: ViewNoteAdapter
    private val mRecyclerActionListener = RecyclerActionListener(this)

    private class RecyclerActionListener(val activity: ViewNoteActivity)
        : ViewNoteAdapter.ActionListener {

        override fun onEditButtonPress() {
            activity.startActivity(EditNoteActivity.getEditNoteIntent(activity,
                activity.viewModel.note))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewnoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inNoteId: UUID? = intent.getStringExtra(NOTE_INTENT_KEY)
        val inNote: Note? = mNotesCollection.findByID(inNoteId)

        if (inNote != null) {
            viewModel = ViewModelProviders.of(this, ViewNoteViewModel.Factory(inNote))
                .get(ViewNoteViewModel::class.java)
        } else {
            throw IllegalStateException("No note provided to ViewNoteActivity.")
        }

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = ViewNoteAdapter(viewModel, mRecyclerActionListener)

        binding.recyclerView.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> return true // TODO
        }
        return super.onOptionsItemSelected(item)
    }
}