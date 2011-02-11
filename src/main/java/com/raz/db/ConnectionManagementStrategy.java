package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

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

}
