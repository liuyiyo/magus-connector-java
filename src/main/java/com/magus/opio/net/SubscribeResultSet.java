package com.magus.opio.net;

import java.sql.ResultSet;
import java.sql.SQLException;


public interface SubscribeResultSet {

	public void onResponse(ResultSet resultSet) throws SQLException;

	public boolean onError(int errorCode, Exception ex, Subscribe sub);

}
