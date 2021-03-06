package com.raz.db.mng;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.raz.db.MockCloseableConnection;
import com.raz.db.conn.ConnectionProvider;
import com.raz.db.conn.ConnectionWrapper;

public class SimpleConnectionManagerTest {

  SimpleConnectionManager<CloseableMockConnectionWrapper> connMngr;

  @Before
  public void setUp() {
    connMngr = new SimpleConnectionManager<CloseableMockConnectionWrapper>(new MockConnectionProvider());
    connMngr.removeAvailableConnections();
  }

  /**
   * Tests {@link SimpleConnectionManager#reallocateConnection(ConnectionWrapper)} puts a
   * connection back in the collection of available connections
   */
  @Test
  public void testReallocateConnection() throws Exception {
    CloseableMockConnectionWrapper conn = new CloseableMockConnectionWrapper();
    connMngr.reallocateConnection(conn);
    Assert.assertSame(conn, connMngr.removeAvailableConnections().get(0));
  }

  /**
   * Tests {@link SimpleConnectionManager#removeAvailableConnections()} removes all connections and
   * returns them in the list.
   */
  @Test
  public void testRemoveAvailableConnections() throws Exception {
    CloseableMockConnectionWrapper conn1 = new CloseableMockConnectionWrapper();
    CloseableMockConnectionWrapper conn2 = new CloseableMockConnectionWrapper();
    CloseableMockConnectionWrapper conn3 = new CloseableMockConnectionWrapper();
    connMngr.reallocateConnection(conn1);
    connMngr.reallocateConnection(conn2);
    connMngr.reallocateConnection(conn3);
    List<CloseableMockConnectionWrapper> removed = connMngr.removeAvailableConnections();
    Assert.assertSame(conn1, removed.get(0));
    Assert.assertSame(conn2, removed.get(1));
    Assert.assertSame(conn3, removed.get(2));
  }

  /**
   * Tests {@link SimpleConnectionManager#aquireConnection()} reuses a connection if available.
   */
  @Test
  public void testAquireConnection_ReturnAvailable() throws Exception {
    CloseableMockConnectionWrapper conn = new CloseableMockConnectionWrapper();
    connMngr.reallocateConnection(conn);
    Assert.assertSame(conn, connMngr.aquireConnection());
  }

  /**
   * Tests {@link SimpleConnectionManager#aquireConnection()} uses FIFO for available connections.
   */
  @Test
  public void testAquireConnection_FIFO() throws Exception {
    CloseableMockConnectionWrapper conn1 = new CloseableMockConnectionWrapper();
    CloseableMockConnectionWrapper conn2 = new CloseableMockConnectionWrapper();
    CloseableMockConnectionWrapper conn3 = new CloseableMockConnectionWrapper();
    connMngr.reallocateConnection(conn1);
    connMngr.reallocateConnection(conn2);
    connMngr.reallocateConnection(conn3);
    Assert.assertSame(conn1, connMngr.aquireConnection());
    Assert.assertSame(conn2, connMngr.aquireConnection());
    Assert.assertSame(conn3, connMngr.aquireConnection());
  }

  /**
   * Tests {@link SimpleConnectionManager#aquireConnection()} creates a new connection if there
   * are not available.
   */
  @Test
  public void testAquireConnection_CreateConnectionIfNoAvailable() throws Exception {
    Assert.assertNotNull(connMngr.aquireConnection());
  }

  /**
   * Tests {@link SimpleConnectionManager#aquireConnection()} skips available connections that
   * are closed.
   */
  @Test
  public void testAquireConnection_SkipClosedConnection() throws Exception {
    CloseableMockConnectionWrapper conn1 = new CloseableMockConnectionWrapper();
    CloseableMockConnectionWrapper conn2 = new CloseableMockConnectionWrapper();
    conn1.getConnection().close();
    connMngr.reallocateConnection(conn1);
    connMngr.reallocateConnection(conn2);
    Assert.assertSame(conn2, connMngr.aquireConnection());
    Assert.assertTrue(connMngr.removeAvailableConnections().isEmpty());
  }

  /**
   * Tests {@link SimpleConnectionManager#aquireConnection()} creates a new connection if all
   * available ones are closed, and it removes them.
   */
  @Test
  public void testAquireConnection_CreateConnectionIfAllClosed() throws Exception {
    CloseableMockConnectionWrapper conn1 = new CloseableMockConnectionWrapper();
    CloseableMockConnectionWrapper conn2 = new CloseableMockConnectionWrapper();
    conn1.getConnection().close();
    conn2.getConnection().close();
    connMngr.reallocateConnection(conn1);
    connMngr.reallocateConnection(conn2);
    Assert.assertNotSame(conn1, connMngr.aquireConnection());
    Assert.assertTrue(connMngr.removeAvailableConnections().isEmpty());
  }

  private static class MockConnectionProvider implements ConnectionProvider<CloseableMockConnectionWrapper> {
    @Override
    public CloseableMockConnectionWrapper getConnection() throws SQLException {
      return new CloseableMockConnectionWrapper();
    }
  }

  private static class CloseableMockConnectionWrapper implements ConnectionWrapper {
    private Connection conn = new MockCloseableConnection();
    @Override
    public Connection getConnection() {
      return conn;
    }
  }

}
