package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.raz.db.conn.ConnectionWrapper;

public class RegularCappedConnectionPool extends CappedConnectionPool {

  private ConnectionManagementStrategy<ConnectionWrapper> connMngr;
  private ConcurrentMap<Connection, ConnectionWrapper> liveConnections;

  public RegularCappedConnectionPool(ConnectionManagementStrategy<ConnectionWrapper> connMngr,
    int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
    liveConnections = new ConcurrentHashMap<Connection, ConnectionWrapper>();
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    ConnectionWrapper connw = connMngr.aquireConnection();
    Connection conn = connw.getConnection();
    liveConnections.put(conn, connw);
    return conn;
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    ConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
      connMngr.reallocateConnection(connw);
      return true;
    }
    return false;
  }

}
