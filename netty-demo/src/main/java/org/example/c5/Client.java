package org.example.c5;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Channel socketChannel = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("接收到服务端消息:{}", msg);
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();

        Scanner scanner = new Scanner(System.in);
        String in;
        while (true) {
            System.out.println("请输入:");
            in = scanner.nextLine();

            if (in.equals("exit")) {
                scanner.close();
                ChannelFuture closeFuture = socketChannel.close();
                eventLoopGroup.shutdownGracefully().sync();
                closeFuture.sync();
                log.info("客户端已关闭...");
                break;
            }

            socketChannel.writeAndFlush(in).sync();
        }
    }
}
