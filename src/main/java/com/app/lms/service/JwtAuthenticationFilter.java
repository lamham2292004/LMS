package com.app.lms.service;

import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.until.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    final JwtTokenUtil jwtTokenUtil;
    final TokenExtractor tokenExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Skip JWT processing cho public endpoints
            String requestPath = request.getRequestURI();
            if (isPublicEndpoint(requestPath)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract token
            String token = tokenExtractor.extractTokenFromRequest(request);

            // Validate token
            if (jwtTokenUtil.validateToken(token)) {
                // Get user info from token
                UserTokenInfo userInfo = jwtTokenUtil.getUserInfoFromToken(token);

                // Convert UserType to Spring Security roles
                Collection<GrantedAuthority> authorities = convertUserTypeToAuthorities(userInfo);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userInfo.getUserId().toString(),
                                null, // credentials (không cần password)
                                authorities
                        );

                // Set additional details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT Authentication successful for user: {} (ID: {}) with roles: {}",
                        userInfo.getEmail(), userInfo.getUserId(), authorities);
            }

        } catch (AppException e) {
            log.warn("JWT Authentication failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private Collection<GrantedAuthority> convertUserTypeToAuthorities(UserTokenInfo userInfo) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        UserType userType = userInfo.getUserType();

        switch (userType) {
            case STUDENT -> authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            case LECTURER -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_LECTURER"));
                if (Boolean.TRUE.equals(userInfo.getIsAdmin())) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
            }
            case ADMIN -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_LECTURER"));
                authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            }
        }

        return authorities;
    }

    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api/test/") ||
                requestPath.equals("/api/category") ||
                requestPath.equals("/api/course") ||
                requestPath.startsWith("/api/health") ||
                requestPath.startsWith("/uploads/");
    }
}
