package com.actiangent.wasteye

import android.app.Application
import com.actiangent.wasteye.di.Injection
import com.yariksoffice.lingver.Lingver

class WasteyeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Lingver.init(this)

        injection = Injection(applicationContext)
    }

    companion object {
        lateinit var injection: Injection
    }
}