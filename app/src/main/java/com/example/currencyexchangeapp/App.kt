package com.example.currencyexchangeapp

import android.app.Application
import com.example.currencyexchangeapp.utils.TimberConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(TimberConfig())
        }
    }
}