package com.magus.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author liuyi
 * @Description //联系信息
 * @Date 13:30 2021/4/9
 * @Param
 * @return
 **/
public class RelationInfo implements Serializable {

    private static final long serialVersionUID = -5809782578272943999L;
    public RelationInfo(){

    }

    private String address;
    private String mobile;
    private Map<String,Object> map;
    private List<String> list;

    public RelationInfo(String address, String mobile) {
        this.address = address;
        this.mobile = mobile;
    }

    public RelationInfo(String address, String mobile, Map<String, Object> map, List<String> list) {
        this.address = address;
        this.mobile = mobile;
        this.map = map;
        this.list = list;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "RelationInfo{" +
                "address='" + address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", map=" + map +
                ", list=" + list +
                '}';
    }
}