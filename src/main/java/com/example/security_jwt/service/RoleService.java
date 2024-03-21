package com.example.security_jwt.service;

import com.example.security_jwt.entities.Role;
import com.example.security_jwt.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER").get();
    }
}
