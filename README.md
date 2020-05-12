# sql-migration-library

This application uses **schema per tenant (shared database) model**. Assuming that the tenant context is stored in 
threadlocal variable of *TenancyContextHolder* class (through some request interceptor), for every incoming request, 
after resolving the tenant and before providing the connection to the db, this application checks if the tenant is 
already migrated using an in-memory cache, if tenant is not present in migration-cache, then it queries the database 
to check existence of the schema (serviceName_tenantid) and creates the schema if it doesnt exist. Next it does 
flyway/liquibase migration to create or modify tables.
 
Note - 

1. **Liquibase is dbVendor agnostic**. What this means is this application can easily be extended to be used for any 
databases like MySQL/SQLServer. Whereas **Flyway** migrates only sql scripts, so the sql **scripts might be required to
be adjusted to make them compatible with the vendor**. eg `IF NOT EXISTS` in `CREATE TABLE` command works for MySQL but not 
for SQLServer.

2. **Spring Boot auto-configures Liquibase and Flyway** through *LiquibaseAutoConfiguration* and *FlywayAutoConfiguration* 
classes during application startup by **default**. Hence **we have disabled it** by setting `spring.liquibase.enabled=false` 
and `spring.flyway.enabled=false` since we need to run migration not at application startup.

3. SQL Server **doesn't allow** us to set schema using syntax like `SET SCHEMA schema_name` or `USE schema_name`. So we have to 
programmatically resolve the schema every time before Hibernate fires any query. The way we are **tackling this 
problem** here is **by registering a Hibernate Interceptor** which replaces the placeholder `${schemaName}` in all 
prepared statements. This placeholder needs to be defined in all entity classes like this 
`@Table(name = "student", schema = "${schemaName}")`
 
4. Currently the liquibase sample changelog scripts dont use **contexts**. To read more about contexts, please refer 
[here](https://www.liquibase.org/documentation/contexts.html)

5. Sample configuration would look like:

  ```
    spring:
      jmx.enabled: false
      main:
        banner-mode: 'off'
        allow-bean-definition-overriding: true
      server:
        port: 8080
      application:
        name: ${service.name}
      datasource:
        url: ${dbUrl}
        username: ${dbUsername}
        password: ${dbPassword}
        driver-class-name: com.mysql.cj.jdbc.Driver
      jpa:
        database: mysql
        hibernate:
          ddl-auto: none
        properties:
          hibernate:
            temp.use_jdbc_metadata_defaults: true
      liquibase:
        enabled: false
      flyway:
        enabled: false
    
    
    service:
      name: sql-migration-library
    
    migration:
      schema-prefix: ${service.name} #optional, default empty string
      packages-to-scan: com.example* #optional, default = com.example*
      vendor: flyway #optional. default = liquibase
      flyway: #optional
        locations: classpath:db/migration/flyway #optional
        placeholders: #optional
          table_name: student
      liquibase: #optional. default = db/migration/liquibase/app-changelog.xml
        path-to-changelog: db/migration/liquibase/app-changelog.xml
  ```


