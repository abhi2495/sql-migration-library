package com.example.migration;

public enum DatabaseVendor {
  H2,
  MYSQL,
  ORACLE,
  SQL_SERVER;

  public static final DatabaseVendor parseValue(String value) {
    String dbVendorVal = value.toUpperCase();
    if (dbVendorVal.contains(MYSQL.name())) {
      return MYSQL;
    } else if (dbVendorVal.contains(ORACLE.name())) {
      return ORACLE;
    } else if (dbVendorVal.contains(SQL_SERVER.name())) {
      return SQL_SERVER;
    }
    return H2;
  }
}

