package com.magus.opio;

public class OPConst {

    // CMD type
    public static final byte CMD_ACK = 1; // /< 报警确认
    public static final byte CMD_INHIBIT = 2; // /< 报警抑制
    public static final byte CMD_PADLOCK = 3; // /< 挂牌
    public static final byte CMD_FORCE_ON = 4; // /< 手工强制
    public static final byte CMD_FORCE_OFF = 5; // /< 取消强制
    public static final byte CMD_CONTROL = 6; // /< 控制命令，下发到IO驱动。

    // CMD mask
    public static final short ALARM_UNACK = 0x20; // /< 报警未确认
    public static final short ALARM_OFF = 0x40; // /< 报警已切除
    public static final short PADLOCK = 0x2000; // /< 挂牌操作位
    public static final short FORCE = 0x0100; // /< 点值为强制输入

    // CMD keycode
    public static final String OPTION_CONTROL = "control"; // /< 控制指令
    public static final String OPTION_MMI = "mmi"; // /< 操作指令

    // mask Array
    public static int cmdMask[] = {ALARM_UNACK, ALARM_OFF, PADLOCK, FORCE, FORCE, 0};

    // Request Key
    public static final String KEY_REQID = "Reqid";
    public static final String KEY_SERVICE = "Service";
    public static final String KEY_ACTION = "Action";
    public static final String KEY_TABLE = "Table";
    public static final String KEY_FILTERS = "Filters";
    public static final String KEY_ORDERBY = "OrderBy";
    public static final String KEY_LIMIT = "Limit";
    public static final String KEY_OPTION = "Option";
    public static final String KEY_COLUMNS = "Columns";
    public static final String KEY_INDEXES = "Indexes";
    public static final String KEY_ERRNO = "Errno";
    public static final String KEY_ERROR = "Error";
    public static final String KEY_SQL = "SQL";

    public static final String KEY_TOKEN = "Token";
    public static final String KEY_DB = "db";
    public static final String KEY_TIMESTAMP = "Time";
    public static final String KEY_TOPIC = "Topic";
    public static final String KEY_TASKID = "TaskId";
    public static final String KEY_CLUSTER = "Cluster";
    public static final String KEY_NASPATH = "NasPath";

    public static final String SERVICE_OPENPLANT = "openPlant";

    // Action names
    public static final String ACTION_SELECT = "Select";
    public static final String ACTION_INSERT = "Insert";
    public static final String ACTION_UPDATE = "Update";
    public static final String ACTION_REPLACE = "Replace";
    public static final String ACTION_DELETE = "Delete";
    public static final String ACTION_CREATE = "Create";
    public static final String ACTION_DROP = "Drop";
    public static final String ACTION_EXECSQL = "ExecSQL";
    public static final String ACTION_RUN = "Run";
    public static final String ACTION_SCHEDULE = "Schedule";
    public static final String ACTION_ECHO = "Echo";
    public static final String ACTION_LOGIN = "Login";
    // Data tables
    public static final String TABLE_DATABASE = "Database";
    public static final String TABLE_NODE = "Node";
    public static final String TABLE_POINT = "Point";
    public static final String TABLE_REALTIME = "Realtime";
    public static final String TABLE_ARCHIVE = "Archive";

    public static final String KEYCODE_ID = "ID";
    public static final String KEYCODE_UD = "UD";
    public static final String KEYCODE_GN = "GN";

    public static final byte ZIP_MODEL_Uncompressed = 0;
    public static final byte ZIP_MODEL_Frame = 1;
    public static final byte ZIP_MODEL_Block = 2;


}
