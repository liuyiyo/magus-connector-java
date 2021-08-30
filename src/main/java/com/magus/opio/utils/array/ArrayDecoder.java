package com.magus.opio.utils.array;

import com.alibaba.fastjson.JSON;
import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.BytesBase;
import com.magus.opio.utils.map.MapConvert;
import com.magus.opio.utils.map.MapDecoder;
import com.magus.opio.utils.struct.StructConvert;
import com.magus.opio.utils.struct.StructDecoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.magus.opio.OPType.*;

public class ArrayDecoder extends BytesBase {
    private byte eleType;
    private Iter iter;

    public void setEleType(byte eleType) {
        this.eleType = eleType;
    }

    public void setIter(Iter iter) {
        this.iter = iter;
    }

    public Iter getIter() {
        return iter;
    }

    public byte getEleType() {
        return eleType;
    }

    public boolean isEmpty() {
        return iter == null;
    }

    public int number() {
        if (iter == null)
            return 0;
        return iter.number();
    }

    public boolean getBool() {
        if (isEmpty())
            return false;
        if (eleType != VtBool)
            return false;
        byte[] data = getData();
        if (data.length == 0)
            return false;
        return Bytes.GetInt8(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd())) > 0;
    }

    public boolean[] getBoolValues() {
        if (isEmpty())
            return null;
        if (eleType != VtBool)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        boolean[] res = new boolean[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = Bytes.GetInt8(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd())) > 0;
            index++;
            iter.next();
        }
        return res;
    }

    public byte getInt8() {
        if (isEmpty())
            return 0;
        if (eleType != VtInt8)
            return 0;
        byte[] data = getData();
        if (data.length == 0)
            return 0;
        return (byte) Bytes.GetInt8(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public byte[] getInt8Values() {
        if (isEmpty())
            return null;
        if (eleType != VtInt8)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        byte[] res = new byte[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = (byte) Bytes.GetInt8(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public short getInt16() {
        if (isEmpty())
            return 0;
        if (eleType != VtInt16)
            return 0;
        byte[] data = getData();
        if (data.length == 0)
            return 0;
        return (short) Bytes.GetInt16(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public short[] getInt16Values() {
        if (isEmpty())
            return null;
        if (eleType != VtInt16)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        short[] res = new short[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = (short) Bytes.GetInt16(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public int getInt32() {
        if (isEmpty())
            return 0;
        if (eleType != VtInt32)
            return 0;
        byte[] data = getData();
        if (data.length == 0)
            return 0;
        return (byte) Bytes.GetInt32(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public int[] getInt32Values() {
        if (isEmpty())
            return null;
        if (eleType != VtInt32)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        int[] res = new int[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = Bytes.GetInt32(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public long getInt64() {
        if (isEmpty())
            return 0;
        if (eleType != VtInt64)
            return 0;
        byte[] data = getData();
        if (data.length == 0)
            return 0;
        return (byte) Bytes.GetInt64(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public long[] getInt64Values() {
        if (isEmpty())
            return null;
        if (eleType != VtInt64)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        long[] res = new long[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = Bytes.GetInt64(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public float getFloat() {
        if (isEmpty())
            return 0;
        if (eleType != VtFloat)
            return 0;
        byte[] data = getData();
        if (data.length == 0)
            return 0;
        return (byte) Bytes.GetFloat32(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public float[] getFloatValues() {
        if (isEmpty())
            return null;
        if (eleType != VtFloat)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        float[] res = new float[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = Bytes.GetFloat32(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public double getDouble() {
        if (isEmpty())
            return 0;
        if (eleType != VtDouble)
            return 0;
        byte[] data = getData();
        if (data.length == 0)
            return 0;
        return (byte) Bytes.GetFloat64(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public double[] getDoubleValues() {
        if (isEmpty())
            return null;
        if (eleType != VtDouble)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        double[] res = new double[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = Bytes.GetFloat64(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public String getString() {
        if (isEmpty())
            return "";
        if (eleType != VtString)
            return "";
        byte[] data = getData();
        if (data.length == 0)
            return "";
        return Bytes.GetStringExt(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public String[] getStringValues() {
        if (isEmpty())
            return null;
        if (eleType != VtString)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        String[] res = new String[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = Bytes.GetStringExt(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public ArrayDecoder getArray() throws OPException {
        if (isEmpty())
            return null;
        if (eleType != VtArray)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        return ArrayConvert.decodeArray(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public ArrayDecoder[] getArrayValues() throws OPException {
        if (isEmpty())
            return null;
        if (eleType != VtArray)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        ArrayDecoder[] res = new ArrayDecoder[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = ArrayConvert.decodeArray(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public MapDecoder getMap() throws OPException {
        if (isEmpty())
            return null;
        if (eleType != VtMap)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        return MapConvert.decodeMap(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public MapDecoder[] getMapValues() throws OPException {
        if (isEmpty())
            return null;
        if (eleType != VtMap)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        MapDecoder[] res = new MapDecoder[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = MapConvert.decodeMap(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public StructDecoder getStruct() throws OPException {
        if (isEmpty())
            return null;
        if (eleType != VtStructure)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        return StructConvert.decodeStruct(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
    }

    public StructDecoder[] getStructValues() throws OPException {
        if (isEmpty())
            return null;
        if (eleType != VtStructure)
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        int num = number();
        if (num == 0)
            return null;
        StructDecoder[] res = new StructDecoder[num];
        iter.seekToFirst();
        int index = 0;
        while (iter.valid()) {
            res[index] = StructConvert.decodeStruct(Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd()));
            index++;
            iter.next();
        }
        return res;
    }

    public Object get() throws OPException {
        if (isEmpty())
            return null;
        byte[] data = getData();
        if (data.length == 0)
            return null;
        byte[] raw = Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd());
        switch (eleType) {
            case VtBool:
                return Bytes.GetBool(raw);
            case VtInt8:
                return Bytes.GetInt8(raw);
            case VtInt16:
                return Bytes.GetInt16(raw);
            case VtInt32:
                return Bytes.GetInt32(raw);
            case VtInt64:
                return Bytes.GetInt64(raw);
            case VtFloat:
                return Bytes.GetFloat32(raw);
            case VtDouble:
                return Bytes.GetFloat64(raw);
            case VtString:
                return Bytes.GetStringExt(raw);
            case VtArray:
                return ArrayConvert.decodeArray(raw);
            case VtMap:
                return MapConvert.decodeMap(raw);
            case VtStructure:
                return StructConvert.decodeStruct(raw);
            default:
                break;
        }
        return null;
    }

    /**
     * @Author liuyi 
     * @Description //根据类型生成list对象
     * @Date 9:28 2021/4/9
     * @Param [] 
     * @return java.util.List
     **/       
    public List createList(Class tclass){
        List list = new ArrayList();
        for (iter.seekToFirst(); iter.valid(); iter.next()) {
            try {
                list.add(getByType(tclass));
            } catch (OPException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * 生成listStr
     * @return
     */
    public String createListStr(){
        List list = createList(null);
        return JSON.toJSONString(list);
    }

    /**
     * @Author liuyi
     * @Description //根据类型获取value值
     * @Date 9:28 2021/4/12
     * @Param [tclass]
     * @return java.lang.Object
     **/
    public Object getByType(Class tClass) throws OPException {
        if (isEmpty()){
            return null;
        }
        byte[] data = getData();
        if (data.length == 0)
            return null;
        byte[] raw = Arrays.copyOfRange(data, iter.currentStart(), iter.currentEnd());
        switch (eleType) {
            case VtBool:
                return Bytes.GetBool(raw);
            case VtInt8:
                return Bytes.GetInt8(raw);
            case VtInt16:
                return Bytes.GetInt16(raw);
            case VtInt32:
                return Bytes.GetInt32(raw);
            case VtInt64:
                return Bytes.GetInt64(raw);
            case VtFloat:
                return Bytes.GetFloat32(raw);
            case VtDouble:
                return Bytes.GetFloat64(raw);
            case VtString:
                return Bytes.GetStringExt(raw);
            case VtArray:
                try {
                    ArrayDecoder arrayDecoder = ArrayConvert.decodeArray(raw);
                    return arrayDecoder.createList(tClass);
                } catch (OPException e) {
                    e.printStackTrace();
                }
            case VtMap:
                try {
                    MapDecoder mapDecoder = MapConvert.decodeMap(raw);
                    return mapDecoder.createMap(tClass);
                } catch (OPException e) {
                    e.printStackTrace();
                }
            case VtStructure:
                try {
                    StructDecoder structDecoder = StructConvert.decodeStruct(raw);
                    if(tClass==null) return structDecoder.createMap();
                    return structDecoder.createObject(tClass);
                } catch (OPException e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
        return null;
    }


    @Override
    public String toString() {
        return "ArrayDecoder{}";
    }
}

