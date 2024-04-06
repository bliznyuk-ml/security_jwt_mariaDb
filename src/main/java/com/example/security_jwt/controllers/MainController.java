package com.example.security_jwt.controllers;

import com.example.security_jwt.entities.Equipment;
import com.example.security_jwt.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final EquipmentService equipmentService;

    @GetMapping("/unsecured")
    public String unsecuredData(){
        return "Unsecured Data";
    }

    @GetMapping("/secured")
    public String securedData(){
        return "Secured data";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Admin data";
    }

    @GetMapping("/info")
    public String userData(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/test")
    public String userData() {
        return "TEST COMPLETED";
    }

    @GetMapping("/equipment")
    public List<Equipment> getUserEquipment(Principal principal) {
        String username = principal.getName();
        List<Equipment> userEquipment = equipmentService.getEquipmentForUser(username);
        userEquipment.forEach(System.out::println);
        return userEquipment;
    }
}
