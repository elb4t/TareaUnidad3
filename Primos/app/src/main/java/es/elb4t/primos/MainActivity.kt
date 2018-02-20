package es.elb4t.primos

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText




class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.name
    }
    private var inputField: EditText? = null
    private lateinit var resultField: EditText
    private lateinit var primecheckbutton: Button
    private var mAsyncTask: MyAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputField = findViewById(R.id.inputField)
        resultField = findViewById(R.id.resultField)
        primecheckbutton = findViewById(R.id.primecheckbutton)
    }

    fun triggerPrimecheck(v: View) {
        if (mAsyncTask!!.status == AsyncTask.Status.RUNNING) {
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": triggerPrimecheck() comienza")
            val parameter = java.lang.Long.parseLong(inputField!!.text.toString())
            mAsyncTask = MyAsyncTask()
            mAsyncTask!!.execute(parameter)
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": triggerPrimecheck() termina")
        } else {
            Log.v(TAG, "Cancelando test " + Thread.currentThread().id)
            mAsyncTask!!.cancel(true)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAsyncTask != null && mAsyncTask!!.status == AsyncTask.Status.RUNNING) {
            mAsyncTask!!.cancel(true)
        }
    }

    private inner class MyAsyncTask : AsyncTask<Long, Double, Boolean>() {
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
            resultField!!.setText("")
            primecheckbutton!!.text = "CANCELAR"
        }

        override fun onProgressUpdate(vararg progress: Double?) {
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": onProgressUpdate()")
            resultField!!.setText(String.format("%.1f%% completed", progress[0]!! * 100))
        }

        override fun onPostExecute(isPrime: Boolean?) {
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": onPostExecute()")
            resultField!!.setText(isPrime!!.toString())
            primecheckbutton.text = "¿ES PRIMO?"
        }

        override fun onCancelled() {
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": onCancelled")
            super.onCancelled()
            resultField.setText("Proceso cancelado")
            primecheckbutton.text = "¿ES PRIMO?"
        }
    }


}
