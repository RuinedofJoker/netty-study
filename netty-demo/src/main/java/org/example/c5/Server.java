package org.example.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server {
    public static void main(String[] args) {
        NioEventLoopGroup parentELG = new NioEventLoopGroup();
        NioEventLoopGroup childELG = new NioEventLoopGroup();

        new ServerBootstrap()
                .group(parentELG, childELG)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("收到客户端消息:{}\n写回消息...", msg);
                                channel.writeAndFlush(msg);
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast(new StringEncoder());
                    }
                })
                .bind(8080);
    }
}
