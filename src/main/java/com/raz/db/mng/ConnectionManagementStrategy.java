package com.raz.db.mng;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.raz.db.conn.ConnectionWrapper;

/**
 * In charge of providing {@link Connection} instances, and reallocating them to be reused, if
 * possible.
 *
 * @author raziel.alvarez
 *
 */
public interface ConnectionManagementStrategy<T extends ConnectionWrapper> {

  /**
   * Acquires a Connection currently not in use.
   *
   * @return A free connection.
   * @throws SQLException If there is a problem acquiring the connection.
   */
  T aquireConnection() throws SQLException;

  /**
   * Reallocates the passed Connection for its later use.
   *
   * @param conn The connection to reallocate.
   * @throws SQLException If there is a problem reallocating the connection.
   */
  void reallocateConnection(T conn) throws SQLException;

  /**
   * Removes all available connections, returning a list of the removed items.
   *
   * @return Removed connections.
   */
  List<T> removeAvailableConnections();

}
