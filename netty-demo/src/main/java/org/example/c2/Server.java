package org.example.c2;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 阻塞式io服务端
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws Exception {

        ExecutorService threadPool = Executors.newCachedThreadPool();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置为非阻塞模式后accept()方法不再阻塞，没有连接返回null
        //ssc.configureBlocking(false);

        ssc.bind(new InetSocketAddress(8080));
        log.info("服务器开启...");

        while (true) {
            try {
                SocketChannel sc = ssc.accept();
                //设置为非阻塞模式后read(buffer)方法不再阻塞
                //sc.configureBlocking(false);
                log.info("接收到连接...{}", sc);
                threadPool.submit(() -> {
                    List<Byte> content = new ArrayList<>();
                    try {
                        while (true) {
                            ByteBuffer buffer = ByteBuffer.allocate(8);
                            int len = 0;
                            //因此这个循环只有客户端断开连接才能执行
                            while (len != -1) {
                                //该方法会阻塞运行直到客户端断开连接抛出异常
                                len = sc.read(buffer);
                                buffer.flip();
                                byte[] bytes = new byte[buffer.limit() - buffer.position()];
                                buffer.get(bytes, buffer.position(), buffer.limit());
                                buffer.compact();

                                for (byte b : bytes) {
                                    content.add(b);
                                }
                            }

                        }
                    }catch (Exception e) {
                        byte[] bytes = new byte[content.size()];

                        for (int i = 0; i < content.size(); i++) {
                            bytes[i] = content.get(i);
                        }
                        log.info("接收到消息:\n{}", new String(bytes));
                        log.error(e.getMessage());
                    }
                });
            }catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}
