package com.example.config.migration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "migration.flyway")
public class FlywayProps {

  private List<String> locations = new ArrayList<>(Collections.singletonList("classpath:db/migration/flyway"));

  private Map<String, String> placeholders = new HashMap<>();

  public List<String> getLocations() {
    return locations;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public Map<String, String> getPlaceholders() {
    return placeholders;
  }

  public void setPlaceholders(Map<String, String> placeholders) {
    this.placeholders = placeholders;
  }
}
