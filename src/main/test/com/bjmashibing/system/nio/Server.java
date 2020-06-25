package com.bjmashibing.system.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author linze.jlz
 */
public class Server {

    private ServerSocketChannel server;
    private List<SocketChannel> clients = new LinkedList<>();
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    public  void open(int port) throws IOException {
        server = ServerSocketChannel.open();

        server.bind(new InetSocketAddress(port));

        server.configureBlocking(false);

        System.out.println("服务器已启动...");

        while (true) {
            SocketChannel accept = server.accept();
            if (accept != null) {
                System.out.println("接受到客户端连接：" + accept.getRemoteAddress());
                accept.configureBlocking(false);
                clients.add(accept);
            }

            Iterator<SocketChannel> iterator = clients.iterator();
            while (iterator.hasNext()){
                SocketChannel client = iterator.next();
                buffer.clear();
                int offset = client.read(buffer);
                if (offset == -1) {
                    System.out.println(client.getRemoteAddress() + "与客户端断开链接");
                    client.close();
                    iterator.remove();
                } else if (offset > 0) {
                    buffer.flip();
                    byte[] buf = new byte[buffer.limit()];
                    buffer.get(buf);
                    System.out.println(client.getRemoteAddress() + "接受到消息：" + new String(buf));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().open(9090);
    }
}
