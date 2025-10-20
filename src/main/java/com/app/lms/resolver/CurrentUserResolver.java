package com.app.lms.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.annotation.CurrentUserId;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.service.TokenExtractor;
import com.app.lms.until.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenExtractor tokenExtractor;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) && parameter.getParameterType().equals(Long.class)
                || parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().equals(UserTokenInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        try {
            // 1. Extract token from request
            String token = tokenExtractor.extractTokenFromRequest(request);

            // 2. Debug token (remove in production)
            jwtTokenUtil.debugToken(token);

            // 3. Validate token
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("Token validation failed");
                throw new AppException(ErroCode.TOKEN_EXPIRED);
            }

            // 4. Return appropriate data based on annotation
            if (parameter.hasParameterAnnotation(CurrentUserId.class)) {
                return jwtTokenUtil.getUserIdFromToken(token);
            }

            if (parameter.hasParameterAnnotation(CurrentUser.class)) {
                return jwtTokenUtil.getUserInfoFromToken(token);
            }

            return null;
        } catch (AppException e) {
            // Re-throw our custom exceptions with specific error codes
            log.warn("JWT processing failed: {} - {}", e.getErroCode().getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in CurrentUserResolver: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        }
    }
}
