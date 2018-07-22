package com.dudu.database;

import com.zaxxer.hikari.HikariConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DataSourceConfiguration {
    private static final Logger logger = LogManager.getLogger(DataSourceConfiguration.class);

    @Bean
    public UserServiceDataSource loadUserServiceDataSource() {
        logger.info("load UserService Database");
        try (InputStream in = this.getClass().getResourceAsStream("/db.conf")) {
            Properties properties = new Properties();
            properties.load(in);

            String jdbcUrl = properties.getProperty("DuduShopping.jdbcUrl").trim();
            String driver = properties.getProperty("DuduShopping.driver").trim();
            String username = properties.getProperty("DuduShopping.username").trim();
            String password = properties.getProperty("DuduShopping.password").trim();

            String dbProperties = properties.getProperty("DuduShopping" + ".properties");
            Map<String, String> dbPropertiesMap = new LinkedHashMap<>();
            if (dbProperties != null) {
                for (String dbProp : dbProperties.split(",")) {
                    dbProp = dbProp.trim();
                    String val = properties.getProperty("DuduShopping" + ".properties." + dbProp).trim();
                    dbPropertiesMap.put(dbProp, val);
                }
            }

            // adding to dataSources
            HikariConfig config = new HikariConfig();
            config.setUsername(username);
            config.setPassword(password);
            config.setJdbcUrl(jdbcUrl);
            config.setConnectionTestQuery("SELECT 1");
            config.setDriverClassName(driver);
            for (String prop: dbPropertiesMap.keySet())
                config.addDataSourceProperty(prop, dbPropertiesMap.get(prop));
            return new UserServiceDataSource(config);
        } catch (Exception e) {
            logger.error("Failed to init the application: ", e);
            return null;
        }
    }
}
