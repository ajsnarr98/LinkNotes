package com.github.ajsnarr98.linknotes

import android.app.Application
import com.github.ajsnarr98.linknotes.data.local.AccountStore
import com.github.ajsnarr98.linknotes.data.local.UnsavedChangeStore
import timber.log.Timber

import timber.log.Timber.DebugTree

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        Providers.apply {
            accountStoreProvider = Providers.BasicProvider(
                AccountStore(this@App)
            )
            unsavedChangeStoreProvider = Providers.BasicProvider(
                UnsavedChangeStore(this@App)
            )
        }
    }
}
