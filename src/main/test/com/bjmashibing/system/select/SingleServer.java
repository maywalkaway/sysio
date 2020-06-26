package com.bjmashibing.system.select;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author linze.jlz
 */
public class SingleServer {

    private List<Thread> readThreads = new ArrayList<>();
    private Selector selector;

    public void open(int port) throws IOException {

        ServerSocketChannel server = ServerSocketChannel.open();

        server.bind(new InetSocketAddress(port));

        server.configureBlocking(false);

        selector = Selector.open();

        server.register(selector, SelectionKey.OP_ACCEPT);

        doHandler();
    }

    private void doHandler() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println("select 被叫停..");
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    if(client != null){
                        System.out.println("【服务器】接收到新连接：" + client.getRemoteAddress());
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }
                } else if (key.isReadable()) {
                    Thread thread = new Thread(() -> {
                        SocketChannel client = (SocketChannel) key.channel();
                        try {
                            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                            int read = client.read(buffer);
                            if (read == -1) {
                                client.close();
                                System.out.println("客户端主动退出");
                            } else if (read > 0) {
                                buffer.flip();
                                byte[] buf = new byte[buffer.limit()];
                                buffer.get(buf);
                                String text = new String(buf).trim();
                                System.out.println(text);
                                // client.register(selector, SelectionKey.OP_WRITE);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } /*catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    });
                    readThreads.add(thread);
                    //thread.start();
                    thread.run();
                } else if (key.isWritable()) {

                    SocketChannel client = (SocketChannel) key.channel();

                    client.write(ByteBuffer.wrap(("服务器回复：" + (2 * 2)).getBytes()));
                }
            }
        }
    }

    public class ReadHandler implements Runnable {

        @Override
        public void run() {

        }
    }

    public static void main(String[] args) throws IOException {
        SingleServer server = new SingleServer();
        new Thread(()->{
            try {
                //while (true){
                    Thread.sleep(1000);
                    server.selector.wakeup();
                //}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        server.open(9090);
    }
}
