package com.bankservice.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLiteDialect;

// Use the built-in SQLiteDialect from Hibernate 6
public class SQLiteDialect extends org.hibernate.dialect.SQLiteDialect {
}
