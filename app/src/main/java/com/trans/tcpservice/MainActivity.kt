package com.trans.tcpservice

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ipv4Address = SocketService.getIPAddress(this)
        Log.e(TAG, "IP地址:$ipv4Address")
    }

    fun onStart(view: View) {
        Thread(SocketService.net).start()
    }

    fun onSend(view: View) {
        SocketService.sendDataToClient("Hello，我来自服务端")
    }
}