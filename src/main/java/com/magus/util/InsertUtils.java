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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yong.xia@hand-china.com
 * @version 1.0
 * @ClassName insertUtils
 * @description
 * @date 2021/6/18 13:55
 * @since JDK 1.8
 */

public class InsertUtils {


    public static String user = "sis";
    public static String password = "openplant";
    private final static String cluster = "kudu";
    private final static String ip = "10.130.168.192";
    private final static int port = 19604;
    private final static String dbName = "dataBase";

    private static final String TOKEN_TOPIC_WRITE = "Token.Write";
    public static final String DB = "db";
    public static final String TIME = "Time";
    private static final String US = ":";

    public static void setAndInsertBatch(List<?> list, String tableName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, OPException {
        if(list==null||list.size() ==0){
            throw new OPException("dataList is not null");
        }
        // 获取实体类的所有属性，返回Field数组
        OPTable table = new OPTable(tableName);
        Class clz = list.get(0).getClass();
        Field[] field = clz.getDeclaredFields();
        // 获取属性的名字
        String[] oName = new String[field.length];
        String[] oType = new String[field.length];
        for (int i = 0; i < field.length; i++) {
            // 获取属性的名字
            String name = field[i].getName();
            oName[i] = name;
            // 获取属性类型
            String type = field[i].getGenericType().toString();
            oType[i] = type;

            //关键。。。可访问私有变量
            field[i].setAccessible(true);

            if (type.equals("class java.lang.String")) {
                table.addColumn(name, OPType.VtString);
            }
            if (type.equals("class java.lang.Integer") || type.equalsIgnoreCase("int")) {
                table.addColumn(name, OPType.VtInt32);

            }
            if (type.equals("class java.lang.Short") || type.equals("short")) {
                table.addColumn(name, OPType.VtInt16);

            }
            if (type.equals("class java.lang.Long") || type.equals("long")) {
                table.addColumn(name, OPType.VtInt64);

            }
            if (type.equals("class java.lang.Double") || type.equalsIgnoreCase("double")) {
                table.addColumn(name, OPType.VtDouble);

            }
            if (type.equals("class java.lang.Boolean") || type.equalsIgnoreCase("boolean")) {
                table.addColumn(name, OPType.VtBool);

            }
            if (type.equals("class java.lang.Float") || type.equalsIgnoreCase("float")) {
                table.addColumn(name, OPType.VtFloat);

            }
            if (type.equals("class java.util.Date") || type.equalsIgnoreCase("date") || type.equalsIgnoreCase("datetime")) {
                table.addColumn(name, OPType.VtDateTime);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            Object insertEntity = list.get(i);
            Field[] insertField = insertEntity.getClass().getDeclaredFields();
            // 获取属性的名字
            String[] insertName = new String[field.length];
            String[] insertType = new String[field.length];
            for (int j = 0; j < insertField.length; j++) {
                // 获取属性的名字
                String name = insertField[j].getName();
                insertName[j] = name;
                // 获取属性类型
                String type = insertField[j].getGenericType().toString();
                insertType[j] = type;

                //关键。。。可访问私有变量
                insertField[j].setAccessible(true);
                Method m = insertEntity.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                if (type.equalsIgnoreCase("class java.lang.String")) {
                    table.setColumnString(j, m.invoke(insertEntity) == null ? "" : m.invoke(insertEntity).toString());
                    continue;
                }
                if (type.equals("class java.lang.Short") || type.equals("short")) {

                    table.setColumnShort(j, (Short) m.invoke(insertEntity));
                    continue;
                }
                if (type.equals("class java.lang.Long") || type.equalsIgnoreCase("Long")) {
                    table.setColumnLong(j, (Long) m.invoke(insertEntity));
                    continue;
                }
                if (type.equals("class java.lang.Double") || type.equalsIgnoreCase("double")) {
                    table.setColumnDouble(j, (Double) m.invoke(insertEntity));
                    continue;
                }
                if (insertType.equals("class java.lang.Boolean") || type.equalsIgnoreCase("boolean")) {
                    table.setColumnBool(j, (Boolean) m.invoke(insertEntity));
                    continue;
                }
                if (insertType.equals("class java.lang.Float") || type.equalsIgnoreCase("float")) {
                    table.setColumnFloat(j, (Float) m.invoke(insertEntity));
                    continue;
                }
                if (insertType.equals("class java.util.Date") || type.equalsIgnoreCase("date") || type.equalsIgnoreCase("datetime")) {
                    table.setColumnDatetime(j, (Date) m.invoke(insertEntity));
                    continue;
                }
            }
            table.bindRow();
        }
        insertBatch(table);
    }


    public static void insertBatch(OPTable table) {
        Token token;
        String tokenCache;
        OPIOConnect writeOP;
        OPIOConnect op;
        OPRequest req;
        try {
            Random random = new Random(System.currentTimeMillis());
            long transId = random.nextLong();
            //设置write OPIOConnect
            ConcurrentHashMap<String, Object> caechMap = LocalCache.map;
            //如果缓存为空则获取token，否则直接从本地缓存中拿
            if (caechMap.get("token") == null || caechMap.get("tokenCache") == null) {
                //连接lcs
                writeOP = new OPIOConnect(ip, port, 120, user, password, OPConst.ZIP_MODEL_Block);
                token = get_token(writeOP, cluster, TOKEN_TOPIC_WRITE);
                tokenCache = token.getTokenCache();
                LocalCache.put("token", token);
                LocalCache.put("tokenCache", tokenCache);
            } else {
                token = (Token) LocalCache.get("token");
                tokenCache = (String) LocalCache.get("tokenCache");
            }
            String[] addresses = token.getDb_listen_addr_list();
            for (String address : addresses) {
                String[] splits = address.split(US);
                String ip = splits[0];
                int port = Integer.parseInt(splits[1]);
                try {
                    if (caechMap.get("op") == null || caechMap.get("req") == null) {
                        op = new OPIOConnect(ip, port, 120, OPConst.ZIP_MODEL_Block);
                        req = new OPRequest(op);
                        LocalCache.put("op", op);
                        LocalCache.put("req", req);
                    } else {
                        op = (OPIOConnect) LocalCache.get("op");
                        req = (OPRequest) LocalCache.get("req");
                    }
                    req.setID(transId);
                    req.setAction(LtConst.ActionInsert);
                    req.setOption(LtConst.SubjectToken, tokenCache);
                    req.setOption(DB, dbName);
                    req.setOption(TIME, (double) (System.currentTimeMillis() / 1000));
                    req.setTable(table);
                    req.Write();
                    req.WriteContent();
                    req.Flush();
                    OPResponse res = req.GetResponse();
                    System.out.println("err no:" + res.getError());
                    System.out.println("err msg:" + res.getErrorMessage());
                    res.destroy();
                    //commit
                    data_commit(transId, op);
                } catch (OPException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }

    private static Token get_token(OPIOConnect op, String cluster, String token_topic) {
        try {
            OPRequest req = new OPRequest(op);
            req.setAction(LtConst.ActionSelect);
            req.setOption(LtConst.PropSubject, token_topic);
            req.setOption(LtConst.DBCluster, cluster);
            req.WriteAndFlush();
            OPResponse res = req.GetResponse();
            String tokenStr = res.getOption(LtConst.SubjectToken);
            if (tokenStr.equals(""))
                throw new OPException("token is nil");
            Algorithm algorithm = Algorithm.HMAC256("magus so excellent");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(tokenStr);
            String extraInfo = jwt.getClaim("ExtraInfo").asString();
            Token token = JSON.parseObject(extraInfo, Token.class);
            token.setTokenCache(tokenStr);
            res.destroy();
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void data_commit(long transId, OPIOConnect writeOP) {
        try {
            OPRequest req = new OPRequest(writeOP);
            req.setID(transId);
            req.setAction(LtConst.ActionCommit);
            req.WriteAndFlush();
            OPResponse res = req.GetResponse();
            res.destroy();
            //writeOP.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


