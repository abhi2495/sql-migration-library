package com.example.migration;

import java.sql.Connection;
import java.sql.SQLException;

final class SchemaCreationUtil {

  private static final String CREATE_SCHEMA_SCRIPT_SQL_SERVER
      = "IF NOT EXISTS ( SELECT  schema_name FROM information_schema.schemata WHERE schema_name = '${schemaName}') \n" +
      "BEGIN\n" +
      "EXEC sp_executesql N'CREATE SCHEMA ${schemaName}' \n" +
      "END";
  private static final String CREATE_SCHEMA_SCRIPT_DEFAULT = "CREATE SCHEMA IF NOT EXISTS ${schemaName}";

  static void createSchemaIfNotExist(Connection connection, String schemaName, DatabaseVendor dbVendor) throws SQLException {
    connection.createStatement().execute(getSchemaCreationQuery(schemaName, dbVendor));
  }

  private static String getSchemaCreationQuery(String schemaName, DatabaseVendor dbVendor) {
    switch (dbVendor) {
      case SQL_SERVER:
        return CREATE_SCHEMA_SCRIPT_SQL_SERVER.replaceAll("\\$\\{schemaName\\}", schemaName);
      default: // MySQL / H2
        return CREATE_SCHEMA_SCRIPT_DEFAULT.replaceAll("\\$\\{schemaName\\}", schemaName);
    }
  }
}
