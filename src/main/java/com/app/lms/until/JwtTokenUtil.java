package com.app.lms.until;

import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.enums.UserType;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
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
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            log.error("Token không được hỗ trợ: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        } catch (MalformedJwtException e) {
            log.error("Token không đúng format: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("Signature không hợp lệ: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.error("Token rỗng: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        } catch (Exception e) {
            log.error("Lỗi không xác định khi parse token: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // Update getUserIdFromToken method
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                userIdObj = claims.getSubject(); // sub: 1
            }

            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                return Long.parseLong((String) userIdObj);
            }

            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
        }
    }


    // Update getUserTypeFromToken method
    public UserType getUserTypeFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String userType = (String) claims.get("userType");
            if (userType == null) {
                userType = (String) claims.get("user_type");
            }
            if (userType == null) {
                throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
            }
            return UserType.valueOf(userType.toUpperCase());
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting user type from token: {}", e.getMessage());
            return UserType.STUDENT;
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
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
        } catch (Exception e) {
            return false;
        }
    }

    // Update getUserInfoFromToken method
    public UserTokenInfo getUserInfoFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);

            return UserTokenInfo.builder()
                    .userId(getUserIdFromToken(token))
                    .accountId(extractLongFromClaims(claims, "accountId"))
                    .username((String) claims.get("username"))
                    .email((String) claims.get("email"))
                    .fullName((String) claims.get("full_name"))
                    .userType(getUserTypeFromToken(token))
                    .studentCode((String) claims.get("studentCode"))
                    .lecturerCode((String) claims.get("lecturerCode"))
                    .classId(extractLongFromClaims(claims, "classId"))
                    .departmentId(extractLongFromClaims(claims, "departmentId"))
                    .isAdmin(extractBooleanFromClaims(claims, "isAdmin"))
                    .build();
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting user info from token: {}", e.getMessage());
            throw new AppException(ErroCode.UNCATEGORIZED_EXCEPTION);
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

    // Thêm vào JwtTokenUtil.java
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
