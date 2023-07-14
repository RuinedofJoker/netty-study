package org.example.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.example.chat.protocol.HddpFrameDecoder;
import org.example.chat.protocol.MessageCodec;
import org.example.chat.servlet.DispatcherServlet;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        NioEventLoopGroup parentELG = new NioEventLoopGroup();
        NioEventLoopGroup childELG = new NioEventLoopGroup();
        DispatcherServlet dispatcherServlet = new DispatcherServlet();

        try {
            new ServerBootstrap()
                    .group(parentELG, childELG)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new LoggingHandler());
                            pipeline.addLast(new HddpFrameDecoder());
                            pipeline.addLast(new MessageCodec());
                            pipeline.addLast(dispatcherServlet);
                        }
                    })
                    .bind(8080);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String directives = scanner.nextLine();
                if (directives.equals("exit")) {
                    break;
                }
            }
        } finally {
            parentELG.shutdownGracefully();
            childELG.shutdownGracefully();
        }
    }
}
