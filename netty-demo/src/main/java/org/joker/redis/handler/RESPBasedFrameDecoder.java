package org.joker.redis.handler;

import com.google.common.primitives.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.joker.redis.protocol.RESP;

import java.util.List;

/**
 * RESP协议解码器类(解决粘包半包问题)
 *
 * 如果lastContent不为null,发生粘包半包问题
 *
 * 将最后不完整的内容放入lastContent的unresolvedBuf交给下一次的decoder解析
 * 每次读取当前ByteBuf in前先判断lastContent是否为null,为null表明有上次未解析完的ByteBuf,先解析上次的
 */
@Slf4j
public class RESPBasedFrameDecoder extends ByteToMessageDecoder {

    private RESP lastContent;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.isReadable()) {
            RESP resp;
            if (lastContent == null) {
                resp = new RESP((char) in.readByte() + "");
            }else {
                resp = lastContent;
                lastContent = null;
            }
            //读取resp报文长度
            int lengthCode = resolveLength(resp, in);
            if (lengthCode == -1) {
                lastContent = resp;
                break;
            }
            if (lengthCode == -2) {
                throw new RuntimeException("error");
            }
            //读取resp报文内容
            int contentCode = resolveContent(resp, in);
            if (contentCode == -1) {
                lastContent = resp;
                break;
            }
            if (contentCode == -2) {
                throw new RuntimeException("error");
            }
            out.add(resp);
            overLine(in);
        }
    }

    /**
     * 读取当前resp报文长度
     * @param resp
     * @param in
     * @return  0正常 -1未读完 -2错误
     */
    private int resolveLength(RESP resp, ByteBuf in) {
//        if (true) {//
//            resp.setUnresolvedBuf(resp.getUnresolvedBuf() == null ? new ArrayList() : resp.getUnresolvedBuf());//
//            return 0;//
//        }//
        if (resp.getLength() != RESP.UNINITIALIZED_LENGTH) {
            //已经读取过长度
            return 0;
        }
        if (!in.isReadable()) {
            //当前报文发送内容不足(半包),未发送长度
            return -1;
        }
        resp.setLength(Integer.parseInt((char) in.readByte() + ""));
        overLine(in);
        return 0;
    }

    /**
     * 读取封装当前resp报文内容
     * @param resp
     * @param in
     * @return 0正常 -1未读完 -2错误
     */
    private int resolveContent(RESP resp, ByteBuf in) {
        if (RESP.SIMPLE_STRINGS.equals(resp.getRespType())///---
                || RESP.ERROR.equals(resp.getRespType())///---
                || RESP.NUMBER.equals(resp.getRespType())) {//---
        //if (true) {//
            //普通类型,以\r\n结尾
            List<Byte> content = resp.getUnresolvedBuf();
            while (in.isReadable()) {
                content.add(in.readByte());
                if (content.size() >= 2
                        && (content.get(content.size() - 2) == '\r' && content.get(content.size() - 1) == '\n')) {
                    resp.setCommonContent(Bytes.toArray(resp.getUnresolvedBuf()));
                    return 0;
                }
            }

            //没有读到\r\n(半包)
            return -1;
        }else if (RESP.BULK_STRINGS.equals(resp.getRespType())) {//---
        //}else if (false) {//
            //带长度的字符串
            byte[] content = resp.getCommonContent();
            int readableBytes = in.readableBytes();
            if (readableBytes >= resp.readableLength()) {
                //当前resp可读满
                in.readBytes(content, resp.getReadLength(), resp.readableLength());
                overLine(in);
                return 0;
            }else {
                //当前resp读不满
                in.readBytes(content, resp.getReadLength(), readableBytes);
                resp.setReadLength(resp.getReadLength() + readableBytes);
                return -1;
            }
        }else if (RESP.ARRAY.equals(resp.getRespType())) {//---
        //}else if (false) {//
            //数组(多个resp报文)
            List<RESP> arrayContent = resp.getArrayContent();
            resolveLength(resp, in);
            for (int i = resp.getReadLength(); i < resp.getLength(); i++) {
                if (!in.isReadable()) {
                    return -1;
                }
                RESP itemResp = arrayContent.get(i);
                if (itemResp == null) {
                    itemResp = new RESP((char) in.readByte() + "");
                    arrayContent.add(i, itemResp);
                }
                int itemLengthCode = resolveLength(itemResp, in);
                if (itemLengthCode != 0) {
                    return itemLengthCode;
                }
                int itemContentCode = resolveContent(itemResp, in);
                if (itemContentCode != 0) {
                    return itemContentCode;
                }
            }
        }
        return -2;
    }

    private void overLine(ByteBuf in) {
        in.readByte();
        in.readByte();
    }
}
