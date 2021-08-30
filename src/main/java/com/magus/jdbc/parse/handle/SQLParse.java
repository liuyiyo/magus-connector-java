package com.magus.jdbc.parse.handle;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.magus.jdbc.parse.entity.ParserResult;

/**
 * @InterfaceName SqlParse
 * @description：sql解析器接口
 * @author：liuyi
 * @Date：2021/5/6 16:53
 */
public interface SQLParse {
    //格式化方法
    public void parse(SQLStatement sqlStatement, ParserResult parserResult);
}
