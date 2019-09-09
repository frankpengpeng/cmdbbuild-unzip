package org.cmdbuild.scheduler.quartz;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class QuartzConnectionProvider implements org.quartz.utils.ConnectionProvider {

	private static DataSource dataSource;

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void shutdown() throws SQLException {
		//nothing to do
	}

	@Override
	public void initialize() throws SQLException {
		checkNotNull(dataSource); //just to be sure
	}

	public static void setDataSource(DataSource dataSource) {
		QuartzConnectionProvider.dataSource = checkNotNull(dataSource);
	}

	public static DataSource getDataSource() {
		return dataSource;
	}

}
