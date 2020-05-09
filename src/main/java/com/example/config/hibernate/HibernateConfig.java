package com.example.config.hibernate;

import com.example.tenancy.TenancyContextHolder;
import com.example.utils.CommonUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig {

  @Autowired
  private JpaProperties jpaProperties;

  @Value("${migration.schema-prefix}")
  private String schemaPrefix;

  @Value("${migration.packages-to-scan}")
  private String packagesToScan;

  @Bean
  JpaVendorAdapter jpaVendorAdapter() {
    return new HibernateJpaVendorAdapter();
  }

  @Bean
  LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource,
      MultiTenantConnectionProvider multiTenantConnectionProviderImpl,
      CurrentTenantIdentifierResolver currentTenantIdentifierResolverImpl
  ) {

    Map<String, Object> jpaPropertiesMap = new HashMap<>(jpaProperties.getProperties());
    jpaPropertiesMap.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
    jpaPropertiesMap.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
    jpaPropertiesMap.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolverImpl);
    jpaPropertiesMap.put("hibernate.session_factory.interceptor", hibernateInterceptor());
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan(packagesToScan);
    em.setJpaVendorAdapter(this.jpaVendorAdapter());
    em.setJpaPropertyMap(jpaPropertiesMap);
    return em;
  }

  private EmptyInterceptor hibernateInterceptor() {
    return new EmptyInterceptor() {
      @Override
      public String onPrepareStatement(String sql) {
        String preparedStatement = super.onPrepareStatement(sql);
        preparedStatement = preparedStatement.replaceAll("\\$\\{schemaName\\}",
            CommonUtils.formatSchemaName(schemaPrefix, TenancyContextHolder.getContext().getTenantId()));
        return preparedStatement;
      }
    };
  }
}

