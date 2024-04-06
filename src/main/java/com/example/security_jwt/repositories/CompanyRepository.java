package com.example.security_jwt.repositories;

import com.example.security_jwt.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findCompanyByCompanyName (String companyName);
}
