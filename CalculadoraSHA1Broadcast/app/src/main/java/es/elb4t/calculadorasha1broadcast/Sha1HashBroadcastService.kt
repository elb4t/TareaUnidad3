package es.elb4t.calculadorasha1broadcast

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.security.MessageDigest
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.experimental.and


class Sha1HashBroadcastService : Service() {

    // Binder given to clients
    private val mBinder = LocalBinder()

    private var mExecutor: ThreadPoolExecutor? = null

    inner class LocalBinder : Binder() {
        internal// Return this instance of LocalService so clients can call public methods
        val service: Sha1HashBroadcastService
            get() = this@Sha1HashBroadcastService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }


    private fun broadcastResult(result: String) {
        val intent = Intent(SHA1_BROADCAST)
        intent.putExtra(RESULT, result)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    internal fun getSha1Digest(text: String) {

        val runnable = Runnable {
            Log.i("Sha1HashService", "Hashing text " + text + " on Thread " +
                    Thread.currentThread().name)
            try {
                // Execute the Long Running Computation
                val result = SHA1(text)
                Log.i("Sha1HashService", "Hash result for $text is $result")
                // broadcast result to Subscribers
                broadcastResult(result)
            } catch (e: Exception) {
                Log.e("Sha1HashService", "Hash failed", e)
            }
        }
        // Submit the Runnable on the ThreadPool
        mExecutor!!.execute(runnable)
    }


    override fun onCreate() {

        Log.i("Sha1HashService", "Starting Hashing Service")
        super.onCreate()
        mExecutor = ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 5,
                TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory)
        mExecutor!!.prestartAllCoreThreads()

    }

    override fun onDestroy() {
        Log.i("Sha1HashService", "Stopping Hashing Service")
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

    private fun convertToHex(data: ByteArray): String {
        val buf = StringBuilder()
        for (b in data) {
            var halfbyte = b.compareTo(4) and 0x0F
            var two_halfs = 0
            do {
                buf.append(if (0 <= halfbyte && halfbyte <= 9)
                    ('0'.toInt() + halfbyte).toChar()
                else
                    ('a'.toInt() + (halfbyte - 10)).toChar())
                halfbyte = (b and 0x0F).toInt()
            } while (two_halfs++ < 1)
        }
        return buf.toString()
    }

    companion object {
        val SHA1_BROADCAST = "SHA1_BROADCAST"
        val RESULT = "sha1"
        //
        private val CORE_POOL_SIZE = 2
        private val MAXIMUM_POOL_SIZE = 4
        private val MAX_QUEUE_SIZE = 16

        private val sPoolWorkQueue = LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE)

        private val sThreadFactory = object : ThreadFactory {

            private val mCount = AtomicInteger(1)

            override fun newThread(r: Runnable): Thread {
                val t = Thread(r, "Sha1HashBroadcastService #" + mCount.getAndIncrement())
                t.priority = Thread.MIN_PRIORITY
                return t
            }
        }
    }
}
