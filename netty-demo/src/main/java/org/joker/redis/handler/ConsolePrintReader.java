package org.joker.redis.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.joker.redis.protocol.RESP;

public class ConsolePrintReader extends SimpleChannelInboundHandler<RESP> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RESP msg) throws Exception {
        if (RESP.ARRAY.equals(msg.getRespType())) {
            for (RESP item : msg.getArrayContent()) {
                System.out.printf(new String(item.getCommonContent()));
            }
        }else {
            System.out.printf(new String(msg.getCommonContent()));
        }
    }
}
