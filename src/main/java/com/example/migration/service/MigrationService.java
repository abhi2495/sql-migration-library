package com.example.migration.service;

import javax.sql.DataSource;
import java.sql.Connection;

public interface MigrationService {
  void runMigration(DataSource dataSource, String schemaName);
}
