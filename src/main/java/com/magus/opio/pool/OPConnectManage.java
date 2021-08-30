package com.magus.opio.pool;

import com.magus.opio.OPConf;
import com.magus.opio.OPException;
import com.magus.opio.OPConf;
import com.magus.opio.net.IOPConnect;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OPConnectManage {

    private static Map<String, OPConnectManage> connectManageMap = new ConcurrentHashMap<>();

    private GenericObjectPool<IOPConnect> opConnectPool;

    private static long TIMEOUT = 1000 * 10;

    private Integer max = 50;
    private Integer min = 1;
    private OPConf opConf;

    // TODO 临时代码，用于测试结构
    private void init(OPConf opConf, int maxIdle, int minIdle) {
        this.opConf = opConf;
        min = minIdle;
        max = maxIdle;
        initConnectPool();
    }

    private void initConnectPool() {
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        // TODO 连接池设定还没开发设定接口
        conf.setMaxIdle(max);
        conf.setMinIdle(min);
        conf.setMaxTotal(max);
        conf.setMaxWaitMillis(TIMEOUT);
        PooledObjectFactory<IOPConnect> factory = new OPConnectFactory(opConf);
        opConnectPool = new GenericObjectPool<IOPConnect>(factory, conf);

        // tp 2018年7月10日09:43:45
        // 添加内存泄漏检测，10分钟链接不归还，认为链接对象失效。
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        abandonedConfig.setRemoveAbandonedTimeout(60 * 10);
        opConnectPool.setAbandonedConfig(abandonedConfig);
        opConnectPool.setTimeBetweenEvictionRunsMillis(5000); // 5秒运行一次维护任务
    }

    private OPConnectManage(OPConf conf, int maxIdle, int minIdle) {
        init(conf, maxIdle, minIdle);
    }

    public synchronized static OPConnectManage getInstance(OPConf conf, int maxIdle, int minIdle) {

        String key = conf.getIp() + conf.getPort() + conf.getUserName() + conf.getPassword();
        OPConnectManage opm = connectManageMap.get(key);
        if (opm == null) {
            opm = new OPConnectManage(conf, maxIdle, minIdle);
            connectManageMap.put(key, opm);
        } else {
            opm.update(maxIdle, minIdle);
            connectManageMap.put(key, opm);
        }
        return opm;
    }

    private synchronized void update(int maxIdle, int minIdle) {
        int oldMaxIdle = opConnectPool.getMaxIdle();
        int oldMinIdle = opConnectPool.getMinIdle();
        opConnectPool.setMaxIdle(oldMaxIdle > maxIdle ? oldMaxIdle : maxIdle);
        opConnectPool.setMinIdle(oldMinIdle < minIdle ? oldMinIdle : minIdle);
        min = opConnectPool.getMinIdle();
        max = opConnectPool.getMaxIdle();

    }

    public synchronized IOPConnect getConnect() throws OPException {
        try {
            IOPConnect conn = opConnectPool.borrowObject();
            if (conn != null) {
                return conn;
            } else {
                throw new OPException("getConnect error!");
            }

        } catch (Exception e) {
            throw new OPException("getConnect error!");
        }
    }

    public synchronized void freeConnect(IOPConnect connect) throws OPException {
        if (connect != null) {
            try {
                opConnectPool.returnObject(connect);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static interface Callback<T> {
        T execute(IOPConnect connect) throws Exception;
    }

    public boolean test() throws SQLException {
        try {
            IOPConnect connect = opConnectPool.borrowObject();
            if (connect.isActive()) {
                freeConnect(connect);
            } else {
                connect.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("数据库链接初始化失败");
        }
        return true;
    }
}
