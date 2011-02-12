package com.raz.db;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.raz.db.conn.ConnectionWrapper;
import com.raz.db.conn.DSConnectionProvider;
import com.raz.db.conn.DriverManagerConnectionProvider;
import com.raz.db.conn.PDSConnectionProvider;
import com.raz.db.conn.PooledConnectionWrapper;
import com.raz.db.conn.SimpleConnectionWrapper;
import com.raz.db.mng.SimpleConnectionManager;

public class ConnectionPoolTest {

  protected final Log logger = LogFactory.getLog(getClass());

  @Test
  public void testApp() {
    RegularCappedConnectionPool dspool = new RegularCappedConnectionPool(new SimpleConnectionManager<SimpleConnectionWrapper>(new DSConnectionProvider(null)), 10, 100);
    RegularCappedConnectionPool dmpool = new RegularCappedConnectionPool(new SimpleConnectionManager<SimpleConnectionWrapper>(new DriverManagerConnectionProvider(null)), 10, 100);
    PooledCappedConnectionPool pdspool = new PooledCappedConnectionPool(new SimpleConnectionManager<PooledConnectionWrapper>(new PDSConnectionProvider(null)), 10, 100);
    logger.error("The log");
    assertTrue( true );
  }

}
