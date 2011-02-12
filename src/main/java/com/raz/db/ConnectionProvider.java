package com.raz.db;

import java.sql.SQLException;

import com.raz.db.conn.ConnectionWrapper;

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
