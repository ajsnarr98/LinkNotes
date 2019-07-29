package com.ajsnarr.peoplenotes.router

import android.content.Context
import android.content.Intent
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.notes.EditNoteActivity
import com.ajsnarr.peoplenotes.search.SearchActivity

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
