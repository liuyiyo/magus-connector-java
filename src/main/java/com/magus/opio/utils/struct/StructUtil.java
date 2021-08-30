package com.magus.opio.utils.struct;

import com.magus.opio.OPConstant;
import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.base.ByteUtil;
import com.magus.opio.utils.base.OPTypeUtil;
import com.magus.opio.utils.base.StrcutInfo;
import static com.magus.opio.utils.BytesUtils.*;
import static com.magus.opio.OPType.*;
import java.lang.reflect.Field;
import java.util.*;


/**
 * @ClassName StructEncodeUtil
 * @description：结构体编码工具类
 * @author：liuyi
 * @Date：2021/8/24 14:09
 */
public class StructUtil {

    private static final List<FieldPack> fieldPackList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        List<StrcutInfo> infoList = new ArrayList<>();

//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000000; i++) {
//            StructConvert.encodeStruct(info);
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("修改前耗时"+(end-start));
        for (int i = 0; i < 1; i++) {
            StrcutInfo info = new StrcutInfo();
            info.setBoolTest(true);
            info.setInt8Test((byte) 127);
            info.setInt16Test((short) 26554);
            info.setInt32Test(i);
            info.setInt64Test(Long.MAX_VALUE);
            info.setFloatTest(453645.1f);
            info.setDoubleTest(1.31335252E7);
            info.setDatetimeTest(new Date());
            infoList.add(info);
        }
        //根据反射获取类型
        Class<?> cls = StrcutInfo.class;
        //获取所有属性
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            FieldPack pack = new FieldPack();
            ByteUtil headBytes = new ByteUtil();
            //设置可访问私有变量
            field.setAccessible(true);
            pack.setField(field);
            //获取属性名称
            String name = field.getName();
            //设置属性名称长度，占用一个字节
            if(name.length() > OPConstant.MAX_UINT8) throw new OPException("属性名称的长度不能大于255");
            headBytes.append(((byte) (name.length() & 0xff)));
            //设置属性名称内容
            headBytes.appendAll(name.getBytes());
            //获取类型
            Class<?> type = field.getType();
            //根据java类型获取lightning数据库对应的类型
            byte opType = OPTypeUtil.getOPTypeByClass(type);
            pack.setOpType(opType);
            //设置类型
            headBytes.append(opType);
            pack.setHeadBytes(headBytes.data);
            fieldPackList.add(pack);
        }
        long start2 = System.currentTimeMillis();
        for (StrcutInfo strcutInfo : infoList) {
            System.out.println(Arrays.toString(encodeStruct(strcutInfo)));
            decodeStruct(encodeStruct(strcutInfo));
        }
        long end2 = System.currentTimeMillis();
        System.out.println("修改后耗时" + (end2 - start2));
    }

    /**
     * @return byte[]
     * @Author liuyi
     * @Description //对象编码
     * @Date 2021/8/25 13:32
     * @Param [obj]
     **/
    public static byte[] encodeStruct(Object obj) throws OPException {
        if (obj == null) {
            return Bytes.MakeEmptyBinary();
        }
        //设置value的字节数组
        ByteUtil struct = new ByteUtil();
        try {
            //记录属性的个数（不包含为null的）
            long filedNum = 0;
            for (FieldPack pack : fieldPackList) {
                Object value = pack.getField().get(obj);
                //如果值为null，则不做任何处理，字段名称也不需要存
                if (value == null) {
                    continue;
                }
                //设置数据长度
                ByteUtil allBytes = new ByteUtil();
                allBytes.appendAll(pack.getHeadBytes());
                allBytes.appendAll(getBytes(value, pack.getOpType()));
                struct.appendAll(allBytes.data);
                filedNum++;
            }
            //设置整体字节数组
            return getAllBytes(VtStructure,filedNum,struct.data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OPException(e.getMessage());
        }
    }

    /**
     * @return T
     * @Author liuyi
     * @Description //对象解码
     * @Date 2021/8/26 10:32
     * @Param [bytes, tClass]
     **/
    public static <T> T decodeStruct(byte[] bytes) throws OPException{
        ByteUtil byteUtil = new ByteUtil(bytes);
        //获取类型
        byte type = byteUtil.getInt8();
        if(VtStructure!=type) throw new OPException("struct内容解析类型不一致，当前类型为："+type);
        //获取属性的个数类型
        byte sizeType = byteUtil.getInt8();
        //获取属性个数
        long size = byteUtil.getValueLen(sizeType);
        //递归读取value，直到读取到size个属性为止
        for (long i = 0; i < size; i++) {
            //读取属性名称长度
            short nameLen = byteUtil.getUint8();
            //读取属性名称
            String name = byteUtil.getString(nameLen);
            //读取value的类型
            byte valueType = byteUtil.getInt8();
            //读取value的值
            Object value = byteUtil.getValue(valueType);
            System.out.println(name+":"+value);
        }
        return null;
    }


}

class FieldPack {
    private Field field;
    private byte opType;
    private byte[] headBytes;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }


    public byte getOpType() {
        return opType;
    }

    public void setOpType(byte opType) {
        this.opType = opType;
    }

    public byte[] getHeadBytes() {
        return headBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        this.headBytes = headBytes;
    }
}
