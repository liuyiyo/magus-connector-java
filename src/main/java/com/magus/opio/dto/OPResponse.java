package com.magus.opio.dto;

import com.magus.opio.OPConstant;
import com.magus.opio.io.OPInputStream;
import com.magus.opio.io.OPOutputStream;

public class OPResponse extends OPRequest {

	private OPDataset dataSet;

	public static void printCallStatck() {
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		if (stackElements != null) {
			for (int i = 0; i < stackElements.length; i++) {
				System.out.print(stackElements[i].getClassName() + "\t");
				System.out.print(stackElements[i].getFileName() + "\t");
				System.out.print(stackElements[i].getLineNumber() + "\t");
				System.out.println(stackElements[i].getMethodName());
				System.out.println("-----------------------------------");
			}
		}
	}

	public OPResponse(OPInputStream in, OPOutputStream out) {
		super(in, out);
		this.dataSet = new OPDataset(io, table);
	}

	public int getError() {

		Object obj = props.get(OPConstant.Props.PropErrNo.key);
		int errNo = 0;
		if (obj instanceof Integer) {
			errNo = (Integer) obj;
		} else if (obj instanceof Long) {
			errNo = ((Long) obj).intValue();
		} else {
			return 0;
		}
		return errNo;
	}

	public String getErrorMessage() {
		Object message = props.get(OPConstant.Props.PropError.key);
		return message != null ? message.toString() : "";
	}

	public String getOption(String key) {
		Object message = props.get(key);
		return message != null ? message.toString() : "";
	}

	public OPDataset getDataSet() {
		return dataSet;
	}

	public void close() {
		destroy();
	}

	public void destroy() {
		super.destroy();
		dataSet.destroy();
		dataSet = null;
	}
}
