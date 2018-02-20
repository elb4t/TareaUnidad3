package es.elb4t.primosinterfaces

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText


class MainActivity : AppCompatActivity(), TaskListener {
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
        if (mAsyncTask == null)
            mAsyncTask = MyAsyncTask(this)
        if (mAsyncTask!!.status !== AsyncTask.Status.RUNNING) {
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": triggerPrimecheck() comienza")
            val parameter = java.lang.Long.parseLong(inputField!!.text.toString())
            mAsyncTask = MyAsyncTask(this)
            mAsyncTask!!.execute(parameter)
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": triggerPrimecheck() termina")
        } else {
            Log.v(TAG, "Cancelando test " + Thread.currentThread().id)
            mAsyncTask!!.cancel(true)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
        if (mAsyncTask != null && mAsyncTask!!.status == AsyncTask.Status.RUNNING) {
            mAsyncTask!!.cancel(true)
        }
    }

    override fun onPreExecute() {
        resultField.setText("")
        primecheckbutton.text = "CANCELAR"
        lockScreenOrientation()
    }

    override fun onProgressUpdate(progreso: Double?) {
        resultField.setText(String.format("%.1f%% completado", progreso!! * 100))
    }

    override fun onPostExecute(resultado: Boolean) {
        resultField.setText(resultado.toString() + "")
        primecheckbutton.text = "¿ES PRIMO?"
        unlockScreenOrientation()
    }

    override fun onCancelled() {
        resultField.setText("Proceso cancelado")
        primecheckbutton.text = "¿ES PRIMO?"
    }

    fun lockScreenOrientation() {
        var currentOrientation: Int = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    fun unlockScreenOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

}