/*
 * Copyright (c) 2012-2017 Snowflake Computing Inc. All rights reserved.
 */

package net.snowflake.client.jdbc;

import net.snowflake.client.core.SFSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import net.snowflake.client.log.SFLoggerFactory;
import net.snowflake.client.log.SFLogger;

/**
 *
 * @author jhuang
 */
public class SnowflakeDatabaseMetaDataResultSet extends
        SnowflakeBaseResultSet
{
  protected ResultSet showObjectResultSet;
  protected Statement statement;
  protected Object[][] rows;

  static final SFLogger logger = SFLoggerFactory.getLogger(
          SnowflakeDatabaseMetaDataResultSet.class);

  /**
   * Used for creating an empty result set
   */
  public SnowflakeDatabaseMetaDataResultSet()
  {
  }

  /**
   * DatabaseMetadataResultSet based on result from show command
   * @param columnNames column names
   * @param columnTypeNames column type names
   * @param columnTypes column types
   * @param showObjectResultSet result set after issuing a show command
   * @param statement show command statement
   * @throws SQLException if failed to construct snowflake database metadata result set
   */
 	public SnowflakeDatabaseMetaDataResultSet(
          final List<String> columnNames,
          final List<String> columnTypeNames,
          final List<Integer> columnTypes,
          final ResultSet showObjectResultSet,
          final Statement statement)
          throws SQLException
  {
    this.showObjectResultSet = showObjectResultSet;

    SFSession session = ((SnowflakeConnectionV1)statement.getConnection()).getSfSession();

    this.resultSetMetaData = new SnowflakeResultSetMetaData(columnNames.size(),
                                                            columnNames,
                                                            columnTypeNames,
                                                            columnTypes,
                                                            session);

    this.nextRow = new Object[columnNames.size()];

    this.statement = statement;
 	}

  /**
   * DatabaseMetadataResultSet based on a constant rowset.
   * @param columnNames column name
   * @param columnTypeNames column types name
   * @param columnTypes column type
   * @param rows returned value of database metadata
   * @param statement show command statement
   * @throws SQLException if failed to construct snowflake database metadata result set
   */
 	public SnowflakeDatabaseMetaDataResultSet(
          final List<String> columnNames,
          final List<String> columnTypeNames,
          final List<Integer> columnTypes,
          final Object[][] rows,
          final Statement statement)
          throws SQLException
  {
    this.rows = rows;
    
    SFSession session = ((SnowflakeConnectionV1)statement.getConnection()).getSfSession();

    this.resultSetMetaData = new SnowflakeResultSetMetaData(columnNames.size(),
                                                            columnNames,
                                                            columnTypeNames,
                                                            columnTypes,
                                                            session);

    this.nextRow = new Object[columnNames.size()];

    this.statement = statement;
 	}

 	public SnowflakeDatabaseMetaDataResultSet(DBMetadataResultSetMetadata metadataType,
                                            ResultSet resultSet,
                                            Statement statement) throws SQLException
  {
    this(metadataType.getColumnNames(), metadataType.getColumnTypeNames(),
        metadataType.getColumnTypes(), resultSet, statement);
  }

  public SnowflakeDatabaseMetaDataResultSet(DBMetadataResultSetMetadata metadataType,
                                            Object[][] rows,
                                            Statement statement) throws SQLException
  {
    this(metadataType.getColumnNames(), metadataType.getColumnTypeNames(),
        metadataType.getColumnTypes(), rows, statement);
  }

  public boolean next() throws SQLException
  {
    logger.debug("public boolean next()");

    // iterate throw the show table result until we find an entry
    // that matches the table name
    while (row < rows.length)
    {
      nextRow = rows[row++];
      return true;
    }

    statement.close();
    statement = null;

    return false;
  }

  public void close() throws SQLException
  {
    if(statement != null)
    {
      statement.close();
      statement = null;
    }

  }

  static ResultSet getEmptyResultSet(DBMetadataResultSetMetadata metadataType, Statement statement)
      throws SQLException
  {
    return new SnowflakeDatabaseMetaDataResultSet(metadataType, new Object[][]{}, statement);
  }
}
