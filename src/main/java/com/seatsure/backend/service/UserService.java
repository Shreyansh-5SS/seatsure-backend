package com.seatsure.backend.service;

import com.seatsure.backend.dto.UserRegistrationRequestDTO;
import com.seatsure.backend.dto.UserResponseDTO;
import com.seatsure.backend.entity.User;
import com.seatsure.backend.exception.EmailAlreadyExistsException;
import com.seatsure.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //Dependency Injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO  registerUser(UserRegistrationRequestDTO requestDTO) {

        // PROACTIVE CHECK: Does it exist?
        if (userRepository.existsByEmail(requestDTO.email())) {
            // Throw the error! The PR Manager will catch it.
            throw new EmailAlreadyExistsException("A user with this email already exists!");
        }

        User userToSave = new User();
        // ... rest of your save logic ...
        userToSave.setEmail(requestDTO.email()); // Note: Using .email() because it's a record!
        userToSave.setRole("USER"); // HARDCODED! Hackers can never become an ADMIN now.

        // 2. Hash the raw password
        String hashedString = passwordEncoder.encode(requestDTO.rawPassword());
        userToSave.setPasswordHash(hashedString);

        // 3. Save to DB
        User savedUser = userRepository.save(userToSave);

        // 4. Return the safe Response DTO
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // Add this inside UserService!
    public List<UserResponseDTO> getAllUsers() {
        // We get the list of heavy entities from the DB
        return userRepository.findAll()
                .stream() // Turn the list into a conveyor belt
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getRole()
                )) // Convert each heavy entity into a safe DTO box
                .toList(); // Collect them back into a List
    }
}
