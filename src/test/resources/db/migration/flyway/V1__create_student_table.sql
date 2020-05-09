CREATE TABLE ${schemaName}.${table_name} (
  id    VARCHAR(255) NOT NULL,
  email VARCHAR(255) DEFAULT NULL,
  name  VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
)
