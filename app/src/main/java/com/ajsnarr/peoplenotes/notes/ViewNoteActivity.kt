package com.ajsnarr.peoplenotes.notes


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.ajsnarr.peoplenotes.BaseActivity
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Note
import java.lang.IllegalStateException

class ViewNoteActivity : BaseActivity() {

    companion object {

        const val NOTE_INTENT_KEY = "note"

        fun getIntent(context: Context, note: Note): Intent {
            return Intent(context, EditNoteActivity::class.java).apply {
                putExtra(NOTE_INTENT_KEY, note)
            }
        }
    }

    lateinit var viewModel: EditNoteViewModel

//    protected lateinit var recyclerAdapter: ViewNoteAdapter
//    private val mRecyclerActionListener = ViewNoteActivity.RecyclerActionListener(this)

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
    }
}