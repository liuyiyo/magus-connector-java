package com.magus.opio.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

public class UtilsBuffer {
    private static final int defaultCap = 65535;
    private ByteBuffer byteBuffer;
    private int capacity;

    public UtilsBuffer() {
        this(defaultCap);
    }

    public UtilsBuffer(int cap) {
        capacity = cap;
        byteBuffer = ByteBuffer.allocate(cap);
    }

    public synchronized void put(byte[] raw) {
        if (raw == null)
            return;
        tryGrowBufferLen(raw.length);
        byteBuffer.put(raw);
    }

    private void tryGrowBufferLen(int n) {
        while (n > byteBuffer.remaining()) {
            int pos = byteBuffer.position();
            capacity += capacity / 2;
            ByteBuffer newAlloc = ByteBuffer.allocate(capacity);
            byteBuffer = newAlloc.put(byteBuffer.array());
            byteBuffer.position(pos);
        }
    }

    public synchronized void reset() {
        byteBuffer.clear();
    }

    public synchronized int len() {
        return byteBuffer.position();
    }

    public synchronized int cap() {
        return byteBuffer.capacity();
    }

    public synchronized byte[] bytes() {
        byte[] buf = byteBuffer.array();
        return Arrays.copyOfRange(buf, 0, byteBuffer.position());
    }

    public void WriteBool(boolean v) {

    }

    public void WriteByte(byte v) {

    }

    public void WriteShort(short v) {

    }

    public void WriteInt(int v) {

    }

    public void WriteLong(long v) {

    }

    public void WriteFloat(float v) {

    }

    public void WriteDouble(double v) {

    }

    public void WriteDatetime(Date v) {

    }

    public void WriteString(String v) {

    }

    public void WriteBinary(byte[] v) {

    }
}
