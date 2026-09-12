package com.seatsure.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(

        @NotNull(message = "userId is required")
        UUID userId,

        @NotNull(message = "screeningId is required")
        UUID screeningId,

        @NotEmpty(message = "At least one seat must be selected")
        @Size(max = 10, message = "Cannot book more than 10 seats in one order")
        List<@NotNull(message = "seatIds cannot contain null values")UUID> seatIds
) {}