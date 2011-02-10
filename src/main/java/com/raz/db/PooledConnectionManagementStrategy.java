package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.PooledConnection;

/**
 * In charge of providing {@link Connection} instances, and reallocating them to be reused, if
 * possible.
 *
 * @author raziel.alvarez
 *
 */
public interface PooledConnectionManagementStrategy {

  /**
   * Acquires a Connection currently not in use.
   *
   * @return A free connection.
   * @throws SQLException If there is a problem acquiring the connection.
   */
  PooledConnection aquireConnection() throws SQLException;

  /**
   * Reallocates the passed Connection for its later use.
   *
   * @param conn The connection to reallocate.
   * @return <code>true</code> is the connection was successfully reallocated, <code>false</code>
   * otherwise.
   * @throws SQLException If there is a problem reallocating the connection.
   */
  boolean reallocateConnection(PooledConnection conn) throws SQLException;

}
