package com.raz.db;

import java.sql.Connection;

import com.raz.db.conn.ConnectionWrapper;

public class RegularConnectionWrapper implements ConnectionWrapper {

  private Connection conn;

  public RegularConnectionWrapper(Connection conn) {
    this.conn = conn;
  }

  @Override
  public Connection getConnection() {
    return conn;
  }

}