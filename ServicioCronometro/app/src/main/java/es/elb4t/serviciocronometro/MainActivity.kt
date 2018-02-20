package es.elb4t.serviciocronometro

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView





class MainActivity : AppCompatActivity() {

    var mBoundService: BoundService? = null
    var mServiceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var timestampText: TextView = findViewById(R.id.timestamptext)
        var btnPrintTimeStamp:Button = findViewById(R.id.btnPrintTimeStamp)
        var btnStopService: Button = findViewById(R.id.btnStopService)

        btnPrintTimeStamp.setOnClickListener{
            timestampText.text = mBoundService!!.getTimestamp()
        }

        btnStopService.setOnClickListener {
            if (mServiceBound) {
                unbindService(mServiceConnection)
                mServiceBound = false
            }
            val i = Intent(this@MainActivity, BoundService::class.java)
            stopService(i)
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, BoundService::class.java)
        startService(intent)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mServiceBound) {
            unbindService(mServiceConnection)
            mServiceBound = false
        }
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as BoundService.MyBinder
            mBoundService = myBinder.service
            mServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mServiceBound = false
        }
    }
}
