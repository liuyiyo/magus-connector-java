package com.magus.jdbc;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;
public class Driver implements java.sql.Driver {

	private static final String Flage = "/";

	private static final String US = ":";

	private static final String QM = "\\?";

	private static final String AND = "&";

	private static final String EQUAL = "=";

	private static final String URL_PREFIX_OPENPLANT = "jdbc:openplant://";

	private static final String URL_PREFIX_Lightning = "jdbc:lightning://";

	private static final String DEFAULT_HOST = "localhost";

	private static final String DEFAULT_PORT = "8200";

	private static final String DEFAULT_USERNAME = "sis";

	private static final String DEFAULT_PASSWORD = "openplant";

	public static final String HOST_PROPERTY_KEY = "HOST";

	public static final String PORT_PROPERTY_KEY = "PORT";

	public static final String MODEL_PROPERTY_KEY = "MODEL";

	public static final String DB_PROPERTY_KEY = "db";

	public static final String USER_PROPERTY_KEY = "user";

	public static final String PASSWORD_PROPERTY_KEY = "password";

	public static final String CLUSTER_PROPERTY_KEY = "cluster";

	private Properties info = null;

	static {
		try {
			DriverManager.registerDriver(new Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean acceptsURL(String url) throws SQLException {
		return (parseURL(url, info)) != null;
	}

	public Connection connect(String url, Properties info) throws SQLException {
		Properties props = null;
		if ((props = parseURL(url, info)) == null) {
			return null;
		}
		SQLConnection newConn = new SQLConnection(host(props), port(props), db(props), user(props), password(props), info);
		if (newConn.test()) {
			return newConn;
		} else {
			return null;
		}
	}

	public String host(Properties props) {
		return props.getProperty(HOST_PROPERTY_KEY, DEFAULT_HOST);
	}

	public String user(Properties props) {
		return props.getProperty(USER_PROPERTY_KEY, DEFAULT_USERNAME);
	}

	public String password(Properties props) {
		return props.getProperty(PASSWORD_PROPERTY_KEY, DEFAULT_PASSWORD);
	}

	public int port(Properties props) {
		return Integer.parseInt(props.getProperty(PORT_PROPERTY_KEY, DEFAULT_PORT));
	}

	public String db(Properties props) {
		return props.getProperty(DB_PROPERTY_KEY);
	}

	public Properties parseURL(String url, Properties defaults) throws SQLException {
		if(defaults==null) defaults = new Properties();
		if (url == null||"".equals(url)) {
			throw new SQLException("url不能为空");
		}
		int beginningOfSlashes = url.indexOf("//");
		url = url.substring(beginningOfSlashes + 2);
		if(url==null||"".equals(url)){
			throw new SQLException("url格式错误，请检查//是否存在，若存在，请检查//前后的语法是否准确");
		}
		String[] urlStrs = url.split(Flage);
		String host = urlStrs[0];
		//设置ip和端口
		try {
			String[] hoststr = host.split(US);
			defaults.setProperty(HOST_PROPERTY_KEY, hoststr[0]);
			defaults.setProperty(PORT_PROPERTY_KEY, hoststr[1]);
		}catch (Exception e){
			e.printStackTrace();
			throw new SQLException("url格式错误，请检查ip和端口格式");
		}
		//设置dbName和其他参数
		try {
			String dbNameAndOtherParamsStr = urlStrs[1];
			String[] dbNameAndOtherParamsArray = dbNameAndOtherParamsStr.split(QM);
			defaults.setProperty(DB_PROPERTY_KEY,dbNameAndOtherParamsArray[0]);
			if(dbNameAndOtherParamsArray.length>1) {
				String otherStr = dbNameAndOtherParamsArray[1];
				String[] paramArray = otherStr.split(AND);
				for (String param : paramArray) {
					String[] childParamArray = param.split(EQUAL);
					String paramkey = childParamArray[0];
					String paramValue = childParamArray[1];
					switch (paramkey) {
						case CLUSTER_PROPERTY_KEY:
							defaults.setProperty(CLUSTER_PROPERTY_KEY, paramValue);
							break;
						default:
							throw new SQLException("参数" + paramkey + "不合法");
					}

				}
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new SQLException("url格式错误，请检查数据名称以及?后的参数格式");
		}
		this.info = defaults;
		return defaults;
	}

	public int getMajorVersion() {
		return 1;
	}

	public int getMinorVersion() {
		return 0;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		if (info == null) {
			info = new Properties();
		}

		info = parseURL(url, info);
		DriverPropertyInfo hostProp = new DriverPropertyInfo(HOST_PROPERTY_KEY, info.getProperty(HOST_PROPERTY_KEY));
		hostProp.required = true;
		hostProp.description = "host";

		DriverPropertyInfo portProp = new DriverPropertyInfo(PORT_PROPERTY_KEY, info.getProperty(PORT_PROPERTY_KEY, DEFAULT_PORT));
		portProp.required = false;
		portProp.description = "port";

		DriverPropertyInfo userProp = new DriverPropertyInfo(USER_PROPERTY_KEY, info.getProperty(USER_PROPERTY_KEY));
		userProp.required = true;
		userProp.description = "user";

		DriverPropertyInfo passwordProp = new DriverPropertyInfo(PASSWORD_PROPERTY_KEY, info.getProperty(PASSWORD_PROPERTY_KEY));
		passwordProp.required = true;
		passwordProp.description = "password";

		DriverPropertyInfo[] dpi = new DriverPropertyInfo[4];

		dpi[0] = hostProp;
		dpi[1] = portProp;
		dpi[2] = userProp;
		dpi[3] = passwordProp;

		return dpi;
	}

	public boolean jdbcCompliant() {
		return false;
	}

}
