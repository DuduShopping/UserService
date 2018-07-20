package com.dudu.oauth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlPermissionConfiguration {
    private static final Logger logger = LogManager.getLogger(PermissionManager.class);

    @Bean
    public PermissionManager getPermissionManager() throws Exception {
        logger.info("Setting up permissions system");
        var filePath = this.getClass().getResource("/permissions.json").getFile();
        var manager = new PermissionManager(filePath);
        return manager;
    }

}
