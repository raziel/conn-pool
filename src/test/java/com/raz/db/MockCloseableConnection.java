package com.raz.db;

import java.sql.SQLException;

public class MockCloseableConnection extends MockConnection {

  private boolean closed;

  @Override
  public boolean isClosed() throws SQLException {
    return closed;
  }

  @Override
  public void close() throws SQLException {
    closed = true;
  }

}
