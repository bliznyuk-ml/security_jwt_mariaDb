package com.example.security_jwt.service;

import com.example.security_jwt.entities.Company;
import com.example.security_jwt.entities.Equipment;
import com.example.security_jwt.entities.User;
import com.example.security_jwt.repositories.EquipmentRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final UserService userService;


    public List <Equipment> getEquipmentForUser(String username){
        User user = userService.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        Company company = user.getCompany();
        if(company.getCompanyName().equals("admin")){
          return   equipmentRepository.findAll();
        } else {
            Long companyId = company.getId();
            return equipmentRepository.findEquipmentByCompanyId(companyId);
        }
    };
}
