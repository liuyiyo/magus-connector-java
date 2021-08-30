package com.magus.util;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.magus.jdbc.constant.LtConst;
import com.magus.opio.OPConst;
import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.Token;
import com.magus.opio.dto.OPRequest;
import com.magus.opio.dto.OPResponse;
import com.magus.opio.dto.OPTable;
import com.magus.opio.net.OPIOConnect;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LtOpUtil
 * @description：lightning opio工具类
 * @author：liuyi
 * @Date：2021/7/7 16:03
 */
@Slf4j
public class LtOpUtil {

    private static final String TOKEN_TOPIC_WRITE = "Token.Write";
    public static final String DB = "db";
    public static final String TIME = "Time";
    private static final String US = ":";
    //连接信息
    private ConnectionInfo connectionInfo;

    public LtOpUtil(ConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo;
    }

    /**
     * @Author liuyi
     * @Description //批量插入数据
     * @Date 2021/7/8 15:51
     * @Param [list]
     * @return void
     **/
    public synchronized void batchInsert(List<?> list) throws OPException{
        if(this.connectionInfo == null) throw new OPException("请先设置连接信息");
        if (list == null || list.size() == 0) {
            throw new OPException("dataList is not null");
        }
        // 获取实体类的所有属性，返回Field数组
        Class clz = list.get(0).getClass();
        Field[] fields = clz.getDeclaredFields();
        //创建OPTable对象
        OPTable table = new OPTable(connectionInfo.getTableName());
        //设置表头
        for (Field field : fields) {
            //关键。。。可访问私有变量
            field.setAccessible(true);
            //获取属性名称
            String name = field.getName();
            // 获取属性类型
            String type = field.getGenericType().getTypeName();
            byte opType;
            switch (type){
                case "java.lang.Byte":
                case "byte":
                    opType = OPType.VtInt8;
                    break;
                case "java.lang.Short":
                case "short":
                    opType = OPType.VtInt16;
                    break;
                case "java.lang.Integer":
                case "int":
                    opType = OPType.VtInt32;
                    break;
                case "java.lang.Long":
                case "long":
                    opType = OPType.VtInt64;
                    break;
                case "java.lang.Float":
                case "float":
                    opType = OPType.VtFloat;
                    break;
                case "java.lang.Double":
                case "double":
                    opType = OPType.VtDouble;
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    opType = OPType.VtBool;
                    break;
                case "java.util.Date":
                case "date":
                    opType = OPType.VtDateTime;
                    break;
                case "byte[]":
                    opType = OPType.VtBinary;
                    break;
                case "java.lang.String":
                    opType = OPType.VtString;
                    break;
                case "java.util.List<java.lang.Boolean>":
                    opType = OPType.BOOL_ARRAY;
                    break;
                case "java.util.List<java.lang.Byte>":
                    opType = OPType.INT8_ARRAY;
                    break;
                case "java.util.List<java.lang.Short>":
                    opType = OPType.INT16_ARRAY;
                    break;
                case "java.util.List<java.lang.Integer>":
                    opType = OPType.INT32_ARRAY;
                    break;
                case "java.util.List<java.lang.Long>":
                    opType = OPType.INT64_ARRAY;
                    break;
                case "java.util.List<java.lang.Float>":
                    opType = OPType.FLOAT_ARRAY;
                    break;
                case "java.util.List<java.lang.Double>":
                    opType = OPType.DOUBLE_ARRAY;
                    break;
                case "java.util.List<java.lang.String>":
                    opType = OPType.STRING_ARRAY;
                    break;
                case "java.util.List<java.util.Date>":
                    opType = OPType.DATETIME_ARRAY;
                    break;
                case "java.util.List<byte[]>":
                    opType = OPType.BINARY_ARRAY;
                    break;
                default:
                    throw new OPException("==================="+type+"类型不存在");

            }
            table.addColumn(name,opType);
        }
        //设置表数据
        list.forEach(obj->{
            Field[] insertField = obj.getClass().getDeclaredFields();
            for (int i = 0; i < insertField.length; i++) {
                //关键。。。可访问私有变量
                Field field = insertField[i];
                field.setAccessible(true);
                //获取属性名称
                String name = field.getName();
                // 获取属性类型
                String type = field.getGenericType().getTypeName();
                try {
                    Method m = obj.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                    switch (type){
                        case "java.lang.Byte":
                        case "byte":
                            table.setColumnByte(i, (byte) m.invoke(obj));
                            continue;
                        case "java.lang.Short":
                        case "short":
                            table.setColumnShort(i, (short) m.invoke(obj));
                            continue;
                        case "java.lang.Integer":
                        case "int":
                            table.setColumnInt(i, (int) m.invoke(obj));
                            continue;
                        case "java.lang.Long":
                        case "long":
                            table.setColumnLong(i, (long) m.invoke(obj));
                            continue;
                        case "java.lang.Float":
                        case "float":
                            table.setColumnFloat(i, (float) m.invoke(obj));
                            continue;
                        case "java.lang.Double":
                        case "double":
                            table.setColumnDouble(i, (double) m.invoke(obj));
                            continue;
                        case "java.lang.Boolean":
                        case "boolean":
                            table.setColumnBool(i, (boolean) m.invoke(obj));
                            continue;
                        case "java.util.Date":
                        case "date":
                            table.setColumnDatetime(i, (Date) m.invoke(obj));
                            continue;
                        case "byte[]":
                            table.setColumnBinary(i, (byte[]) m.invoke(obj));
                            continue;
                        case "java.lang.String":
                            table.setColumnString(i, m.invoke(obj) == null ? "" : m.invoke(obj).toString());
                            continue;
                        case "java.util.List<java.lang.Boolean>":
                            table.setColumnBoolArray(i, (List<Boolean>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.Byte>":
                            table.setColumnByteArray(i, (List<Byte>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.Short>":
                            table.setColumnShortArray(i, (List<Short>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.Integer>":
                            table.setColumnIntegerArray(i, (List<Integer>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.Long>":
                            table.setColumnLongArray(i, (List<Long>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.Float>":
                            table.setColumnFloatArray(i, (List<Float>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.Double>":
                            table.setColumnDoubleArray(i, (List<Double>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.lang.String>":
                            table.setColumnStringArray(i, (List<String>) m.invoke(obj));
                            continue;
                        case "java.util.List<java.util.Date>":
                            table.setColumnDateArray(i, (List<Date>) m.invoke(obj));
                            continue;
                        case "java.util.List<byte[]>":
                            table.setColumnBinaryArray(i, (List<byte[]>) m.invoke(obj));
                            continue;
                        default:
                            throw new OPException("==================="+type+"类型不存在");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            try {
                table.bindRow();
            } catch (OPException e) {
                e.printStackTrace();
            }
        });
        insertBatchByOPIO(table);
    }


    /**
     * @Author liuyi
     * @Description //根据opio批量插入数据
     * @Date 2021/7/9 9:58
     * @Param [table]
     * @return void
     **/
    private void insertBatchByOPIO(OPTable table) throws OPException {
        Object obj = LocalCache.get(connectionInfo.toString());
        if (obj != null) {
            //如果缓存中已经存在，则直接写数据
            writeData((OPIOConnect) obj, table);
        } else {
            //如果缓存没有，则需要先去lcs去ldb的连接信息
            Token token = get_token();
            String tokenCache = token.getTokenCache();
            String[] addresses = token.getDb_listen_addr_list();
            for (String address : addresses) {
                try {
                    String[] splits = address.split(US);
                    String ip = splits[0];
                    int port = Integer.parseInt(splits[1]);
                    OPIOConnect writeOp = new OPIOConnect(ip, port, 120, OPConst.ZIP_MODEL_Block);
                    writeOp.setTokenCache(tokenCache);
                    writeData(writeOp, table);
                    break;
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }
    }

    /**
     * @Author liuyi
     * @Description //写数据
     * @Date 2021/7/9 14:33
     * @Param [writeOp, table]
     * @return void
     **/
    private void writeData(OPIOConnect writeOp,OPTable table) throws OPException {
        try {
            long transId =  new Random(System.currentTimeMillis()).nextLong();
            OPRequest req = new OPRequest(writeOp);
            req.setID(transId);
            req.setAction(LtConst.ActionInsert);
            req.setOption(LtConst.SubjectToken, writeOp.getTokenCache());
            req.setOption(DB, connectionInfo.getDbName());
            req.setOption(TIME, (double) (System.currentTimeMillis() / 1000));
            req.setTable(table);
            req.Write();
            req.WriteContent();
            req.Flush();
            OPResponse res = req.GetResponse();
            log.info("writeData err no:" + res.getError());
            log.info("writeData err msg:" + res.getErrorMessage());
            res.destroy();
            //commit
            data_commit(transId, writeOp);
            LocalCache.put(connectionInfo.toString(),writeOp);
        }catch (Exception e){
            log.error("向ldb写入数据失败",e);
            throw new OPException("向ldb写入数据失败");
        }

    }


    /**
     * @Author liuyi
     * @Description //根据cluster去lcs获取token，token返回该cluster对应ldb的连接信息
     * 此连接信息中，只有一个是适合当前服务器进行连接的
     * @Date 2021/7/9 11:11
     * @Param [op, cluster, token_topic]
     * @return com.magus.opio.Token
     **/
    private Token get_token() {
        try {
            OPIOConnect lcsOP = new OPIOConnect(
                    connectionInfo.getIp(),
                    connectionInfo.getPort(),
                    120,
                    connectionInfo.getUser(),
                    connectionInfo.getPassword(),
                    OPConst.ZIP_MODEL_Block);
            OPRequest req = new OPRequest(lcsOP);
            req.setAction(LtConst.ActionSelect);
            req.setOption(LtConst.PropSubject, TOKEN_TOPIC_WRITE);
            req.setOption(LtConst.DBCluster, connectionInfo.getCluster());
            req.WriteAndFlush();
            OPResponse res = req.GetResponse();
            String tokenStr = res.getOption(LtConst.SubjectToken);
            if (tokenStr.equals(""))
                throw new OPException("获取的token为空，原因===="+res.getErrorMessage());
            Algorithm algorithm = Algorithm.HMAC256("magus so excellent");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(tokenStr);
            String extraInfo = jwt.getClaim("ExtraInfo").asString();
            Token token = JSON.parseObject(extraInfo, Token.class);
            token.setTokenCache(tokenStr);
            res.destroy();
            lcsOP.destroy();
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Author liuyi
     * @Description //commint数据
     * @Date 2021/7/9 10:46
     * @Param [transId, writeOP]
     * @return void
     **/
    private void data_commit(long transId, OPIOConnect writeOP) throws OPException {
        try {
            OPRequest req = new OPRequest(writeOP);
            req.setID(transId);
            req.setAction(LtConst.ActionCommit);
            req.WriteAndFlush();
            OPResponse res = req.GetResponse();
            res.destroy();
        } catch (Exception e) {
            log.error("向ldb commit失败",e);
            throw new OPException("向ldb commit失败");
        }
    }
}


