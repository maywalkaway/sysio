package com.bjmashibing.system.io;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author linze.jlz
 */
public class IOTests {

    @Test
    public void bytebuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(5);

        buffer.put((byte) 55);
        System.out.println(buffer + " hasRemaining：" + buffer.hasRemaining());
        buffer.put((byte) 55);
        buffer.put((byte) 55);
        buffer.put((byte) 55);
        buffer.put((byte) 55);
        System.out.println(buffer + " hasRemaining：" + buffer.hasRemaining());
        buffer.put((byte) 55);
        System.out.println(buffer + " hasRemaining：" + buffer.hasRemaining());
    }
}
