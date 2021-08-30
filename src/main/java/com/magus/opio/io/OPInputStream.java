package com.magus.opio.io;

import com.magus.opio.OPException;
import com.magus.opio.OPConst;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

import java.io.IOException;
import java.io.InputStream;

/**
 * 本类实现了openPlant网络数据流的缓存及封装. 网络包组成：是否结束[1字节] + 数据长度[3字节] + 数据
 * 
 * @author yang
 * @since 3.0
 */
public class OPInputStream {

	public static LZ4Factory factory = LZ4Factory.fastestInstance();
	public static LZ4SafeDecompressor decompressor = factory.safeDecompressor();
	public static LZ4FrameInputStream compressedOutput = new LZ4FrameInputStream();
	public static int maxBufLen = 4 << 20;
	/**
	 * 底层输入流.
	 */
	protected InputStream in;

	/**
	 * 是否是最后的一个数据包.
	 */
	protected int eof;

	/**
	 * 内部数据缓存区.
	 */
	protected byte[] buffer;

	/**
	 * 内部数据缓存区当前读取位置.
	 */
	protected int pos;

	/**
	 * 内部数据缓存区当前可读字节数.
	 */
	protected int count;

	public OPInputStream(InputStream in) {
		this(in, 64 << 10);
	}

	public OPInputStream(InputStream in, int i) {
		this.in = in;
		eof = 0;
		pos = 0;
		count = 0;
		buffer = new byte[i];
	}

	public int available() throws IOException {
		return (count - pos);
	}

	public void close() throws IOException {
		in.close();
	}

	protected byte[] readN(int count) throws IOException {
		byte[] buf = new byte[count];
		int off = 0;
		while (off < count) {
			int n = in.read(buf, off, (count - off));
			if (n < 0) {
				throw new IOException("");
			}
			off += n;
		}
		return buf;
	}

	protected byte[] readHead() throws IOException {
		return readN(4);
	}

	private synchronized int fill() throws OPException {
		try {
			byte[] head = readHead();
			int eof = (head[0] & 0xff);
			byte mod = (byte) (head[1] & 0xff);
			int dataSize = (head[2] & 0xff) << 8 | (head[3] & 0xff);
			byte[] buf;
			try {
				buf = readN(dataSize);
			} catch (IOException ex) {
				return -1;
			}
			switch (mod) {
			case OPConst.ZIP_MODEL_Uncompressed:
				buffer = buf;
				break;
			case OPConst.ZIP_MODEL_Frame:
				buffer = uncompressFrame(buf);
				break;
			case OPConst.ZIP_MODEL_Block:
				buffer = uncompressBlock(buf);
				break;
			default:
				throw new OPException("io mod error" + mod);
			}
			this.eof = eof;
			this.count = buffer.length;
			this.pos = 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}
		return 0;
	}

	public int read() throws OPException {
		if (pos >= count) {
			if (fill() < 0)
				return -1; // end of the stream
		}
		return (buffer[pos++] & 0xff);
	}

	public int read(byte[] b) throws OPException {
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws OPException {
		int avail = count - pos;
		if (avail <= 0) {
			if (fill() < 0)
				return -1; // end of the stream
			avail = count - pos;
		}
		if (len > avail)
			len = avail;
		// copy to b
		System.arraycopy(buffer, pos, b, off, len);
		pos += len;
		return len;
	}

	public synchronized byte Peek() throws Exception {
		if (pos >= count) {
			if (fill() < 0)
				return -1; // end of the stream
		}
		return buffer[pos];
	}

	private String print(byte[] bytes) {
		StringBuffer sb = new StringBuffer("[");
		for (byte b : bytes) {
			sb.append((b & 0xff) + " ");
		}
		sb.append("]");
		return sb.toString();
	}

	public synchronized byte[] uncompressBlock(byte[] compressorByte) {
		return decompressor.decompress(compressorByte, maxBufLen);
	}

	public synchronized static byte[] uncompressFrame(byte[] compressorByte)
			throws IOException {
		return compressedOutput.uncompressFrame(compressorByte);
	}
}
