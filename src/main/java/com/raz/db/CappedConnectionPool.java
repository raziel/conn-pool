package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class CappedConnectionPool implements ConnectionPool {

  //TODO: make logs check if debug is enabled
  //TODO: add useful data to logs: Thread.currentThread().getId(), connMngr name

  protected final Log logger = LogFactory.getLog(getClass());

  protected Semaphore connRequest;
  protected int requestTimeout;

  public CappedConnectionPool(int maxLiveConnections, int requestTimeout) {
    connRequest = new Semaphore(maxLiveConnections, true);
    this.requestTimeout = requestTimeout;
  }

  @Override
  public Connection getConnection() throws SQLException {
    logger.debug("Trying to acquire connection.");
    try {
      if (!connRequest.tryAcquire(requestTimeout, TimeUnit.SECONDS)) {
        logger.debug("Thread timed out trying to acquire connection.");
        // TODO: throw timeoutexception
      }
    } catch (InterruptedException e) {
      String msg = "The thread was interrupted while waiting to acquire a connection from the pool.";
      logger.debug(msg, e);
      throw new RuntimeException(msg, e);
    }
    Connection conn = null;
    try {
      conn = aquireConnection();
      logger.debug("Connection acquired.");
      return conn;
    } finally {
      if (conn == null) {
        connRequest.release();
        logger.debug("Connection-request released due to exception while acquiring the connection.");
      }
    }
  }

  @Override
  public void releaseConnection(Connection conn) throws SQLException {
    boolean doRelease = true;
    try {
      logger.debug("Trying to release a connection.");
      doRelease = reallocateConnection(conn);
    } finally {
      if (doRelease) {
        connRequest.release();
        logger.debug("Connection released.");
      } else {
        logger.debug("Unable to release a connection.");
      }
    }
  }

  /**
   * Method expected to provide the Connection to be returned to users of the connection pool.
   *
   * @return A unused Connection.
   * @throws SQLException If there is a problem acquiring the connection.
   */
  protected abstract Connection aquireConnection() throws SQLException;

  /**
   * Method expected to re-allocate the Connection back into the collection of available connections
   * to be reused.
   *
   * The method must return true only the first time the connection is re-allocated since it was
   * last acquired. This is to prevent for multiple calls to re-allocate the connection without
   * re-acquiring it in between them.
   *
   * @param conn The Connection to re-allocate.
   * @return <code>true</code> is the connection was successfully reallocated, <code>false</code>
   * otherwise.
   * @throws SQLException If there is a problem reallocating the connection.
   */
  protected abstract boolean reallocateConnection(Connection conn) throws SQLException;

}
