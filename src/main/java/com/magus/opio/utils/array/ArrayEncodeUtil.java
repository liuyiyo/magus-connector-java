package com.magus.opio.utils.array;

import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.BytesUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.magus.opio.OPType.*;
import static com.magus.opio.OPConstant.*;
import static com.magus.opio.utils.EmptyUtil.*;
import static com.magus.opio.utils.EmptyUtil.makeEmptyBinary;
import static com.magus.opio.utils.array.ArrayCodeUtil.*;

/**
 * @ClassName ArrayEncode
 * @description：数组编码工具类
 * @author：liuyi
 * @Date：2021/5/14 9:55
 */
public class ArrayEncodeUtil {
    public static void main(String[] args) throws OPException {
//        System.out.println("Boolean测试============================================================");
//        List<Boolean> list = new ArrayList<>();
//        list.add(true);
//        list.add(false);
//        list.add(true);
//        list.add(false);
//        list.add(true);
//        list.add(false);
//        list.add(true);
//        list.add(false);
//        System.out.println(Arrays.toString(encodeBaseArray(list,BOOL_ARRAY)));
//        List<Boolean> list1 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list,BOOL_ARRAY),Boolean.class);
//        System.out.println(list1);
//        System.out.println("byte测试============================================================");
//        List<Byte> list2 = new ArrayList<>();
//        list2.add((byte) 12);
//        list2.add((byte)13);
//        list2.add((byte)14);
//        list2.add((byte)15);
//        list2.add((byte)16);
//        list2.add((byte)17);
//        list2.add((byte)18);
//        list2.add((byte)19);
//        System.out.println(Arrays.toString(encodeBaseArray(list2,INT8_ARRAY)));
//        List<Byte> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,INT8_ARRAY),Byte.class);
//        System.out.println(list3);

//        System.out.println("short测试============================================================");
//        List<Short> list2 = new ArrayList<>();
//        list2.add((short)1012);
//        list2.add((short)1013);
//        list2.add((short)1014);
//        list2.add((short)1015);
//        list2.add((short)1016);
//        list2.add((short)1017);
//        list2.add((short)1018);
//        list2.add((short)1019);
//        System.out.println(Arrays.toString(encodeBaseArray(list2,INT16_ARRAY)));
//        List<Short> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,INT16_ARRAY),Short.class);
//        System.out.println(list3);'

//        System.out.println("int测试============================================================");
//        List<Integer> list2 = new ArrayList<>();
//        list2.add(11012);
//        list2.add(11013);
//        list2.add(11014);
//        list2.add(11015);
//        list2.add(11016);
//        list2.add(11017);
//        list2.add(11018);
//        list2.add(11019);
//        System.out.println(Arrays.toString(encodeBaseArray(list2,INT32_ARRAY)));
//        List<Integer> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,INT32_ARRAY),Integer.class);
//        System.out.println(list3);

//        System.out.println("float测试============================================================");
//        List<Float> list2 = new ArrayList<>();
//        list2.add((float)11012.01);
//        list2.add((float)11012.02);
//        list2.add((float)11012.03);
//        list2.add((float)11012.04);
//        list2.add((float)11012.05);
//        list2.add((float)11012.06);
//        list2.add((float)11012.07);
//        list2.add((float)11012.08);
//        System.out.println(Arrays.toString(encodeBaseArray(list2,FLOAT_ARRAY)));
//        List<Object> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,FLOAT_ARRAY),FLOAT_ARRAY);
//        System.out.println(list3);

//        System.out.println("double测试============================================================");
//        List<Double> list2 = new ArrayList<>();
//        list2.add((double)11012.01);
//        list2.add((double)11012.02);
//        list2.add((double)11012.03);
//        list2.add((double)11012.04);
//        list2.add((double)11012.05);
//        list2.add((double)11012.06);
//        list2.add((double)11012.07);
//        list2.add((double)11012.08);
//        System.out.println(Arrays.toString(encodeBaseArray(list2,DOUBLE_ARRAY)));
//        List<Object> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,DOUBLE_ARRAY),DOUBLE_ARRAY);
//        System.out.println(list3);

//        System.out.println("long测试============================================================");
//        List<Long> list2 = new ArrayList<>();
//        list2.add(11012L);
//        list2.add(11013L);
//        list2.add(11014L);
//        list2.add(11015L);
//        list2.add(11016L);
//        list2.add(11017L);
//        list2.add(11018L);
//        list2.add(11019L);
//        System.out.println(Arrays.toString(encodeBaseArray(list2,INT64_ARRAY)));
//        List<Object> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,INT64_ARRAY),INT64_ARRAY);
//        System.out.println(list3);

//        System.out.println("datetime测试============================================================");
//        List<Date> list2 = new ArrayList<>();
//        list2.add(new Date());
//        list2.add(new Date());
//        list2.add(new Date());
//        list2.add(new Date());
//        list2.add(new Date());
//        list2.add(new Date());
//        list2.add(new Date());
//        list2.add(new Date());
//        System.out.println(Arrays.toString(encodeBaseArray(list2,DATETIME_ARRAY)));
//        List<Object> list3 = ArrayDecodeUtil.decodeBaseArray(encodeBaseArray(list2,DATETIME_ARRAY),DATETIME_ARRAY);
//        System.out.println(list3);


//        System.out.println("string变长array测试");
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            list.add(i + "");
//        }
//        System.out.println(Arrays.toString(encodeVarArray(list, STRING_ARRAY)));
//        System.out.println(Arrays.toString("0".getBytes()));
//        List<Object> objects = ArrayDecodeUtil.decodeVarArray(encodeVarArray(list, STRING_ARRAY), STRING_ARRAY);
//        System.out.println(objects.get(0));

        System.out.println("binary变长array测试");
        List<byte[]> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            byte[] bytes = new byte[i];
            for (int i1 = 0; i1 < bytes.length; i1++) {
                bytes[i1] = (byte)i1;
            }
            list.add(bytes);
        }
        List<Object> objects = ArrayDecodeUtil.decodeVarArray(encodeVarArray(list, BINARY_ARRAY), BINARY_ARRAY);
        System.out.println(objects);
        for (Object object : objects) {
            System.out.println(Arrays.toString((byte[]) object));
        }

        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");
        stringList.add("liuyi");
        System.out.println(Arrays.toString("Hello".getBytes()));
        byte[] bytes = encodeVarArray(stringList, STRING_ARRAY);
        System.out.println(Arrays.toString(bytes));
        System.out.println(ArrayDecodeUtil.decodeVarArray(bytes,STRING_ARRAY));


    }

    /**
     * @return byte[]
     * @Author liuyi
     * @Description //基本类型Array编码
     * @Date 2021/5/17 13:14
     * @Param [list]
     * TYPE SIZE(UINT8 UINT16 UINT32 ) DATA
     *
     **/
    public static byte[] encodeBaseArray(List<?> list, byte dataType) throws OPException {
        if (isEmpty(list)) {
            //如果为空，返回一个约定的字节数组
            return makeEmptyBinary();
        }
        byte dataLen = getLenByDataType(dataType);
        byte[] rawData;
        long bodyLen = (long) list.size() * dataLen;
        int headLen;
        if (bodyLen < MAX_UINT8) {
            headLen = 3;
            rawData = new byte[headLen + (int) bodyLen];
            rawData[0] = dataType;
            rawData[1] = VtInt8;//设置body长度编码
            rawData[2] = (byte) bodyLen;//设置body长度
        } else if (bodyLen > MAX_UINT8 && bodyLen <= MAX_UINT16) {
            headLen = 4;
            rawData = new byte[headLen + (int) bodyLen];
            rawData[0] = dataType;
            rawData[1] = VtInt16;//设置body长度编码
            //设置body长度
            rawData[2] = (byte) (bodyLen >> 8);
            rawData[3] = (byte) bodyLen;
        } else if (bodyLen < MAX_UINT32) {
            headLen = 6;
            rawData = new byte[(int) (headLen + bodyLen)];
            rawData[0] = dataType;
            rawData[1] = VtInt32;//设置body长度编码
            //设置body长度
            rawData[2] = (byte) (bodyLen >> 24);
            rawData[3] = (byte) (bodyLen >> 16);
            rawData[4] = (byte) (bodyLen >> 8);
            rawData[5] = (byte) (bodyLen);
        } else {
            throw new OPException("data长度超长");
        }
        for (Object value : list) {
            byte[] v = new byte[dataLen];
            if (value instanceof Boolean) {
                Bytes.PutBool(v, (Boolean) value);
            } else if (value instanceof Byte) {
                Bytes.PutInt8(v, (Byte) value);
            } else if (value instanceof Short) {
                Bytes.PutInt16(v, (Short) value);
            } else if (value instanceof Integer) {
                Bytes.PutInt32(v, (Integer) value);
            } else if (value instanceof Long) {
                Bytes.PutInt64(v, (Long) value);
            } else if (value instanceof Float) {
                Bytes.PutFloat32(v, (Float) value);
            } else if (value instanceof Double) {
                Bytes.PutFloat64(v, (Double) value);
            } else if (value instanceof Date) {
                Date date = (Date) value;
                Bytes.PutFloat64(v, ((double) date.getTime()) / 1e3);
            }
            System.arraycopy(v, 0, rawData, headLen, v.length);
            headLen += dataLen;
        }
        return rawData;
    }

    /**
     * @return byte[]
     * @Author liuyi
     * @Description //变长数组类型编码
     * @Date 2021/5/18 9:51
     * @Param [list, dataType]
     **/
    public static byte[] encodeVarArray(List<?> list, byte dataType) throws OPException {
        if (isEmpty(list)) {
            //如果为空，返回一个约定的字节数组
            return makeEmptyBinary();
        }
        byte[] bytes;
        //设置head
        int len = list.size();
        if (len <= MAX_UINT8) {
            bytes = new byte[3];
            //设置类型
            bytes[0] = (dataType);
            bytes[1] = VtInt8;
            bytes[2] = (byte) len;
        } else if (len > MAX_UINT8 && len <= MAX_UINT16) {
            bytes = new byte[4];
            //设置类型
            bytes[0] = (dataType);
            bytes[1] = VtInt16;
            bytes[2] = (byte) (len >> 8 & 0xff);
            bytes[3] = (byte) (len & 0xff);
        } else {
            bytes = new byte[6];
            //设置类型
            bytes[0] = (dataType);
            bytes[1] = VtInt32;
            bytes[2] = (byte) (len >> 24 & 0xff);
            bytes[3] = (byte) (len >> 16 & 0xff);
            bytes[4] = (byte) (len >> 8 & 0xff);
            bytes[5] = (byte) (len & 0xff);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置list
        try {
            for (Object o : list) {
                if (o == null) {
                    bos.write(makeEmptyBinary());
                    continue;
                }
                byte[] bytesData = new byte[0];
                if (o instanceof String) {
                    bytesData = o.toString().getBytes();
                } else if (o instanceof byte[]) {
                    bytesData = (byte[]) o;
                }
                int dataLen = bytesData.length;
                bos.write(BytesUtils.getVlueLenBytes(dataLen));
                bos.write(bytesData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }
}
