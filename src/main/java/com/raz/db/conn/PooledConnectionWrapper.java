package com.raz.db.conn;

import java.sql.Connection;

import javax.sql.PooledConnection;

/**
 * Wraps {@link PooledConnection} instances.
 *
 * @author raziel.alvarez
 *
 */
public interface PooledConnectionWrapper extends ConnectionWrapper {

  /**
   * Returns the wrapped pooled connection.
   *
   * @return The pooled connection.
   */
  PooledConnection getPooledConnection();

  /**
   * Returns a Connection backed by the Pooled Connection. It consistently returns the same
   * Connection instance (i.e. does not create a new one with every call).
   */
  @Override
  Connection getConnection();

}
