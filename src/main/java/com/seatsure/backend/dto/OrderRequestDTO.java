package com.seatsure.backend.dto;

import java.util.List;
import java.util.UUID;

public record OrderRequestDTO(
        UUID userId,
        UUID screeningId,
        List<UUID> seatIds
) {}