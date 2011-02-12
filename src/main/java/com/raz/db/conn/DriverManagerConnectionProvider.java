package com.raz.db.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Provider of {@link SimpleConnectionWrapper} instances, backed by a pre-configured
 * {@link DriverManager}, and pointing to a specific database.
 *
 * @author raziel.alvarez
 *
 */
public class DriverManagerConnectionProvider implements ConnectionProvider<SimpleConnectionWrapper> {

  private String url;
  private Properties props;
  private String un;
  private String pw;

  /**
   * Creates a new provider backed by the driver manager from the configured database.
   *
   * @param url a database url of the form jdbc:subprotocol:subname
   */
  public DriverManagerConnectionProvider(String url) {
    this.url = url;
  }

  /**
   * Creates a new provider backed by the driver manager from the configured database.
   *
   * @param url a database url of the form jdbc:subprotocol:subname
   * @param props a list of arbitrary string tag/value pairs as connection arguments; normally at
   * least a "user" and "password" property should be included.
   */
  public DriverManagerConnectionProvider(String url, Properties props) {
    this.url = url;
    this.props = props;
  }

  /**
   * Creates a new provider backed by the driver manager from the configured database.
   *
   * @param url a database url of the form jdbc:subprotocol:subname
   * @param un The database user on whose behalf the connection is being made.
   * @param pw The user's password.
   */
  public DriverManagerConnectionProvider(String url, String un, String pw) {
    this.url = url;
    this.un = un;
    this.pw = pw;
  }

  @Override
  public SimpleConnectionWrapper getConnection() throws SQLException {
    return new SimpleConnectionWrapper(obtainConnection());
  }

  private Connection obtainConnection() throws SQLException {
    if (props != null) {
      return DriverManager.getConnection(url, props);
    } else if (un != null){
      return DriverManager.getConnection(url, un, pw);
    } else {
      return DriverManager.getConnection(url);
    }
  }

}
