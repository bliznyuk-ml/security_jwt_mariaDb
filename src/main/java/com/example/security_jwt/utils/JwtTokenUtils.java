package com.example.security_jwt.utils;

import com.example.security_jwt.entities.Role;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    @Value("${jwt.refresh-token-lifetime}")
    private Duration refreshTokenLifetime;

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
        userOptional.ifPresent(user -> claims.put("first_name", user.getFirstName()));
        userOptional.ifPresent(user -> claims.put("last_name", user.getLastName()));

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

//    public String generateToken(UserDetails userDetails){
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtLifetime.toMillis()))
//                .signWith(SignatureAlgorithm.HS512, secret)
//                .compact();
//    }

    public String generateRefreshToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenLifetime.toMillis()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

//    private String createToken(Map<String, Object> claims, String subject, Long expiration){
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(SignatureAlgorithm.HS512, secret)
//                .compact();
//    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String getUsername(String token) {
//        Claims claims = getAllClaimsFromToken(token);
//        System.out.println(claims.toString());
//        String username = getAllClaimsFromToken(token).getSubject();
//        System.out.println(username);
//        return getAllClaimsFromToken(token).getSubject();
        return getClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        //return getAllClaimsFromToken(token).get("roles", List.class);
        return getClaims(token).get("roles", List.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

//    public <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver){
//        final Claims claims = getAllClaimsFromToken(token);
//        return claimResolver.apply(claims);
//    }
//
//    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public Boolean isTokenExpired(String token){
//        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
//        return expiration.before(new Date());
//    }
//
//    public String refreshToken(String refreshToken){
//        final String userName = getUsername(refreshToken);
//        Optional<User> userOptional = userService.findByUsername(userName);
//
//        if(userOptional.isPresent() && isTokenExpired(refreshToken)){
//            User user = userOptional.get();
//            return generateToken(userName, user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
//        } else {
//            throw new RuntimeException("Refresh token os invalid or expiried");
//        }
//    }

}
