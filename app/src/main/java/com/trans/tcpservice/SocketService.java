package com.trans.tcpservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Tom灿
 * @description: TCP通信服务端 要求在同一局域网下(同一网段)
 * @date :2023/5/25 9:14
 */
public class SocketService {
    private static final String TAG = "SocketService";
    //监听端口12345
    private static final int PORT = 12345;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    public static Runnable net = new Runnable() {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT); // 监听12345端口 阻塞状态
                Log.e(TAG, "服务器连接中.....等待客户端(线程为阻塞状态)");
                clientSocket = serverSocket.accept();
                Log.e(TAG, "客户端上线......已连接到客户端");
                // 监听客户端
                getClientData2();
            } catch (Exception e) {
                Log.e(TAG, "端口被占用:" + e);
                e.printStackTrace();
            }
        }
    };

    /**
     * 监听客户端请求
     * TODO 无法实时接收客户端发来的数据，当客户端与服务端断开连接之前，会一次性接收所有的客户端发送的数据，之后客户端与服务端断开连接
     */
    private static void getClientData() {
//        while (true) {
        //循环监听客户端请求
        Log.i(TAG, "监听客户端请求");
        try {
            //获取输入流
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())); // 读取客户端数据
            //获取从客户端发来的信息
            String msg = in.readLine();
            Log.e(TAG, "收到客户端数据: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "读写数据错误: " + e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "关闭服务端错误:" + e);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "关闭客户端错误:" + e);
            }
            Log.e(TAG, "服务器关闭");
        }
//        }
    }

    /**
     * 监听客户端请求
     */
    public static void getClientData2() {
        InputStream inputStream = null;
        try {
            inputStream = clientSocket.getInputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                String data = new String(buffer, 0, len);
                Log.e(TAG, "收到客户端数据:" + data);
            }
        } catch (IOException e) {
            Log.e(TAG, "接收客户端数据错误:" + e);
            e.printStackTrace();
        }
    }


    /**
     * 发送数据到客户端
     *
     * @param msg
     */
    public static void sendDataToClient(String msg) {
        if (clientSocket != null && clientSocket.isConnected()) {
            new Thread(() -> {
                try {
                    clientSocket.getOutputStream().write(msg.getBytes()); // 写入数据
                    clientSocket.getOutputStream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "发送数据错误:" + e);
                }
            }).start();
        }
    }


    /**
     * 获取IP地址
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            Log.e(TAG, "无网络，请先连接网络");
        }
        return null;
    }


    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

}
