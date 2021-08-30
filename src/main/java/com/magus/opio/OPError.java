package com.magus.opio;

import com.magus.opio.OPException;


public class OPError {

	public static final int ERROR_ERROR = -1;
	public static final int ERROR_TIMEOUT = -2;
	public static final int ERROR_EOF = -3;
	public static final int ERROR_MEMORY = -96;
	public static final int ERROR_NET_IO = -97;
	public static final int ERROR_NET_CLOSED = -98;
	public static final int ERROR_NET_CONNECT = -99;
	public static final int ERROR_COMMAND = -101;
	public static final int ERROR_EXIST = -102;
	public static final int ERROR_NEXIST = -103;
	public static final int ERROR_DUP_KEY = -104;
	public static final int ERROR_READ = -105;
	public static final int ERROR_WRITE = -108;
	public static final int ERROR_LIMIT = -106;
	public static final int ERROR_FOREIGN_KEY = -107;
	public static final int ERROR_ACCESS = -109;
	public static final int ERROR_BUSY = -111;
	public static final int ERROR_INVALID_TYPE = -112;
	public static final int ERROR_PARAM = -113;
	public static final int ERROR_OUTDATED = -114;
	public static final int ERROR_LOCKED = -115;
	public static final int ERROR_NOT_INIT = -116;
	public static final int ERROR_PARTIAL = -117;

	public static void throwException(int errNo) throws OPException {
		switch (errNo) {
		case OPError.ERROR_ERROR:
			throw new OPException("未知错误");
		case OPError.ERROR_TIMEOUT:
			throw new OPException("超时");
		case OPError.ERROR_EOF:
			throw new OPException("流已关闭");
		case OPError.ERROR_MEMORY:
			throw new OPException("内存不足");
		case OPError.ERROR_NET_IO:
			throw new OPException("网络通讯I/O错误");
		case OPError.ERROR_NET_CLOSED:
			throw new OPException("网络通讯I/O已关闭");
		case OPError.ERROR_NET_CONNECT:
			throw new OPException("网络通讯无法连接");
		case OPError.ERROR_COMMAND:
			throw new OPException("命令不支持");
		case OPError.ERROR_EXIST:
			throw new OPException("资源已存在");
		case OPError.ERROR_NEXIST:
			throw new OPException("资源不存在");
		case OPError.ERROR_DUP_KEY:
			throw new OPException("关键字重复");
		case OPError.ERROR_READ:
			throw new OPException("I/O读错误");
		case OPError.ERROR_WRITE:
			throw new OPException("I/O写错误");
		case OPError.ERROR_LIMIT:
			throw new OPException("容量限制");
		case OPError.ERROR_FOREIGN_KEY:
			throw new OPException("引用错误");
		case OPError.ERROR_ACCESS:
			throw new OPException("操作不允许");
		case OPError.ERROR_BUSY:
			throw new OPException("系统忙");
		case OPError.ERROR_INVALID_TYPE:
			throw new OPException("类型不匹配");
		case OPError.ERROR_PARAM:
			throw new OPException("参数错误");
		case OPError.ERROR_OUTDATED:
			throw new OPException("数据过时");
		case OPError.ERROR_LOCKED:
			throw new OPException("资源被锁定");
		case OPError.ERROR_NOT_INIT:
			throw new OPException("未初始化");
		case OPError.ERROR_PARTIAL:
			throw new OPException("部分错误");
		default:
			throw new OPException("未知错误");
		}
	}
}
