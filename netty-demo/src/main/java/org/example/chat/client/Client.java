package org.example.chat.client;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.example.chat.pojo.User;
import org.example.chat.protocol.Hddp;
import org.example.chat.protocol.HddpFrameDecoder;
import org.example.chat.protocol.HddpProtocol;
import org.example.chat.protocol.MessageCodec;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Channel channel = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new HddpFrameDecoder());
                            pipeline.addLast(new MessageCodec());
                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    Hddp hddp = (Hddp) msg;
                                    if ("string".equals(hddp.getSerializationType())) {
                                        System.out.println(hddp.getContent());
                                    }
                                    super.channelRead(ctx, msg);
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress("localhost", 8080))
                    .sync()
                    .channel();

            Scanner scanner = new Scanner(System.in);
            User user = new User();
            user.setUsername("zhangsan");
            user.setPassword("123");
            Hddp hddp = new Hddp();
            HddpProtocol msg = hddp.path("/login").content(new Gson().toJson(user)).serializationType("json").build();
            channel.writeAndFlush(msg);
            //while (true) {
            //    System.out.println("");
            //}
            scanner.nextLine();
            channel.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
