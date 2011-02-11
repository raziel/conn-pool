package com.raz.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.raz.db.conn.ConnectionWrapper;

public class DSConnectionProvider implements ConnectionProvider<ConnectionWrapper> {

  private DataSource dataSource;

  public DSConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public ConnectionWrapper getConnection() throws SQLException {
    return new RegularConnectionWrapper(dataSource.getConnection());
  }

}
