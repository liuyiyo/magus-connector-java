package com.magus.opio;

public class OPConstant {

	public static void main(String[] args) {
		System.out.println(0x100);
		System.out.println(MAX_UINT8);
		System.out.println(MAX_UINT16);
		System.out.println((long) MAX_UINT32);
		System.out.println(MAX_INT32);
	}

	public static final int max_buffer_size = 65535;
	public static final int propCapacity = 255;

	public static final int MAX_INT8 = Byte.MAX_VALUE;
	public static final int MAX_UINT8 = 0xff;
	public static final int MAX_INT16 = Short.MAX_VALUE;
	public static final int MAX_UINT16 = 0xffff;
	public static final int MAX_INT32 = Integer.MAX_VALUE;
	public static final long MAX_UINT32 = 0xFFFFFFFFL;
	public static final long MAX_INT64 = Long.MAX_VALUE;

	public enum OPIOType {

		ArrayMask(16, 0), VtNull(0, 0), VtBool(1, 1), VtInt8(2, 1), VtInt16(3, 2), VtInt32(4, 4), VtInt64(5, 8), VtFloat(6, 4), VtDouble(7, 8), VtDateTime(8, 8), VtString(9, 0), VtBinary(10, 0), VtObject(11, 0), VtMap(12, 0), VtStructure(13, 0), VtSlice(14, 0), VtArray(14, 0), VtEnum(15, 0), BOOL_ARRAY(17, 0), INT8_ARRAY(18, 0), INT16_ARRAY(19, 0), INT32_ARRAY(20, 0), INT64_ARRAY(21, 0), FLOAT_ARRAY(
				22, 0), DOUBLE_ARRAY(23, 0), DATETIME_ARRAY(24, 0), STRING_ARRAY(25, 0), BINARY_ARRAY(26, 0), OBJECT_ARRAY(27, 0), STRUCT(28, 0), VtRow(32, 0), VtRowArray(48, 0);

		public int code;
		public int len;

		private OPIOType(int code, int len) {
			this.code = code;
			this.len = len;
		}

	}

	public enum Opers {
		OperEQ(0), OperNE(1), OperGT(2), OperLT(3), OperGE(4), OperLE(5), OperIn(6), OperNotIn(7), OperLike(8), OperNotLike(9), OperReqexp(10);
		public int code;

		private Opers(int code) {
			this.code = code;
		}
	}

	public enum Relation {
		AND(0), Or(1);
		public int code;

		private Relation(int code) {
			this.code = code;
		}
	}

	public enum Sync {
		SYNC("0"), ASYNC("1");
		public String code;

		private Sync(String code) {
			this.code = code;
		}
	}

	public enum OPType {
		TypeAX(0), TypeDX(1), TypeI2(2), TypeI4(3), TypeR8(4), TypeI8(5), TypeTX(6), TypeBN(7), TypeAny(15);
		public int code;

		private OPType(int code) {
			this.code = code;
		}
	}

	public static int MAGIC = 0x10203040;

	public enum OPCmdCode {
		cmdSelect(110), cmdUpdate(120), cmdInsert(130), cmdDelete(140), cmdReplace(150);
		public int code;

		private OPCmdCode(int code) {
			this.code = code;
		}
	}

	public enum OPURL {
		Scheme(0x20000000), ID(0x21000000), Static(0x22000000), Dynamic(0x23000000), ChildID(0x24000000), ChildStatic(0x25000000), ChildDynamic(0x26000000), Alarm(0x2A000000), ChildAlarm(0x2B000000), Archive(0x30000000), CloudNodes(0x40000000), CloudNode(0x41000000), CloudDBs(0x42000000), CloudDB(0x43000000), CloudTime(0x44000000), Echo(0x46000000), ;
		public int code;

		private OPURL(int code) {
			this.code = code;
		}
	}

	public enum OPFlag {
		flagByName(1), flagByID(2), flagFilter(4), flagNoDS(0x40), flagNoTM(0x80), flagWall(0x100), flagMMI(0x200), flagSync(0x400), flagCtrl(0x800), flagFeedback(0x1000), flagCache(0x2000), ;
		public int code;

		private OPFlag(int code) {
			this.code = code;
		}
	}

	public enum Props {
		PropReqId("Reqid"), PropService("Service"), PropTable("Table"), PropAction("Action"), PropSubject("Subject"), PropOption("Option"), PropOrderBy("OrderBy"), PropLimit("Limit"), PropAsync("Async"), PropColumns("Columns"), PropKey("Key"), PropIndexes("Indexes"), PropFilters("Filters"), PropError("Error"), PropErrNo("Errno"), PropSQL("SQL"), PropToken("Token"), PropDB("db"), PropTimestamp(
				"Time"), PropSnapshot("Snapshot"), PropSubscribe("Subscribe"), LPropTaskId("TaskId"), LPropCluster("Cluster"), LPropNasPath("NasPath");
		public String key;

		private Props(String key) {
			this.key = key;
		}
	}

	public enum OpenPlantTables {
		TableNode("Node"), TablePoint("Point"), TableRealtime("Realtime"), TableArchive("Archive"), TableStat("Stat"), TableAlarm("Alarm"), TableAAlarm("AAlarm"), ;
		public String key;

		private OpenPlantTables(String key) {
			this.key = key;
		}
	}

	public enum Action {
		Create("Create"), Select("Select"), Insert("Insert"), Update("Update"), Replace("Replace"), Delete("Delete"), ExecSQL("ExecSQL"), ;
		public String key;

		private Action(String key) {
			this.key = key;
		}
	}

	public enum ZIP_MODEL {
		ZIP_MODEL_Uncompressed(0), ZIP_MODEL_Frame(1), ZIP_MODEL_Block(2), ;
		public int code;

		private ZIP_MODEL(int code) {
			this.code = code;
		}
	}

	public static int[] _uppercase = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 47, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91,
			92, 93, 94, 95, 96, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174,
			175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251,
			252, 253, 254, 255, };

	public static byte[] statusHeader = { 0x10, 0x20, 0x30, 0x40, 0, 0, 0, 110, 0x46, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) 0xA5, 0x10, 0x20, 0x30, 0x40, };

	// msgpack
	public static byte mpBin8 = (byte) 0xc4;
	public static int mpBin16 = 0xc5;
	public static int mpBin32 = 0xc6;

}
