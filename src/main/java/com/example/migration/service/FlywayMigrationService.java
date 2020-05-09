package com.example.migration.service;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class FlywayMigrationService implements MigrationService {

  private List<String> locations;
  private Map<String, String> placeholders;

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public void setPlaceholders(Map<String, String> placeholders) {
    this.placeholders = placeholders;
  }

  @Override
  public void runMigration(DataSource dataSource, String schemaName) {
    placeholders.put("schemaName", schemaName);
    FluentConfiguration configuration = Flyway.configure().dataSource(dataSource).placeholders(placeholders);
    locations.forEach(configuration::locations);
    Flyway flyway = configuration.schemas(schemaName).load();
    //flyway.repair(); //required if there is checksum mismatch during upgrading of flyway from v3
    flyway.migrate();
  }
}
