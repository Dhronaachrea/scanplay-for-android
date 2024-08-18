package com.skilrock.scanplay.utility

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi

class MyApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication private set
    }
}