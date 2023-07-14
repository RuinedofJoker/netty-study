package org.example.chat.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class HddpFrameDecoder extends LengthFieldBasedFrameDecoder {
    public HddpFrameDecoder() {
        this(1024, 28, 4, 0, 0);
    }

    public HddpFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
