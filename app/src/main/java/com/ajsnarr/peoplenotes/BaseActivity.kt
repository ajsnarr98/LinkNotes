package com.ajsnarr.peoplenotes

import androidx.appcompat.app.AppCompatActivity
import com.ajsnarr.peoplenotes.data.NoteCollection

open class BaseActivity : AppCompatActivity() {

    protected val mNotesCollection: NoteCollection = NoteCollection.instance

    override fun onStart() {
        super.onStart()
        mNotesCollection.onActivityStart()
    }

    override fun onStop() {
        super.onStop()
        mNotesCollection.onActivityStop()
    }
}
