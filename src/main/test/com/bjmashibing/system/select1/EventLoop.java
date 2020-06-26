package com.bjmashibing.system.select1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author linze.jlz
 */
public class EventLoop extends Thread {

    Selector selector;
    LinkedBlockingQueue<Channel> queue;
    EventLoopGroup group;

    EventLoop(String name, EventLoopGroup group) throws IOException {
        this.selector = Selector.open();
        this.queue = new LinkedBlockingQueue<>();
        this.setName(name);
        this.group = group;
    }

    @Override
    public void run() {

        try {
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        EventLoop eventLoop = group.nextWorker();
                        eventLoop.queue.put(client);
                        eventLoop.selector.wakeup();
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int offset = client.read(buffer);
                        if (offset == -1) {
                            System.out.println("客户端退出：" + client.getRemoteAddress());
                            client.close();
                        } else if (offset > 0) {
                            buffer.flip();
                            byte[] buf = new byte[buffer.limit()];
                            buffer.get(buf);
                            String text = new String(buf).trim();
                            System.out.println("【" + Thread.currentThread().getName() + "】接收到数据：" + text);
                        }
                    }
                }
                if (!queue.isEmpty()) {
                    Channel channel = queue.take();
                    if (channel instanceof ServerSocketChannel) {
                        ServerSocketChannel server = (ServerSocketChannel) channel;
                        server.register(selector, SelectionKey.OP_ACCEPT);
                    } else if (channel instanceof SocketChannel) {
                        SocketChannel client = (SocketChannel) channel;
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("【" + Thread.currentThread().getName() + "】接收到新连接：" + client.getRemoteAddress());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
