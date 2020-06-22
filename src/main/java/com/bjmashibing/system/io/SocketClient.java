package com.bjmashibing.system.io;

import java.io.*;
import java.net.Socket;

/**
 * @author: 马士兵教育
 * @create: 2020-05-17 16:18
 */
public class SocketClient {

    public static void main(String[] args) {

        try {
            Socket client = new Socket("192.168.2.103", 9090);

            //client.setSendBufferSize(20);
            //client.setTcpNoDelay(true);
            OutputStream out = client.getOutputStream();

            InputStream in = System.in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    if ("-1".contains(line)) {
                        in.close();
                        out.close();
                        client.close();
                        break;
                    }
                    byte[] bb = line.getBytes();
                    out.write(bb);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
