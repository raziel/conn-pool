package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

  private ConnectionManagementStrategy<RegularConnectionWrapper> connMngr;
  private ConcurrentMap<Connection, RegularConnectionWrapper> liveConnections;

  /**
   * Creates a new Connection Pool capped to the given parameters, and uses the passed connection
   * manager to obtain, manage, and reallocate connections.
   *
   * @param connMngr The connection manager.
   * @param maxLiveConnections The max number of connections.
   * @param requestTimeout The timeout for a request.
   */
  public RegularCappedConnectionPool(ConnectionManagementStrategy<RegularConnectionWrapper> connMngr,
    int maxLiveConnections, int requestTimeout) {
    super(maxLiveConnections, requestTimeout);
    this.connMngr = connMngr;
    liveConnections = new ConcurrentHashMap<Connection, RegularConnectionWrapper>();
  }

  @Override
  protected Connection aquireConnection() throws SQLException {
    logDebug("Acquiring a connection from the connection manager.");
    RegularConnectionWrapper connw = connMngr.aquireConnection();
    Connection conn = connw.getConnection();
    liveConnections.put(conn, connw);
    logDebug("Connection aquired from the connection manager.");
    return conn;
  }

  @Override
  protected boolean reallocateConnection(Connection conn) throws SQLException {
    logDebug("Reallocating a connection into the connection manager.");
    RegularConnectionWrapper connw = liveConnections.remove(conn);
    if (connw != null) {
      connMngr.reallocateConnection(connw);
      logDebug("Connection reallocated into the connection manager.");
      return true;
    }
    logDebug("Unable to reallocate a connection into the connection manager.");
    return false;
  }

}
