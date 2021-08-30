package com.magus.opio.utils;

import com.magus.opio.OPException;
import com.magus.opio.utils.base.ByteUtil;
import org.apache.commons.lang3.ArrayUtils;
import static com.magus.opio.OPType.*;
import static com.magus.opio.OPConstant.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;

/**
 * 字节数组转换工具类
 */
public class BytesUtils {
    public static void main(String[] args) throws Exception{
        System.out.println(Arrays.toString(getBytes("name")));
        System.out.println(getString(getBytes("name")));
    }

    public static final String GBK = "GBK";
    public static final String UTF8 = "utf-8";
    public static final char[] ascii = "0123456789ABCDEF".toCharArray();
    private static char[] HEX_VOCABLE = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    
    /**
     * @Author liuyi 
     * @Description //根据数据类型转字节数组
     * 定长，不需要指定数据长度。定长：data。变长：len+data
     * @Date 2021/8/25 16:00 
     * @Param [obj, opType] 
     * @return byte[]
     **/
    public static byte[] getBytes(Object obj,byte opType) throws OPException{
        switch (opType){
            case VtBool:
                return getBytes((boolean) obj);
            case VtInt8:
                return getBytes((byte) obj);
            case VtInt16:
                return getBytes((short) obj);
            case VtInt32:
                return getBytes((int) obj);
            case VtInt64:
                return getBytes((long) obj);
            case VtFloat:
                return getBytes((float)obj);
            case VtDouble:
                return getBytes((double) obj);
            case VtDateTime:
                Date date = (Date) obj;
                return getBytes(date.getTime());
            case VtString:
                byte[] bytes = getBytes(obj.toString(),UTF8);
                ByteUtil byteUtil = new ByteUtil();
                byteUtil.appendAll(getVlueLenBytes(bytes.length));
                byteUtil.appendAll(bytes);
            default:
                throw new OPException("该数据类型不存在，type==="+opType);
        }
    }

    /**
     * @Author liuyi
     * @Description //根据value组装字节数组
     * type+len（type+data）+value
     * len 可能为value 字节长度，也可能为属性个数
     * @Date 2021/8/26 17:10
     * @Param [opType, value]
     * @return byte[]
     **/
    public static byte[] getAllBytes(byte opType,long len,byte[] value) throws OPException {
        ByteUtil byteUtil = new ByteUtil();
        byteUtil.append(opType);
        byteUtil.appendAll(getVlueLenBytes(len));
        byteUtil.appendAll(value);
        return byteUtil.data;
    }

    /**
     * @Author liuyi
     * @Description //获取valueLen的类型和值的字节数组
     * valueLen可能为字节长度，也可能为元素个数
     * @Date 2021/8/25 17:33
     * @Param [valueLen]
     * @return byte[]
     **/
    public static byte[] getVlueLenBytes(long valueLen) throws OPException{
        byte[] rawData;
        //设置body的类型和长度
        if (valueLen <= MAX_UINT8) {
            rawData = new byte[2];
            rawData[0] = VtInt8;//设置body长度类型
            rawData[1] = (byte) (valueLen & 0xFF);//设置body长度
        } else if (valueLen > MAX_UINT8 && valueLen <= MAX_UINT16) {
            rawData = new byte[3];
            rawData[0] = VtInt16;//设置body长度类型
            //设置body长度
            rawData[1] = (byte) (valueLen >> 8 & 0xff);
            rawData[2] = (byte) (valueLen & 0xff);
        } else if (valueLen <= MAX_UINT32) {
            rawData = new byte[5];
            rawData[0] = VtInt32;//设置body长度类型
            //设置body长度
            rawData[1] = (byte) (valueLen >> 24 & 0xff);
            rawData[2] = (byte) (valueLen >> 16 & 0xff);
            rawData[3] = (byte) (valueLen >> 8 & 0xff);
            rawData[4] = (byte) (valueLen & 0xff);
        } else {
            throw new OPException("value长度超长");
        }
        return rawData;
    }

    /**
     * 将byte整型数值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(byte data) {
        return new byte[]{data};
    }

    /**
     * 将short整型数值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((data & 0xff00) >> 8);
        bytes[1] = (byte) (data & 0xff);
        return bytes;
    }

    /**
     * 将字符转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data >> 8);
        bytes[1] = (byte) (data);
        return bytes;
    }

    /**
     * 将布尔值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(boolean data) {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (data ? 1 : 0);
        return bytes;
    }

    /**
     * 将整型数值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((data & 0xff000000) >> 24);
        bytes[1] = (byte) ((data & 0xff0000) >> 16);
        bytes[2] = (byte) ((data & 0xff00) >> 8);
        bytes[3] = (byte) (data & 0xff);
        return bytes;
    }

    /**
     * 将long整型数值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) ((data >> 56) & 0xff);
        bytes[1] = (byte) ((data >> 48) & 0xff);
        bytes[2] = (byte) ((data >> 40) & 0xff);
        bytes[3] = (byte) ((data >> 32) & 0xff);
        bytes[4] = (byte) ((data >> 24) & 0xff);
        bytes[5] = (byte) ((data >> 16) & 0xff);
        bytes[6] = (byte) ((data >> 8) & 0xff);
        bytes[7] = (byte) (data & 0xff);
        return bytes;
    }

    /**
     * 将float型数值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    /**
     * 将double型数值转换为字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    /**
     * 将字符串按照charsetName编码格式的字节数组
     *
     * @param data
     *            字符串
     * @param charsetName
     *            编码格式
     * @return
     */
    public static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    /**
     * 将字符串按照utf-8编码格式的字节数组
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(String data) {
        return getBytes(data, UTF8);
    }

    /**
     * 将字节数组第0字节转换为布尔值
     *
     * @param bytes
     * @return
     */
    public static boolean getBoolean(byte[] bytes) {
        return bytes[0] == 1;
    }

    /**
     * 将字节数组的第index字节转换为布尔值
     *
     * @param bytes
     * @param index
     * @return
     */
    public static boolean getBoolean(byte[] bytes, int index) {
        return bytes[index] == 1;
    }

    /**
     * 将字节数组前2字节转换为short整型数值
     *
     * @param bytes
     * @return
     */
    public static short getShort(byte[] bytes) {
        return (short) ((0xff00 & (bytes[0] << 8)) | (0xff & bytes[1]));
    }

    /**
     * 将字节数组从startIndex开始的2个字节转换为short整型数值
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static short getShort(byte[] bytes, int startIndex) {
        return (short) ((0xff00 & (bytes[startIndex] << 8)) | (0xff & bytes[startIndex + 1]));
    }

    /**
     * @Author liuyi
     * @Description //获取无符号的byte
     * @Date 2021/7/25 22:41
     * @Param [bytes, startIndex]
     * @return short
     **/
    public static short getUnsignedByte(byte[] bytes, int startIndex) {
        return (short) (bytesToInt(bytes,startIndex,1));
    }

    /**
     * 将字节数组前2字节转换为字符
     *
     * @param bytes
     * @return
     */
    public static char getChar(byte[] bytes) {
        return (char) ((0xff00 & (bytes[0] << 8)) | (0xff & bytes[1]));
    }

    /**
     * 将字节数组从startIndex开始的2个字节转换为字符
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static char getChar(byte[] bytes, int startIndex) {
        return (char) ((0xff00 & (bytes[startIndex] << 8)) | (0xff & bytes[startIndex + 1]));
    }

    /**
     * 将字节数组前4字节转换为整型数值
     *
     * @param bytes
     * @return
     */
    public static int getInt(byte[] bytes) {
        return (0xff000000 & (bytes[0] << 24) | (0xff0000 & (bytes[1] << 16))
                | (0xff00 & (bytes[2] << 8)) | (0xff & bytes[3]));
    }

    /**
     * 将字节数组从startIndex开始的4个字节转换为整型数值
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static int getInt(byte[] bytes, int startIndex) {
        return (0xff000000 & (bytes[startIndex] << 24)
                | (0xff0000 & (bytes[startIndex + 1] << 16))
                | (0xff00 & (bytes[startIndex + 2] << 8)) | (0xff & bytes[startIndex + 3]));
    }

    /**
     * 将字节数组前8字节转换为long整型数值
     *
     * @param bytes
     * @return
     */
    public static long getLong(byte[] bytes) {
        return (0xff00000000000000L & ((long) bytes[0] << 56)
                | (0xff000000000000L & ((long) bytes[1] << 48))
                | (0xff0000000000L & ((long) bytes[2] << 40))
                | (0xff00000000L & ((long) bytes[3] << 32))
                | (0xff000000L & ((long) bytes[4] << 24))
                | (0xff0000L & ((long) bytes[5] << 16))
                | (0xff00L & ((long) bytes[6] << 8)) | (0xffL & (long) bytes[7]));
    }

    /**
     * 将字节数组从startIndex开始的8个字节转换为long整型数值
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static long getLong(byte[] bytes, int startIndex) {
        return (0xff00000000000000L & ((long) bytes[startIndex] << 56)
                | (0xff000000000000L & ((long) bytes[startIndex + 1] << 48))
                | (0xff0000000000L & ((long) bytes[startIndex + 2] << 40))
                | (0xff00000000L & ((long) bytes[startIndex + 3] << 32))
                | (0xff000000L & ((long) bytes[startIndex + 4] << 24))
                | (0xff0000L & ((long) bytes[startIndex + 5] << 16))
                | (0xff00L & ((long) bytes[startIndex + 6] << 8)) | (0xffL & (long) bytes[startIndex + 7]));
    }

    /**
     * 将字节数组前4字节转换为float型数值
     *
     * @param bytes
     * @return
     */
    public static float getFloat(byte[] bytes) {
        return Float.intBitsToFloat(getInt(bytes));
    }

    /**
     * 将字节数组从startIndex开始的4个字节转换为float型数值
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static float getFloat(byte[] bytes, int startIndex) {
        byte[] result = new byte[4];
        System.arraycopy(bytes, startIndex, result, 0, 4);
        return Float.intBitsToFloat(getInt(result));
    }
    /**
     * 将字节数组从startIndex开始的N(n<=4)个字节转换为float型数值
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static float getFloat(byte[] bytes, int startIndex,int len) {
        byte[] result = new byte[4];
        System.arraycopy(bytes, startIndex, result, 0, len);
        return Float.intBitsToFloat(getInt(result));
    }

    /**
     * 将字节数组前8字节转换为double型数值
     *
     * @param bytes
     * @return
     */
    public static double getDouble(byte[] bytes) {
        long l = getLong(bytes);
        return Double.longBitsToDouble(l);
    }

    /**
     * 将字节数组从startIndex开始的8个字节转换为double型数值
     *
     * @param bytes
     * @param startIndex
     * @return
     */
    public static double getDouble(byte[] bytes, int startIndex) {
        byte[] result = new byte[8];
        System.arraycopy(bytes, startIndex, result, 0, 8);
        long l = getLong(result);
        return Double.longBitsToDouble(l);
    }

    /**
     * 将charsetName编码格式的字节数组转换为字符串
     *
     * @param bytes
     * @param charsetName
     * @return
     */
    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }

    /**
     * 将utf-8编码格式的字节数组转换为字符串
     *
     * @param bytes
     * @return
     */
    public static String getString(byte[] bytes) {
        return getString(bytes, UTF8);
    }

    /**
     * 将16进制字符串转换为字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToBytes(String hex) {
        if (hex == null || "".equals(hex)) {
            return null;
        }
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] chArr = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chArr[pos]) << 4 | toByte(chArr[pos + 1]));
        }
        return result;
    }

    /**
     * 将16进制字符串转换为字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0)
            throw new IllegalArgumentException(
                    "input string should be any multiple of 2!");
        hex.toUpperCase();

        byte[] byteBuffer = new byte[hex.length() / 2];

        byte padding = 0x00;
        boolean paddingTurning = false;
        for (int i = 0; i < hex.length(); i++) {
            if (paddingTurning) {
                char c = hex.charAt(i);
                int index = indexOf(hex, c);
                padding = (byte) ((padding << 4) | index);
                byteBuffer[i / 2] = padding;
                padding = 0x00;
                paddingTurning = false;
            } else {
                char c = hex.charAt(i);
                int index = indexOf(hex, c);
                padding = (byte) (padding | index);
                paddingTurning = true;
            }

        }
        return byteBuffer;
    }

    private static int indexOf(String input, char c) {
        int index = ArrayUtils.indexOf(HEX_VOCABLE, c);

        if (index < 0) {
            throw new IllegalArgumentException("err input:" + input);
        }
        return index;

    }

    /**
     * 将BCD编码的字节数组转换为字符串
     *
     * @param bcds
     * @return
     */
    public static String bcdToString(byte[] bcds) {
        if (bcds == null || bcds.length == 0) {
            return null;
        }
        byte[] temp = new byte[2 * bcds.length];
        for (int i = 0; i < bcds.length; i++) {
            temp[i * 2] = (byte) ((bcds[i] >> 4) & 0x0f);
            temp[i * 2 + 1] = (byte) (bcds[i] & 0x0f);
        }
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < temp.length; i++) {
            res.append(ascii[temp[i]]);
        }
        return res.toString();
    }

    /**
     * 字节转整形
     * @param value
     * @return
     */
    public static int bcdToInt(byte value){
        return ((value>>4) * 10) + (value&0x0F);
    }

    /**
     * 字节数组转16进制字符串
     * @param bs
     * @return
     */
    public static String bytesToHex(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            int high = (b >> 4) & 0x0f;
            int low = b & 0x0f;
            sb.append(HEX_VOCABLE[high]);
            sb.append(HEX_VOCABLE[low]);
        }
        return sb.toString();
    }

    /**
     * 字节数组取前len个字节转16进制字符串
     * @param bs
     * @param len
     * @return
     */
    public static String bytesToHex(byte[] bs, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<len; i++ ) {
            byte b = bs[i];
            int high = (b >> 4) & 0x0f;
            int low = b & 0x0f;
            sb.append(HEX_VOCABLE[high]);
            sb.append(HEX_VOCABLE[low]);
        }
        return sb.toString();
    }
    /**
     * 字节数组偏移offset长度之后的取len个字节转16进制字符串
     * @param bs
     * @param offset
     * @param len
     * @return
     */
    public static String bytesToHex(byte[] bs, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<len; i++ ) {
            byte b = bs[offset + i];
            int high = (b >> 4) & 0x0f;
            int low = b & 0x0f;
            sb.append(HEX_VOCABLE[high]);
            sb.append(HEX_VOCABLE[low]);
        }
        return sb.toString();
    }
    /**
     * 字节数组转16进制字符串
     * @param
     * @return
     */
    public static String byteToHex(byte b) {
        StringBuilder sb = new StringBuilder();
            int high = (b >> 4) & 0x0f;
            int low = b & 0x0f;
            sb.append(HEX_VOCABLE[high]);
            sb.append(HEX_VOCABLE[low]);
        return sb.toString();
    }
    /**
     * 将字节数组取反
     *
     * @param src
     * @return
     */
    public static String negate(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        byte[] temp = new byte[2 * src.length];
        for (int i = 0; i < src.length; i++) {
            byte tmp = (byte) (0xFF ^ src[i]);
            temp[i * 2] = (byte) ((tmp >> 4) & 0x0f);
            temp[i * 2 + 1] = (byte) (tmp & 0x0f);
        }
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < temp.length; i++) {
            res.append(ascii[temp[i]]);
        }
        return res.toString();
    }

    /**
     * 比较字节数组是否相同
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean compareBytes(byte[] a, byte[] b) {
        if (a == null || a.length == 0 || b == null || b.length == 0
                || a.length != b.length) {
            return false;
        }
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
    /**
     * 只比对指定长度byte
     * @param a
     * @param b
     * @param len
     * @return
     */
    public static boolean compareBytes(byte[] a, byte[] b, int len) {
        if (a == null || a.length == 0 || b == null || b.length == 0
                || a.length < len || b.length < len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将字节数组转换为二进制字符串
     *
     * @param items
     * @return
     */
    public static String bytesToBinaryString(byte[] items) {
        if (items == null || items.length == 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        for (byte item : items) {
            buf.append(byteToBinaryString(item));
        }
        return buf.toString();
    }

    /**
     * 将字节转换为二进制字符串
     *
     * @param
     * @return
     */
    public static String byteToBinaryString(byte item) {
        byte a = item;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            buf.insert(0, a % 2);
            a = (byte) (a >> 1);
        }
        return buf.toString();
    }

    /**
     * 对数组a，b进行异或运算
     *
     * @param a
     * @param b
     * @return
     */
    public static byte[] xor(byte[] a, byte[] b) {
        if (a == null || a.length == 0 || b == null || b.length == 0
                || a.length != b.length) {
            return null;
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    /**
     * 对数组a，b进行异或运算 运算长度len
     * @param a
     * @param b
     * @param len
     * @return
     */
    public static byte[] xor(byte[] a, byte[] b, int len) {
        if (a == null || a.length == 0 || b == null || b.length == 0) {
            return null;
        }
        if (a.length < len || b.length < len){
            return null;
        }
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
    /**
     * 将short整型数值转换为字节数组
     *
     * @param num
     * @return
     */
    public static byte[] shortToBytes(short num) {
        byte[] temp = new byte[2];
        for (int i = 0; i < 2; i++) {
            temp[i] = (byte) ((num >>> (8 - i * 8)) & 0xFF);
        }
        return temp;
    }

    /**
     * 将字节数组转为整型
     *
     * @param
     * @param offset
     * @return
     */
    public static short bytesToShort(byte[] arr, int offset) {
        return (short) ((0xff00 & (arr[offset] << 8))|(0xff & arr[offset+1]));
    }

    /**
     * 将整型数值转换为指定长度的字节数组
     *
     * @param num
     * @return
     */
    public static byte[] intToBytes(int num) {
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = (byte) ((num >>> (24 - i * 8)) & 0xFF);
        }
        return temp;
    }

    /**
     * 将整型数值转换为指定长度的字节数组
     *
     * @param src
     * @param len
     * @return
     */
    public static byte[] intToBytes(int src, int len) {
        if (len < 1 || len > 4) {
            return null;
        }
        byte[] temp = new byte[len];
        for (int i = 0; i < len; i++) {
            temp[len - 1 - i] = (byte) ((src >>> (8 * i)) & 0xFF);
        }
        return temp;
    }

    /**
     * 将字节数组转换为整型数值
     *
     * @param arr
     * @return
     */
    public static int bytesToInt(byte[] arr) {
        int mask = 0xFF;
        int temp = 0;
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            temp = arr[i] & mask;
            result |= temp;
        }
        return result;
    }

    /**
     * 将long整型数值转换为字节数组
     *
     * @param num
     * @return
     */
    public static byte[] longToBytes(long num) {
        byte[] temp = new byte[8];
        for (int i = 0; i < 8; i++) {
            temp[i] = (byte) ((num >>> (56 - i * 8)) & 0xFF);
        }
        return temp;
    }

    /**
     * 将字节数组转换为long整型数值
     *
     * @param arr
     * @return
     */
    public static long bytesToLong(byte[] arr) {
        int mask = 0xFF;
        int temp = 0;
        long result = 0;
        int len = Math.min(8, arr.length);
        for (int i = 0; i < len; i++) {
            result <<= 8;
            temp = arr[i] & mask;
            result |= temp;
        }
        return result;
    }

    /**
     * 将字节数组转换为long整型数值
     *
     * @param arr
     * @return
     */
    public static long bytesToLong(byte[] arr,int offset, int len) {
        int mask = 0xFF;
        int temp = 0;
        long result = 0;
        len = Math.min(8,len);
        for (int i = 0; i < len; i++) {
            result <<= 8;
            temp = arr[offset+i] & mask;
            result |= temp;
        }
        return result;
    }

    /**
     * 将16进制字符转换为字节
     *
     * @param c
     * @return
     */
    public static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 功能描述：把两个字节的字节数组转化为整型数据，高位补零，例如：<br/>
     * 有字节数组byte[] data = new byte[]{1,2};转换后int数据的字节分布如下：<br/>
     * 00000000  00000000 00000001 00000010,函数返回258
     * @param lenData 需要进行转换的字节数组
     * @return  字节数组所表示整型值的大小
     */
    public static int bytesToIntWhereByteLengthEquals2(byte lenData[]) {
        if(lenData.length != 2){
            return -1;
        }
        byte fill[] = new byte[]{0,0};
        byte real[] = new byte[4];
        System.arraycopy(fill, 0, real, 0, 2);
        System.arraycopy(lenData, 0, real, 2, 2);
        int len = byteToInt(real);
        return len;

    }

    /**
     * 功能描述：将byte数组转化为int类型的数据
     * @param byteVal 需要转化的字节数组
     * @return 字节数组所表示的整型数据
     */
    public static int byteToInt(byte[] byteVal) {
        int result = 0;
        for(int i = 0;i < byteVal.length;i++) {
            int tmpVal = (byteVal[i]<<(8*(3-i)));
            switch(i) {
                case 0:
                    tmpVal = tmpVal & 0xFF000000;
                    break;
                case 1:
                    tmpVal = tmpVal & 0x00FF0000;
                    break;
                case 2:
                    tmpVal = tmpVal & 0x0000FF00;
                    break;
                case 3:
                    tmpVal = tmpVal & 0x000000FF;
                    break;
            }

            result = result | tmpVal;
        }
        return result;
    }
    public static byte CheckXORSum(byte[] bData){
        byte sum = 0x00;
        for (int i = 0; i < bData.length; i++) {
            sum ^= bData[i];
        }
        return sum;
    }
    /**
     * 从offset开始 将后续长度为len的byte字节转为int
     * @param data
     * @param offset
     * @param len
     * @return
     */
    public static int bytesToInt(byte[] data, int offset, int len){
        int mask = 0xFF;
        int temp = 0;
        int result = 0;
        len = Math.min(len, 4);
        for (int i = 0; i < len; i++) {
            result <<= 8;
            temp = data[offset + i] & mask;
            result |= temp;
        }
        return result;
    }

    /**
     * @Author liuyi
     * @Description //从offset开始,获取一个byte
     * @Date 2021/7/21 16:02
     * @Param [data, offset]
     * @return byte
     **/
    public static byte bytesToByte(byte[] data, int offset){
        return data[offset];
    }

    /**
     * byte字节数组中的字符串的长度
     * @param data
     * @return
     */
    public static int getBytesStringLen(byte[] data)
    {
        int count = 0;
        for (byte b : data) {
            if(b == 0x00)
                break;
            count++;
        }
        return count;
    }

    /**
     * @Author liuyi
     * @Description //无符号的字节转16进制字符
     * @Date 2021/7/27 17:27
     * @Param [s]
     * @return java.lang.String
     **/
    public static String unsignedByteToHex(short s){
        return bytesToHex(getBytes(s)).substring(2,4);
    }

    /**
     * @Author liuyi
     * @Description // 从offset开始,获取一个Unit8
     * @Date 2021/8/27 11:22
     * @Param [data, offset]
     * @return short
     **/
    public static short bytesToUint8(byte[] data, int offset) {
        byte b = data[offset];
        return (short) (b & 0xff);
    }

    /**
     * @Author liuyi
     * @Description //  从offset开始,获取一个Unit16
     * @Date 2021/8/30 11:32
     * @Param [data, offset]
     * @return short
     **/
    public static int bytesToUint16(byte[] data, int offset) {
        return bytesToInt(data,offset,2);
    }

    /**
     * @Author liuyi
     * @Description //  从offset开始,获取一个Unit32
     * @Date 2021/8/30 11:32
     * @Param [data, offset]
     * @return short
     **/
    public static long bytesToUint32(byte[] data, int offset) {
        return bytesToLong(data,offset,4);
    }

    /**
     * @Author liuyi
     * @Description //从offset开始,获取一个utf-8编码的字符串
     * @Date 2021/8/30 14:08
     * @Param [data, offset]
     * @return void
     **/
    public static String bytesToString(byte[] data, int offset,int len) {
        return getString(Arrays.copyOfRange(data, offset, offset+len));
    }
}