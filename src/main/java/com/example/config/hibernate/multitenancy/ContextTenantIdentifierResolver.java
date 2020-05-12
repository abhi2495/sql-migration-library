package com.example.config.hibernate.multitenancy;

import static com.example.tenancy.Tenant.DEFAULT_TENANT;

import com.example.tenancy.TenancyContextHolder;
import com.example.tenancy.Tenant;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class ContextTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

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
