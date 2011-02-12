package com.raz.db.conn;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;


/**
 * Provider of {@link PooledConnectionWrapper} instances backed by a
 * {@link ConnectionPoolDataSource}.
 *
 * @author raziel.alvarez
 *
 */
public class PDSConnectionProvider implements ConnectionProvider<PooledConnectionWrapper> {

  private ConnectionPoolDataSource dataSource;
  private String un;
  private String pw;

  /**
   * Creates a new provider backed by the passed pooled data source.
   *
   * @param dataSource The pooled data source.
   */
  public PDSConnectionProvider(ConnectionPoolDataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Creates a new provider backed by the passed pooled data source.
   *
   * @param dataSource The pooled data source.
   * @param un The database user on whose behalf the connection is being made.
   * @param pw The user's password.
   */
  public PDSConnectionProvider(ConnectionPoolDataSource dataSource, String un, String pw) {
    this.dataSource = dataSource;
    this.un = un;
    this.pw = pw;
  }

  @Override
  public PooledConnectionWrapper getConnection() throws SQLException {
    return new BasePooledConnectionWrapper(obtainConnection());
  }

  private PooledConnection obtainConnection() throws SQLException {
    if (un == null) {
      return dataSource.getPooledConnection();
    } else {
      return dataSource.getPooledConnection(un, pw);
    }
  }

  /*
   * Utility class, concrete implementation of a PooledConnectionWrapper.
   */
  private static class BasePooledConnectionWrapper implements PooledConnectionWrapper {

    private PooledConnection pconn;
    private Connection conn;

    private BasePooledConnectionWrapper(PooledConnection pconn) throws SQLException {
      this.pconn = pconn;
      this.conn = pconn.getConnection();
    }

    @Override
    public Connection getConnection() {
      return conn;
    }

    @Override
    public PooledConnection getPooledConnection() {
      return pconn;
    }

  }

}
