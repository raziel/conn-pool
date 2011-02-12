package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.raz.db.conn.SimpleConnectionWrapper;
import com.raz.db.mng.ConnectionManagementStrategy;

/**
 * Concrete Capped Connection Pool. It is called "Regular" to differentiate it from its
 * {@link PooledCappedConnectionPool}.
 *
 * The actual allocation of connections is delegated to the {@link ConnectionManagementStrategy}.
 *
 * @author raziel.alvarez
 *
 */
public class RegularCappedConnectionPool extends CappedConnectionPool {

  private ConnectionManagementStrategy<SimpleConnectionWrapper> connMngr;
  private ConcurrentMap<Connection, SimpleConnectionWrapper> liveConnections;

  /**
   * Creates a new Connection Pool capped to the given parameters, and uses the passed connection
   * manager to obtain, manage, and reallocate connections.
   *
   * @param connMngr The connection manager.
   * @param maxLiveConnections The max number of connections.
   * @param requestTimeout The timeout in seconds for a request.
   */
  public RegularCappedConnectionPool(ConnectionManagementStrategy<SimpleConnectionWrapper> connMngr,
    int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
    liveConnections = new ConcurrentHashMap<Connection, SimpleConnectionWrapper>();
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    logDebug("Acquiring a connection from the connection manager.");
    SimpleConnectionWrapper connw = connMngr.aquireConnection();
    Connection conn = connw.getConnection();
    liveConnections.put(conn, connw);
    logDebug("Connection aquired from the connection manager.");
    return conn;
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    logDebug("Reallocating a connection into the connection manager.");
    SimpleConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
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
    SimpleConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
      connw.getConnection().close();
      logDebug("Connection closed.");
    }
  }

  @Override
  protected void closeConnections() {
    logDebug("Releasing connections in the connection manager.");
    for (SimpleConnectionWrapper cw : connMngr.removeAvailableConnections()) {
      try {
        cw.getConnection().close();
      } catch (SQLException e) {
        logger.error("Problem closing connection.", e);
      }
    }
    logDebug("Available connections released in the connection manager.");
  }

}
