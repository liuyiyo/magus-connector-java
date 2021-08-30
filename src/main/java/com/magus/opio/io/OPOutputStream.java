package com.magus.opio.io;

/**
 * All right reserved.
 */

import com.magus.opio.OPConst;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 本类实现了网络数据流的缓存及封装. 网络包组成：是否结束[1字节] + 数据长度[3字节] + 数据
 * 
 */
public class OPOutputStream extends OutputStream {

	public static LZ4Factory factory = LZ4Factory.fastestInstance();
	public static LZ4Compressor compressor = factory.fastCompressor();
	public static LZ4FrameOutputStream compressorFrame = new LZ4FrameOutputStream();

	private byte compressMod;

	protected OutputStream out;

	protected byte buf[];

	protected int count;

	public OPOutputStream(OutputStream out) {
		this(out, (64 << 10) - 272); // maxCompressedLength(64k-272) = 65535
	}

	public OPOutputStream(OutputStream out, int size) {
		this.out = out;
		if (size <= 0) {
			throw new IllegalArgumentException("Buffer size <= 0");
		}
		buf = new byte[size];
	}

	public void close() throws IOException {
		compressorFrame.close();
		out.close();
	}

	private String print(byte[] bytes) {
		StringBuffer sb = new StringBuffer("[");
		for (byte b : bytes) {
			sb.append((b & 0xff) + " ");
		}
		sb.append("]");
		return sb.toString();
	}

	private void sendBuffer(int eof, byte[] data) throws IOException {
		switch (compressMod) {
		case OPConst.ZIP_MODEL_Uncompressed:
			byte[] head = { (byte) eof, 0, (byte) ((data.length >> 8) & 0xFF),
					(byte) ((data.length) & 0xFF) };
			out.write(head, 0, 4);
			out.write(data, 0, data.length);
			break;
		case OPConst.ZIP_MODEL_Frame:
			byte[] dst = compressFrame(data);
			head = new byte[] { (byte) eof, compressMod,
					(byte) ((dst.length >> 8) & 0xFF),
					(byte) ((dst.length) & 0xFF) };
			out.write(head, 0, 4);
			out.write(dst, 0, dst.length);
			break;
		case OPConst.ZIP_MODEL_Block:
			dst = compressBlock(data);
			head = new byte[] { (byte) eof, compressMod,
					(byte) ((dst.length >> 8) & 0xFF),
					(byte) ((dst.length) & 0xFF) };
			out.write(head, 0, 4);
			out.write(dst, 0, dst.length);
			break;
		}
	}

	public void flush(int eof) throws IOException {
		if (count > 0) {
			sendBuffer(eof, Arrays.copyOfRange(buf, 0, count));
			count = 0;
		}
	}

	public synchronized void write(int b) throws IOException {
		if (count >= buf.length) {
			flush(0);
		}
		buf[count++] = (byte) b;
	}

	public synchronized void write(byte b[], int off, int len)
			throws IOException {
		int max_len = buf.length;
		int avail = max_len - count;
		if (len > avail) {
			if (count > 0) {
				System.arraycopy(b, off, buf, count, avail);
				count += avail;
				off += avail;
				len -= avail;
				flush(0);
			}
			while (len > max_len) {
				sendBuffer(0, Arrays.copyOfRange(b, off, max_len));
				off += max_len;
				len -= max_len;
			}
		}
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}

	public synchronized void flush() throws IOException {
		flush(1);
		out.flush();
	}

	public static byte[] compressBlock(byte[] srcByte) {
		return compressor.compress(srcByte);
	}

	public synchronized static byte[] compressFrame(byte[] srcByte)
			throws IOException {
		return compressorFrame.compressFrame(srcByte);
	}

	public void setCompressMod(byte compressMod) {
		this.compressMod = compressMod;
	}

}
