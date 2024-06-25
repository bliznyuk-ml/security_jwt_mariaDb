package com.example.security_jwt.controllers;

import com.example.security_jwt.dtos.UserDto;
import com.example.security_jwt.entities.Equipment;
import com.example.security_jwt.exceptions.UserNotFoundException;
import com.example.security_jwt.service.EquipmentService;
import com.example.security_jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import static com.example.security_jwt.qrCode.QRCodeGenerator.generateQRCode;

@RestController
@RequiredArgsConstructor

public class MainController {
    private final EquipmentService equipmentService;
    private final UserService userService;

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured Data";
    }

    @GetMapping("/secured")
    public String securedData() {
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

    @PostMapping("/setqrcode/{text}")
    public String setQRCode(@PathVariable String text) {
        String qrCodeText = text;
        String filePath = "src/main/resources/qrcodes/" + text + ".png";

        try {
            generateQRCode(qrCodeText, filePath);
            System.out.println("QR-код успешно создан по пути: " + filePath);
            return "QR-код успешно создан";
        } catch (Exception e) {
            System.out.println("Ошибка при создании QR-кода: " + e.getMessage());
            return "Ошибка при создании QR-кода: ";
        }
    }

    @GetMapping("/getqrcode/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<ByteArrayResource> getQRCodeImage(@PathVariable String id) {
        String fileName = id + ".png";

        try {
            Path qrCodePath = Paths.get("src/main/resources/qrcodes/", fileName);
            byte[] qrCodeBytes = Files.readAllBytes(qrCodePath);
            ByteArrayResource resource = new ByteArrayResource(qrCodeBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .contentLength(qrCodeBytes.length)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("getemployee/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<UserDto> showUser(@PathVariable Long id) {
        try {
            UserDto userDto = userService.findUserById(id);
            return ResponseEntity.ok(userDto);
        } catch (UserNotFoundException e) {
            // Обработка исключения UserNotFoundException
            // Возвращаем HTTP статус 404 (Not Found) с сообщением об ошибке
            return ResponseEntity.notFound().build();
        }
    }
}
