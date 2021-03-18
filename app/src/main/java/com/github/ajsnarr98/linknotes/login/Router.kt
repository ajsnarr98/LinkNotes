package com.github.ajsnarr98.linknotes.login

import android.content.Context
import android.content.Intent
import com.github.ajsnarr98.linknotes.search.SearchActivity

object Router {
    fun postLoginIntent(context: Context): Intent {
        return SearchActivity.getSearchIntent(context)
    }
}