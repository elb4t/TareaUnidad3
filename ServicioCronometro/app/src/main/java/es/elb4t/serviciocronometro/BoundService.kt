package es.elb4t.serviciocronometro

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer





/**
 * Created by eloy on 20/2/18.
 */
class BoundService: Service() {
    private val TAG = "BoundService"
    private val mBinder = MyBinder()
    private var mChronometer: Chronometer? = null

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "in onCreate")
        mChronometer = Chronometer(this)
        mChronometer!!.base = SystemClock.elapsedRealtime()
        mChronometer!!.start()
    }

    override fun onBind(p0: Intent?): IBinder {
        Log.v(TAG, "in onBind");
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        Log.v(TAG, "in onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.v(TAG, "in onUnbind")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "in onDestroy")
        mChronometer!!.stop()
    }

    fun getTimestamp(): String {
        var elapsedMillis: Long = SystemClock.elapsedRealtime()-mChronometer!!.base
        var hours: Int =(elapsedMillis / 3600000).toInt()
        var minutes: Int = ((elapsedMillis - hours * 3600000) / 60000).toInt()
        var seconds: Int = ((elapsedMillis - hours * 3600000 - minutes * 60000) / 1000).toInt()
        var millis: Int =(elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000).toInt()
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis)
    }
    inner class MyBinder : Binder() {
        val service: BoundService
            get() = this@BoundService
    }
}