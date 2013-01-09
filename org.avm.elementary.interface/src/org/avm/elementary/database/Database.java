package org.avm.elementary.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {
	public Connection getConnection() throws SQLException;

	public void releaseConnection(Connection conn) throws SQLException;

	public ResultSet sql(String sql);

	public PreparedStatement preparedStatement(String query)
			throws SQLException;

	public void releasePreparedStatement(PreparedStatement query)
			throws SQLException;

	public String getVersion();
}