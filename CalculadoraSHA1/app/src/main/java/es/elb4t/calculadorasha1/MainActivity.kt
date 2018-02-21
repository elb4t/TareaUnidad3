package es.elb4t.calculadorasha1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.service.carrier.CarrierMessagingService
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity(), CarrierMessagingService.ResultCallback<String> {

    var mService: Sha1HashService? = null
    var mBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val queryButton = findViewById<Button>(R.id.hashIt)
        queryButton.setOnClickListener{
                val et = findViewById<View>(R.id.text) as EditText
                if (mService != null) {
                    mService!!.getSha1Digest(et.text.toString(), this)
                }
        }
    }
    override fun onReceiveResult(data: String?) {
        val et = findViewById<TextView>(R.id.hashResult)
        et.text = data
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, Sha1HashService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName,
                                        service: IBinder) {
            val binder = service as Sha1HashService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            mService = null
        }
    }
}
