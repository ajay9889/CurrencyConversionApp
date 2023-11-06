package com.demo.currencyconversion

import android.app.Application
import android.content.Context
import com.demo.currencyconversion.koindi.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level

class MainApplication: Application() {
    private val modules = listOf(
        appModule
    )
    override fun onCreate() {
        super.onCreate()
        getStartKoin(this)
    }
    fun getStartKoin(context: Context){
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger(Level.NONE)
                androidContext(context)
                modules(modules)
            }
        }
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}