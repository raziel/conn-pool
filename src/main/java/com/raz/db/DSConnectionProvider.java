package com.raz.db;

import java.sql.SQLException;

import javax.sql.DataSource;

public class DSConnectionProvider implements ConnectionProvider<RegularConnectionWrapper> {

  private DataSource dataSource;

  public DSConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public RegularConnectionWrapper getConnection() throws SQLException {
    return new RegularConnectionWrapper(dataSource.getConnection());
  }

}
