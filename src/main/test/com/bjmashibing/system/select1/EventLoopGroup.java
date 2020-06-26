package com.bjmashibing.system.select1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linze.jlz
 */
public class EventLoopGroup {

    EventLoop[] eventLoops;
    EventLoopGroup workerGroup;
    AtomicInteger bossOffSet = new AtomicInteger();
    AtomicInteger workerOffSet = new AtomicInteger();

    EventLoopGroup(String name, int size) throws IOException {
        this.eventLoops = new EventLoop[size];
        for (int i = 0; i < eventLoops.length; i++) {
            EventLoop eventLoop = new EventLoop(name + " - " + i, this);
            eventLoops[i] = eventLoop;
            eventLoop.start();
        }
    }

    public void start(int port) throws IOException, InterruptedException {

        ServerSocketChannel server = ServerSocketChannel.open();

        server.configureBlocking(false);

        server.bind(new InetSocketAddress(port));

        EventLoop boss = nextBoss();
        boss.queue.put(server);
        boss.selector.wakeup();
    }

    public EventLoop nextBoss() {
        int length = eventLoops.length;
        int i = bossOffSet.getAndIncrement() % length;
        return eventLoops[i];
    }

    public EventLoop nextWorker() {
        int length = workerGroup.eventLoops.length;
        int i = workerOffSet.getAndIncrement() % length;
        return workerGroup.eventLoops[i];
    }

    public void setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }
}
