package com.example.security_jwt.repositories;

import com.example.security_jwt.entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional <Equipment> findEquipmentByEquipmentName(String equipmentName);
    List<Equipment> findEquipmentByCompanyId(Long companyId);
}
