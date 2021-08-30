package com.magus.opio.utils.base;
import com.magus.opio.OPConstant;
import com.magus.opio.OPException;
import com.magus.opio.OPType;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

/**
 * @ClassName TypeUtil
 * @description：
 * @author：liuyi
 * @Date：2021/8/25 14:47
 */
public class OPTypeUtil {
    public static void main(String[] args) throws Exception{
        StrcutInfo info = new StrcutInfo();
//        info.setBoolTest(true);
//        info.setInt8Test((byte) 127);
//        info.setInt16Test((short) 26554);
//        info.setInt32Test(Integer.MAX_VALUE);
//        info.setInt64Test(Long.MAX_VALUE);
//        info.setFloatTest(453645.1f);
//        info.setLocalDateTime(LocalDateTime.now());
//        info.setDoubleTest(13133525.2);
//        info.setDatetimeTest(new Date());
        Class<?> cls = info.getClass();
        Object obj = cls.newInstance();
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            System.out.println(declaredField.getType().getTypeName());
//            System.out.println(ObjectUtils.isEmpty(declaredField.get(info)));
            System.out.println(declaredField.get(info));
        }
    }
    private static final String BOOLEAN = "boolean";
    private static final String BYTE = "byte";
    private static final String SHORT = "short";
    private static final String INT = "int";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String DATE = "java.util.Date";
    private static final String STRING = "java.lang.String";

    /**
     * @Author liuyi
     * @Description //根据java反射对象获取
     * @Date 2021/8/25 15:11
     * @Param [cls]
     * @return void
     **/
    public static byte getOPTypeByClass(Class<?> type) throws OPException{
        String typeName = type.getTypeName();
        switch (typeName){
            case "java.lang.Byte":
            case "byte":
                return OPType.VtInt8;
            case "java.lang.Short":
            case "short":
                return OPType.VtInt16;
            case "java.lang.Integer":
            case "int":
                return OPType.VtInt32;
            case "java.lang.Long":
            case "long":
                return OPType.VtInt64;
            case "java.lang.Float":
            case "float":
                return OPType.VtFloat;
            case "java.lang.Double":
            case "double":
                return OPType.VtDouble;
            case "java.lang.Boolean":
            case "boolean":
                return OPType.VtBool;
            case "java.util.Date":
            case "java.time.LocalDateTime":
            case "date":
                return OPType.VtDateTime;
            case "byte[]":
                return OPType.VtBinary;
            case "java.lang.String":
                return OPType.VtString;
            case "java.util.List<java.lang.Boolean>":
                return OPType.BOOL_ARRAY;
            case "java.util.List<java.lang.Byte>":
                return OPType.INT8_ARRAY;
            case "java.util.List<java.lang.Short>":
                return OPType.INT16_ARRAY;
            case "java.util.List<java.lang.Integer>":
                return OPType.INT32_ARRAY;
            case "java.util.List<java.lang.Long>":
                return OPType.INT64_ARRAY;
            case "java.util.List<java.lang.Float>":
                return OPType.FLOAT_ARRAY;
            case "java.util.List<java.lang.Double>":
                return OPType.DOUBLE_ARRAY;
            case "java.util.List<java.lang.String>":
                return OPType.STRING_ARRAY;
            case "java.util.List<java.util.Date>":
                return OPType.DATETIME_ARRAY;
            case "java.util.List<byte[]>":
                return OPType.BINARY_ARRAY;
            default:
                throw new OPException("==================="+type+"类型不存在");
        }
    }
}
