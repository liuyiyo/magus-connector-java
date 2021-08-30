package com.magus.jdbc;

import com.magus.opio.dto.OPValue;

public enum OPValueEnum {

	TYPE_NULL(OPValue.TYPE_NULL, null, "NULL"), TYPE_BOOL(OPValue.TYPE_BOOL, Boolean.class, "Boolean"), TYPE_INT8(OPValue.TYPE_INT8, Byte.class, "Byte"), TYPE_INT16(OPValue.TYPE_INT16, Short.class, "Short"), TYPE_INT32(OPValue.TYPE_INT32, Integer.class, "Integer"), TYPE_INT64(OPValue.TYPE_INT64, Long.class, "Long"), TYPE_FLOAT(OPValue.TYPE_FLOAT,
			Float.class, "Float"), TYPE_DOUBLE(OPValue.TYPE_DOUBLE, Double.class, "Double"), TYPE_DATETIME(OPValue.TYPE_DATETIME, java.util.Date.class, "Date"), TYPE_STRING(OPValue.TYPE_STRING, String.class, "String"), TYPE_BINARY(OPValue.TYPE_BINARY, (new byte[0]).getClass(), "byte[]"), TYPE_OBJECT(OPValue.TYPE_OBJECT, Object.class, "Object"),

	TYPE_ARRAY(OPValue.TYPE_ARRAY, java.sql.Array.class, "Array"), TYPE_MAP(OPValue.TYPE_MAP, java.util.Map.class, "Map"),

	TYPE_BOOL_ARRAY(OPValue.TYPE_BOOL_ARRAY, (new boolean[0]).getClass(), "boolean[]"), TYPE_INT8_ARRAY(OPValue.TYPE_INT8_ARRAY, (new byte[0]).getClass(), "byte[]"), TYPE_INT16_ARRAY(OPValue.TYPE_INT16_ARRAY, (new short[0]).getClass(), "short[]"), TYPE_INT32_ARRAY(OPValue.TYPE_INT32_ARRAY, (new int[0]).getClass(), "int[]"), TYPE_INT64_ARRAY(OPValue.TYPE_INT64_ARRAY, (new long[0]).getClass(),
			"long[]"), TYPE_FLOAT_ARRAY(OPValue.TYPE_FLOAT_ARRAY, (new float[0]).getClass(), "float[]"), TYPE_DOUBLE_ARRAY(OPValue.TYPE_DOUBLE_ARRAY, (new double[0]).getClass(), "double[]"), TYPE_DATETIME_ARRAY(OPValue.TYPE_DATETIME_ARRAY, (new java.util.Date[0]).getClass(), "Date"), TYPE_STRING_ARRAY(OPValue.TYPE_STRING_ARRAY, (new String[0]).getClass(), "String[]"), TYPE_BINARY_ARRAY(
			OPValue.TYPE_BINARY_ARRAY, (new byte[0][0]).getClass(), "byte[][]"), TYPE_OBJECT_ARRAY(OPValue.TYPE_OBJECT_ARRAY, (new Object[0]).getClass(), "Object[]");

	private String type;

	private Class classObject;

	private int typeCode;

	private OPValueEnum(int typeCode, String typeString) {
		this.typeCode = typeCode;
		this.type = typeString;
	}

	private OPValueEnum(int typeCode, Class classObject, String typeString) {
		this.typeCode = typeCode;
		this.classObject = classObject;
		this.type = typeString;
	}

	public String getType() {
		return type;
	}

	public int getTypeCode() {
		return typeCode;
	}

	public Class getClassObject() {
		return classObject;
	}

	public static OPValueEnum valueOf(int type) {
		for (OPValueEnum typeValue : values()) {
			if (typeValue.getTypeCode() == type) {
				return typeValue;
			}
		}
		return TYPE_NULL;
	}
}