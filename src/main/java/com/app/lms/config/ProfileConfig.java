package com.app.lms.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
@Profile("development")
@ConditionalOnProperty(name = "app.security.debug", havingValue = "true")
public class ProfileConfig {
    @PostConstruct
    public void enableSecurityDebug() {
        log.info("=== DEVELOPMENT MODE - Security Debug Enabled ===");
        log.info("This should NOT be enabled in production!");
    }

    @PostConstruct
    public void productionSecuritySettings() {
        log.info("=== PRODUCTION MODE - Enhanced Security Enabled ===");
    }
}
