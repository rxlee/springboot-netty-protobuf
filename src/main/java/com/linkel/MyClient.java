package com.linkel;


import sun.awt.windows.ThemeReader;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MyClient {
    private Socket client;    //定义客户端套接字

    //建立客户端函数
    void getClient() {
        try {
            client = new Socket("192.168.1.147", 2404);    //建立客户端，使用的IP为127.0.0.1，端口和服务器一样为1100
            client.setKeepAlive(true);
            client.setTcpNoDelay(true);
            System.out.println("客户端建立成功！");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //定义客户端信息写入函数
    void sendClientMessage() {
        String msg = "##0144ST=31;CN=2021;PW=123456;MN=ZG130102201666;CP=&&DataTime=20180119131000;SB1-RS=0;SB2-RS=0;SB3-RS=0;SB4-RS=0;SB5-RS=0;SB6-RS=0;SB7-RS=0;SB8-RS=0&&CAC1\r\n";
        System.out.println("正在发送：" + msg);
        try {
            OutputStream pt = client.getOutputStream();        //建立客户端信息输出流
            pt.write(msg.getBytes());        //以二进制的形式将信息进行输出


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //生成客户端类对象
        MyClient myClient = new MyClient();
        myClient.getClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;    //建立客户端信息输入流
                try {
                    inputStream = myClient.client.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("进入接收循环");
                try {
                    byte[] bytes = new byte[1000];
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                        //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                        StringBuilder sb = new StringBuilder();
                        sb.append(new String(bytes, 0, len, "UTF-8"));
                        System.out.println("收到数据：" + new String(sb));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (true) {
            try {
                Thread.sleep(3000);
                myClient.sendClientMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}