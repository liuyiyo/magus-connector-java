package com.magus.opio.utils.struct;

public class Field_ {
    public String name;
    public String tag;
    public byte type;
    public int pos;
    public int dataLen;

    public Field_(String name, String tag, byte type, int pos, int dataLen) {
        this.name = name;
        this.tag = tag;
        this.type = type;
        this.pos = pos;
        this.dataLen = dataLen;
    }
}
