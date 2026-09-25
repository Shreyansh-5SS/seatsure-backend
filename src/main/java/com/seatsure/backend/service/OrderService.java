package com.seatsure.backend.service;

import com.seatsure.backend.dto.OrderRequestDTO;
import com.seatsure.backend.dto.OrderResponseDTO;
import com.seatsure.backend.entity.Booking;
import com.seatsure.backend.entity.Order;
import com.seatsure.backend.entity.Screening;
import com.seatsure.backend.entity.Seat;
import com.seatsure.backend.entity.User;
import com.seatsure.backend.entity.enums.ReservationStatus;
import com.seatsure.backend.exception.InvalidRequestException;
import com.seatsure.backend.exception.ResourceNotFoundException;
import com.seatsure.backend.repository.OrderRepository;
import com.seatsure.backend.repository.ScreeningRepository;
import com.seatsure.backend.repository.SeatRepository;
import com.seatsure.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

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

        Set<UUID> uniqueSeatIds = new LinkedHashSet<>(request.seatIds());
        if (uniqueSeatIds.size() != request.seatIds().size()) {
            throw new InvalidRequestException("Duplicate seat IDs in request");
        }

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

        List<UUID> seatIds = request.seatIds();

        List<Seat> seats = seatRepository.findAllByIdInAndScreenId(
                seatIds, screening.getScreen().getId());

        if (seats.size() != seatIds.size()) {
            throw new InvalidRequestException(
                    "One or more seats do not exist or do not belong to this screening's screen");
        }
        for (Seat seat : seats) {
            Booking booking = new Booking();
            booking.setOrder(order);
            booking.setScreening(screening);
            booking.setSeat(seat);
            booking.setStatus(ReservationStatus.PENDING);
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