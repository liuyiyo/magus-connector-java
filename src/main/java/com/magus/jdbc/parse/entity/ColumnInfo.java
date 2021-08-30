package com.magus.jdbc.parse.entity;

import lombok.Data;

/**
 * @Author liuyi
 * @Description //字段信息类
 * @Date 2021/5/8 11:38
 **/
@Data
public class ColumnInfo {

    //主键标识
    private String ClassifyColumn;
    //字段名称
    private String Name;
    //字段类型
    private int Type;
    //是否可以为空(默认可以为空)
    private boolean NotNull = false;
    //默认值
    private String DefaultValue;
}
