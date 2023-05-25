package com.trans.udpservice;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @author Tom灿
 * @description: UDP通信服务端 要求在同一局域网下(同一网段)
 * @date :2023/5/25 14:09
 */
public class DatagramSocketService {
    private static final String TAG = "DatagramSocketService";
    private static boolean isReceiveRunning = false;
    private static int port = 56789; // 端口
    public static Runnable net = new Runnable() {
        @Override
        public void run() {
            receiveMessage(true, "我是服务端，我已收到data");
        }
    };


    /**
     * 接收udp消息
     *
     * @param repeat   重复接收
     * @param response 响应数据
     */
    public static void receiveMessage(boolean repeat, String response) {
        isReceiveRunning = true;
        try {
            Log.e(TAG, "start");
            // 接收数据
            byte[] dataBytes = new byte[1024 * 1024];
            final DatagramPacket clientPacket =
                    new DatagramPacket(dataBytes, dataBytes.length);  // 数据包，用于存储数据
            Log.e(TAG, "创建数据包");
            DatagramSocket service = new DatagramSocket(null); // socket对象
            Log.e(TAG, "创建socket对象");
            service.setReuseAddress(true);
            service.bind(new InetSocketAddress(port));
            do {
                Log.e(TAG, "等待接收客户端数据......");
                service.receive(clientPacket);  // 接收数据, 阻塞线程操作
                String clientIp = clientPacket.getAddress().getHostAddress();
                String data = new String(dataBytes, 0, clientPacket.getLength());
                Log.e(TAG, "数据接收成功:" + data);
                // 响应数据
                byte[] responseBytes = (response == null ? "default response" : response).getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes,
                        responseBytes.length, clientPacket.getAddress(), clientPacket.getPort());
                service.send(responsePacket); // 发送数据
                Log.e(TAG, "发送响应");
            } while (repeat && !isReceiveRunning);
            service.disconnect();
            Log.e(TAG, "断开连接");
            service.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "接收、响应错误: " + e);
        }
    }

}
