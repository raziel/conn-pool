package com.raz.db;

import java.sql.SQLException;

import com.raz.db.conn.ConnectionWrapper;

public interface ConnectionProvider<T extends ConnectionWrapper> {

  T getConnection() throws SQLException;

}
