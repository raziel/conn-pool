package com.raz.db;

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Provider of {@link SimpleConnectionWrapper} instances backed by a {@link DataSource}.
 *
 * @author raziel.alvarez
 *
 */
public class DSConnectionProvider implements ConnectionProvider<SimpleConnectionWrapper> {

  private DataSource dataSource;

  /**
   * Creates a new provider backed by the passed data source.
   *
   * @param dataSource The data source.
   */
  public DSConnectionProvider(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public SimpleConnectionWrapper getConnection() throws SQLException {
    return new SimpleConnectionWrapper(dataSource.getConnection());
  }

}
