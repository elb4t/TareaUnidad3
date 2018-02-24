package es.elb4t.bouncingball

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.SurfaceView


/**
 * Created by eloy on 24/2/18.
 */
class BouncingBallThread(view: SurfaceView) : Thread() {
    val FPS: Long = 10
    private var superfView: SurfaceView? = null
    private var width: Int = 0
    private var height: Int = 0
    private var running = false
    private var pos_x = -1
    private var pos_y = -1
    private var xVelocidad = 10
    private var yVelocidad = 5
    private var pelota: BitmapDrawable? = null

    init {
        this.superfView = view
        // Coloca una imagen de tu elección
        pelota = view.context.resources.getDrawable(R.drawable.pelota) as BitmapDrawable?
    }

    fun setRunning(run: Boolean) {
        running = run
    }

    override fun run() {
        val ticksPS = (1000 / this.FPS)
        var startTime: Long
        var sleepTime: Long
        while (running) {
            var canvas: Canvas? = null
            startTime = System.currentTimeMillis()

            try {
                // Bloqueamos el canvas de la superficie para dibujarlo
                canvas = superfView!!.holder.lockCanvas()
                // Sincronizamos el método draw() de la superficie para
                // que se ejecute como un bloque
                synchronized(superfView!!.holder) {
                    if (canvas != null) doDraw(canvas)
                }
            } finally {
                // Liberamos el canvas de la superficie desbloqueándolo
                if (canvas != null) {
                    superfView!!.holder.unlockCanvasAndPost(canvas)
                }
            }
            // Tiempo que debemos parar la ejecución del hilo
            sleepTime = ticksPS - System.currentTimeMillis() - startTime
            // Paramos la ejecución del hilo
            try {
                if (sleepTime > 0)
                    Thread.sleep(sleepTime)
                else
                    Thread.sleep(10)
            } catch (e: Exception) {
            }
        }
    }

    protected fun doDraw(canvas: Canvas) {
        if (pos_x < 0 && pos_y < 0) {
            pos_x = this.width / 2
            pos_y = this.height / 2
        } else {
            pos_x += xVelocidad
            pos_y += yVelocidad
            if (pos_x > this.width - pelota!!.bitmap.width || pos_x < 0) {
                xVelocidad *= -1
            }
            if (pos_y > this.height - pelota!!.bitmap.height || pos_y < 0) {
                yVelocidad *= -1
            }
        }
        canvas.drawColor(Color.LTGRAY)
        canvas.drawBitmap(pelota!!.bitmap, pos_x.toFloat(), pos_y.toFloat(), null)
    }

    fun setSurfaceSize(width: Int, heigth: Int) {
        synchronized(superfView!!) {
            this.width = width
            this.height = height
        }
    }
}