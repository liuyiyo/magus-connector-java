package com.magus.opio.dto;

import com.magus.opio.OPException;
import com.magus.opio.io.OPIOBuffer;

public class OPFilter {

	public static final byte OPER_EQ = 0; // =
	public static final byte OPER_NE = 1; // !=
	public static final byte OPER_GT = 2; // >
	public static final byte OPER_LT = 3; // <
	public static final byte OPER_GE = 4; // >=
	public static final byte OPER_LE = 5; // <=
	public static final byte OPER_IN = 6; // in
	public static final byte OPER_NOT_IN = 7; // not in
	public static final byte OPER_LIKE = 8; // like
	public static final byte OPER_NOT_LIKE = 9; // not like
	public static final byte OPER_REG_EXP = 10; // regexp

	public static final byte LOGIC_AND = 0;
	public static final byte LOGIC_OR = 1;

	private String left;
	private String right;
	private byte operator;
	private byte relation;

	public OPFilter() {
	}

	public OPFilter(String left, byte operator, String right, byte relation) {
		super();
		this.left = left;
		this.right = right;
		this.operator = operator;
		this.relation = relation;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public byte getOperator() {
		return operator;
	}

	public void setOperator(byte operator) {
		this.operator = operator;
	}

	public byte getRelation() {
		return relation;
	}

	public void setRelation(byte relation) {
		this.relation = relation;
	}

	public void write(OPIOBuffer io) throws OPException {
		io.EncodeMapStart(4);
		io.EncodeString("L");
		io.EncodeString(left);
		io.EncodeString("O");
		io.EncodeInt8(operator);
		io.EncodeString("R");
		io.EncodeString(right);
		io.EncodeString("Or");
		io.EncodeInt8(relation);
	}

	public void read(OPIOBuffer io) throws OPException {
		if (io.DecodeMapStart() != 4) throw new OPException("OPFilter read error");
		if (!io.DecodeString().equals("L")) throw new OPException("OPFilter read L error");
		left = io.DecodeString();
		if (!io.DecodeString().equals("O")) throw new OPException("OPFilter read O error");
		operator = (byte) io.DecodeInt8();
		if (!io.DecodeString().equals("R")) throw new OPException("OPFilter read R error");
		right = io.DecodeString();
		if (!io.DecodeString().equals("Or")) throw new OPException("OPFilter read Or error");
		relation = (byte) io.DecodeInt8();
	}
}
