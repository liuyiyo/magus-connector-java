package com.magus.opio.utils.array;

import com.magus.opio.OPException;
import com.magus.opio.io.OPIOBuffer;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.BytesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.magus.opio.OPType.*;
import static com.magus.opio.utils.EmptyUtil.*;
import static com.magus.opio.utils.array.ArrayCodeUtil.*;

/**
 * @ClassName ArrayDecodeUtil
 * @description：数组解码工具类
 * @author：liuyi
 * @Date：2021/5/14 9:56
 */
public class ArrayDecodeUtil {

    public static void main(String[] args) {
        byte[] arr = new byte[4];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        arr[3] = 4;
        System.out.println(Arrays.toString(Arrays.copyOfRange(arr, 1, 4)));
    }

    /**
     * @return java.util.List<object>
     * @Author liuyi
     * @Description 基本类型Array解码
     * @Date 2021/5/14 11:59
     * @Param [list]
     **/
    public static List<Object> decodeBaseArray(byte[] rawData, byte dataType) throws OPException {
        if (IsEmptyBinary(rawData)) return null;
        //获取类型
        byte dataType2 = rawData[0];
        if (dataType != dataType2) throw new OPException("头信息数据类型和数据体类型对不上");
        byte dataLen = getLenByDataType(dataType);
        //获取dody类型
        byte bodyLenType = rawData[1];
        int headLen;
        //获取head的长度
        switch (bodyLenType) {
            case VtInt8:
                headLen = 3;
                break;
            case VtInt16:
                headLen = 4;
                break;
            case VtInt32:
                headLen = 6;
                break;
            default:
                throw new OPException("BOOL_ARRAY解码 body长度异常");
        }
        //获取body的长度
        int bodyLen = rawData.length - headLen;
        byte[] bodyArray = new byte[bodyLen];
        //获取bodyArray
        System.arraycopy(rawData, headLen, bodyArray, 0, bodyLen);
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < bodyArray.length; i += dataLen) {
            byte[] v = new byte[dataLen];
            System.arraycopy(bodyArray, i, v, 0, dataLen);
            addValueByDataType(list, dataType, v);
        }
        return list;
    }

    /**
     * @return java.util.List<java.lang.Object>
     * @Author liuyi
     * @Description 变长类型Array解码
     * @Date 2021/5/18 11:34
     * @Param [rawData, dataType]
     **/
    public static List<Object> decodeVarArray(byte[] rawData, byte dataType) throws OPException {
        if (IsEmptyBinary(rawData)) return null;
        if (rawData.length < 2) throw new OPException("数组长度异常，不能小于2");
        //获取类型
        byte dataType2 = rawData[0];
        if (dataType != dataType2) throw new OPException("头信息数据类型和数据体类型对不上");
        //获取数据长度的类型
        byte lenType = rawData[1];
        int dataLen = 0;
        int headLen = 0;
        switch (lenType){
            case VtInt8:
                dataLen = BytesUtils.bytesToInt(rawData,2,1);
                headLen = 3;
                break;
            case VtInt16:
                dataLen = BytesUtils.bytesToInt(rawData,2,2);
                headLen = 4;
                break;
            case VtInt32:
                dataLen = BytesUtils.bytesToInt(rawData,2,4);
                headLen = 6;
                break;
            default:
                throw new OPException("不存在这样的数据长度类型");
        }
        int bodyLen = rawData.length - headLen;
        byte[] bodyBytes = new byte[bodyLen];
        System.arraycopy(rawData, headLen, bodyBytes, 0, bodyLen);
        List<Object> list = new ArrayList<>();
        int globalOffset = 0;//定义全局offset
        for (int i = 0; i < dataLen; i++) {
            byte typeFlag = bodyBytes[globalOffset];//获取数据长度类型
            int valueLen = 0;
            globalOffset +=1;
            switch (typeFlag) {
                case VtInt8:
                    valueLen = BytesUtils.bytesToInt(bodyBytes,globalOffset,1);
                    globalOffset += 1;
                    break;
                case VtInt16:
                    valueLen = BytesUtils.bytesToInt(bodyBytes,globalOffset,2);
                    globalOffset += 2;
                    break;
                case VtInt32:
                    valueLen = BytesUtils.bytesToInt(bodyBytes,globalOffset,4);
                    globalOffset += 4;
                    break;
                default:
                    break;
            }
            switch (dataType){
                case STRING_ARRAY:
                    list.add(new String(Arrays.copyOfRange(bodyBytes, globalOffset, globalOffset+valueLen)));
                    break;
                case BINARY_ARRAY:
                    list.add(Arrays.copyOfRange(bodyBytes, globalOffset, globalOffset+valueLen));
                    break;
                default:
                    throw new OPException("暂不支持该类型解码");
            }
            globalOffset += valueLen;
        }
        return list;
    }

}
