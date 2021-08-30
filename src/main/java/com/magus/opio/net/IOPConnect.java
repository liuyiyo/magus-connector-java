package com.magus.opio.net;

import com.magus.opio.OPException;
import com.magus.jdbc.OPResultSet;
import com.magus.opio.dto.OPTable;

import java.util.Date;

public interface IOPConnect {

	/**
	 * 关闭网络连接
	 */
	public void close();

	// exec
	/**
	 * 执行无参数SQL 语句方法，主要可用于数据库相关的增删改查活动，若操作的测点明确请调用有参数的 execSQL 方法。 SQL
	 * 语句规范请查阅相关SQL 说明文档。
	 * 
	 * @param sql
	 * @return
	 * @throws OPException
	 */
	public OPResultSet execSQL(String sql) throws OPException;

	/**
	 * @param sql
	 * @param keys
	 *            数据查询的关键索引，索引包括ID:>>int[]，GN:>>String[]，UD:>>long[]
	 * @return
	 * @throws OPException
	 */
	public OPResultSet execSQL(String sql, Object keys) throws OPException;

	/**
	 * 
	 * @param table
	 * @param keys
	 *            数据查询的关键索引，索引包括ID:>>int[]，GN:>>String[]，UD:>>long[]
	 * @return
	 */
	public OPResultSet find(OPTable table, Object keys) throws OPException;

	/**
	 * 
	 * @param tablename
	 * @param keys
	 *            数据查询的关键索引，索引包括ID:>>int[]，GN:>>String[]，UD:>>long[]
	 * @return
	 */
	public OPResultSet remove(String tablename, Object keys) throws OPException;

	/**
	 * @param table
	 */
	public OPResultSet insert(OPTable table) throws OPException;

	// update
	/**
	 * @param table
	 * @return
	 */
	public OPResultSet update(OPTable table) throws OPException;

	// replace
	/**
	 * @param table
	 * @return
	 */
	public OPResultSet replace(OPTable table) throws OPException;

	/**
	 * @return 数据库系统时间
	 */
	public Date getServerTime() throws OPException;

	public OPTable createTable(String tableName);

	public abstract void destroy();

	public boolean isActive();

	public boolean resetSession() throws OPException;

}