package com.gondev.ably.subject

import android.app.Application
import com.gondev.ably.subject.util.timber.DebugLogTree
import com.gondev.ably.subject.util.timber.ReleaseLogTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class RootApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ReleaseLogTree())
        }
    }
}