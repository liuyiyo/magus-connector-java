package com.magus.opio.net;

import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Subscribe extends Thread {

	protected static final int subscription = 1;
	protected static final int unsubscribe = 0;

	protected static final double SKEWING = 0.0005;
	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected SubscribeResultSet subscribeResultSet = null;
	protected String ip = "";
	protected int port = 0;
	protected String user = "";
	protected String pwd = "";
	protected String tableName = "";

	protected Lock lock = new ReentrantLock();


}
