package com.app.lms.until;

import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtTokenUtil {
    @Value("${app.jwt.secret}")
    String secret;

    SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("Token đã hết hạn: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("Token không được hỗ trợ: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            log.error("Token không đúng format: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("Signature không hợp lệ: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            log.error("Token rỗng: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_MISSING);
        } catch (Exception e) {
            log.error("Lỗi không xác định khi parse token: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                userIdObj = claims.getSubject(); // Fallback to "sub"
            }

            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                return Long.parseLong((String) userIdObj);
            }

            log.error("Cannot extract user ID from token - no valid userId or sub claim found");
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (AppException e) {
            throw e; // Re-throw JWT exceptions
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        }
    }

    public UserType getUserTypeFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String userType = (String) claims.get("userType");
            if (userType == null) {
                userType = (String) claims.get("user_type"); // Identity Service uses "user_type"
            }
            if (userType == null) {
                userType = (String) claims.get("role"); // Fallback
            }

            if (userType == null) {
                log.error("No user type found in token");
                throw new AppException(ErroCode.TOKEN_INVALID);
            }

            return UserType.valueOf(userType.toUpperCase());
        } catch (AppException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Invalid user type in token: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        } catch (Exception e) {
            log.error("Error extracting user type from token: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (AppException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    public UserTokenInfo getUserInfoFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);

            return UserTokenInfo.builder()
                    .userId(getUserIdFromToken(token))
                    .accountId(extractLongFromClaims(claims, "account_id"))
                    .username((String) claims.get("username"))
                    .email((String) claims.get("email"))
                    .fullName((String) claims.get("full_name")) // Identity Service uses "full_name"
                    .userType(getUserTypeFromToken(token))
                    .studentCode((String) claims.get("studentCode"))
                    .lecturerCode((String) claims.get("lecturerCode"))
                    .classId(extractLongFromClaims(claims, "class_id"))
                    .departmentId(extractLongFromClaims(claims, "department_id"))
                    .isAdmin(extractBooleanFromClaims(claims, "is_admin"))
                    .build();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting user info from token: {}", e.getMessage());
            throw new AppException(ErroCode.TOKEN_INVALID);
        }
    }

    private Long extractLongFromClaims(Claims claims, String key) {
        try {
            Object value = claims.get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean extractBooleanFromClaims(Claims claims, String key) {
        try {
            Object value = claims.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Debug method - có thể remove trong production
    public void debugToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            log.info("=== JWT TOKEN DEBUG ===");
            log.info("Subject: {}", claims.getSubject());
            log.info("All claims: {}", claims);
            claims.forEach((key, value) -> {
                log.info("Claim - {}: {}", key, value);
            });
        } catch (Exception e) {
            log.error("Debug token failed: {}", e.getMessage());
        }
    }

}
