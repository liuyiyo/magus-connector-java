package com.magus.jdbc.parse.entity;

import com.magus.jdbc.parse.constant.SqlType;
import lombok.Data;
import java.util.List;

/**
 * @ClassName FormatResult
 * @description：格式化结果类
 * @author：liuyi
 * @Date：2021/1/26 17:03
 */
@Data
public class ParserResult {
    //表名
    private String tableName;
    //sql类型
    private SqlType sqlType;
    //字段信息
    List<ColumnInfo> columnInfoList;
    //时间字段标志
    private String matchDateColumn;
    private String classifyColumn;
}
