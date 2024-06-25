package com.example.security_jwt.controllers;

import com.example.security_jwt.dtos.JwtRequest;
import com.example.security_jwt.dtos.RegistrationUserDto;
import com.example.security_jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        System.out.println(authRequest);
        System.out.println("Login controller worked");
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/hello")
    public ResponseEntity<?> helloWorld(@RequestBody String request){
        System.out.println("Hello World");
        System.out.println(request);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto){
       return authService.createNewUser(registrationUserDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAuthToken(@CookieValue("refreshToken") String refreshToken){
        System.out.println("Refresh token worked");
        return authService.refreshAuthToken(refreshToken);
    }
}
