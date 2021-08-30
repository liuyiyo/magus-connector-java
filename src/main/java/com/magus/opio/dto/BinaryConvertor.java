package com.magus.opio.dto;

import java.nio.ByteBuffer;

/**
 * @author liwei.tang@magustek.com
 * @since 2015年5月26日
 * 
 *        8种基础类型到byte[]的转换<br>
 *        byte[] toBytes(boolean b);<br>
 *        byte[] toBytes(byte b);<br>
 *        byte[] toBytes(short s);<br>
 *        byte[] toBytes(int i);<br>
 *        byte[] toBytes(long l);<br>
 *        byte[] toBytes(float f);<br>
 *        byte[] toBytes(double d);<br>
 *        byte[] toBytes(String hexString);<br>
 * <br>
 *        byte[]到8种基础类型的转换<br>
 *        boolean toBoolean(byte[] bytes);<br>
 *        byte toInt8(byte[] bytes);<br>
 *        short toInt16(byte[] bytes);<br>
 *        int toInt32(byte[] bytes);<br>
 *        long toInt64(byte[] bytes);<br>
 *        float toFloat(byte[] bytes);<br>
 *        double toDouble(byte[] bytes);<br>
 *        String toHexString(byte[] bytes);<br>
 */
public class BinaryConvertor {

	// Boolean类型对应的整数值
	public static final int FALSE = 0;
	public static final int TRUE = 1;

	// 字符串形式的true, false;
	public static final String TRUE_STR = String.valueOf(TRUE);
	public static final String FALSE_STR = String.valueOf(FALSE);

	// 二进制到字符串的转换
	public static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();
	public static final String HEX_STRING_PREFIX = "0x";
	public static final String ZERO = "0";

	protected byte[] toBytes(boolean b) {
		return new byte[]{(byte) (b ? TRUE : FALSE)};
	}
	
	protected byte[] toBytes(byte b) {
		return new byte[] { b };
	}

	protected byte[] toBytes(short s) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(s);
		return buffer.array();
	}

	protected byte[] toBytes(int i) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(i);
		return buffer.array();
	}

	protected byte[] toBytes(long l) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(l);
		return buffer.array();
	}

	protected byte[] toBytes(float f) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putFloat(f);
		return buffer.array();
	}

	protected byte[] toBytes(double d) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putDouble(d);
		return buffer.array();
	}
	
	protected boolean toBoolean(byte[] bytes) {
		checkLength(bytes, 1);
		int byt=bytes[0];
		switch(byt){
		case TRUE:return true;
		case FALSE:return false;
		default: throw new IllegalArgumentException("hexBinary for boolean value must be 0 or 1.");
		}
	}
	
	protected byte parseInt8(byte[] bytes) {
		checkLength(bytes, 1);
		return bytes[0];
	}

	protected short parseInt16(byte[] bytes) {
		checkLength(bytes, 2);
		return ByteBuffer.wrap(bytes).getShort();
	}

	protected int parseInt32(byte[] bytes) {
		checkLength(bytes, 4);
		return ByteBuffer.wrap(bytes).getInt();
	}

	protected long parseInt64(byte[] bytes) {
		checkLength(bytes, 8);
		return ByteBuffer.wrap(bytes).getLong();
	}

	protected float parseFloat(byte[] bytes) {
		return (float)parseDouble(bytes);
	}

	protected double parseDouble(byte[] bytes) {
		if(bytes.length!=1 && bytes.length!=2 && bytes.length!=4 && bytes.length!=8){
			throw new IllegalArgumentException("hexBinary length " + bytes.length + " can't convert to Double.");
		}
		double v;
		int length = bytes.length;
		switch (length) {
		case 1:
			v = bytes[0];
			break;
		case 2:
			v = (bytes[0] << 8) + (bytes[1] & 0xFF);// 返回高位带符号的short
			break;
		case 4:
            int valueInt = (((int)bytes[0] & 0xff) << 24) |
	            (((int)bytes[1] & 0xff) << 16) |
	            (((int)bytes[2] & 0xff) <<  8) |
	            (((int)bytes[3] & 0xff)      );
	            v = Float.intBitsToFloat(valueInt);
			break;
		case 8:
			long valueLong = ((((long)bytes[0]       ) << 56) |
	                (((long)bytes[1] & 0xff) << 48) |
	                (((long)bytes[2] & 0xff) << 40) |
	                (((long)bytes[3] & 0xff) << 32) |
	                (((long)bytes[4] & 0xff) << 24) |
	                (((long)bytes[5] & 0xff) << 16) |
	                (((long)bytes[6] & 0xff) <<  8) |
	                (((long)bytes[7] & 0xff)      ));
			v = Double.longBitsToDouble(valueLong);
			break;
		default:
			throw new RuntimeException("Unsupport bytes[" + length + "] to number");
		}
		return v;
	}

	protected String toHexString(byte[] bytes) {
		//TODO 字符串类型是否checkLength?
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		sb.append(HEX_STRING_PREFIX);
		for (byte b : bytes) {
			sb.append(HEX_CODE[(b >> 4) & 0xF]);
			sb.append(HEX_CODE[(b & 0xF)]);
		}
		return sb.toString();
	}

	protected byte[] toBytes(String hexString) {
		byte[] bytes = hexString.getBytes();
		// 检查是否HexString
		if (bytes[0] == '0' && bytes[1] == 'x') {
			
			final int len = hexString.length()-HEX_STRING_PREFIX.length();

			// "111" is not a valid hex encoding.
			if (len % 2 != 0) {
				throw new IllegalArgumentException("hexBinary needs to be even-length: " + hexString);
			}

			byte[] out = new byte[len / 2];

			for (int i = 2; i <= len; i += 2) {
				int h = hexToBin(hexString.charAt(i));
				int l = hexToBin(hexString.charAt(i + 1));
				if (h == -1 || l == -1) {
					throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
				}

				out[i / 2 - 1] = (byte) (h * 16 + l);
			}

			return out;
		} else {
			throw new IllegalArgumentException("hexString needs start with: " + HEX_STRING_PREFIX);
		}
	}

	protected static int hexToBin(char ch) {
		if ('0' <= ch && ch <= '9') {
			return ch - '0';
		}
		if ('A' <= ch && ch <= 'F') {
			return ch - 'A' + 10;
		}
		if ('a' <= ch && ch <= 'f') {
			return ch - 'a' + 10;
		}
		return -1;
	}

	//检查要被转换为基础类型的二进制数组长度是否恰好是该类型的长度，如果不是则抛出异常
	private void checkLength(byte[] bytes, int expectLength){
		if(bytes.length!=expectLength){
			throw new IllegalArgumentException("hexBinary expectLength length " + expectLength + " but actually "+bytes.length);
		}
	}
}
