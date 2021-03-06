/*
 * Copyright (c) 2012-2017 Snowflake Computing Inc. All rights reserved.
 */

package net.snowflake.client.jdbc;

import net.snowflake.common.core.ResourceBundleManager;
import java.sql.SQLException;
import net.snowflake.client.log.SFLogger;
import net.snowflake.client.log.SFLoggerFactory;

/**
 * @author jhuang
 */
public class SnowflakeSQLException extends SQLException
{
  static final SFLogger logger =
                       SFLoggerFactory.getLogger(SnowflakeSQLException.class);

  static final ResourceBundleManager errorResourceBundleManager =
  ResourceBundleManager.getSingleton(ErrorCode.errorMessageResource);

  private String queryId = "unknown";

  /**
   * This constructor should only be used for error from
   * Global service. Since Global service has already built the error message,
   * we use it as is. For any errors local to JDBC driver, we should use one
   * of the constructors below to build the error message.
   * @param queryId query id
   * @param reason reason for which exception is created
   * @param sqlState sql state
   * @param vendorCode vendor code
   */
  public SnowflakeSQLException(String queryId,
                               String reason,
                               String sqlState,
                               int vendorCode)
  {
    super(reason, sqlState, vendorCode);

    this.queryId = queryId;

    // log user error from GS at fine level
    logger.debug("Snowflake exception: {}, sqlState:{}, vendorCode:{}, queryId:{}",
                reason, sqlState, vendorCode, queryId);

  }

  public SnowflakeSQLException(String sqlState, int vendorCode)
  {
    super(errorResourceBundleManager.getLocalizedMessage(
            String.valueOf(vendorCode)), sqlState, vendorCode);

    logger.debug("Snowflake exception: {}, sqlState:{}, vendorCode:{}",
                errorResourceBundleManager.getLocalizedMessage(String.valueOf(vendorCode)),
                sqlState,
                vendorCode);
  }

  public SnowflakeSQLException(String sqlState, int vendorCode, Object... params)
  {
    super(errorResourceBundleManager.getLocalizedMessage(
            String.valueOf(vendorCode), params), sqlState, vendorCode);

    logger.debug("Snowflake exception: {}, sqlState:{}, vendorCode:{}",
                errorResourceBundleManager.getLocalizedMessage(
                  String.valueOf(vendorCode), params),
                sqlState,
                vendorCode);
  }

  public SnowflakeSQLException(Throwable ex, String sqlState, int vendorCode)
  {
    super(errorResourceBundleManager.getLocalizedMessage(
            String.valueOf(vendorCode)), sqlState, vendorCode, ex);

    logger.debug("Snowflake exception: {}" +
                              errorResourceBundleManager.getLocalizedMessage(
                              String.valueOf(vendorCode)), ex);
  }

  public SnowflakeSQLException(Throwable ex,
                               String sqlState,
                               int vendorCode,
                               Object... params)
  {
    super(errorResourceBundleManager.getLocalizedMessage(
            String.valueOf(vendorCode), params), sqlState, vendorCode, ex);

    logger.debug("Snowflake exception: " +
                           errorResourceBundleManager.getLocalizedMessage(
                                   String.valueOf(vendorCode), params), ex);
  }

  public String getQueryId()
  {
    return queryId;
  }
}
