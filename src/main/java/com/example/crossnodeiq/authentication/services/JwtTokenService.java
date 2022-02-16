package com.example.crossnodeiq.authentication.services;

import com.example.crossnodeiq.authentication.models.User;
import com.example.crossnodeiq.authentication.models.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtTokenService implements TokenService {
    private final String jwtSecret;

    public JwtTokenService(@Value("${authentication.jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    public String generateToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String compactTokenString = Jwts.builder()
                                        .claim("id", user.getId())
                                        .claim("sub", user.getUsername())
                                        //.setExpiration(expirationDate) //For ease of testing we don't set an expiration date. In reality this should be set
                                        .signWith(key, SignatureAlgorithm.HS256)
                                        .compact();

        return "Bearer " + compactTokenString;
    }

    @Override
    public UserPrincipal parseToken(String token) {
        byte[] secretBytes = jwtSecret.getBytes();

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                                    .setSigningKey(secretBytes)
                                    .build()
                                    .parseClaimsJws(token);

        String username = jwsClaims.getBody()
                                   .getSubject();
        Integer userId = jwsClaims.getBody()
                                  .get("id", Integer.class);

        return new UserPrincipal(userId, username, token);
    }
}
