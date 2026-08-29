package com.seatsure.backend.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        String role
) {}