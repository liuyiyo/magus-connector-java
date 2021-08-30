package com.magus.jdbc;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.magus.jdbc.constant.LtConst;
import com.magus.jdbc.parse.constant.SqlType;
import com.magus.jdbc.parse.entity.ColumnInfo;
import com.magus.jdbc.parse.entity.ParserResult;
import com.magus.jdbc.parse.utils.SQLParseUtil;
import com.magus.opio.OPException;
import com.magus.opio.*;
import com.magus.opio.dto.OPRequest;
import com.magus.opio.dto.OPResponse;
import com.magus.opio.dto.OPTable;
import com.magus.opio.net.IOPConnect;
import com.magus.opio.net.OPIOConnect;
import com.magus.opio.pool.OPConnectManage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class OPPreparedStatement implements PreparedStatement {

	private static final String US = ":";
	public static final String DB = "db";
	public static final String TIME = "Time";
	public static final String CLUSTER = "cluster";
	private static final String TOKEN_TOPIC_READ = "Token.Read";
	private static final String TOKEN_TOPIC_WRITE = "Token.Write";
	private boolean closed = false;
	private SQLConnection sqlConnection;
	//数据库名
	private String dbName;
	private StringBuffer sql;
	private Object[] paramArray;
	private boolean parameterTypeFlag;
	private String tableName;
	private List<ColumnInfo> columnInfoList;
	private OPTable table;
	private SqlType sqlType;
	private OPIOConnect op;
	private List<OPIOConnect> writeOPIOConnectList;
	private String tokenCache;
	private String cluster;
	private ResultSet resultSet;
	private String matchDateColumn;
	private String classifyColumn;
	public OPPreparedStatement(){

	}
	/**
	 * @Author liuyi
	 * @Description //创建普通的Statement对象
	 * @Date 2021/5/13 16:31
	 * @Param [sqlConnection]
	 * @return com.magus.jdbc.OPPreparedStatement
	 **/
	public static OPPreparedStatement createStatement(SQLConnection sqlConnection) throws SQLException{
		OPPreparedStatement statement = new OPPreparedStatement();
		statement.sqlConnection = sqlConnection;
		try {
			statement.op = (OPIOConnect) sqlConnection.getOPConnectManage().getConnect();
		} catch (OPException e) {
			log.error("创建opio对象失败,原因===="+e.getMessage());
			e.printStackTrace();
		}
		return statement;
	}

	/**
	 * @Author liuyi
	 * @Description //创建预处理Statement对象
	 * @Date 2021/5/13 16:42
	 * @Param [sqlConnection, sql]
	 * @return com.magus.jdbc.OPPreparedStatement
	 **/
	public static OPPreparedStatement createPretreatment(SQLConnection sqlConnection, String sql) throws SQLException{
		OPPreparedStatement statement = new OPPreparedStatement();
		//解析sql
		ParserResult parserResult = SQLParseUtil.parserSql(sql);
		statement.parameterTypeFlag = true;
		if(parserResult!=null){
			statement.tableName = parserResult.getTableName();
			statement.columnInfoList = parserResult.getColumnInfoList();
			statement.sqlType = parserResult.getSqlType();
			statement.table = new OPTable(statement.tableName);
			statement.parameterTypeFlag = false;
			statement.matchDateColumn = parserResult.getMatchDateColumn();
			statement.classifyColumn = parserResult.getClassifyColumn();
		}
		statement.sqlConnection = sqlConnection;
		//设置数据库名
		statement.dbName = sqlConnection.getDb();
		statement.sql = new StringBuffer(sql);
		int length = statement.sql.length() - sql.replaceAll("\\?", "").length();
		statement.paramArray = new Object[length];
		try {
			statement.op = (OPIOConnect) sqlConnection.getOPConnectManage().getConnect();
		} catch (OPException e) {
			log.error("创建opio对象失败,原因===="+e.getMessage());
			e.printStackTrace();
			throw new SQLException("创建opio对象失败,原因===="+e.getMessage());
		}
		//如果是写入数据，需要从lcs获取Token，并获取写数据的OPIOConnect对象列表
		if(statement.sqlType!=null&&statement.sqlType==SqlType.INSERT_TABLE){
			//设置cluster
			if(sqlConnection.getInfo().get(CLUSTER)==null){
				throw new SQLException("cluster不能为空");
			}
			statement.cluster = sqlConnection.getInfo().get(CLUSTER).toString();
			//设置write OPIOConnect
			Token token = get_token(statement.op,statement.cluster,TOKEN_TOPIC_WRITE);
			statement.tokenCache = token.getTokenCache();
			statement.writeOPIOConnectList = new ArrayList<>();
			String[] addresses = token.getDb_listen_addr_list();
			for (String address : addresses) {
				String[] splits = address.split(US);
				String ip = splits[0];
				int port = Integer.parseInt(splits[1]);
				try {
					OPIOConnect op = new OPIOConnect(ip, port, 120, OPConst.ZIP_MODEL_Block);
					statement.writeOPIOConnectList.add(op);
					break;
				} catch (OPException e) {
					e.printStackTrace();
				}
			}
		}
		return statement;
	}

	public void addBatch(String sql) throws SQLException {

		throw new UnrealizedException("OPStatement.addBatch");
	}

	public void cancel() throws SQLException {
		throw new UnrealizedException("OPStatement.cancel");

	}

	public void clearBatch() throws SQLException {
		table = null;
	}

	public void clearWarnings() throws SQLException {
		throw new UnrealizedException("OPStatement.clearWarnings");

	}

	/**
	 * @Author liuyi
	 * @Description 关闭statement
	 * @Date 2021/5/12 13:46
	 * @Param []
	 * @return void
	 **/
	public void close() throws SQLException {
		this.closed = true;
		this.dbName = null;
		this.sql = null;
		this.paramArray = null;
		this.parameterTypeFlag = false;
		this.tableName = null;
		this.columnInfoList = null;
		this.table = null;
		this.sqlType = null;
		//將OPIOConnect归还到opio对象池
		try {
			this.sqlConnection.getOPConnectManage().freeConnect(this.op);
		} catch (OPException e) {
			e.printStackTrace();
			throw new SQLException("归还OPIOConnect对象失败");
		}
		this.sqlConnection = null;
		//关闭write OPConnect
		if(writeOPIOConnectList!=null&&writeOPIOConnectList.size()>0){
			writeOPIOConnectList.parallelStream()
					.forEach(writeOP->{
						writeOP.close();
					});
			writeOPIOConnectList = null;
		}
	}

	public void closeOnCompletion() throws SQLException {
		throw new UnrealizedException("OPStatement.closeOnCompletion");

	}

	public boolean execute(String sql) throws SQLException {
		int count = executeUpdate(sql);
		return count > 0;
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnrealizedException("OPStatement.execute");

	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		throw new UnrealizedException("OPStatement.execute2");

	}

	public boolean execute(String sql, String[] columnNames) throws SQLException {
		throw new UnrealizedException("OPStatement.execute3");

	}

	/**
	 * @Author liuyi
	 * @Description //批量执行
	 * @Date 2021/5/7 13:51
	 * @Param []
	 * @return int[]
	 **/
	public int[] executeBatch() throws SQLException {
		try {
			Random random = new Random(System.currentTimeMillis());
			writeOPIOConnectList.parallelStream()
					.forEach(writeOP->{
						try {
							long transId = random.nextLong();
							OPRequest req = new OPRequest(writeOP);
							req.setID(transId);
							req.setAction(LtConst.ActionInsert);
							req.setOption(LtConst.SubjectToken, tokenCache);
							req.setOption(DB, dbName);
							req.setOption(TIME, (double) (System.currentTimeMillis() / 1000));
							req.setTable(table);
							req.Write();
							req.WriteContent();
							req.Flush();
							OPResponse res = req.GetResponse();
							System.out.println("err no:" + res.getError());
							System.out.println("err msg:" + res.getErrorMessage());
							res.destroy();
							//commit
							data_commit(transId,writeOP);
						}catch (OPException e){
							e.printStackTrace();
							log.error("向 "+writeOP.getHost()+":"+writeOP.getPort()+"写入数据失败");
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet executeQuery(final String sql) throws SQLException {
		String sql_new = SQLConvert.removeComment(sql);
		try {
			OPResultSet rs = op.execSQL(sql_new);
			if (rs != null) {
				// 调用数据库API
				return rs;
			} else {
				throwException(OPError.ERROR_NET_IO);
			}

		} catch (OPException e) {
			throw new SQLException(e.getMessage());
		}
		return null;
	}

	public int executeUpdate(final String sql) throws SQLException {
		//判断是否是建表语句
		if(sqlType==SqlType.CREATE_TABLE){
			try {
				OPRequest req = new OPRequest(op);
				req.setAction(LtConst.ActionCreate);
				req.setOption(LtConst.PropSubject, LtConst.SubjectMetadata);
				req.setOption(LtConst.SubDataBase, dbName);
				req.setOption(LtConst.SubjectTable, tableName);
				req.setOption(LtConst.TableColumns, JSON.toJSONString(columnInfoList));
				req.setOption(LtConst.MatchDateColumn, matchDateColumn);
				req.setOption(LtConst.ClassifyColumn, classifyColumn);
				req.WriteAndFlush();
				OPResponse res = req.GetResponse();
				log.info("err no:" + res.getError());
				log.info("err msg:" + res.getErrorMessage());
				return 1;
			} catch (OPException e) {
				log.error("创建表失败，原因："+e.getMessage(),e);
				throw new SQLException("创建表失败，原因："+e.getMessage());
			}
		}
		String sql_new = SQLConvert.removeComment(sql);
		OPConnectManage opcm = sqlConnection.getOPConnectManage();
		try {
			IOPConnect connect = opcm.getConnect();
			OPResultSet rs = connect.execSQL(sql_new);
			this.resultSet = rs;
			opcm.freeConnect(connect);
			if (rs == null) {
				throwException(OPError.ERROR_NET_IO);
				return OPError.ERROR_NET_IO;
			} else {
//				int rowCount = getSuccessCount(rs);
//				rs.close();
				return 1;
			}
		} catch (OPException e) {
			throw new SQLException(e.getMessage());
		}
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		throw new UnrealizedException("OPStatement.executeUpdate");
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		throw new UnrealizedException("OPStatement.executeUpdate2");
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		throw new UnrealizedException("OPStatement.executeUpdate3");
	}

	public Connection getConnection() throws SQLException {
		throw new UnrealizedException("OPStatement.getConnection");
	}

	public int getFetchDirection() throws SQLException {
		throw new UnrealizedException("OPStatement.getFetchDirection");
	}

	public int getFetchSize() throws SQLException {
		throw new UnrealizedException("OPStatement.getFetchSize");
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		throw new UnrealizedException("OPStatement.getGeneratedKeys");
	}

	public int getMaxFieldSize() throws SQLException {
		return 255;
	}

	public int getMaxRows() throws SQLException {
		// throw new UnrealizedException("OPStatement.getMaxRows");
		return Short.MAX_VALUE;
	}

	public boolean getMoreResults() throws SQLException {
		return false;
	}

	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}

	public int getQueryTimeout() throws SQLException {
		// throw new UnrealizedException("OPStatement.getQueryTimeout");
		return 0;
	}

	public ResultSet getResultSet() throws SQLException {
		return this.resultSet;
	}

	public int getResultSetConcurrency() throws SQLException {
		throw new UnrealizedException("OPStatement.getResultSetConcurrency");
	}

	public int getResultSetHoldability() throws SQLException {
		throw new UnrealizedException("OPStatement.getResultSetHoldability");
	}

	public int getResultSetType() throws SQLException {
		throw new UnrealizedException("OPStatement.getResultSetType");
	}

	public int getUpdateCount() throws SQLException {
		return 0;
	}

	public SQLWarning getWarnings() throws SQLException {
		throw new UnrealizedException("OPStatement.getWarnings");
	}

	public boolean isCloseOnCompletion() throws SQLException {
		return true;
	}

	public boolean isClosed() throws SQLException {
		// TODO 关闭连接活动
		return closed;
	}

	public boolean isPoolable() throws SQLException {
		// throw new UnrealizedException("OPStatement.isPoolable");
		return false;
	}

	public void setCursorName(String name) throws SQLException {
		// throw new UnrealizedException("OPStatement.setCursorName");
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		// throw new UnrealizedException("OPStatement.setEscapeProcessing");

	}

	public void setFetchDirection(int direction) throws SQLException {
		// throw new UnrealizedException("OPStatement.setFetchDirection");
	}

	public void setFetchSize(int rows) throws SQLException {
		// throw new UnrealizedException("OPStatement.setFetchSize");
	}

	public void setMaxFieldSize(int max) throws SQLException {
		// throw new UnrealizedException("OPStatement.setMaxFieldSize");
	}

	public void setMaxRows(int max) throws SQLException {
		// throw new UnrealizedException("OPStatement.setMaxRows");
	}

	public void setPoolable(boolean poolable) throws SQLException {
		// throw new UnrealizedException("OPStatement.setPoolable");
	}

	public void setQueryTimeout(int seconds) throws SQLException {
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// throw new UnrealizedException("OPStatement.isWrapperFor");
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnrealizedException("OPStatement.unwrap");
	}

	public void addBatch() throws SQLException {
		try {
			for (int i = 0; i < paramArray.length; i++) {
				int type = table.getColumns().get(i).getType();
				switch (type){
					case OPType.VtBool:
						table.setColumnBool(i, (boolean)paramArray[i]);
						break;
					case OPType.VtInt8:
						table.setColumnByte(i, (byte)paramArray[i]);
						break;
					case OPType.VtInt16:
						table.setColumnShort(i, (short)paramArray[i]);
						break;
					case OPType.VtInt32:
						table.setColumnInt(i, (int)paramArray[i]);
						break;
					case OPType.VtInt64:
						table.setColumnLong(i, (long)paramArray[i]);
						break;
					case OPType.VtFloat:
						table.setColumnFloat(i, (float)paramArray[i]);
						break;
					case OPType.VtDouble:
						table.setColumnDouble(i, (double)paramArray[i]);
						break;
					case OPType.VtDateTime:
						table.setColumnDatetime(i, (java.util.Date) paramArray[i]);
						break;
					case OPType.VtString:
						String str = (String) paramArray[i];
						table.setColumnString(i, str.substring(1,str.length()-1));
						break;
					case OPType.VtBinary:
						table.setColumnBinary(i, (byte[]) paramArray[i]);
						break;
					case OPType.VtMap:
						table.setColumnMap(i, (Map<?, ?>) paramArray[i]);
						break;
					case OPType.VtStructure:
						table.setColumnStruct(i, paramArray[i]);
						break;
					case OPType.VtArray:
						table.setColumnArray(i, (List<?>) paramArray[i]);
						break;
					case OPType.BOOL_ARRAY:
						table.setColumnBoolArray(i,(List<Boolean>) paramArray[i]);
						break;
					case OPType.INT8_ARRAY:
						table.setColumnByteArray(i,(List<Byte>) paramArray[i]);
						break;
					case OPType.INT16_ARRAY:
						table.setColumnShortArray(i,(List<Short>) paramArray[i]);
						break;
					case OPType.INT32_ARRAY:
						table.setColumnIntegerArray(i,(List<Integer>) paramArray[i]);
						break;
					case OPType.INT64_ARRAY:
						table.setColumnLongArray(i,(List<Long>) paramArray[i]);
						break;
					case OPType.FLOAT_ARRAY:
						table.setColumnFloatArray(i,(List<Float>) paramArray[i]);
						break;
					case OPType.DOUBLE_ARRAY:
						table.setColumnDoubleArray(i,(List<Double>) paramArray[i]);
						break;
					case OPType.DATETIME_ARRAY:
						table.setColumnDateArray(i,(List<java.util.Date>) paramArray[i]);
						break;
					case OPType.STRING_ARRAY:
						table.setColumnStringArray(i,(List<String>) paramArray[i]);
						break;
					case OPType.BINARY_ARRAY:
						table.setColumnBinaryArray(i,(List<byte[]>) paramArray[i]);
						break;
					default:
						throw new SQLException("暂不支持该类型============type:"+type);
				}
			}
			table.bindRow();
		} catch (OPException e) {
			e.printStackTrace();
			throw new SQLException("addBatch失败:"+e.getMessage());
		}
		if(parameterTypeFlag) return;
		//如果是第一次，则将是否设置类型标志设置为true
		parameterTypeFlag = true;
	}

	public void clearParameters() throws SQLException {
		this.paramArray = null;
	}

	public boolean execute() throws SQLException {
		return this.execute(this.replaceSqlString());
	}

	public ResultSet executeQuery() throws SQLException {
		return this.executeQuery(this.replaceSqlString());
	}

	public int executeUpdate() throws SQLException {
		return this.executeUpdate(this.replaceSqlString());
	}

	/**
	 * 将参数替换入sql中
	 *
	 * @return
	 * @throws SQLException
	 */
	private String replaceSqlString() throws SQLException {
		if (this.sql == null)
			throw new SQLException("sql语句为空");
		int replaceNum = this.sql.length() - this.sql.toString().replaceAll("\\?", "").length();

		if (replaceNum == 0)
			return this.sql.toString();

		// 检查参数数组和sql中要替换的参数长度是否一致
		if (this.paramArray == null || replaceNum != this.paramArray.length)
			throw new SQLException("参数传入有误");

		String replacedSql = "";
		StringBuffer sql = new StringBuffer(this.sql);

		for (Object para : this.paramArray) {
			int index = sql.indexOf("?");
			replacedSql += sql.substring(0, index) + (para == null ? "NULL" : para);
			sql.delete(0, index + 1);
		}

		replacedSql += sql;
		sql = null;

		return replacedSql;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		throw new UnrealizedException("OPStatement.getMetaData");
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		throw new UnrealizedException("OPStatement.getParameterMetaData");
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		throw new UnrealizedException("OPStatement.setArray");
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		throw new UnrealizedException("OPStatement.setAsciiStream");
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new UnrealizedException("OPStatement.setAsciiStream");
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		throw new UnrealizedException("OPStatement.setAsciiStream");
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		throw new UnrealizedException("OPStatement.setBigDecimal");
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		throw new UnrealizedException("OPStatement.setBinaryStream");
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		throw new UnrealizedException("OPStatement.setBinaryStream");
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		throw new UnrealizedException("OPStatement.setBinaryStream");
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		throw new UnrealizedException("OPStatement.setBlob");
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		throw new UnrealizedException("OPStatement.setBlob");
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		throw new UnrealizedException("OPStatement.setBlob");
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x == true ? 1 : 0;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtBool);
		}
		paramArray[parameterIndex] = x;
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtInt8);
		}
		paramArray[parameterIndex] = x;
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtBinary);
		}
		paramArray[parameterIndex] = x;
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		throw new UnrealizedException("OPStatement.setCharacterStream");
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		throw new UnrealizedException("OPStatement.setCharacterStream");
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		throw new UnrealizedException("OPStatement.setCharacterStream");
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		parameterIndex--;
		throw new UnrealizedException("OPStatement.setClob");
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		parameterIndex--;
		throw new UnrealizedException("OPStatement.setClob");
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		parameterIndex--;
		throw new UnrealizedException("OPStatement.setClob");
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x.getTime() / 1000;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtDateTime);
		}
		paramArray[parameterIndex] = x;
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		parameterIndex--;
		throw new UnrealizedException("OPStatement.setDate");
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtDouble);
		}
		paramArray[parameterIndex] = x;
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtFloat);
		}
		paramArray[parameterIndex] = x;
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtInt32);
		}
		paramArray[parameterIndex] = x;
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtInt64);
		}
		paramArray[parameterIndex] = x;
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		parameterIndex--;
		throw new UnrealizedException("OPStatement.setNCharacterStream");
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		throw new UnrealizedException("OPStatement.setNCharacterStream");
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		throw new UnrealizedException("OPStatement.setNClob");
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		throw new UnrealizedException("OPStatement.setNClob");
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		throw new UnrealizedException("OPStatement.setNClob");
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		throw new UnrealizedException("OPStatement.setNString");
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		throw new UnrealizedException("OPStatement.setNull");
	}

	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		throw new UnrealizedException("OPStatement.setNull");
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		if(parameterTypeFlag) return;
		//Object有可能是map、list和structure
		byte dataType = 0;
		if(x instanceof Map){
			dataType = OPType.VtMap;
		}else if(x instanceof List){
			//设置字段名和字段类型
			Object o = ((List<?>) x).get(0);
			if(o instanceof Boolean){
				dataType = OPType.BOOL_ARRAY;
			}else if(o instanceof Byte){
				dataType = OPType.INT8_ARRAY;
			}else if(o instanceof Short){
				dataType = OPType.INT16_ARRAY;
			}else if(o instanceof Integer){
				dataType = OPType.INT32_ARRAY;
			}else if(o instanceof Long){
				dataType = OPType.INT64_ARRAY;
			}else if(o instanceof Float){
				dataType = OPType.FLOAT_ARRAY;
			}else if(o instanceof Double){
				dataType = OPType.DOUBLE_ARRAY;
			}else if(o instanceof java.util.Date || o instanceof LocalDateTime){
				dataType = OPType.DATETIME_ARRAY;
			}else if(o instanceof String){
				dataType = OPType.STRING_ARRAY;
			}else if(o instanceof byte[]){
				dataType = OPType.BINARY_ARRAY;
			}else {
				dataType = OPType.VtArray;
			}
		}else {
			dataType = OPType.VtStructure;
		}
		//设置字段名和字段类型
		addColumn(parameterIndex,dataType);

	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		setObject(parameterIndex,x);
//		throw new UnrealizedException("OPStatement.setObject");
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		throw new UnrealizedException("OPStatement.setObject");
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		throw new UnrealizedException("OPStatement.setRef");
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		throw new UnrealizedException("OPStatement.setRowId");
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		throw new UnrealizedException("OPStatement.setSQLXML");
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
		//设置字段名和字段类型
		if(!parameterTypeFlag){
			addColumn(parameterIndex,OPType.VtInt16);
		}
	}

	/**
	 * 设置string类型参数
	 * @param parameterIndex
	 * @param x
	 * @throws SQLException
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		synchronized (this.sqlConnection){
			parameterIndex--;
			if (this.sql == null)
				throw new SQLException("sql语句未初始化");
			if (this.paramArray == null || this.paramArray.length <= parameterIndex)
				throw new SQLException("sql初始化错误");
			//拼接单引号，防止sql注入
			//判断是否需要转义
			int stringLength = x.length();
			boolean needsHexEscape = isEscapeNeededForString(x, stringLength);
			//如果不需要转义则直接在两边拼上单引号
			if(!needsHexEscape){
				StringBuilder quotedString = new StringBuilder(x.length() + 2);
				quotedString.append('\'');
				quotedString.append(x);
				quotedString.append('\'');
				this.paramArray[parameterIndex] = quotedString.toString();
			}else {
				//如果需要转义，则进行转义处理之后再拼接单引号
				StringBuilder buf = new StringBuilder((int) (stringLength * 1.1));
				buf.append('\'');
				for (int i = 0; i < stringLength; ++i) {
					char c = x.charAt(i);
					switch (c) {
						case 0: /* Must be escaped for 'mysql' */
							buf.append('\\');
							buf.append('0');
							break;
						case '\n': /* Must be escaped for logs */
							buf.append('\\');
							buf.append('n');
							break;
						case '\r':
							buf.append('\\');
							buf.append('r');
							break;
						case '\\':
							buf.append('\\');
							buf.append('\\');
							break;
						case '\'':
							buf.append('\\');
							buf.append('\'');
							break;
						case '"': /* Better safe than sorry */
							buf.append('\\');
							buf.append('"');
							break;
						case '\032': /* This gives problems on Win32 */
							buf.append('\\');
							buf.append('Z');
							break;
						case '\u00a5':
						case '\u20a9':
							buf.append('\\');
							buf.append(c);
							break;
						default:
							buf.append(c);
					}
				}
				buf.append('\'');
				this.paramArray[parameterIndex] = buf.toString();
			}

			//设置字段名和字段类型
			if(!parameterTypeFlag){
				addColumn(parameterIndex,OPType.VtString);
			}
		}
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x.getTime();
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		throw new UnrealizedException("OPStatement.setTime");
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x.getTime();
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		throw new UnrealizedException("OPStatement.setTimestamp");
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");
		this.paramArray[parameterIndex] = x;
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		parameterIndex--;
		if (this.sql == null)
			throw new SQLException("sql语句未初始化");
		if (this.paramArray == null || this.paramArray.length <= parameterIndex)
			throw new SQLException("sql初始化错误");

		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		int readLength = 0;
		try {
			for (int n; (n = x.read(b)) != -1;) {
				readLength += n;
				out.append(new String(b, 0, n));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (readLength != length)
			throw new SQLException("数据流错误");
		this.paramArray[parameterIndex] = out.toString();

		out = null;
		b = null;
	}

	private int getSuccessCount(OPResultSet rs) throws OPException, SQLException {
		int successNum = 0;
		while (rs.next()) {
			successNum++;
		}
		return successNum;
	}

	private void throwException(int errNo) throws OPException {
		OPError.throwException(errNo);
	}

	private boolean isEscapeNeededForString(String x, int stringLength) {
		boolean needsHexEscape = false;

		for (int i = 0; i < stringLength; ++i) {
			char c = x.charAt(i);

			switch (c) {
				case 0: /* Must be escaped for 'mysql' */

					needsHexEscape = true;
					break;

				case '\n': /* Must be escaped for logs */
					needsHexEscape = true;

					break;

				case '\r':
					needsHexEscape = true;
					break;

				case '\\':
					needsHexEscape = true;

					break;

				case '\'':
					needsHexEscape = true;

					break;

				case '"': /* Better safe than sorry */
					needsHexEscape = true;

					break;

				case '\032': /* This gives problems on Win32 */
					needsHexEscape = true;
					break;
			}

			if (needsHexEscape) {
				break; // no need to scan more
			}
		}
		return needsHexEscape;
	}

	/**
	 * @Author liuyi
	 * @Description //添加表的列信息
	 * @Date 2021/5/7 14:16
	 * @Param [columnName, columnType]
	 * @return void
	 **/
	private void addColumn(int parameterIndex,byte columnType) throws SQLException{
		String columnName = "";
		try {
			columnName = columnInfoList.get(parameterIndex).getName();
			table.addColumn(columnName,columnType);
		}catch (Exception e){
			e.printStackTrace();
			throw new SQLException("添加列名为"+columnName+"类型为"+columnType+"失败");
		}
	}
	/**
	 * @Author liuyi
	 * @Description //获取token
	 * @Date 2021/5/7 15:10
	 * @Param [isRead]
	 * @return com.magus.opio.Token
	 **/
	private static Token get_token(OPIOConnect op,String cluster,String token_topic) {
		try {
			OPRequest req = new OPRequest(op);
			req.setAction(LtConst.ActionSelect);
			req.setOption(LtConst.PropSubject, token_topic);
			req.setOption(LtConst.DBCluster, cluster);
			req.WriteAndFlush();
			OPResponse res = req.GetResponse();
			String tokenStr = res.getOption(LtConst.SubjectToken);
			if (tokenStr.equals(""))
				throw new OPException("token is nil");
			Algorithm algorithm = Algorithm.HMAC256("magus so excellent");
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(tokenStr);
			String extraInfo = jwt.getClaim("ExtraInfo").asString();
			Token token = JSON.parseObject(extraInfo, Token.class);
			token.setTokenCache(tokenStr);
			res.destroy();
			return token;
		} catch (Exception e) {
			log.error("从lcs获取token失败",e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Author liuyi
	 * @Description //commit
	 * @Date 2021/5/7 15:46
	 * @Param [transId, ip, port]
	 * @return void
	 **/
	private void data_commit(long transId,OPIOConnect writeOP) {
		try {
			OPRequest req = new OPRequest(writeOP);
			req.setID(transId);
			req.setAction(LtConst.ActionCommit);
			req.WriteAndFlush();
			OPResponse res = req.GetResponse();
			res.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
