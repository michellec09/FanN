package edu.tec.azuay.faan.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    /*
     * 3 hours
     */
    private static final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 3;

    /*
     * 30 days
     */
    private static final Long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 30;

    public String getAccessToken(UserDetails user, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, user, ACCESS_TOKEN_EXPIRATION);
    }

    public String getRefreshToken(UserDetails user, Map<String, Object> extraClaims) {
        return generateToken(extraClaims, user, REFRESH_TOKEN_EXPIRATION);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails user, Long expiration) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expirationAt = new Date(System.currentTimeMillis() + expiration);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()

                .subject(user.getUsername())
                .issuedAt(issuedAt)
                .expiration(expirationAt)
                .claims(extraClaims)

                .signWith(generatedKey(), Jwts.SIG.HS256)

                .compact();
    }

    private SecretKey generatedKey() {
        byte [] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(passwordDecoded);
    }

    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith(generatedKey()).build()
                .parseSignedClaims(jwt).getPayload();
    }
}
