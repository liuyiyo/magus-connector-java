package com.magus.opio.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author liuyi
 * @Description //全局线程池工具类(单例)
 * @Date 2021/8/26 9:10
 * @Param
 * @return
 **/
public class ExecutorsUtil{
    //防止恶意通过反射破坏单例
    private ExecutorsUtil(){
        if(ExecutorsUtilInside.EXECUTOR_SERVICE!=null){
            throw new RuntimeException("此类不允许创建多个实例");
        }
    }
    //最大执行线程数
    private static int maxThreadNum = 8;
    //静态内部类保证线程池对象单例
    private static class ExecutorsUtilInside{
        //创建限制最大并发执行数量的线程池
        private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(ExecutorsUtil.maxThreadNum);
    }
    //获取线程池(保证线程池的全局唯一)
    public static ExecutorService getPoolInstance(){
        return ExecutorsUtilInside.EXECUTOR_SERVICE;
    }

}
