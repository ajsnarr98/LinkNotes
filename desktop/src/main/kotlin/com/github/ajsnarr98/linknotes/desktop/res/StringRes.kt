package com.github.ajsnarr98.linknotes.desktop.res

interface StringRes {
    val languageTitle: String

    // login
    val login: Login
    interface Login {
        val title: String
        val signInWithGoogle: String
    }
}

class AmericanEnglishStringRes : StringRes {
    override val languageTitle: String = "English (US)"

    override val login = object : StringRes.Login {
        override val title: String = "Login"
        override val signInWithGoogle: String = "Sign in with Google"
    }
}