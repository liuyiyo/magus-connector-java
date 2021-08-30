package com.magus.jdbc;

import com.magus.opio.dto.OPColumn;
import com.magus.opio.dto.OPValue;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OPResultSetMetaData implements ResultSetMetaData {
    private List<OPColumn> columns;
    private Map<String, Integer> columnMap = new HashMap<String, Integer>();

    public OPResultSetMetaData(List<OPColumn> columns) {
        this.columns = columns;
        for (int i = 0; i < columns.size(); i++) {
            OPColumn column = columns.get(i);
            columnMap.put(column.getColumnName(), (i + 1));
        }
    }

    public int getColumnIndex(String columnLabel) {
        return columnMap.get(columnLabel);
    }

    public String getCatalogName(int column) throws SQLException {
        column -= 1;
        throw new UnrealizedException("OPResultSetMetaData.getCatalogName");
    }

    public String getColumnClassName(int column) throws SQLException {
        return "java.lang.Object";
    }

    public int getColumnCount() throws SQLException {
        return columns.size();
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        column -= 1;
        int length = columns.get(column).getLength();
        return length == 0 ? 255 : length;
    }

    public String getColumnLabel(int column) throws SQLException {
        column -= 1;
        return columns.get(column).getColumnName();
    }

    public String getColumnName(int column) throws SQLException {
        column -= 1;
        return columns.get(column).getColumnName();
    }

    public int getColumnType(int column) throws SQLException {
        column -= 1;
        int type = columns.get(column).getType();
        switch (type) {
            case OPValue.TYPE_NULL:
                return Types.NULL;
            case OPValue.TYPE_BOOL:
                return Types.BOOLEAN;
            case OPValue.TYPE_INT8:
                return Types.TINYINT;
            case OPValue.TYPE_INT16:
                return Types.SMALLINT;
            case OPValue.TYPE_INT32:
                return Types.INTEGER;
            case OPValue.TYPE_INT64:
                return Types.BIGINT;
            case OPValue.TYPE_FLOAT:
                return Types.FLOAT;
            case OPValue.TYPE_DOUBLE:
                return Types.DOUBLE;
            case OPValue.TYPE_DATETIME:
                return Types.TIMESTAMP;
            case OPValue.TYPE_BINARY:
                return Types.BINARY;
            case OPValue.TYPE_OBJECT:
            case OPValue.TYPE_MAP:
            case OPValue.TYPE_STRUCT:
            case OPValue.TYPE_ARRAY:
            case OPValue.TYPE_BOOL_ARRAY:
            case OPValue.TYPE_INT8_ARRAY:
            case OPValue.TYPE_INT16_ARRAY:
            case OPValue.TYPE_INT32_ARRAY:
            case OPValue.TYPE_INT64_ARRAY:
            case OPValue.TYPE_FLOAT_ARRAY:
            case OPValue.TYPE_DOUBLE_ARRAY:
            case OPValue.TYPE_DATETIME_ARRAY:
            case OPValue.TYPE_STRING_ARRAY:
            case OPValue.TYPE_BINARY_ARRAY:
                return Types.JAVA_OBJECT;
            default:
                return Types.VARCHAR;
        }
    }

    public String getColumnTypeName(int column) throws SQLException {
        column -= 1;
        int type = columns.get(column).getType();
        switch (type) {
            case OPValue.TYPE_NULL:
                return "NULL";
            case OPValue.TYPE_BOOL:
                return "BOOLEAN";
            case OPValue.TYPE_INT8:
                return "TINYINT";
            case OPValue.TYPE_INT16:
                return "SMALLINT";
            case OPValue.TYPE_INT32:
                return "INTEGER";
            case OPValue.TYPE_INT64:
                return "BIGINT";
            case OPValue.TYPE_FLOAT:
                return "FLOAT";
            case OPValue.TYPE_DOUBLE:
                return "DOUBLE";
            case OPValue.TYPE_DATETIME:
                return "TIMESTAMP";
            case OPValue.TYPE_STRING:
                return "VARCHAR";
            case OPValue.TYPE_BINARY:
                return "BINARY";
            case OPValue.TYPE_OBJECT:
                return "OBJECT";
            default:
                return "VARCHAR";
        }
    }

    public int getPrecision(int column) throws SQLException {
        column -= 1;
        int type = columns.get(column).getType();
        switch (type) {
            case OPValue.TYPE_NULL:
                return 0;
            case OPValue.TYPE_BOOL:
            case OPValue.TYPE_INT8:
            case OPValue.TYPE_INT16:
            case OPValue.TYPE_INT32:
                return 10;
            case OPValue.TYPE_INT64:
                return 20;
            case OPValue.TYPE_FLOAT:
                return 10;
            case OPValue.TYPE_DOUBLE:
                return 20;
            case OPValue.TYPE_DATETIME:
                return 20;
            case OPValue.TYPE_STRING:
                return 8192;
            case OPValue.TYPE_BINARY:
                return 255;
            default:
                return 255;
        }
    }

    public int getScale(int column) throws SQLException {
        column -= 1;
        int type = columns.get(column).getType();
        switch (type) {
            case OPValue.TYPE_FLOAT:
                return 5;
            case OPValue.TYPE_DOUBLE:
            case OPValue.TYPE_DATETIME:
                return 10;
        }
        return 0;
    }

    public String getSchemaName(int column) throws SQLException {
        column -= 1;
        throw new UnrealizedException("OPResultSetMetaData.getSchemaName");
    }

    public String getTableName(int column) throws SQLException {
        column -= 1;
        throw new UnrealizedException("OPResultSetMetaData.getTableName");
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        column -= 1;
        throw new UnrealizedException("OPResultSetMetaData.isAutoIncrement");
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        column -= 1;
        return false;
    }

    public int isNullable(int column) throws SQLException {
        column -= 1;

        return 2;
    }

    public boolean isReadOnly(int column) throws SQLException {
        column -= 1;
        return true;
    }

    public boolean isSearchable(int column) throws SQLException {
        column -= 1;
        return true;
    }

    public boolean isSigned(int column) throws SQLException {
        column -= 1;
        return true;
    }

    public boolean isWritable(int column) throws SQLException {
        column -= 1;
        return false;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnrealizedException("OPResultSetMetaData.unwrap");
    }
}
