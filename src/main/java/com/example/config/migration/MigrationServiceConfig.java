package com.example.config.migration;

import com.example.migration.service.FlywayMigrationService;
import com.example.migration.service.LiquibaseMigrationService;
import com.example.migration.service.MigrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(FlywayProps.class)
public class MigrationServiceConfig {

  @Value("${migration.liquibase.path-to-changelog:#{null}}")
  private String pathToChangeLog;

  @Bean
  @ConditionalOnProperty(value = "migration.vendor", havingValue = "flyway")
  public MigrationService flywayMigration(FlywayProps flywayProps) {
    FlywayMigrationService migrationService = new FlywayMigrationService();
    migrationService.setLocations(flywayProps.getLocations());
    migrationService.setPlaceholders(flywayProps.getPlaceholders());
    return migrationService;
  }

  @Bean
  @Primary
  @ConditionalOnProperty(value = "migration.vendor", havingValue = "liquibase", matchIfMissing = true)
  public MigrationService liquibaseMigration() {
    LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
    migrationService.setPathToChangeLogFile(pathToChangeLog);
    return migrationService;
  }

}
