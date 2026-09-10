package com.seatsure.backend.dto;

import com.seatsure.backend.entity.enums.ReservationStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID orderId, // Changed to Order ID!
        String movieTitle,
        OffsetDateTime showTime,
        BigDecimal totalAmount,
        ReservationStatus status, // Upgraded to Enum!
        List<UUID> bookingIds // The missing piece: exact tickets!
) {}