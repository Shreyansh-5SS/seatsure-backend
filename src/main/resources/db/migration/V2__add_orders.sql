-- 1. Create the new Orders table
CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
                        total_amount NUMERIC(10, 2) NOT NULL,
                        status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
                        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Link Bookings to Orders (and fix the normalization soft spot!)
ALTER TABLE bookings
DROP COLUMN user_id; -- Normalization: Bookings now inherit the user through the Order

ALTER TABLE bookings
    ADD COLUMN order_id UUID NOT NULL REFERENCES orders(id) ON DELETE RESTRICT;

-- 3. Shift Payment linking from Booking to Order
ALTER TABLE payments
DROP COLUMN booking_id;

ALTER TABLE payments
    ADD COLUMN order_id UUID UNIQUE NOT NULL REFERENCES orders(id) ON DELETE RESTRICT;