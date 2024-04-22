package com.example.security_jwt.service;

import com.example.security_jwt.dtos.RegistrationUserDto;
import com.example.security_jwt.dtos.UserDto;
import com.example.security_jwt.entities.Company;
import com.example.security_jwt.entities.User;
import com.example.security_jwt.exceptions.DuplicateEmailException;
import com.example.security_jwt.exceptions.UserNotFoundException;
import com.example.security_jwt.repositories.CompanyRepository;
import com.example.security_jwt.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    //сделать отдельный сервис для ролей
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
           String.format("User '%s' not found", username)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public UserDto findUserById(Long id){
       Optional <User> userOptional = userRepository.findById(id);
       UserDto userDto = new UserDto();
       if(userOptional.isPresent()){
           User user = userOptional.get();
           userDto.setId(user.getId());
           userDto.setUsername(user.getUsername());
           userDto.setEmail(user.getEmail());
           return userDto;
       } else {
           throw new UserNotFoundException("User with id: " + id + " not found");
       }
    }

    public User createNewUser(RegistrationUserDto registrationUserDto){
        //Добавить проверку на существование юзера и роли, которую добавляем ему
        User user = new User();
        Company company = companyRepository.findCompanyByCompanyName(registrationUserDto.getCompanyName());
        if(company != null){
            setUser(registrationUserDto, user, company);
        } else {
            company = new Company();
            company.setCompanyName(registrationUserDto.getCompanyName());
            company.setAddress(registrationUserDto.getAddress());
            companyRepository.save(company);
            company = companyRepository.findCompanyByCompanyName(registrationUserDto.getCompanyName());
            setUser(registrationUserDto, user, company);
        }
        return userRepository.save(user);
    }

    private void setUser(RegistrationUserDto registrationUserDto, User user, Company company) {
        String email = registrationUserDto.getEmail();
        boolean b = userRepository.existsByEmail(email);
        if (b) {
            throw new DuplicateEmailException("Email already exists: " + email);
        }
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));
        user.setCompany(company);
    }
}
