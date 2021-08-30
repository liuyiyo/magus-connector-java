package com.magus.opio.dto;

import com.alibaba.fastjson.JSON;
import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.io.OPIOBuffer;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.array.ArrayConvert;
import com.magus.opio.utils.array.ArrayDecodeUtil;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.map.MapConvert;
import com.magus.opio.utils.map.MapDecoder;
import com.magus.opio.utils.struct.StructConvert;
import com.magus.opio.utils.struct.StructDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.magus.opio.io.OPIOBuffer.*;

@Slf4j
public class OPDataset {

	private OPIOBuffer io;
	private OPTable table;
	private List<OPColumn> columns;
	private boolean isFirst;
	private int rowSize;
	private int rowCursor;
	private byte[] rowBuf;
	private byte[] rowVariableBuf;

	public OPDataset(OPIOBuffer io, OPTable table) {
		this.io = io;
		this.isFirst = true;
		this.table = table;
	}

	// public native int next(long dataset);
	public boolean next() throws OPException {
		while (true) {
			if(!isFirst && rowSize ==0 && rowCursor ==0) return false;
			if (isFirst || (rowSize > 0 && rowCursor == rowSize)) {
				int size = io.DecodeArrayStart();
				switch (size) {
					case 0xffffffff: // eof
						isFirst = false;
						rowSize = 0;
						rowCursor = 0;
						return false;
					case 0:
						continue;
					default:
						break;
				}
				rowSize += size;
				isFirst = false;
			}
			rowBuf = io.DecodeBytes();
			rowVariableBuf = Arrays.copyOfRange(rowBuf, table.getFixedLen() + table.getBitLen(), rowBuf.length);
			rowCursor++;
			return true;
		}
	}

	public List<OPColumn> getColumns() {
		columns = table.getColumns();
		return columns;
	}

	public int getColumnCount() {
		return columns.size();
	}

	public String getColumnName(int col) {
		return columns.get(col).getColumnName();
	}

	//
	// public native int column_type(long dataset, int col);
	public int getColumnType(int col) {
		return columns.get(col).getType();
	}

	private byte[] getRaw(int index) throws OPException {
		if (index >= columns.size())
			throw new OPException("index:" + index + ",Size:" + columns.size());
		OPColumn col = columns.get(index);
		int colOff = col.getOffset();
		return Arrays.copyOfRange(rowBuf, colOff, colOff + col.getLength());
	}

	private byte[] getRaw(OPColumn col) throws OPException {
		int colOff = col.getOffset();
		return Arrays.copyOfRange(rowBuf, colOff, colOff + col.getLength());
	}

	//
	// public native int get_column_bool(long dataset, int col);
	public boolean getBoolean(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtBool) {
			switch (type) {
				case OPType.VtNull:
					return false;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return v == 1;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return v == 1;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return i32 == 1;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return i64 == 1;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return (int) f32 != 0;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return (int) f64 != 0;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return date.getTime() != 0;
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Integer.parseInt(str) != 0;
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return Integer.parseInt(obj.toString()) != 1;
				default:
					throw new OPException("getBoolean not support type:" + type);
			}
		}
		return Bytes.GetBool(getRaw(col));
	}

	// public native int get_column_int8(long dataset, int col);
	public byte getByte(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtInt8) {
			switch (type) {
				case OPType.VtNull:
					return 0;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? (byte) 1 : (byte) 0;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return (byte) v;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return (byte) v;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return (byte) i32;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return (byte) i64;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return (byte) f32;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return (byte) f64;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return (byte) date.getTime();
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Byte.parseByte(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return (byte) Integer.parseInt(obj.toString());
				default:
					throw new OPException("getByte not support type:" + type);
			}
		}
		return (byte) Bytes.GetInt8(getRaw(col));
	}

	// public native int get_column_int16(long dataset, int col);
	public short getShort(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtInt16) {
			switch (type) {
				case OPType.VtNull:
					return 0;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? (byte) 1 : (byte) 0;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return v;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return v;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return (short) i32;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return (short) i64;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return (short) f32;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return (short) f64;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return (short) date.getTime();
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Short.parseShort(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return (short) Integer.parseInt(obj.toString());
				default:
					throw new OPException("getShort not support type:" + type);
			}
		}
		return (short) Bytes.GetInt16(getRaw(col));
	}

	// public native int get_column_int32(long dataset, int col);
	public int getInt(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtInt32) {
			switch (type) {
				case OPType.VtNull:
					return 0;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? (byte) 1 : (byte) 0;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return v;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return v;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return i32;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return (int) i64;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return (int) f32;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return (int) f64;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return (int) date.getTime();
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Integer.parseInt(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return Integer.parseInt(obj.toString());
				default:
					throw new OPException("getInt not support type:" + type);
			}
		}
		return Bytes.GetInt32(getRaw(col));
	}

	// public native long get_column_int64(long dataset, int col);
	public long getLong(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtInt64) {
			switch (type) {
				case OPType.VtNull:
					return 0;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? (byte) 1 : (byte) 0;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return v;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return v;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return i32;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return i64;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return (long) f32;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return (long) f64;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return date.getTime();
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Long.parseLong(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return Long.parseLong(obj.toString());
				default:
					throw new OPException("getLong not support type:" + type);
			}
		}
		return Bytes.GetInt64(getRaw(col));
	}

	// public native float get_column_float(long dataset, int col);
	public float getFloat(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtFloat) {
			switch (type) {
				case OPType.VtNull:
					return 0;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? (byte) 1 : (byte) 0;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return v;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return v;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return i32;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return i64;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return f32;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return (float) f64;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return date.getTime();
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Float.parseFloat(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return Float.parseFloat(obj.toString());
				default:
					throw new OPException("getFloat not support type:" + type);
			}
		}
		return Bytes.GetFloat32(getRaw(col));
	}

	// public native double get_column_double(long dataset, int col);
	public double getDouble(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtDouble) {
			switch (type) {
				case OPType.VtNull:
					return 0;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? (byte) 1 : (byte) 0;
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return v;
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return v;
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return i32;
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return i64;
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return f32;
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return f64;
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return date.getTime();
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return Double.parseDouble(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					if (obj instanceof Boolean) {
						return (Boolean) obj ? 1d : 0d;
					} else if (obj instanceof String) {
						return 0.0d;
					} else {
						return Double.parseDouble(obj.toString());
					}
				default:
					throw new OPException("getDouble not support type:" + type);
			}
		}
		return Bytes.GetFloat64(getRaw(col));
	}

	// public native String get_column_string(long dataset, int col);
	public String getString(int colIndex) throws OPException {
		try {
			if (colIndex >= columns.size())
				throw new OPException("index:" + colIndex + ",Size:" + columns.size());
			OPColumn col = columns.get(colIndex);
			int type = col.getType();
			if (type != OPType.VtString) {
				switch (type) {
					case OPType.VtNull:
						return "";
					case OPType.VtBool:
						boolean b = Bytes.GetBool(getRaw(col));
						return b + "";
					case OPType.VtInt8:
						short v = Bytes.GetInt8(getRaw(col));
						return v + "";
					case OPType.VtInt16:
						v = (short) Bytes.GetInt16(getRaw(col));
						return v + "";
					case OPType.VtInt32:
						int i32 = Bytes.GetInt32(getRaw(col));
						return i32 + "";
					case OPType.VtInt64:
						long i64 = Bytes.GetInt64(getRaw(col));
						return i64 + "";
					case OPType.VtFloat:
						float f32 = Bytes.GetFloat32(getRaw(col));
						return f32 + "";
					case OPType.VtDouble:
						double f64 = Bytes.GetFloat64(getRaw(col));
						return f64 + "";
					case OPType.VtDateTime:
						Date date = Bytes.GetDatetime(getRaw(col));
						return date.toString();
					case OPType.VtString:
						String str = Bytes.GetString(getRaw(col));
						return str;
					case OPType.VtBinary:
						return new String(getBytes(colIndex), UTF8);
					case OPType.VtObject:
						Object obj = getObject(colIndex);
						return obj.toString();
					case  OPType.VtStructure:
						StructDecoder structDecoder = getStruct(colIndex);
						if (structDecoder == null)
							return "StructDecoder{}";
						return structDecoder.toString();
					case  OPType.VtMap:
						MapDecoder mapDecoder = getMap(colIndex);
						if (mapDecoder == null)
							return "MapDecoder{}";
						return mapDecoder.toString();
					case  OPType.VtArray:
						ArrayDecoder arrayDecoder = getArray(colIndex);
						if (arrayDecoder == null)
							return "ArrayDecoder{}";
						return arrayDecoder.toString();
					default:
						throw new OPException("getString not support type:" + type);
				}
			}
			return new String(getBytes(colIndex), UTF8);
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	public MapDecoder getMap(int colIndex) throws OPException {
		try {
			if (colIndex >= columns.size())
				throw new OPException("index:" + colIndex + ",Size:"
						+ columns.size());
			OPColumn col = columns.get(colIndex);
			int type = col.getType();
			if (type != OPType.VtMap) {
				throw new OPException("getMap not support type:" + type);
			}
			return MapConvert.decodeMap(getBytes(colIndex));
		}catch (Exception e){
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}
	}

	public ArrayDecoder getArray(int colIndex) throws OPException {
		try {
			if (colIndex >= columns.size())
				throw new OPException("index:" + colIndex + ",Size:"
						+ columns.size());
			OPColumn col = columns.get(colIndex);
			int type = col.getType();
			if (type != OPType.VtArray) {
				throw new OPException("getArray not support type:" + type);
			}
			return ArrayConvert.decodeArray(getBytes(colIndex));
		}catch (Exception e){
			throw new OPException(e.getMessage());
		}
	}

	public StructDecoder getStruct(int colIndex) throws OPException {
		try {
			if (colIndex >= columns.size())
				throw new OPException("index:" + colIndex + ",Size:"
						+ columns.size());
			OPColumn col = columns.get(colIndex);
			int type = col.getType();
			if (type != OPType.VtStructure) {
				throw new OPException("getStruct not support type:" + type);
			}
			return StructConvert.decodeStruct(getBytes(colIndex));
		}catch (Exception e){
			e.printStackTrace();
			throw new OPException("getStruct error:" + e.getMessage());
		}
	}

	// public native byte[] get_column_binary(long dataset, int col);
	public byte[] getBytes(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		if (col.getLength() == 0) {
			int offset = 0;
			int valLen = 0;
			int lenCode = 0;
			int cell = col.getCell();
			for (int i = 0; i <= cell; i++) {
				lenCode = rowVariableBuf[offset];
				offset++;
				switch (lenCode) {
					case mpBin8:
						valLen = Bytes.GetInt8(Arrays.copyOfRange(rowVariableBuf, offset, offset + 1));
						offset += 1;
						break;
					case mpBin16:
						valLen = Bytes.GetInt16(Arrays.copyOfRange(rowVariableBuf, offset, offset + 2));
						offset += 2;
						break;
					case mpBin32:
						valLen = Bytes.GetInt32(Arrays.copyOfRange(rowVariableBuf, offset, offset + 4));
						offset += 4;
						break;
				}
				offset += valLen;
			}
			return Arrays.copyOfRange(rowVariableBuf, offset - valLen, offset);
		} else {
			return Arrays.copyOfRange(rowVariableBuf, col.getOffset(), (col.getOffset() + col.getLength()));
		}
	}

	// public native long get_column_datetime(long dataset, int col);
	public Date getDate(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		if (type != OPType.VtDateTime) {
			switch (type) {
				case OPType.VtNull:
					return null;
				case OPType.VtBool:
					boolean b = Bytes.GetBool(getRaw(col));
					return b ? new Date(1) : new Date(0);
				case OPType.VtInt8:
					short v = Bytes.GetInt8(getRaw(col));
					return new Date(v);
				case OPType.VtInt16:
					v = (short) Bytes.GetInt16(getRaw(col));
					return new Date(v);
				case OPType.VtInt32:
					int i32 = Bytes.GetInt32(getRaw(col));
					return new Date(i32);
				case OPType.VtInt64:
					long i64 = Bytes.GetInt64(getRaw(col));
					return new Date(i64);
				case OPType.VtFloat:
					float f32 = Bytes.GetFloat32(getRaw(col));
					return new Date((long) f32);
				case OPType.VtDouble:
					double f64 = Bytes.GetFloat64(getRaw(col));
					return new Date((long) f64);
				case OPType.VtDateTime:
					Date date = Bytes.GetDatetime(getRaw(col));
					return date;
				case OPType.VtString:
					String str = Bytes.GetString(getRaw(col));
					return new Date(str);
				case OPType.VtObject:
					Object obj = getObject(colIndex);
					return new Date(obj.toString());
				default:
					throw new OPException("getDate not support type:" + type);
			}
		}
		return Bytes.GetDatetime(getRaw(col));
	}

	// public native String get_column_object(long dataset, int col);
	public Object getObject(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		try {
			OPColumn col = columns.get(colIndex);
			int t = col.getType();
			switch (t) {
				case OPType.VtNull:
					return null;
				case OPType.VtBool:
					return Bytes.GetBool(getRaw(col));
				case OPType.VtInt8:
					return Bytes.GetInt8(getRaw(col));
				case OPType.VtInt16:
					return Bytes.GetInt16(getRaw(col));
				case OPType.VtInt32:
					return Bytes.GetInt32(getRaw(col));
				case OPType.VtInt64:
					return Bytes.GetInt64(getRaw(col));
				case OPType.VtFloat:
					return Bytes.GetFloat32(getRaw(col));
				case OPType.VtDouble:
					return Bytes.GetFloat64(getRaw(col));
				case OPType.VtDateTime:
					return Bytes.GetDatetime(getRaw(col));
				case OPType.VtString:
					return new String(getBytes(colIndex), UTF8);
				case OPType.VtBinary:
					return getBytes(colIndex);
			}
			if (t == OPType.VtObject) {
				int endIndex = 0;
				int vLen = 0;
				int cell = col.getCell();
				for (int i = 0; i <= cell; i++) {
					byte vType = rowVariableBuf[endIndex];
					endIndex += 1;
					switch (vType) {
						case mpBin8:
							vLen = Bytes.GetInt8(Arrays.copyOfRange(rowVariableBuf, endIndex, endIndex + 1));
							endIndex += 1;
							break;
						case mpBin16:
							vLen = Bytes.GetInt16(Arrays.copyOfRange(rowVariableBuf, endIndex, endIndex + 2));
							endIndex += 2;
							break;
						case mpBin32:
							vLen = Bytes.GetInt32(Arrays.copyOfRange(rowVariableBuf, endIndex, endIndex + 4));
							endIndex += 4;
							break;
					}
					endIndex += vLen;
				}
				if (vLen > 0) {
					int type = rowVariableBuf[endIndex - vLen];
					byte[] raw = Arrays.copyOfRange(rowVariableBuf, endIndex - vLen + 1, endIndex);
					switch (type) {
						case OPType.VtNull:
							return null;
						case OPType.VtBool:
							return Bytes.GetBool(raw);
						case OPType.VtInt8:
							return Bytes.GetInt8(raw);
						case OPType.VtInt16:
							return Bytes.GetInt16(raw);
						case OPType.VtInt32:
							return Bytes.GetInt32(raw);
						case OPType.VtInt64:
							return Bytes.GetInt64(raw);
						case OPType.VtFloat:
							return Bytes.GetFloat32(raw);
						case OPType.VtDouble:
							return Bytes.GetFloat64(raw);
						case OPType.VtDateTime:
							return Bytes.GetDatetime(raw);
						case OPType.VtString:
							return new String(raw, UTF8);
						case OPType.VtBinary:
						case OPType.VtObject:
							return raw;
					}
				} else {
					return "";
				}
			} else {
				if (t == OPType.VtStructure) {
					return getStruct(colIndex);
				} else if (t == OPType.VtMap) {
					return getMap(colIndex);
				} else if (t == OPType.VtArray) {
					return getArray(colIndex);
				} else if(t == OPType.VtDateTime){
					return getDate(colIndex);
				} else {
					return getMicClassArray(colIndex);
//					throw new OPException("getObject col type:" + col.getType() + " error");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}
		return null;
	}

	public Object getValue(int colIndex) throws OPException {
		if (colIndex >= columns.size())
			throw new OPException("index:" + colIndex + ",Size:" + columns.size());
		OPColumn col = columns.get(colIndex);
		int type = col.getType();
		switch (type) {
			case OPType.VtNull:
				return null;
			case OPType.VtBool:
				return getBoolean(colIndex);
			case OPType.VtInt8:
				return getByte(colIndex);
			case OPType.VtInt16:
				return getShort(colIndex);
			case OPType.VtInt32:
				return getInt(colIndex);
			case OPType.VtInt64:
				return getLong(colIndex);
			case OPType.VtFloat:
				return getFloat(colIndex);
			case OPType.VtDouble:
				return getDouble(colIndex);
			case OPType.VtDateTime:
				return getDate(colIndex);
			case OPType.VtString:
				return getString(colIndex);
			case OPType.VtBinary:
				return getBytes(colIndex);
			case OPType.VtObject:
				return getObject(colIndex);
			default:
				throw new OPException("getValue not support type:" + type);
		}
	}

	protected OPIOBuffer getIo() {
		return io;
	}

	/**
	 * @Author liuyi
	 * @Description 销毁dataset
	 * @Date 2021/5/12 14:32
	 * @Param []
	 * @return void
	 **/
	public void destroy() {
		io = null;
		table = null;
		columns = null;
		isFirst = false;
		rowSize = 0;
		rowCursor = 0;
		rowBuf = null;
		rowVariableBuf = null;
	}

	/**
	 * @Author liuyi
	 * @Description 转换指定类型数组
	 * @Date 2021/5/17 17:07
	 * @Param [col]
	 * @return java.util.List<java.lang.Object>
	 **/
	public List<Object> getMicClassArray(int col) throws OPException {
		OPColumn opColumn = columns.get(col);
		int type = opColumn.getType();
		switch (type){
			case OPType.BOOL_ARRAY:
			case OPType.INT8_ARRAY:
			case OPType.INT16_ARRAY:
			case OPType.INT32_ARRAY:
			case OPType.INT64_ARRAY:
			case OPType.FLOAT_ARRAY:
			case OPType.DOUBLE_ARRAY:
			case OPType.DATETIME_ARRAY:
				return ArrayDecodeUtil.decodeBaseArray(getBytes(col),(byte) type);
			case OPType.STRING_ARRAY:
			case OPType.BINARY_ARRAY:
				return ArrayDecodeUtil.decodeVarArray(getBytes(col),(byte) type);
			default:
				throw new OPException("不存在该数组类型=====type="+type+",col="+col+"name="+columns.get(col).getColumnName());
		}
	}

	/**
	 * @Author liuyi
	 * @Description 转换指定类型数组字符串
	 * @Date 2021/5/20 9:44
	 * @Param [col]
	 * @return java.util.List<java.lang.Object>
	 **/
	public String  getMicClassArrayStr(int col) throws OPException {
		return JSON.toJSONString(getMicClassArray(col));
	}

//	public static synchronized void printLog(String log){
//		String saveFile = "D:\\logs\\jdbc_log.txt";
//		File file = new File(saveFile);
//		FileOutputStream fos = null;
//		OutputStreamWriter osw = null;
//		try {
//			if (!file.exists()) {
//				boolean hasFile = file.createNewFile();
//				if(hasFile){
//					System.out.println("file not exists, create new file");
//				}
//				fos = new FileOutputStream(file);
//			} else {
//				System.out.println("file exists");
//				fos = new FileOutputStream(file, true);
//			}
//			osw = new OutputStreamWriter(fos, "utf-8");
//			osw.write(LocalDateTime.now()+":"+log); //写入内容
//			osw.write("\r\n");  //换行
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {   //关闭流
//			try {
//				if (osw != null) {
//					osw.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			try {
//				if (fos != null) {
//					fos.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
