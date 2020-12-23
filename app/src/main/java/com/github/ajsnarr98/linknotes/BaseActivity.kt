package com.github.ajsnarr98.linknotes

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Gets the root view for the activity.
     */
    abstract val rootView: ViewGroup
}
