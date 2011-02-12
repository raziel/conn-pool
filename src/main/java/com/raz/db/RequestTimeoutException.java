package com.raz.db;

/**
 * Exception thrown when a request to obtain a Connection expires a set timeout.
 *
 * @author raziel.alvarez
 *
 */
public class RequestTimeoutException extends RuntimeException {

  public RequestTimeoutException() {
  }

  public RequestTimeoutException(String msg) {
    super(msg);
  }

}
