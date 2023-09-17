package com.teamdefine.legalvault.application

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber

class FirstSignatures : Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initFirebase()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }
}