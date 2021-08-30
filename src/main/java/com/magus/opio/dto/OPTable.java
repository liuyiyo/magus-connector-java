package com.magus.opio.dto;

import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.utils.Bytes;
import com.magus.opio.utils.array.ArrayConvert;
import com.magus.opio.utils.array.ArrayEncodeUtil;
import com.magus.opio.utils.map.MapConvert;
import com.magus.opio.utils.struct.StructConvert;
import java.util.*;
import static com.magus.opio.OPType.*;

public class OPTable {
    private String tableName;
    private int colCount;       //当前表字段总个数
    private int fixedLen;       //定长字段占用空间大小
    private int bitLen;         //字段bit标记占用空间大小，eg:8个字段,bitLen=1
    private int variableLen;    //变长字段个数
    private final List<OPColumn> columns;
    private final List<byte[]> rows;
    private byte[] fixedBuf;
    private byte[][] variableBuf;
    private byte[] bitBuf;
    private int bufSize;
    private int rowCount;

    public OPTable() {
        columns = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public OPTable(String tableName) {
        this();
        this.tableName = tableName;
    }

    public void setTableName(String name) {
        this.tableName = name;
    }

    // void add_column(long table, String name, int type);
    public void addColumn(String key, byte type) throws OPException {
        OPColumn col = new OPColumn(key, type, colCount);
        switch (type) {
            case OPType.VtBool:
            case OPType.VtInt8:
                col.setLength(1);
                break;
            case OPType.VtInt16:
                col.setLength(2);
                break;
            case OPType.VtInt32:
            case OPType.VtFloat:
                col.setLength(4);
                break;
            case OPType.VtInt64:
            case OPType.VtDouble:
            case OPType.VtDateTime:
                col.setLength(8);
                break;
            case OPType.VtString:
            case OPType.VtBinary:
            case OPType.VtObject:
            case OPType.VtNull:
                col.setLength(0);
                break;
            default:
                //ToDo 复杂数据类型
                if (type > 32) {
                    throw new OPException("Type " + type + " is not currently supported");
                } else {
                    col.setLength(0);
                    break;
                }
        }
        //变长类型
        if (col.getLength() == 0) {
            col.setCell(variableLen);
            variableLen++;
        }
        col.setOffset(fixedLen);
        col.setEnd(fixedLen + col.getLength());
        col.setIndex(colCount);
        fixedLen = col.getEnd();
        columns.add(col);
        colCount++;
        bitLen = (colCount + 7) >> 3;
        initBuf();
    }

    // void set_column_bool(long table, int col, int value);
    public void setColumnBool(int col, boolean value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtBool);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutBool(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_int8(long table, int col, int value);
    public void setColumnByte(int col, byte value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtInt8);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutInt8(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_int16(long table, int col, int value);
    public void setColumnShort(int col, short value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtInt16);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutInt16(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_int32(long table, int col, int value);
    public void setColumnInt(int col, int value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtInt32);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutInt32(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_int64(long table, int col, long value);
    public void setColumnLong(int col, long value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtInt64);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutInt64(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_float(long table, int col, float value);
    public void setColumnFloat(int col, float value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtFloat);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutFloat32(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_double(long table, int col, double value);
    public void setColumnDouble(int col, double value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtDouble);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutFloat64(v, value);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_datetime(long table, int col, double value);
    public void setColumnDatetime(int col, Date value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtDateTime);
        byte[] v = new byte[column.getEnd() - column.getOffset()];
        Bytes.PutFloat64(v, ((double) value.getTime()) / 1e3);
        System.arraycopy(v, 0, fixedBuf, column.getOffset(), v.length);
        setColumnBit(col);
    }

    // void set_column_string(long table, int col, String value);
    public void setColumnString(int col, String value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtString);
        setColumnBinary(col, column, value.getBytes());
    }

    public void setColumnBinary(int col, byte[] value) throws OPException {
        OPColumn column = checkParameter(col, OPType.VtBinary);
        setColumnBinary(col, column, value);
    }

    public void setColumnBinary(int col, OPColumn column, byte[] value){
        int dataLen = value.length;
        byte[] v = Bytes.PutBinary(value);
        int headLen = v[0];
        //变长是不固定的，下一行要减去上一行的bufSize，重新设置
        if (variableBuf[column.getCell()] != null) bufSize -= variableBuf[column.getCell()].length;
        variableBuf[column.getCell()] = Arrays.copyOfRange(v, 1, v.length);
        bufSize += headLen + dataLen;
        setColumnBit(col);
    }

    public void setColumnMap(int col, Map<?, ?> value) throws OPException {
        OPColumn opColumn = checkParameter(col, OPType.VtMap);
        setColumnBinary(col, opColumn, MapConvert.encodeMap(value));
    }

    public void setColumnArray(int col, List<?> value) throws OPException {
        OPColumn opColumn = checkParameter(col, OPType.VtArray);
        setColumnBinary(col, opColumn, ArrayConvert.encodeArray(value));
    }

    public void setColumnBoolArray(int col, List<Boolean> value) throws OPException {
        OPColumn opColumn = checkParameter(col, OPType.BOOL_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, BOOL_ARRAY));
    }

    public void setColumnByteArray(int col, List<Byte> value) throws OPException {
        OPColumn opColumn = checkParameter(col, INT8_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, INT8_ARRAY));
    }

    public void setColumnShortArray(int col, List<Short> value) throws OPException {
        OPColumn opColumn = checkParameter(col, INT16_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, INT16_ARRAY));
    }

    public void setColumnIntegerArray(int col, List<Integer> value) throws OPException {
        OPColumn opColumn = checkParameter(col, INT32_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, INT32_ARRAY));
    }

    public void setColumnLongArray(int col, List<Long> value) throws OPException {
        OPColumn opColumn = checkParameter(col, INT64_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, INT64_ARRAY));
    }

    public void setColumnFloatArray(int col, List<Float> value) throws OPException {
        OPColumn opColumn = checkParameter(col, FLOAT_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, FLOAT_ARRAY));
    }

    public void setColumnDoubleArray(int col, List<Double> value) throws OPException {
        OPColumn opColumn = checkParameter(col, DOUBLE_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, DOUBLE_ARRAY));
    }

    public void setColumnDateArray(int col, List<Date> value) throws OPException {
        OPColumn opColumn = checkParameter(col, DATETIME_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeBaseArray(value, DATETIME_ARRAY));
    }

    public void setColumnStringArray(int col, List<String> value) throws OPException {
        OPColumn opColumn = checkParameter(col, STRING_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeVarArray(value, STRING_ARRAY));
    }

    public void setColumnBinaryArray(int col, List<byte[]> value) throws OPException {
        OPColumn opColumn = checkParameter(col, BINARY_ARRAY);
        setColumnBinary(col, opColumn, ArrayEncodeUtil.encodeVarArray(value, BINARY_ARRAY));
    }

    public void setColumnStruct(int col, Object value) throws OPException {
        OPColumn opColumn = checkParameter(col, OPType.VtStructure);
        setColumnBinary(col, opColumn, StructConvert.encodeStruct(value));
    }

    private void setColumnBit(int col) {
        bitBuf[col >> 3] |= 1 << (byte) (col & 7);
    }

    private void initBuf() {
        //默认缓冲大小
        bufSize = fixedLen + bitLen;
        //构建变长缓冲
        variableBuf = new byte[variableLen][];
        //构建缓冲区
        fixedBuf = new byte[fixedLen];
        bitBuf = new byte[bitLen];
    }

    private OPColumn checkParameter(int col, byte type) throws OPException {
        if (col >= colCount) throw new OPException("col index:" + col + " error,max col index is:" + (colCount - 1));
        OPColumn column = columns.get(col);
        int colType = column.getType();
        if (colType != type && colType != OPType.VtNull) {
            throw new OPException("add col type is:" + columns.get(col).getType() + ",but set type is:" + type);
        }
        return column;
    }

    // void bind_row(long table);
    public synchronized void bindRow() throws OPException {
        try {
            byte[] row = new byte[bufSize];
            //拷贝定长内容
            System.arraycopy(fixedBuf, 0, row, 0, fixedBuf.length);
            //拷贝位参数
            System.arraycopy(bitBuf, 0, row, fixedLen, bitBuf.length);
            int index = fixedLen + bitLen;
            //拷贝变长
            for (int i = 0; i < variableLen; i++) {
                System.arraycopy(variableBuf[i], 0, row, index, variableBuf[i].length);
                index += variableBuf[i].length;
            }

            //reset
            Arrays.fill(fixedBuf, (byte) 0);
            Arrays.fill(bitBuf, (byte) 0);

            //append
            rows.add(row);
            rowCount++;
        } catch (Exception e) {
            throw new OPException("bind row error,please check col index and real row index," + e.getMessage());
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<byte[]> getRows() {
        return rows;
    }

    public List<OPColumn> getColumns() {
        return columns;
    }

    public String getTableName() {
        return tableName;
    }

    public int getFixedLen() {
        return fixedLen;
    }

    public int getBitLen() {
        return bitLen;
    }

//    public void close() {
//        destroy();
//    }
//
//    private void destroy() {
//    }

//    public void clear() {
//        Arrays.fill(fixedBuf, (byte) 0);
//        Arrays.fill(bitBuf, (byte) 0);
//        rowCount = 0;
//        bufSize = defaultBufSize;
//        rows.clear();
//    }
}
