package com.trans.tcpservice

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.trans.udpservice.DatagramSocketService

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ipv4Address = SocketService.getIPAddress(this)
        Log.e(TAG, "IP地址:$ipv4Address")
        findViewById<TextView>(R.id.tv_ip).text = "IP: $ipv4Address"
    }

    fun onStartTCPService(view: View) {
        Thread(SocketService.net).start()
    }

    fun onSendTCPDataToClient(view: View) {
        SocketService.sendDataToClient("Hello,我是TCP数据,我来自服务端")
    }

    fun onReceiveUDPDataFromClient(view: View) {
        Thread(DatagramSocketService.net).start()
    }
}