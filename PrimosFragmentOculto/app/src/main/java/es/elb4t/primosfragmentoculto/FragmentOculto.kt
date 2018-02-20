package es.elb4t.primosfragmentoculto

import android.app.Activity
import android.app.Fragment
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log


class FragmentOculto : Fragment() {

    internal interface TaskListener {
        fun onPreExecute()
        fun onProgressUpdate(progreso: Double)
        fun onPostExecute(resultado: Boolean)
        fun onCancelled()
    }
    companion object {
        val TAG = FragmentOculto::class.java.name
    }
    private var taskListener: TaskListener? = null
    private var myAsyncTask: MyAsyncTask? = null
    private var numComprobar: Long = 0

    fun newInstance(argumentos: Bundle?): FragmentOculto {
        val f = FragmentOculto()
        if (argumentos != null) {
            f.arguments = argumentos
        }
        return f
    }

    override fun onAttach(actividad: Activity) {
        super.onAttach(actividad)
        try {
            this.taskListener = actividad as TaskListener
        } catch (ex: ClassCastException) {
            Log.e(TAG, "El Activity debe implementar la interfaz TaskListener")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        var parameters: Bundle = this.arguments
        if (parameters != null)
            this.numComprobar = parameters.getLong("numComprobar", 0)
        myAsyncTask =  MyAsyncTask()
        myAsyncTask!!.execute(this.numComprobar)
    }

    override fun onDetach() {
        this.taskListener = null
        super.onDetach()
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
            taskListener!!.onPreExecute()
        }

        override fun onProgressUpdate(vararg progress: Double?) {
            Log.v(TAG, "Thread " + Thread.currentThread().id +
                    ": onProgressUpdate()")
            taskListener!!.onProgressUpdate(progress[0]!!)
        }

        override fun onPostExecute(isPrime: Boolean?) {
            Log.v(TAG, "Thread" + Thread.currentThread().id + ": onPostExecute()")
            taskListener!!.onPostExecute(isPrime!!)
        }

        override fun onCancelled() {
            Log.v(TAG, "Thread " + Thread.currentThread().id + ": onCancelled")
            taskListener!!.onCancelled()
            super.onCancelled()
        }
    }
}
