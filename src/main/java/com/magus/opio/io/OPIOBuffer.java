package com.magus.opio.io;

import com.magus.opio.OPException;

import java.io.IOException;

class mpContainerType {

	int fixCutoff;
	byte bFixMin;
	byte b8;
	byte b16;
	byte b32;

	public mpContainerType(int fixCutoff, byte bFixMin, byte b8, byte b16,
			byte b32) {
		this.fixCutoff = fixCutoff;
		this.bFixMin = bFixMin;
		this.b8 = b8;
		this.b16 = b16;
		this.b32 = b32;
	}
}

public class OPIOBuffer {

	public static String UTF8 = "UTF-8";
	// msgpack
	public static final byte mpPosFixNumMin = 0x00;
	public static final byte mpPosFixNumMax = 0x7f;
	public static final byte mpFixMapMin = (byte) 0x80;
	public static final byte mpFixMapMax = (byte) 0x8f;
	public static final byte mpFixArrayMin = (byte) 0x90;
	public static final byte mpFixArrayMax = (byte) 0x9f;
	public static final byte mpFixStrMin = (byte) 0xa0;
	public static final byte mpFixStrMax = (byte) 0xbf;
	public static final byte mpNil = (byte) 0xc0;
	public static final byte mpNotUsed = (byte) 0xc1;
	public static final byte mpFalse = (byte) 0xc2;
	public static final byte mpTrue = (byte) 0xc3;
	public static final byte mpFloat = (byte) 0xca;
	public static final byte mpDouble = (byte) 0xcb;
	public static final byte mpUint8 = (byte) 0xcc;
	public static final byte mpUint16 = (byte) 0xcd;
	public static final byte mpUint32 = (byte) 0xce;
	public static final byte mpUint64 = (byte) 0xcf;
	public static final byte mpInt8 = (byte) 0xd0;
	public static final byte mpInt16 = (byte) 0xd1;
	public static final byte mpInt32 = (byte) 0xd2;
	public static final byte mpInt64 = (byte) 0xd3;

	// extensions below ;
	public static final byte mpBin8 = (byte) 0xc4;
	public static final byte mpBin16 = (byte) 0xc5;
	public static final byte mpBin32 = (byte) 0xc6;
	public static final byte mpExt8 = (byte) 0xc7;
	public static final byte mpExt16 = (byte) 0xc8;
	public static final byte mpExt32 = (byte) 0xc9;
	public static final byte mpFixExt1 = (byte) 0xd4;
	public static final byte mpFixExt2 = (byte) 0xd5;
	public static final byte mpFixExt4 = (byte) 0xd6;
	public static final byte mpFixExt8 = (byte) 0xd7;
	public static final byte mpFixExt16 = (byte) 0xd8;
	public static final byte mpStr8 = (byte) 0xd9;// new
	public static final byte mpStr16 = (byte) 0xda;
	public static final byte mpStr32 = (byte) 0xdb;
	public static final byte mpArray16 = (byte) 0xdc;
	public static final byte mpArray32 = (byte) 0xdd;
	public static final byte mpMap16 = (byte) 0xde;
	public static final byte mpMap32 = (byte) 0xdf;
	public static final byte mpNegFixNumMin = (byte) 0xe0;
	public static final byte mpNegFixNumMax = (byte) 0xff;

	public static final byte ctString = 0;
	public static final byte ctBinary = 1;
	public static final byte ctArray = 2;
	public static final byte ctMap = 3;

	protected OPInputStream in;
	protected OPOutputStream out;

	public OPIOBuffer() {
	}

	public OPIOBuffer(OPInputStream in, OPOutputStream out) {
		this();
		this.in = in;
		this.out = out;
	}

	public static mpContainerType[] mpContainerTypes;

	static {
		init();
	}

	public static void init() {
		mpContainerType[] types = {
				new mpContainerType(32, mpFixStrMin, mpStr8, mpStr16, mpStr32),
				new mpContainerType(0, (byte) 0, mpBin8, mpBin16, mpBin32),
				new mpContainerType(16, mpFixArrayMin, (byte) 0, mpArray16,
						mpArray32),
				new mpContainerType(16, mpFixMapMin, (byte) 0, mpMap16, mpMap32), };
		mpContainerTypes = types;
	}

	// Flush -
	public void flush(boolean eof) throws OPException {
		try {
			if (eof) {
				out.flush(1);
			} else {
				out.flush(0);
			}
		} catch (IOException e) {
			throw new OPException(e.getMessage());
		}
	}

	// PutBool -
	public void PutBool(boolean v) throws OPException {
		try {
			if (v == true) {
				out.write(new byte[] { 1 });
			} else {
				out.write(new byte[] { 0 });
			}
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	// PutInt8 -
	public synchronized void PutInt8(byte v) throws OPException {
		try {
			out.write(new byte[] { v });
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}

	}

	// PutInt16 -
	public synchronized void PutInt16(short v) throws OPException {

		try {
			out.write(new byte[] { (byte) (v >> 8), (byte) v });
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}

	}

	// PutInt32 -
	public void PutInt32(int v) throws OPException {
		try {
			out.write(new byte[] { (byte) (v >> 24), (byte) (v >> 16),
					(byte) (v >> 8), (byte) v });
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}

	}

	// PutInt64 -
	public void PutInt64(long v) throws OPException {
		try {
			out.write(new byte[] { (byte) (v >> 56), (byte) (v >> 48),
					(byte) (v >> 40), (byte) (v >> 32), (byte) (v >> 24),
					(byte) (v >> 16), (byte) (v >> 8), (byte) v });
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}

	}

	// PutFloat32 -
	public void PutFloat32(float f) throws OPException {
		int v = Float.floatToRawIntBits(f);
		PutInt32(v);
	}

	// PutFloat64 -
	public void PutFloat64(double f) throws OPException {
		long v = Double.doubleToRawLongBits(f);
		PutInt64(v);
	}

	// PutBytes -
	public synchronized void PutBytes(byte[] buf) throws OPException {
		try {
			out.write(buf);
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	public synchronized int GetUint8() throws OPException {
		byte[] buf = GetBytesN(1);
		return buf[0] & 0xff;
	}

	// GetUint8 -
	public synchronized byte GetInt8() throws OPException {
		byte[] buf = GetBytesN(1);
		return buf[0];
	}

	// GetInt16 -
	public synchronized short GetInt16() throws OPException {
		byte[] buf = GetBytesN(2);
		return (short) (((short) buf[0]) << 8 | ((short) buf[1]) & 0xff);
	}

	// GetUint16 -
	public synchronized int GetUint16() throws OPException {
		byte[] buf = GetBytesN(2);
		return (((int) buf[0] & 0xff) << 8) | (((int) buf[1]) & 0xff);
	}

	//	
	// GetInt32 -
	public synchronized int GetInt32() throws OPException {
		byte[] buf = GetBytesN(4);
		return ((((int) buf[0]) << 24) | (((int) buf[1] & 0xff) << 16)
				| (((int) buf[2] & 0xff) << 8) | ((int) buf[3] & 0xff));
	}

	// GetUint32 -
	public synchronized long GetUint32() throws OPException {
		byte[] buf = GetBytesN(4);
		return ((((long) buf[0] & 0xff) << 24) | (((long) buf[1] & 0xff) << 16)
				| (((long) buf[2] & 0xff) << 8) | ((long) buf[3] & 0xff));
	}

	public synchronized long GetInt64() throws OPException {
		byte[] buf = GetBytesN(8);
		long v = ((((long) buf[0]) << 56) | (((long) buf[1] & 0xff) << 48)
				| (((long) buf[2] & 0xff) << 40)
				| (((long) buf[3] & 0xff) << 32)
				| (((long) buf[4] & 0xff) << 24)
				| (((long) buf[5] & 0xff) << 16)
				| ((long) ((long) buf[6] & 0xff) << 8) | ((long) buf[7] & 0xff));
		return v;
	}

	// GetUint64 -
	public synchronized long GetUint64() throws OPException {
		byte[] buf = GetBytesN(8);
		long v = ((((long) buf[0] & 0xff) << 56)
				| (((long) buf[1] & 0xff) << 48)
				| (((long) buf[2] & 0xff) << 40)
				| (((long) buf[3] & 0xff) << 32)
				| (((long) buf[4] & 0xff) << 24)
				| (((long) buf[5] & 0xff) << 16)
				| ((long) ((long) buf[6] & 0xff) << 8) | ((long) buf[7] & 0xff));
		return v;
	}

	// GetFloat32 -
	public synchronized float GetFloat32() throws OPException {
		byte[] buf = GetBytesN(4);
		int v = (((int) buf[0] << 24) | ((int) buf[1] << 16)
				| ((int) buf[2] << 8) | (int) buf[3]);
		return Float.intBitsToFloat(v);
	}

	// GetFloat64 -
	public synchronized double GetFloat64() throws OPException {
		byte[] buf = GetBytesN(8);
		long v = ((((long) buf[0]) << 56) | (((long) buf[1]) << 48)
				| (((long) buf[2]) << 40) | (((long) buf[3]) << 32)
				| (((long) buf[4]) << 24) | (((long) buf[5]) << 16)
				| ((long) ((long) buf[6]) << 8) | ((long) buf[7]));
		return Double.longBitsToDouble(v);

	}

	public synchronized byte[] GetBytesN(int count) throws OPException {
		try {
			byte[] buf = new byte[count];
			int off = 0, len = count;
			while (off < count) {
				int n = in.read(buf, off, len);
				off += n;
				len -= n;
			}
			return buf;
		} catch (Exception e) {
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}
	}

	public synchronized byte Peek() throws OPException {
		try {
			return in.Peek();
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	public synchronized byte[] GetBytes(byte[] buf) throws OPException {
		try {
			int off = 0;
			while (off < buf.length) {
				int n = in.read(buf, off, buf.length);
				off += n;
			}
			return buf;

		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	// EncodeNil -
	public void EncodeNil() throws OPException {
		PutInt8(mpNil);
	}

	// EncodeBool -
	public void EncodeBool(boolean v) throws OPException {
		if (v) {
			PutInt8(mpTrue);
		} else {
			PutInt8(mpFalse);
		}
	}

	// EncodeInt8 - [-128, 127]
	public void EncodeInt8(byte i) throws OPException {
		if (i >= -32) {
			PutInt8(i);
		} else {
			PutInt16((short) (mpInt8 << 8 | (short) i));
		}
	}

	// EncodeInt16 - [-32768, 32767]
	public void EncodeInt16(short i) throws OPException {
		byte[] v = { mpInt16, (byte) (i >> 8), (byte) i };
		PutBytes(v);
	}

	// EncodeInt32 -
	public void EncodeInt32(int i) throws OPException {
		byte[] v = { mpInt32, (byte) (i >> 24), (byte) (i >> 16),
				(byte) (i >> 8), (byte) i };
		PutBytes(v);
	}

	// EncodeInt -
	public void EncodeInt64(long i) throws OPException {
		byte[] v = { mpInt64, (byte) (i >> 56), (byte) (i >> 48),
				(byte) (i >> 40), (byte) (i >> 32), (byte) (i >> 24),
				(byte) (i >> 16), (byte) (i >> 8), (byte) i };
		PutBytes(v);
	}

	// EncodeFloat32 -
	public synchronized void EncodeFloat32(float f) throws OPException {
		PutInt8(mpFloat);
		PutFloat32(f);
	}

	// EncodeFloat64 -
	public void EncodeFloat64(double d) throws OPException {
		PutInt8(mpDouble);
		PutFloat64(d);
	}

	// encodeContainerLen -
	public void encodeContainerLen(int cindex, int l) throws OPException {
		mpContainerType ct = mpContainerTypes[cindex];
		if ((ct.bFixMin & 0xff) > 0 && l < ct.fixCutoff) {
			PutInt8((byte) (ct.bFixMin | (byte) l));
		} else if ((ct.b8 & 0xff) > 0 && l < Byte.MAX_VALUE) {
			PutInt16((short) (ct.b8 << 8 | (short) l));
		} else if (l < Short.MAX_VALUE) {
			byte[] v = { ct.b16, (byte) (l >> 8), (byte) l };
			PutBytes(v);
		} else {
			byte[] v = { ct.b32, (byte) (l >> 24), (byte) (l >> 16),
					(byte) (l >> 8), (byte) l };
			PutBytes(v);
		}
	}

	// EncodeString -
	public void EncodeString(String s) throws OPException {
		// encodeContainerLen(ctString, s.length());
		// PutBytes(s.getBytes());
		try {
			byte[] strBytes = s.getBytes(UTF8);
			encodeContainerLen(ctString, strBytes.length);
			PutBytes(strBytes);
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	// EncodeBytes -
	public void EncodeBytes(byte[] blob) throws OPException {
		encodeContainerLen(ctBinary, blob.length);
		PutBytes(blob);
	}

	public void EncodeValue(Object v) throws OPException {
		if (v instanceof Byte) {
			EncodeInt8((Byte) v);
		} else if (v instanceof Short) {
			EncodeInt16((Short) v);
		} else if (v instanceof Integer) {
			EncodeInt32((Integer) v);
		} else if (v instanceof Long) {
			EncodeInt64((Long) v);
		} else if (v instanceof Float) {
			EncodeFloat32((Float) v);
		} else if (v instanceof Double) {
			EncodeFloat64((Double) v);
		} else if (v instanceof Boolean) {
			EncodeBool((Boolean) v);
		} else if (v instanceof String) {
			EncodeString((String) v);
		} else if (v instanceof byte[]) {
			EncodeBytes((byte[]) v);
		} else {
			throw new OPException("EncodeValue type:" + v.getClass().getName()
					+ " not support");
		}
	}

	public void EncodeMapStart(int l) throws OPException {
		encodeContainerLen(ctMap, l);
	}

	public void EncodeArrayStart(int l) throws OPException {
		encodeContainerLen(ctArray, l);
	}

	public void EncodeExtendLen(int l, byte xtag) throws OPException {
		byte[] v;
		if (l < 256) {
			v = new byte[3];
			v[0] = mpExt8;
			v[1] = (byte) l;
			v[2] = xtag;
		} else if (l < 65536) {
			v = new byte[4];
			v[0] = mpExt16;
			v[1] = (byte) (l >> 8);
			v[2] = (byte) l;
			v[3] = xtag;
		} else {
			v = new byte[6];
			v[0] = mpExt32;
			v[1] = (byte) (l >> 24);
			v[2] = (byte) (l >> 16);
			v[3] = (byte) (l >> 8);
			v[4] = (byte) l;
			v[5] = xtag;
		}
		PutBytes(v);
	}

	public int DecodeArrayStart() throws OPException {
		byte type = GetInt8();
		int size = 0;
		switch (type) {
		case mpArray16:
			size = GetUint16();
			break;
		case mpArray32:
			size = (int) GetUint32();
			break;
		case mpNil:
			size = 0xffffffff;
			break;
		default:
			if ((type & 0xf0) == (mpFixArrayMin & 0xff)) {
				size = type & 15;
			} else {
				throw new OPException("DecodeArrayStart error type:" + type);
			}
		}
		return size;
	}

	public byte[] DecodeBytes() throws OPException {
		byte type = GetInt8();
		int size = 0;
		switch (type) {
		case mpBin8:
			size = GetUint8();
			break;
		case mpBin16:
			size = GetUint16();
			break;
		case mpBin32:
			size = (int) GetUint32();
			break;
		case mpFixExt1:
		case mpFixExt2:
		case mpFixExt4:
		case mpFixExt8:
		case mpExt8:
		case mpExt16:
		case mpExt32:
			size = decodeExtendLen(type);
			GetInt8(); // for openPlant
			break;
		case mpNil:
			size = 0;
			break;
		default:
			throw new OPException("DecodeBytes type error " + (type & 0xff));
		}
		return GetBytesN(size);
	}

	public OPExtendStart DecodeExtendStart() throws OPException {
		int size = decodeExtendLen(GetInt8());
		byte xtag = GetInt8();
		return new OPExtendStart(xtag, size);
	}

	public OPExtendValue DecodeExtend() throws OPException {
		byte type = GetInt8();
		int size = decodeExtendLen(type);
		byte xtag = GetInt8();
		if (size > 0) {
			return new OPExtendValue(xtag, GetBytesN(size));
		}
		return null;
	}

	public int DecodeInt8() throws OPException {
		byte type = GetInt8();
		int value = 0;
		switch (type) {
		case mpUint8:
			value = GetUint8();
			break;
		case mpInt8:
			value = GetInt8();
			break;
		default:
			if ((type & 0xff) <= 127) {
				value = type;
			} else {
				throw new OPException("DecodeInt8 type error:" + type);
			}
		}
		return value;
	}

	public int DecodeInt16() throws OPException {
		byte type = GetInt8();
		int value = 0;
		switch (type) {
		case mpUint16:
		case mpInt16:
			value = GetInt16();
			break;
		default:
			throw new OPException("DecodeInt16 type error:" + type);
		}
		return value;
	}

	public int DecodeInt32() throws OPException {
		byte type = GetInt8();
		int value = 0;
		switch (type) {
		case mpUint32:
		case mpInt32:
			value = GetInt32();
			break;
		default:
			throw new OPException("DecodeInt32 type error:" + type);
		}
		return value;
	}

	public long DecodeInt64() throws OPException {
		byte type = GetInt8();
		long value = 0;
		switch (type) {
		case mpUint64:
		case mpInt64:
			value = GetInt64();
			break;
		default:
			throw new OPException("DecodeInt64 type error:" + type);
		}
		return value;
	}

	public long DecodeInt() throws OPException {
		byte type = GetInt8();
		long value = 0;
		switch (type) {
		case mpUint8:
			value = GetUint8();
			break;
		case mpInt8:
			value = GetInt8();
			break;
		case mpUint16:
			value = GetUint16();
			break;
		case mpInt16:
			value = GetInt16();
			break;
		case mpUint32:
			value = GetUint32();
			break;
		case mpInt32:
			value = GetInt32();
			break;
		case mpUint64:
			value = GetUint64();
			break;
		case mpInt64:
			value = GetInt64();
			break;
		default:
			if ((type & 0xff) <= 127 || (type & 0xff) >= mpNegFixNumMin) {
				value = type;
			} else {
				throw new OPException("DecodeInt type error:" + type);
			}
		}
		return value;
	}

	public double DecodeFloat64() throws OPException {
		byte type = GetInt8();
		double value = 0;
		switch (type) {
		case mpFloat:
			value = GetFloat32();
			break;
		case mpDouble:
			value = GetFloat64();
			break;
		default:
			throw new OPException("DecodeFloat64 type error:" + type);
		}
		return value;
	}

	public String DecodeString() throws OPException {
		try {
			byte type = GetInt8();
			int size = 0;
			switch (type) {
			case mpStr8:
				size = GetUint8();
				break;
			case mpStr16:
				size = GetUint16();
				break;
			case mpStr32:
				size = (int) GetUint32();
				break;
			case mpNil:
				size = 0;
				break;
			default:
				if ((type & 0xe0) == (mpFixStrMin & 0xff)) {
					size = type & 31;
				} else {
					throw new OPException("DecodeString type error:" + type);
				}
			}
			return new String(GetBytesN(size), UTF8);
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	public Object DecodeValue() throws OPException {
		try {
			byte type = GetInt8();
			Object v = null;
			switch (type) {
			case mpNil:
			case mpNotUsed:
				v = null;
				break;
			case mpFalse:
				v = false;
				break;
			case mpTrue:
				v = true;
				break;
			case mpUint8:
				v = GetUint8();
				break;
			case mpInt8:
				v = GetInt8();
				break;
			case mpUint16:
				v = GetUint16();
				break;
			case mpInt16:
				v = GetInt16();
				break;
			case mpUint32:
				v = GetUint32();
				break;
			case mpInt32:
				v = GetInt32();
				break;
			case mpUint64:
				v = GetUint64();
				break;
			case mpInt64:
				v = GetInt64();
				break;
			case mpFloat:
				v = GetFloat32();
				break;
			case mpDouble:
				v = GetFloat64();
				break;
			case mpStr8:
				byte[] blob = new byte[GetUint8()];
				GetBytes(blob);
				v = new String(blob, UTF8);
				break;
			case mpStr16:
				blob = new byte[GetUint16()];
				GetBytes(blob);
				v = new String(blob, UTF8);
				break;
			case mpStr32:
				blob = new byte[(int) GetUint32()];
				GetBytes(blob);
				v = new String(blob, UTF8);
				break;
			case mpFixStrMin:
			case (byte) 0xb0:
				blob = new byte[(int) (type & 31)];
				GetBytes(blob);
				v = new String(blob, UTF8);
				break;
			default:
				switch (type & 0xf0) {
				case mpFixStrMin & 0xff:
				case 0xb0:
					blob = new byte[(int) (type & 31)];
					GetBytes(blob);
					v = new String(blob, UTF8);
					break;
				default:
					v = type;
				}
			}
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}
	}

	public int DecodeMapStart() throws OPException {
		byte type = GetInt8();
		int size = 0;
		switch (type) {
		case mpMap16:
			size = GetUint16();
			break;
		case mpMap32:
			size = (int) GetUint32();
			break;
		case mpNil:
			size = 0xffffffff;
			break;
		default:
			if ((type & 0xf0) == (mpFixMapMin & 0xff)) {
				size = type & 15;
			} else {
				throw new OPException("DecodeValue type error:" + (type & 0xff));
			}
		}

		return size;
	}

	public int decodeExtendLen(byte typ) throws OPException {
		try {
			switch (typ) {
			case mpFixExt1:
				return 1;
			case mpFixExt2:
				return 2;
			case mpFixExt4:
				return 4;
			case mpFixExt8:
				return 8;
			case mpFixExt16:
				return 16;
			case mpExt8:
				return GetUint8();
			case mpExt16:
				return GetUint16();
			case mpExt32:
				return (int) GetUint32();
			default:
				throw new OPException("DecodeExtend error type=" + typ);
			}
		} catch (Exception e) {
			throw new OPException("decodeExtendLen error " + e.getMessage());
		}
	}
}
