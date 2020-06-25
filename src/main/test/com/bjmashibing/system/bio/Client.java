package com.bjmashibing.system.bio;

import java.io.*;
import java.net.Socket;

/**
 * @author linze.jlz
 */
public class Client {

    private int port;

    public Client(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        Socket socket = new Socket("localhost", port);

        BufferedReader is = new BufferedReader(new InputStreamReader(System.in));
        OutputStream os = socket.getOutputStream();

        while (true) {

            String text = is.readLine();
            if ("quit".equals(text)) {
                System.out.println("客户端 - quit");
                is.close();
                os.close();
                socket.close();
                break;
            }
            os.write(text.getBytes());
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(9090);
        client.start();
    }
}
