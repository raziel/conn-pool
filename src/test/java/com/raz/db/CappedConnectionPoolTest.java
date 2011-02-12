package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;

public class CappedConnectionPoolTest {

  private MockCappedConnectionPool pool;

  /**
   * Tests {@link CappedConnectionPool#getConnection()} returns the configured maximum number of
   * connections, and the next time out.
   */
  @Test
  public void testGetConnection_MaxConnectionAndTimeout() throws Exception {
    pool = new MockCappedConnectionPool(3, 1);
    pool.getConnection();
    pool.getConnection();
    pool.getConnection();
    try {
      pool.getConnection();
    } catch (RequestTimeoutException e) {
      return;
    }
    Assert.fail();
  }

  /**
   * Tests {@link CappedConnectionPool#releaseConnection(Connection)} successfully reallocates
   * a request ticket to obtain a connection.
   */
  @Test
  public void testReleaseConnection_FreesRequest() throws Exception {
    pool = new MockCappedConnectionPool(3, 5);
    pool.getConnection();
    pool.getConnection();
    pool.getConnection();
    pool.releaseConnection(null);
    pool.getConnection();
  }

  /**
   * Tests {@link CappedConnectionPool#getConnection()} throws IllegalStateException if the pool is
   * closed.
   */
  @Test
  public void testGetConnection_AquireConnectionCalled() throws Exception {
    pool = new MockCappedConnectionPool(3, 5);
    pool.getConnection();
    Assert.assertTrue(pool.isAquireConnectionCalled());
  }

  /**
   * Tests {@link CappedConnectionPool#getConnection()} throws IllegalStateException if the pool is
   * closed.
   */
  @Test
  public void testGetConnection_ClosedPool_IllegalStateException() throws Exception {
    pool = new MockCappedConnectionPool(3, 5);
    pool.closePool();
    try {
      pool.getConnection();
    } catch (IllegalStateException e) {
      return;
    }
    Assert.fail();
  }

  /**
   * Tests {@link CappedConnectionPool#releaseConnection(Connection)} calls closeConnection, instead
   * of reallocateConnection, if the pool is closed.
   */
  @Test
  public void testReleaseConnection_ClosedPool_CloseConnectionCalled() throws Exception {
    pool = new MockCappedConnectionPool(3, 5);
    pool.closePool();
    pool.releaseConnection(null);
    Assert.assertTrue(pool.isCloseConnectionCalled());
    Assert.assertFalse(pool.isReallocateConnectionCalled());
  }

  /**
   * Tests {@link CappedConnectionPool#releaseConnection(Connection)} calls reallocateConnection,
   * instead of closeConnection.
   */
  @Test
  public void testReleaseConnection_ReallocateConnectionCalled() throws Exception {
    pool = new MockCappedConnectionPool(3, 5);
    pool.releaseConnection(null);
    Assert.assertFalse(pool.isCloseConnectionCalled());
    Assert.assertTrue(pool.isReallocateConnectionCalled());
  }

  /**
   * Tests {@link CappedConnectionPool#getConnection()} returns a connection when the request quota
   * has not being met.
   */
  @Test
  public void testGetConnection() throws Exception {
    pool = new MockCappedConnectionPool(5, 10);
    Assert.assertNotNull(pool.getConnection());
  }

  private static class MockCappedConnectionPool extends CappedConnectionPool {
    private boolean aquireConnectionCalled;
    private boolean reallocateConnectionCalled;
    private boolean closeConnectionCalled;
    private boolean closeConnectionsCalled;

    public MockCappedConnectionPool(int maxLiveConnections, int requestTimeout) {
      super(maxLiveConnections, requestTimeout);
    }

    @Override
    protected Connection aquireConnection() throws SQLException {
      aquireConnectionCalled = true;
      return new MockCloseableConnection();
    }

    @Override
    protected boolean reallocateConnection(Connection conn) throws SQLException {
      reallocateConnectionCalled = true;
      return true;
    }

    @Override
    protected void closeConnection(Connection conn) throws SQLException {
      closeConnectionCalled = true;
    }

    @Override
    protected void closeConnections() {
      closeConnectionsCalled = true;
    }

    public boolean isAquireConnectionCalled() {
      return aquireConnectionCalled;
    }

    public boolean isReallocateConnectionCalled() {
      return reallocateConnectionCalled;
    }

    public boolean isCloseConnectionCalled() {
      return closeConnectionCalled;
    }

    public boolean isCloseConnectionsCalled() {
      return closeConnectionsCalled;
    }

  }

}
