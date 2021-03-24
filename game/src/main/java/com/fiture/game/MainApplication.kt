package com.fiture.game

import android.app.Application
import android.widget.Toast
import com.fiture.platform.IBridge

/**
 *<pre>
 *  author : juneYang
 *  time   : 2021/03/11 6:03 PM
 *  desc   :
 *  version: 1.0
 *</pre>
 */
class MainApplication : Application() {

    private val mPackageName = "com.fiture.platform.aidlserver"

    override fun onCreate() {
        super.onCreate()
        IBridge.init(this, mPackageName)
    }

    override fun onTerminate() {
        IBridge.recycle()
        super.onTerminate()
    }
}