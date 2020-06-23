package com.bjmashibing.system.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author linze.jlz
 */
public class NioClient {
    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("127.0.0.1",9090));

        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        ByteBuffer buf = ByteBuffer.allocate(1024);
        while (true) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                if ("q".equals(line)) {
                    reader.close();
                    in.close();
                    client.close();
                    break;
                }
                buf.put(line.getBytes());
                client.write(buf);
                buf.clear();
            }
        }
    }
}
