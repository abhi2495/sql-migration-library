package com.example.config.hibernate.multitenancy;

import com.example.migration.SqlMigrationUtil;
import com.example.migration.service.MigrationService;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchemaMultiTenantConnectionProvider.class);
  private static final String DEFAULT_TENANT = "public";
  private final DataSource datasource;
  private final MigrationService migrationService;
  private final String schemaPrefix;
  private final String dbvendor;

  @Autowired
  public SchemaMultiTenantConnectionProvider(
      DataSource dataSource, MigrationService migrationService,
      @Value("${migration.schema-prefix:}") String schemaPrefix,
      @Value("${spring.jpa.database-platform:H2}") String dbvendor) {
    this.datasource = dataSource;
    this.schemaPrefix = schemaPrefix;
    this.dbvendor = dbvendor;
    this.migrationService = migrationService;
  }

  @Override
  public Connection getAnyConnection() throws SQLException {
    return datasource.getConnection();
  }

  @Override
  public void releaseAnyConnection(Connection connection) throws SQLException {
    connection.close();
  }

  @Override
  public Connection getConnection(String tenantIdentifier) throws SQLException {
    LOGGER.info("Get connection for tenant {}", tenantIdentifier);
    SqlMigrationUtil.createTenantIfDoesNotExistAndMigrate(datasource, tenantIdentifier, schemaPrefix, dbvendor, migrationService);
    return getAnyConnection();
  }

  @Override
  public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
    LOGGER.info("Release connection for tenant {}", tenantIdentifier);
    connection.setSchema(DEFAULT_TENANT);
    releaseAnyConnection(connection);
  }

  @Override
  public boolean supportsAggressiveRelease() {
    return true;
  }

  @Override
  public boolean isUnwrappableAs(Class unwrapType) {
    return false;
  }

  @Override
  public <T> T unwrap(Class<T> unwrapType) {
    return null;
  }

}
