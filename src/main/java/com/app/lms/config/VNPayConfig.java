package com.app.lms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@Data
public class VNPayConfig {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.secret-key}")
    private String secretKey;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    // Inject CORS allowed origins
    @Value("${cors.allowed-origins}")
    private String corsAllowedOrigins;

    // Frontend URL sẽ tự động lấy từ CORS config
    private String frontendUrl;

    @PostConstruct
    public void init() {
        // Tự động lấy origin đầu tiên từ CORS config
        if (corsAllowedOrigins != null && !corsAllowedOrigins.isEmpty()) {
            String[] origins = corsAllowedOrigins.split(",");
            frontendUrl = origins[0].trim();
        } else {
            frontendUrl = "http://localhost:3000"; // fallback
        }
    }
}
