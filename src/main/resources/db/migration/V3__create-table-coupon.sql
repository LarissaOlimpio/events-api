CREATE TABLE coupon
(
    id       UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    code     VARCHAR(100) NOT NULL,
    discount INTEGER      NOT NULL,
    valid    TIMESTAMP    NOT NULL,
    event_id UUID,
    CONSTRAINT fk_event_coupon
        FOREIGN KEY (event_id) REFERENCES events (id) on DELETE CASCADE
);