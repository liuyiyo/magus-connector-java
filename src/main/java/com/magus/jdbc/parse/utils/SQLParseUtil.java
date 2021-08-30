package com.magus.jdbc.parse.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.magus.jdbc.parse.constant.SqlType;
import com.magus.jdbc.parse.entity.ParserResult;
import com.magus.jdbc.parse.handle.SQLParse;
import com.magus.opio.OPException;
import com.magus.util.BeanFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * @ClassName SQLParseUtil
 * @description：SQL解析工具类
 * @author：liuyi
 * @Date：2021/5/7 9:46
 */
public class SQLParseUtil {
    private static final String SQL_SELECT = "select";
    private static final String PREFIX_BRACKET = "[";
    private static final String SUFFIX_BRACKET = "]";
    private static final String PRFFIX_BRACKET = "(";
    private static final String FULLWIDTH_GRAVE_ACCENT = "`";
    private static final String SU_FULLWIDTH_GRAVE_ACCENT = "]`";
    private static final String BLANK = " ";
    private static final String COMMA = ",";
    private static final String MATCH_DATE = "MATCH_DATE";
    private static final String PRIMARY_KEY = "PRIMARY";


    /**
     * @Author liuyi
     * @Description //sql解析
     * @Date 2021/5/7 12:02
     * @Param [sql]
     * @return com.magus.jdbc.parse.entity.ParserResult
     **/
    public static ParserResult parserSql(String sql) throws SQLException {
        //如果是查询语句，不需要解析
        if(sql.toLowerCase().contains(SQL_SELECT)) return null;
        ParserResult parserResult = new ParserResult();
        String classifyColumn = findClassifyColumn(sql);
        if(classifyColumn!=null&&!"".equals(classifyColumn)){
            parserResult.setClassifyColumn(classifyColumn);
        }
        String matchDateColumn = findMatchDateColumn(sql);
        if(matchDateColumn!=null&&!"".equals(matchDateColumn)){
            parserResult.setMatchDateColumn(matchDateColumn);
            //将MATCH_DATE替换掉，不然sql解析器会无法解析
            sql = sql.toLowerCase().replace(MATCH_DATE.toLowerCase(),"");
        }
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = stmtList.get(0);
        String className = sqlStatement.getClass().getSimpleName();
        SQLParse bean = BeanFactory.getBean(SQLParse.class, SqlType.findClassName(className));
        bean.parse(sqlStatement,parserResult);
        return parserResult;
    }

    /**
     * @Author liuyi
     * @Description 处理特殊sql
     * @Date 2021/5/23 12:12
     * @Param [sql]
     * @return java.lang.String
     **/
    public static String handleSpecialSql(String sql){
        //找到[之前的第一个空格或者逗号，找到这个位置插入`符号。
        //先找到[的位置
        Set<Integer> set = new TreeSet<>();
        for (int i = 0; i <= sql.lastIndexOf(PREFIX_BRACKET); i++) {
            int index = sql.indexOf(PREFIX_BRACKET, i);
            set.add(index);
        }
        StringBuffer stringBuffer = new StringBuffer(sql);
        //找到[符号前面第一个逗号或者空格的位置
        int index = 1;
        for (Integer integer : set) {
            List<Integer> list = new ArrayList<>();
            list.add(sql.substring(0, integer).lastIndexOf(BLANK));
            list.add(sql.substring(0, integer).lastIndexOf(COMMA));
            list.add(sql.substring(0, integer).lastIndexOf(PRFFIX_BRACKET));
            int max = Collections.max(list);
            stringBuffer.insert(max+index,FULLWIDTH_GRAVE_ACCENT);
            index ++;
        }
        return stringBuffer.toString().replaceAll(SUFFIX_BRACKET,SU_FULLWIDTH_GRAVE_ACCENT);
    }

    /**
     * @Author liuyi
     * @Description //获取匹配的时间字段
     * @Date 2021/8/13 13:09
     * @Param [sql]
     * @return java.lang.String
     **/
    public static String findMatchDateColumn(String sql) throws SQLException{
        if(sql.toUpperCase().contains(MATCH_DATE)){
            String[] columns = sql.split(COMMA);
            for (String column : columns) {
                if(column.contains(MATCH_DATE)){
                    String[] childs = column.split(BLANK);
                    for (int i = 0; i < childs.length; i++) {
                        if(childs[i].trim().contains(MATCH_DATE)){
                            if(i == 0){
                                //如果columnName为MATCH_DATE则抛出异常
                                throw new SQLException("columnName不能命名为match_date");
                            }
                            return childs[0].trim().toLowerCase();
                        }
                    }
                }
            }
        }
        return "";
    }



    public static String findClassifyColumn(String sql) throws SQLException{
        if(sql.toUpperCase().contains(PRIMARY_KEY)){
            String[] columns = sql.split(COMMA);
            for (String column : columns) {
                if(column.contains(PRIMARY_KEY)){
                    String[] childs = column.split(BLANK);
                    for (int i = 0; i < childs.length; i++) {
                        if(childs[i].trim().contains(PRIMARY_KEY)){
                            if(i == 0){
                                //如果columnName为MATCH_DATE则抛出异常
                                throw new SQLException("columnName不能命名为primary");
                            }
                            return childs[0].trim().toLowerCase();
                        }
                    }
                }
            }
        }
        return "";
    }
    public static void main(String[] args) throws Exception{
//        String sql = "SELECT safa,asfas,sdgsdg,ce_v[0:5] a, pt_t[1:6] b FROM roewe where cluster = 'roewe' limit 10";
//        String sql = "SELECT to_string(ce_v[0:5],',') FROM roewe where ce_v[0:4] > 3.52 limit 10";
        String sql = "CREATE TABLE testly (" +
                "bool_test VtBool DEFAULT false," +
                "datetime_test VtDateTime MATCH_DATE DEFAULT 0," +
                "int8_test VtInt8 DEFAULT 0," +
                "int16_test VtInt16 DEFAULT 0," +
                "int32_test VtInt32 DEFAULT 0," +
                "int64_test VtInt64 DEFAULT 0," +
                "float_test VtFloat DEFAULT 0.0," +
                "double_test VtDouble DEFAULT 0.0," +
                "string_test VtString DEFAULT NULL," +
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
                "binary_array_test BINARY_ARRAY DEFAULT NULL," +
                "cluster VtString NOT NULL DEFAULT 'roewe')";
        System.out.println(parserSql(sql));

    }
}
