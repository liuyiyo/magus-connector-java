package com.magus.opio.utils.map;

import com.alibaba.fastjson.JSON;
import com.magus.opio.OPException;
import com.magus.opio.utils.BytesBase;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.struct.StructDecoder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.magus.opio.OPType.*;

public class MapDecoder extends BytesBase {
    private byte keyType;
    private byte valType;
    private Pair pair;

    public void setKeyType(byte keyType) {
        this.keyType = keyType;
    }

    public void setValType(byte valType) {
        this.valType = valType;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public byte getKeyType() {
        return keyType;
    }

    public byte getValType() {
        return valType;
    }

    public Pair getPair() {
        return pair;
    }

    public boolean isEmpty() {
        return this.pair == null;
    }

    public boolean findBool(Object key) {
        if (this.isEmpty())
            return false;
        return this.pair.getBool(key);
    }

    public byte findInt8(Object key) {
        if (this.isEmpty())
            return 0;
        return this.pair.getInt8(key);
    }

    public short findInt16(Object key) {
        if (this.isEmpty())
            return 0;
        return this.pair.getInt16(key);
    }

    public int findInt32(Object key) {
        if (this.isEmpty())
            return 0;
        return this.pair.getInt32(key);
    }

    public long findInt64(Object key) {
        if (this.isEmpty())
            return 0;
        return this.pair.getInt64(key);
    }

    public float findFloat32(Object key) {
        if (this.isEmpty())
            return 0;
        return this.pair.getFloat32(key);
    }

    public double findFloat64(Object key) {
        if (this.isEmpty())
            return 0;
        return this.pair.getFloat64(key);
    }

    public String findString(Object key) {
        if (this.isEmpty())
            return "";
        return this.pair.getString(key);
    }

    public ArrayDecoder findArray(Object key) throws OPException {
        if (this.isEmpty())
            throw new OPException("MapDecoder findArray key:" + key.toString() + " not exit");
        return this.pair.getArray(key);
    }

    public MapDecoder findMap(Object key) throws OPException {
        if (this.isEmpty())
            throw new OPException("MapDecoder findMap key:" + key.toString() + " not exit");
        return this.pair.getMap(key);
    }

    public StructDecoder findStruct(Object key) throws OPException {
        if (this.isEmpty())
            throw new OPException("MapDecoder findStruct key:" + key.toString() + " not exit");
        return this.pair.getStruct(key);
    }

    public void range(MapRange mapRange) throws OPException {
        if (mapRange == null)
            throw new OPException("MapRange obj is nil");
        List<?> keys = this.pair.allKeys();
        boolean breakForLoop = false;
        for (Object key : keys) {
            switch (valType) {
                case VtBool:
                    if (!mapRange.range(key, pair.getBool(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtInt8:
                    if (!mapRange.range(key, pair.getInt8(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtInt16:
                    if (!mapRange.range(key, pair.getInt16(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtInt32:
                    if (!mapRange.range(key, pair.getInt32(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtInt64:
                    if (!mapRange.range(key, pair.getInt64(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtFloat:
                    if (!mapRange.range(key, pair.getFloat32(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtDouble:
                    if (!mapRange.range(key, pair.getFloat64(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtString:
                    if (!mapRange.range(key, pair.getString(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtArray:
                    if (!mapRange.range(key, pair.getArray(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtMap:
                    if (!mapRange.range(key, pair.getMap(key))) {
                        breakForLoop = true;
                    }
                    break;
                case VtStructure:
                    if (!mapRange.range(key, pair.getStruct(key))) {
                        breakForLoop = true;
                    }
                    break;
                default:
                    break;
            }
            if (breakForLoop)
                break;
        }
    }

    /**
     * @Author liuyi
     * @Description //根据class生成map对象
     * @Date 9:09 2021/4/9
     * @Param []
     * @return java.util.Map
     **/
    public Map createMap(Class tClass){
        ConcurrentHashMap map = new ConcurrentHashMap();
        List<?> keys = pair.allKeys();
        keys.parallelStream()
                .forEach(key->{
                    switch (valType) {
                        case VtBool:
                            map.put(key,pair.getBool(key));
                            break;
                        case VtInt8:
                            map.put(key,pair.getInt8(key));
                            break;
                        case VtInt16:
                            map.put(key,pair.getInt16(key));
                            break;
                        case VtInt32:
                            map.put(key,pair.getInt32(key));
                            break;
                        case VtInt64:
                            map.put(key,pair.getInt64(key));
                            break;
                        case VtFloat:
                            map.put(key,pair.getFloat32(key));
                            break;
                        case VtDouble:
                            map.put(key,pair.getFloat64(key));
                            break;
                        case VtString:
                            map.put(key,pair.getString(key));
                            break;
                        case VtArray:
                            try {
                                ArrayDecoder arrayDecoder = pair.getArray(key);
                                map.put(key,arrayDecoder.createList(tClass));
                            } catch (OPException e) {
                                e.printStackTrace();
                            }
                            break;
                        case VtMap:
                            try {
                                MapDecoder mapDecoder = pair.getMap(key);
                                map.put(key,mapDecoder.createMap(tClass));
                            } catch (OPException e) {
                                e.printStackTrace();
                            }
                            break;
                        case VtStructure:
                            try {
                                StructDecoder structDecoder = pair.getStruct(key);
                                if(tClass==null){
                                    map.put(key,structDecoder.createMap());
                                    break;
                                }
                                map.put(key,structDecoder.createObject(tClass));
                            } catch (OPException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                });
        return map;
    }
    /**
     * 生成MapStr
     * @return
     */
    public String createMapStr(){
        Map map = createMap(null);
        return JSON.toJSONString(map);
    }

    @Override
    public String toString() {
        return "MapDecoder{}";
    }
}
