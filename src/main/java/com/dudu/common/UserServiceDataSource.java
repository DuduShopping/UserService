package com.dudu.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class UserServiceDataSource extends HikariDataSource {
    public UserServiceDataSource(HikariConfig config) {
        super(config);
    }
}
