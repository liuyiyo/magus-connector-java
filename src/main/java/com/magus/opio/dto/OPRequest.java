package com.magus.opio.dto;

import com.magus.opio.OPException;
import com.magus.opio.OPConst;
import com.magus.opio.OPType;
import com.magus.opio.io.OPIOBuffer;
import com.magus.opio.io.OPInputStream;
import com.magus.opio.io.OPOutputStream;
import com.magus.opio.net.OPIOConnect;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OPRequest {

    protected OPTable table;
    protected HashMap<String, Object> props; // 属性集合
    private OPOutputStream out;
    private OPInputStream in;
    protected OPIOBuffer io;

    public OPRequest() {
    }

    public OPRequest(OPInputStream in, OPOutputStream out) {
        this.in = in;
        this.out = out;
        io = new OPIOBuffer(in, out);
        props = new HashMap<String, Object>();
        table = new OPTable();
    }

    public OPRequest(OPIOConnect con) {
        this.in = con.getIn();
        this.out = con.getOut();
        io = new OPIOBuffer(in, out);
        props = new HashMap<String, Object>();
        table = new OPTable();
    }

    public void Reset() {
        props = new HashMap<String, Object>();
    }

    public void Write() throws OPException {
        io.EncodeMapStart(props.size());
        // table name
        Object tbName = props.get(OPConst.KEY_TABLE);
        if (tbName != null) {
            io.EncodeString(OPConst.KEY_TABLE);
            io.EncodeValue(tbName);
        }

        // props
        Set<String> propsSet = props.keySet();
        for (String propName : propsSet) {
            if (propName.equals(OPConst.KEY_TABLE))
                continue;
            io.EncodeString(propName);

            if (propName.equals(OPConst.KEY_COLUMNS)) {
                List<OPColumn> columns = table.getColumns();
                io.EncodeArrayStart(columns.size());
                for (OPColumn column : columns) {
                    column.write(io);
                }
            } else if (propName.equals(OPConst.KEY_INDEXES)) {
                OPIndexs indexs = (OPIndexs) props.get(propName);
                indexs.write(io);
            } else if (propName.equals(OPConst.KEY_FILTERS)) {
                OPFilter[] filters = (OPFilter[]) props.get(propName);
                io.EncodeArrayStart(filters.length);
                for (OPFilter filter : filters) {
                    filter.write(io);
                }
            } else {
                io.EncodeValue(props.get(propName));
            }
        }
    }

    public void WriteAndFlush() throws OPException {
        Write();
        io.EncodeNil();
        io.flush(true);
    }

    public void WriteContent() throws OPException {
        List<byte[]> rows = table.getRows();
        io.EncodeArrayStart(table.getRowCount());
        for (byte[] row : rows) {
            io.EncodeExtendLen(row.length, OPType.VtRow);
            io.PutBytes(row);
        }
    }

    public void Flush() throws OPException {
        io.EncodeNil();
        io.flush(true);
    }

    public void Read() throws OPException {
        int size = io.DecodeMapStart();
        for (int i = 0; i < size; i++) {
            String key = io.DecodeString();

            if (OPConst.KEY_TABLE.equals(key)) {
                String v = io.DecodeString();
                table.setTableName(v);
                props.put(key, v);
            } else if (key.equals(OPConst.KEY_COLUMNS)) {
                int num = io.DecodeArrayStart();
                for (int j = 0; j < num; j++) {
                    OPColumn col = new OPColumn();
                    col.read(io);
                    table.addColumn(col.getColumnName(), (byte) col.getType());
                }
                props.put(key, table.getColumns());
            } else if (key.equals(OPConst.KEY_INDEXES)) {
                OPIndexs indexs = new OPIndexs();
                indexs.read(io);
                props.put(key, indexs);
            } else if (key.equals(OPConst.KEY_FILTERS)) {
                int num = io.DecodeArrayStart();
                OPFilter[] filters = new OPFilter[num];
                for (int k = 0; k < num; k++) {
                    OPFilter filter = new OPFilter();
                    filter.read(io);
                    filters[k] = filter;
                }
                props.put(key, filters);
            } else {
                Object objV = io.DecodeValue();
                props.put(key, objV);
            }
        }
    }

    public OPResponse GetResponse() throws OPException {
        OPResponse opResponse = MakeResponse();
        opResponse.Read();
        return opResponse;
    }

    public OPResponse MakeResponse() throws OPException {
        return new OPResponse(in, out);
    }

    public synchronized void setOption(String key, String value) {
        props.put(key, value);
    }

    public synchronized void setOption(String key, int value) {
        setOption(key, new Long(value));
    }

    public synchronized void setOption(String key, long value) {
        props.put(key, value);
    }

    public synchronized void setOption(String key, double value) {
        props.put(key, value);
    }

    public synchronized void setCluster(String value) {
        props.put(OPConst.KEY_CLUSTER, value);
    }

    public synchronized void setID(long id) {
        props.put(OPConst.KEY_REQID, id);
    }

    public synchronized void setService(String serviceName) {
        props.put(OPConst.KEY_SERVICE, serviceName);
    }

    public synchronized void setAction(String action) {
        props.put(OPConst.KEY_ACTION, action);
    }

    public synchronized void setSQL(String sql) {
        props.put(OPConst.KEY_SQL, sql);
    }

    public synchronized void setTopic(String topic) {
        props.put(OPConst.KEY_TOPIC, topic);
    }

    public synchronized void setTimestamp(Date ts) {
        setTimestamp(ts.getTime() / 1000);
    }

    public synchronized void setTimestamp(double ts) {
        props.put(OPConst.KEY_TIMESTAMP, ts);
    }

    public synchronized void setTable(OPTable table) {
        this.table = table;
        props.put(OPConst.KEY_TABLE, table.getTableName());
        props.put(OPConst.KEY_COLUMNS, table.getColumns());
    }

    public synchronized void setFilters(OPFilter[] filters) {
        props.put(OPConst.KEY_FILTERS, filters);
    }

    public synchronized void setIndices(String code, long[] keys) {
        OPIndexs indexs = new OPIndexs();
        indexs.type = OPType.INT64_ARRAY;
        indexs.key_i64 = keys;
        props.put(OPConst.KEY_INDEXES, indexs);
    }

    public synchronized void setIndices(String code, String[] keys) {
        OPIndexs indexs = new OPIndexs();
        indexs.type = OPType.STRING_ARRAY;
        indexs.key_str = keys;
        props.put(OPConst.KEY_INDEXES, indexs);
    }

    public synchronized void setIDList(int[] keys) {
        long[] ids = new long[keys.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = keys[i];
        }
        setIDList(ids);
    }

    public synchronized void setIDList(long[] keys) {
        setIndices(OPConst.KEYCODE_ID, keys);
    }

    public synchronized void setUDList(int[] keys) {
        long[] ids = new long[keys.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = keys[i];
        }
        setUDList(ids);
    }

    public synchronized void setUDList(long[] keys) {
        setIndices(OPConst.KEYCODE_UD, keys);
    }

    public synchronized void setIndices(String code, int[] keys) {
        long[] ids = new long[keys.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = keys[i];
        }
        setIndices(code, ids);
    }

    public synchronized void setNameList(String[] keys) {
        setIndices(OPConst.KEYCODE_GN, keys);
    }

    public OPResponse execute() throws OPException {
        this.Write();
        this.Flush();
        return GetResponse();
    }

    public void destroy(){
        this.table = null;
        this.props = null; // 属性集合
        this.out = null;
        this.in = null;
        this.io = null;
    }
}
