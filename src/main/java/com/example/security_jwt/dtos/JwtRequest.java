package com.example.security_jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequest {
    private String username;
    private String password;
}
