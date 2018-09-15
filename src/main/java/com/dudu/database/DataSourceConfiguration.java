package com.dudu.database;

import com.zaxxer.hikari.HikariConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {
    private static final Logger logger = LogManager.getLogger(DataSourceConfiguration.class);

    @Value("${dudu.datasource.jdbcUrl}")
    private String jdbcUrl;

    @Value("${dudu.datasource.driver}")
    private String driver;

    @Value("${dudu.datasource.username}")
    private String username;

    @Value("${dudu.datasource.password}")
    private String password;

    @Bean
    public UserServiceDataSource loadUserServiceDataSource() {
        logger.info("load UserService Database");
        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        config.setConnectionTestQuery("SELECT 1");
        config.setDriverClassName(driver);
        return new UserServiceDataSource(config);
    }
}
