package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

public class PDSCappedConnectionPool extends CappedConnectionPool {

  private PooledConnectionManagementStrategy connMngr;

  public PDSCappedConnectionPool(PooledConnectionManagementStrategy connMngr, int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    PooledConnection pConn = connMngr.aquireConnection();
    pConn.addConnectionEventListener(new ConnectionEventListener() {

      @Override
      public void connectionErrorOccurred(ConnectionEvent event) {
        connectionClosed(event);
      }

      @Override
      public void connectionClosed(ConnectionEvent event) {
        PooledConnection conn = (PooledConnection)event.getSource();
        try {
          connMngr.reallocateConnection(conn);
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          connRequest.release();
        }
      }
    });
    return null;
  }

  @Override
  public void releaseConnection(Connection conn) throws SQLException {
    conn.close();
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    conn.close();
    return true;
  }

}
