package com.ajsnarr.linknotes.router

import android.content.Context
import android.content.Intent
import com.ajsnarr.linknotes.data.Note
import com.ajsnarr.linknotes.notes.EditNoteActivity
import com.ajsnarr.linknotes.search.SearchActivity

class Router {
    companion object {

        fun createNoteIntent(context: Context): Intent {
            return EditNoteActivity.getCreateNoteIntent(context)
        }

        fun editNoteIntent(context: Context, note: Note): Intent {
            return EditNoteActivity.getEditNoteIntent(context, note)
        }

        fun searchIntent(context: Context): Intent {
            return SearchActivity.getSearchIntent(context)
        }
    }
}
