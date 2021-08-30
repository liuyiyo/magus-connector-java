package com.magus.opio.dto;

import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.io.OPExtendStart;
import com.magus.opio.io.OPIOBuffer;

public class OPIndexs {
	byte type;
	String[] key_str;
	int[] key_i32;
	long[] key_i64;

	public OPIndexs() {

	}

	// read -
	public void read(OPIOBuffer io) throws OPException {
		OPExtendStart opExtendStart = io.DecodeExtendStart();
		int size = io.DecodeArrayStart();
		switch (opExtendStart.xtag) {
		case OPType.STRING_ARRAY:
			key_str = new String[size];
			for (int i = 0; i < size; i++) {
				key_str[i] = io.DecodeString();
			}
			break;

		case OPType.INT32_ARRAY:
			key_i32 = new int[size];
			for (int i = 0; i < size; i++) {
				key_i32[i] = io.DecodeInt32();
			}
			break;

		case OPType.INT64_ARRAY:
			key_i64 = new long[size];
			for (int i = 0; i < size; i++) {
				key_i64[i] = io.DecodeInt64();
			}
			break;

		default:
			throw new OPException("OPIndexs read error type:" + opExtendStart.xtag);
		}
	}

	private int mpStringExtra(int l) {
		if(l < 32) {
			return 1;
		} else if(l < 256) {
			return 2;
		} else if(l < 65536) {
			return 3;
		} else {
			return 5;
		}
	}

	public void write(OPIOBuffer opioBuffer) throws OPException {

		switch (type) {
		case OPType.STRING_ARRAY:
			int totalLen = 0;
			for (String s : key_str) {
				totalLen += mpStringExtra(s.length()) + s.length();
			}
			opioBuffer.EncodeExtendLen(totalLen, type);
			opioBuffer.EncodeArrayStart(key_str.length);
			for (String s : key_str) {
				opioBuffer.EncodeString(s);
			}
			break;

		case OPType.INT32_ARRAY:
			totalLen = key_i32.length*5;
			opioBuffer.EncodeExtendLen(totalLen, type);
			opioBuffer.EncodeArrayStart(key_i32.length);
			for (int i32 : key_i32) {
				opioBuffer.EncodeInt32(i32);
			}
			break;

		case OPType.INT64_ARRAY:
			totalLen = key_i64.length*9;
			opioBuffer.EncodeExtendLen(totalLen, type);
			opioBuffer.EncodeArrayStart(key_i64.length);
			for (long i64 : key_i64) {
				opioBuffer.EncodeInt64(i64);
			}
			break;

		default:
			throw new OPException("Indexs write type error" + type);
		}

	}
}
