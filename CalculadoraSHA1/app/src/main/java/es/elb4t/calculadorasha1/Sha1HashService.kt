package es.elb4t.calculadorasha1

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.service.carrier.CarrierMessagingService
import android.service.carrier.CarrierMessagingService.ResultCallback
import android.util.Log
import java.lang.ref.WeakReference
import java.security.MessageDigest
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.experimental.and


class Sha1HashService : Service() {

    private val mBinder = LocalBinder()
    private val TAG = "Sha1HashService"
    private val CORE_POOL_SIZE = 2
    private val MAXIMUM_POOL_SIZE = 4
    private val MAX_QUEUE_SIZE = 16
    private val sPoolWorkQueue = LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE)

    private val sThreadFactory = object : ThreadFactory {
        private val mCount = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread {
            val t = Thread(r, "SHA1HashService #" + mCount.getAndIncrement())
            t.priority = Thread.MIN_PRIORITY
            return t
        }
    }
    private var mExecutor: ThreadPoolExecutor? = null

    inner class LocalBinder : Binder() {
        internal val service: Sha1HashService
            get() = this@Sha1HashService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    @SuppressLint("NewApi")
    private fun postResultOnUI(result: String, callback:  WeakReference<CarrierMessagingService.ResultCallback<String>> ){

        val mainLooper = Looper.getMainLooper()
        val handler = Handler(mainLooper)
        handler.post {
            if (callback.get() != null) {
                callback.get()!!.onReceiveResult(result)
            }
        }
    }

    fun getSha1Digest(text: String, callback: ResultCallback<String>) {
        val ref = WeakReference(callback)
        val runnable = Runnable {
            Log.i(TAG, "Hashing text " + text + " on Thread " +
                    Thread.currentThread().name)
            try {
                // Execute the Long Running Computation
                val result = SHA1(text)
                Log.i(TAG, "Hash result for $text is $result") // Execute the Runnable on UI Thread
                postResultOnUI(result, ref)
            } catch (e: Exception) {
                Log.e(TAG, "Hash failed", e)
            }
        }
        // Submit the Runnable on the ThreadPool
        mExecutor!!.execute(runnable)
    }


    override fun onCreate() {
        Log.i(TAG, "Starting Hashing Service")
        super.onCreate()
        mExecutor = ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                5, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory)
        mExecutor!!.prestartAllCoreThreads()
    }

    override fun onDestroy() {
        Log.i(TAG, "Stopping Hashing Service")
        super.onDestroy()
        mExecutor!!.shutdown()
    }

    @Throws(Exception::class)
    fun SHA1(text: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        md.update(text.toByteArray(charset("iso-8859-1")), 0, text.length)
        val sha1hash = md.digest()
        return convertToHex(sha1hash)
    }

    private fun convertToHex(data: ByteArray?): String {
        val buf = StringBuilder()
        for (b in data!!) {
            var halfbyte = b.compareTo(4) and 0x0F
            var two_halfs = 0
            do {
                buf.append(if (0 <= halfbyte && halfbyte <= 9) ('0'.toInt() + halfbyte).toChar() else ('a'.toInt() + (halfbyte - 10)).toChar())
                halfbyte = (b and 0x0F).toInt()
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }
}
