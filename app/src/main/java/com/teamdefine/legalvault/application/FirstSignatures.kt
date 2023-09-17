package com.teamdefine.legalvault.application

import android.app.Application
import com.google.firebase.FirebaseApp

class FirstSignatures : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }
}