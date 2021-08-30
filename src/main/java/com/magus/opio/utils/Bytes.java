package com.magus.opio.utils;

import com.magus.opio.io.OPIOBuffer;

import java.util.Arrays;
import java.util.Date;

public class Bytes {

    public static void main(String[] args) {
        byte[] b = new byte[2];
        PutInt16(b,(short) 258);
        System.out.println(Integer.toBinaryString(-258));
        System.out.println(Arrays.toString(b));

    }
    public static void PutBool(byte[] b, boolean v) {
        b[0] = v ? (byte) (1) : (byte) (0);
    }

    public static void PutInt8(byte[] b, byte v) {
        b[0] = (byte) (v & 0xff);
    }

    public static void PutInt16(byte[] b, short v) {
        b[0] = (byte) (v >> 8 & 0xff);
        b[1] = (byte) (v & 0xff);
    }

    public static void PutInt32(byte[] b, int v) {
        b[0] = (byte) (v >> 24 & 0xff);
        b[1] = (byte) (v >> 16 & 0xff);
        b[2] = (byte) (v >> 8 & 0xff);
        b[3] = (byte) (v & 0xff);
    }

    public static void PutInt64(byte[] b, long v) {
        b[0] = (byte) (v >> 56 & 0xff);
        b[1] = (byte) (v >> 48 & 0xff);
        b[2] = (byte) (v >> 40 & 0xff);
        b[3] = (byte) (v >> 32 & 0xff);
        b[4] = (byte) (v >> 24 & 0xff);
        b[5] = (byte) (v >> 16 & 0xff);
        b[6] = (byte) (v >> 8 & 0xff);
        b[7] = (byte) (v & 0xff);
    }

    public static void PutFloat32(byte[] b, float v) {
        int v_ = Float.floatToRawIntBits((float) v);
        PutInt32(b, v_);
    }

    public static void PutFloat64(byte[] b, double v) {
        long v_ = Double.doubleToRawLongBits(v);
        PutInt64(b, v_);
    }

    // 注意第一个字节放的是head len，真实内容从第二个字节开始
    public static byte[] PutString(String v) {
        byte[] b = v.getBytes();
        return PutBinary(b);
    }

    public static byte[] putVarString(String v) {
        byte[] b = v.getBytes();
        return putVarBinary(b);
    }

    public static int GetStringHeadLen(byte[] v) {

        return 0;
    }

    // 注意第一个字节放的是head len，真实内容从第二个字节开始
    // 参考go opio，java仅有一个返回值，取巧用，pkg之前会处理掉
    public static byte[] PutBinary(byte[] v) {
        int headLen = 2;
        byte[] rawData;
        int valueLen = v.length;
        if (valueLen < 0x100) {
            rawData = new byte[headLen + valueLen + 1];
            rawData[1] = OPIOBuffer.mpBin8;
            rawData[2] = (byte) (valueLen & 0xff);
        } else if (valueLen < 0x10000) {
            headLen = 3;
            rawData = new byte[headLen + valueLen + 1];
            rawData[1] = OPIOBuffer.mpBin16;
            rawData[2] = (byte) (valueLen >> 8 & 0xff);
            rawData[3] = (byte) (valueLen & 0xff);
        } else {
            headLen = 5;
            rawData = new byte[headLen + valueLen + 1];
            rawData[1] = OPIOBuffer.mpBin32;
            rawData[2] = (byte) (valueLen >> 24 & 0xff);
            rawData[3] = (byte) (valueLen >> 16 & 0xff);
            rawData[4] = (byte) (valueLen >> 8 & 0xff);
            rawData[5] = (byte) (valueLen & 0xff);
        }
        rawData[0] = (byte) headLen; // 第一个字节标识head len
        System.arraycopy(v, 0, rawData, headLen + 1, valueLen);
        return rawData;
    }

    /**
     * @Author liuyi
     * @Description //组装变长字节数组
     * @Date 2021/5/18 14:06
     * @Param [v]
     * @return byte[]
     **/
    public static byte[] putVarBinary(byte[] v) {
        int headLen;
        byte[] rawData;
        int valueLen = v.length;
        if (valueLen < 0x100) {
            headLen = 2;
            rawData = new byte[headLen + valueLen];
            rawData[0] = OPIOBuffer.mpBin8;
            rawData[1] = (byte) (valueLen & 0xff);
        } else if (valueLen < 0x10000) {
            headLen = 3;
            rawData = new byte[headLen + valueLen];
            rawData[0] = OPIOBuffer.mpBin16;
            rawData[1] = (byte) (valueLen >> 8 & 0xff);
            rawData[2] = (byte) (valueLen & 0xff);
        } else {
            headLen = 5;
            rawData = new byte[headLen + valueLen];
            rawData[0] = OPIOBuffer.mpBin32;
            rawData[1] = (byte) (valueLen >> 24 & 0xff);
            rawData[2] = (byte) (valueLen >> 16 & 0xff);
            rawData[3] = (byte) (valueLen >> 8 & 0xff);
            rawData[4] = (byte) (valueLen & 0xff);
        }
        System.arraycopy(v, 0, rawData, headLen, valueLen);
        return rawData;
    }

    public static boolean GetBool(byte[] b) {
        return b[0] == 1;
    }

    public static short GetInt8(byte[] b) {
        return (short) (b[0] & 0xff);
    }

    public static int GetInt16(byte[] b) {
        return (int) ((b[0] & 0xff) << 8 | b[1] & 0xff);
    }

    public static int GetInt32(byte[] b) {
        return (int) ((b[0] & 0xff) << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | b[3] & 0xff);
    }

    public static long GetInt64(byte[] b) {
        return (((long) b[0] & 0xff) << 56 | ((long) b[1] & 0xff) << 48 | ((long) b[2] & 0xff) << 40
                | ((long) b[3] & 0xff) << 32 | ((long) b[4] & 0xff) << 24
                | ((long) b[5] & 0xff) << 16 | ((long) b[6] & 0xff) << 8 | ((long) b[7] & 0xff));
    }

    public static float GetFloat32(byte[] b) {
        return Float.intBitsToFloat(GetInt32(b));
    }

    public static double GetFloat64(byte[] b) {
        return Double.longBitsToDouble(GetInt64(b));
    }

    public static Date GetDatetime(byte[] b) {
        return new Date((long) GetFloat64(b) * 1000);
    }

    public static String GetString(byte[] b) {
        int offset = 0;
        byte typeFlag = b[offset];
        switch (typeFlag) {
            case OPIOBuffer.mpBin8:
                offset++;
                break;
            case OPIOBuffer.mpBin16:
                offset += 2;
                break;
            case OPIOBuffer.mpBin32:
                offset += 4;
                break;
            default:
                break;
        }

        return new String(Arrays.copyOfRange(b, offset, b.length));
    }

    public static String GetStringExt(byte[] b) {
        int offset = 0;
        byte typeFlag = b[offset];
        offset++;
        switch (typeFlag) {
            case OPIOBuffer.mpBin8:
                offset++;
                break;
            case OPIOBuffer.mpBin16:
                offset += 2;
                break;
            case OPIOBuffer.mpBin32:
                offset += 4;
                break;
            default:
                break;
        }
        return new String(Arrays.copyOfRange(b, offset, b.length));
    }

    public static byte[] MakeEmptyBinary() {
        return new byte[]{OPIOBuffer.mpBin8, 0};
    }

    public static boolean IsEmptyBinary(byte[] src) {
        if (src == null) {
            return true;
        }
        if (src.length < 2) {
            return true;
        }
        return OPIOBuffer.mpBin8 == src[0] && 0 == src[1];
    }
}
