package com.magus.opio.utils.struct;

import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.Common;
import com.magus.opio.utils.UtilsBuffer;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.array.Iter;
import com.magus.opio.utils.map.MapDecoder;
import com.magus.opio.utils.map.MapRange;

import java.lang.reflect.Field;
import java.util.*;

import static com.magus.opio.io.OPIOBuffer.*;

public class StructConvert implements StructRange, MapRange {

    public static void main(String[] args) {

    }

    private StructConvert() {
    }

    public static byte[] encodeStruct(Object obj) throws OPException {
        if (obj == null)
            return Bytes.MakeEmptyBinary();
        final UtilsBuffer fieldBuf = new UtilsBuffer();
        final UtilsBuffer structBuf = new UtilsBuffer();
        Class<?> cls = obj.getClass();
        try {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                fieldBuf.reset();
                field.setAccessible(true);
                String name = field.getName();
                if (name.length() > 0x100)
                    throw new OPException("field name too long," + name);
                //put name len
                byte[] lenRaw = new byte[1];
                Bytes.PutInt8(lenRaw, (byte) name.length());
                fieldBuf.put(lenRaw);
                //put name
                //byte[] nameRaw = Bytes.PutString(name);
                //fieldBuf.put(Arrays.copyOfRange(nameRaw, 1, nameRaw.length));
                fieldBuf.put(name.getBytes());
                //java not support tag
                //put tag
                fieldBuf.put(new byte[]{0});
                Object val = field.get(obj);
                int valType = Common.getObjType(val);
                if (valType == -1)
                    throw new OPException("val type:" + valType + " unsupport");
                byte[] buf = null;
                int fixedLen = Common.fixedTypeLen.getOrDefault((byte) valType, (byte) -1);
                if (fixedLen != -1) {
                    buf = new byte[fixedLen];
                    Common.putFixedVal(val, valType, buf);
                } else {
                    buf = Common.putVarVal(val, valType);
                }
                fieldBuf.put(buf);
                byte[] valTypeRaw = new byte[1];
                Bytes.PutInt8(valTypeRaw, (byte) valType);
                structBuf.put(valTypeRaw);
                byte[] fieldBytes = Bytes.PutBinary(fieldBuf.bytes());
                structBuf.put(Arrays.copyOfRange(fieldBytes, 1, fieldBytes.length));
            }
            byte[] rawData = Bytes.PutBinary(structBuf.bytes());
            return Arrays.copyOfRange(rawData, 1, rawData.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new OPException(e.getMessage());
        }
    }

    public static StructDecoder decodeStruct(byte[] src) throws OPException {
        if (Bytes.IsEmptyBinary(src))
            return null;
        StructDecoder structDecoder = new StructDecoder();
        structDecoder.setData(src);

        int offset = 0;
        byte flagByte = src[0];
        offset++;
        switch (flagByte) {
            case mpBin8:
                offset++;
                break;
            case mpBin16:
                offset += 2;
                break;
            case mpBin32:
                offset += 4;
                break;
            default:
                break;
        }

        Map<String, Field_> fields = decodeFields(src, offset);
        structDecoder.setFields(fields);
        return structDecoder;
    }

    public static Map<String, Field_> decodeFields(byte[] src, int start) throws OPException {
        int total = src.length;
        if (total == 0) {
            throw new OPException("decodeFields src is null");
        }
        Map<String, Field_> resFields = new HashMap<>();
        try {
            int offset = start;
            while (offset != total) {
                byte fieldType = src[offset];
                offset++;
                byte fixedLen = Common.fixedTypeLen.getOrDefault(fieldType, (byte) -1);
                byte fieldLenFlag = src[offset];
                offset++;
                switch (fieldLenFlag) {
                    case mpBin8:
                        offset++;
                        break;
                    case mpBin16:
                        offset += 2;
                        break;
                    case mpBin32:
                        offset += 4;
                        break;
                    default:
                        break;
                }

                //field name
                int nameLen = src[offset]&0xff;
                offset++;
                String fieldName = new String(Arrays.copyOfRange(src, offset, offset + nameLen), "UTF-8");
                offset += nameLen;

                //tag
                byte tagFlagByte = src[offset];
                offset++;
                String fieldTag = "";
                if (tagFlagByte == 1) {
                    int tagLen = src[offset] & 0xff;
                    offset++;
                    fieldTag = new String(Arrays.copyOfRange(src, offset, offset + tagLen), "UTF-8");
                    offset += tagLen;
                }

                int dataPos = offset;
                int valLen = 0;
                if (fixedLen != -1) {
                    offset += fixedLen;
                    valLen = fixedLen;
                } else {
                    int headLen = 0, dataLen = 0;
                    byte flagByte = src[offset];
                    offset++;
                    headLen++;
                    switch (flagByte) {
                        case mpBin8:
                            dataLen = src[offset] & 0xff;
                            offset++;
                            headLen++;
                            break;
                        case mpBin16:
                            dataLen = Bytes.GetInt16(Arrays.copyOfRange(src, offset, offset + 2));
                            offset += 2;
                            headLen += 2;
                            break;
                        case mpBin32:
                            dataLen = Bytes.GetInt32(Arrays.copyOfRange(src, offset, offset + 4));
                            offset += 4;
                            headLen += 4;
                            break;
                        default:
                            break;
                    }
                    valLen = headLen + dataLen;
                    //skip to next field
                    offset += dataLen;
                }
                resFields.put(fieldName, new Field_(fieldName, fieldTag, fieldType, dataPos, valLen));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OPException(e.getMessage());
        }
        return resFields;
    }

    @Override
    public boolean range(String key, Object val) {
        System.out.print("field:" + key);
        if (val instanceof ArrayDecoder) {
            try {
                ArrayDecoder arrayDecoder = (ArrayDecoder) val;
                Iter iter = arrayDecoder.getIter();
                System.out.print(",[");
                for (iter.seekToFirst(); iter.valid(); iter.next()) {
                    System.out.print(arrayDecoder.get() + ",");
                }
                System.out.println("]");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        } else if (val instanceof MapDecoder) {
            try {
                MapDecoder mapDecoder = (MapDecoder) val;
                mapDecoder.range(new StructConvert());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else if (val instanceof StructDecoder) {
            try {
                StructDecoder structDecoder = (StructDecoder) val;
                structDecoder.range(new StructConvert());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println(",v:" + val);
        }
        return true;
    }

    @Override
    public boolean range(Object key, Object val) {
        //System.out.println(",map k:" + key.toString() + ",v:" + val.toString());
        System.out.println(",map k:" + key.toString() + ",v:" + val.toString());
        return true;
    }
}
