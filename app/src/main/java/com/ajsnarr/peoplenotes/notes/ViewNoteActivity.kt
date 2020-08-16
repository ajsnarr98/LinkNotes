package com.ajsnarr.peoplenotes.notes


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajsnarr.peoplenotes.BaseActivity
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Note
import kotlinx.android.synthetic.main.activity_viewnote.*
import java.lang.IllegalStateException

open class ViewNoteActivity : BaseActivity() {

    companion object {

        const val NOTE_INTENT_KEY = "note"

        fun getIntent(context: Context, note: Note): Intent {
            return Intent(context, EditNoteActivity::class.java).apply {
                putExtra(NOTE_INTENT_KEY, note)
            }
        }
    }

    lateinit var viewModel: EditNoteViewModel

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
        setContentView(R.layout.activity_viewnote)

        val inNote: Note? = intent.getParcelableExtra(NOTE_INTENT_KEY)

        if (inNote != null) {
            viewModel = ViewModelProviders.of(this, ViewNoteViewModel.Factory(inNote))
                .get(EditNoteViewModel::class.java)
        } else {
            throw IllegalStateException("No note provided to ViewNoteActivity.")
        }

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = ViewNoteAdapter(viewModel.note, mRecyclerActionListener)

        recycler_view.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> return true // TODO
        }
        return super.onOptionsItemSelected(item)
    }
}