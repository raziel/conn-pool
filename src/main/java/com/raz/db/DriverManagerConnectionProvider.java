package com.raz.db;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.raz.db.conn.ConnectionWrapper;

public class DriverManagerConnectionProvider implements ConnectionProvider<ConnectionWrapper> {

  private String url;

  public DriverManagerConnectionProvider(String url) {
    this.url = url;
  }

  @Override
  public ConnectionWrapper getConnection() throws SQLException {
    return new RegularConnectionWrapper(DriverManager.getConnection(url));
  }

}
