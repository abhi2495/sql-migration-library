package com.example.config.hibernate.multitenancy;

import com.example.tenancy.TenancyContextHolder;
import com.example.tenancy.Tenant;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class ContextTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

  private static final String DEFAULT_TENANT = "public";

  @Override
  public String resolveCurrentTenantIdentifier() {
    Tenant tenant = TenancyContextHolder.getContext().getTenant();
    if (tenant != null) {
      return tenant.getId();
    }
    return DEFAULT_TENANT;
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return false;
  }
}
