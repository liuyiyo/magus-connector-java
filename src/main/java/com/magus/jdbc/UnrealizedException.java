package com.magus.jdbc;

import java.sql.SQLException;

public class UnrealizedException extends SQLException {

	private static final String msg = "方法未实现";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnrealizedException() {
		super(msg);
	}

	public UnrealizedException(String msg) {
		super(msg);
	}
}
