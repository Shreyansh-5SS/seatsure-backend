package com.seatsure.backend.dto;

public record UserRegistrationRequestDTO(
        String email,
        String rawPassword
) {}