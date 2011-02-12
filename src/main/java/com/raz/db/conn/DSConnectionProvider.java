package com.raz.db.conn;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 * Provider of {@link SimpleConnectionWrapper} instances backed by a {@link DataSource}.
 *
 * @author raziel.alvarez
 *
 */
public class DSConnectionProvider implements ConnectionProvider<SimpleConnectionWrapper> {

  private DataSource dataSource;
  private String un;
  private String pw;

  /**
   * Creates a new provider backed by the passed data source.
   *
   * @param dataSource The data source.
   */
  public DSConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Creates a new provider backed by the passed data source.
   *
   * @param dataSource The data source.
   * @param un The database user on whose behalf the connection is being made.
   * @param pw The user's password.
   */
  public DSConnectionProvider(DataSource dataSource, String un, String pw) {
    this.dataSource = dataSource;
    this.un = un;
    this.pw = pw;
  }

  @Override
  public SimpleConnectionWrapper getConnection() throws SQLException {
    return new SimpleConnectionWrapper(obtainConnection());
  }

  private Connection obtainConnection() throws SQLException {
    if (un == null) {
      return dataSource.getConnection();
    } else {
      return dataSource.getConnection(un, pw);
    }
  }

}
