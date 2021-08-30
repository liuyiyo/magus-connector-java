package com.magus.jdbc.parse.handle;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.magus.jdbc.parse.constant.ColumnType;
import com.magus.jdbc.parse.constant.SqlType;
import com.magus.jdbc.parse.entity.ColumnInfo;
import com.magus.jdbc.parse.entity.ParserResult;
import com.magus.opio.OPType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName InsertSQLParse
 * @description：插入语句解析类
 * @author：liuyi
 * @Date：2021/5/6 18:28
 */
public class CreateSQLParse implements SQLParse {

    private static final String VALUE_NULL = "NULL";

    private static final String VALUE_NOT_NULL = "NOT NULL";

    private static final String PRIMARY_KEY = "PRIMARY KEY";

    @Override
    public void parse(SQLStatement sqlStatement, ParserResult parserResult) {
        MySqlCreateTableStatement statement = (MySqlCreateTableStatement)sqlStatement;
        parserResult.setSqlType(SqlType.CREATE_TABLE);
        parserResult.setTableName(statement.getTableSource().getTableName());
        //设置字段信息
        List<SQLTableElement> tableElementList = statement.getTableElementList();
        List<ColumnInfo> columnInfoList = tableElementList.parallelStream()
                .map(element -> {
                    ColumnInfo columnInfo = new ColumnInfo();
                    SQLColumnDefinition definition = (SQLColumnDefinition) element;
                    //设置字段名称
                    columnInfo.setName(definition.getColumnName());
                    //设置字段类型
                    columnInfo.setType(ColumnType.findValue(definition.getDataType().getName()));
                    String defaultVaule = definition.getDefaultExpr().toString();
                    //设置默认值
                    columnInfo.setDefaultValue(VALUE_NULL.equals(defaultVaule) ? "" : defaultVaule);
                    //设置是否可以为空
                    if(definition.getConstraints()!=null&&definition.getConstraints().size()>0){
                        String valueIsNotNull = definition.getConstraints().get(0).toString();
                        columnInfo.setNotNull(VALUE_NOT_NULL.equals(valueIsNotNull) ? true : false);
                    }
                    return columnInfo;
                }).collect(Collectors.toList());
        //最后默认设置字段
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setName("cluster");
        columnInfo.setType(OPType.VtString);
        columnInfo.setNotNull(true);
        columnInfo.setDefaultValue("");
        columnInfoList.add(columnInfo);
        parserResult.setColumnInfoList(columnInfoList);
    }

    public static void main(String[] args) {
    }
}
