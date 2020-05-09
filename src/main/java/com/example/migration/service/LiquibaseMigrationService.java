package com.example.migration.service;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class LiquibaseMigrationService implements MigrationService {

  private static final String DEFAULT_PATH_TO_CHANGELOG_FILE = "db/migration/liquibase/app-changelog.xml";
  private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseMigrationService.class);

  private String pathToChangeLogFile;

  public LiquibaseMigrationService() {
    pathToChangeLogFile = DEFAULT_PATH_TO_CHANGELOG_FILE;
  }

  public void setPathToChangeLogFile(String pathToChangeLogFile) {
    if (!isBlank(pathToChangeLogFile)) {
      this.pathToChangeLogFile = pathToChangeLogFile;
    }
  }

  @Override
  public void runMigration(DataSource dataSource, String schemaName) {
    ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(
        getClass().getClassLoader());
    try {
      Database database = DatabaseFactory.getInstance()
          .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
      database.setLiquibaseSchemaName(schemaName); //tells in which schema should Liquibase create the  DATABASECHANGELOG and DATABASECHANGELOGLOCK tables. If not given, it uses default schema
      Liquibase liquibase = new Liquibase(pathToChangeLogFile, resourceAccessor, database);
      liquibase.getChangeLogParameters().set("schemaName", schemaName); //This changelog param cant come from configuration, since it is evaluated at runtime
      liquibase.update("");
    } catch (LiquibaseException | SQLException e) {
      LOGGER.error("Liquibase service error: ", e);
    }
  }

  private boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
