package com.magus.jdbc.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/* 数据库中的数据类型 */
@MappedJdbcTypes(JdbcType.JAVA_OBJECT)
/* 转化后的数据类型 */
@MappedTypes(value = List.class)
public class ListTypeHandler extends BaseTypeHandler<List> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        return (List)object;
    }

    @Override
    public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object object = rs.getObject(columnIndex);
        return (List)object;
    }

    @Override
    public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}