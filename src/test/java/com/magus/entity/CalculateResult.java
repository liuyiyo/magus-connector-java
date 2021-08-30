package com.magus.entity;

import com.alibaba.fastjson.JSON;
import com.magus.util.ConnectionInfo;
import com.magus.util.InsertUtils;
import com.magus.util.LtOpUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName VehicleInfo
 * @description：车辆基础信息
 * @author：liuyi
 * @Date：2021/7/5 10:56
 */
public class CalculateResult {

    public static void main(String[] args) throws Exception {
        CalculateResult info = new CalculateResult();
        info.setSource("nissan");
        info.setVin("LJNEDV1T1JN650180");
        info.setE_t(new Date());
        info.setCe_v("3.956");
        info.setHv(3.969f);
        info.setLv(3.97f);
        info.setCreate_time(new Date());
        info.setCluster("kudu");
        List<CalculateResult> list = new ArrayList<>();
        list.add(info);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setIp("10.130.168.192");
        connectionInfo.setPort(19604);
        connectionInfo.setUser("sis");
        connectionInfo.setPassword("openplant");
        connectionInfo.setCluster("kudu");
        connectionInfo.setDbName("dataBase");
        connectionInfo.setTableName("gb32960_calculate");
        LtOpUtil ltOpUtil = new LtOpUtil(connectionInfo);
        ltOpUtil.batchInsert(list);
    }

    private String source;
    private String vin;
    private Date e_t;
    private String ce_v;
    private float hv;
    private float lv;
    private String cluster;
    private Date create_time;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getE_t() {
        return e_t;
    }

    public void setE_t(Date e_t) {
        this.e_t = e_t;
    }

    public String getCe_v() {
        return ce_v;
    }

    public void setCe_v(String ce_v) {
        this.ce_v = ce_v;
    }

    public float getHv() {
        return hv;
    }

    public void setHv(float hv) {
        this.hv = hv;
    }

    public float getLv() {
        return lv;
    }

    public void setLv(float lv) {
        this.lv = lv;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }


}
