package com.raz.db;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Queue;

import com.raz.db.conn.ConnectionWrapper;

/**
 * Unbounded Connection manager, creates new connections when {@link #aquireConnection()} is
 * called and there are no existing <b>usable</b> connections available in stock. Thus, it will
 * create as many connections as calls to {@link #aquireConnection()} are done with an empty stock.
 *
 * We say <b>usable</b> connections because available connections are inspected if they have not
 * closed yet; otherwise a new connection is created.
 *
 * Reallocated connections are collected and reused during subsequent calls to
 * {@link #aquireConnection()}.
 *
 * @author raziel.alvarez
 *
 * @param <T>
 */
public class SimpleConnectionManager<T extends ConnectionWrapper>
implements ConnectionManagementStrategy<T> {

  private ConnectionProvider<T> connProvider;
  private Queue<T> availableConnections;

  /**
   * Creates a simple connection manager of connections of type T.
   *
   * @param connProvider The provider of new connections.
   */
  public SimpleConnectionManager(ConnectionProvider<T> connProvider) {
    this.connProvider = connProvider;
    availableConnections = new ArrayDeque<T>();
  }

  @Override
  public synchronized T aquireConnection() throws SQLException {
    if (availableConnections.isEmpty()) {
      return connProvider.getConnection();
    } else {
      T conn = availableConnections.remove();
      if (conn.getConnection().isClosed()) {
        aquireConnection();
      }
      return conn;
    }
  }

  @Override
  public synchronized void reallocateConnection(T conn) throws SQLException {
    availableConnections.add(conn);
  }

}
