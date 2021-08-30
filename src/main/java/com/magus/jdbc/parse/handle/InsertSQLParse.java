package com.magus.jdbc.parse.handle;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.magus.jdbc.parse.entity.ColumnInfo;
import com.magus.jdbc.parse.entity.ParserResult;
import com.magus.jdbc.parse.constant.SqlType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName InsertSQLParse
 * @description：插入语句解析类
 * @author：liuyi
 * @Date：2021/5/6 18:28
 */
public class InsertSQLParse implements SQLParse {
    @Override
    public void parse(SQLStatement sqlStatement, ParserResult parserResult) {
        MySqlInsertStatement statement = (MySqlInsertStatement)sqlStatement;
        parserResult.setSqlType(SqlType.INSERT_TABLE);
        parserResult.setTableName(statement.getTableSource().getTableName());
        List<ColumnInfo> columnList = statement.getColumns().parallelStream()
                .map(expr -> {
                    ColumnInfo columnInfo = new ColumnInfo();
                    SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) expr;
                    columnInfo.setName(sqlIdentifierExpr.getName());
                    return columnInfo;
                }).collect(Collectors.toList());
        parserResult.setColumnInfoList(columnList);
    }

    public static void main(String[] args) {
//        String sql = "INSERT INTO user (id,name,age,sex,address,status,create_user,create_time,modify_user,modify_time) VALUES ('?','?','?','?','?','?','?','?','?','?');";
//        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
//        SQLStatement sqlStatement = stmtList.get(0);
//        String className = sqlStatement.getClass().getSimpleName();
//        SQLParse bean = BeanFactory.getBean(SQLParse.class, SqlType.findClassName(className));
//        ParserResult parserResult = new ParserResult();
//        bean.parse(sqlStatement,parserResult);
//        System.out.println(parserResult);
    }
}
