package org.example.security.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.data.model.User;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;


    public boolean isTokenValid(String token){
        return false;
    }

    public String generateJwt(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getId());  // ✅ Add user ID
        claims.put("roles", userPrincipal.getAuthorities().toString()); //
        log.info("Generating JWT for user: {}, Claims: {}", userPrincipal.getEmail(), claims); // ✅ Debug log

        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .claims(claims)  // ✅ Use userPrincipal
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusMillis(jwtExpiration)))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    public<T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUserNameFromJwtToken(String token){
        return extractClaims(token,Claims::getSubject);
    }

//    public String generateToken(Map<String,Object> extractClaims, User user){
//        log.info("This is the key{}",getSigningKey());
//        return Jwts.builder()
//                .claims(extractClaims)
//                .claim("userId", user.getId())  // ✅ Use userPrincipal
//                .claim("roles", user.getRole())
//                .subject(user.getEmail())
//                .issuedAt(Date.from(Instant.now()))
//                .expiration(Date.from(Instant.now().plusSeconds(jwtExpiration)))
//                .signWith(getSigningKey()).compact();
//    }
//
//    public String generateToken(User user){
//        return generateToken(new HashMap<>(),user);
//    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = getUserNameFromJwtToken(token);
        return username.equals(userDetails.getUsername()) && isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).after(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token,Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public Long extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", Long.class));
    }

}
