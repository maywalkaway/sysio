package com.bjmashibing.system.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author linze.jlz
 */
public class Server {

    private boolean isStop;
    private ServerSocket server;
    private ExecutorService pool;

    public Server(int port) throws IOException {
        server = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(5);
    }

    public void start() throws IOException {
        System.out.println("服务器已启动...");
        while (!isStop) {
            Socket client = server.accept();
            System.out.println("接受到客户端连接：" + client.getInetAddress().getHostAddress() + ":" + client.getPort());

            pool.execute(new ClientAccepter(client));
        }
    }

    private class ClientAccepter implements Runnable {

        private Socket client;

        private ClientAccepter(Socket client) {
            this.client = client;
        }

        private String name() {
            return "[服务端-" + Thread.currentThread().getName() + "] - ";
        }

        @Override
        public void run() {
            try {
                InputStream is = client.getInputStream();

                byte[] buf = new byte[1024];

                while (true) {
                    int offset = is.read(buf);
                    if (offset == -1) {
                        is.close();
                        client.close();
                        System.out.println(name() + "与客户端断开链接");
                        break;
                    } else if (offset > 0) {
                        String text = new String(buf, 0, offset);
                        System.out.println(name() + "接受到消息：" + text);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(9090);
        server.start();
    }
}
