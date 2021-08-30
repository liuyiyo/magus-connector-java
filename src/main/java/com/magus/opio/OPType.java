package com.magus.opio;

public class OPType {
	public static final byte ArrayMask = 16;;
	public static final byte VtNull = 0;
	public static final byte VtBool = 1;
	public static final byte VtInt8 = 2;
	public static final byte VtInt16 = 3;
	public static final byte VtInt32 = 4;
	public static final byte VtInt64 = 5;
	public static final byte VtFloat = 6;
	public static final byte VtDouble = 7;
	public static final byte VtDateTime = 8;
	public static final byte VtString = 9;
	public static final byte VtBinary = 10;

	public static final byte VtObject = 11;

	public static final byte VtMap = 12;
	public static final byte VtStructure = 13;

	public static final byte VtSlice = 14; // VtSlice ==VtArray ;
	public static final byte VtArray = 14;// VtSlice ==VtArray ;

	public static final byte VtEnum = 15;


	public static final byte BOOL_ARRAY = 17;
	public static final byte INT8_ARRAY = 18;
	public static final byte INT16_ARRAY = 19;
	public static final byte INT32_ARRAY = 20;
	public static final byte INT64_ARRAY = 21;
	public static final byte FLOAT_ARRAY = 22;
	public static final byte DOUBLE_ARRAY = 23;
	public static final byte DATETIME_ARRAY = 24;
	public static final byte STRING_ARRAY = 25;
	public static final byte BINARY_ARRAY = 26;

	public static final byte OBJECT_ARRAY = 27;
	public static final byte STRUCT = 28;
	public static final byte VtRow = 32;
	public static final byte VtRowArray = 48;

	/**
	 * @Author liuyi
	 * @Description 各个基本类型的长度
	 * @Date 2021/5/14 10:50
	 * @Param
	 * @return
	 **/
	public static final class BasicTypeLength{
		public static final byte VtBoolLen = 1;
		public static final byte VtInt8Len = 1;
		public static final byte VtInt16Len = 2;
		public static final byte VtInt32Len = 4;
		public static final byte VtInt64Len = 8;
		public static final byte VtFloatLen = 4;
		public static final byte VtDoubleLen = 8;
		public static final byte VtDateTimeLen = 8;
	}
}
