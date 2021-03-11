package com.fiture.game

import android.app.Application
import android.widget.Toast
import com.sjtu.yifei.IBridge

/**
 *<pre>
 *  author : juneYang
 *  time   : 2021/03/11 6:03 PM
 *  desc   :
 *  version: 1.0
 *</pre>
 */
class MainApplication : Application() {

    private val mPackageName = "com.sjtu.yifei.aidlserver"

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "init", Toast.LENGTH_SHORT).show()
        IBridge.init(this, mPackageName, IBridge.AbridgeType.AIDL)
    }

    override fun onTerminate() {
        IBridge.recycle()
        super.onTerminate()
    }
}