package com.raz.db;

import org.junit.Ignore;


/**
 * Sample round-trip tests.
 * Uncomment once I set up a DB in the cloud to run automates tests.
 *
 * @author raziel.alvarez
 *
 */
@Ignore
public class ConnectionPoolTest {


////@Test
//public void testDataStore() throws Exception {
//JdbcDataSource dataSource = new JdbcDataSource();
//dataSource.setURL("jdbc:h2:test");
//
//ConnectionPool dspool = null;
//Connection connection = null;
//PreparedStatement statement = null;
//try {
//  dspool = new RegularCappedConnectionPool(new SimpleConnectionManager<SimpleConnectionWrapper>(new DSConnectionProvider(dataSource)), 10, 100);
//  connection = dspool.getConnection();
//  final String sql = "SELECT * FROM INFORMATION_SCHEMA.CATALOGS;";
//  statement = connection.prepareStatement(sql);
//  ResultSet rs = statement.executeQuery();
//  if (!rs.next()) throw new Exception("Failure");
//  Assert.assertEquals("TEST", rs.getString(1));
//} finally {
//  if (statement != null) { statement.close(); }
//  if (connection != null) { dspool.releaseConnection(connection); }
//}
//}
//
////@Test
//public void testPooledDataStore() throws Exception {
//JdbcDataSource dataSource = new JdbcDataSource();
//dataSource.setURL("jdbc:h2:test");
//
//Connection connection = null;
//PreparedStatement statement = null;
//try {
//  ConnectionPool pdspool = new PooledCappedConnectionPool(new SimpleConnectionManager<PooledConnectionWrapper>(new PDSConnectionProvider(dataSource)), 10, 100);
//  connection = pdspool.getConnection();
//  final String sql = "SELECT * FROM INFORMATION_SCHEMA.CATALOGS;";
//  statement = connection.prepareStatement(sql);
//  ResultSet rs = statement.executeQuery();
//  if (!rs.next()) throw new Exception("Failure");
//  Assert.assertEquals("TEST", rs.getString(1));
//} finally {
//  if (statement != null) { statement.close(); }
//  if (connection != null) { connection.close(); }
//}
//}
//
////@Test
//public void testDriverManager() throws Exception {
//JdbcOdbcDriver driver = new JdbcOdbcDriver();
//DriverManager.registerDriver(driver);
//
//ConnectionPool dmpool = null;
//Connection connection = null;
//PreparedStatement statement = null;
//try {
//  dmpool = new RegularCappedConnectionPool(new SimpleConnectionManager<SimpleConnectionWrapper>(new DriverManagerConnectionProvider("jdbc:h2:test")), 10, 100);
//  connection = dmpool.getConnection();
//  final String sql = "SELECT * FROM INFORMATION_SCHEMA.CATALOGS;";
//  statement = connection.prepareStatement(sql);
//  ResultSet rs = statement.executeQuery();
//  if (!rs.next()) throw new Exception("Failure");
//  Assert.assertEquals("TEST", rs.getString(1));
//} finally {
//  if (statement != null) { statement.close(); }
//  if (connection != null) { dmpool.releaseConnection(connection); }
//}
//}

}
