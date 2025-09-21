package com.app.lms.config;

import com.app.lms.service.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // ===== TẤT CẢ PUBLIC ENDPOINTS ĐƯỢC DEFINE TẠI ĐÂY =====
                .authorizeHttpRequests(request ->
                        request
                                // Health check endpoints
                                .requestMatchers("/api/health/**").permitAll()

                                // Test endpoints (development)
                                .requestMatchers("/api/test/**").permitAll()

                                // Public browse endpoints (không cần login)
                                .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/course/**").permitAll()

                                // Static file serving
                                .requestMatchers("/uploads/**").permitAll()

                                // CORS preflight requests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                // Actuator endpoints (có thể cần auth tùy environment)
                                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                .requestMatchers("/actuator/**").hasRole("ADMIN")

                                // API Documentation (development only)
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                                // Tất cả endpoints khác cần authentication
                                .anyRequest().authenticated()
                )

                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Stateless session (không sử dụng session)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF cho API (vì dùng JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // Disable form login và http basic
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Exception handling
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"code\": 2001, \"message\": \"Vui lòng đăng nhập để tiếp tục\"}");
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"code\": 2101, \"message\": \"Không có quyền truy cập\"}");
                                })
                );

        return httpSecurity.build();
    }
}