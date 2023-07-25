package org.joker.redis.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.joker.redis.client.task.ConsoleWriterListener;
import org.joker.redis.handler.ConsolePrintReader;
import org.joker.redis.handler.RESPBasedFrameDecoder;
import org.joker.redis.handler.RESPStringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入ip(输入d使用默认127.0.0.1):");
        String ip = scanner.nextLine();
        System.out.print("请输入端口号(输入d使用默认6379):");
        String port = scanner.nextLine();
        if ("d".equals(ip)) {
            ip = "127.0.0.1";
        }
        if ("d".equals(port)) {
            port = "6379";
        }

        InetSocketAddress redisServerAddress = new InetSocketAddress(ip, Integer.parseInt(port));

        NioEventLoopGroup nel = new NioEventLoopGroup();
        ExecutorService userWriterListener = Executors.newSingleThreadExecutor();
        Channel channel = null;

        try {
            channel = new Bootstrap()
                    .group(nel)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new LoggingHandler());
                            pipeline.addLast(new RESPBasedFrameDecoder());
                            pipeline.addLast(new ConsolePrintReader());
                            pipeline.addLast(new RESPStringEncoder());
                        }
                    })
                    .connect(redisServerAddress)
                    .sync()
                    .channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

            userWriterListener.execute(new ConsoleWriterListener(Thread.currentThread(), channel, redisServerAddress));
            LockSupport.park();

            if (channel != null) {
                channel.close();
            }
            nel.shutdownGracefully();
            userWriterListener.shutdown();
        }
    }
}
