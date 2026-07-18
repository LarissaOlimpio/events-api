package com.manager.events_api.domain.coupon;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CouponResponseDTO(
        UUID id,
        String code,
        Integer discount,
        OffsetDateTime validFrom,
        OffsetDateTime validUntil,
        UUID eventId
) {
}
