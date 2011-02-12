package com.raz.db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerConnectionProvider implements ConnectionProvider<RegularConnectionWrapper> {

  private String url;

  public DriverManagerConnectionProvider(String url) {
    this.url = url;
  }

  @Override
  public RegularConnectionWrapper getConnection() throws SQLException {
    return new RegularConnectionWrapper(DriverManager.getConnection(url));
  }

}
