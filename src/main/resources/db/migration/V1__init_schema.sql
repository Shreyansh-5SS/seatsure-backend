-- V1__init_schema.sql: Production-Ready Movie Reservation Schema

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
                       is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Soft Delete Flag
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE theatres (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(255) NOT NULL,
                          city VARCHAR(100) NOT NULL,
                          is_active BOOLEAN NOT NULL DEFAULT TRUE -- Soft Delete Flag
);

CREATE TABLE screens (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         theatre_id UUID NOT NULL REFERENCES theatres(id) ON DELETE RESTRICT,
                         screen_number INT NOT NULL
);

CREATE TABLE seats (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       screen_id UUID NOT NULL REFERENCES screens(id) ON DELETE RESTRICT,
                       seat_number VARCHAR(20) NOT NULL
);

CREATE TABLE movies (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        title VARCHAR(255) NOT NULL,
                        duration_minutes INT NOT NULL,
                        genre VARCHAR(100) NOT NULL,
                        is_active BOOLEAN NOT NULL DEFAULT TRUE -- Soft Delete Flag
);

CREATE TABLE screenings (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            movie_id UUID NOT NULL REFERENCES movies(id) ON DELETE RESTRICT,
                            screen_id UUID NOT NULL REFERENCES screens(id) ON DELETE RESTRICT,
                            start_time TIMESTAMPTZ NOT NULL,
                            end_time TIMESTAMPTZ NOT NULL
);

CREATE TABLE bookings (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                          seat_id UUID NOT NULL REFERENCES seats(id) ON DELETE RESTRICT,
                          screening_id UUID NOT NULL REFERENCES screenings(id) ON DELETE RESTRICT,
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- THE MASTERPIECE: Partial Unique Index to prevent double-booking
-- while allowing cancelled bookings to not permanently lock the seat.
CREATE UNIQUE INDEX idx_unique_active_seat_booking
    ON bookings (seat_id, screening_id)
    WHERE status != 'CANCELLED';

CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          booking_id UUID UNIQUE NOT NULL REFERENCES bookings(id) ON DELETE RESTRICT,
                          amount NUMERIC(10, 2) NOT NULL,
                          status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED'))
);

CREATE TABLE reviews (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                         movie_id UUID NOT NULL REFERENCES movies(id) ON DELETE RESTRICT,
                         rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5)
);