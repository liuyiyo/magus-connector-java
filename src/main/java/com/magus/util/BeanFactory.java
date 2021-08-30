package com.magus.util;

/**
 * @Author liuyi
 * @Description //对象工厂
 * @Date 2021/1/26 17:32
 **/
public class BeanFactory {
    /**
     * @Author liuyi
     * @Description //获取对象
     * @Date 2021/1/27 15:54
     * @Param [className]
     * @return com.liuyi.springbootdemo.exercise.jdbc.parser.handle.MysqlFormat
     **/
    public static <T> T getBean(Class<T> tClass,String className) {
        try {
            Class clz = Class.forName(className);
            return (T)clz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}