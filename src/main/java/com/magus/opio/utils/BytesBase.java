package com.magus.opio.utils;

import java.util.Arrays;

import static com.magus.opio.io.OPIOBuffer.*;

public class BytesBase {
    private int headLen;
    private int bodyLen;
    private byte[] data;

    public void setData(byte[] data) {
        if (data.length == 0)
            return;
        int headLen = 0, bodyLen = 0;
        headLen++;
        switch (data[0]) {
            case mpBin8:
                bodyLen = data[1];
                headLen++;
                break;
            case mpBin16:
                bodyLen = Bytes.GetInt16(Arrays.copyOfRange(data, 1, 3));
                headLen += 2;
                break;
            case mpBin32:
                bodyLen = Bytes.GetInt16(Arrays.copyOfRange(data, 1, 5));
                headLen += 4;
                break;
            default:
        }
        this.headLen = headLen;
        this.bodyLen = bodyLen;
        this.data = data;
    }

    public byte[] getBody() {
        if (this.data == null)
            return null;
        return Arrays.copyOfRange(this.data, this.headLen, this.data.length);
    }

    public int getBodyLen() {
        return bodyLen;
    }

    public byte[] getData() {
        return data;
    }

    public int getDataLen() {
        return data.length;
    }

    public byte[] getHead() {
        if (this.data == null)
            return null;
        return Arrays.copyOfRange(this.data, 0, this.headLen);
    }

    public int getHeadLen() {
        return headLen;
    }
}
