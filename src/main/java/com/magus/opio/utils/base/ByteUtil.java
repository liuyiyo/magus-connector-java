package com.magus.opio.utils.base;

import com.magus.opio.OPException;
import com.magus.opio.utils.BytesUtils;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.magus.opio.OPType.*;

/**
 * @ClassName ByteUtil
 * @description：字节处理工具类
 * @author：liuyi
 * @Date：2021/8/25 13:56
 */
public class ByteUtil {
    public static void main(String[] args) {

    }

    public ByteUtil() {
    }

    public ByteUtil(byte[] data) {
        this.data = data;
    }

    public ByteUtil(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
    }

    public byte[] data;
    public int offset;

    /**
     * @Author liuyi
     * @Description //追加单个字节
     * @Date 2021/8/25 14:15
     * @Param [e]
     * @return void
     **/
    public void append(byte e){
        if(data == null){
            data = new byte[1];
            data[0] = e;
        }else {
            int length = data.length+1;
            byte[] newData = new byte[length];
            System.arraycopy(data,0,newData,0,data.length);
            newData[length-1] = e;
            data = newData;
        }
    }

    /**
     * @Author liuyi
     * @Description //前面添加单个字节
     * @Date 2021/8/26 13:12
     * @Param [e]
     * @return void
     **/
    public void front(byte e){
        if(data == null){
            data = new byte[1];
            data[0] = e;
        }else {
            int length = data.length+1;
            byte[] newData = new byte[length];
            newData[0] = e;
            System.arraycopy(data,0,newData,1,data.length);
            data = newData;
        }
    }

    /**
     * @Author liuyi
     * @Description //追加多个字节
     * @Date 2021/8/25 14:21
     * @Param [bytes]
     * @return void
     **/
    public void appendAll(byte[] bytes){
        if(data == null){
            data = bytes;
        }else {
            int length = data.length+bytes.length;
            byte[] newData = new byte[length];
            System.arraycopy(data,0,newData,0,data.length);
            System.arraycopy(bytes,0,newData,data.length,bytes.length);
            data = newData;
        }
    }

    /**
     * @Author liuyi
     * @Description //前面添加多个字节
     * @Date 2021/8/26 13:21
     * @Param [bytes]
     * @return void
     **/
    public void frontAll(byte[] bytes){
        if(data == null){
            data = bytes;
        }else {
            int length = data.length+bytes.length;
            byte[] newData = new byte[length];
            System.arraycopy(bytes,0,newData,0,bytes.length);
            System.arraycopy(data,0,newData,bytes.length,data.length);
            data = newData;
        }
    }


    /**
     * @Author liuyi
     * @Description //根据类型获取
     * 如果是定长类型，则直接读取，如果是变长类型，则需要根据长度进行读取
     * @Date 2021/8/30 14:24
     * @Param [valueType]
     * @return java.lang.Object
     **/
    public Object getValue(byte valueType) throws OPException{
        switch (valueType){
            //定长类型
            case VtBool:
                return getBoolean();
            case VtInt8:
                return getInt8();
            case VtInt16:
                return getInt16();
            case VtInt32:
                return getInt32();
            case VtInt64:
                return getInt64();
            case VtFloat:
                return getFloat();
            case VtDouble:
                return getDouble();
            case VtDateTime:
                return new Date(getInt64());
            //变长类型
            case VtBinary:
                return null;
            case VtStructure:
                return null;
            case VtMap:
                return new HashMap<>();
            case BOOL_ARRAY:
                return getBaseArray(valueType);
        }

        return null;
    }

    /**
     * @Author liuyi
     * @Description //获取value的长度
     * @Date 2021/8/30 11:18
     * @Param [sizeType]
     * @return long
     **/
    public long getValueLen(byte sizeType) {
        //注意长度都是无符号的
        switch (sizeType){
            case VtInt8:
                return getUint8();
            case VtInt16:
                return getUint16();
            case VtInt32:
                return getUint32();

        }
        return 0;
    }

    /**
     * @Author liuyi
     * @Description //顺序读取读取boolean
     * @Date 2021/8/27 10:57
     * @Param []
     * @return boolean
     **/
    public boolean getBoolean(){
        return BytesUtils.getBoolean(data,offset++);
    }

    /**
     * @Author liuyi
     * @Description //获取有符号的int8
     * @Date 2021/8/27 11:10
     * @Param []
     * @return byte
     **/
    public byte getInt8(){
        return BytesUtils.bytesToByte(data,offset++);
    }

    /**
     * @Author liuyi
     * @Description //获取有符号的int8
     * @Date 2021/8/27 11:12
     * @Param []
     * @return short
     **/
    public short getUint8(){
        return BytesUtils.bytesToUint8(data,offset++);
    }

    /**
     * @Author liuyi
     * @Description //获取有符号的int16
     * @Date 2021/8/27 11:31
     * @Param []
     * @return short
     **/
    public short getInt16(){
        short s = BytesUtils.bytesToShort(data, offset);
        offset += 2;
        return s;
    }

    /**
     * @Author liuyi
     * @Description //获取无符号的int16
     * @Date 2021/8/30 13:30
     * @Param []
     * @return long
     **/
    public int getUint16() {
        int i = BytesUtils.bytesToUint16(data, offset);
        offset += 2;
        return i;
    }

    /**
     * @Author liuyi
     * @Description //获取有符号的int32
     * @Date 2021/8/30 13:30
     * @Param []
     * @return long
     **/
    public int getInt32() {
        int i = BytesUtils.bytesToInt(data, offset, 4);
        offset += 4;
        return i;
    }

    /**
     * @Author liuyi 
     * @Description //获取无符号的int32
     * @Date 2021/8/30 13:30 
     * @Param [] 
     * @return long
     **/
    public long getUint32() {
        long l = BytesUtils.bytesToUint32(data, offset);
        offset += 4;
        return l;
    }

    /**
     * @Author liuyi
     * @Description //获取有符号的int64
     * @Date 2021/8/30 13:30
     * @Param []
     * @return long
     **/
    public long getInt64() {
        long l = BytesUtils.bytesToLong(data, offset, 8);
        offset += 8;
        return l;
    }

    /**
     * @Author liuyi
     * @Description //获取一个float
     * @Date 2021/8/30 14:36
     * @Param []
     * @return float
     **/
    public float getFloat(){
        float f = BytesUtils.getFloat(data, offset);
        offset += 4;
        return f;
    }

    /**
     * @Author liuyi
     * @Description //获取一个double
     * @Date 2021/8/30 14:36
     * @Param []
     * @return float
     **/
    public double getDouble(){
        double d = BytesUtils.getDouble(data,offset);
        offset += 8;
        return d;
    }

    /**
     * @Author liuyi
     * @Description //获取字符串（utf-8）
     * @Date 2021/8/30 14:02
     * @Param []
     * @return java.lang.String
     **/
    public String getString(int len){
        String s = BytesUtils.bytesToString(data, offset, len);
        offset += len;
        return s;
    }

    /**
     * @Author liuyi
     * @Description //TODO
     * @Date 2021/8/30 15:50
     * @Param []
     * @return java.sql.Date
     **/
    public Date getDateTime(){
        Date date = new Date(getInt64());
        offset += 8;
        return date;
    }

    /**
     * @Author liuyi
     * @Description //获取基本array类型
     * @Date 2021/8/30 15:29
     * @Param [rawData, dataType]
     * @return java.util.List<java.lang.Object>
     **/
    public List<Object> getBaseArray(byte valueType) throws OPException {
        //获取value长度类型
        byte lenType = getInt8();
        long valueSize = getValueLen(lenType);
        //获取基础数据类型的固定字节长度
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < valueSize; i ++) {
            switch (valueType){
                case BOOL_ARRAY:
                    list.add(getBoolean());
                    break;
                case INT8_ARRAY:
                    list.add(getInt8());
                    break;
                case INT16_ARRAY:
                    list.add(getInt16());
                    break;
                case INT32_ARRAY:
                    list.add(getInt32());
                    break;
                case INT64_ARRAY:
                    list.add(getInt64());
                    break;
                case FLOAT_ARRAY:
                    list.add(getFloat());
                    break;
                case DOUBLE_ARRAY:
                    list.add(getDouble());
                    break;
                case DATETIME_ARRAY:
                    list.add(getDateTime());
                    break;
                default:
                    throw new OPException("不存在的Array基础类型，opType==="+valueType);
            }
        }
        return list;
    }

}
