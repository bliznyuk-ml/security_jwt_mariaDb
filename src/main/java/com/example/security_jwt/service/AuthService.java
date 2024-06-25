package com.example.security_jwt.service;

import com.example.security_jwt.dtos.JwtRequest;
import com.example.security_jwt.dtos.JwtResponse;
import com.example.security_jwt.dtos.RegistrationUserDto;
import com.example.security_jwt.dtos.UserDto;
import com.example.security_jwt.entities.Role;
import com.example.security_jwt.entities.User;
import com.example.security_jwt.exceptions.AppError;
import com.example.security_jwt.exceptions.DuplicateEmailException;
import com.example.security_jwt.utils.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

//    public ResponseEntity<?> createAuthToken(JwtRequest authRequest){
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        } catch (BadCredentialsException e){
//            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Невірний логін чи пароль"), HttpStatus.UNAUTHORIZED);
//        }
//        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
//        String token = jwtTokenUtils.generateToken(userDetails);
//        return ResponseEntity.ok(new JwtResponse(token));
//    }

    public ResponseEntity<?> createAuthToken(JwtRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtils.generateToken(userDetails);
        String refreshToken = jwtTokenUtils.generateRefreshToken(userDetails);
        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + jwt + "; HttpOnly; Path=/")
                .header("Set-Cookie", "refreshToken=" + refreshToken + "; HttpOnly; Path=/refresh-token")
                .body(new JwtResponse(jwt, refreshToken));
    }

//    public ResponseEntity<?> refreshAuthToken(HttpServletRequest request, HttpServletResponse response) {
//        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
//                .filter(cookie -> "jwt-token".equals(cookie.getName()))
//                .findFirst();
//        if (jwtCookie.isPresent()) {
//            String token = jwtCookie.get().getValue();
//            if (jwtTokenUtils.isTokenExpired(token)) {
//                return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Token expired"), HttpStatus.UNAUTHORIZED);
//            }
//            String refreshedToken = jwtTokenUtils.refreshToken(token);
//            addCookie(response, "jwt-token", refreshedToken, (int) jwtTokenUtils.getJwtLifetime().getSeconds());
//            return ResponseEntity.ok(new JwtResponse(refreshedToken));
//        }
//        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Token not found"), HttpStatus.UNAUTHORIZED);
//    }

    public ResponseEntity<?> refreshAuthToken(String refreshToken){
        if(!jwtTokenUtils.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        String username = jwtTokenUtils.getUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        String newJwt = jwtTokenUtils.generateToken(userDetails);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken(userDetails);

        return ResponseEntity.ok()
                .header("Set-Cookie", "accessToken=" + newJwt + "; HttpOnly; Path=/")
                .header("Set-Cookie", "refreshToken=" + newRefreshToken + "; HttpOnly; Path=/refresh-token")
                .body(new JwtResponse(newJwt, newRefreshToken));
    }



    public ResponseEntity<?> createNewUser(RegistrationUserDto registrationUserDto){
        try {
            if(!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())){
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Паролі не співпадають"), HttpStatus.BAD_REQUEST);
            }
            if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()){
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Користувач з ім'ям " + registrationUserDto.getUsername() + " вже існує"), HttpStatus.BAD_REQUEST);
            }
            userService.createNewUser(registrationUserDto);
            return ResponseEntity.ok("User successfully created");
        } catch (DuplicateEmailException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.CONFLICT.value(), "Цей email " + registrationUserDto.getEmail() + " вже існує"), HttpStatus.CONFLICT);
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
