package es.elb4t.calculadorasha1broadcast

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    var mService: Sha1HashBroadcastService? = null
    var mBound = false

    private val mReceiver = DigestReceiver()

    private class DigestReceiver : BroadcastReceiver() {

        private var view: TextView? = null

        override fun onReceive(context: Context, intent: Intent) {

            if (view != null) {
                val result = intent.getStringExtra(Sha1HashBroadcastService.RESULT)
                view!!.text = result
            } else {
                Log.i("Sha1HashService", " ignoring - we're detached")
            }
        }

        fun attach(view: TextView) {
            this.view = view
        }

        fun detach() {
            this.view = null
        }
    };

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val queryButton = findViewById<View>(R.id.hashIt) as Button
        queryButton.setOnClickListener {
            val et = findViewById<View>(R.id.text) as EditText
            if (mService != null) {
                mService!!.getSha1Digest(et.text.toString())
            }
        }

    }

    override fun onStart() {
        super.onStart()
        // Bind to LocalService
        val intent = Intent(this, Sha1HashBroadcastService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        mReceiver.attach(findViewById<View>(R.id.hashResult) as TextView)
        val filter = IntentFilter(
                Sha1HashBroadcastService.SHA1_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
        mReceiver.detach()
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as Sha1HashBroadcastService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            mService = null
        }
    }
}
