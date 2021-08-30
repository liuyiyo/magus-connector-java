package com.magus.opio.utils;

import com.alibaba.fastjson.JSONObject;
import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.utils.array.ArrayConvert;
import com.magus.opio.utils.map.MapConvert;
import com.magus.opio.utils.struct.StructConvert;

import java.util.*;

public class Common {

    public static void main(String[] args) throws OPException {
        putVarVal("1",OPType.VtString);
    }

    public static byte getObjType(Object value) {
        if (value instanceof Boolean) {
            return OPType.VtBool;
        } else if (value instanceof Byte) {
            return OPType.VtInt8;
        } else if (value instanceof Short) {
            return OPType.VtInt16;
        } else if (value instanceof Integer) {
            return OPType.VtInt32;
        } else if (value instanceof Long) {
            return OPType.VtInt64;
        } else if (value instanceof Float) {
            return OPType.VtFloat;
        } else if (value instanceof Double) {
            return OPType.VtDouble;
        } else if (value instanceof Date) {
            return OPType.VtDateTime;
        } else if (value instanceof String) {
            return OPType.VtString;
        } else if (value instanceof Map<?, ?>) {
            return OPType.VtMap;
        } else if (value instanceof List<?>) {
            return OPType.VtArray;
        } else {
            return OPType.VtStructure;
        }
    }

    public static String print(byte[] bytes) {
        StringBuffer sb = new StringBuffer("[");
        for (byte b : bytes) {
            sb.append((b & 0xff) + " ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static final Map<Byte, Byte> fixedTypeLen = new HashMap<Byte, Byte>() {
        {
            put(OPType.VtBool, (byte) 1);
            put(OPType.VtInt8, (byte) 1);
            put(OPType.VtInt16, (byte) 2);
            //put(OPType.VtInt32, (byte) 8);//和go打包规则保持一致
            put(OPType.VtInt32, (byte) 4);
            put(OPType.VtInt64, (byte) 8);
            put(OPType.VtFloat, (byte) 4);
            put(OPType.VtDouble, (byte) 8);
            put(OPType.VtDateTime, (byte) 8);
        }
    };

    public static void putFixedVal(Object val, int valType, byte[] buf) {
        switch (valType) {
            case OPType.VtBool:
                Bytes.PutBool(buf, (Boolean) val);
                break;
            case OPType.VtInt8:
                Bytes.PutInt8(buf, (Byte) val);
                break;
            case OPType.VtInt16:
                Bytes.PutInt16(buf, (Short) val);
                break;
            case OPType.VtInt32:
                //和go打包规则保持一致
                //Bytes.PutInt64(buf, (Integer) val);
                Bytes.PutInt32(buf, (Integer) val);
                break;
            case OPType.VtInt64:
                Bytes.PutInt64(buf, (Long) val);
                break;
            case OPType.VtFloat:
                Bytes.PutFloat32(buf, (Float) val);
                break;
            case OPType.VtDouble:
                Bytes.PutFloat64(buf, (Double) val);
                break;
            case OPType.VtDateTime:
                Bytes.PutFloat64(buf, ((double) ((Date) val).getTime()) / 1e3);
                break;
            default:
                break;
        }
    }

    public static byte[] putVarVal(Object val, int valType) throws OPException {
        switch (valType) {
            case OPType.VtString:
                byte[] eleRaw = Bytes.PutString((String) val);
                return Arrays.copyOfRange(eleRaw, 1, eleRaw.length);
            case OPType.VtArray:
                return ArrayConvert.encodeArray((List<?>) val);
            case OPType.VtMap:
                return MapConvert.encodeMap((Map<?, ?>) val);
            case OPType.VtStructure:
                return StructConvert.encodeStruct(val);
            default:
                return null;
        }
    }

    public static final int minMapBodyLen = 2;

    public static long obj2Int(Object val) {
        if (val instanceof Byte) {
            return (byte) val;
        } else if (val instanceof Short) {
            return (short) val;
        } else if (val instanceof Integer) {
            return (int) val;
        } else if (val instanceof Long) {
            return (long) val;
        } else {
            return 0;
        }
    }

    public static double obj2Double(Object val) {
        if (val instanceof Float) {
            return (float) val;
        } else if (val instanceof Double) {
            return (double) val;
        } else {
            return 0;
        }
    }

    public static String obj2String(Object val) {
        if (val instanceof String) {
            return (String) val;
        } else {
            return "";
        }
    }

}
