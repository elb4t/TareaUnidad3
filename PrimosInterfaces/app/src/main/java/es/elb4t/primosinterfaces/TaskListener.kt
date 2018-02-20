package es.elb4t.primosinterfaces

/**
 * Created by eloy on 19/2/18.
 */
interface TaskListener {
    fun onPreExecute()
    fun onProgressUpdate(progreso: Double?)
    fun onPostExecute(resultado: Boolean)
    fun onCancelled()
}