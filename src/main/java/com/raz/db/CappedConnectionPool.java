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
        logger.debug("Releasing connection-request due to exception while acquiring the connection.");
        connRequest.release();
      }
    }
  }

  @Override
  public void releaseConnection(Connection conn) throws SQLException {
    logger.debug("Trying to release a connection.");
    if (reallocateConnection(conn)) {
      connRequest.release();
      logger.debug("Connection released.");
      return;
    }
    logger.debug("Unable to release a connection.");
  }

  protected abstract Connection aquireConnection() throws SQLException;

  protected abstract boolean reallocateConnection(Connection conn) throws SQLException;

}
