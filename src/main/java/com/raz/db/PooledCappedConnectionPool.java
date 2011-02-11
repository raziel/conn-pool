package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

import com.raz.db.conn.PooledConnectionWrapper;

public class PooledCappedConnectionPool extends CappedConnectionPool {

  private ConnectionManagementStrategy<PooledConnectionWrapper> connMngr;
  private ConcurrentMap<Connection, PooledConnectionWrapper> liveConnections;
  private ConcurrentMap<PooledConnection, Connection> livePooledConnections;

  public PooledCappedConnectionPool(ConnectionManagementStrategy<PooledConnectionWrapper> connMngr,
    int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
    liveConnections = new ConcurrentHashMap<Connection, PooledConnectionWrapper>();
    livePooledConnections = new ConcurrentHashMap<PooledConnection, Connection>();
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    PooledConnectionWrapper connw = connMngr.aquireConnection();
    PooledConnection pConn = connw.getPooledConnection();
    pConn.addConnectionEventListener(new PooledConnectionEventListener());
    Connection conn = connw.getConnection();
    liveConnections.put(conn, connw);
    livePooledConnections.put(pConn, conn);
    return conn;
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    PooledConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
      livePooledConnections.remove(connw.getPooledConnection());
      connMngr.reallocateConnection(connw);
      return true;
    }
    return false;
  }

  protected void reallocatePooledConnection(PooledConnection pConn) {
    try {
      reallocateConnection(livePooledConnections.remove(pConn));
    } catch (SQLException e) {
      logger.debug("There was a problem while reallocating the connection.", e);
    }
  }

  private class PooledConnectionEventListener implements ConnectionEventListener {

    @Override
    public void connectionClosed(ConnectionEvent event) {
      PooledConnection pConn = (PooledConnection) event.getSource();
      reallocatePooledConnection(pConn);
    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent event) {
      connectionClosed(event);
    }

  }

}
