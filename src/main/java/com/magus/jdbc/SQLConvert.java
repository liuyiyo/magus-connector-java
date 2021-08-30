package com.magus.jdbc;

public class SQLConvert {
	public static String removeComment(String sql) {
		if (sql == null) {
			return null;
		}

		int index = 0;
		int start = 0;
		StringBuffer contentBuffer = new StringBuffer();
		String temp = null;
		String tmpResult = null;
		boolean isStart = false;
		while ((index = sql.indexOf('\n', start)) != -1) {
			temp = sql.substring(start, index);
			start = index + 1;
			// 去空格和;
			tmpResult = new String(temp.replaceAll("\\s{2,}", " "));
			if (tmpResult != null) {
				// 去除同一行/* */注释
				if (tmpResult.indexOf("/*") != -1 && tmpResult.indexOf("*/") != -1) {
					// 最小匹配
					tmpResult = tmpResult.replaceAll("\\/\\*.*?\\*\\/", "");
				} else if (tmpResult.indexOf("/*") != -1 && tmpResult.indexOf("*/") == -1 && tmpResult.indexOf("--") == -1) {
					// /*开始
					isStart = true;
				} else if (tmpResult.indexOf("/*") != -1 && tmpResult.indexOf("--") != -1 && tmpResult.indexOf("--") < tmpResult.indexOf("/*")) {
					// 同时存在--/*
					tmpResult = tmpResult.replaceAll("--.*", "");
				}
				if (isStart && tmpResult.indexOf("*/") != -1) {
					// */结束
					isStart = false;
					continue;
				}
				// 去除同一行的--注释
				tmpResult = new String(tmpResult.replaceAll("--.*", ""));
			}
			if (!isStart) {
				// 保留换行符
				contentBuffer.append(tmpResult).append("\r\n");
			}
		}
		contentBuffer.append(sql.substring(start));
		temp = contentBuffer.toString();
		// 保留换行符
		temp = new String(temp.replaceAll("\\s{2,}\\r\\n", " "));
		return temp;
	}
}