package com.example.security_jwt.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "equipment")
public class Equipment extends CompanyOwned{

    @Column(name = "equipment_name")
    private String equipmentName;

    @Column(name = "serial_number")
    private String serialNumber;

}
