package com.bankservice.config;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import java.sql.Types;

public class SQLiteDialect extends Dialect {
    public SQLiteDialect() {
        super();
        registerColumnType(Types.INTEGER, "integer");
        registerColumnType(Types.VARCHAR, "varchar");
        registerColumnType(Types.BLOB, "blob");
    }
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new IdentityColumnSupportImpl();
    }
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }
}
