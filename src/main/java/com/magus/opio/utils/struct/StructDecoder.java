package com.magus.opio.utils.struct;

import com.alibaba.fastjson.JSON;
import com.magus.opio.OPException;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.BytesBase;
import com.magus.opio.utils.array.ArrayConvert;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.map.MapConvert;
import com.magus.opio.utils.map.MapDecoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.magus.opio.OPType.*;

public class StructDecoder extends BytesBase {
	private Map<String, Field_> fields;

	public Map<String, Field_> getFields() {
		return fields;
	}

	public void setFields(Map<String, Field_> fields) {
		this.fields = fields;
	}

	public boolean isEmpty() {
		return fields == null || fields.size() == 0 || getData() == null || getData().length == 0;
	}

	public Field_[] getAllField() {
		if (isEmpty())
			return null;
		int fieldNum = fields.size();
		Field_[] fields_ = new Field_[fieldNum];
		int index = 0;
		for (Map.Entry<String, Field_> entry : fields.entrySet()) {
			fields_[index] = entry.getValue();
			index++;
		}
		return fields_;
	}

	public Field_ getField(String fieldName) {
		if (isEmpty())
			return null;
		return fields.getOrDefault(fieldName, null);
	}

	public boolean getBool(String fieldName) {
		if (isEmpty())
			return false;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return false;
		if (field_.type != VtBool) {
			return false;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return Bytes.GetBool(valRaw);
	}

	public byte getInt8(String fieldName) {
		if (isEmpty())
			return 0;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return 0;
		if (field_.type != VtInt8) {
			return 0;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return (byte) Bytes.GetInt8(valRaw);
	}

	public short getInt16(String fieldName) {
		if (isEmpty())
			return 0;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return 0;
		if (field_.type != VtInt16) {
			return 0;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return (short) Bytes.GetInt16(valRaw);
	}

	public int getInt32(String fieldName) {
		if (isEmpty())
			return 0;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return 0;
		if (field_.type != VtInt32) {
			return 0;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return Bytes.GetInt32(valRaw);
	}

	public long getInt64(String fieldName) {
		if (isEmpty())
			return 0;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return 0;
		if (field_.type != VtInt64) {
			return 0;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return Bytes.GetInt64(valRaw);
	}

	public float getFloat32(String fieldName) {
		if (isEmpty())
			return 0;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return 0;
		if (field_.type != VtFloat) {
			return 0;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return Bytes.GetFloat32(valRaw);
	}

	public double getFloat64(String fieldName) {
		if (isEmpty())
			return 0;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return 0;
		if (field_.type != VtDouble) {
			return 0;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return Bytes.GetFloat64(valRaw);
	}

	public String getString(String fieldName) {
		if (isEmpty())
			return "";
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return "";
		if (field_.type != VtString) {
			return "";
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return Bytes.GetStringExt(valRaw);
	}

	public ArrayDecoder getArray(String fieldName) throws OPException {
		if (isEmpty())
			return null;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return null;
		if (field_.type != VtArray) {
			return null;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return ArrayConvert.decodeArray(valRaw);
	}

	public MapDecoder getMap(String fieldName) throws OPException {
		if (isEmpty())
			return null;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return null;
		if (field_.type != VtMap) {
			return null;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return MapConvert.decodeMap(valRaw);
	}

	public StructDecoder getStruct(String fieldName) throws OPException {
		if (isEmpty())
			return null;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return null;
		if (field_.type != VtStructure) {
			return null;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		return StructConvert.decodeStruct(valRaw);
	}

	public Object get(String fieldName) throws OPException {
		if (isEmpty())
			return null;
		Field_ field_ = fields.getOrDefault(fieldName, null);
		if (field_ == null)
			return null;
		if (field_.type != VtBool) {
			return null;
		}
		byte[] src = getData();
		byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
		switch (field_.type) {
		case VtBool:
			return Bytes.GetBool(valRaw);
		case VtInt8:
			return Bytes.GetInt8(valRaw);
		case VtInt16:
			return Bytes.GetInt16(valRaw);
		case VtInt32:
			return Bytes.GetInt32(valRaw);
		case VtInt64:
			return Bytes.GetInt64(valRaw);
		case VtFloat:
			return Bytes.GetFloat32(valRaw);
		case VtDouble:
			return Bytes.GetFloat64(valRaw);
		case VtString:
			return Bytes.GetStringExt(valRaw);
		case VtArray:
			return ArrayConvert.decodeArray(valRaw);
		case VtMap:
			return MapConvert.decodeMap(valRaw);
		case VtStructure:
			return StructConvert.decodeStruct(valRaw);
		default:
			break;
		}
		return null;
	}

	public void range(StructRange structRange) throws OPException {
		if (isEmpty())
			throw new OPException("range fields or src is null");
		byte[] src = getData();
		boolean breakLoop = false;
		for (Map.Entry<String, Field_> entry : fields.entrySet()) {
			Field_ field_ = entry.getValue();
			String fieldName = entry.getKey();
			byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
			switch (field_.type) {
			case VtBool:
				if (!structRange.range(fieldName, Bytes.GetBool(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtInt8:
				if (!structRange.range(fieldName, (byte) Bytes.GetInt8(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtInt16:
				if (!structRange.range(fieldName, Bytes.GetInt16(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtInt32:
				if (!structRange.range(fieldName, Bytes.GetInt32(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtInt64:
				if (!structRange.range(fieldName, Bytes.GetInt64(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtFloat:
				if (!structRange.range(fieldName, Bytes.GetFloat32(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtDouble:
				if (!structRange.range(fieldName, Bytes.GetFloat64(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtString:
				if (!structRange.range(fieldName, Bytes.GetStringExt(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtArray:
				if (!structRange.range(fieldName, ArrayConvert.decodeArray(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtMap:
				if (!structRange.range(fieldName, MapConvert.decodeMap(valRaw))) {
					breakLoop = true;
				}
				break;
			case VtStructure:
				if (!structRange.range(fieldName, StructConvert.decodeStruct(valRaw))) {
					breakLoop = true;
				}
				break;
			default:
				break;
			}
			if (breakLoop)
				break;
		}
	}

    /**
     * @Author liuyi
     * @Description //组装对象
     * @Date 17:27 2021/4/7
     * @Param [tClass]
     * @return T
     **/
    public <T> T createObject(Class<T> tClass){
        //创建放置具体结果属性的对象
        T obj = null;
        try {
            obj = tClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        byte[] src = getData();
        T finalObj = obj;
        fields.entrySet().parallelStream()
                    .forEach(entry-> {
                        try {
                            Field_ field_ = entry.getValue();
                            String fieldName = entry.getKey();
                            //获取泛型类的属性
                            Field declaredField = tClass.getDeclaredField(fieldName);
                            //获取类中属性对应的set方法
                            Method method = tClass.getMethod(getSetName(fieldName), declaredField.getType());
                            byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
                            switch (field_.type) {
                                case VtBool:
                                    method.invoke(finalObj,Bytes.GetBool(valRaw));
                                    break;
                                case VtInt8:
                                    method.invoke(finalObj,(byte) Bytes.GetInt8(valRaw));
                                    break;
                                case VtInt16:
                                    method.invoke(finalObj,Bytes.GetInt16(valRaw));
                                    break;
                                case VtInt32:
                                    method.invoke(finalObj,Bytes.GetInt32(valRaw));
                                    break;
                                case VtInt64:
                                    method.invoke(finalObj,Bytes.GetInt64(valRaw));
                                    break;
                                case VtFloat:
                                    method.invoke(finalObj,Bytes.GetFloat32(valRaw));
                                    break;
                                case VtDouble:
                                    method.invoke(finalObj,Bytes.GetFloat64(valRaw));
                                    break;
                                case VtString:
                                    method.invoke(finalObj,Bytes.GetStringExt(valRaw));
                                    break;
                                case VtArray:
									ArrayDecoder arrayDecoder = ArrayConvert.decodeArray(valRaw);
									if(arrayDecoder.isEmpty()) break;
									//获取list的value类型
									ParameterizedType parameterizedType = (ParameterizedType)declaredField.getGenericType();
									Class genericType = (Class)parameterizedType.getActualTypeArguments()[0];
                                    method.invoke(finalObj,arrayDecoder.createList(genericType));
                                    break;
                                case VtMap:
									MapDecoder mapDecoder = MapConvert.decodeMap(valRaw);
									if(mapDecoder.isEmpty()) break;
									//获取map的value类型
									ParameterizedType parameterizedMapType = (ParameterizedType)declaredField.getGenericType();
									Class genericMapType = (Class)parameterizedMapType.getActualTypeArguments()[1];
									method.invoke(finalObj,mapDecoder.createMap(genericMapType));
                                    break;
                                case VtStructure:
									StructDecoder structDecoder = StructConvert.decodeStruct(valRaw);
									if(structDecoder==null||structDecoder.isEmpty()) break;
									method.invoke(finalObj,structDecoder.createObject(declaredField.getType()));
                                    break;
                                default:
                                    break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    });
                    return obj;
    }

	/**
	 * 组装Map对象
	 * @return
	 */
	public Map<String,Object> createMap(){
		//创建放置具体结果属性的对象
		Map<String,Object> map = new ConcurrentHashMap();
		byte[] src = getData();
		fields.entrySet().parallelStream()
				.forEach(entry-> {
					try {
						Field_ field_ = entry.getValue();
						String fieldName = entry.getKey();
						byte[] valRaw = Arrays.copyOfRange(src, field_.pos, field_.pos + field_.dataLen);
						switch (field_.type) {
							case VtBool:
								map.put(fieldName,Bytes.GetBool(valRaw));
								break;
							case VtInt8:
								map.put(fieldName,(byte) Bytes.GetInt8(valRaw));
								break;
							case VtInt16:
								map.put(fieldName,Bytes.GetInt16(valRaw));
								break;
							case VtInt32:
								map.put(fieldName,Bytes.GetInt32(valRaw));
								break;
							case VtInt64:
								map.put(fieldName,Bytes.GetInt64(valRaw));
								break;
							case VtFloat:
								map.put(fieldName,Bytes.GetFloat32(valRaw));
								break;
							case VtDouble:
								map.put(fieldName,Bytes.GetFloat64(valRaw));
								break;
							case VtString:
								map.put(fieldName,Bytes.GetStringExt(valRaw));
								break;
							case VtArray:
								ArrayDecoder arrayDecoder = ArrayConvert.decodeArray(valRaw);
								if(arrayDecoder.isEmpty()) break;
								map.put(fieldName,arrayDecoder.createList(null));
								break;
							case VtMap:
								MapDecoder mapDecoder = MapConvert.decodeMap(valRaw);
								if(mapDecoder.isEmpty()) break;
								map.put(fieldName,mapDecoder.createMap(null));
								break;
							case VtStructure:
								StructDecoder structDecoder = StructConvert.decodeStruct(valRaw);
								if(structDecoder==null||structDecoder.isEmpty()) break;
								map.put(fieldName,structDecoder.createMap());
								break;
							default:
								break;
						}
					}catch (Exception e){
						e.printStackTrace();
					}

				});
		return map;
	}

	/**
	 * 获取对象Str
	 * @return
	 */
	public String createMapStr(){
		Map<String, Object> map = createMap();
		return JSON.toJSONString(map);

	}

    /**
     * @Author liuyi
     * @Description //组装set方法
     * @Date 14:37 2021/4/7
     * @Param [name]
     * @return java.lang.String
     **/
    public static String getSetName(String name){
        return "set"+name.substring(0,1).toUpperCase()+name.substring(1);
    }


    @Override
    public String toString() {
        return "StructDecoder{}";
    }
}
