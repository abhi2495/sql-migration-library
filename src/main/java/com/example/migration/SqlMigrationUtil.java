package com.example.migration;

import com.example.migration.service.MigrationService;
import com.example.utils.CommonUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SqlMigrationUtil {

  private static final Map<String, TenantLock> locks = new ConcurrentHashMap<>();

  public static void createTenantIfDoesNotExistAndMigrate(
      DataSource dataSource, String tenantId, String schemaPrefix,
      String dbVendor, MigrationService migrationService) throws SQLException {
    String schemaName = CommonUtils.formatSchemaName(schemaPrefix, tenantId);
    TenantLock lock = locks.putIfAbsent(tenantId, new TenantLock());
    if (shouldWeLock(lock)) {
      synchronized (locks.get(tenantId)) {
        if (nowThatWeAreLockedShouldWeInit(tenantId)) {
          SchemaCreationUtil.createSchemaIfNotExist(dataSource.getConnection(), schemaName, DatabaseVendor.parseValue(dbVendor));
          migrationService.runMigration(dataSource, schemaName);
          setMigrated(tenantId);
        }
      }
    }
  }

  private static void setMigrated(String tenantId) {
    locks.get(tenantId).getMigrated().set(true);
  }

  private static boolean nowThatWeAreLockedShouldWeInit(String tenantId) {
    return !locks.get(tenantId).getMigrated().get();
  }

  private static boolean shouldWeLock(TenantLock lock) {
    return lock == null || !lock.getMigrated().get();
  }

  static class TenantLock {
    private final AtomicBoolean migrated = new AtomicBoolean();

    AtomicBoolean getMigrated() {
      return migrated;
    }
  }
}
