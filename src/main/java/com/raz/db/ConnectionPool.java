package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database Connection Pool.
 *
 * Provides and release connections to a configured database.
 *
 * @author raziel.alvarez
 *
 */
public interface ConnectionPool {

  /**
   * Obtains a new database connection.
   *
   * @return A connection.
   * @throws SQLException Exception thrown in case of a failure getting the connection.
   */
  Connection getConnection() throws SQLException;

  /**
   * Releases a database connection.
   *
   * @param con The connection to be released.
   * @throws SQLException Exception thrown in case of a failure releasing the connection.
   */
  void releaseConnection(Connection con) throws SQLException;

}
