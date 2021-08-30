package com.magus.opio.io;

import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;

public class LZ4FrameInputStream {

	static final String PREMATURE_EOS = "Stream ended prematurely";
	static final String NOT_SUPPORTED = "Stream unsupported";
	static final String BLOCK_HASH_MISMATCH = "Block checksum mismatch";
	static final String DESCRIPTOR_HASH_MISMATCH = "Stream frame descriptor corrupted";
	static final int MAGIC_SKIPPABLE_BASE = 0x184D2A50;

	private final LZ4SafeDecompressor decompressor;
	private final XXHash32 checksum;
	private final byte[] headerArray = new byte[LZ4FrameOutputStream.LZ4_MAX_HEADER_LENGTH];
	private final ByteBuffer headerBuffer = ByteBuffer.wrap(headerArray).order(
			ByteOrder.LITTLE_ENDIAN);
	private byte[] compressedBuffer;
	private ByteBuffer buffer = null;
	private byte[] rawBuffer = null;
	private int maxBlockSize = -1;
	private long expectedContentSize = -1L;
	private long totalContentSize = 0L;

	private ByteArrayInputStream in;
	private LZ4FrameOutputStream.FrameInfo frameInfo = null;

	private byte[] dst = new byte[128 << 10];

	public LZ4FrameInputStream() {
		this.decompressor = LZ4Factory.fastestInstance().safeDecompressor();
		this.checksum = XXHashFactory.fastestInstance().hash32();
	}

	public synchronized byte[] uncompressFrame(byte[] compressorByte)
			throws IOException {
		this.in = new ByteArrayInputStream(compressorByte);
		int len = read(dst, 0, dst.length);
		in.close();
		return Arrays.copyOfRange(dst, 0, len);

	}

	private boolean nextFrameInfo() throws IOException {
		while (true) {
			int size = 0;
			do {
				final int mySize = in.read(readNumberBuff.array(), size,
						LZ4FrameOutputStream.INTEGER_BYTES - size);
				if (mySize < 0) {
					return false;
				}
				size += mySize;
			} while (size < LZ4FrameOutputStream.INTEGER_BYTES);
			final int magic = readNumberBuff.getInt(0);
			if (magic == LZ4FrameOutputStream.MAGIC) {
				readHeader();
				return true;
			} else if ((magic >>> 4) == (MAGIC_SKIPPABLE_BASE >>> 4)) {
				skippableFrame();
			} else {
				throw new IOException(NOT_SUPPORTED);
			}
		}
	}

	private void skippableFrame() throws IOException {
		int skipSize = readInt(in);
		final byte[] skipBuffer = new byte[1 << 10];
		while (skipSize > 0) {
			final int mySize = in.read(skipBuffer, 0, Math.min(skipSize,
					skipBuffer.length));
			if (mySize < 0) {
				throw new IOException(PREMATURE_EOS);
			}
			skipSize -= mySize;
		}
	}

	private void readHeader() throws IOException {
		headerBuffer.rewind();

		final int flgRead = in.read();
		if (flgRead < 0) {
			throw new IOException(PREMATURE_EOS);
		}
		final int bdRead = in.read();
		if (bdRead < 0) {
			throw new IOException(PREMATURE_EOS);
		}

		final byte flgByte = (byte) (flgRead & 0xFF);
		final LZ4FrameOutputStream.FLG flg = LZ4FrameOutputStream.FLG
				.fromByte(flgByte);
		headerBuffer.put(flgByte);
		final byte bdByte = (byte) (bdRead & 0xFF);
		final LZ4FrameOutputStream.BD bd = LZ4FrameOutputStream.BD
				.fromByte(bdByte);
		headerBuffer.put(bdByte);

		this.frameInfo = new LZ4FrameOutputStream.FrameInfo(flg, bd);

		if (flg.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_SIZE)) {
			expectedContentSize = readLong(in);
			headerBuffer.putLong(expectedContentSize);
		}
		totalContentSize = 0L;

		// check stream descriptor hash
		final byte hash = (byte) ((checksum.hash(headerArray, 0, headerBuffer
				.position(), 0) >> 8) & 0xFF);
		final int expectedHash = in.read();
		if (expectedHash < 0) {
			throw new IOException(PREMATURE_EOS);
		}

		if (hash != (byte) (expectedHash & 0xFF)) {
			throw new IOException(DESCRIPTOR_HASH_MISMATCH);
		}

		maxBlockSize = frameInfo.getBD().getBlockMaximumSize();
		compressedBuffer = new byte[maxBlockSize]; // Reused during different
		// compressions
		rawBuffer = new byte[maxBlockSize];
		buffer = ByteBuffer.wrap(rawBuffer);
		buffer.limit(0);
	}

	private final ByteBuffer readNumberBuff = ByteBuffer.allocate(
			LZ4FrameOutputStream.LONG_BYTES).order(ByteOrder.LITTLE_ENDIAN);

	private long readLong(InputStream stream) throws IOException {
		int offset = 0;
		do {
			final int mySize = stream.read(readNumberBuff.array(), offset,
					LZ4FrameOutputStream.LONG_BYTES - offset);
			if (mySize < 0) {
				throw new IOException(PREMATURE_EOS);
			}
			offset += mySize;
		} while (offset < LZ4FrameOutputStream.LONG_BYTES);
		return readNumberBuff.getLong(0);
	}

	private int readInt(InputStream stream) throws IOException {
		int offset = 0;
		do {
			final int mySize = stream.read(readNumberBuff.array(), offset,
					LZ4FrameOutputStream.INTEGER_BYTES - offset);
			if (mySize < 0) {
				throw new IOException(PREMATURE_EOS);
			}
			offset += mySize;
		} while (offset < LZ4FrameOutputStream.INTEGER_BYTES);
		return readNumberBuff.getInt(0);
	}

	private void readBlock() throws IOException {
		int blockSize = readInt(in);
		final boolean compressed = (blockSize & LZ4FrameOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK) == 0;
		blockSize &= ~LZ4FrameOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK;

		// Check for EndMark
		if (blockSize == 0) {
			if (frameInfo
					.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_CHECKSUM)) {
				final int contentChecksum = readInt(in);
				if (contentChecksum != frameInfo.currentStreamHash()) {
					throw new IOException("Content checksum mismatch");
				}
			}
			if (frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_SIZE)
					&& expectedContentSize != totalContentSize) {
				throw new IOException("Size check mismatch");
			}
			frameInfo.finish();
			return;
		}

		final byte[] tmpBuffer; // Use a temporary buffer, potentially one used
		// for compression
		if (compressed) {
			tmpBuffer = compressedBuffer;
		} else {
			tmpBuffer = rawBuffer;
		}
		if (blockSize > maxBlockSize) {
			throw new IOException(String.format(Locale.ROOT,
					"Block size %s exceeded max: %s", blockSize, maxBlockSize));
		}

		int offset = 0;
		while (offset < blockSize) {
			final int lastRead = in.read(tmpBuffer, offset, blockSize - offset);
			if (lastRead < 0) {
				throw new IOException(PREMATURE_EOS);
			}
			offset += lastRead;
		}

		// verify block checksum
		if (frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.BLOCK_CHECKSUM)) {
			final int hashCheck = readInt(in);
			if (hashCheck != checksum.hash(tmpBuffer, 0, blockSize, 0)) {
				throw new IOException(BLOCK_HASH_MISMATCH);
			}
		}

		final int currentBufferSize;
		if (compressed) {
			try {
				currentBufferSize = decompressor.decompress(tmpBuffer, 0,
						blockSize, rawBuffer, 0, rawBuffer.length);
			} catch (LZ4Exception e) {
				throw new IOException(e);
			}
		} else {
			currentBufferSize = blockSize;
		}
		if (frameInfo.isEnabled(LZ4FrameOutputStream.FLG.Bits.CONTENT_CHECKSUM)) {
			frameInfo.updateStreamHash(rawBuffer, 0, currentBufferSize);
		}
		totalContentSize += currentBufferSize;
		buffer.limit(currentBufferSize);
		buffer.rewind();
	}

	private int read(byte[] b, int off, int len) throws IOException {
		nextFrameInfo();

		if ((off < 0) || (len < 0) || (off + len > b.length)) {
			throw new IndexOutOfBoundsException();
		}
		while (buffer.remaining() == 0) {
			if (frameInfo.isFinished()) {
				if (!nextFrameInfo()) {
					return -1;
				}
			}
			readBlock();
		}
		len = Math.min(len, buffer.remaining());
		buffer.get(b, off, len);
		return len;
	}

}
