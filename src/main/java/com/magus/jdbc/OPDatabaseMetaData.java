package com.magus.jdbc;

import java.sql.*;
import java.util.regex.Pattern;

public class OPDatabaseMetaData extends CoreDatabaseMetaData implements DatabaseMetaData {

	protected OPDatabaseMetaData(SQLConnection conn) {
		super(conn);
	}

	public Connection getConnection() {
		return conn;
	}

	public int getDatabaseMajorVersion() {
		return 4;
	}

	public int getDatabaseMinorVersion() {
		return 0;
	}

	public int getDriverMajorVersion() {
		return 1;
	}

	public int getDriverMinorVersion() {
		return 0;
	}

	public int getJDBCMajorVersion() {
		return 2;
	}

	public int getJDBCMinorVersion() {
		return 1;
	}

	public int getDefaultTransactionIsolation() {
		return Connection.TRANSACTION_SERIALIZABLE;
	}

	public int getMaxBinaryLiteralLength() {
		return 0;
	}

	public int getMaxCatalogNameLength() {
		return 0;
	}

	public int getMaxCharLiteralLength() {
		return 0;
	}

	public int getMaxColumnNameLength() {
		return 0;
	}

	public int getMaxColumnsInGroupBy() {
		return 0;
	}

	public int getMaxColumnsInIndex() {
		return 0;
	}

	public int getMaxColumnsInOrderBy() {
		return 0;
	}

	public int getMaxColumnsInSelect() {
		return 0;
	}

	public int getMaxColumnsInTable() {
		return 0;
	}

	public int getMaxConnections() {
		return 0;
	}

	public int getMaxCursorNameLength() {
		return 0;
	}

	public int getMaxIndexLength() {
		return 0;
	}

	public int getMaxProcedureNameLength() {
		return 0;
	}

	public int getMaxRowSize() {
		return 0;
	}

	public int getMaxSchemaNameLength() {
		return 0;
	}

	public int getMaxStatementLength() {
		return 0;
	}

	public int getMaxStatements() {
		return 0;
	}

	public int getMaxTableNameLength() {
		return 0;
	}

	public int getMaxTablesInSelect() {
		return 0;
	}

	public int getMaxUserNameLength() {
		return 0;
	}

	public int getResultSetHoldability() {
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	public int getSQLStateType() {
		return DatabaseMetaData.sqlStateSQL99;
	}

	public String getDatabaseProductName() {
		return "openPlant";
	}

	public String getDatabaseProductVersion() throws SQLException {
		return "4.0";
	}

	public String getDriverName() {
		return "openPlantJDBC";
	}

	public String getDriverVersion() {
		return "1.0";
	}

	public String getExtraNameCharacters() {
		return "";
	}

	public String getCatalogSeparator() {
		return ".";
	}

	public String getCatalogTerm() {
		return "catalog";
	}

	public String getSchemaTerm() {
		return "schema";
	}

	public String getProcedureTerm() {
		return "not_implemented";
	}

	public String getSearchStringEscape() {
		return null;
	}

	public String getIdentifierQuoteString() {
		return " ";
	}

	public String getSQLKeywords() {
		return "";
	}

	public String getNumericFunctions() {
		return "";
	}

	public String getStringFunctions() {
		return "";
	}

	public String getSystemFunctions() {
		return "";
	}

	public String getTimeDateFunctions() {
		return "";
	}

	public String getURL() {
		return conn.getHost();
	}

	public String getUserName() {
		return null;
	}

	public boolean allProceduresAreCallable() {
		return false;
	}

	public boolean allTablesAreSelectable() {
		return true;
	}

	public boolean dataDefinitionCausesTransactionCommit() {
		return false;
	}

	public boolean dataDefinitionIgnoredInTransactions() {
		return false;
	}

	public boolean doesMaxRowSizeIncludeBlobs() {
		return false;
	}

	public boolean deletesAreDetected(int type) {
		return false;
	}

	public boolean insertsAreDetected(int type) {
		return false;
	}

	public boolean isCatalogAtStart() {
		return true;
	}

	public boolean locatorsUpdateCopy() {
		return false;
	}

	public boolean nullPlusNonNullIsNull() {
		return true;
	}

	public boolean nullsAreSortedAtEnd() {
		return !nullsAreSortedAtStart();
	}

	public boolean nullsAreSortedAtStart() {
		return true;
	}

	public boolean nullsAreSortedHigh() {
		return true;
	}

	public boolean nullsAreSortedLow() {
		return !nullsAreSortedHigh();
	}

	public boolean othersDeletesAreVisible(int type) {
		return false;
	}

	public boolean othersInsertsAreVisible(int type) {
		return false;
	}

	public boolean othersUpdatesAreVisible(int type) {
		return false;
	}

	public boolean ownDeletesAreVisible(int type) {
		return false;
	}

	public boolean ownInsertsAreVisible(int type) {
		return false;
	}

	public boolean ownUpdatesAreVisible(int type) {
		return false;
	}

	public boolean storesLowerCaseIdentifiers() {
		return false;
	}

	public boolean storesLowerCaseQuotedIdentifiers() {
		return false;
	}

	public boolean storesMixedCaseIdentifiers() {
		return true;
	}

	public boolean storesMixedCaseQuotedIdentifiers() {
		return false;
	}

	public boolean storesUpperCaseIdentifiers() {
		return false;
	}

	public boolean storesUpperCaseQuotedIdentifiers() {
		return false;
	}

	public boolean supportsAlterTableWithAddColumn() {
		return false;
	}

	public boolean supportsAlterTableWithDropColumn() {
		return false;
	}

	public boolean supportsANSI92EntryLevelSQL() {
		return false;
	}

	public boolean supportsANSI92FullSQL() {
		return false;
	}

	public boolean supportsANSI92IntermediateSQL() {
		return false;
	}

	public boolean supportsBatchUpdates() {
		return true;
	}

	public boolean supportsCatalogsInDataManipulation() {
		return false;
	}

	public boolean supportsCatalogsInIndexDefinitions() {
		return false;
	}

	public boolean supportsCatalogsInPrivilegeDefinitions() {
		return false;
	}

	public boolean supportsCatalogsInProcedureCalls() {
		return false;
	}

	public boolean supportsCatalogsInTableDefinitions() {
		return false;
	}

	public boolean supportsColumnAliasing() {
		return true;
	}

	public boolean supportsConvert() {
		return false;
	}

	public boolean supportsConvert(int fromType, int toType) {
		return false;
	}

	public boolean supportsCorrelatedSubqueries() {
		return false;
	}

	public boolean supportsDataDefinitionAndDataManipulationTransactions() {
		return true;
	}

	public boolean supportsDataManipulationTransactionsOnly() {
		return false;
	}

	public boolean supportsDifferentTableCorrelationNames() {
		return false;
	}

	public boolean supportsExpressionsInOrderBy() {
		return true;
	}

	public boolean supportsMinimumSQLGrammar() {
		return true;
	}

	public boolean supportsCoreSQLGrammar() {
		return true;
	}

	public boolean supportsExtendedSQLGrammar() {
		return false;
	}

	public boolean supportsLimitedOuterJoins() {
		return true;
	}

	public boolean supportsFullOuterJoins() {
		return false;
	}

	public boolean supportsGetGeneratedKeys() {
		return true;
	}

	public boolean supportsGroupBy() {
		return true;
	}

	public boolean supportsGroupByBeyondSelect() {
		return false;
	}

	public boolean supportsGroupByUnrelated() {
		return false;
	}

	public boolean supportsIntegrityEnhancementFacility() {
		return false;
	}

	public boolean supportsLikeEscapeClause() {
		return false;
	}

	public boolean supportsMixedCaseIdentifiers() {
		return true;
	}

	public boolean supportsMixedCaseQuotedIdentifiers() {
		return false;
	}

	public boolean supportsMultipleOpenResults() {
		return false;
	}

	public boolean supportsMultipleResultSets() {
		return false;
	}

	public boolean supportsMultipleTransactions() {
		return true;
	}

	public boolean supportsNamedParameters() {
		return true;
	}

	public boolean supportsNonNullableColumns() {
		return true;
	}

	public boolean supportsOpenCursorsAcrossCommit() {
		return false;
	}

	public boolean supportsOpenCursorsAcrossRollback() {
		return false;
	}

	public boolean supportsOpenStatementsAcrossCommit() {
		return false;
	}

	public boolean supportsOpenStatementsAcrossRollback() {
		return false;
	}

	public boolean supportsOrderByUnrelated() {
		return false;
	}

	public boolean supportsOuterJoins() {
		return true;
	}

	public boolean supportsPositionedDelete() {
		return false;
	}

	public boolean supportsPositionedUpdate() {
		return false;
	}

	public boolean supportsResultSetConcurrency(int t, int c) {
		return t == ResultSet.TYPE_FORWARD_ONLY && c == ResultSet.CONCUR_READ_ONLY;
	}

	public boolean supportsResultSetHoldability(int h) {
		return h == ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	public boolean supportsResultSetType(int t) {
		return t == ResultSet.TYPE_FORWARD_ONLY;
	}

	public boolean supportsSavepoints() {
		return false;
	}

	public boolean supportsSchemasInDataManipulation() {
		return false;
	}

	public boolean supportsSchemasInIndexDefinitions() {
		return false;
	}

	public boolean supportsSchemasInPrivilegeDefinitions() {
		return false;
	}

	public boolean supportsSchemasInProcedureCalls() {
		return false;
	}

	public boolean supportsSchemasInTableDefinitions() {
		return false;
	}

	public boolean supportsSelectForUpdate() {
		return false;
	}

	public boolean supportsStatementPooling() {
		return false;
	}

	public boolean supportsStoredProcedures() {
		return false;
	}

	public boolean supportsSubqueriesInComparisons() {
		return false;
	}

	public boolean supportsSubqueriesInExists() {
		return true;
	}

	public boolean supportsSubqueriesInIns() {
		return true;
	}

	public boolean supportsSubqueriesInQuantifieds() {
		return false;
	}

	public boolean supportsTableCorrelationNames() {
		return false;
	}

	public boolean supportsTransactionIsolationLevel(int level) {
		return level == Connection.TRANSACTION_SERIALIZABLE;
	}

	public boolean supportsTransactions() {
		return true;
	}

	public boolean supportsUnion() {
		return true;
	}

	public boolean supportsUnionAll() {
		return true;
	}

	public boolean updatesAreDetected(int type) {
		return false;
	}

	public boolean usesLocalFilePerTable() {
		return false;
	}

	public boolean usesLocalFiles() {
		return true;
	}

	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}

	public ResultSet getAttributes(String c, String s, String t, String a) throws SQLException {
		if (getAttributes == null) {
			getAttributes = conn.prepareStatement("select null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as ATTR_NAME, null as DATA_TYPE, null as ATTR_TYPE_NAME, null as ATTR_SIZE, null as DECIMAL_DIGITS, null as NUM_PREC_RADIX, null as NULLABLE, null as REMARKS, null as ATTR_DEF, null as SQL_DATA_TYPE, null as SQL_DATETIME_SUB, null as CHAR_OCTET_LENGTH, "
					+ "null as ORDINAL_POSITION, null as IS_NULLABLE, null as SCOPE_CATALOG, null as SCOPE_SCHEMA, null as SCOPE_TABLE, null as SOURCE_DATA_TYPE from schema limit 0;");
		}
		return getAttributes.executeQuery();
	}

	public ResultSet getBestRowIdentifier(String c, String s, String t, int scope, boolean n) throws SQLException {
		if (getBestRowIdentifier == null) {
			getBestRowIdentifier = conn.prepareStatement("select null as SCOPE, null as COLUMN_NAME, null as DATA_TYPE, null as TYPE_NAME, null as COLUMN_SIZE, null as BUFFER_LENGTH, null as DECIMAL_DIGITS, null as PSEUDO_COLUMN from schema limit 0;");
		}
		return getBestRowIdentifier.executeQuery();
	}

	public ResultSet getColumnPrivileges(String c, String s, String t, String colPat) throws SQLException {
		if (getColumnPrivileges == null) {
			getColumnPrivileges = conn.prepareStatement("select null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as COLUMN_NAME, null as GRANTOR, null as GRANTEE, null as PRIVILEGE, null as IS_GRANTABLE from schema limit 0;");
		}
		return getColumnPrivileges.executeQuery();
	}

	// Column type patterns
	protected static final Pattern TYPE_INTEGER = Pattern.compile(".*(INT|BOOL).*");
	protected static final Pattern TYPE_VARCHAR = Pattern.compile(".*(CHAR|CLOB|TEXT|BLOB).*");
	protected static final Pattern TYPE_FLOAT = Pattern.compile(".*(REAL|FLOA|DOUB|DEC|NUM).*");

	public ResultSet getColumns(String c, String s, String tblNamePattern, String colNamePattern) throws SQLException {
		ResultSet rs;
		StringBuilder sql = new StringBuilder(700);
		Statement stat = conn.createStatement();

		checkOpen();

		if (getColumnsTblName == null) {
			getColumnsTblName = conn.prepareStatement("select name,sql from schema where name like ?;");
		}

		// determine exact table name
		getColumnsTblName.setString(1, tblNamePattern);
		rs = getColumnsTblName.executeQuery();
		ResultSetMetaData md = null;
		if (rs.next()) {
			tblNamePattern = rs.getString(1);
			rs.close();
			String sqlTemp = "select * from " + tblNamePattern + " limit 0";
			rs = stat.executeQuery(sqlTemp);
			md = rs.getMetaData();
		}

		sql.append("SELECT " + "  NULL AS TABLE_CAT," + "  NULL AS TABLE_SCHEM," + "  NULL AS TABLE_NAME," + "  NULL AS COLUMN_NAME," + "  NULL AS DATA_TYPE," + "  NULL AS TYPE_NAME," + "  255 AS COLUMN_SIZE," + "  255 AS BUFFER_LENGTH," + "  10 AS DECIMAL_DIGITS," + "  10 AS NUM_PREC_RADIX," + "  NULL AS NULLABLE," + "  NULL AS REMARKS," + "  NULL AS COLUMN_DEF," + "  0 AS SQL_DATA_TYPE,"
				+ "  0 AS SQL_DATETIME_SUB," + "  2000000000 AS CHAR_OCTET_LENGTH," + "  NULL AS ORDINAL_POSITION," + "  NULL AS IS_NULLABLE," + "  NULL AS SCOPE_CATLOG," + "  NULL AS SCOPE_SCHEMA," + "  NULL AS SCOPE_TABLE," + "  NULL AS SOURCE_DATA_TYPE from schema where 0 ");

		int count = (md == null ? 0 : md.getColumnCount());

		for (int i = 1; i <= count; i++) {
			sql.append(" union ");
			String colName = md.getColumnName(i);
			String typeName = md.getColumnTypeName(i);
			int colType = md.getColumnType(i);
			int colLen = md.getColumnDisplaySize(i);
			String colNotNull = "0";
			int colNullable = 2;

			if (colNotNull != null) {
				colNullable = colNotNull.equals("0") ? 1 : 0;
			}

			sql.append("SELECT " + " NULL AS TABLE_CAT," + " NULL AS TABLE_SCHEM," + " '" + tblNamePattern + "' AS TABLE_NAME," + " '" + colName + "' AS COLUMN_NAME, " + "  " + colType + " AS DATA_TYPE," + " '" + typeName + "' AS TYPE_NAME," + " " + colLen + " AS COLUMN_SIZE," + " " + colLen + " AS BUFFER_LENGTH," + " 10 AS DECIMAL_DIGITS," + " 10 AS NUM_PREC_RADIX," + "  NULL AS NULLABLE,"
					+ "  NULL AS REMARKS," + "  NULL AS COLUMN_DEF," + "  0 AS SQL_DATA_TYPE," + "  0 AS SQL_DATETIME_SUB," + "  2000000000 AS CHAR_OCTET_LENGTH," + "  " + i + " AS ORDINAL_POSITION," + "  " + colNullable + " AS IS_NULLABLE," + "  NULL AS SCOPE_CATLOG," + "  NULL AS SCOPE_SCHEMA," + "  NULL AS SCOPE_TABLE," + "  NULL AS SOURCE_DATA_TYPE ");
		}
		sql.append(" order by ORDINAL_POSITION ");
		rs.close();
		return stat.executeQuery(sql.toString());
	}

	public ResultSet getCrossReference(String pc, String ps, String pt, String fc, String fs, String ft) throws SQLException {
		return conn.prepareStatement("select null from schema limit 0").executeQuery();
	}

	public ResultSet getSchemas() throws SQLException {
		if (getSchemas == null) {
			getSchemas = conn.prepareStatement("select null as TABLE_SCHEM, null as TABLE_CATALOG from schema limit 0;");
		}

		return getSchemas.executeQuery();
	}

	public ResultSet getCatalogs() throws SQLException {
		if (getCatalogs == null) {
			getCatalogs = conn.prepareStatement("select null as TABLE_CAT from schema limit 0;");
		}
		return getCatalogs.executeQuery();
	}

	public ResultSet getPrimaryKeys(String c, String s, String table) throws SQLException {
		return conn.prepareStatement("select null from schema limit 0").executeQuery();
	}

	public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		return conn.prepareStatement("select null from schema limit 0").executeQuery();
	}

	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		return conn.prepareStatement("select null from schema limit 0").executeQuery();
	}

	public ResultSet getIndexInfo(String c, String s, String t, boolean u, boolean approximate) throws SQLException {
		return conn.prepareStatement("select null from schema limit 0").executeQuery();
	}

	public ResultSet getProcedureColumns(String c, String s, String p, String colPat) throws SQLException {
		if (getProcedures == null) {
			getProcedureColumns = conn.prepareStatement("select null as PROCEDURE_CAT, null as PROCEDURE_SCHEM, null as PROCEDURE_NAME, null as COLUMN_NAME, null as COLUMN_TYPE, null as DATA_TYPE, null as TYPE_NAME, null as PRECISION, null as LENGTH, null as SCALE, null as RADIX, null as NULLABLE, null as REMARKS from schema limit 0;");
		}
		return getProcedureColumns.executeQuery();

	}

	public ResultSet getProcedures(String c, String s, String p) throws SQLException {
		if (getProcedures == null) {
			getProcedures = conn.prepareStatement("select null as PROCEDURE_CAT, null as PROCEDURE_SCHEM, null as PROCEDURE_NAME, null as UNDEF1, null as UNDEF2, null as UNDEF3, null as REMARKS, null as PROCEDURE_TYPE from schema limit 0;");
		}
		return getProcedures.executeQuery();
	}

	public ResultSet getSuperTables(String c, String s, String t) throws SQLException {
		if (getSuperTables == null) {
			getSuperTables = conn.prepareStatement("select null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as SUPERTABLE_NAME from schema limit 0;");
		}
		return getSuperTables.executeQuery();
	}

	public ResultSet getSuperTypes(String c, String s, String t) throws SQLException {
		if (getSuperTypes == null) {
			getSuperTypes = conn.prepareStatement("select null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as SUPERTYPE_CAT, null as SUPERTYPE_SCHEM, null as SUPERTYPE_NAME from schema limit 0;");
		}
		return getSuperTypes.executeQuery();
	}

	public ResultSet getTablePrivileges(String c, String s, String t) throws SQLException {
		if (getTablePrivileges == null) {
			getTablePrivileges = conn.prepareStatement("select  null as TABLE_CAT, null as TABLE_SCHEM, null as TABLE_NAME, null as GRANTOR, null GRANTEE,  null as PRIVILEGE, null as IS_GRANTABLE from schema limit 0;");
		}
		return getTablePrivileges.executeQuery();
	}

	public synchronized ResultSet getTables(String c, String s, String tblNamePattern, String types[]) throws SQLException {
		checkOpen();

		String inlist = "";
		if (types == null || types.length == 0) {
			inlist = "'TABLE','VIEW'";
		} else {
			inlist = "'" + types[0].toUpperCase() + "'";
			for (int i = 1; i < types.length; i++) {
				inlist += ",'" + types[i].toUpperCase() + "'";
			}
		}
		if (tblNamePattern == null)
			tblNamePattern = "%";
		String sql = "select null as TABLE_CAT, null as TABLE_SCHEM, name as TABLE_NAME, upper(type) as TABLE_TYPE, null as REMARKS from schema where TABLE_NAME like '" + tblNamePattern + "' and TABLE_TYPE in (" + inlist + ");";
		return ((OPPreparedStatement) conn.createStatement()).executeQuery(sql);
	}

	public ResultSet getTableTypes() throws SQLException {
		checkOpen();
		if (getTableTypes == null) {
			getTableTypes = conn.prepareStatement("select 'TABLE' as TABLE_TYPE from schema union select 'VIEW' as TABLE_TYPE;");
		}
		getTableTypes.clearParameters();
		return getTableTypes.executeQuery();
	}

	public ResultSet getTypeInfo() throws SQLException {
		if (getTypeInfo == null) {
			getTypeInfo = conn.prepareStatement("select null from schema limit 0");
		}

		getTypeInfo.clearParameters();
		return getTypeInfo.executeQuery();
	}

	/**
	 * java.lang.String, java.lang.String, int[])
	 */
	public ResultSet getUDTs(String c, String s, String t, int[] types) throws SQLException {
		if (getUDTs == null) {
			getUDTs = conn.prepareStatement("select  null as TYPE_CAT, null as TYPE_SCHEM, null as TYPE_NAME, null as CLASS_NAME, null as DATA_TYPE, null as REMARKS, null as BASE_TYPE from schema limit 0;");
		}

		getUDTs.clearParameters();
		return getUDTs.executeQuery();
	}

	/**
	 * java.lang.String, java.lang.String)
	 */
	public ResultSet getVersionColumns(String c, String s, String t) throws SQLException {
		if (getVersionColumns == null) {
			getVersionColumns = conn.prepareStatement("select null as SCOPE, null as COLUMN_NAME, null as DATA_TYPE, null as TYPE_NAME, null as COLUMN_SIZE, null as BUFFER_LENGTH, null as DECIMAL_DIGITS, null as PSEUDO_COLUMN from schema limit 0;");
		}
		return getVersionColumns.executeQuery();
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		if (getGeneratedKeys == null) {
			getGeneratedKeys = conn.prepareStatement("select null from schema limit 0;");
		}
		return getGeneratedKeys.executeQuery();
	}

	public Struct createStruct(String t, Object[] attr) throws SQLException {
		throw new SQLException("Not yet implemented by openPlant JDBC driver");
	}

	public ResultSet getFunctionColumns(String a, String b, String c, String d) throws SQLException {
		throw new SQLException("Not yet implemented by openPlant JDBC driver");
	}

	protected void finalize() throws Throwable {
		close();
	}

	// JDBC 4
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public RowIdLifetime getRowIdLifetime() throws SQLException {
		return null;
	}

	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		return null;
	}

	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return false;
	}

	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return false;
	}

	public ResultSet getClientInfoProperties() throws SQLException {
		return null;
	}

	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
		return null;
	}

	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return null;
	}

	public boolean generatedKeyAlwaysReturned() throws SQLException {
		return false;
	}
}
