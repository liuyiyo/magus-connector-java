package com.magus.jdbc.parse.constant;
/**
 * @Author liuyi
 * @Description //sql解析类型类枚举
 * @Date 2021/1/27 14:07
 **/
public enum SqlType {

    //字段操作
//    ALTER_TABLE("SQLAlterTableStatement",""),
    INSERT_TABLE("MySqlInsertStatement","com.magus.jdbc.parse.handle.InsertSQLParse"),
    //创建操作
    CREATE_TABLE("MySqlCreateTableStatement","com.magus.jdbc.parse.handle.CreateSQLParse"),
    //删除操作
    DROP_TABLE("","");

    SqlType(String statementClass, String className) {
        this.statementClass = statementClass;
        this.className = className;
    }

    private String statementClass;

    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatementClass() {
        return statementClass;
    }

    public void setStatementClass(String statementClass) {
        this.statementClass = statementClass;
    }

    public static String findClassName(String statementClass){
        for (SqlType parserTypeEnum : SqlType.values()) {
            if(statementClass.equals(parserTypeEnum.getStatementClass())){
                return parserTypeEnum.getClassName();
            }
        }
        return null;
    }
}
