package com.raz.db.conn;

import javax.sql.PooledConnection;

public interface PooledConnectionWrapper extends ConnectionWrapper {

  PooledConnection getPooledConnection();

}
