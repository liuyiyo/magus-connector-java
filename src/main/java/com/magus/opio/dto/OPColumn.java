package com.magus.opio.dto;

import com.magus.opio.OPException;
import com.magus.opio.io.OPIOBuffer;

public class OPColumn {
	private String columnName;
	private int type;
	private int index;
	private int length;
	private int cell; // 变长字段索引
	private int offset;
	private int end;
	private byte[] ext;
	private boolean pk;

	private static String cName = "Name";
	private static String cType = "Type";
	private static String cLength = "Length";
	private static String cExt = "Ext";

	public OPColumn() {
	}

	public OPColumn(String columnName, int type, int index) {
		super();
		this.columnName = columnName;
		this.type = type;
		this.index = index;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getCell() {
		return cell;
	}

	public int getIndex() {
		return index;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getOffset() {
		return offset;
	}

	public int getEnd() {
		return end;
	}

	public void setExt(byte[] ext) {
		this.ext = ext;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isPk() {
		return pk;
	}

	public void setPk(boolean pk) {
		this.pk = pk;
	}

	public void write(OPIOBuffer io) throws OPException {
		int size = 3;
		boolean isHaveExt = false;
		if (ext != null && ext.length > 0) {
			isHaveExt = true;
			size = 4;
		}
		io.EncodeMapStart(size);
		io.EncodeString(cName);
		io.EncodeString(columnName);
		io.EncodeString(cType);
		io.EncodeInt8((byte) type);
		io.EncodeString(cLength);
		io.EncodeInt8((byte) length);
		if (isHaveExt) {
			io.EncodeString(cExt);
			io.EncodeBytes(ext);
		}
	}

	public void read(OPIOBuffer io) throws OPException {
		int size = io.DecodeMapStart();
		for (int i = 0; i < size; i++) {
			String key = io.DecodeString();
			if (key.equals(cName)) {
				columnName = io.DecodeString();
			} else if (key.equals(cType)) {
				type = (int) (io.DecodeInt() & 0xff);
			} else if (key.equals(cLength)) {
				length = (int) (io.DecodeInt() & 0xff);
			} else if (key.equals(cExt)) {
				ext = io.DecodeBytes();
			} else {
				io.DecodeValue();
			}
		}
	}

	@Override
	public String toString() {
		return "OPColumn [columnName=" + columnName + ", type=" + type
				+ ", index=" + index + "]";
	}

}
