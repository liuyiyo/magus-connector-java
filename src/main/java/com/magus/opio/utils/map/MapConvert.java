package com.magus.opio.utils.map;

import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.Common;
import com.magus.opio.utils.UtilsBuffer;
import com.magus.opio.utils.array.ArrayConvert;
import com.magus.opio.utils.struct.StructConvert;
import java.util.*;

import static com.magus.opio.OPType.*;


public class MapConvert implements MapRange {
    private MapConvert() {
    }

    public static <K, V> byte[] encodeMap(Map<K, V> value) throws OPException {
        if (value == null || value.size() == 0)
            return Bytes.MakeEmptyBinary();
        UtilsBuffer utilsBuffer = new UtilsBuffer();
        //get k,v type
        //当前只支持value类型一致的类型
        byte kType = 0, vType = 0;
        for (K key : value.keySet()) {
            byte kType_ = Common.getObjType(key);
            V v = value.get(key);
            byte vType_ = Common.getObjType(v);
            if (kType > 0 && kType != kType_) {
                throw new OPException("map key type need same");
            }
            if (vType > 0 && vType != vType_) {
                throw new OPException("map val type need same");
            }
            kType = kType_;
            vType = vType_;
        }
        //k type check
        if (kType <= OPType.VtBool || kType > OPType.VtString) {
            throw new OPException("map k type:" + kType + ",not support");
        }
        //v type check
        if (vType < 0) {
            throw new OPException("map v type:" + vType + ",not support");
        }
        utilsBuffer.put(new byte[]{kType, vType});
        for (Map.Entry<K, V> kvEntry : value.entrySet()) {
            K key = kvEntry.getKey();
            byte[] buf = null;
            switch (kType) {
                case VtInt8:
                    buf = new byte[1];
                    Bytes.PutInt8(buf, (Byte) key);
                    break;
                case OPType.VtInt16:
                    buf = new byte[2];
                    Bytes.PutInt16(buf, (Short) key);
                    break;
                case OPType.VtInt32:
                    buf = new byte[4];
                    Bytes.PutInt32(buf, (Integer) key);
                    break;
                case OPType.VtInt64:
                    buf = new byte[8];
                    Bytes.PutInt64(buf, (Long) key);
                    break;
                case OPType.VtFloat:
                    buf = new byte[4];
                    Bytes.PutFloat32(buf, (Float) key);
                    break;
                case OPType.VtDouble:
                    buf = new byte[8];
                    Bytes.PutFloat64(buf, (Double) key);
                    break;
                case OPType.VtString:
                    byte[] b = Bytes.PutString((String) key);
                    buf = Arrays.copyOfRange(b, 1, b.length);
                    break;
                default:
                    break;
            }
            utilsBuffer.put(buf);

            buf = null;
            V val = kvEntry.getValue();
            switch (vType) {
                case OPType.VtBool:
                    buf = new byte[1];
                    Bytes.PutBool(buf, (Boolean) val);
                    break;
                case VtInt8:
                    buf = new byte[1];
                    Bytes.PutInt8(buf, (Byte) val);
                    break;
                case OPType.VtInt16:
                    buf = new byte[2];
                    Bytes.PutInt16(buf, (Short) val);
                    break;
                case OPType.VtInt32:
                    buf = new byte[4];
                    Bytes.PutInt32(buf, (Integer) val);
                    //buf = new byte[8];
                    //Bytes.PutInt64(buf, (Integer) val);
                    break;
                case OPType.VtInt64:
                    buf = new byte[8];
                    Bytes.PutInt64(buf, (Long) val);
                    break;
                case OPType.VtFloat:
                    buf = new byte[4];
                    Bytes.PutFloat32(buf, (Float) val);
                    break;
                case OPType.VtDouble:
                    buf = new byte[8];
                    Bytes.PutFloat64(buf, (Double) val);
                    break;
                case OPType.VtString:
                    byte[] b = Bytes.PutString((String) val);
                    buf = Arrays.copyOfRange(b, 1, b.length);
                    break;
                case OPType.VtMap:
                    buf = encodeMap((Map<?, ?>) val);
                    break;
                case OPType.VtArray:
                    buf = ArrayConvert.encodeArray((List<?>) val);
                    break;
                case OPType.VtStructure:
                    buf = StructConvert.encodeStruct(val);
                    break;
                default:
                    break;
            }
            utilsBuffer.put(buf);
        }
        byte[] b = Bytes.PutBinary(utilsBuffer.bytes());
        return Arrays.copyOfRange(b, 1, b.length);
    }

    public static MapDecoder decodeMap(byte[] src) throws OPException {

        if (Bytes.IsEmptyBinary(src))
            return null;
        MapDecoder mapDecoder = new MapDecoder();
        mapDecoder.setData(src);

        int offset = mapDecoder.getHeadLen();
        byte keyType = src[offset];
        byte valType = src[offset + 1];
        offset += 2;
        mapDecoder.setKeyType(keyType);
        mapDecoder.setValType(valType);

        int bodyLen = mapDecoder.getBodyLen();
        if (Common.minMapBodyLen == bodyLen)
            return mapDecoder;
        switch (keyType) {
            case VtInt8:
            case VtInt16:
            case VtInt32:
            case VtInt64:
                mapDecoder.setPair(new IntPairImpl(src, keyType, valType, offset));
                break;
            case VtFloat:
            case VtDouble:
                mapDecoder.setPair(new FloatPairImpl(src, keyType, valType, offset));
                break;
            case VtString:
                mapDecoder.setPair(new StringPairImpl(src, keyType, valType, offset));
                break;
            default:
                break;
        }

        return mapDecoder;
    }

    public static void main(String[] args) throws Exception {
        //Map<String, String> m = new HashMap<String, String>() {
        //    {
        //        put("a", "xx");
        //        put("b", "yy");
        //        put("c", "zz");
        //    }
        //};
        //byte[] res = encodeMap(m);
        //System.out.println(res.length);
        //System.out.println(Common.print(res));

//        Map<Integer, String> m = new HashMap<Integer, String>() {
//            {
//                put(10, "aa");
//                put(11, "bb");
//                put(12, "cc");
//            }
//        };
//        byte[] res = encodeMap(m);
//        System.out.println(res.length);
//        System.out.println(Common.print(res));

        //Map<Float, String> m = new HashMap<Float, String>() {
        //    {
        //        put(10.1f, "xx");
        //        put(11.1f, "yy");
        //        put(12.1f, "zz");
        //    }
        //};
        //byte[] res = encodeMap(m);
        //System.out.println(res.length);
        //System.out.println(Common.print(res));

        //System.out.println("===================");
        //Map<Integer, List<String>> m2 = new HashMap<Integer, List<String>>() {
        //    {
        //        put(1, Arrays.asList("a", "b"));
        //        put(2, Arrays.asList("c", "d"));
        //    }
        //};
        //encodeMap(m2);

        //System.out.println("===================");
        //Map<Double, Map<String, Integer>> m3 = new HashMap<Double, Map<String, Integer>>() {
        //    {
        //        put(1.2d, new HashMap<String, Integer>() {
        //            {
        //                put("xx", 12);
        //                put("yy", 13);
        //            }
        //        });
        //        put(1.3d, new HashMap<String, Integer>() {
        //            {
        //                put("hh", 12);
        //                put("ll", 13);
        //            }
        //        });
        //    }
        //};
        //res = encodeMap(m3);
        //System.out.println(res.length);
        //System.out.println(Common.print(res));

//        MapDecoder mapDecoder = decodeMap(res);
//        mapDecoder.range(new MapConvert());
//
//        System.out.println("~~~~end~~~~");

        //Thread.sleep(1000 * 60);

//        Map<String,Object> map1 = new HashMap<>();
//        map1.put("a",123);
//        map1.put("b",456);
//        map1.put("c",789);
//        map1.put("d",56451);
//        map1.put("e",554656);
//        map1.put("f",87874);
//        List<String> list1 = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            list1.add(String.valueOf(i));
//        }
//        Map<String,Object> map = new HashMap<>();
//        map.put("a", new RelationInfo("重庆武隆","13356487845",map1,list1));
//        map.put("b", new RelationInfo("重庆江北","13356566552",map1,list1));
//        map.put("c", new RelationInfo("重庆渝北","13356454545",map1,list1));
//        map.put("d", new RelationInfo("重庆南岸","13356454566",map1,list1));
//        byte[] bytes = encodeMap(map);
//        System.out.println(Common.print(bytes));
//        System.out.println(bytes.length);
//
//        MapDecoder mapDecoder = decodeMap(bytes);
//        System.out.println(mapDecoder);


    }

    @Override
    public boolean range(Object key, Object val) {
        System.out.println("k:" + key.toString() + ",v:" + val.toString());
        //byte keyType = Common.getObjType(key);
        //byte valType = Common.getObjType(val);

        return true;
    }
}
