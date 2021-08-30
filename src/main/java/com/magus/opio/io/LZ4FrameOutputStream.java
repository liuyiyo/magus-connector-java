package com.magus.opio.io;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.xxhash.StreamingXXHash32;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;

public class LZ4FrameOutputStream {

	private ByteArrayOutputStream out;

	static final int INTEGER_BYTES = Integer.SIZE >>> 3; // or Integer.BYTES
	static final int LONG_BYTES = Long.SIZE >>> 3; // or Long.BYTES in Java

	static final int MAGIC = 0x184D2204;
	static final int LZ4_MAX_HEADER_LENGTH = 4 + // magic
			1 + // FLG
			1 + // BD
			8 + // Content Size
			1; // HC
	static final int LZ4_FRAME_INCOMPRESSIBLE_MASK = 0x80000000;
	static final FLG.Bits[] DEFAULT_FEATURES = new FLG.Bits[] { FLG.Bits.BLOCK_INDEPENDENCE };

	public static enum BLOCKSIZE {
		SIZE_64KB(4), SIZE_256KB(5), SIZE_1MB(6), SIZE_4MB(7);
		private final int indicator;

		BLOCKSIZE(int indicator) {
			this.indicator = indicator;
		}

		public int getIndicator() {
			return this.indicator;
		}

		public static BLOCKSIZE valueOf(int indicator) {
			switch (indicator) {
			case 7:
				return SIZE_4MB;
			case 6:
				return SIZE_1MB;
			case 5:
				return SIZE_256KB;
			case 4:
				return SIZE_64KB;
			default:
				throw new IllegalArgumentException(String.format(Locale.ROOT,
						"Block size must be 4-7. Cannot use value of [%d]",
						indicator));
			}
		}
	}

	private final LZ4Compressor compressor;
	private final XXHash32 checksum;
	private final byte[] compressedBuffer; // Only allocated once so it can be
	// reused
	private final int maxBlockSize;
	private final long knownSize;
	private final ByteBuffer intLEBuffer = ByteBuffer.allocate(INTEGER_BYTES)
			.order(ByteOrder.LITTLE_ENDIAN);

	private FrameInfo frameInfo = null;

	public LZ4FrameOutputStream() {
		this.out = new ByteArrayOutputStream();
		this.compressor = LZ4Factory.fastestInstance().fastCompressor();
		this.checksum = XXHashFactory.fastestInstance().hash32();
		frameInfo = new FrameInfo(
				new FLG(FLG.DEFAULT_VERSION, DEFAULT_FEATURES), new BD(
						BLOCKSIZE.SIZE_64KB));
		maxBlockSize = frameInfo.getBD().getBlockMaximumSize();
		compressedBuffer = new byte[this.compressor
				.maxCompressedLength(maxBlockSize)];
		if (frameInfo.getFLG().isEnabled(FLG.Bits.CONTENT_SIZE)) {
			throw new IllegalArgumentException(
					"Known size must be greater than zero in order to use the known size feature");
		}
		this.knownSize = -1L;
	}

	public synchronized byte[] compressFrame(byte[] src) throws IOException {
		out.reset();
		writeHeader();
		writeBlock(src);
		writeEndMark();
		byte[] data = out.toByteArray();
		return data;
	}

	private void writeHeader() throws IOException {
		final ByteBuffer headerBuffer = ByteBuffer.allocate(
				LZ4_MAX_HEADER_LENGTH).order(ByteOrder.LITTLE_ENDIAN);
		headerBuffer.putInt(MAGIC);
		headerBuffer.put(frameInfo.getFLG().toByte());
		headerBuffer.put(frameInfo.getBD().toByte());
		if (frameInfo.isEnabled(FLG.Bits.CONTENT_SIZE)) {
			headerBuffer.putLong(knownSize);
		}
		// compute checksum on all descriptor fields
		final int hash = (checksum.hash(headerBuffer.array(), INTEGER_BYTES,
				headerBuffer.position() - INTEGER_BYTES, 0) >> 8) & 0xFF;
		headerBuffer.put((byte) hash);
		// write out frame descriptor
		out.write(headerBuffer.array(), 0, headerBuffer.position());
	}

	private void writeBlock(byte[] src) throws IOException {
		if (src.length == 0) {
			return;
		}
		// Make sure there's no stale data
		Arrays.fill(compressedBuffer, (byte) 0);

		if (frameInfo.isEnabled(FLG.Bits.CONTENT_CHECKSUM)) {
			frameInfo.updateStreamHash(src, 0, src.length);
		}

		int compressedLength = compressor.compress(src, 0, src.length,
				compressedBuffer, 0);
		final byte[] bufferToWrite;
		final int compressMethod;

		if (compressedLength >= src.length) {
			compressedLength = src.length;
			bufferToWrite = Arrays.copyOf(src, compressedLength);
			compressMethod = LZ4_FRAME_INCOMPRESSIBLE_MASK;
		} else {
			bufferToWrite = compressedBuffer;
			compressMethod = 0;
		}

		// Write content
		intLEBuffer.putInt(0, compressedLength | compressMethod);
		out.write(intLEBuffer.array());
		out.write(bufferToWrite, 0, compressedLength);

		// Calculate and write block checksum
		if (frameInfo.isEnabled(FLG.Bits.BLOCK_CHECKSUM)) {
			intLEBuffer.putInt(0, checksum.hash(bufferToWrite, 0,
					compressedLength, 0));
			out.write(intLEBuffer.array());
		}
	}

	private void writeEndMark() throws IOException {
		intLEBuffer.putInt(0, 0);
		out.write(intLEBuffer.array());
		if (frameInfo.isEnabled(FLG.Bits.CONTENT_CHECKSUM)) {
			intLEBuffer.putInt(0, frameInfo.currentStreamHash());
			out.write(intLEBuffer.array());
		}
		frameInfo.finish();
	}

	public void close() throws IOException {
		out.close();
	}

	public static class FLG {
		private static final int DEFAULT_VERSION = 1;

		private final BitSet bitSet;
		private final int version;

		public enum Bits {
			RESERVED_0(0), RESERVED_1(1), CONTENT_CHECKSUM(2), CONTENT_SIZE(3), BLOCK_CHECKSUM(
					4), BLOCK_INDEPENDENCE(5);

			private final int position;

			Bits(int position) {
				this.position = position;
			}
		}

		public FLG(int version, Bits... bits) {
			this.bitSet = new BitSet(8);
			this.version = version;
			if (bits != null) {
				for (Bits bit : bits) {
					bitSet.set(bit.position);
				}
			}
			validate();
		}

		private FLG(int version, byte b) {
			this.bitSet = BitSet.valueOf(new byte[] { b });
			this.version = version;
			validate();
		}

		public static FLG fromByte(byte flg) {
			final byte versionMask = (byte) (flg & (3 << 6));
			return new FLG(versionMask >>> 6, (byte) (flg ^ versionMask));
		}

		public byte toByte() {
			return (byte) (bitSet.toByteArray()[0] | ((version & 3) << 6));
		}

		private void validate() {
			if (bitSet.get(Bits.RESERVED_0.position)) {
				throw new RuntimeException("Reserved0 field must be 0");
			}
			if (bitSet.get(Bits.RESERVED_1.position)) {
				throw new RuntimeException("Reserved1 field must be 0");
			}
			if (!bitSet.get(Bits.BLOCK_INDEPENDENCE.position)) {
				throw new RuntimeException(
						"Dependent block stream is unsupported (BLOCK_INDEPENDENCE must be set)");
			}
			if (version != DEFAULT_VERSION) {
				throw new RuntimeException(String.format(Locale.ROOT,
						"Version %d is unsupported", version));
			}
		}

		public boolean isEnabled(Bits bit) {
			return bitSet.get(bit.position);
		}

		public int getVersion() {
			return version;
		}
	}

	public static class BD {
		private static final int RESERVED_MASK = 0x8F;

		private final BLOCKSIZE blockSizeValue;

		private BD(BLOCKSIZE blockSizeValue) {
			this.blockSizeValue = blockSizeValue;
		}

		public static BD fromByte(byte bd) {
			int blockMaximumSize = (bd >>> 4) & 7;
			if ((bd & RESERVED_MASK) > 0) {
				throw new RuntimeException("Reserved fields must be 0");
			}

			return new BD(BLOCKSIZE.valueOf(blockMaximumSize));
		}

		// 2^(2n+8)
		public int getBlockMaximumSize() {
			return 1 << ((2 * blockSizeValue.getIndicator()) + 8);
		}

		public byte toByte() {
			return (byte) ((blockSizeValue.getIndicator() & 7) << 4);
		}
	}

	static class FrameInfo {
		private final FLG flg;
		private final BD bd;
		private final StreamingXXHash32 streamHash;
		private boolean finished = false;

		public FrameInfo(FLG flg, BD bd) {
			this.flg = flg;
			this.bd = bd;
			this.streamHash = flg.isEnabled(FLG.Bits.CONTENT_CHECKSUM) ? XXHashFactory
					.fastestInstance().newStreamingHash32(0)
					: null;
		}

		public boolean isEnabled(FLG.Bits bit) {
			return flg.isEnabled(bit);
		}

		public FLG getFLG() {
			return this.flg;
		}

		public BD getBD() {
			return this.bd;
		}

		public void updateStreamHash(byte[] buff, int off, int len) {
			this.streamHash.update(buff, off, len);
		}

		public int currentStreamHash() {
			return this.streamHash.getValue();
		}

		public void finish() {
			this.finished = true;
		}

		public boolean isFinished() {
			return this.finished;
		}
	}
}