package com.seatsure.backend.service;

import com.seatsure.backend.dto.BookingRequestDTO;
import com.seatsure.backend.dto.BookingResponseDTO;
import com.seatsure.backend.entity.Booking;
import com.seatsure.backend.entity.Order;
import com.seatsure.backend.entity.Screening;
import com.seatsure.backend.entity.Seat;
import com.seatsure.backend.entity.User;
import com.seatsure.backend.entity.enums.ReservationStatus;
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
public class BookingService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository; // Added Seat Repository!

    public BookingService(OrderRepository orderRepository,
                          UserRepository userRepository,
                          ScreeningRepository screeningRepository,
                          SeatRepository seatRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional // The ACID Vault!
    public BookingResponseDTO createBooking(BookingRequestDTO request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Screening screening = screeningRepository.findById(request.screeningId())
                .orElseThrow(() -> new RuntimeException("Screening not found!"));

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

            // Fetching the actual Seat!
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found!"));

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

        return new BookingResponseDTO(
                savedOrder.getId(),
                screening.getMovie().getTitle(),
                screening.getStartTime(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                savedBookingIds
        );
    }
}