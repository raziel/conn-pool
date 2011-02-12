package com.raz.db;

import java.sql.Connection;

import com.raz.db.conn.ConnectionWrapper;

/**
 * Wraps {@link Connection} instances directly.
 *
 * @author raziel.alvarez
 *
 */
public class SimpleConnectionWrapper implements ConnectionWrapper {

  private Connection conn;

  /**
   * Constructs a wrapper for the passed {@link Connection}.
   *
   * @param conn The connection to wrap.
   */
  public SimpleConnectionWrapper(Connection conn) {
    this.conn = conn;
  }

  @Override
  public Connection getConnection() {
    return conn;
  }

}