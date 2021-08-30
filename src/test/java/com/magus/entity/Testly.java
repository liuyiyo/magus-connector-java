package com.magus.entity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName：Testly
 * @Description：
 * @Author：liuyi
 * @Date：2021/4/7 14:58
 * @Version：1.0
 */
public class Testly {
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean boolTest;
    private byte int8Test;
    private short int16Test;
    private int int32Test;
    private long int64Test;
    private float floatTest;
    private double doubleTest;
    private Date datetimeTest;
    private String stringTest;
    private byte[] binaryTest;
    private List<Boolean> bool_ArrayTest;
    private String cluster;

    public boolean getBoolTest() {
        return boolTest;
    }

    public void setBoolTest(boolean boolTest) {
        this.boolTest = boolTest;
    }

    public byte getInt8Test() {
        return int8Test;
    }

    public void setInt8Test(byte int8Test) {
        this.int8Test = int8Test;
    }

    public short getInt16Test() {
        return int16Test;
    }

    public void setInt16Test(short int16Test) {
        this.int16Test = int16Test;
    }

    public int getInt32Test() {
        return int32Test;
    }

    public void setInt32Test(int int32Test) {
        this.int32Test = int32Test;
    }

    public long getInt64Test() {
        return int64Test;
    }

    public void setInt64Test(long int64Test) {
        this.int64Test = int64Test;
    }

    public float getFloatTest() {
        return floatTest;
    }

    public void setFloatTest(float floatTest) {
        this.floatTest = floatTest;
    }

    public double getDoubleTest() {
        return doubleTest;
    }

    public void setDoubleTest(double doubleTest) {
        this.doubleTest = doubleTest;
    }

    public Date getDatetimeTest() {
        return datetimeTest;
    }

    public void setDatetimeTest(Date datetimeTest) {
        this.datetimeTest = datetimeTest;
    }

    public String getStringTest() {
        return stringTest;
    }

    public void setStringTest(String stringTest) {
        this.stringTest = stringTest;
    }

    public byte[] getBinaryTest() {
        return binaryTest;
    }

    public void setBinaryTest(byte[] binaryTest) {
        this.binaryTest = binaryTest;
    }

    public List<Boolean> getBool_ArrayTest() {
        return bool_ArrayTest;
    }

    public void setBool_ArrayTest(List<Boolean> bool_ArrayTest) {
        this.bool_ArrayTest = bool_ArrayTest;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
