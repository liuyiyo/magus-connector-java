package com.magus.jdbc.parse.constant;

/**
 * @Author liuyi
 * @Description sql字段类型枚举类
 * @Date 2021/5/8 11:43
 **/
public enum ColumnType {
    VtBool("vtbool",1),
    VtInt8("vtint8",2),
    VtInt16("vtint16",3),
    VtInt32("vtint32",4),
    VtInt64("vtint64",5),
    VtFloat("vtfloat",6),
    VtDouble("vtdouble",7),
    VtDateTime("vtdatetime",8),
    VtString("vtstring",9),
    VtBinary("vtbinary",10),
    VtMap("vtmap",12),
    VtStructure("vtstructure",13),
    VtArray("vtarray",14),
    BOOL_ARRAY("bool_array",17),
    INT8_ARRAY("int8_array",18),
    INT16_ARRAY("int16_array",19),
    INT32_ARRAY("int32_array",20),
    INT64_ARRAY("int64_array",21),
    FLOAT_ARRAY("float_array",22),
    DOUBLE_ARRAY("double_array",23),
    DATETIME_ARRAY("datetime_array",24),
    STRING_ARRAY("string_array",25),
    BINARY_ARRAY("binary_array",26);

    ColumnType(String type, int value) {
        this.type = type;
        this.value = value;
    }

    private String type;
    private int value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static int findValue(String type){
        for (ColumnType columnType : ColumnType.values()) {
            if(type.equals(columnType.getType())){
                return columnType.getValue();
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(findValue("VtString"));
    }
}
