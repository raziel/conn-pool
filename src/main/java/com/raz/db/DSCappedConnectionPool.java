package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DSCappedConnectionPool extends CappedConnectionPool {

  private ConnectionManagementStrategy connMngr;

  public DSCappedConnectionPool(ConnectionManagementStrategy connMngr, int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    return connMngr.aquireConnection();
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    return connMngr.reallocateConnection(conn);
  }

}
