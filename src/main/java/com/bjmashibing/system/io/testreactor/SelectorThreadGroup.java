package com.bjmashibing.system.io.testreactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: 马士兵教育
 * @create: 2020-06-21 20:37
 */
public class SelectorThreadGroup {  //天生都是boss

    SelectorThread[] selectors;
    ServerSocketChannel server;
    AtomicInteger xid = new AtomicInteger(0);
    SelectorThreadGroup group = this;

    public void setWorker(SelectorThreadGroup workerGroup) {
        this.group = workerGroup;
    }

    SelectorThreadGroup(int num, String name) {
        //num  线程数
        selectors = new SelectorThread[num];
        for (int i = 0; i < num; i++) {
            selectors[i] = new SelectorThread(this);

            new Thread(selectors[i], name + " - " + (i + 1)).start();
        }

    }

    public void bind(int port) {

        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            //注册到那个selector上呢？
//            nextSelectorV2(server);
            nextSelector(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void nextSelector(Channel c) {

        try {
            if (c instanceof ServerSocketChannel) {
                SelectorThread st = nextBossSelector();  //listen 选择了 boss组中的一个线程后，要更新这个线程的work组
                st.queue.put(c);
                // st.setWorker(group);
                st.selector.wakeup();
            } else {
                SelectorThread st = nextWorkerSelector();  //在 main线程种，取到堆里的selectorThread对象
                //1,通过队列传递数据 消息
                st.queue.add(c);
                //2,通过打断阻塞，让对应的线程去自己在打断后完成注册selector
                st.selector.wakeup();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //无论 serversocket  socket  都复用这个方法
    private SelectorThread nextBossSelector() {
        int index = xid.incrementAndGet() % selectors.length;  //轮询就会很尴尬，倾斜
        return selectors[index];
    }

    private SelectorThread nextWorkerSelector() {
        int index = xid.incrementAndGet() % group.selectors.length;  //动用worker的线程分配
        return group.selectors[index];
    }
}
