package com.raz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.jdbcx.JdbcDataSource;

import sun.jdbc.odbc.JdbcOdbcDriver;

import com.raz.db.conn.DSConnectionProvider;
import com.raz.db.conn.DriverManagerConnectionProvider;
import com.raz.db.conn.PDSConnectionProvider;
import com.raz.db.conn.PooledConnectionWrapper;
import com.raz.db.conn.SimpleConnectionWrapper;
import com.raz.db.mng.SimpleConnectionManager;

/**
 * Sample round-trip tests.
 * Uncomment once I set up a DB in the cloud to run automates tests.
 *
 * @author raziel.alvarez
 *
 */
public class ConnectionPoolTest {

  protected final Log logger = LogFactory.getLog(getClass());

//  @Test
  public void testDataStore() throws Exception {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:test");

    Connection connection = null;
    PreparedStatement statement = null;
    try {
      RegularCappedConnectionPool dspool = new RegularCappedConnectionPool(new SimpleConnectionManager<SimpleConnectionWrapper>(new DSConnectionProvider(dataSource)), 10, 100);
      connection = dspool.getConnection();
      final String sql = "SELECT * FROM INFORMATION_SCHEMA.CATALOGS;";
      statement = connection.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      if (!rs.next()) throw new Exception("Failure");
      Assert.assertEquals("TEST", rs.getString(1));
    } finally {
      if (statement != null) { statement.close(); }
      if (connection != null) { connection.close(); }
    }
  }

//  @Test
  public void testPooledDataStore() throws Exception {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:test");

    Connection connection = null;
    PreparedStatement statement = null;
    try {
      PooledCappedConnectionPool pdspool = new PooledCappedConnectionPool(new SimpleConnectionManager<PooledConnectionWrapper>(new PDSConnectionProvider(dataSource)), 10, 100);
      connection = pdspool.getConnection();
      final String sql = "SELECT * FROM INFORMATION_SCHEMA.CATALOGS;";
      statement = connection.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      if (!rs.next()) throw new Exception("Failure");
      Assert.assertEquals("TEST", rs.getString(1));
    } finally {
      if (statement != null) { statement.close(); }
      if (connection != null) { connection.close(); }
    }
  }

//  @Test
  public void testDriverManager() throws Exception {
    JdbcOdbcDriver driver = new JdbcOdbcDriver();
    DriverManager.registerDriver(driver);

    Connection connection = null;
    PreparedStatement statement = null;
    try {
      RegularCappedConnectionPool dmpool = new RegularCappedConnectionPool(new SimpleConnectionManager<SimpleConnectionWrapper>(new DriverManagerConnectionProvider("jdbc:h2:test")), 10, 100);
      connection = dmpool.getConnection();
      final String sql = "SELECT * FROM INFORMATION_SCHEMA.CATALOGS;";
      statement = connection.prepareStatement(sql);
      ResultSet rs = statement.executeQuery();
      if (!rs.next()) throw new Exception("Failure");
      Assert.assertEquals("TEST", rs.getString(1));
    } finally {
      if (statement != null) { statement.close(); }
      if (connection != null) { connection.close(); }
    }
  }

}
