package org.example.c3;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
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

        ssc.register(selector, SelectionKey.OP_ACCEPT, null);

        log.info("服务启动成功...");
        while (true) {
            selector.select();
            log.info("接收到事件...");

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    //System.out.println(channel == ssc); true
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ, null);
                    log.info("接收连接{}...", key.channel());
                }else if (key.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);

                        int isValid = sc.read(buffer);
                        if (isValid == -1) {
                            key.cancel();
                            continue;
                        }

                        buffer.flip();

                        //log.info("接收到消息:{}", StandardCharsets.UTF_8.decode(buffer).toString());
                        byte[] bytes = new byte[buffer.limit() - buffer.position()];
                        buffer.get(bytes);
                        buffer.compact();

                        log.info("接收到消息:{}", new String(bytes));
                    }catch (IOException e) {
                        //取消事件
                        key.cancel();
                        log.info("{}客户端断开连接...", key.channel());
                    }
                }
            }
        }
    }
}
