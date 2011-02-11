package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import com.raz.db.conn.PooledConnectionWrapper;

public class PDSConnectionProvider implements ConnectionProvider<PooledConnectionWrapper> {

  private ConnectionPoolDataSource dataSource;

  public PDSConnectionProvider(ConnectionPoolDataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public PooledConnectionWrapper getConnection() throws SQLException {
    return new BasePooledConnectionWrapper(dataSource.getPooledConnection());
  }

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
