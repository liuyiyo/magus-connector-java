package com.magus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName Test
 * @description：
 * @author：liuyi
 * @Date：2021/7/21 14:20
 */
public class Test {
    public static void main(String[] args) {
//        List<Integer> list = new ArrayList<>();
//        list.add(1);
//        test1(list);
//        System.out.println(list.toString());
//        AtomicInteger integer = new AtomicInteger(10);
//        test1(integer);
//        System.out.println(integer.get());

//        double a = 38556.2;
//        System.out.println(a/10-1000);
//        String s = "1||2";
//        String[] split = s.split("\\|");
//        System.out.println(split[1].equals(""));
//        System.out.println(split.length);
    }

    private static void test1(List<Integer> list){
        list.add(2);
        list.add(3);
    }

    private static void test1(AtomicInteger integer){
        integer.addAndGet(10);
    }

}
