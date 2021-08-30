package com.magus.util;

/**
 * @author yong.xia@hand-china.com
 * @version 1.0
 * @ClassName CacheUtil
 * @description
 * @date 2021/6/2 20:59
 * @since JDK 1.8
 */


import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *     基于concurrentHash的本地缓存工具类
 *     缓存删除基于timer定时器
 * <pre>
 * @author yong.xia
 * @date 2021/6/2
 * @param
 * @return
 */
public class LocalCache {

    //默认大小
    public static final int DEFAULT_CAPACITY = 1024;

    // 最大缓存大小
    public static final int MAX_CAPACITY = 5000;

    //默认缓存过期时间一天
    public static final long DEFAULT_TIMEOUT = 1000 * 60 * 60 * 24;

    //1000毫秒
    public static final long SECOND_TIME = 1000;

    //存储缓存的Map
    public static final ConcurrentHashMap<String, Object> map;

    public static final Timer timer;

    static {
        map = new ConcurrentHashMap<>(DEFAULT_CAPACITY);
        timer = new Timer();
    }

    //私有化构造方法
    private LocalCache() {

    }

    /**
     * <pre>
     *     缓存任务清除类
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param
     * @return
     */
    static class ClearTask extends TimerTask {
        private String key;

        public ClearTask(String key) {
            this.key = key;
        }

        @Override
        public void run() {
            LocalCache.remove(key);
        }

    }

    //==================缓存的增删改查

    /**
     * <pre>
     *     添加缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param key
     * @param object
     * @return void
     */
    public static boolean put(String key, Object object) {
        if (checkCapacity()) {
            map.put(key, object);
            //默认缓存时间
            timer.schedule(new ClearTask(key), DEFAULT_TIMEOUT);
            return true;
        }
        return false;
    }

    /**
     * <pre>
     *     添加缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param key
     * @param object
     * @param time_out  ：缓存过期时间：单位秒
     * @return void
     * <pre>
     * 修改记录
     * 版本号		修订日期		修改人		bug编号		修改内容
     * 1.0.0	  2021/6/2       yong.xia		    		     新建
     * </pre>
     */
    public static boolean put(String key, Object object, int time_out) {
        if (checkCapacity()) {
            map.put(key, object);
            //默认缓存时间
            timer.schedule(new ClearTask(key), time_out * SECOND_TIME);
        }
        return false;
    }


    /**
     * <pre>
     *     判断容量大小
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param
     * @return boolean
     */
    public static boolean checkCapacity() {
        return map.size() < MAX_CAPACITY;
    }

    /**
     * <pre>
     *     批量增加缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param m
     * @param time_out
     * @return void
     */
    public static boolean put(Map<String, Object> m, int time_out) {
        if (map.size() + m.size() <= MAX_CAPACITY) {
            map.putAll(map);
            for (String key : m.keySet()) {
                timer.schedule(new ClearTask(key), time_out * SECOND_TIME);
            }
            return true;
        }
        return false;
    }

    /**
     * <pre>
     *     删除缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param key
     * @return void
     */
    public static void remove(String key) {
        map.remove(key);
    }

    public static void getAll() {

    }

    /**
     * <pre>
     *     清除所有缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param
     * @return void
     */
    public static void clearAll() {
        if (map.size() > 0) {
            map.clear();
        }
        timer.cancel();
    }

    /**
     * <pre>
     *     获取缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param key
     * @return java.lang.Object
     */
    public static Object get(String key) {
        return map.get(key);
    }


    /**
     * <pre>
     *     是否包含某个缓存
     * <pre>
     * @author yong.xia
     * @date 2021/6/2
     * @param key
     * @return boolean
     */
    public static boolean isContain(String key) {
        return map.contains(key);
    }
}