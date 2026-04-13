ALTER TABLE events
    ADD COLUMN address_id UUID;

ALTER TABLE events
    ADD CONSTRAINT fk_event_address
        FOREIGN KEY (address_id) REFERENCES address (id) ON DELETE CASCADE;