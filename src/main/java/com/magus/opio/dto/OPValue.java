package com.magus.opio.dto;

public class OPValue extends BinaryConvertor {
    public static final byte TYPE_NULL = 0;
    public static final byte TYPE_BOOL = 1;
    public static final byte TYPE_INT8 = 2;
    public static final byte TYPE_INT16 = 3;
    public static final byte TYPE_INT32 = 4;
    public static final byte TYPE_INT64 = 5;
    public static final byte TYPE_FLOAT = 6;
    public static final byte TYPE_DOUBLE = 7;
    public static final byte TYPE_DATETIME = 8;
    public static final byte TYPE_STRING = 9;
    public static final byte TYPE_BINARY = 10;
    public static final byte TYPE_OBJECT = 11;
    public static final byte TYPE_MAP = 12;
    public static final byte TYPE_STRUCT = 13;
    public static final byte TYPE_ARRAY = 14;
    public static final byte TYPE_BOOL_ARRAY = 17;
    public static final byte TYPE_INT8_ARRAY = 18;
    public static final byte TYPE_INT16_ARRAY = 19;
    public static final byte TYPE_INT32_ARRAY = 20;
    public static final byte TYPE_INT64_ARRAY = 21;
    public static final byte TYPE_FLOAT_ARRAY = 22;
    public static final byte TYPE_DOUBLE_ARRAY = 23;
    public static final byte TYPE_DATETIME_ARRAY = 24;
    public static final byte TYPE_STRING_ARRAY = 25;
    public static final byte TYPE_BINARY_ARRAY = 26;
    public static final byte TYPE_OBJECT_ARRAY = 27;
    public static final byte TYPE_ROW = 32;
    public static final int FALSE = 0;
    public static final int TRUE = 1;
    private byte type = 0;
    private Object value;

    public OPValue() {
        this.setValue((int)0);
    }

    public OPValue(boolean v) {
        this.setValue(v);
    }

    public OPValue(byte v) {
        this.setValue(v);
    }

    public OPValue(short v) {
        this.setValue(v);
    }

    public OPValue(int v) {
        this.setValue(v);
    }

    public OPValue(long v) {
        this.setValue(v);
    }

    public OPValue(float v) {
        this.setValue(v);
    }

    public OPValue(double v) {
        this.setValue(v);
    }

    public OPValue(String v) {
        this.setValue(v);
    }

    public OPValue(byte[] v) {
        this.setValue(v);
    }

    public byte getType() {
        return this.type;
    }

    public void setValue(boolean obj) {
        this.type = 1;
        this.value = new Boolean(obj);
    }

    public void setValue(byte obj) {
        this.type = 2;
        this.value = new Byte(obj);
    }

    public void setValue(short obj) {
        this.type = 3;
        this.value = new Short(obj);
    }

    public void setValue(int obj) {
        this.type = 4;
        this.value = new Integer(obj);
    }

    public void setValue(long obj) {
        this.type = 5;
        this.value = new Long(obj);
    }

    public void setValue(float obj) {
        this.type = 6;
        this.value = new Float(obj);
    }

    public void setValue(double obj) {
        this.type = 7;
        this.value = new Double(obj);
    }

    public void setValue(String obj) {
        this.type = 9;
        this.value = obj;
    }

    public void setValue(byte[] obj) {
        this.type = 10;
        this.value = obj;
    }

    public boolean getBoolean() {
        return this.getLong() != 0L;
    }

    public byte getByte() {
        return (byte)((int)this.getLong());
    }

    public short getShort() {
        return (short)((int)this.getLong());
    }

    public int getInt() {
        return (int)this.getLong();
    }

    public long getLong() {
        long v = 0L;
        switch(this.type) {
            case 1:
                v = (Boolean)this.value ? 1L : 0L;
                break;
            case 2:
                v = (long)(Byte)this.value;
                break;
            case 3:
                v = (long)(Short)this.value;
                break;
            case 4:
                v = (long)(Integer)this.value;
                break;
            case 5:
                v = (Long)this.value;
                break;
            case 6:
                v = ((Float)this.value).longValue();
                break;
            case 7:
            case 8:
                v = ((Double)this.value).longValue();
                break;
            case 9:
                String valueStr = (String)this.value;
                if (valueStr != null && !valueStr.trim().equals("")) {
                    if (valueStr.startsWith("0x")) {
                        return Long.decode(valueStr);
                    }

                    try {
                        Double d = Double.valueOf(valueStr);
                        v = d.longValue();
                        break;
                    } catch (NumberFormatException var6) {
                        throw new RuntimeException(var6);
                    }
                }

                return 0L;
            case 10:
                byte[] bytes = (byte[])((byte[])this.value);
                int length = bytes.length;
                switch(length) {
                    case 1:
                        v = (long)bytes[0];
                        return v;
                    case 2:
                        v = (long)((bytes[0] << 8) + (bytes[1] & 255));
                        return v;
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                    default:
                        throw new RuntimeException("Unsupport bytes[" + length + "] to number");
                    case 4:
                        v = (long)((bytes[0] << 24) + ((bytes[1] & 255) << 16) + ((bytes[2] & 255) << 8) + (bytes[3] & 255));
                        return v;
                    case 8:
                        v = (long)bytes[0] << 56 | ((long)bytes[1] & 255L) << 48 | ((long)bytes[2] & 255L) << 40 | ((long)bytes[3] & 255L) << 32 | ((long)bytes[4] & 255L) << 24 | ((long)bytes[5] & 255L) << 16 | ((long)bytes[6] & 255L) << 8 | (long)bytes[7] & 255L;
                        return v;
                }
            default:
                throw new RuntimeException("Unsupport '" + this.value + "'[" + this.value.getClass() + "] to number.");
        }

        return v;
    }

    public double getFloat() {
        return this.getDouble();
    }

    public double getDouble() {
        double v = 0.0D;
        switch(this.type) {
            case 1:
                v = (Boolean)this.value ? 1.0D : 0.0D;
                break;
            case 2:
                v = (double)(Byte)this.value;
                break;
            case 3:
                v = (double)(Short)this.value;
                break;
            case 4:
                v = (double)(Integer)this.value;
                break;
            case 5:
                v = (double)(Long)this.value;
                break;
            case 6:
                v = (double)(Float)this.value;
                break;
            case 7:
            case 8:
                v = (Double)this.value;
                break;
            case 9:
                String valueStr = (String)this.value;
                if (valueStr != null && !valueStr.trim().equals("")) {
                    if (valueStr.startsWith("0x")) {
                        byte[] bytes = this.toBytes(valueStr);
                        v = this.parseDouble(bytes);
                    } else {
                        try {
                            Double d = Double.valueOf(valueStr);
                            v = (double)d.longValue();
                        } catch (NumberFormatException var5) {
                            throw new RuntimeException(var5);
                        }
                    }
                } else {
                    v = 0.0D;
                }
                break;
            case 10:
                v = this.parseDouble((byte[])((byte[])this.value));
                break;
            default:
                throw new RuntimeException("Unsupport '" + this.value + "'[" + this.value.getClass() + "] to double.");
        }

        return v;
    }

    public String getString() {
        if (this.value == null) {
            return null;
        } else {
            String v = null;
            switch(this.type) {
                case 1:
                    v = (Boolean)this.value ? TRUE_STR : FALSE_STR;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    v = this.value.toString();
                    break;
                case 10:
                    v = this.toHexString((byte[])((byte[])this.value));
            }

            return v;
        }
    }

    public byte[] getBytes() {
        if (this.value == null) {
            return null;
        } else {
            switch(this.type) {
                case 1:
                    return this.toBytes((Boolean)this.value);
                case 2:
                    return this.toBytes((Byte)this.value);
                case 3:
                    return this.toBytes((Short)this.value);
                case 4:
                    return this.toBytes((Integer)this.value);
                case 5:
                    return this.toBytes((Long)this.value);
                case 6:
                    return this.toBytes((Float)this.value);
                case 7:
                case 8:
                    return this.toBytes((Double)this.value);
                case 9:
                    return ((String)this.value).getBytes();
                case 10:
                    return (byte[])((byte[])this.value);
                default:
                    throw new RuntimeException("Unsupport '" + this.value + "'[" + this.value.getClass() + "] to bytes.");
            }
        }
    }

    public Object getObject() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }
}

