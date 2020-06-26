package com.bjmashibing.system.select;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author linze.jlz
 */
public class Client {

    public void open(int port) throws IOException {

        SocketChannel client = SocketChannel.open();

        //client.configureBlocking();

        client.connect(new InetSocketAddress(port));


        BufferedReader is = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String text = is.readLine();
            if ("quit".equals(text)) {
                System.out.println("客户端 - quit");
                is.close();
                client.close();
                break;
            }
            ByteBuffer buffer = ByteBuffer.wrap(text.getBytes());
            client.write(buffer);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.open(9090);
    }
}
