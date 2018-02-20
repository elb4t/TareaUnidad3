package es.elb4t.primosinterfaces

import android.os.AsyncTask
import android.util.Log

/**
 * Created by eloy on 19/2/18.
 */
class MyAsyncTask(private val listener: TaskListener) : AsyncTask<Long, Double, Boolean>() {
    private val TAG = MyAsyncTask::class.java.name


    override fun doInBackground(vararg n: Long?): Boolean? {
        Log.v(TAG, "Thread " + Thread.currentThread().id + ": doInBackground() starts")
        val numComprobar = n[0]
        if (numComprobar!! < 2 || numComprobar % 2 == 0L)
            return false
        val limit = Math.sqrt(numComprobar.toDouble()) + 0.0001
        var progress = 0.0
        var factor: Long = 3
        while (factor < limit && !isCancelled) {
            if (numComprobar % factor == 0L)
                return false

            if (factor > limit * progress / 100) {
                publishProgress(progress / 100)
                progress += 5.0
            }
            factor += 2
        }
        Log.v(TAG, "Thread " + Thread.currentThread().id + ": doInBackground() ends")
        return true
    }

    override fun onPreExecute() {
        Log.v(TAG, "Thread " + Thread.currentThread().id + ": onPreExecute()")
        listener.onPreExecute();
    }

    override fun onProgressUpdate(vararg progress: Double?) {
        Log.v(TAG, "Thread " + Thread.currentThread().id + ": onProgressUpdate()")
        listener.onProgressUpdate(progress[0])
    }

    override fun onPostExecute(isPrime: Boolean) {
        Log.v(TAG, "Thread " + Thread.currentThread().id + ": onPostExecute()")
        listener.onPostExecute(isPrime)
    }

    override fun onCancelled() {
        Log.v(TAG, "Thread " + Thread.currentThread().id + ": onCancelled")
        listener.onCancelled()
        super.onCancelled()
    }
}