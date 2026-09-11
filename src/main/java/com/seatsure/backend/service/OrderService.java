package com.seatsure.backend.service;

import com.seatsure.backend.dto.OrderRequestDTO;
import com.seatsure.backend.dto.OrderResponseDTO;
import com.seatsure.backend.entity.Booking;
import com.seatsure.backend.entity.Order;
import com.seatsure.backend.entity.Screening;
import com.seatsure.backend.entity.Seat;
import com.seatsure.backend.entity.User;
import com.seatsure.backend.entity.enums.ReservationStatus;
import com.seatsure.backend.exception.ResourceNotFoundException;
import com.seatsure.backend.repository.OrderRepository;
import com.seatsure.backend.repository.ScreeningRepository;
import com.seatsure.backend.repository.SeatRepository;
import com.seatsure.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository; // Added Seat Repository!

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ScreeningRepository screeningRepository,
                        SeatRepository seatRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional // The ACID Vault!
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + request.userId()));

        Screening screening = screeningRepository.findById(request.screeningId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Screening not found: " + request.screeningId()));

        // BigDecimal math fix
        BigDecimal pricePerSeat = new BigDecimal("250.00");
        BigDecimal totalAmount = pricePerSeat.multiply(BigDecimal.valueOf(request.seatIds().size()));

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(ReservationStatus.PENDING);
        order.setCreatedAt(OffsetDateTime.now());
        order.setBookings(new ArrayList<>());

        for (UUID seatId : request.seatIds()) {

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Seat not found: " + seatId));

            Booking booking = new Booking();
            booking.setOrder(order);
            booking.setScreening(screening);
            booking.setSeat(seat); // Attaching the seat so it saves correctly
            booking.setStatus(ReservationStatus.PENDING);
            booking.setCreatedAt(OffsetDateTime.now());

            order.getBookings().add(booking);
        }

        // Saves Order AND all attached Bookings automatically
        Order savedOrder = orderRepository.save(order);

        List<UUID> savedBookingIds = savedOrder.getBookings().stream()
                .map(Booking::getId)
                .toList();

        return new OrderResponseDTO(
                savedOrder.getId(),
                screening.getMovie().getTitle(),
                screening.getStartTime(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedBookingIds
        );
    }
}