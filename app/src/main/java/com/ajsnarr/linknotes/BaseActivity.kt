package com.ajsnarr.linknotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajsnarr.linknotes.data.NoteCollection

open class BaseActivity : AppCompatActivity() {

    protected val mNotesCollection: NoteCollection = NoteCollection.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNotesCollection.onActivityCreate()
    }

    override fun onStart() {
        super.onStart()
        mNotesCollection.onActivityStart()
    }

    override fun onStop() {
        super.onStop()
        mNotesCollection.onActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mNotesCollection.onActivityDestroy()
    }
}
