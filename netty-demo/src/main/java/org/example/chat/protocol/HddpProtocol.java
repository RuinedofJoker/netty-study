package org.example.chat.protocol;

import lombok.Data;

/**
 自定义协议:
    4字节魔数
    1字节消息类型
    1字节序列化类型
    22字节路径
    4字节长度
    x字节内容
 */
@Data
public class HddpProtocol {
    private byte requestType;
    private byte serializationType;
    private String path;

    private static final String BLANK_STRING = "                      ";
    private byte[] content;

    public static final byte GET = Byte.decode("00");
    public static final byte POST = Byte.decode("01");
    public static final byte PUT = Byte.decode("10");
    public static final byte DELETE = Byte.decode("11");

    public static final byte STRING = Byte.decode("0");
    public static final byte JSON = Byte.decode("1");

    public void setPath(String path) {
        if (path.length() > 22) {
            throw new RuntimeException("路径过长");
        }else if (path.length() < 22) {
            path = BLANK_STRING.substring(0, 22 - path.length()) + path;
        }
        this.path = path;
    }
}
