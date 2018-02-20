package es.elb4t.messengerservice

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Chronometer
import java.lang.ref.WeakReference


class MessengerService : Service() {

    private val LOG_TAG = "MessengerService"
    private var mChronometer: Chronometer? = null
    companion object {
        val MSG_GET_TIMESTAMP = 1000
    }

    internal class BoundServiceHandler(service: MessengerService) : Handler() {
        private val mService: WeakReference<MessengerService> = WeakReference(service)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_GET_TIMESTAMP -> {
                    val elapsedMillis = SystemClock.elapsedRealtime() - mService.get()!!.mChronometer!!.base
                    val hours = (elapsedMillis / 3600000).toInt()
                    val minutes = (elapsedMillis - hours * 3600000).toInt() / 60000
                    val seconds = (elapsedMillis - (hours * 3600000).toLong() - (minutes * 60000).toLong()).toInt() / 1000
                    val millis = (elapsedMillis - (hours * 3600000).toLong() - (minutes * 60000).toLong() - (seconds * 1000).toLong()).toInt()
                    val activityMessenger = msg.replyTo
                    val b = Bundle()
                    b.putString("timestamp",
                            String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis))
                    val replyMsg = Message.obtain(null, MSG_GET_TIMESTAMP)
                    replyMsg.data = b
                    try {
                        activityMessenger.send(replyMsg)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }

                }
                else -> super.handleMessage(msg)
            }
        }
    }

    val mMessenger = Messenger(BoundServiceHandler(this))

    override fun onCreate() {
        super.onCreate()
        Log.v(LOG_TAG, "in onCreate")
        mChronometer = Chronometer(this)
        mChronometer!!.base = SystemClock.elapsedRealtime()
        mChronometer!!.start()
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.v(LOG_TAG, "in onBind")
        return mMessenger.getBinder()
    }

    override fun onRebind(intent: Intent) {
        Log.v(LOG_TAG, "in onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.v(LOG_TAG, "in onUnbind")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(LOG_TAG, "in onDestroy")
        mChronometer!!.stop()
    }
}
