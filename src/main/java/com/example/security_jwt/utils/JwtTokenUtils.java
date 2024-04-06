package com.example.security_jwt.utils;

import com.example.security_jwt.entities.User;
import com.example.security_jwt.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;
    private final UserService userService;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roleList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", roleList);

        //добавление названия предприятия в токен
        Optional<User> userOptional = userService.findByUsername(userDetails.getUsername());
        userOptional.ifPresent(user -> claims.put("company", user.getCompany().getCompanyName()));

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsername(String token) {
        Claims claims = getAllClaimsFromToken(token);
        System.out.println(claims.toString());
        String username = getAllClaimsFromToken(token).getSubject();
        System.out.println(username);
        return getAllClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
