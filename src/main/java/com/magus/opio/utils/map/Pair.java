package com.magus.opio.utils.map;

import com.magus.opio.OPException;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.struct.StructDecoder;

import java.util.List;

public interface Pair {
    boolean getBool(Object key);

    byte getInt8(Object key);

    short getInt16(Object key);

    int getInt32(Object key);

    long getInt64(Object key);

    float getFloat32(Object key);

    double getFloat64(Object key);

    String getString(Object key);

    ArrayDecoder getArray(Object key) throws OPException;

    MapDecoder getMap(Object key) throws OPException;

    StructDecoder getStruct(Object key) throws OPException;

    List<?> allKeys();
}
