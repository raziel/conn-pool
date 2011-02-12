package com.raz.db.conn;

import java.sql.SQLException;


/**
 * Provider of {@link ConnectionWrapper} instances.
 *
 * @author raziel.alvarez
 *
 * @param <T>
 */
public interface ConnectionProvider<T extends ConnectionWrapper> {

  /**
   * Provides a new connection.
   *
   * @return A new connection.
   * @throws SQLException If there is a problem while obtaining the connection.
   */
  T getConnection() throws SQLException;

}
