package com.magus;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.magus.entity.RelationInfo;
import com.magus.entity.TestStructPerson;
import com.magus.entity.Testly;
import com.magus.jdbc.Driver;
import com.magus.jdbc.constant.LtConst;
import com.magus.opio.OPConst;
import com.magus.opio.OPException;
import com.magus.opio.OPType;
import com.magus.opio.Token;
import com.magus.opio.dto.OPRequest;
import com.magus.opio.dto.OPResponse;
import com.magus.opio.dto.OPTable;
import com.magus.opio.net.OPIOConnect;
import com.magus.opio.utils.array.ArrayDecoder;
import com.magus.opio.utils.map.MapDecoder;
import com.magus.opio.utils.struct.StructConvert;
import com.magus.opio.utils.struct.StructDecoder;
import com.magus.util.ConnectionInfo;
import com.magus.util.LtOpUtil;
import org.junit.Test;
import java.lang.reflect.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
/**
 * @ClassName LightningTest
 * @description：
 * @author：liuyi
 * @Date：2021/4/27 17:35
 */
public class LightningTest {

    private final static String ip = "192.168.0.101";
    //    private final static String ip = "10.130.6.3";//汽研
    private final static int port = 19504;
    private final static String cluster = "kudu";
    private final static String dbName = "dataBase";
    public static String url = "jdbc:lighting://192.168.0.104:19504/dataBase?cluster=kudu";
//    public static String url = "jdbc:lighting://10.130.168.192:19604/dataBase?cluster=kudu";//汽研
    //public static String url = "jdbc:openplant://127.0.0.1:8400/RTDB";
    public static String user = "sis";
    public static String password = "openplant";
    private static final String TOKEN_TOPIC_WRITE = "Token.Write";
    public static final String DB = "db";
    public static final String TIME = "Time";
    private static final String US = ":";



    /**
     * @Author liuyi
     * @Description // struct打包解包测试
     * @Date 2021/8/24 13:46
     * @Param []
     * @return void
     **/
    @Test
    public void structTest() throws Exception{
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stringList.add(i+"");
        }
        Map<String,Object> map  = new HashMap<>();
        map.put("key1","hello");
        map.put("key2","hello2");
        TestStructPerson testStructPerson = new TestStructPerson("wxh", 10, 300, 3.3f, 8.888d,
                new RelationInfo("重庆武隆", "13356487845", map, stringList));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            StructConvert.encodeStruct(testStructPerson);
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    /**
     * @Author liuyi
     * @Description //批量插入（OPIO）
     * @Date 2021/7/12 10:30
     * @Param []
     * @return void
     **/
    @Test
    public void batchInsertByOPIO() throws OPException {
        //批量插入测试，一次最好不要超过10000条
        //组装参数
        List<Testly> testlyList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<Boolean> list = new ArrayList<>();
            list.add(true);
            list.add(false);
            Testly test = new Testly();
            test.setBoolTest(true);
            test.setBinaryTest("DSGDSG".getBytes());
            test.setBool_ArrayTest(list);
            test.setDoubleTest(2.13);
            test.setFloatTest(2.1f);
            test.setDatetimeTest(new java.util.Date());
            test.setInt8Test((byte)8);
            test.setInt16Test((short) 16);
            test.setInt32Test(32);
            test.setInt64Test(64L);
            test.setStringTest("my name is liuyi");
            test.setCluster("kudu");
            testlyList.add(test);
        }
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setIp(ip);
        connectionInfo.setPort(port);
        connectionInfo.setUser(user);
        connectionInfo.setPassword(password);
        connectionInfo.setCluster("kudu");
        connectionInfo.setDbName(dbName);
        connectionInfo.setTableName("testly5");
        LtOpUtil ltOpUtil = new LtOpUtil(connectionInfo);
        ltOpUtil.batchInsert(testlyList);
    }

    /**
     * @Author liuyi
     * @Description //创建表
     * @Date 2021/7/12 10:29
     * @Param []
     * @return void
     **/
    @Test
    public void createTable() {
        String sql = "CREATE TABLE testly3 (" +
                "bool_test VtBool DEFAULT false," +
                "int8_test VtInt8 DEFAULT 0," +
                "int16_test VtInt16 DEFAULT 0," +
                "int32_test VtInt32 DEFAULT 0," +
                "int64_test VtInt64 DEFAULT 0," +
                "float_test VtFloat DEFAULT 0.0," +
                "double_test VtDouble DEFAULT 0.0," +
                "datetime_test VtDateTime MATCH_DATE DEFAULT 0," +
                "string_test VtString PRIMARY KEY DEFAULT NULL," +
                "binary_test VtBinary DEFAULT NULL," +
                "map_test VtMap DEFAULT NULL," +
                "structure_test VtStructure DEFAULT NULL," +
                "array_test VtArray DEFAULT NULL," +
                "bool_array_test BOOL_ARRAY DEFAULT NULL," +
                "int8_array_test INT8_ARRAY DEFAULT NULL," +
                "int16_array_test INT16_ARRAY DEFAULT NULL," +
                "int32_array_test INT32_ARRAY DEFAULT NULL," +
                "int64_array_test INT64_ARRAY DEFAULT NULL," +
                "float_array_test FLOAT_ARRAY DEFAULT NULL," +
                "double_array_test DOUBLE_ARRAY DEFAULT NULL," +
                "datetime_array_test DATETIME_ARRAY DEFAULT NULL," +
                "string_array_test STRING_ARRAY DEFAULT NULL," +
                "binary_array_test BINARY_ARRAY DEFAULT NULL)";
        Connection connection = null;
        Statement ptmt = null;
        try {
            //2.创建连接
            connection = DriverManager.getConnection(url, user, password);
            //3.创建Statement对象
            ptmt = connection.prepareStatement(sql);
            //4.执行sql并获取结果
            ptmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            closeConnection(connection, ptmt, null);
        }
    }

    /**
     * @return void
     * @Author liuyi
     * @Description //批量插入
     * @Date 2021/5/6 14:45
     * @Param []
     **/
    @Test
    public void batchInsertByJDBC() {
        Connection conn = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            //预编译SQL，减少sql执行，防止sql注入
            ptmt = conn.prepareStatement("INSERT INTO testly3 (bool_test,int8_test,int16_test," +
                    "int32_test,int64_test,float_test,double_test,datetime_test,string_test,binary_test," +
                    "map_test,structure_test,array_test,bool_array_test,int8_array_test,int16_array_test," +
                    "int32_array_test,int64_array_test,float_array_test,double_array_test,datetime_array_test," +
                    "string_array_test,binary_array_test,cluster) VALUES " +
                    "('?','?','?','?','?','?','?','?','?','?','?','?'," +
                    "'?','?','?','?','?','?','?','?','?','?','?','?')");
            //传参
            List<Boolean> booleanList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                booleanList.add(i%2==0);
            }
            List<Byte> byteList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                byteList.add((byte)i);
            }
            List<Short> shortList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                shortList.add((short)i);
            }
            List<Integer> integerList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                integerList.add(i);
            }

            List<Long> longList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                longList.add((long)i);
            }

            List<Float> floatList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                floatList.add((float)i);
            }

            List<Double> doubleList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                doubleList.add((double)i);
            }

            List<java.util.Date> dateList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                dateList.add(new java.util.Date());
            }

            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                stringList.add(i+"");
            }

            List<byte[]> bytesList = new ArrayList<>();
            for (byte i = 0; i < 10; i++) {
                bytesList.add(new byte[]{i,(byte)(i+1)});
            }
            Map<String,Object> map  = new HashMap<>();
            map.put("key1","hello");
            map.put("key2","hello2");
            TestStructPerson testStructPerson = new TestStructPerson("wxh", 10, 300, 3.3f, 8.888d,
                    new RelationInfo("重庆武隆", "13356487845", map, stringList));
            List<TestStructPerson> list = new ArrayList<>();
            list.add(testStructPerson);
            long start = System.currentTimeMillis();
            for (int i = 1; i < 2; i++) {
                ptmt.setBoolean(1, false);
                ptmt.setByte(2, (byte) i);
                ptmt.setShort(3, (short) (i + 1));
                ptmt.setInt(4, i + 2);
                ptmt.setLong(5, Long.valueOf(i + 3));
                ptmt.setFloat(6, i + 4);
                ptmt.setDouble(7, i + 5);
                ptmt.setDate(8, new Date(System.currentTimeMillis()));
                ptmt.setString(9, i + "");
                ptmt.setBytes(10, (Math.random() * 100 + "").getBytes());
                ptmt.setObject(11, map);
                ptmt.setObject(12, testStructPerson);
                ptmt.setObject(13, list);//VtArray
                ptmt.setObject(14, booleanList);//BOOL_ARRAY
                ptmt.setObject(15, byteList);//INT8_ARRAY
                ptmt.setObject(16, shortList);//INT16_ARRAY
                ptmt.setObject(17, integerList);//INT32_ARRAY
                ptmt.setObject(18, longList);//INT64_ARRAY
                ptmt.setObject(19, floatList);//FLOAT_ARRAY
                ptmt.setObject(20, doubleList);//DOUBLE_ARRAY
                ptmt.setObject(21, dateList);//DATETIME_ARRAY
                ptmt.setObject(22, stringList);//STRING_ARRAY
                ptmt.setObject(23, bytesList);//BINARY_ARRAY
                ptmt.setString(24, "kudu");
                ptmt.addBatch();
            }
            ptmt.executeBatch();
            long end = System.currentTimeMillis();
            System.out.println("一共耗时" + (end - start) / 1000 + "秒");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            closeConnection(conn, ptmt, resultSet);
        }
    }

    /**
     * @Author liuyi
     * @Description //opio方式数据插入
     * @Date 2021/6/2 17:24
     * @Param []
     * @return void
     **/
    @Test
    public void opioInsert(){
        try {
            OPTable table = new OPTable("testly5");
            //设置表头
            table.addColumn("boolTest", OPType.VtBool);//0
            table.addColumn("int8Test", OPType.VtInt8);//1
            table.addColumn("int16Test", OPType.VtInt16);//2
            table.addColumn("int32Test", OPType.VtInt32);//3
            table.addColumn("int64Test", OPType.VtInt64);//4
            table.addColumn("floatTest", OPType.VtFloat);//5
            table.addColumn("doubleTest", OPType.VtDouble);//6
            table.addColumn("datetimeTest", OPType.VtDateTime);//7
            table.addColumn("stringTest", OPType.VtString);//8
            table.addColumn("binaryTest", OPType.VtBinary);//9
            table.addColumn("bool_ArrayTest", OPType.BOOL_ARRAY);//10
            table.addColumn("cluster", OPType.VtString);//13
            //设置表数据
            List<Boolean> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(i%2==0);
            }
            table.setColumnBool(0, false);
            table.setColumnByte(1, (byte) 1);
            table.setColumnShort(2, (short) 2);
            table.setColumnInt(3, 3);
            table.setColumnLong(4, 4L);
            table.setColumnFloat(5, 3.3f);
            table.setColumnDouble(6, 4.4);
            table.setColumnDatetime(7, new java.util.Date());
            table.setColumnString(8, "strings");
            table.setColumnBinary(9,(Math.random()*100+"").getBytes());
            table.setColumnBoolArray(10,list);
            table.setColumnString(11,"kudu");
            table.bindRow();
            insert(table);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * @Author liuyi
     * @Description //插入数据
     * @Date 2021/6/3 11:37
     * @Param [table]
     * @return void
     **/
    public void insert( OPTable table){
        try {
            Random random = new Random(System.currentTimeMillis());
            long transId = random.nextLong();
            OPIOConnect writeOP = new OPIOConnect(ip,port,120,user,password,OPConst.ZIP_MODEL_Block);
            //设置write OPIOConnect
            Token token = get_token(writeOP,cluster,TOKEN_TOPIC_WRITE);
            String tokenCache = token.getTokenCache();
            String[] addresses = token.getDb_listen_addr_list();
            for (String address : addresses) {
                String[] splits = address.split(US);
                String ip = splits[0];
                int port = Integer.parseInt(splits[1]);
                try {
                    OPIOConnect op = new OPIOConnect(ip, port, 120, OPConst.ZIP_MODEL_Block);
                    OPRequest req = new OPRequest(op);
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
                    data_commit(transId,op);
                } catch (OPException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @Author liuyi
     * @Description //获取token
     * @Date 2021/5/7 15:10
     * @Param [isRead]
     * @return com.magus.opio.Token
     **/
    private Token get_token(OPIOConnect op,String cluster,String token_topic) {
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

    /**
     * @Author liuyi
     * @Description //commit
     * @Date 2021/5/7 15:46
     * @Param [transId, ip, port]
     * @return void
     **/
    private void data_commit(long transId,OPIOConnect writeOP) {
        try {
            OPRequest req = new OPRequest(writeOP);
            req.setID(transId);
            req.setAction(LtConst.ActionCommit);
            req.WriteAndFlush();
            OPResponse res = req.GetResponse();
            res.destroy();
            writeOP.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @return json对象
     * @Author liuyi
     * @Description //查询（jdbc方式）
     * @Date 14:12 2021/4/7
     * @Param []
     **/
    @Test
    public void queryJsonJDBC() {
//        String sql = "SELECT msg, fb_t, w_i1, w_i2, w_i3, w_i4, ce_v, ce_v_1, pt_t, pt_t_1 FROM roewe where cluster = 'roewe' limit 10";
//        String sql = "SELECT ce_v[0:5] a, pt_t[1:6] b FROM roewe where cluster = 'roewe' limit 10;";
//        String sql = "select vin,e_t,ce_v[0:5] a,hv,lv from kudu_gb32960_extend where cluster = 'kudu' limit 10";
//        String sql = "select count(*) as countTotal from kudu_gb32960_extend v\n" +
//                " join vehicle_info on v.vin = vehicle_info.vin\n" +
//                " where\n" +
//                " e_t >= 1619798935 and e_t <= 1619798935   and v.vin = 'LJNDFV1T8GN600052'";
        List<String> sqlList = new ArrayList<>();
//        sqlList.add("SELECT COUNT(*) AS totoal_frame_count, from_unixtime(MIN(e_t)) AS access_date\n" +
//                "\t, datediff(now(), from_unixtime(MIN(e_t))) AS access_day_count\n" +
//                "FROM kudu_gb32960_extend v\n" +
//                "WHERE v.vin = 'LJNDFV1T4HN601040'");
//        sqlList.add("SELECT min(e_t)\n" +
//                "FROM kudu_gb32960_extend\n" +
//                "WHERE vin = 'LJNDFV1T4HN601040'");
//        sqlList.add("SELECT vin, e_t\n" +
//                "FROM kudu_gb32960_extend\n" +
//                "WHERE vin = 'LJNDFV1T8GN600052'\n" +
//                "ORDER BY e_t DESC\n" +
//                "LIMIT 1");
//        sqlList.add("select * ,(ht-40) as ht,(lt-40) as lt from kudu_gb32960_extend where vin = 'LJNDFV1T8GN600052' and e_t = 1585122173");
//        sqlList.add("SELECT vin, e_t\n" +
//                "FROM kudu_gb32960_extend\n" +
//                "WHERE vin = 'LJNDFV1T8GN600052'\n" +
//                "\tAND lon > 0\n" +
//                "\tAND lat > 0\n" +
//                "ORDER BY e_t DESC\n" +
//                "LIMIT 1");
//        sqlList.add("SELECT mt_n, mt_t, ve_s, ch_s, wk_m\n" +
//                "\t, spd, v, i, odo, soc\n" +
//                "\t, dc_s, gps, ce_n, ce_v, hv\n" +
//                "\t, lv, source, msg, vin, e_t\n" +
//                "\t, ge_s, lat, lon, lv_w, in_w\n" +
//                "\t, mt_c, mt_s, mc_t, mt_n, m_tq\n" +
//                "\t, mt_t, mt_v, mt_i, _RowID\n" +
//                "FROM kudu_gb32960_extend\n" +
//                "WHERE vin = 'LJNDFV1T8GN600052'\n" +
//                "\tAND e_t >= 943891200\n" +
//                "\tAND e_t < 1626850378\n" +
//                "\tAND lat > 0\n" +
//                "ORDER BY e_t ASC");
//        sqlList.add("SELECT\n" +
//                "\tsoc,\n" +
//                "\te_t AS time from kudu_gb32960_extend v\n" +
//                "JOIN vehicle_info ON v.vin = vehicle_info.vin\n" +
//                "WHERE\n" +
//                "\tsoc BETWEEN 0\n" +
//                "AND 100\n" +
//                "AND e_t >= 1585122173\n" +
//                "AND e_t <= 1625068799\n" +
//                "AND plate_number LIKE concat('%', '琼A2012X', '%')\n" +
//                "AND v.vin LIKE concat(\n" +
//                "\t'%',\n" +
//                "\t'LJNDFV1T8GN600052',\n" +
//                "\t'%'\n" +
//                ")\n" +
//                "ORDER BY\n" +
//                "\te_t ASC");
//        sqlList.add("SELECT e_t, v.vin, plate_number, ht - 40 AS ht\n" +
//                "\t, lt - 40 AS lt, ht - lt AS t_sub\n" +
//                "FROM kudu_gb32960_extend v\n" +
//                "\tJOIN vehicle_info ON v.vin = vehicle_info.vin\n" +
//                "WHERE e_t >= 1623859200\n" +
//                "\tAND e_t <= 1625068799\n" +
//                "\tAND v.vin = 'LJNDFV1T8GN600052'\n" +
//                "\tAND plate_number = '琼A2012X'\n" +
//                "ORDER BY e_t ASC");
//        sqlList.add("SELECT e_t, v.vin, ce_n, v, i\n" +
//                "\t, plate_number, ce_v\n" +
//                "FROM kudu_gb32960_extend v\n" +
//                "\tJOIN vehicle_info ON v.vin = vehicle_info.vin\n" +
//                "WHERE e_t >= 1623859200\n" +
//                "\tAND e_t <= 1625068799\n" +
//                "\tAND v.vin = 'LJNDFV1T8GN600052'\n" +
//                "\tAND plate_number = '琼A2012X'\n" +
//                "\tAND rand() > 0.7\n" +
//                "ORDER BY e_t ASC");
//        sqlList.add("SELECT v.vin, e_t, mt_n, m_tq\n" +
//                "FROM kudu_gb32960_extend v\n" +
//                "\tJOIN vehicle_info ON v.vin = vehicle_info.vin\n" +
//                "WHERE mt_n != '45535'\n" +
//                "\tAND mt_n != '45534'\n" +
//                "\tAND rand() > 0.9\n" +
//                "\tAND e_t >= 1622563200\n" +
//                "\tAND e_t <= 1625068799\n" +
//                "\tAND plate_number = '琼A2012X'\n" +
//                "\tAND v.vin = 'LJNDFV1T8GN600052'\n" +
//                "ORDER BY e_t ASC");
        sqlList.add("select string_array_test from testly limit 1");
        List<Map<String, Object>> mapList = new ArrayList<>();
        Connection connection = null;
        Statement ptmt = null;
        ResultSet resultSet = null;
        try {
            //2.创建连接
            connection = DriverManager.getConnection(url, user, password);
            //3.创建Statement对象
            ptmt = connection.createStatement();
            long allStertTime = System.currentTimeMillis();
            for (int j = 0; j < sqlList.size(); j++) {
                long stertTime = System.currentTimeMillis();
                //4.执行sql并获取结果
                resultSet = ptmt.executeQuery(sqlList.get(j));
                //5.获取结果集合的元数据对象
                ResultSetMetaData metaData = resultSet.getMetaData();
                //6.获取字段数(由于每一行的列肯定是一样的，所以写在外面，防止每次循环都去获取)
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> map = new HashMap<>();
                    //7.2循环每一列的数据
                    int timeIndex = 0;
                    for (int i = 1; i <= columnCount; i++) {
                        //获取列的名称
                        String columnName = metaData.getColumnName(i);
                        switch (metaData.getColumnType(i)) {
                            case Types.BOOLEAN:
                                map.put(columnName, resultSet.getBoolean(i));
                                break;
                            case Types.TINYINT:
                                map.put(columnName, resultSet.getByte(i));
                                break;
                            case Types.SMALLINT:
                                map.put(columnName, resultSet.getShort(i));
                                break;
                            case Types.INTEGER:
                                map.put(columnName, resultSet.getInt(i));
                                break;
                            case Types.BIGINT:
                                map.put(columnName, resultSet.getLong(i));
                                break;
                            case Types.FLOAT:
                                map.put(columnName, resultSet.getFloat(i));
                                break;
                            case Types.DOUBLE:
                                map.put(columnName, resultSet.getDouble(i));
                                break;
                            case Types.TIMESTAMP:
                                map.put(columnName, resultSet.getDate(i));
                                break;
                            case Types.VARCHAR:
                                map.put(columnName, resultSet.getString(i));
                                break;
                            case Types.BINARY:
                                map.put(columnName, resultSet.getBytes(i));
                                break;
                            case Types.JAVA_OBJECT:
                                map.put(columnName, resultSet.getObject(i));
                                break;
                            default:
                                break;
                        }
                    }
                    mapList.add(map);
                }
                mapList.parallelStream()
                        .forEach(map -> System.out.println(map));
                System.out.println("第"+(j+1)+"条sql查询结果条数"+mapList.size());
                long endTime = System.currentTimeMillis();
                System.out.println("第"+(j+1)+"条sql耗时"+(endTime-stertTime)+"毫秒");
            }
            long allEndTime = System.currentTimeMillis();
            System.out.println("所有sql一共耗时"+(allEndTime-allStertTime)+"毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            closeConnection(connection, ptmt, resultSet);
        }

    }


    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author liuyi
     * @Description //hikari数据库连接池测试
     * @Date 2021/4/30 9:46
     * @Param [sql]
     **/
//    @Test
//    public void hikariCPTest() {
//        String sql = "select int8Test,int16Test from testly3";
//        List<Map<String, Object>> mapList = new ArrayList<>();
//        Connection connection = null;
////        Statement statement = null;
//        PreparedStatement ptmt = null;
//        ResultSet resultSet = null;
//        try {
//            HikariDataSource ds = new HikariDataSource();
//            ds.setJdbcUrl(url);
//            ds.setUsername(user);
//            ds.setPassword(password);
//            connection = ds.getConnection();
//            //2.创建连接
////            connection = DriverManager.getConnection(url, user, password);
//            //3.创建Statement对象
////            statement = connection.createStatement();
//            ptmt = connection.prepareStatement(sql);
////            ptmt.setString(1,"LSJA24030HS163858");
////            ptmt.setInt(2,144);
//            //4.执行sql并获取结果
////            resultSet = statement.executeQuery(sql);
//            resultSet = ptmt.executeQuery();
//            //5.获取结果集合的元数据对象
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            //6.获取字段数(由于每一行的列肯定是一样的，所以写在外面，防止每次循环都去获取)
//            int columnCount = metaData.getColumnCount();
//            while (resultSet.next()) {
//                Map<String, Object> map = new HashMap<>();
//                //7.2循环每一列的数据
//                int timeIndex = 0;
//                for (int i = 1; i <= columnCount; i++) {
//                    //获取列的名称
//                    String columnName = metaData.getColumnName(i);
//                    switch (metaData.getColumnType(i)) {
//                        case Types.BOOLEAN:
//                            map.put(columnName, resultSet.getBoolean(i));
//                            break;
//                        case Types.TINYINT:
//                            map.put(columnName, resultSet.getByte(i));
//                            break;
//                        case Types.SMALLINT:
//                            map.put(columnName, resultSet.getShort(i));
//                            break;
//                        case Types.INTEGER:
//                            map.put(columnName, resultSet.getInt(i));
//                            break;
//                        case Types.BIGINT:
//                            map.put(columnName, resultSet.getLong(i));
//                            break;
//                        case Types.FLOAT:
//                            map.put(columnName, resultSet.getFloat(i));
//                            break;
//                        case Types.DOUBLE:
//                            map.put(columnName, resultSet.getDouble(i));
//                            break;
//                        case Types.TIMESTAMP:
//                            map.put(columnName, resultSet.getDate(i));
//                            break;
//                        case Types.VARCHAR:
//                            map.put(columnName, resultSet.getString(i));
//                            break;
//                        case Types.BINARY:
//                            map.put(columnName, resultSet.getBytes(i));
//                            break;
//                        case Types.JAVA_OBJECT:
//                            //object类型可能是Structure,Map，Array中的一种
//                            Object object = resultSet.getObject(i);
//                            if (object != null) {
//                                if (object instanceof StructDecoder) {
//                                    StructDecoder structDecoder = (StructDecoder) object;
//                                    if (structDecoder.isEmpty()) break;
//                                    map.put(columnName, structDecoder.createMap());
//                                } else if (object instanceof MapDecoder) {
//                                    MapDecoder mapDecoder = (MapDecoder) object;
//                                    if (mapDecoder.isEmpty()) break;
//                                    map.put(columnName, mapDecoder.createMap(null));
//                                } else if (object instanceof ArrayDecoder) {
//                                    ArrayDecoder arrayDecoder = (ArrayDecoder) object;
//                                    if (arrayDecoder.isEmpty()) break;
//                                    map.put(columnName, arrayDecoder.createList(null));
//                                }
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                mapList.add(map);
//            }
//            mapList.parallelStream()
//                    .forEach(map -> System.out.println(map));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //关闭连接
//            closeConnection(connection, ptmt, resultSet);
//        }
//    }

    /**
     * @Author liuyi
     * @Description c3p0数据库连接池测试
     * @Date 2021/5/10 16:43
     * @Param []
     * @return void
     **/
//    @Test
//    public void c3p0Test() {
//        String sql = "SELECT * FROM testly1 limit 10";
//        List<Map<String, Object>> mapList = new ArrayList<>();
//        Connection connection = null;
//        PreparedStatement ptmt = null;
//        ResultSet resultSet = null;
//        try {
//            ComboPooledDataSource cpds = new ComboPooledDataSource();
//            cpds.setJdbcUrl(url);
//            cpds.setUser(user);
//            cpds.setPassword(password);
//            connection = cpds.getConnection();
//            //2.创建连接
////            connection = DriverManager.getConnection(url, user, password);
//            //3.创建Statement对象
////            statement = connection.createStatement();
//            ptmt = connection.prepareStatement(sql);
////            ptmt.setString(1,"LSJA24030HS163858");
////            ptmt.setInt(2,144);
//            //4.执行sql并获取结果
////            resultSet = statement.executeQuery(sql);
//            resultSet = ptmt.executeQuery();
//            //5.获取结果集合的元数据对象
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            //6.获取字段数(由于每一行的列肯定是一样的，所以写在外面，防止每次循环都去获取)
//            int columnCount = metaData.getColumnCount();
//            while (resultSet.next()) {
//                Map<String, Object> map = new HashMap<>();
//                //7.2循环每一列的数据
//                int timeIndex = 0;
//                for (int i = 1; i <= columnCount; i++) {
//                    //获取列的名称
//                    String columnName = metaData.getColumnName(i);
//                    switch (metaData.getColumnType(i)) {
//                        case Types.BOOLEAN:
//                            map.put(columnName, resultSet.getBoolean(i));
//                            break;
//                        case Types.TINYINT:
//                            map.put(columnName, resultSet.getByte(i));
//                            break;
//                        case Types.SMALLINT:
//                            map.put(columnName, resultSet.getShort(i));
//                            break;
//                        case Types.INTEGER:
//                            map.put(columnName, resultSet.getInt(i));
//                            break;
//                        case Types.BIGINT:
//                            map.put(columnName, resultSet.getLong(i));
//                            break;
//                        case Types.FLOAT:
//                            map.put(columnName, resultSet.getFloat(i));
//                            break;
//                        case Types.DOUBLE:
//                            map.put(columnName, resultSet.getDouble(i));
//                            break;
//                        case Types.TIMESTAMP:
//                            map.put(columnName, resultSet.getDate(i));
//                            break;
//                        case Types.VARCHAR:
//                            map.put(columnName, resultSet.getString(i));
//                            break;
//                        case Types.BINARY:
//                            map.put(columnName, resultSet.getBytes(i));
//                            break;
//                        case Types.JAVA_OBJECT:
//                            //object类型可能是Structure,Map，Array中的一种
//                            Object object = resultSet.getObject(i);
//                            if (object != null) {
//                                if (object instanceof StructDecoder) {
//                                    StructDecoder structDecoder = (StructDecoder) object;
//                                    if (structDecoder.isEmpty()) break;
//                                    map.put(columnName, structDecoder.createMap());
//                                } else if (object instanceof MapDecoder) {
//                                    MapDecoder mapDecoder = (MapDecoder) object;
//                                    if (mapDecoder.isEmpty()) break;
//                                    map.put(columnName, mapDecoder.createMap(null));
//                                } else if (object instanceof ArrayDecoder) {
//                                    ArrayDecoder arrayDecoder = (ArrayDecoder) object;
//                                    if (arrayDecoder.isEmpty()) break;
//                                    map.put(columnName, arrayDecoder.createList(null));
//                                }
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                mapList.add(map);
//            }
//            mapList.parallelStream()
//                    .forEach(map -> System.out.println(map));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //关闭连接
//            closeConnection(connection, ptmt, resultSet);
//        }
//    }


    /**
     * @Author liuyi
     * @Description //dbcp连接池测试
     * @Date 2021/5/10 16:50
     * @Param []
     * @return void
     **/
//    @Test
//    public void dbcpTest() {
//        String sql = "SELECT * FROM testly1 limit 10";
//        List<Map<String, Object>> mapList = new ArrayList<>();
//        Connection connection = null;
//        PreparedStatement ptmt = null;
//        ResultSet resultSet = null;
//        try {
//            BasicDataSource dataSource = new BasicDataSource();
//            dataSource.setUrl(url);
//            dataSource.setUsername(user);
//            dataSource.setPassword(password);
//            connection = dataSource.getConnection();
//            //2.创建连接
////            connection = DriverManager.getConnection(url, user, password);
//            //3.创建Statement对象
////            statement = connection.createStatement();
//            ptmt = connection.prepareStatement(sql);
////            ptmt.setString(1,"LSJA24030HS163858");
////            ptmt.setInt(2,144);
//            //4.执行sql并获取结果
////            resultSet = statement.executeQuery(sql);
//            resultSet = ptmt.executeQuery();
//            //5.获取结果集合的元数据对象
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            //6.获取字段数(由于每一行的列肯定是一样的，所以写在外面，防止每次循环都去获取)
//            int columnCount = metaData.getColumnCount();
//            while (resultSet.next()) {
//                Map<String, Object> map = new HashMap<>();
//                //7.2循环每一列的数据
//                int timeIndex = 0;
//                for (int i = 1; i <= columnCount; i++) {
//                    //获取列的名称
//                    String columnName = metaData.getColumnName(i);
//                    switch (metaData.getColumnType(i)) {
//                        case Types.BOOLEAN:
//                            map.put(columnName, resultSet.getBoolean(i));
//                            break;
//                        case Types.TINYINT:
//                            map.put(columnName, resultSet.getByte(i));
//                            break;
//                        case Types.SMALLINT:
//                            map.put(columnName, resultSet.getShort(i));
//                            break;
//                        case Types.INTEGER:
//                            map.put(columnName, resultSet.getInt(i));
//                            break;
//                        case Types.BIGINT:
//                            map.put(columnName, resultSet.getLong(i));
//                            break;
//                        case Types.FLOAT:
//                            map.put(columnName, resultSet.getFloat(i));
//                            break;
//                        case Types.DOUBLE:
//                            map.put(columnName, resultSet.getDouble(i));
//                            break;
//                        case Types.TIMESTAMP:
//                            map.put(columnName, resultSet.getDate(i));
//                            break;
//                        case Types.VARCHAR:
//                            map.put(columnName, resultSet.getString(i));
//                            break;
//                        case Types.BINARY:
//                            map.put(columnName, resultSet.getBytes(i));
//                            break;
//                        case Types.JAVA_OBJECT:
//                            //object类型可能是Structure,Map，Array中的一种
//                            Object object = resultSet.getObject(i);
//                            if (object != null) {
//                                if (object instanceof StructDecoder) {
//                                    StructDecoder structDecoder = (StructDecoder) object;
//                                    if (structDecoder.isEmpty()) break;
//                                    map.put(columnName, structDecoder.createMap());
//                                } else if (object instanceof MapDecoder) {
//                                    MapDecoder mapDecoder = (MapDecoder) object;
//                                    if (mapDecoder.isEmpty()) break;
//                                    map.put(columnName, mapDecoder.createMap(null));
//                                } else if (object instanceof ArrayDecoder) {
//                                    ArrayDecoder arrayDecoder = (ArrayDecoder) object;
//                                    if (arrayDecoder.isEmpty()) break;
//                                    map.put(columnName, arrayDecoder.createList(null));
//                                }
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//                mapList.add(map);
//            }
//            mapList.parallelStream()
//                    .forEach(map -> System.out.println(map));
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            //关闭连接
//            closeConnection(connection, ptmt, resultSet);
//        }
//    }

    /**
     * @Author liuyi
     * @Description druid连接池测试
     * @Date 2021/5/10 16:53
     * @Param []
     * @return void
     **/
    @Test
    public void druidTest() {
        String sql = "SELECT * FROM testly1 limit 10";
        List<Map<String, Object>> mapList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ptmt = null;
        ResultSet resultSet = null;
        try {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriver(new Driver());
            dataSource.setUrl(url);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            connection = dataSource.getConnection();
            //2.创建连接
//            connection = DriverManager.getConnection(url, user, password);
            //3.创建Statement对象
//            statement = connection.createStatement();
            ptmt = connection.prepareStatement(sql);
//            ptmt.setString(1,"LSJA24030HS163858");
//            ptmt.setInt(2,144);
            //4.执行sql并获取结果
//            resultSet = statement.executeQuery(sql);
            resultSet = ptmt.executeQuery();
            //5.获取结果集合的元数据对象
            ResultSetMetaData metaData = resultSet.getMetaData();
            //6.获取字段数(由于每一行的列肯定是一样的，所以写在外面，防止每次循环都去获取)
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                //7.2循环每一列的数据
                int timeIndex = 0;
                for (int i = 1; i <= columnCount; i++) {
                    //获取列的名称
                    String columnName = metaData.getColumnName(i);
                    switch (metaData.getColumnType(i)) {
                        case Types.BOOLEAN:
                            map.put(columnName, resultSet.getBoolean(i));
                            break;
                        case Types.TINYINT:
                            map.put(columnName, resultSet.getByte(i));
                            break;
                        case Types.SMALLINT:
                            map.put(columnName, resultSet.getShort(i));
                            break;
                        case Types.INTEGER:
                            map.put(columnName, resultSet.getInt(i));
                            break;
                        case Types.BIGINT:
                            map.put(columnName, resultSet.getLong(i));
                            break;
                        case Types.FLOAT:
                            map.put(columnName, resultSet.getFloat(i));
                            break;
                        case Types.DOUBLE:
                            map.put(columnName, resultSet.getDouble(i));
                            break;
                        case Types.TIMESTAMP:
                            map.put(columnName, resultSet.getDate(i));
                            break;
                        case Types.VARCHAR:
                            map.put(columnName, resultSet.getString(i));
                            break;
                        case Types.BINARY:
                            map.put(columnName, resultSet.getBytes(i));
                            break;
                        case Types.JAVA_OBJECT:
                            //object类型可能是Structure,Map，Array中的一种
                            Object object = resultSet.getObject(i);
                            if (object != null) {
                                if (object instanceof StructDecoder) {
                                    StructDecoder structDecoder = (StructDecoder) object;
                                    if (structDecoder.isEmpty()) break;
                                    map.put(columnName, structDecoder.createMap());
                                } else if (object instanceof MapDecoder) {
                                    MapDecoder mapDecoder = (MapDecoder) object;
                                    if (mapDecoder.isEmpty()) break;
                                    map.put(columnName, mapDecoder.createMap(null));
                                } else if (object instanceof ArrayDecoder) {
                                    ArrayDecoder arrayDecoder = (ArrayDecoder) object;
                                    if (arrayDecoder.isEmpty()) break;
                                    map.put(columnName, arrayDecoder.createList(null));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                mapList.add(map);
            }
            mapList.parallelStream()
                    .forEach(map -> System.out.println(map));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            closeConnection(connection, ptmt, resultSet);
        }
    }

    /**
     * @return void
     * @Author liuyi
     * @Description //关闭jdbc连接
     * @Date 14:24 2021/4/7
     * @Param [connection, statement, resultSet]
     **/
    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test(){

        try {
            Field field = JdbcConstants.class.getField("POLARDB_DRIVER");
            //将字段的访问权限设为true：即去除private修饰符的影响
            field.setAccessible(true);
            /*去除final修饰符的影响，将字段设为可修改的*/
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null,"com.magus.jdbc.Driver");
            System.out.println(JdbcConstants.POLARDB_DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
