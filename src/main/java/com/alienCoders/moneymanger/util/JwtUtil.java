package com.alienCoders.moneymanger.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component  // Marks this class as a Spring-managed component (so we can use it anywhere with @Autowired)
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;  // Secret key (from application.properties) → used to sign/verify tokens

    @Value("${jwt.expiration}")
    private long expirationTime; // Expiration time for token (in ms)

    private Key keys; // Will hold the signing key

    // When the bean is created, we initialize the signing key
    @PostConstruct
    public void init() {
        keys = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // ✅ Generate JWT Token
    // This creates a token with: email (subject), issue time, expiry time, and signed with our key
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // who the token belongs to (the user's email)
                .setIssuedAt(new Date()) // when the token was created
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // when it will expire
                .signWith(keys, SignatureAlgorithm.HS256) // sign it with our secret key using HS256
                .compact(); // generate the final token string
    }

    // ✅ Extract email (subject) from token
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(keys) // set the signing key to validate
                .build()
                .parseClaimsJws(token) // parse the token
                .getBody()
                .getSubject(); // return the email stored in token
    }

    // ✅ Validate Token
    // Checks if the email in token matches user email AND if token is not expired
    public boolean validateToken(String token, String userEmail) {
        try {
            String extractedEmail = extractEmail(token);
            return extractedEmail.equals(userEmail) && !isTokenExpired(token);
        } catch (Exception e) {
            return false; // if token is invalid/expired, return false
        }
    }

    // ✅ Check if token is expired
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(keys)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date()); // true if expired
    }
}
