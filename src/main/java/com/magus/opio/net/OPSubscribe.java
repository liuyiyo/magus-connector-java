package com.magus.opio.net;

import com.magus.opio.OPException;
import com.magus.opio.io.OPCallback;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

/**
 */
public class OPSubscribe extends Subscribe implements OPCallback {

	private HashSet<Long> idSet = new HashSet<Long>();
	private HashSet<String> tagSet = new HashSet<String>();

	private long lastChange = 0;

	// 刷新变量锁
	private boolean runingCheck;

	// 重连变量锁标记
	private boolean reconnectionLock = false;

	@SuppressWarnings("static-access")
	public void run() {
		lastChange = System.currentTimeMillis();
		while (runingCheck) {
			// 超过1分钟没有收到任何消息
			if (System.currentTimeMillis() - this.lastChange > 60000) {
				System.err.println(" long time no message received, reset subscription ");
				this.reconnection();
			} else {
				try {
					this.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void makeSub(IOPConnect conn, String tableName, String[] keyList, SubscribeResultSet callback) throws OPException {
		synchronized (this) {
		}
	}

	private void makeSub(IOPConnect conn, String tableName, long[] keyList, SubscribeResultSet callback) throws OPException {
		synchronized (this) {

		}
	}

	private void cacheConf(IOPConnect conn, String tableName) {
		this.ip = ((OPIOConnect) conn).getHost();
		this.port = ((OPIOConnect) conn).getPort();
		this.user = ((OPIOConnect) conn).getUser();
		this.pwd = ((OPIOConnect) conn).getPassword();
		this.tableName = tableName;
	}

	public OPSubscribe(IOPConnect conn, String tableName, String[] keyList, SubscribeResultSet callback) throws OPException {
		if (keyList.length == 0) {
			throw new OPException(" Subscribe keyList is nil");
		}
		makeSub(conn, tableName, keyList, callback);
	}

	public OPSubscribe(IOPConnect conn, String tableName, long[] keyList, SubscribeResultSet callback) throws OPException {
		if (keyList.length == 0) {
			throw new OPException(" Subscribe keyList is nil");
		}
		makeSub(conn, tableName, keyList, callback);
	}

	@SuppressWarnings("unchecked")
	public OPSubscribe(IOPConnect conn, String tableName, String sql, SubscribeResultSet callback) throws OPException, SQLException {
		synchronized (this) {

		}
	}

	public OPSubscribe(IOPConnect conn, String tableName, List<?> keyList, SubscribeResultSet callback) throws OPException {
		if (keyList.get(0) instanceof String) {
			String[] tags = new String[keyList.size()];
			for (int i = 0; i < keyList.size(); i++) {
				tags[i] = (String) keyList.get(i);
			}
			makeSub(conn, tableName, tags, callback);
		} else if (keyList.get(0) instanceof Integer) {
			long[] ids = new long[keyList.size()];
			for (int i = 0; i < keyList.size(); i++) {
				ids[i] = (Integer) keyList.get(i);
			}
			makeSub(conn, tableName, ids, callback);
		} else if (keyList.get(0) instanceof Long) {
			long[] ids = new long[keyList.size()];
			for (int i = 0; i < keyList.size(); i++) {
				ids[i] = (Long) keyList.get(i);
			}
			makeSub(conn, tableName, ids, callback);
		} else {
			throw new OPException("unsupport key type " + keyList.get(0).getClass().getName());
		}

	}

	private Boolean subChange(String[] keyList, int type) throws OPException {
		if (keyList == null || keyList.length <= 0) {
			return false;
		}
		try {
			lock.lock();
			if (type == subscription) {
				for (String tag : keyList) {
					tagSet.add(tag);
				}
			} else {
				for (String tag : keyList) {
					tagSet.remove(tag);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}finally {
			lock.unlock();
		}
		return true;
	}

	private Boolean subChange(long[] keyList, int type) throws OPException {
		if (keyList == null || keyList.length <= 0) {
			return false;
		}
		try {
			lock.lock();
			if (type == subscription) {
				for (long id : keyList) {
					idSet.add(id);
				}
			} else {
				for (long id : keyList) {
					idSet.remove(id);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new OPException(e.getMessage());
		}finally {
			lock.unlock();
		}
		return true;
	}

	public Boolean unsubscribe(String[] keyList) throws OPException {
		synchronized (this) {
			return subChange(keyList, unsubscribe);
		}
	}

	public Boolean unsubscribe(long[] keyList) throws OPException {
		synchronized (this) {
			return subChange(keyList, unsubscribe);
		}
	}

	public Boolean subscription(String[] keyList) throws OPException {
		synchronized (this) {
			return subChange(keyList, subscription);
		}
	}

	public Boolean subscription(long[] keyList) throws OPException {
		synchronized (this) {
			return subChange(keyList, subscription);
		}
	}

	public void onEvent(long response) {
		synchronized (this) {
			if (response == 0) {
				System.out.println("OPSubscribe response error");
				boolean isClose = subscribeResultSet.onError(-100, new OPException("OPSubscribe response error"), this);
				if (isClose) {
					this.close();
				} else {
					this.reconnection();
				}
				return;
			}
		}
	}

	private int reconnection() {
		synchronized (this) {
			if (!reconnectionLock) {
				reconnectionLock = true;
			}
		}
		return 0;
	}

	public int status() {
		synchronized (this) {
		}
		return port;
	}

	@SuppressWarnings("deprecation")
	public void close() {
		synchronized (this) {

		}
	}
}
