package com.magus.opio.utils.array;

import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.Common;
import com.magus.opio.utils.UtilsBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.magus.opio.io.OPIOBuffer.*;

public class ArrayConvert {
	private ArrayConvert() {
	}

	public static <E> byte[] encodeArray(List<E> value) throws OPException {
		if (value == null || value.size() == 0)
			return Bytes.MakeEmptyBinary();

		byte eleType = Common.getObjType(value.get(0));
		if (eleType == -1)
			throw new OPException("encodeArray type:" + eleType + ",not support");
		// fixed len
		if (eleType < OPType.VtString) {
			return putFixedArray(eleType, value);
		} else {
			return putVarArray(eleType, value);
		}
	}

	private static byte[] putFixedArray(byte eleType, List<?> list) {
		int fixedLen = Common.fixedTypeLen.get(eleType);
		byte[] dataBuf = new byte[list.size() * fixedLen + 1];
		int offset = 0;
		dataBuf[offset] = eleType;
		// if (eleType == OPType.VtInt32) {
		// //和go打包规则保持一致
		// dataBuf[offset] = OPType.VtInt64;
		// }
		offset++;
		// System.arraycopy是深拷贝，可以复用同一个buf
		byte[] buf = new byte[fixedLen];
		for (Object ele : list) {
			Common.putFixedVal(ele, eleType, buf);
			System.arraycopy(buf, 0, dataBuf, offset, fixedLen);
			offset += fixedLen;
		}

		byte[] b = Bytes.PutBinary(dataBuf);
		return Arrays.copyOfRange(b, 1, b.length);
	}

	private static byte[] putVarArray(byte eleType, List<?> list) throws OPException {

		UtilsBuffer utilsBuffer = new UtilsBuffer();
		utilsBuffer.put(new byte[] { eleType });
		for (Object ele : list) {
			utilsBuffer.put(Common.putVarVal(ele, eleType));
		}

		byte[] b = Bytes.PutBinary(utilsBuffer.bytes());
		return Arrays.copyOfRange(b, 1, b.length);
	}

	public static ArrayDecoder decodeArray(byte[] src) throws OPException {

        if (Bytes.IsEmptyBinary(src))
            return null;

        int srcLen = src.length;

        ArrayDecoder arrayDecoder = new ArrayDecoder();
        arrayDecoder.setData(src);

        int offset = arrayDecoder.getHeadLen();
        byte eleType = src[offset];
        offset++;
        arrayDecoder.setEleType(eleType);
        int bodyLen = arrayDecoder.getBodyLen();
        if (Common.minMapBodyLen == bodyLen) {
            return arrayDecoder;
        }
        int dataStart = offset;
        byte fixedLen = Common.fixedTypeLen.getOrDefault(eleType, (byte) -1);
        if (fixedLen != -1) {
            arrayDecoder.setIter(new FixedIterImpl(dataStart, fixedLen, srcLen));
        } else {
            List<Integer> posList = new ArrayList<Integer>();
            int eleHeadLen = 0, eleBodyLen = 0;
            while (offset != srcLen) {
                eleHeadLen = 0;
                eleBodyLen = 0;
                byte flagByte = src[offset];
                offset++;
                eleHeadLen++;
                switch (flagByte) {
                    case mpBin8:
                        eleBodyLen = src[offset]&0xff;
                        offset++;
                        eleHeadLen++;
                        break;
                    case mpBin16:
                        eleBodyLen = Bytes.GetInt16(Arrays.copyOfRange(src, offset, offset + 2));
                        offset += 2;
                        eleHeadLen += 2;
                        break;
                    case mpBin32:
                        eleBodyLen = Bytes.GetInt32(Arrays.copyOfRange(src, offset, offset + 4));
                        offset += 4;
                        eleHeadLen += 4;
                        break;
                    default:
                        break;
                }
                posList.add(eleHeadLen + eleBodyLen);
                offset += eleBodyLen;
            }
            arrayDecoder.setIter(new VarIterImpl(posList.stream().mapToInt(Integer::valueOf).toArray(), dataStart, srcLen));
        }
        return arrayDecoder;
    }

	public static void main(String[] args) throws Exception {
		// byte[] dataBuf = new byte[10];
		// byte[] buf = new byte[1];
		// for (int i = 0; i < 10; i++) {
		// buf[0] = (byte) i;
		// System.arraycopy(buf, 0, dataBuf, i, 1);
		// }
		// System.out.println(Common.print(dataBuf));

		List<Integer> list = Arrays.asList(new Integer[] { 11, 12, 13, 14, 15, 81, 82, 83, 84, 85 });
		byte[] res = encodeArray(list);
		// List<String> list = Arrays.asList(new String[]{
		// "11", "12", "13", "14", "15",
		// "81", "82", "83", "84", "85"
		// });
		// byte[] res = encodeArray(list);
		// System.out.println(res.length);
		// System.out.println(Common.print(res));

		ArrayDecoder arrayDecoder = decodeArray(res);
		Iter iter = arrayDecoder.getIter();
		int count = 0;
		for (iter.seekToFirst(); iter.valid(); iter.next()) {
			System.out.println("ele value:" + arrayDecoder.get());
			count++;
		}
		System.out.println("count:" + count);

		// Map<String, String> m1 = new HashMap<String, String>() {
		// {
		// put("a", "aa");
		// put("b", "bb");
		// put("c", "cc");
		// }
		// };
		// Map<String, String> m2 = new HashMap<String, String>() {
		// {
		// put("d", "dd");
		// put("e", "ee");
		// put("f", "ff");
		// }
		// };
		// List<Map<String, String>> listMapValue = new ArrayList<Map<String,
		// String>>();
		// listMapValue.add(m1);
		// listMapValue.add(m2);
		// byte[] res = encodeArray(listMapValue);
		// System.out.println(res.length);
		// System.out.println(Common.print(res));
	}
}
