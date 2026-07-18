ALTER TABLE coupon
    ADD COLUMN valid_from  TIMESTAMPTZ,
    ADD COLUMN valid_until TIMESTAMPTZ;

UPDATE coupon
SET valid_from  = valid,
    valid_until = valid;

ALTER TABLE coupon
    ALTER COLUMN valid_from SET NOT NULL,
    ALTER COLUMN valid_until SET NOT NULL;

ALTER TABLE coupon
    DROP COLUMN valid;