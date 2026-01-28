package com.app.lms.service;

import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class TokenExtractor {
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_HEADER);

        // Check if Authorization header exists
        if (!StringUtils.hasText(bearerToken)) {
            log.debug("No Authorization header found");
            throw new AppException(ErroCode.TOKEN_MISSING);
        }

        // Check if it starts with "Bearer "
        if (!bearerToken.startsWith(HEADER_PREFIX)) {
            log.debug("Authorization header does not start with Bearer");
            throw new AppException(ErroCode.TOKEN_INVALID);
        }

        String token = bearerToken.substring(HEADER_PREFIX.length());

        // Check if token is not empty after removing "Bearer "
        if (!StringUtils.hasText(token)) {
            log.debug("Token is empty after removing Bearer prefix");
            throw new AppException(ErroCode.TOKEN_MISSING);
        }

        return token;
    }
}
