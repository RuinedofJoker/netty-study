package org.joker.redis.protocol;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RESP {
    public static final String SIMPLE_STRINGS = "+";
    public static final String ERROR = "-";
    public static final String NUMBER = ":";
    public static final String BULK_STRINGS = "$";
    public static final String ARRAY = "*";

    public static final int COMMON_LENGTH = -2;
    public static final int UNINITIALIZED_LENGTH = -1;

    private String respType;
    private byte[] commonContent;
    private List<RESP> arrayContent;
    private int length;
    private int readLength;
    private List<Byte> unresolvedBuf;

    public RESP(String respType) {
        this.respType = respType;
        readLength = 0;
        if (SIMPLE_STRINGS.equals(respType) || ERROR.equals(respType) || NUMBER.equals(respType)) {
            length = COMMON_LENGTH;
            unresolvedBuf = new ArrayList();
        }else {
            length = UNINITIALIZED_LENGTH;
        }
        if (ARRAY.equals(respType)) {
            arrayContent = new ArrayList();
        }
    }

    public void setLength(int length) {
        this.length = length;
        if (BULK_STRINGS.equals(respType)) {
            commonContent = new byte[length];
        }
    }

    public int readableLength() {
        return length - readLength;
    }
}
