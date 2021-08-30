package com.magus.jdbc;

import com.magus.opio.OPConf;
import com.magus.opio.dto.OPDataset;
import com.magus.opio.pool.OPConnectManage;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLConnection implements Connection {
	// private static final String PARAMETERS_OF_UNREALIZED =
	// "未实现方法参数，请调用无参方法构建对象";
	private static final String EMPTY_IMPLEMENTATION = "方法空实现";

	private static Logger log = Logger.getLogger(SQLConnection.class.getName());
	private static final int TIME_SECONDS = 60;
	private static int MinIdle = 2;
	private static int MaxIdle = 50;

	private String host;
	private int port;
	private String user;
	private String password;
	private String db;
	private Properties info;
	private boolean closed = false;
	private OPConnectManage opcm = null;
	private OPConf config = null;

	static {
		log.setLevel(Level.ALL);
	}

	public SQLConnection(String host, int port, String db, String user, String password, Properties info) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.password = password;
		this.info = info;
		config = new OPConf(host, port, user, password, true, TIME_SECONDS);
		opcm = OPConnectManage.getInstance(config, MaxIdle, MinIdle);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void abort(Executor executor) throws SQLException {
		throw new UnrealizedException("SQLConnection.abort");

	}

	public void clearWarnings() {
	}

	/**
	 * @Author liuyi
	 * @Description //清空connection
	 * @Date 2021/5/12 16:14
	 * @Param []
	 * @return void
	 **/
	public void close() throws SQLException {
		synchronized (this) {
			if (!closed) {
				this.closed = true;
				this.host = null;
				this.port = 0;
				this.db = null;
				this.user = null;
				this.password = null;
				this.info = null;
				config = null;
				opcm = null;
			}
		}
	}

	public void commit() throws SQLException {
		log.warning(EMPTY_IMPLEMENTATION);
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new UnrealizedException("SQLConnection.createArrayOf");

	}

	public Blob createBlob() throws SQLException {
		throw new UnrealizedException("SQLConnection.createBlob");

	}

	public Clob createClob() throws SQLException {
		throw new UnrealizedException("SQLConnection.createClob");

	}

	public NClob createNClob() throws SQLException {
		throw new UnrealizedException("SQLConnection.createClob");
	}

	public SQLXML createSQLXML() throws SQLException {
		throw new UnrealizedException("SQLConnection.createSQLXML");

	}

	public Statement createStatement() throws SQLException {
		return OPPreparedStatement.createStatement(this);

	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return OPPreparedStatement.createStatement(this);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return OPPreparedStatement.createStatement(this);
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		throw new UnrealizedException("SQLConnection.createStruct");

	}

	public boolean getAutoCommit() throws SQLException {
		return true;
	}

	public String getCatalog() throws SQLException {
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		throw new UnrealizedException("SQLConnection.getClientInfo");

	}

	public String getClientInfo(String name) throws SQLException {
		if (name.equals("url"))
			return this.host;
		else if (name.equals("user"))
			return this.user;
		else if (name.equals("password"))
			return this.password;
		else if (name.equals("port"))
			return "" + this.port;
		else if (name.equals("mode"))
			return this.db;
		else
			throw new SQLException("未找到匹配参数");
	}

	public int getHoldability() {
		return 0;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return new OPDatabaseMetaData(this);
	}

	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	public String getSchema() throws SQLException {
		throw new UnrealizedException("SQLConnection.getSchema");
	}

	public int getTransactionIsolation() {
		return 0;
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new UnrealizedException("SQLConnection.getTypeMap");

	}

	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	public boolean isClosed() throws SQLException {
		return closed;
	}

	public boolean isReadOnly() {
		return false;
	}

	public boolean isValid(int timeout) throws SQLException {
		return !closed;
	}

	public String nativeSQL(String sql) throws SQLException {
		throw new UnrealizedException("SQLConnection.nativeSQL");

	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new UnrealizedException("SQLConnection.prepareCall");

	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new UnrealizedException("SQLConnection.prepareCall2");

	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new UnrealizedException("SQLConnection.prepareCall3");

	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return OPPreparedStatement.createPretreatment(this,sql);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		// throw new UnrealizedException("SQLConnection.prepareStatement2");
		return OPPreparedStatement.createPretreatment(this,sql);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		// throw new UnrealizedException("SQLConnection.prepareStatement3");
		return OPPreparedStatement.createPretreatment(this,sql);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		// throw new UnrealizedException("SQLConnection.prepareStatement4");
		return OPPreparedStatement.createPretreatment(this,sql);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		// throw new UnrealizedException("SQLConnection.prepareStatement5");
		return OPPreparedStatement.createPretreatment(this,sql);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		// throw new UnrealizedException("SQLConnection.prepareStatement6");
		return OPPreparedStatement.createPretreatment(this,sql);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new UnrealizedException("SQLConnection.releaseSavepoint");

	}

	public void rollback() throws SQLException {
		log.warning(EMPTY_IMPLEMENTATION);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		throw new UnrealizedException("SQLConnection.rollback");

	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		// log.warning(EMPTY_IMPLEMENTATION);
	}

	public void setCatalog(String catalog) throws SQLException {
		throw new UnrealizedException("SQLConnection.setCatalog");

	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		throw new SQLClientInfoException();
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		throw new SQLClientInfoException();
	}

	public void setHoldability(int holdability) throws SQLException {
		throw new UnrealizedException("SQLConnection.setHoldability");
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		log.warning(EMPTY_IMPLEMENTATION);
	}

	public void setReadOnly(boolean readOnly) {
	}

	public Savepoint setSavepoint() throws SQLException {
		throw new UnrealizedException("SQLConnection.setSavepoint");
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		throw new UnrealizedException("SQLConnection.setSavepoint");

	}

	public void setSchema(String schema) throws SQLException {
		throw new UnrealizedException("SQLConnection.setSchema");

	}

	public void setTransactionIsolation(int level) throws SQLException {
		throw new UnrealizedException("SQLConnection.setTransactionIsolation");

	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new UnrealizedException("SQLConnection.setTypeMap");

	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnrealizedException("SQLConnection.isWrapperFor");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnrealizedException("SQLConnection.unwrap");
	}

	public String toString() {
		return super.toString();
	}

	public OPConnectManage getOPConnectManage() {
		return opcm;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Properties getInfo() {
		return info;
	}

	public void setInfo(Properties info) {
		this.info = info;
	}

	public boolean test() throws SQLException {
		return opcm.test();
	}

}
