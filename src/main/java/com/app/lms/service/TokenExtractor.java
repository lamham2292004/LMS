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

        if (!StringUtils.hasText(bearerToken)) {
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        }

        if (!bearerToken.startsWith(HEADER_PREFIX)) {
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        }

        return bearerToken.substring(HEADER_PREFIX.length());
    }
}
