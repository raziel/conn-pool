package com.raz.db.conn;

import java.sql.Connection;

/**
 * Wrap an actual {@link Connection}, serving as an abstraction that allows users of instances of
 * this interface to be able to handle different kinds of connections or containers-of-connections.
 *
 * @author raziel.alvarez
 *
 */
public interface ConnectionWrapper {

  /**
   * Returns the wrapped connection.
   * @return The connection.
   */
  Connection getConnection();

}
