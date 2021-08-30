package com.magus.opio.utils.map;

import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.Common;
import com.magus.opio.utils.array.ArrayConvert;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.struct.StructConvert;
import com.magus.opio.utils.struct.StructDecoder;

import java.util.*;

import static com.magus.opio.OPType.*;
import static com.magus.opio.io.OPIOBuffer.*;

public class IntPairImpl implements Pair {
    private byte[] src;
    private byte valType;
    private Map<Long, Long> keyPos;

    public IntPairImpl(byte[] src, byte keyType, byte valType, int startPos) throws OPException {
        this.src = src;
        this.valType = valType;

        if (src == null || src.length == 0) {
            throw new OPException("src is empty");
        }

        if (keyType != VtInt8 && keyType != VtInt16 && keyType != VtInt32 && keyType != VtInt64) {
            throw new OPException("unsupported integer key type:" + keyType);
        }

        byte valFixedLen = Common.fixedTypeLen.getOrDefault(valType, (byte) -1);
        int offset = startPos;
        int total = src.length;
        long key = 0;
        this.keyPos = new HashMap<>();
        System.out.println("key type:" + keyType);
        while (offset != total) {
            //offset++;
            switch (keyType) {
                case VtInt8:
                    key = src[offset];
                    offset++;
                    break;
                case VtInt16:
                    key = Bytes.GetInt16(Arrays.copyOfRange(src, offset, offset + 2));
                    offset += 2;
                    break;
                case VtInt32:
                    key = Bytes.GetInt32(Arrays.copyOfRange(src, offset, offset + 4));
                    offset += 4;
                    break;
                case VtInt64:
                    key = Bytes.GetInt64(Arrays.copyOfRange(src, offset, offset + 8));
                    offset += 8;
                    break;
                default:
                    break;
            }

            //pos:high 32, len:low 32
            long posAndLen = offset;
            if (valFixedLen > 0) {  //val是定长
                offset += valFixedLen;
            } else {    //val是变长
                posAndLen = posAndLen << 32;
                int headLen = 0, dataLen = 0;
                byte flagByte_ = src[offset];
                offset++;
                headLen++;
                switch (flagByte_) {
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
                }
                posAndLen = posAndLen | (long) (headLen + dataLen);

                //skip to next pair
                offset += dataLen;
            }

            System.out.println("init int key:" + key);

            keyPos.put(key, posAndLen);
        }
    }

    public void setSrc(byte[] src) {
        this.src = src;
    }

    public void setValType(byte valType) {
        this.valType = valType;
    }

    public void setKeyPos(Map<Long, Long> keyPos) {
        this.keyPos = keyPos;
    }

    @Override
    public boolean getBool(Object key) {
        if (valType != VtBool)
            return false;
        if (src.length == 0 || keyPos.size() == 0)
            return false;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return false;
        if (src[(int) pos] > 0)
            return true;
        return false;
    }

    @Override
    public byte getInt8(Object key) {
        if (valType != VtInt8)
            return 0;
        if (src.length == 0 || keyPos.size() == 0)
            return 0;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return 0;
        return src[(int) pos];
    }

    @Override
    public short getInt16(Object key) {
        if (valType != VtInt16)
            return 0;
        if (src.length == 0 || keyPos.size() == 0)
            return 0;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return 0;
        return (short) Bytes.GetInt16(Arrays.copyOfRange(src, (int) pos, (int) (pos + 2)));
    }

    @Override
    public int getInt32(Object key) {
        if (valType != VtInt32)
            return 0;
        if (src.length == 0 || keyPos.size() == 0)
            return 0;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return 0;
        return Bytes.GetInt32(Arrays.copyOfRange(src, (int) pos, (int) (pos + 4)));
    }

    @Override
    public long getInt64(Object key) {
        if (valType != VtInt64)
            return 0;
        if (src.length == 0 || keyPos.size() == 0)
            return 0;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return 0;
        return Bytes.GetInt64(Arrays.copyOfRange(src, (int) pos, (int) (pos + 8)));
    }

    @Override
    public float getFloat32(Object key) {
        if (valType != VtFloat)
            return 0;
        if (src.length == 0 || keyPos.size() == 0)
            return 0;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return 0;
        return Bytes.GetFloat32(Arrays.copyOfRange(src, (int) pos, (int) (pos + 4)));
    }

    @Override
    public double getFloat64(Object key) {
        if (valType != VtDouble)
            return 0;
        if (src.length == 0 || keyPos.size() == 0)
            return 0;
        long pos = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (pos == -1)
            return 0;
        return Bytes.GetFloat64(Arrays.copyOfRange(src, (int) pos, (int) (pos + 8)));
    }

    @Override
    public String getString(Object key) {
        if (valType != VtString)
            return "";
        if (src.length == 0 || keyPos.size() == 0)
            return "";
        long posLen = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (posLen == -1)
            return "";
        int pos = (int) (posLen >> 32);
        int dataLen = (int) posLen;
        return Bytes.GetStringExt(Arrays.copyOfRange(src, pos, pos + dataLen));
    }

    @Override
    public ArrayDecoder getArray(Object key) throws OPException {
        if (valType != VtArray)
            return null;
        if (src.length == 0 || keyPos.size() == 0)
            return null;
        long posLen = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (posLen == -1)
            return null;
        int pos = (int) (posLen >> 32);
        int dataLen = (int) posLen;
        return ArrayConvert.decodeArray(Arrays.copyOfRange(src, pos, pos + dataLen));
    }

    @Override
    public MapDecoder getMap(Object key) throws OPException {
        if (valType != VtMap)
            return null;
        if (src.length == 0 || keyPos.size() == 0)
            return null;
        long posLen = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (posLen == -1)
            return null;
        int pos = (int) (posLen >> 32);
        int dataLen = (int) posLen;
        return MapConvert.decodeMap(Arrays.copyOfRange(src, pos, pos + dataLen));
    }

    @Override
    public StructDecoder getStruct(Object key) throws OPException {
        if (valType != VtStructure)
            return null;
        if (src.length == 0 || keyPos.size() == 0)
            return null;
        long posLen = keyPos.getOrDefault(Common.obj2Int(key), (long) -1);
        if (posLen == -1)
            return null;
        int pos = (int) (posLen >> 32);
        int dataLen = (int) posLen;
        return StructConvert.decodeStruct(Arrays.copyOfRange(src, pos, pos + dataLen));
    }

    @Override
    public List<?> allKeys() {
        return new ArrayList<>(keyPos.keySet());
    }
}
