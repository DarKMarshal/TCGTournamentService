package Services.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final String DEFAULT_SECRET = "default-dev-secret-key-change-in-production-must-be-at-least-32-bytes";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            (System.getenv("JWT_SECRET") != null ? System.getenv("JWT_SECRET") : DEFAULT_SECRET).getBytes()
    );
    private static final long EXPIRATION_MS = 86400000; // 24 hours

    public static String generateToken(int accountId, String username, String role) {
        return Jwts.builder()
                .subject(String.valueOf(accountId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    public static io.jsonwebtoken.Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}