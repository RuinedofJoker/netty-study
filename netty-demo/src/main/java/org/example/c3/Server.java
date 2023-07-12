package org.example.c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * io多路复用服务端
 * 存在粘包半包问题
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);

        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        log.info("服务启动成功...");
        while (true) {
            selector.select();
            log.info("接收到事件...");

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //key.cancel(); 取消事件
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    //System.out.println(channel == ssc); true
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, 0, null).interestOps(SelectionKey.OP_READ);
                    log.info("接收连接...");
                }else if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    sc.read(buffer);

                    buffer.flip();
                    byte[] bytes = new byte[buffer.limit() - buffer.position()];
                    buffer.get(bytes);
                    buffer.compact();

                    log.info("接收到消息:{}", new String(bytes));
                }
                iterator.remove();
            }
        }
    }
}
