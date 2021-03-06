package com.github.ajsnarr98.linknotes.notes


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajsnarr98.linknotes.BaseActivity
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.databinding.ActivityViewnoteBinding
import com.github.ajsnarr98.linknotes.util.createMarkwonInstance
import io.noties.markwon.Markwon
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class ViewNoteActivity : BaseActivity() {

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
    override val rootView: ViewGroup get() = binding.root

    private lateinit var recyclerAdapter: ViewNoteAdapter
    private val mRecyclerActionListener by lazy { RecyclerActionListener(this) }
    private val markwon: Markwon by lazy { createMarkwonInstance(this) }

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

        if (inNoteId != null) {
            viewModel = ViewModelProvider(this, ViewNoteViewModel.Factory(inNoteId))
                .get(ViewNoteViewModel::class.java)
        } else {
            throw IllegalStateException("No note provided to ViewNoteActivity.")
        }
        viewModel.lifecycleObservers.forEach { lifecycle.addObserver(it) }

        // add up navigation button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = ViewNoteAdapter(viewModel, mRecyclerActionListener, markwon)

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
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}