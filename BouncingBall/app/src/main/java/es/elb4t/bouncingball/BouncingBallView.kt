package es.elb4t.bouncingball

import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView


/**
 * Created by eloy on 24/2/18.
 */
class BouncingBallView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var bbThread: BouncingBallThread? = null

    init {
        if (bbThread == null) {
            bbThread = BouncingBallThread(this)

            holder.addCallback(this)

        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Si la superficie cambia, entonces guardamos el tamaño de la
        // pantalla
        bbThread!!.setSurfaceSize(width, height)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        bbThread!!.setRunning(true)
        bbThread!!.start()
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // Si la superficie se destruye, entonces paramos el hilo
        var reintentar = true
        bbThread!!.setRunning(false)

        while (reintentar) {
            try {
                bbThread!!.join()
                reintentar = false
            } catch (e: InterruptedException) {
            }

        }
    }
}