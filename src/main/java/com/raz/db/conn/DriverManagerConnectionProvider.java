package com.raz.db.conn;

import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Provider of {@link SimpleConnectionWrapper} instances, backed by a pre-configured
 * {@link DriverManager}, and pointing to a specific database.
 *
 * @author raziel.alvarez
 *
 */
public class DriverManagerConnectionProvider implements ConnectionProvider<SimpleConnectionWrapper> {

  private String url;

  /**
   * Creates a new provider backed by the driver manager from the configured database.
   *
   * @param url a database url of the form jdbc:subprotocol:subname
   */
  public DriverManagerConnectionProvider(String url) {
    this.url = url;
  }

  @Override
  public SimpleConnectionWrapper getConnection() throws SQLException {
    return new SimpleConnectionWrapper(DriverManager.getConnection(url));
  }

}
