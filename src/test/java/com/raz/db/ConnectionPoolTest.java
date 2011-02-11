package com.raz.db;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.raz.db.conn.ConnectionWrapper;
import com.raz.db.conn.PooledConnectionWrapper;

public class ConnectionPoolTest {

  protected final Log logger = LogFactory.getLog(getClass());

  @Test
  public void testApp() {
    RegularCappedConnectionPool dspool = new RegularCappedConnectionPool(new SimpleConnectionManager<ConnectionWrapper>(new DSConnectionProvider(null)), 10, 100);
    RegularCappedConnectionPool dmpool = new RegularCappedConnectionPool(new SimpleConnectionManager<ConnectionWrapper>(new DriverManagerConnectionProvider(null)), 10, 100);
    PooledCappedConnectionPool pdspool = new PooledCappedConnectionPool(new SimpleConnectionManager<PooledConnectionWrapper>(new PDSConnectionProvider(null)), 10, 100);
    logger.error("The log");
    assertTrue( true );
  }

}
