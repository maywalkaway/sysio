package com.bjmashibing.system.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketIO {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9090, 20);

        System.out.println("step1: new ServerSocket(9090) ");

        while (true) {
            Socket client = server.accept();  //阻塞1
            System.out.println("step2:client\t" + client.getPort());
            new Thread(() -> {
                InputStream is;
                try {
                    is = client.getInputStream();

                    byte[] buf = new byte[512];
                    while (true) {
                        int offset = is.read(buf);
                        if (offset == -1) {
                            is.close();
                            client.close();
                            break;
                        }
                        String data = new String(buf, 0, offset);
                        System.out.println(data);
                    }
                    System.out.println("客户端断开");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
