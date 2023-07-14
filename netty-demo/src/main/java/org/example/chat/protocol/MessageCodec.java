package org.example.chat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
    自定义协议编解码器
*/
public class MessageCodec extends ByteToMessageCodec<HddpProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, HddpProtocol msg, ByteBuf out) throws Exception {
        out.writeInt(4396);
        out.writeByte(msg.getRequestType());
        out.writeByte(msg.getSerializationType());
        out.writeBytes(msg.getPath().getBytes());
        out.writeInt(msg.getContent().length);
        out.writeBytes(msg.getContent());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        HddpProtocol msg = new HddpProtocol();
        if (in.readInt() != 4396) {
            throw new RuntimeException("协议不匹配");
        }
        msg.setRequestType(in.readByte());
        msg.setSerializationType(in.readByte());
        byte[] path = new byte[22];
        in.readBytes(path, 0, 22);
        msg.setPath(new String(path));
        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content, 0, length);
        msg.setContent(content);
        Hddp hddp = new Hddp(msg);
        out.add(hddp);
    }
}
