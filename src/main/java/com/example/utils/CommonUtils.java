package com.example.utils;

import org.springframework.util.StringUtils;

public interface CommonUtils {

  static String formatSchemaName(String schemaPrefix, String tenantId) {
    if (StringUtils.isEmpty(schemaPrefix)) {
      return format(tenantId);
    }
    String formattedPrefix = format(schemaPrefix);
    if (tenantId.startsWith(formattedPrefix)) {
      return tenantId;
    }
    return formattedPrefix.concat("_").concat(format(tenantId));
  }

  static String format(String string) {
    return string.replaceAll("-", "_").toUpperCase();
  }
}
