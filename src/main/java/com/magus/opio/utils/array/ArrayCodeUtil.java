package com.magus.opio.utils.array;

import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import java.util.List;
import static com.magus.opio.OPType.*;
import static com.magus.opio.OPType.BasicTypeLength.*;
import static com.magus.opio.OPType.BasicTypeLength.VtDateTimeLen;
import static com.magus.opio.OPType.DATETIME_ARRAY;


/**
 * @ClassName ArrayCodeUtil
 * @description：
 * @author：liuyi
 * @Date：2021/5/17 14:21
 */
public class ArrayCodeUtil {
    /**
     * @Author liuyi
     * @Description //根据数据类型获取数据长度
     * @Date 2021/5/17 13:42
     * @Param [dataType]
     * @return byte
     **/
    public static byte getLenByDataType(byte valueType)throws OPException {
        switch (valueType){
            case BOOL_ARRAY:
                return VtBoolLen;
            case INT8_ARRAY:
                return VtInt8Len;
            case INT16_ARRAY:
                return VtInt16Len;
            case INT32_ARRAY:
                return VtInt32Len;
            case INT64_ARRAY:
                return VtInt64Len;
            case FLOAT_ARRAY:
                return VtFloatLen;
            case DOUBLE_ARRAY:
                return VtDoubleLen;
            case DATETIME_ARRAY:
                return VtDateTimeLen;
            default:
                throw new OPException("不存在"+valueType+"基本数据类型");
        }
    }

    /**
     * @Author liuyi
     * @Description //根据class获取数据类型
     * @Date 2021/5/17 15:03
     * @Param [tClass]
     * @return byte
     **/
    public static byte getDataTypeBytClass(Class tClass) throws OPException{
        switch (tClass.getName()){
            case "java.lang.Boolean":
                return BOOL_ARRAY;
            case "java.lang.Byte":
                return INT8_ARRAY;
            case "java.lang.Short":
                return INT16_ARRAY;
            case "java.lang.Integer":
                return INT32_ARRAY;
            case "java.lang.Lang":
                return INT64_ARRAY;
            case "java.lang.Float":
                return FLOAT_ARRAY;
            case "java.lang.Double":
                return DOUBLE_ARRAY;
            case "java.util.Date":
                return DATETIME_ARRAY;
            default:
                throw new OPException("不存在"+tClass.getName()+"基本数据类型");
        }
    }

    /**
     * @Author liuyi
     * @Description //根据类型转换字节数组的值
     * @Date 2021/5/17 16:00
     * @Param [list, dataType, v]
     * @return void
     **/
    public static void addValueByDataType(List<Object> list,byte dataType,byte[] v){
        switch (dataType){
            case BOOL_ARRAY:
                list.add(Bytes.GetBool(v));
                break;
            case INT8_ARRAY:
                list.add(Bytes.GetInt8(v));
                break;
            case INT16_ARRAY:
                list.add(Bytes.GetInt16(v));
                break;
            case INT32_ARRAY:
                list.add(Bytes.GetInt32(v));
                break;
            case INT64_ARRAY:
                list.add(Bytes.GetInt64(v));
                break;
            case FLOAT_ARRAY:
                list.add(Bytes.GetFloat32(v));
                break;
            case DOUBLE_ARRAY:
                list.add(Bytes.GetFloat64(v));
                break;
            case DATETIME_ARRAY:
                list.add(Bytes.GetDatetime(v));
                break;
        }
    }
}
