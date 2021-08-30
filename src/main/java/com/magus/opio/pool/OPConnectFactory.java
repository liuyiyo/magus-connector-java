package com.magus.opio.pool;

import com.magus.opio.OPConf;
import com.magus.opio.OPException;
import com.magus.opio.net.IOPConnect;
import com.magus.opio.net.OPIOConnect;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class OPConnectFactory implements PooledObjectFactory<IOPConnect> {

	private OPConf conf;

	public OPConnectFactory(OPConf conf) {
		super();
		this.conf = conf;
	}

	public void activateObject(PooledObject<IOPConnect> p) throws Exception {
		synchronized (this) {
			IOPConnect conn = p.getObject();
			if (!conn.isActive()) {
				boolean isConnected = conn.resetSession();
				if (!isConnected) {
					destroyObject(p);
					throw new OPException("重连失败...");
				}

			}
		}
	}

	public void passivateObject(PooledObject<IOPConnect> p) throws Exception {
		// do nothing
	}

	public void destroyObject(PooledObject<IOPConnect> p) throws Exception {
		synchronized (this) {
			p.getObject().destroy();
		}
	}

	public PooledObject<IOPConnect> makeObject() throws Exception {
		synchronized (this) {
			IOPConnect conn;
			conn = new OPIOConnect(conf.getIp(), conf.getPort(), conf.getTimeout(), conf.getUserName(), conf.getPassword());
			if (conn.isActive()) {
				return new DefaultPooledObject<IOPConnect>(conn);
			} else {
				throw new OPException("Connect Server error, IP：" + conf.getIp() + " port:" + conf.getPort() + " user:" + conf.getUserName());
			}
		}
	}

	public boolean validateObject(PooledObject<IOPConnect> p) {
		synchronized (this) {
			return p.getObject().isActive();
		}

	}

}
