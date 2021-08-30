package com.magus.opio.net;

import com.magus.opio.OPException;
import com.magus.jdbc.OPResultSet;
import com.magus.opio.OPConst;
import com.magus.opio.OPConstant;
import com.magus.opio.dto.OPRequest;
import com.magus.opio.dto.OPResponse;
import com.magus.opio.dto.OPTable;
import com.magus.opio.io.OPInputStream;
import com.magus.opio.io.OPOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class OPIOConnect implements IOPConnect {

	protected String host;
	protected int port;
	protected String user;
	protected String password;
	protected int timeout;
	protected Random rand;
	private final static byte defaultCompressMod = OPConst.ZIP_MODEL_Frame;
	protected byte compressMod = 0;
	private boolean isConnected = false;
	private OPOutputStream out;
	private OPInputStream in;
	private Socket socket = new Socket();
	private String tokenCache;

	public String getTokenCache() {
		return tokenCache;
	}

	public void setTokenCache(String tokenCache) {
		this.tokenCache = tokenCache;
	}

	protected static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	protected static final String KEYS_ERROR_MSG = "Keys Type Error!  type not in ([]int,[]long,[]String);"
			+ " ID type is []int; GN type is []String; UD type is []long;";
	// buffer get/put
	private static void putShort(byte[] buf, int off, short value) {
		ByteBuffer b = ByteBuffer.wrap(buf, off, 2);
		b.putShort(value);
	}

	private static void putInt(byte[] buf, int off, int value) {
		ByteBuffer b = ByteBuffer.wrap(buf, off, 4);
		b.putInt(value);
	}

	private static int getInt(byte buf[], int i) {
		return (buf[i] << 24) + ((buf[i + 1] & 0xff) << 16)
				+ ((buf[i + 2] & 0xff) << 8) + (buf[i + 3] & 0xff);
	}

	private static String login(OPInputStream in, OPOutputStream out,
			String user, String password) throws OPException {
		try {
			byte[] buf = new byte[100];
			byte[] reply = null;
			int passwd_len = 0;
			int rv = in.read(buf);
			if (rv != 100) {
				throw new OPException("login phase 1 failed");
			}
			// server: SERVER_VERSION(60) + session(4) + SCRAMBLE + 16
			byte[] seed = Arrays.copyOfRange(buf, 64, 84);
			if (user == null)
				user = "guest";
			if (password != null && password.length() != 0) {
				try {
					reply = scramble(password, seed);
					passwd_len = reply.length;
				} catch (NoSuchAlgorithmException e) {
					throw new OPException("no digest algorithm");
				}
			}
			// db(16) + 2;
			byte[] to = new byte[100];
			// client id
			int thread_id = Thread.currentThread().hashCode();
			putInt(to, 40, thread_id);
			// user
			byte[] userb = user.getBytes();
			int len = (Math.min(userb.length, 16));
			System.arraycopy(userb, 0, to, 44, len);
			// password
			putShort(to, 60, (short) passwd_len);
			if (passwd_len > 0) {
				System.arraycopy(reply, 0, to, 62, passwd_len);
			}
			out.write(to);
			out.flush();
			// recv status
			rv = in.read(buf);
			if (rv != 16) {
				throw new OPException("login phase 2 failed");
			}
			rv = getInt(buf, 8);
			if (rv != 0) {
				throw new OPException("login is refused by server");
			}
			byte[] b = new byte[4];
			System.arraycopy(buf, 4, b, 0, 4);
			InetAddress a = InetAddress.getByAddress(b);
			String peer = a.getHostAddress();
			return peer;
		} catch (Exception ex) {
			throw new OPException(ex.getMessage());
		}

	}

	private static byte[] scramble(String password, byte[] seed)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1"); //$NON-NLS-1$
		byte[] passwordHashStage1 = md.digest(password.getBytes());
		md.reset();
		byte[] passwordHashStage2 = md.digest(passwordHashStage1);
		md.reset();
		// byte[] seedAsBytes = seed.getBytes(); // for debugging
		md.update(seed);
		md.update(passwordHashStage2);
		byte[] toBeXord = md.digest();
		int numToXor = toBeXord.length;
		for (int i = 0; i < numToXor; i++) {
			toBeXord[i] = (byte) (toBeXord[i] ^ passwordHashStage1[i]);
		}
		return toBeXord;
	}

	/**
	 * @param host
	 *            数据库IP
	 * @param port
	 *            端口
	 * @param timeout
	 *            数据库连接超时
	 * @param user
	 *            数据库账号
	 * @param password
	 *            数据库密码
	 * @throws OPException
	 * @throws OPException
	 */
	public OPIOConnect(String host, int port, int timeout, String user,
			String password) throws OPException {
		if (!isConnected) {
			initConn(host, port, timeout, user, password, defaultCompressMod);
		}
	}

	/**
	 * @param host
	 *            数据库IP
	 * @param port
	 *            端口
	 * @param timeout
	 *            数据库连接超时
	 * @param user
	 * @param password
	 * @throws OPException
	 */
	public OPIOConnect(String host, int port, int timeout, String user,
			String password, byte compressMod) throws OPException {
		if (!isConnected) {
			initConn(host, port, timeout, user, password, compressMod);
		}
	}

	public OPIOConnect(String host, int port, int timeout) throws OPException {
		if (!isConnected) {
			initConn(host, port, timeout, "", "", defaultCompressMod);
		}
	}

	public OPIOConnect(String host, int port, int timeout, byte compressMod) throws OPException {
		if (!isConnected) {
			initConn(host, port, timeout, "", "", compressMod);
		}
	}


	public OPIOConnect(String host, int port, int timeout, byte compressMod,String tokenCache) throws OPException {
		this.tokenCache = tokenCache;
		if (!isConnected) {
			initConn(host, port, timeout, "", "", compressMod);
		}
	}

	private void initConn(String host, int port, int timeout, String user,
			String password, byte compressMod) throws OPException {
		Socket socket = new Socket();
		InetAddress inetaddr;
		try {
			inetaddr = InetAddress.getByName(host);
			if (timeout < 60000)
				timeout = 60000;
			socket.connect(new InetSocketAddress(inetaddr, port), timeout);
			//socket.setSoTimeout(timeout * 1000); // read 操作时的超时
			// 初始化网络流
			this.in = new OPInputStream(socket.getInputStream());
			this.out = new OPOutputStream(socket.getOutputStream());
			// 判断是否需要登录
			if (!user.equals("") && !password.equals("")) {
				String rv = login(in, out, user, password);
			}
			// 设置网络IO 压缩
			out.setCompressMod(compressMod);
			this.host = host;
			this.port = port;
			//设置超时时间
			this.timeout = timeout;
			this.user = user;
			this.password = password;
			rand = new Random();
			this.socket = socket;
			isConnected = true;
		} catch (Exception e) {
			throw new OPException(e.getMessage());
		}
	}

	public boolean resetSession() throws OPException {
		synchronized (this) {
			initConn(host, port, timeout, user, password, compressMod);
		}
		return true;
	}

	public Date getServerTime() throws OPException {
		String sql = "select strftime('%Y-%m-%d %H:%M:%f','now','localtime') from database limit 1";
		OPResultSet rs = execSQL(sql);
		Date data = new Date(0);
		try {
			while (rs.next()) {
				data = sdf.parse(rs.getObject(1).toString());
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}
		return data;
	}

	public void close() {
		destroy();
	}

	public OPTable createTable(String tableName) {
		return new OPTable(tableName);
	}

	public OPResultSet execSQL(String sql) throws OPException {
		return execSQL(sql, null);
	}

	public synchronized OPRequest NewOPRequest() {
		OPRequest request = new OPRequest(in, out);
		return request;
	}

	public synchronized OPResultSet execSQL(String sql, Object keys)
			throws OPException {
		if (isActive()) {
			OPRequest request = new OPRequest(in, out);
			request.setID(rand.nextInt());
			request.setService(OPConst.SERVICE_OPENPLANT);
			request.setAction(OPConst.ACTION_EXECSQL);
			request.setSQL(sql);
			OPResponse resp = request.execute();
			//销毁OPRequest
			return new OPResultSet(resp);
		} else {
			throw new OPException(" conn  is bad !  ");
		}
	}

	public OPResultSet remove(String tablename, Object keys) throws OPException {
		OPRequest request = new OPRequest(in, out);
		// 设置执行SQL
		request.setAction(OPConst.ACTION_DELETE);
		request.setOption(OPConst.KEY_TABLE, tablename);
		if (keys != null) {
			if (keys instanceof int[]) {
				request.setIDList((int[]) keys);
			} else if (keys instanceof long[]) {
				request.setUDList((long[]) keys);
			} else if (keys instanceof String[]) {
				request.setNameList((String[]) keys);
			} else {
				throw new OPException(KEYS_ERROR_MSG);
			}
		}
		OPResponse resp = request.execute();
		return new OPResultSet(resp);
	}

	public OPResultSet find(OPTable table, Object keys) throws OPException {
		OPRequest request = new OPRequest(in, out);
		request.setAction(OPConst.ACTION_SELECT);
		request.setOption(OPConst.KEY_TABLE, table.getTableName());
		request.setTable(table);
		if (keys != null) {
			if (keys instanceof int[]) {
				request.setIDList((int[]) keys);
			} else if (keys instanceof long[]) {
				request.setUDList((long[]) keys);
			} else if (keys instanceof String[]) {
				request.setNameList((String[]) keys);
			} else {
				throw new OPException(KEYS_ERROR_MSG);
			}
		}
		try {
			OPResponse resp = request.execute();
			return new OPResultSet(resp);
		} catch (Exception e) {
			boolean conn = resetSession();
			if (conn) {
				throw new OPException(" find table " + table.getTableName()
						+ " IO error!  ResetSession success!");
			} else {
				throw new OPException(" find table " + table.getTableName()
						+ " IO error! Conn close!");
			}
		}
	}

	public OPResultSet replace(OPTable table) throws OPException {
		return execTable(table, OPConst.ACTION_REPLACE);
	}

	public OPResultSet insert(OPTable table) throws OPException {
		return execTable(table, OPConst.ACTION_INSERT);
	}

	public OPResultSet update(OPTable table) throws OPException {
		return execTable(table, OPConst.ACTION_UPDATE);
	}

	protected OPResultSet execTable(OPTable table, String action)
			throws OPException {
		OPRequest request = new OPRequest(in, out);
		request.setAction(action);
		request.setService(OPConst.SERVICE_OPENPLANT);
		request.setOption(OPConst.KEY_TABLE, table.getTableName());
		request.setTable(table);
		OPResponse resp = request.execute();
		return new OPResultSet(resp);
	}

	public OPResultSet update(OPTable table, Object keys) throws OPException {
		OPRequest request = new OPRequest(in, out);
		request.setAction(OPConst.ACTION_UPDATE);
		request.setOption(OPConst.KEY_TABLE, table.getTableName());
		request.setTable(table);
		if (keys != null) {
			if (keys instanceof int[]) {
				request.setIDList((int[]) keys);
			} else if (keys instanceof long[]) {
				request.setUDList((long[]) keys);
			} else if (keys instanceof String[]) {
				request.setNameList((String[]) keys);
			} else {
				throw new OPException(KEYS_ERROR_MSG);
			}
		}
		OPResponse resp = request.execute();
		return new OPResultSet(resp);
	}

	public void destroy() {
		synchronized (this) {
			try {
				if(socket == null)
					return;
				socket.close();
				in.close();
				out.close();
				socket = null;
				in = null;
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String toString() {
		return "host:" + host + "\tport:" + port + "\tuser:" + user
				+ "\tpassword:" + password;
	}

	public OPOutputStream getOut() {
		return out;
	}

	public OPInputStream getIn() {
		return in;
	}

	public synchronized boolean isActive() {
		try {
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(OPConstant.statusHeader);
			outputStream.flush();
			inputStream.read();
		} catch (Exception e) {
			if (!(e instanceof SocketTimeoutException)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws OPException,
			InterruptedException {
		System.out.println("GO ....");
		OPIOConnect conn = new OPIOConnect("127.0.0.1", 9800, 60, "sis",
				"openplant");
		System.out.println("success");
		Thread.sleep(1000);
		System.out.println(conn);
		conn.close();
	}
}
