package com.magus.jdbc;

import com.magus.opio.OPException;
import com.magus.opio.dto.OPDataset;
import com.magus.opio.dto.OPResponse;
import com.magus.opio.utils.array.ArrayDecodeUtil;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

public class OPResultSet implements ResultSet {

    private OPResponse response;
    private OPResultSetMetaData metaData = null;
    private OPDataset dataset;
    private Boolean closed = false;
    private String sql;

    public OPResultSet(OPResponse resp) throws OPException {
        this.response = resp;
        if (response != null) {
            // 检查返回结果集
            this.dataset = response.getDataSet();
            if (this.dataset != null) {
                this.metaData = new OPResultSetMetaData(this.dataset.getColumns());
            } else {
                throw new OPException("response error ");
            }
        }
        closed = false;
    }

    public String getSql() {
        return sql;
    }

    public boolean absolute(int row) throws SQLException {
        throw new UnrealizedException("OPResultSet.absolute");
    }

    public void afterLast() throws SQLException {
        throw new UnrealizedException("OPResultSet.afterLast");
    }

    public void beforeFirst() throws SQLException {
        throw new UnrealizedException("OPResultSet.beforeFirst");
    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnrealizedException("OPResultSet.cancelRowUpdates");
    }

    public void clearWarnings() throws SQLException {
        throw new UnrealizedException("OPResultSet.clearWarnings");
    }

    public void close() throws SQLException {
        if (!closed) {
            destroy();
        }
    }

    public void destroy() {
        if (!closed) {
            closed = true;
            if (response != null) {
                response.close();
                response = null;
            }
        }
    }

    public void deleteRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.deleteRow");
    }

    public int findColumn(String columnLabel) throws SQLException {
        return metaData.getColumnIndex(columnLabel);
    }

    public boolean first() throws SQLException {
        throw new UnrealizedException("OPResultSet.first");
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getArray");
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getArray");
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getAsciiStream");
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getAsciiStream");
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBigDecimal");
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBigDecimal");
    }

    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBigDecimal");
    }

    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBigDecimal");
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBinaryStream");
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBinaryStream");
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBlob");
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getBlob");
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getBoolean(columnIndex);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(metaData.getColumnIndex(columnLabel));
    }

    public byte getByte(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getByte(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public byte getByte(String columnLabel) throws SQLException {
        return getByte(metaData.getColumnIndex(columnLabel));
    }

    public short getShort(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getShort(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public short getShort(String columnLabel) throws SQLException {
        return getShort(metaData.getColumnIndex(columnLabel));
    }

    @SuppressWarnings("deprecation")
    public Time getTime(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            java.util.Date date = dataset.getDate(columnIndex);
            return new Time(date.getHours(), date.getMinutes(), date.getSeconds());
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return new Timestamp(new Double(dataset.getDouble(columnIndex) * 1000L).longValue());
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(metaData.getColumnIndex(columnLabel));
    }

    public int getInt(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getInt(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public int getInt(String columnLabel) throws SQLException {
        return getInt(metaData.getColumnIndex(columnLabel));
    }

    public long getLong(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getLong(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public long getLong(String columnLabel) throws SQLException {
        return getLong(metaData.getColumnIndex(columnLabel));
    }

    public Date getDate(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return new Date(dataset.getLong(columnIndex));
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnrealizedException("OPResultSet.getDate");
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    public float getFloat(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getFloat(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(metaData.getColumnIndex(columnLabel));
    }

    public double getDouble(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getDouble(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(metaData.getColumnIndex(columnLabel));
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getBytes(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(metaData.getColumnIndex(columnLabel));
    }

    public String getString(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getString(columnIndex);
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public String getString(String columnLabel) throws SQLException {
        return getString(metaData.getColumnIndex(columnLabel));
    }

    public String getCursorName() throws SQLException {
        return null;
    }

    public Time getTime(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getTime");
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnrealizedException("OPResultSet.getTime");
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnrealizedException("OPResultSet.getTime");
    }

    public Statement getStatement() throws SQLException {
        throw new UnrealizedException("OPResultSet.getStatement");
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getCharacterStream");
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getCharacterStream");
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getClob");
    }

    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getClob");
    }

    public int getConcurrency() throws SQLException {
        throw new UnrealizedException("OPResultSet.getConcurrency");
    }

    public int getFetchDirection() throws SQLException {
        throw new UnrealizedException("OPResultSet.getFetchDirection");
    }

    public int getFetchSize() throws SQLException {
        throw new UnrealizedException("OPResultSet.getFetchSize");
    }

    public int getHoldability() throws SQLException {
        throw new UnrealizedException("OPResultSet.getHoldability");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return metaData;
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getNCharacterStream");
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getNCharacterStream");
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getNClob");
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getNClob");
    }

    public String getNString(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getNString");
    }

    public String getNString(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getNString");
    }

    public Object getObject(int columnIndex) throws SQLException {
        try {
            columnIndex -= 1;
            return dataset.getObject(columnIndex);
        } catch (OPException e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }

    public Object getObject(String columnLabel) throws SQLException {
        return getObject(metaData.getColumnIndex(columnLabel));
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getObject");
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnrealizedException("OPResultSet.getObject");
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        try {
            columnIndex -= 1;
            return (T) dataset.getMicClassArray(columnIndex);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnrealizedException("OPResultSet.getObject");
    }

    public Ref getRef(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getRef");
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getRef");
    }

    public int getRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.getRow");
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getRowId");
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getRowId");
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getSQLXML");
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getSQLXML");
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getTimestamp");
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new UnrealizedException("OPResultSet.getTimestamp");
    }

    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    public URL getURL(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.getURL");
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getURL");
    }

    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnrealizedException("OPResultSet.getUnicodeStream");
    }

    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.getUnicodeStream");
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnrealizedException("OPResultSet.getWarnings");
    }

    public void insertRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.insertRow");
    }

    public boolean isAfterLast() throws SQLException {
        throw new UnrealizedException("OPResultSet.isAfterLast");
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new UnrealizedException("OPResultSet.isBeforeFirst");
    }

    public boolean isClosed() throws SQLException {
        return closed;
    }

    public boolean isFirst() throws SQLException {
        throw new UnrealizedException("OPResultSet.isFirst");
    }

    public boolean isLast() throws SQLException {
        throw new UnrealizedException("OPResultSet.isLast");
    }

    public boolean last() throws SQLException {
        throw new UnrealizedException("OPResultSet.last");
    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.moveToCurrentRow");
    }

    public void moveToInsertRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.moveToInsertRow");
    }

    public boolean next() throws SQLException {
        try {
            boolean next = dataset.next();
            return next;
        } catch (OPException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public boolean previous() throws SQLException {
        throw new UnrealizedException("OPResultSet.previous");
    }

    public void refreshRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.refreshRow");
    }

    public boolean relative(int rows) throws SQLException {
        throw new UnrealizedException("OPResultSet.relative");
    }

    public boolean rowDeleted() throws SQLException {
        throw new UnrealizedException("OPResultSet.rowDeleted");
    }

    public boolean rowInserted() throws SQLException {
        throw new UnrealizedException("OPResultSet.rowInserted");
    }

    public boolean rowUpdated() throws SQLException {
        throw new UnrealizedException("OPResultSet.rowUpdated");
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new UnrealizedException("OPResultSet.setFetchDirection");
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new UnrealizedException("OPResultSet.setFetchSize");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateArray");
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateArray");
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateAsciiStream");
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateAsciiStream");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateAsciiStream");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateAsciiStream");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateAsciiStream");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateAsciiStream");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBigDecimal");
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBigDecimal");
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBinaryStream");
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBinaryStream");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBinaryStream");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBinaryStream");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBinaryStream");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBinaryStream");
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBlob");
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBlob");
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBlob");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBlob");
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBlob");
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBlob");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBoolean");
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBoolean");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateByte");
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateByte");
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateBytes");
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateBytes");
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateCharacterStream");
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateCharacterStream");
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateCharacterStream");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateCharacterStream");
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateCharacterStream");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateCharacterStream");
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateClob");
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateClob");
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateClob");
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateClob");
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateClob");
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateClob");
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateDate");
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateDate");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateDouble");
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateDouble");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateFloat");
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateFloat");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateInt");
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateInt");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateLong");
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateLong");
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNCharacterStream");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNCharacterStream");
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNCharacterStream");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNCharacterStream");
    }

    public void updateNClob(int columnIndex, NClob clob) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNClob");
    }

    public void updateNClob(String columnLabel, NClob clob) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNClob");
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNClob");
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNClob");
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNClob");
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNClob");
    }

    public void updateNString(int columnIndex, String string) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNString");
    }

    public void updateNString(String columnLabel, String string) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNString");
    }

    public void updateNull(int columnIndex) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateNull");
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateNull");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateObject");
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateObject");
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateObject");
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateObject");
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateRef");
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateRef");
    }

    public void updateRow() throws SQLException {
        throw new UnrealizedException("OPResultSet.updateRow");
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateRowId");
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateRowId");
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateSQLXML");
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateSQLXML");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateShort");
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateShort");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateString");
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateString");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateTime");
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateTime");
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        columnIndex -= 1;
        throw new UnrealizedException("OPResultSet.updateTimestamp");
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new UnrealizedException("OPResultSet.updateTimestamp");
    }

    public boolean wasNull() throws SQLException {
        return false;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnrealizedException("OPResultSet.isWrapperFor");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnrealizedException("OPResultSet.unwrap");
    }
}
