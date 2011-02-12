package com.raz.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Connection Pool capped to a maximum number of alive connection and a timeout for
 * obtaining one.
 *
 * When the maximum number of connections has been reached, subsequent requests to get a connection
 * will wait until a connection frees and can be allocated, or until the timeout is met. In case of
 * a timeout, a {@link RequestTimeoutException} will be thrown.
 *
 * Requests are processed in a fair, first-in first-out, way, however connection allocation is up
 * to the concrete implementations of this class.
 *
 * @author raziel.alvarez
 *
 */
public abstract class CappedConnectionPool implements ConnectionPool {

  protected final Log logger = LogFactory.getLog(getClass());

  protected Semaphore connRequest;
  protected int requestTimeout;
  protected int maxLiveConnections;

  private boolean poolClosed = false;

  /**
   * Creates a new Connection Pool capped to the given maximum number of alive (in use) connections,
   * and a timeout.
   * @param maxLiveConnections The maximum number of alive connections.
   * @param requestTimeout The timeout for a request to be fulfilled.
   */
  public CappedConnectionPool(int maxLiveConnections, int requestTimeout) {
    connRequest = new Semaphore(maxLiveConnections, true);
    this.maxLiveConnections = maxLiveConnections;
    this.requestTimeout = requestTimeout;
  }

  /**
   * Provides a connection, waiting until it becomes available or a timeout is met, in which case
   * a {@link RequestTimeoutException} is thrown.
   */
  @Override
  public Connection getConnection() throws SQLException {
    if (poolClosed) {
      logDebug("Rejected request to acquire connection due pool being closed.");
      throw new IllegalStateException("Connection pool has been closed.");
    }
    logDebug("Trying to acquire connection.");
    try {
      if (!connRequest.tryAcquire(requestTimeout, TimeUnit.SECONDS)) {
        String msg = "Thread timed out trying to acquire connection.";
        logDebug(msg);
        throw new RequestTimeoutException(msg);
      }
    } catch (InterruptedException e) {
      String msg = "The thread was interrupted while waiting to acquire a connection from the pool.";
      logDebug(msg, e);
      throw new RuntimeException(msg, e);
    }
    Connection conn = null;
    try {
      conn = aquireConnection();
      logDebug("Connection acquired.");
      return conn;
    } finally {
      if (conn == null) {
        connRequest.release();
        logDebug("Connection-request released due to exception while acquiring the connection.");
      }
    }
  }

  /**
   * Releases a connection, obtained from this pool, in order to be reused.
   */
  @Override
  public void releaseConnection(Connection conn) throws SQLException {
    boolean doRelease = true;
    try {
      if (poolClosed) {
        logDebug("Trying to close a connection.");
        closeConnection(conn);
      } else {
        logDebug("Trying to release a connection.");
        doRelease = reallocateConnection(conn);
      }
    } finally {
      if (doRelease) {
        connRequest.release();
        logDebug("Connection released.");
      } else {
        logDebug("Unable to release a connection.");
      }
    }
  }

  @Override
  public void freeConnections() {
    logDebug("Closing available connections.");
    poolClosed = true;
    closeConnections();
    logDebug("Available connections closed.");
  }

  /**
   * Convenience method that logs a debug level message. The message automatically includes
   * data to more easily identify the thread on which it was called.
   *
   * @param message The message.
   */
  protected void logDebug(String message) {
    if (logger.isDebugEnabled()) {
      logDebug(getThreadInfo() + " " + message);
    }
  }

  /**
   * Convenience method that logs a debug level message, and associated throwable. The message
   * automatically includes data to more easily identify the thread on which it was called.
   *
   * @param message The message.
   * @param t The throwable.
   */
  protected void logDebug(String message, Throwable t) {
    if (logger.isDebugEnabled()) {
      logDebug(getThreadInfo() + " " + message, t);
    }
  }

  /*
   * Produces a string with information about the thread.
   */
  private String getThreadInfo() {
    Thread thread = Thread.currentThread();
    return "[class=" + thread.getClass() + ", threadId=" + thread.getId() + "]";
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

  /**
   * Method expected to close the passed connection.
   * @throws SQLException If there is a problem closing the connection.
   */
  protected abstract void closeConnection(Connection conn) throws SQLException;

  /**
   * Method expected to close all available physical connections.
   */
  protected abstract void closeConnections();

}
