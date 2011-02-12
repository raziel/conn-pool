package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

import com.raz.db.conn.PooledConnectionWrapper;
import com.raz.db.mng.ConnectionManagementStrategy;

/**
 * Concrete Capped Connection Pool that provides connections backed by a {@link PooledConnection}.
 *
 * Hence, the connections obtained could follow the pattern expected for pooled connections
 * of not requiring a direct call to {@link #releaseConnection(Connection)}, but instead
 * "closing" the connection directly. For example:
 *
 * Connection conn = null;
 * ConnectionPool pool = null;
 *
 * try {
 *   // pool = ... <obtain concrete connection pool>
 *   conn = pool.getConnection();
 *   ...
 * } finally {
 *   if (conn != null) { conn.close(); }
 * }
 *
 * This class supports the {@link #releaseConnection(Connection)}, as well, with the intention of
 * providing a non-disruptive, interchangeable, use of connection pools.
 *
 * @author raziel.alvarez
 *
 */
public class PooledCappedConnectionPool extends CappedConnectionPool {

  private ConnectionManagementStrategy<PooledConnectionWrapper> connMngr;
  private ConcurrentMap<Connection, PooledConnectionWrapper> liveConnections;
  private ConcurrentMap<PooledConnection, Connection> livePooledConnections;

  /**
   * Creates a new Connection Pool capped to the given parameters, and uses the passed pooled
   * connection manager to obtain, manage, and reallocate connections.
   *
   * @param connMngr The connection manager.
   * @param maxLiveConnections The max number of connections.
   * @param requestTimeout The timeout for a request.
   */
  public PooledCappedConnectionPool(ConnectionManagementStrategy<PooledConnectionWrapper> connMngr,
    int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
    liveConnections = new ConcurrentHashMap<Connection, PooledConnectionWrapper>();
    livePooledConnections = new ConcurrentHashMap<PooledConnection, Connection>();
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    logDebug("Acquiring a connection from the connection manager.");
    PooledConnectionWrapper connw = connMngr.aquireConnection();
    PooledConnection pConn = connw.getPooledConnection();
    pConn.addConnectionEventListener(new PooledConnectionEventListener());
    Connection conn = connw.getConnection();
    liveConnections.put(conn, connw);
    livePooledConnections.put(pConn, conn);
    logDebug("Connection aquired from the connection manager.");
    return conn;
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    logDebug("Reallocating a connection into the connection manager.");
    PooledConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
      livePooledConnections.remove(connw.getPooledConnection());
      connMngr.reallocateConnection(connw);
      logDebug("Connection reallocated into the connection manager.");
      return true;
    }
    logDebug("Unable to reallocate a connection into the connection manager.");
    return false;
  }


  @Override
  protected void closeConnection(Connection conn) throws SQLException {
    logDebug("Closing a connection.");
    PooledConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
      livePooledConnections.remove(connw.getPooledConnection());
      connw.getPooledConnection().close();
      logDebug("Connection closed.");
    }
  }

  @Override
  protected void closeConnections() {
    logDebug("Releasing connections in the connection manager.");
    connMngr.closeAvailableConnections();
    logDebug("Available connections released in the connection manager.");
  }

  /*
   * Utility method to reallocate a pooled connection back into the manager. This method is used
   * when the reallocation is triggered by a connection event (see PooledConnectionEventListener).
   */
  private void reallocatePooledConnection(PooledConnection pConn) {
    try {
      reallocateConnection(livePooledConnections.remove(pConn));
    } catch (SQLException e) {
      logDebug("There was a problem while reallocating the connection.", e);
    }
  }

  /*
   * Utility class that implements the logic to handle Connection Events, which are used to
   * reallocate the pooled connection instead of directly calling #releaseConnection.

   * @author raziel.alvarez
   *
   */
  private class PooledConnectionEventListener implements ConnectionEventListener {

    @Override
    public void connectionClosed(ConnectionEvent event) {
      PooledConnection pConn = (PooledConnection) event.getSource();
      pConn.removeConnectionEventListener(this);
      reallocatePooledConnection(pConn);
    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent event) {
      connectionClosed(event);
    }

  }

}
