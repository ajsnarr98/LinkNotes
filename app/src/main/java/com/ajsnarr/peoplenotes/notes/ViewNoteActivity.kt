package com.ajsnarr.peoplenotes.notes


import android.os.Bundle
import com.ajsnarr.peoplenotes.BaseActivity
import com.ajsnarr.peoplenotes.R

class ViewNoteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewnote)
    }
}