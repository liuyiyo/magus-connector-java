package com.magus.opio;

public class OPConf {
	private String ip;
	private int port;
	private String userName;
	private String password;
	private boolean compress = true;
	private int timeout = 600;

	private int maxIdle;
	private int minIdle;


	public OPConf(String ip, int port, String userName, String password) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}

	public OPConf(String ip, int port, String userName, String password, boolean compress) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.compress = compress;
	}

	public OPConf(String ip, int port, String userName, String password, boolean compress, int timeOut) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.compress = compress;
		this.timeout = timeOut;
	}

	public OPConf(String ip) {
		this.ip = ip;
	}

	public OPConf(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isCompress() {
		return compress;
	}

	public boolean getCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public String toString() {
		return "OPConf [ip=" + ip + ", port=" + port + ", userName=" + userName + ", password=" + password + ", compress=" + compress + ", timeout=" + timeout + "]";
	}
}
