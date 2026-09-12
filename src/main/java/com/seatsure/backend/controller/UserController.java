package com.seatsure.backend.controller;

import com.seatsure.backend.dto.UserRegistrationRequestDTO;
import com.seatsure.backend.dto.UserResponseDTO;
import com.seatsure.backend.entity.User;
import com.seatsure.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 1. The Waiter only knows about the Manager now! No more UserRepository.
    private final UserService userService;

    // 2. Dependency Injection: Spring gives us the Manager.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        // 3. Ask the Manager for the safe list of DTOs
        return userService.getAllUsers();
    }

    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody UserRegistrationRequestDTO requestDTO) {
        return userService.registerUser(requestDTO);
    }
}