package com.app.lms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Prints VNPay configuration on startup for verification
 */
@Configuration
@Slf4j
public class VNPayConfigLogger {

    @Bean
    public CommandLineRunner logVNPayConfig(VNPayConfig vnPayConfig) {
        return args -> {
            log.info("=================================================");
            log.info("VNPay Configuration:");
            log.info("  TMN Code: {}", vnPayConfig.getTmnCode());
            log.info("  Pay URL: {}", vnPayConfig.getPayUrl());
            log.info("  Return URL: {}", vnPayConfig.getReturnUrl());
            log.info("  Frontend URL (auto from CORS): {}", vnPayConfig.getFrontendUrl());
            log.info("=================================================");
        };
    }
}
