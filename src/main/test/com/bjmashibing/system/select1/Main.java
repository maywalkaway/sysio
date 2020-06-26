package com.bjmashibing.system.select1;

import java.io.IOException;

/**
 * @author linze.jlz
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        EventLoopGroup bossGroup = new EventLoopGroup("boss", 1);

        EventLoopGroup workerGroup = new EventLoopGroup("worker", 3);

        bossGroup.setWorkerGroup(workerGroup);

        bossGroup.start(9090);
    }
}
